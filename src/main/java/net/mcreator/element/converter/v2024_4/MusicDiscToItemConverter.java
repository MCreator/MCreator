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

package net.mcreator.element.converter.v2024_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.Item;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.List;

public class MusicDiscToItemConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		JsonObject musicdisc = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");
		Item item = new Item(new ModElement(workspace, input.getModElement().getName(), ModElementType.ITEM));

		item.name = musicdisc.get("name").getAsString();
		item.texture = new TextureHolder(workspace, musicdisc.get("texture").getAsString());
		item.creativeTabs = new ArrayList<>();

		item.customModelName = "Normal";
		item.renderType = 0;

		item.stackSize = 1;

		JsonObject creativeTab = musicdisc.getAsJsonObject("creativeTab");
		if (creativeTab != null && !creativeTab.get("value").getAsString().equals("No creative tab entry")) {
			item.creativeTabs = List.of(new TabEntry(workspace, creativeTab.get("value").getAsString()));
		} else if (musicdisc.has("creativeTabs")) {
			musicdisc.getAsJsonArray("creativeTabs").iterator().forEachRemaining(element -> {
				if (element.getAsJsonObject().has("value"))
					item.creativeTabs.add(
							new TabEntry(workspace, element.getAsJsonObject().get("value").getAsString()));
			});
		}

		if (musicdisc.has("rarity")) {
			item.rarity = musicdisc.get("rarity").getAsString();
		} else {
			item.rarity = "RARE";
		}

		item.isMusicDisc = true;
		item.musicDiscDescription = musicdisc.get("description").getAsString();
		if (musicdisc.has("music") && musicdisc.get("music").getAsJsonObject().has("value"))
			item.musicDiscMusic = new Sound(workspace,
					musicdisc.get("music").getAsJsonObject().get("value").getAsString());
		else
			item.isMusicDisc = false;
		if (musicdisc.has("lengthInTicks")) {
			item.musicDiscLengthInTicks = musicdisc.get("lengthInTicks").getAsInt();
		} else {
			item.musicDiscLengthInTicks = 100;
		}
		if (musicdisc.has("analogOutput")) {
			item.musicDiscAnalogOutput = musicdisc.get("analogOutput").getAsInt();
		} else {
			item.musicDiscAnalogOutput = 0;
		}

		List<String> infoFixedValues = new ArrayList<>();
		String infoProcedureName = null;
		if (musicdisc.has("specialInfo")) {
			musicdisc.getAsJsonArray("specialInfo").iterator()
					.forEachRemaining(element -> infoFixedValues.add(element.getAsString()));
		} else if (musicdisc.get("specialInformation") != null) {
			if (musicdisc.get("specialInformation").getAsJsonObject().get("fixedValue") != null) {
				musicdisc.get("specialInformation").getAsJsonObject().getAsJsonArray("fixedValue")
						.forEach(element -> infoFixedValues.add(element.getAsString()));
			}

			if (musicdisc.get("specialInformation").getAsJsonObject().get("name") != null)
				infoProcedureName = musicdisc.get("specialInformation").getAsJsonObject().get("name").getAsString();
		}
		item.specialInformation = new StringListProcedure(infoProcedureName, infoFixedValues);

		if (musicdisc.has("glowCondition")) {
			JsonObject rangedGlow = musicdisc.getAsJsonObject("glowCondition");
			String glowConditionProcedureName = rangedGlow.has("name") ? rangedGlow.get("name").getAsString() : null;
			boolean value = musicdisc.has("hasGlow") ? musicdisc.get("hasGlow").getAsBoolean() : // Old format
					rangedGlow.get("fixedValue").getAsBoolean(); // New format of 2023.4

			item.glowCondition = new LogicProcedure(glowConditionProcedureName, value);
		} else if (musicdisc.has("hasGlow")) {
			item.glowCondition = new LogicProcedure(null, musicdisc.get("hasGlow").getAsBoolean());
		}

		if (musicdisc.has("onRightClickedInAir"))
			item.onRightClickedInAir = new Procedure(
					musicdisc.get("onRightClickedInAir").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onRightClickedOnBlock"))
			item.onRightClickedOnBlock = new Procedure(
					musicdisc.get("onRightClickedOnBlock").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onCrafted"))
			item.onCrafted = new Procedure(musicdisc.get("onCrafted").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onEntityHitWith"))
			item.onEntityHitWith = new Procedure(
					musicdisc.get("onEntityHitWith").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onItemInInventoryTick"))
			item.onItemInInventoryTick = new Procedure(
					musicdisc.get("onItemInInventoryTick").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onItemInUseTick"))
			item.onItemInUseTick = new Procedure(
					musicdisc.get("onItemInUseTick").getAsJsonObject().get("name").getAsString());
		if (musicdisc.has("onEntitySwing"))
			item.onEntitySwing = new Procedure(
					musicdisc.get("onEntitySwing").getAsJsonObject().get("name").getAsString());

		return item;
	}

	@Override public int getVersionConvertingTo() {
		return 70;
	}

}