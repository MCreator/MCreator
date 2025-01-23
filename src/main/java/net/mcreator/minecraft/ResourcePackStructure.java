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
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ResourcePackStructure {

	private static final List<String> extensions = List.of("json", "mcmeta", "png", "ogg", "fsh", "vsh", "txt");

	private static final Map<File, Collection<String>> ZIP_STRUCTURE_CACHE = new HashMap<>();

	private static String getResourcesFolder(String namespace) {
		return "assets/" + namespace;
	}

	public static File getResourcePackRoot(Workspace workspace, String namespace) {
		return new File(workspace.getGenerator().getResourceRoot(), getResourcesFolder(namespace));
	}

	@Nullable public static File getResourcePackArchive(Workspace workspace, String namespace) {
		if ("minecraft".equals(namespace)) {
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
		}
		return null;
	}

	public static Collection<Entry> getResourcePackStructure(Workspace workspace, String namespace, @Nullable File resourcePackArchive) {
		Set<Entry> entries = new TreeSet<>();

		if (resourcePackArchive != null) {
			Collection<String> zipEntries = ZIP_STRUCTURE_CACHE.computeIfAbsent(resourcePackArchive, file -> {
				List<String> zipEntriesComputed = new ArrayList<>();
				ZipIO.iterateZip(resourcePackArchive, entry -> {
					if (!entry.isDirectory()) {
						String path = entry.getName();
						if (path.startsWith(getResourcesFolder(namespace)) && extensions.contains(FilenameUtils.getExtension(path))) {
							path = path.substring(getResourcesFolder(namespace).length());
							zipEntriesComputed.add(path);
						}
					}
				}, true);
				return zipEntriesComputed;
			});

			for (String path : zipEntries) {
				File override = new File(getResourcePackRoot(workspace, namespace), path);
				entries.add(
						new Entry(namespace, path, override, override.isFile() ? EntryType.VANILLA_OVERRIDE : EntryType.VANILLA));
			}
		}

		// Load custom resources
		File customResources = getResourcePackRoot(workspace, namespace);
		try (Stream<Path> paths = Files.walk(customResources.toPath())) {
			paths.forEach(path -> {
				File file = path.toFile();
				String relativePath = "/" + customResources.toPath().relativize(path).toString().replace("\\", "/");
				Entry toAdd = new Entry(namespace, relativePath, file, EntryType.CUSTOM);
				if (file.isDirectory()) {
					if (file.isDirectory() && !relativePath.endsWith("/"))
						relativePath += "/";

					boolean folderExists = false;
					for (Entry existing : entries) {
						if (existing.path().startsWith(relativePath)) {
							folderExists = true;
							break;
						}
					}

					if (!folderExists) { // If folder is already defined, do not re-add it
						File[] children = file.listFiles();
						// Only explicitly add empty folders because non-empty folders will be added by their children
						if (children == null || children.length == 0)
							entries.add(toAdd);
					}
				} else {
					entries.add(toAdd);
				}
			});
		} catch (IOException ignored) {
		}

		return entries;
	}

	public record Entry(String namespace, String path, File override, EntryType type) implements Comparable<Entry> {

		public String fullPath() {
			return getResourcesFolder(namespace) + path;
		}

		public Entry parent() {
			return new Entry(namespace, FilenameUtils.getFullPath(path), override.getParentFile(), type);
		}

		public String extension() {
			return FilenameUtils.getExtension(path).toLowerCase(Locale.ROOT);
		}

		public boolean isFolder() {
			return extension().isBlank();
		}

		@Override public int compareTo(Entry o) {
			boolean isThisFolder = this.isFolder();
			boolean isOtherFolder = o.isFolder();
			if (isThisFolder && !isOtherFolder) {
				return -1;
			} else if (!isThisFolder && isOtherFolder) {
				return 1;
			} else {
				return path.compareTo(o.path);
			}
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof Entry entry) {
				return path.equals(entry.path);
			}
			return false;
		}

		@Override public int hashCode() {
			return path.hashCode();
		}

		@Override public String toString() {
			return path + " [" + type + "]";
		}
	}

	public enum EntryType {
		VANILLA, VANILLA_OVERRIDE, CUSTOM
	}

}
