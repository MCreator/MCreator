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

package net.mcreator.element.converter.v2021_2;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.EffectEntry;
import net.mcreator.element.types.Potion;
import net.mcreator.element.types.PotionEffect;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class PotionToEffectConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(PotionToEffectConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Potion potion = (Potion) input;

		String originalName = input.getModElement().getName();

		try {
			String displayName = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("name")
					.getAsString();

			if (displayName.isEmpty())
				displayName = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("effectName")
						.getAsString();

			if (displayName.isEmpty())
				displayName = originalName;

			potion.potionName = displayName;
			potion.splashName = "Splash " + displayName;
			potion.lingeringName = "Lingering " + displayName;
			potion.arrowName = "Arrow of " + displayName;

			potion.effects = new ArrayList<>();
			Potion.CustomEffectEntry effectEntry = new Potion.CustomEffectEntry();
			effectEntry.effect = new EffectEntry(workspace, "CUSTOM:" + potion.getModElement().getName());
			effectEntry.amplifier = 0;
			effectEntry.duration = 3600;
			effectEntry.ambient = false;
			effectEntry.showParticles = true;
			potion.effects.add(effectEntry);

			workspace.getModElementManager().storeModElementPicture(potion);
			workspace.getGenerator().generateElement(potion);
			workspace.getModElementManager().storeModElement(potion);

			PotionEffect potionEffect = new Gson().fromJson(jsonElementInput.getAsJsonObject().get("definition"),
					PotionEffect.class);

			// Pre-update for FV31 - new texture types
			try {
				FileIO.copyFile(workspace.getFolderManager()
								.getTextureFile(FilenameUtilsPatched.removeExtension(potionEffect.icon), TextureType.OTHER),
						workspace.getFolderManager()
								.getTextureFile(FilenameUtilsPatched.removeExtension(potionEffect.icon),
										TextureType.EFFECT));
			} catch (Exception e) {
				LOG.warn("Failed to copy image for potion effect " + potionEffect.getModElement().getType() + ": "
						+ e.getMessage());
			}

			potionEffect.setModElement(new ModElement(workspace,
					ConverterUtils.findSuitableModElementName(workspace, originalName + "PotionEffect"),
					ModElementType.POTIONEFFECT));

			potionEffect.getModElement().setParentFolder(
					FolderElement.findFolderByPath(input.getModElement().getWorkspace(),
							input.getModElement().getFolderPath()));

			// for backwards game saves compatibility
			potionEffect.getModElement().setRegistryName(input.getModElement().getRegistryName());

			workspace.getModElementManager().storeModElementPicture(potionEffect);
			workspace.addModElement(potionEffect.getModElement());
			workspace.getGenerator().generateElement(potionEffect);
			workspace.getModElementManager().storeModElement(potionEffect);
		} catch (Exception e) {
			LOG.warn("Failed to update potion to new format", e);
		}

		return potion;
	}

	@Override public int getVersionConvertingTo() {
		return 23;
	}

}