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

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.zip.ZipIO;
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

	public static VanillaTexture getTexture(Workspace workspace, TextureType textureType, String textureName) {
		CacheIdentifier cacheIdentifier = new CacheIdentifier(workspace.getGeneratorConfiguration(), textureType);

		if (!CACHE.containsKey(cacheIdentifier))
			getTexturesOfType(workspace, textureType); // Load CACHE if not already loaded

		return (VanillaTexture) CACHE.get(cacheIdentifier).getOrDefault(textureName,
				new VanillaTexture(textureType, textureName, new EmptyIcon.ImageIcon(16, 16)));
	}

	public static List<Texture> getTexturesOfType(Workspace workspace, TextureType type) {
		String root = workspace.getGeneratorConfiguration()
				.getSpecificRoot("vanilla_" + type.getID() + "_textures_dir");
		if (root == null)
			return Collections.emptyList();

		String[] data = root.split("!/"); // 0 = jar name, 1 = path
		final String jarName = data[0];
		final String path = data[1];

		return CACHE.computeIfAbsent(new CacheIdentifier(workspace.getGeneratorConfiguration(), type), key -> {
			Map<String, Texture> textures = new LinkedHashMap<>();
			if (workspace.getGenerator().getProjectJarManager() != null) {
				List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager().getClassFileSources();
				for (LibraryInfo libraryInfo : libraryInfos) {
					File libraryFile = new File(libraryInfo.getLocationAsString());
					if (libraryFile.isFile() && (ZipIO.checkIfZip(libraryFile) || ZipIO.checkIfJMod(libraryFile))) {
						if (libraryFile.getName().contains(jarName)) {
							try (ZipFile zipFile = ZipIO.openZipFile(libraryFile)) {
								List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
								entries.stream().sorted(Comparator.comparing(ZipEntry::getName)).forEach(entry -> {
									if (entry.getName().startsWith(path) && entry.getName().endsWith(".png")) {
										String textureName = "minecraft:" + FilenameUtils.getName(entry.getName());
										try {
											textures.put(textureName, new VanillaTexture(type, textureName,
													new ImageIcon(ImageIO.read(zipFile.getInputStream(entry)))));
										} catch (IOException ignored) {
										}
									}
								});
							} catch (IOException e) {
								LOG.warn("Failed to read library file: {}", libraryFile, e);
							}
							break;
						}
					}
				}
			}
			return textures;
		}).values().stream().toList();
	}

	private record CacheIdentifier(GeneratorConfiguration generatorConfiguration, TextureType textureType) {}

}
