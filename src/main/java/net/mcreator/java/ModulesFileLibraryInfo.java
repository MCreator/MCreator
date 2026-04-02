/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.classreader.ClassFile;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ModulesFileLibraryInfo extends LibraryInfo {

	private static final Logger LOG = LogManager.getLogger(ModulesFileLibraryInfo.class);

	private final File jdkHome;
	private FileSystem jrtFileSystem;

	// Cache to quickly map "java/lang/String.class" to "/modules/java.base/java/lang/String.class"
	private final Map<String, Path> classPathCache = new HashMap<>();

	public ModulesFileLibraryInfo(File jdkHome) {
		if (jdkHome == null || !jdkHome.exists()) {
			throw new IllegalArgumentException("JDK Home does not exist");
		}
		this.jdkHome = jdkHome;
	}

	public FileSystem getJrtFileSystem() throws IOException {
		if (jrtFileSystem == null || !jrtFileSystem.isOpen()) {
			try {
				ClassLoader classLoader = new URLClassLoader(
						new URL[] { new File(jdkHome, "lib/jrt-fs.jar").toURI().toURL() },
						ClassLoader.getPlatformClassLoader());

				// Load the JRT FileSystem using the external provider
				jrtFileSystem = FileSystems.newFileSystem(URI.create("jrt:/"),
						Map.of("java.home", jdkHome.getAbsolutePath()), classLoader);
			} catch (Exception e) {
				throw new IOException("Failed to initialize JRT FileSystem", e);
			}
		}
		return jrtFileSystem;
	}

	@Override public PackageMapNode createPackageMap() throws IOException {
		PackageMapNode root = new PackageMapNode();
		FileSystem fs = getJrtFileSystem();
		Path baseModule = fs.getPath("/modules/java.base");

		try (Stream<Path> walk = Files.walk(baseModule)) {
			walk.filter(path -> path.toString().endsWith(".class")).forEach(path -> {
				// jimage paths look like: /modules/java.base/java/lang/String.class
				// We need to strip the first two elements (/modules/module.name/)
				if (path.getNameCount() > 2) {
					String relativePath = path.subpath(2, path.getNameCount()).toString();

					// Populate the RSTA tree
					root.add(relativePath);

					// Populate our lookup cache for createClassFile
					classPathCache.put(relativePath.replace('\\', '/'), path);
				}
			});
		}
		return root;
	}

	@Override public ClassFile createClassFile(String entryName) throws IOException {
		// Normalize entryName
		String lookupName = entryName.replace('\\', '/');

		// Use cached path if available, otherwise fallback to a search
		Path classPath = classPathCache.get(lookupName);

		if (classPath == null) {
			// Fallback for classes not found in the initial walk
			FileSystem fs = getJrtFileSystem();
			try (Stream<Path> s = Files.find(fs.getPath("/modules"), 10, (p, a) -> p.toString().endsWith(lookupName))) {
				classPath = s.findFirst().orElse(null);
			}
		}

		if (classPath != null) {
			try (InputStream in = Files.newInputStream(classPath);
					DataInputStream din = new DataInputStream(new BufferedInputStream(in))) {
				return new ClassFile(din);
			}
		}
		return null;
	}

	@Override public ClassFile createClassFileBulk(String entryName) throws IOException {
		return createClassFile(entryName);
	}

	@Override public void bulkClassFileCreationStart() {
		try {
			getJrtFileSystem();
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	@Override public void bulkClassFileCreationEnd() {
	}

	@Override public String getLocationAsString() {
		return jdkHome.getAbsolutePath();
	}

	@Override public long getLastModified() {
		return new File(jdkHome, "lib/modules").lastModified();
	}

	@Override public int compareTo(@Nonnull LibraryInfo info) {
		return (info instanceof ModulesFileLibraryInfo) ?
				jdkHome.compareTo(((ModulesFileLibraryInfo) info).jdkHome) :
				-1;
	}

	@Override public int hashCode() {
		return jdkHome.hashCode();
	}

	@Override public int hashCodeImpl() {
		return jdkHome.hashCode();
	}

}