/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2022_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.types.GUI;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;

public class GUITexturesConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		GUI gui = (GUI) input;

		if (workspace.getFolderManager().getTextureFile(gui.getModElement().getRegistryName(), TextureType.OTHER)
				.isFile()) {
			FileIO.copyFile(workspace.getFolderManager()
							.getTextureFile(gui.getModElement().getRegistryName(), TextureType.OTHER),
					workspace.getFolderManager()
							.getTextureFile(gui.getModElement().getRegistryName(), TextureType.SCREEN));

			// It is very unlikely GUI texture is used for something else than a screen
			workspace.getFolderManager().getTextureFile(gui.getModElement().getRegistryName(), TextureType.OTHER)
					.delete();
		}

		if (gui.components != null && !gui.components.isEmpty()) {
			for (GUIComponent component : gui.components) {
				if (component instanceof Image image) {
					FileIO.copyFile(workspace.getFolderManager()
									.getTextureFile(FilenameUtilsPatched.removeExtension(image.image), TextureType.OTHER),
							workspace.getFolderManager()
									.getTextureFile(FilenameUtilsPatched.removeExtension(image.image),
											TextureType.SCREEN));
				}
			}
		}

		return gui;
	}

	@Override public int getVersionConvertingTo() {
		return 31;
	}
}
