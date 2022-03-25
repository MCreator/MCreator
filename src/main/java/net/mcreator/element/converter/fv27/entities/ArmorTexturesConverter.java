/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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


package net.mcreator.element.converter.fv27.entities;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Armor;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;

public class ArmorTexturesConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Armor armor = (Armor) input;

		if (armor.helmetModelTexture != null && !armor.helmetModelTexture.isEmpty()) {
			FileIO.copyFile(workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.helmetModelTexture), TextureType.OTHER),
					workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.helmetModelTexture),
									TextureType.ENTITY));
		}

		if (armor.bodyModelTexture != null && !armor.bodyModelTexture.isEmpty()) {
			FileIO.copyFile(workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.bodyModelTexture), TextureType.OTHER),
					workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.bodyModelTexture),
									TextureType.ENTITY));
		}

		if (armor.leggingsModelTexture != null && !armor.leggingsModelTexture.isEmpty()) {
			FileIO.copyFile(workspace.getFolderManager()
					.getTextureFile(FilenameUtilsPatched.removeExtension(armor.leggingsModelTexture),
							TextureType.OTHER), workspace.getFolderManager()
					.getTextureFile(FilenameUtilsPatched.removeExtension(armor.leggingsModelTexture),
							TextureType.ENTITY));
		}

		if (armor.bootsModelTexture != null && !armor.bootsModelTexture.isEmpty()) {
			FileIO.copyFile(workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.bootsModelTexture), TextureType.OTHER),
					workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(armor.bootsModelTexture),
									TextureType.ENTITY));
		}

		return armor;
	}

	@Override public int getVersionConvertingTo() {
		return 27;
	}
}
