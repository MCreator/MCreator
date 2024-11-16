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

package net.mcreator.ui.minecraft.recourcepack;

import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ResourcePackStructure {

	private static final List<String> extensions = List.of("json", "mcmeta", "png", "ogg", "fsh", "vsh");

	public static List<Entry> getResourcePackStructure(Workspace workspace) {
		List<Entry> entries = new ArrayList<>();
		String vanillaResourcesJar = workspace.getGeneratorConfiguration().getSpecificRoot("vanilla_resources_jar");
		if (vanillaResourcesJar != null) {
			List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager() != null ?
					workspace.getGenerator().getProjectJarManager().getClassFileSources() :
					List.of();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile() && Pattern.compile(vanillaResourcesJar).matcher(libraryFile.getName())
						.find()) {
					ZipIO.iterateZip(libraryFile, entry -> {
						if (!entry.isDirectory()) {
							String path = entry.getName();
							if (path.startsWith("assets/minecraft/") && extensions.contains(
									FilenameUtils.getExtension(path))) {
								path = path.substring("assets/minecraft/".length());
								File override = new File(workspace.getGenerator().getResourceRoot(), path);
								entries.add(new Entry(path, override, override.isFile()));
							}
						}
						// Get input stream of the entry
					}, true);
					break;
				}
			}
		}
		return entries;
	}

	public record Entry(String path, File override, boolean overrideExists) {}

}
