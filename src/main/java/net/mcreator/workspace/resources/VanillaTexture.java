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

package net.mcreator.workspace.resources;

import net.mcreator.io.zip.ZipIO;
import net.mcreator.plugin.modapis.ModAPIImplementation;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A texture loaded from vanilla MC asset archives or from mods downloaded by API plugins.
 */
public final class VanillaTexture extends Texture {

	private static final Logger LOG = LogManager.getLogger(VanillaTexture.class);

	private static final Map<CacheIdentifier, Map<String, Texture>> CACHE = new HashMap<>();

	private final ImageIcon icon;

	private VanillaTexture(TextureType textureType, String textureName, ImageIcon icon) {
		super(textureType, textureName);
		this.icon = icon;
	}

	@Override public ImageIcon getTextureIcon(Workspace workspace) {
		return icon;
	}

	/**
	 * Searches in the provided workspace for an externally loaded texture with matching name and type.
	 *
	 * @param workspace   The workspace to search in.
	 * @param textureType The type of the textures to check.
	 * @param textureName The name of the texture to look for.
	 * @return The vanilla/external texture with the provided name of the specified type.
	 */
	public static VanillaTexture getTexture(Workspace workspace, TextureType textureType, String textureName) {
		CacheIdentifier cacheIdentifier = new CacheIdentifier(workspace, textureType);

		// Ensure cache is populated and valid
		getTexturesOfType(workspace, textureType);

		return (VanillaTexture) CACHE.get(cacheIdentifier).getOrDefault(textureName,
				new VanillaTexture(textureType, textureName, new EmptyIcon.ImageIcon(16, 16)));
	}

	/**
	 * Scans the workspace and collects all the available externally loaded textures of a certain type.
	 *
	 * @param workspace The workspace to collect icons for.
	 * @param type      The type of the textures to collect.
	 * @return The list of textures from vanilla MC and API mods available in the provided workspace.
	 */
	public static List<Texture> getTexturesOfType(Workspace workspace, TextureType type) {
		CacheIdentifier cacheId = new CacheIdentifier(workspace, type);

		Map<String, Texture> textures = CACHE.getOrDefault(cacheId, new LinkedHashMap<>());
		if (textures.isEmpty()) { // if not cached or empty list is cached, attempt to rebuild cache
			Map<String, Texture> textures = new LinkedHashMap<>();

			List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager() != null ?
					workspace.getGenerator().getProjectJarManager().getClassFileSources() :
					List.of();

			String root = workspace.getGeneratorConfiguration()
					.getSpecificRoot("vanilla_" + type.getID() + "_textures_dir");
			if (root != null) {
				String[] data = root.split("!/"); // 0 = jar name, 1 = path
				final String jarName = data[0];
				final String path = data[1];

				for (LibraryInfo libraryInfo : libraryInfos) {
					File libraryFile = new File(libraryInfo.getLocationAsString());
					if (libraryFile.isFile() && libraryFile.getName().contains(jarName)) {
						loadTexturesFrom(libraryFile, "minecraft", path, type, textures);
						break;
					}
				}
			}

			for (String dep : workspace.getWorkspaceSettings().getMCreatorDependencies()) {
				ModAPIImplementation apiImpl = ModAPIManager.getModAPIForNameAndGenerator(dep,
						workspace.getGenerator().getGeneratorName());
				if (apiImpl != null && apiImpl.resource_paths() != null) {
					String resPath = apiImpl.resource_paths().get(type.getID() + "_textures_dir");
					if (resPath != null) {
						String[] data = resPath.split("!/"); // 0 = jar name, 1 = path
						final String jarName = data[0];
						final String path = data[1];

						File apiLibFile = new File(workspace.getWorkspaceFolder(), jarName);
						if (apiLibFile.isFile()) {
							loadTexturesFrom(apiLibFile, apiImpl.parent().id(), path, type, textures);
							break;
						}

						for (LibraryInfo libraryInfo : libraryInfos) {
							File libraryFile = new File(libraryInfo.getLocationAsString());
							if (libraryFile.isFile() && libraryFile.getName().contains(jarName)) {
								loadTexturesFrom(libraryFile, apiImpl.parent().id(), path, type, textures);
								break;
							}
						}
					}
				}
			}

			CACHE.put(cacheId, textures);
		}

		return textures.values().stream().toList();
	}

	private static void loadTexturesFrom(File libFile, String namespace, String path, TextureType type,
			Map<String, Texture> textures) {
		try (ZipFile zipFile = ZipIO.openZipFile(libFile)) {
			List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
			entries.parallelStream().sorted(Comparator.comparing(ZipEntry::getName)).forEachOrdered(entry -> {
				if (entry.getName().startsWith(path) && entry.getName().endsWith(".png")) {
					String textureName = namespace + ":" + FilenameUtils.getBaseName(entry.getName());
					try {
						textures.put(textureName, new VanillaTexture(type, textureName,
								new ImageIcon(ImageIO.read(zipFile.getInputStream(entry)))));
					} catch (IOException ignored) {
					}
				}
			});
		} catch (IOException e) {
			LOG.warn("Failed to read library file: {}", libFile, e);
		}
	}

	public static void invalidateCache(Workspace workspace) {
		CACHE.keySet().stream().filter(e -> e.workspace.equals(workspace)).toList().forEach(CACHE::remove);
	}

	private record CacheIdentifier(Workspace workspace, TextureType textureType) {}

}
