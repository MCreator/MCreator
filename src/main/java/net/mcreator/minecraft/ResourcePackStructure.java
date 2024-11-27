/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.minecraft;

import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ResourcePackStructure {

	private static final List<String> extensions = List.of("json", "mcmeta", "png", "ogg", "fsh", "vsh");

	private static final String RESOURCES_FOLDER = "assets/minecraft";

	public static File getResourcePackRoot(Workspace workspace) {
		return new File(workspace.getGenerator().getResourceRoot(), RESOURCES_FOLDER);
	}

	@Nullable public static File getResourcePackArchive(Workspace workspace) {
		String vanillaResourcesJar = workspace.getGeneratorConfiguration().getSpecificRoot("vanilla_resources_jar");
		if (vanillaResourcesJar != null) {
			List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager() != null ?
					workspace.getGenerator().getProjectJarManager().getClassFileSources() :
					List.of();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile() && Pattern.compile(vanillaResourcesJar).matcher(libraryFile.getName())
						.find()) {
					return libraryFile;
				}
			}
		}
		return null;
	}

	public static List<Entry> getResourcePackStructure(Workspace workspace, @Nullable File resourcePackArchive) {
		List<Entry> entries = new ArrayList<>();

		if (resourcePackArchive != null) {
			ZipIO.iterateZip(resourcePackArchive, entry -> {
				if (!entry.isDirectory()) {
					String path = entry.getName();
					if (path.startsWith(RESOURCES_FOLDER) && extensions.contains(FilenameUtils.getExtension(path))) {
						path = path.substring(RESOURCES_FOLDER.length());
						File override = new File(getResourcePackRoot(workspace), path);
						entries.add(new Entry(path, override,
								override.isFile() ? EntryType.VANILLA_OVERRIDE : EntryType.VANILLA));
					}
				}
				// Get input stream of the entry
			}, true);
		}

		// Load custom resources
		File customResources = getResourcePackRoot(workspace);
		try (Stream<Path> paths = Files.walk(customResources.toPath())) {
			paths.forEach(path -> {
				File file = path.toFile();
				if (file.isFile() && extensions.contains(FilenameUtils.getExtension(file.getName()))) {
					String relativePath = customResources.toPath().relativize(path).toString();
					entries.add(new Entry(relativePath, file, EntryType.CUSTOM));
				}
			});
		} catch (IOException ignored) {
		}

		return entries;
	}

	public record Entry(String path, File override, EntryType type) {

		public String fullPath() {
			return RESOURCES_FOLDER + path;
		}

	}

	public enum EntryType {
		VANILLA, VANILLA_OVERRIDE, CUSTOM
	}

}
