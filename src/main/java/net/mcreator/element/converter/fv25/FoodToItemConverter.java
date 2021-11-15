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

package net.mcreator.element.converter.fv25;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Food;
import net.mcreator.element.types.Item;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FoodToItemConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(FoodToItemConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Food food = (Food) input;

		Item item = new Item(new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEM));

		try {
			item.name = food.name;
			item.texture = food.texture;
			item.renderType = food.renderType;
			item.customModelName = food.customModelName;
			item.creativeTab = food.creativeTab;
			item.rarity = food.rarity;
			item.specialInfo = food.specialInfo;
			item.stackSize = food.stackSize;
			item.isFood = true;
			item.nutritionalValue = food.nutritionalValue;
			item.saturation = food.saturation;
			item.isAlwaysEdible = food.isAlwaysEdible;
			item.forDogs = food.forDogs;
			item.useDuration = food.eatingSpeed;
			item.resultItem = food.resultItem;
			item.animation = food.animation;
			item.hasGlow = food.hasGlow;
			item.glowCondition = food.glowCondition;
			item.onRightClickedInAir = food.onRightClicked;
			item.onRightClickedOnBlock = food.onRightClickedOnBlock;
			item.onCrafted = food.onCrafted;
			item.onEaten = food.onEaten;
			item.onEntityHitWith = food.onEntityHitWith;
			item.onEntitySwing = food.onEntitySwing;
			item.onItemInInventoryTick = food.onItemInInventoryTick;
			item.onItemInUseTick = food.onItemInUseTick;
			item.onDroppedByPlayer = food.onDroppedByPlayer;

			LOG.debug("Deleting " + input.getModElement().getName() + " food mod element and replacing it by an item mod element...");
			workspace.removeModElement(food.getModElement());

			item.getModElement().setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
			workspace.getModElementManager().storeModElementPicture(item);
			workspace.addModElement(item.getModElement());
			workspace.getGenerator().generateElement(item);
			workspace.getModElementManager().storeModElement(item);
		} catch (Exception e) {
			LOG.warn("Failed to update food to new format", e);
		}

		return item;
	}

	@Override public int getVersionConvertingTo() {
		return 25;
	}
}
