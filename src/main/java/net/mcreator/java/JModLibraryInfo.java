/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import org.apache.commons.lang3.StringUtils;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.classreader.ClassFile;
import javax.annotation.Nonnull;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JModLibraryInfo extends LibraryInfo {

	private final File jmodFile;
	private ZipFile bulkCreateZip;

	public JModLibraryInfo(File jmodFile) {
		if (jmodFile == null || !jmodFile.exists()) {
			String name = jmodFile == null ? "null" : jmodFile.getAbsolutePath();
			throw new IllegalArgumentException("JMOD file does not exist: " + name);
		}
		this.jmodFile = jmodFile;
	}

	@Override public void bulkClassFileCreationEnd() {
		try {
			bulkCreateZip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override public void bulkClassFileCreationStart() {
		try {
			bulkCreateZip = new ZipFile(jmodFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override public int compareTo(@Nonnull LibraryInfo info) {
		if (info == this) {
			return 0;
		}
		int result = -1;
		if (info instanceof JModLibraryInfo)
			result = jmodFile.compareTo(((JModLibraryInfo) info).jmodFile);
		return result;
	}

	@Override public ClassFile createClassFile(String entryName) throws IOException {
		try (JarFile jar = new JarFile(jmodFile)) {
			return createClassFileImpl(jar, entryName);
		}
	}

	@Override public ClassFile createClassFileBulk(String entryName) throws IOException {
		return createClassFileImpl(bulkCreateZip, entryName);
	}

	private static ClassFile createClassFileImpl(ZipFile jar, String entryName) throws IOException {
		JarEntry entry = (JarEntry) jar.getEntry("classes/" + entryName);
		if (entry == null) {
			System.err.println("ERROR: Invalid entry: " + entryName);
			return null;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(jar.getInputStream(entry)));
		ClassFile cf;
		try {
			cf = new ClassFile(in);
		} finally {
			in.close();
		}
		return cf;
	}

	@Override public PackageMapNode createPackageMap() throws IOException {
		PackageMapNode root = new PackageMapNode();

		try (JarFile jar = new JarFile(jmodFile)) {
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".class") && entryName.startsWith("classes/"))
					root.add(StringUtils.stripStart(entryName, "classes/"));
			}
		}
		return root;
	}

	@Override public long getLastModified() {
		return jmodFile.lastModified();
	}

	@Override public String getLocationAsString() {
		return jmodFile.getAbsolutePath();
	}

	@Override public int hashCode() {
		return jmodFile.hashCode();
	}

}
