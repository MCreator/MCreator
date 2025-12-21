/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2025_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Item;
import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class ItemToBedrockConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Item item = (Item) input;
		JsonObject itemDefinition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		if (workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			BEItem beitem = new BEItem(
					new ModElement(workspace, item.getModElement().getName(), ModElementType.BEITEM));
			beitem.name = item.name;
			beitem.texture = item.texture;
			beitem.stackSize = item.stackSize;
			beitem.useDuration = (double) item.useDuration / 20;
			beitem.maxDurability = item.damageCount;
			beitem.enableMeleeDamage = item.enableMeleeDamage;
			beitem.damageVsEntity = item.damageVsEntity;

			if (itemDefinition.has("glowCondition")) {
				JsonObject itemGlowCondition = itemDefinition.getAsJsonObject("glowCondition");
				beitem.hasGlint = itemDefinition.has("hasGlow") ? itemDefinition.get("hasGlow").getAsBoolean() :
						// Old format
						itemGlowCondition.get("fixedValue").getAsBoolean(); // New format of 2023.4
			} else if (itemDefinition.has("hasGlow")) {
				beitem.hasGlint = itemDefinition.get("hasGlow").getAsBoolean();
			}
			beitem.isFood = item.isFood;
			beitem.foodNutritionalValue = item.nutritionalValue;
			beitem.foodSaturation = item.saturation;
			beitem.foodCanAlwaysEat = item.isAlwaysEdible;
			return beitem;
		}

		return item;
	}

	@Override public int getVersionConvertingTo() {
		return 81;
	}

}
