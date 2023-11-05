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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.ProfessionEntry;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class VillagerTrade extends GeneratableElement {

	@ModElementReference public List<CustomTradeEntry> tradeEntries;

	private VillagerTrade() {
		this(null);
	}

	public VillagerTrade(ModElement element) {
		super(element);
		tradeEntries = new ArrayList<>();
	}

	public static class CustomTradeEntry {

		public ProfessionEntry villagerProfession;
		@ModElementReference public List<Entry> entries;

		public static class Entry {

			public MItemBlock price1;
			public int countPrice1;

			public MItemBlock price2;
			public int countPrice2;

			public MItemBlock offer;
			public int countOffer;

			public int level;
			public int maxTrades;
			public int xp;

			public double priceMultiplier;
		}
	}

	public boolean hasVillagerTrades(boolean wandering) {
		for (CustomTradeEntry tradeEntry : tradeEntries) {
			if (wandering && "WANDERING_TRADER".equals(tradeEntry.villagerProfession.getUnmappedValue()))
				return true;
			if (!wandering && !"WANDERING_TRADER".equals(tradeEntry.villagerProfession.getUnmappedValue()))
				return true;
		}
		return false;
	}
}
