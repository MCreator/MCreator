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

package net.mcreator.element.converter.v2022_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.Item;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FoodToItemConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(FoodToItemConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			Item item = new Item(new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEM));

			JsonObject food = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");
			item.name = food.get("name").getAsString();
			item.texture = food.get("texture").getAsString();
			item.renderType = food.get("renderType").getAsInt();
			if (food.get("customModelName") != null)
				item.customModelName = food.get("customModelName").getAsString();
			item.creativeTab = new TabEntry(workspace,
					food.get("creativeTab").getAsJsonObject().get("value").getAsString());
			if (food.get("rarity") != null)
				item.rarity = food.get("rarity").getAsString();
			List<String> specialInfo = new ArrayList<>();
			if (food.get("specialInfo") != null)
				food.getAsJsonArray("specialInfo").iterator()
						.forEachRemaining(element -> specialInfo.add(element.getAsString()));
			item.specialInformation = new StringListProcedure(null, specialInfo);
			item.stackSize = food.get("stackSize").getAsInt();
			item.isFood = true;
			item.nutritionalValue = food.get("nutritionalValue").getAsInt();
			item.saturation = food.get("saturation").getAsDouble();
			item.isAlwaysEdible = food.get("isAlwaysEdible").getAsBoolean();
			item.isMeat = food.get("forDogs").getAsBoolean();
			item.useDuration = food.get("eatingSpeed").getAsInt();
			if (food.get("resultItem") != null)
				item.eatResultItem = new MItemBlock(workspace,
						food.get("resultItem").getAsJsonObject().get("value").getAsString());
			item.animation = food.get("animation").getAsString();
			if (food.get("hasGlow").getAsBoolean()) {
				if (food.get("glowCondition") != null)
					item.glowCondition = new LogicProcedure(
							food.get("glowCondition").getAsJsonObject().get("name").getAsString(), true);
				else
					item.glowCondition = new LogicProcedure(null, true);
			}
			if (food.get("onRightClicked") != null)
				item.onRightClickedInAir = new Procedure(
						food.get("onRightClicked").getAsJsonObject().get("name").getAsString());
			if (food.get("onRightClickedOnBlock") != null)
				item.onRightClickedOnBlock = new Procedure(
						food.get("onRightClickedOnBlock").getAsJsonObject().get("name").getAsString());
			if (food.get("onCrafted") != null)
				item.onCrafted = new Procedure(food.get("onCrafted").getAsJsonObject().get("name").getAsString());
			if (food.get("onEaten") != null)
				item.onFinishUsingItem = new Procedure(food.get("onEaten").getAsJsonObject().get("name").getAsString());
			if (food.get("onEntityHitWith") != null)
				item.onEntityHitWith = new Procedure(
						food.get("onEntityHitWith").getAsJsonObject().get("name").getAsString());
			if (food.get("onEntitySwing") != null)
				item.onEntitySwing = new Procedure(
						food.get("onEntitySwing").getAsJsonObject().get("name").getAsString());
			if (food.get("onItemInInventoryTick") != null)
				item.onItemInInventoryTick = new Procedure(
						food.get("onItemInInventoryTick").getAsJsonObject().get("name").getAsString());
			if (food.get("onItemInUseTick") != null)
				item.onItemInUseTick = new Procedure(
						food.get("onItemInUseTick").getAsJsonObject().get("name").getAsString());
			if (food.get("onDroppedByPlayer") != null)
				item.onDroppedByPlayer = new Procedure(
						food.get("onDroppedByPlayer").getAsJsonObject().get("name").getAsString());

			return item;
		} catch (Exception e) {
			LOG.warn("Failed to update food to new format", e);
			return null;
		}
	}

	@Override public int getVersionConvertingTo() {
		return 28;
	}
}
