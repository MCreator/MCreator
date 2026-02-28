/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2026_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Tool;
import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class ToolToBedrockConverter implements IConverter{
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Tool tool = (Tool) input;
		JsonObject itemDefinition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		if (workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			BEItem beitem = new BEItem(
					new ModElement(workspace, tool.getModElement().getName(), ModElementType.BEITEM));
			beitem.name = tool.name;
			beitem.texture = tool.texture;
			beitem.stackSize = 1;
			beitem.maxDurability = tool.usageCount;
			beitem.enableMeleeDamage = tool.damageVsEntity > 0;
			beitem.damageVsEntity = (int) Math.min(tool.damageVsEntity, 255);
			beitem.enableCreativeTab = true;
			beitem.creativeTab = "TOOLS";
			beitem.handEquipped = true;

			if (itemDefinition.has("glowCondition")) {
				JsonObject itemGlowCondition = itemDefinition.getAsJsonObject("glowCondition");
				beitem.hasGlint = itemDefinition.has("hasGlow") ? itemDefinition.get("hasGlow").getAsBoolean() :
						// Old format
						itemGlowCondition.get("fixedValue").getAsBoolean(); // New format of 2023.4
			} else if (itemDefinition.has("hasGlow")) {
				beitem.hasGlint = itemDefinition.get("hasGlow").getAsBoolean();
			}

			return beitem;
		}

		return tool;
	}

	@Override public int getVersionConvertingTo() {
		return 85;
	}
}
