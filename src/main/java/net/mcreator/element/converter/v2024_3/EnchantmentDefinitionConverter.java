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

package net.mcreator.element.converter.v2024_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Enchantment;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantmentDefinitionConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(EnchantmentDefinitionConverter.class);

	private static final Gson gson = new GsonBuilder().create();

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Enchantment enchantment = (Enchantment) input;
		try {
			JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			String type = definition.get("type").getAsString();
			enchantment.supportedSlots = switch (type) {
				case "ARMOR_FEET" -> "feet";
				case "ARMOR_LEGS" -> "legs";
				case "ARMOR_CHEST" -> "chest";
				case "ARMOR_HEAD" -> "head";
				case "ARMOR", "WEARABLE" -> "armor";
				case "SWORD", "FIRE_ASPECT", "SHARP", "WEAPON", "DIGGER", "DIGGER_LOOT", "FISHING_ROD", "TRIDENT",
					 "BOW", "CROSSBOW", "MACE" -> "mainhand";
				default -> "any";
			};

			String rarity = definition.get("rarity").getAsString();
			enchantment.weight = switch (rarity) {
				case "UNCOMMON" -> 5;
				case "RARE" -> 2;
				case "VERY_RARE" -> 1;
				default -> 10;
			};
			enchantment.anvilCost = switch (rarity) {
				case "UNCOMMON" -> 2;
				case "RARE" -> 4;
				case "VERY_RARE" -> 8;
				default -> 1;
			};

			List<net.mcreator.element.parts.Enchantment> compatibleEnchantments = definition.has(
					"compatibleEnchantments") ?
					Arrays.asList(gson.fromJson(definition.get("compatibleEnchantments"),
							net.mcreator.element.parts.Enchantment[].class)) :
					new ArrayList<>();
			boolean excludeEnchantments =
					definition.has("excludeEnchantments") && definition.get("excludeEnchantments").getAsBoolean();
			enchantment.incompatibleEnchantments = new ArrayList<>();
			if (!compatibleEnchantments.isEmpty()) { // if empty, it is compatible with all enchantments thus we leave enchantment.incompatibleEnchantments empty
				if (excludeEnchantments) { // incompatibleEnchantments works in exclude mode - directly convert
					for (net.mcreator.element.parts.Enchantment compatibleEnchantment : compatibleEnchantments) {
						// should not be possible to have tags here in FV<69, but just in case
						if (!compatibleEnchantment.getUnmappedValue().startsWith("#")) {
							compatibleEnchantment.setWorkspace(workspace);
							enchantment.incompatibleEnchantments.add(compatibleEnchantment);
						}
					}
				} else { // if list was in include mode, we need to exclude all but those listed here as a workaround
					List<DataListEntry> allEnchantments = ElementUtil.loadAllEnchantments(workspace);
					for (DataListEntry entry : allEnchantments) {
						net.mcreator.element.parts.Enchantment enchantmentEntry = new net.mcreator.element.parts.Enchantment(
								workspace, entry);
						// If in include mode and compatibleEnchantments does not contain the entry, add it to incompatibleEnchantments
						if (!compatibleEnchantments.contains(enchantmentEntry)) {
							enchantment.incompatibleEnchantments.add(enchantmentEntry);
						}
					}
				}
			}

			List<MItemBlock> compatibleItems = definition.has("compatibleItems") ?
					Arrays.asList(gson.fromJson(definition.get("compatibleItems"), MItemBlock[].class)) :
					new ArrayList<>();
			boolean excludeItems = definition.has("excludeItems") && definition.get("excludeItems").getAsBoolean();
			enchantment.supportedItems = new ArrayList<>();
			if (!compatibleItems.isEmpty()
					&& !excludeItems) { // include mode with non-empty compatibleItems list, we can convert directly
				for (MItemBlock compatibleItem : compatibleItems) {
					// we do not allow tags in enchantment.supportedItems, unless there is only one item (tag) in the list
					if (!compatibleItem.getUnmappedValue().startsWith("TAG:") || compatibleItems.size() == 1) {
						compatibleItem.setWorkspace(workspace);
						enchantment.supportedItems.add(compatibleItem);
					}
				}
			}
			// If at this point, supportedItems is empty, we need to find suitable fallback
			if (enchantment.supportedItems.isEmpty()) {
				enchantment.supportedItems.add(new MItemBlock(workspace, "TAG:enchantable/" + switch (type) {
					case "ARMOR_FEET" -> "foot_armor";
					case "ARMOR_LEGS" -> "leg_armor";
					case "ARMOR_CHEST" -> "chest_armor";
					case "ARMOR_HEAD" -> "head_armor";
					case "ARMOR" -> "armor";
					case "SWORD" -> "sword";
					case "FIRE_ASPECT" -> "fire_aspect";
					case "SHARP" -> "sharp_weapon";
					case "WEAPON" -> "weapon";
					case "DIGGER_LOOT" -> "mining_loot";
					case "FISHING_ROD" -> "fishing";
					case "TRIDENT" -> "trident";
					case "BREAKABLE" -> "durability";
					case "BOW" -> "bow";
					case "WEARABLE" -> "equippable";
					case "CROSSBOW" -> "crossbow";
					case "VANISHABLE" -> "vanishing";
					case "MACE" -> "mace";
					default -> "mining";
				}));
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert enchantment to the new definition format", e);
		}
		return enchantment;
	}

	@Override public int getVersionConvertingTo() {
		return 69;
	}

}
