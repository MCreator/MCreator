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

import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A texture created or imported as a resource of a workspace.
 */
public final class CustomTexture extends Texture {

	private final File textureFile;

	@Nullable private ImageIcon cachedIcon = null;

	CustomTexture(TextureType textureType, File texture) {
		super(textureType, textureType == TextureType.ARMOR ?
				(FilenameUtils.removeExtension(texture.getName()).replace("_layer_1", "").replace("_layer_2", "")) :
				FilenameUtils.removeExtension(texture.getName()));

		this.textureFile = texture;
	}

	public File getTextureFile() {
		return textureFile;
	}

	@Override public ImageIcon getTextureIcon(Workspace workspace) {
		if (cachedIcon != null)
			return cachedIcon;

		if (textureType == TextureType.ARMOR) {
			File[] armorTextures = workspace.getFolderManager().getArmorTextureFilesForName(textureName);
			return cachedIcon = new ImageIcon(armorTextures[0].getAbsolutePath());
		} else {
			return cachedIcon = new ImageIcon(
					workspace.getFolderManager().getTextureFile(textureName, textureType).getAbsolutePath());
		}
	}

	/**
	 * Scans the workspace and collects all the available custom textures of a certain type.
	 *
	 * @param workspace The workspace to collect icons for.
	 * @param type      The type of the textures to collect.
	 * @return The list of custom textures available in the provided workspace.
	 */
	public static List<Texture> getTexturesOfType(Workspace workspace, TextureType type) {
		List<File> customTextureFiles;
		if (type == TextureType.ARMOR) {
			customTextureFiles = new ArrayList<>();
			List<File> armors = workspace.getFolderManager().getTexturesList(TextureType.ARMOR);
			for (File texture : armors)
				if (texture.getName().endsWith("_layer_1.png") && !texture.getName().equals("_layer_1.png"))
					customTextureFiles.add(texture);
		} else {
			customTextureFiles = workspace.getFolderManager().getTexturesList(type);
		}

		return customTextureFiles.parallelStream().map(e -> (Texture) new CustomTexture(type, e)).toList();
	}

}
