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

import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A {@link JarLibraryInfo} that excludes class files the RSTA class reader cannot parse from the
 * package map. The reader predates JPMS, so module descriptors (constant pool tags 19/20) make class
 * file parsing fail with an IOException that aborts the completion lookup in progress. Module
 * descriptors ({@code module-info.class}) and multi-release {@code META-INF/versions} entries are
 * useless for code completion anyway, so they are not indexed at all.
 */
public class SafeJarLibraryInfo extends JarLibraryInfo {

	public SafeJarLibraryInfo(String jarFile) {
		super(jarFile);
	}

	public SafeJarLibraryInfo(File jarFile) {
		super(jarFile);
	}

	static boolean isIndexableClassEntry(String entryName) {
		return !entryName.startsWith("META-INF/") && !entryName.endsWith("module-info.class");
	}

	@Override public PackageMapNode createPackageMap() throws IOException {
		PackageMapNode packageMap = new PackageMapNode();
		try (JarFile jar = new JarFile(getJarFile())) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String entryName = entries.nextElement().getName();
				if (entryName.endsWith(".class") && isIndexableClassEntry(entryName))
					packageMap.add(entryName);
			}
		}
		return packageMap;
	}

}
