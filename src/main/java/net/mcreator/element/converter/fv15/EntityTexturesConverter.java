/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.element.converter.fv15;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Mob;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class EntityTexturesConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Mob entity = (Mob) input;
		String texture = RegistryNameFixer.fix(FilenameUtils.removeExtension(jsonElementInput.getAsJsonObject().get("definition")
						.getAsJsonObject().get("mobModelTexture").getAsString()));
		File fromFileTexture = workspace.getFolderManager().getOtherTextureFile(texture);
		File toFileTexture = workspace.getFolderManager().getEntityTextureFile(texture);
		FileIO.copyFile(fromFileTexture, toFileTexture);
		entity.mobModelTexture = texture + ".png";
		fromFileTexture.delete();

		if(jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("mobModelGlowTexture") != null) {
			if(!jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("mobModelGlowTexture").getAsString().isEmpty()) {
				String glowTexture = RegistryNameFixer.fix(FilenameUtils.removeExtension(
						jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
								.get("mobModelGlowTexture").getAsString()));
				File fromFileGlowTexture = workspace.getFolderManager().getOtherTextureFile(glowTexture);
				File toFileGlowTexture = workspace.getFolderManager().getEntityTextureFile(glowTexture);
				FileIO.copyFile(fromFileGlowTexture, toFileGlowTexture);
				entity.mobModelGlowTexture = glowTexture + ".png";
				fromFileGlowTexture.delete();
			}
		}
		return entity;
	}

	@Override public int getVersionConvertingTo() {
		return 15;
	}
}
