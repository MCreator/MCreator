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

import net.mcreator.io.zip.ZipIO;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LibraryInfoIterator {

	public static void iterateLibraryInfo(LibraryInfo libraryInfo, Consumer<Entry> consumer, boolean sortByName)
			throws IOException {
		if (libraryInfo instanceof ModulesFileLibraryInfo moduleLibraryInfo) {
			FileSystem fs = moduleLibraryInfo.getJrtFileSystem();
			Path modulesRoot = fs.getPath("/modules/java.base");

			try (Stream<Path> walk = Files.walk(modulesRoot)) {
				List<Path> paths = walk.filter(Files::isRegularFile).toList();

				if (sortByName) {
					paths = new java.util.ArrayList<>(paths);
					paths.sort(Comparator.comparing(Path::toString));
				}

				for (Path path : paths) {
					// JRT paths: /modules/<module_name>/<package>/<Class>.class
					// We need to skip the first two elements ('modules' and the module name)
					if (path.getNameCount() > 2) {
						String relativePath = path.subpath(2, path.getNameCount()).toString().replace('\\', '/');
						consumer.accept(new Entry(relativePath, () -> Files.newInputStream(path)));
					}
				}
			}
			return;
		}

		// If no other handling possible, try to load library as a ZIP file
		File libraryFile = new File(libraryInfo.getLocationAsString());
		if (libraryFile.isFile() && (ZipIO.checkIfZip(libraryFile) || ZipIO.checkIfJMod(libraryFile))) {
			try (ZipFile zipFile = ZipIO.openZipFile(libraryFile)) {
				List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
				if (sortByName)
					entries.sort(Comparator.comparing(ZipEntry::getName));

				entries.forEach(e -> consumer.accept(new Entry(e.getName(), () -> zipFile.getInputStream(e))));
			}
			return;
		}

		// Failed to handle library info
		throw new IOException("Unsupported library format: " + libraryInfo.getClass().getName() + ", path: "
				+ libraryInfo.getLocationAsString());
	}

	public record Entry(String path, StreamSupplier streamSupplier) {}

	interface StreamSupplier {
		InputStream getStream() throws IOException;
	}

}
