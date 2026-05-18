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

package net.mcreator.element.converter.v2026_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.ProfessionEntry;
import net.mcreator.element.types.VillagerTrade;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VillagerTradeSplitter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		VillagerTrade originalTrade = (VillagerTrade) input;
		String originalName = input.getModElement().getName();
		JsonObject originalJsonDef = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		// If the list of villager trades is empty, we don't have to do anything
		if (originalJsonDef.get("tradeEntries") != null && !originalJsonDef.getAsJsonArray("tradeEntries").isEmpty()) {
			// Get all defined trades and group them by profession
			LinkedHashMap<String, ArrayList<JsonElement>> professionToTrades = new LinkedHashMap<>();

			originalJsonDef.getAsJsonArray("tradeEntries").iterator().forEachRemaining(e -> {
				String profession = e.getAsJsonObject().get("villagerProfession").getAsString();
				professionToTrades.computeIfAbsent(profession, _ -> new ArrayList<>());
				professionToTrades.get(profession).addAll(e.getAsJsonObject().get("entries").getAsJsonArray().asList());
			});

			// Reuse the current mod element for the first profession in the map
			var firstEntry = professionToTrades.pollFirstEntry();
			originalTrade.villagerProfession = new ProfessionEntry(workspace, firstEntry.getKey());
			for (var tradeJson : firstEntry.getValue()) {
				originalTrade.trades.add(
						entryFromJsonObject(workspace, tradeJson.getAsJsonObject(), firstEntry.getKey()));
			}

			// Create new mod elements to handle the remaining professions
			professionToTrades.forEach((profession, tradeJsons) -> {
				VillagerTrade newTrade = new VillagerTrade(new ModElement(workspace,
						ConverterUtils.findSuitableModElementName(workspace, originalName + "ExtraProfession"),
						ModElementType.VILLAGERTRADE));
				newTrade.villagerProfession = new ProfessionEntry(workspace, profession);
				for (var tradeJson : tradeJsons) {
					newTrade.trades.add(entryFromJsonObject(workspace, tradeJson.getAsJsonObject(), profession));
				}

				newTrade.getModElement().setParentFolder(
						FolderElement.findFolderByPath(workspace, input.getModElement().getFolderPath()));
				workspace.getModElementManager().storeModElementPicture(newTrade);
				workspace.addModElement(newTrade.getModElement());
				workspace.getGenerator().generateElement(newTrade);
				workspace.getModElementManager().storeModElement(newTrade);
			});
		}

		return originalTrade;
	}

	@Override public int getVersionConvertingTo() {
		return 86;
	}

	private VillagerTrade.TradeEntry entryFromJsonObject(Workspace workspace, JsonObject tradeJson, String profession) {
		VillagerTrade.TradeEntry entry = new VillagerTrade.TradeEntry();
		entry.price1 = new MItemBlock(workspace, tradeJson.get("price1").getAsString());
		entry.countPrice1 = tradeJson.get("countPrice1").getAsInt();
		if (tradeJson.get("price2") != null) {
			entry.price2 = new MItemBlock(workspace, tradeJson.get("price2").getAsString());
		}
		entry.countPrice2 = tradeJson.get("countPrice2").getAsInt();
		entry.offer = new MItemBlock(workspace, tradeJson.get("offer").getAsString());
		entry.countOffer = tradeJson.get("countOffer").getAsInt();
		// Level was previously ignored for wandering traders, defaulting to the COMMON trade set
		entry.level = "WANDERING_TRADER".equals(profession) ? 1 : tradeJson.get("level").getAsInt();
		entry.maxTrades = tradeJson.get("maxTrades").getAsInt();
		entry.xp = tradeJson.get("xp").getAsInt();
		entry.priceMultiplier = tradeJson.get("priceMultiplier").getAsDouble();

		return entry;
	}
}
