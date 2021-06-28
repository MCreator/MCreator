/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.element.converter.fv23;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.EffectEntry;
import net.mcreator.element.types.Potion;
import net.mcreator.element.types.PotionEffect;
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

		try {
			potion.potionName = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("name")
					.getAsString();
			potion.splashName =
					"Splash " + jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("name")
							.getAsString();
			potion.lingeringName =
					"Lingering " + jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("name")
							.getAsString();
			potion.arrowName =
					"Arrow of " + jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("name")
							.getAsString();

			potion.effects = new ArrayList<>();
			Potion.CustomEffectEntry effectEntry = new Potion.CustomEffectEntry();
			effectEntry.effect = new EffectEntry(workspace, "CUSTOM:" + potion.getModElement().getName() + "Effect");
			effectEntry.amplifier = 0;
			effectEntry.duration = 3600;
			effectEntry.ambient = false;
			effectEntry.showParticles = true;
			potion.effects.add(effectEntry);

			// if we did not split potion to effect element yet, add it now
			if (workspace.getModElementByName(input.getModElement().getName() + "Effect") == null) {
				PotionEffect potionEffect = new Gson()
						.fromJson(jsonElementInput.getAsJsonObject().get("definition"), PotionEffect.class);

				potionEffect.setModElement(new ModElement(workspace, input.getModElement().getName() + "Effect",
						ModElementType.POTIONEFFECT));

				potionEffect.getModElement()
						.setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
				potionEffect.getModElement().setRegistryName(
						input.getModElement().getRegistryName()); // for backwards game saves compatibility

				workspace.getModElementManager().storeModElementPicture(potionEffect);
				workspace.addModElement(potionEffect.getModElement());
				workspace.getGenerator().generateElement(potionEffect);
				workspace.getModElementManager().storeModElement(potionEffect);
			}
		} catch (Exception e) {
			LOG.warn("Failed to update potion to new format", e);
		}

		return potion;
	}

	@Override public int getVersionConvertingTo() {
		return 23;
	}

}
