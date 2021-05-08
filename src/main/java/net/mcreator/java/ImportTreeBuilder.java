/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.java;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.ConstPool;
import net.mcreator.generator.Generator;
import net.mcreator.io.zip.ZipIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ImportTreeBuilder {

	private static final Logger LOG = LogManager.getLogger("Import Tree Builder");

	public static Map<String, List<String>> generateImportTree(ProjectJarManager projectJarManager) {
		Map<String, List<String>> retval = new ConcurrentHashMap<>();
		List<LibraryInfo> libraryInfos = projectJarManager.getClassFileSources();
		libraryInfos.parallelStream().forEach(libraryInfo -> {
			File libraryFile = new File(libraryInfo.getLocationAsString());
			if (libraryFile.isFile() && (ZipIO.checkIfZip(libraryFile) || libraryFile.getName().endsWith(".jmod"))) {
				try (ZipFile zipFile = new ZipFile(libraryFile)) {
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						String entryName = entry.getName();

						if (libraryFile.getName().endsWith(".jmod")) {
							if (!entryName.startsWith("classes/"))
								continue;

							entryName = StringUtils.stripStart(entryName, "classes/");
						}

						// quickly skip all meta-info paths
						if (entryName.startsWith("META-INF/"))
							continue;

						// only load classes that are not inner
						if (!entryName.endsWith(".class") || entryName.contains("$"))
							continue;

						// skip Sun APIs
						if (entryName.startsWith("sun/") || entryName.startsWith("com/sun/"))
							continue;

						// skip package and modules info entries
						if (entryName.endsWith("package-info.class") || entryName.endsWith("module-info.class"))
							continue;

						// check if class is public or protected
						try {
							DataInputStream dis = new DataInputStream(zipFile.getInputStream(entry));
							int magic = dis.readInt(); // check magic number
							if (magic != 0xCAFEBABE)
								throw new Exception();
							dis.readUnsignedShort();// class minor
							dis.readUnsignedShort();// class major
							new ConstPool(dis);// read const pool
							int accessFlags = dis.readUnsignedShort(); //accessFlags
							if ((accessFlags & AccessFlag.PUBLIC) == 0 && (accessFlags & AccessFlag.PROTECTED) == 0)
								continue;
						} catch (Exception e) {
							LOG.debug("Failed to check access flags of " + entryName + " - assuming public");
						}

						String fqdn = entryName.replaceAll("[\\\\/]", ".");
						fqdn = fqdn.substring(0, fqdn.length() - 6);
						int lastIndxDot = fqdn.lastIndexOf('.');
						String className = fqdn;
						String packageName = "";
						if (lastIndxDot != -1) {
							packageName = fqdn.substring(0, lastIndxDot);
							className = fqdn.substring(lastIndxDot + 1);
						}

						addClassToTree(packageName, className, retval);
					}
				} catch (IOException e) {
					LOG.warn("Failed to load import format classes", e);
				}
			}
		});
		return retval;
	}

	static void reloadClassesFromMod(Generator generator, Map<String, List<String>> store) {
		reloadClassesFromModImpl(generator.getSourceRoot(), generator.getSourceRoot(), store);
	}

	private static void reloadClassesFromModImpl(File parent, File root, Map<String, List<String>> store) {
		String pathRelativeToRoot = parent.getAbsolutePath().replace(root.getAbsolutePath(), "");
		String packageName = pathRelativeToRoot.replaceAll("[\\\\/]", ".").replaceFirst("\\.", "");

		File[] files = parent.listFiles();
		for (File file : files != null ? files : new File[0]) {
			if (file.isDirectory()) {
				reloadClassesFromModImpl(file, root, store);
			} else {
				String className = FilenameUtils.removeExtension(file.getName());
				addClassToTree(packageName, className, store);
			}
		}
	}

	private static void addClassToTree(String packageName, String className, Map<String, List<String>> store) {
		if (store.get(packageName) == null) {
			store.put(packageName, new ArrayList<>(Collections.singletonList(className)));
		} else {
			store.get(packageName).add(className);
		}
	}

}
