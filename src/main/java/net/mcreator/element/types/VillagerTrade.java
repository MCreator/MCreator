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
import net.mcreator.element.parts.VillagerTradeEntry;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.List;

public class VillagerTrade extends GeneratableElement {

	public List<VillagerTrade.CustomTradeEntry> tradeEntry;

	public VillagerTrade(ModElement element) {
		super(element);
		tradeEntry = new ArrayList<>();
	}

	public static class CustomTradeEntry {
		public VillagerTradeEntry tradeEntry;
		public int level;
		public MItemBlock price1;
		public MItemBlock price2;
		public MItemBlock sale1;
		public MItemBlock sale2;
		public int maxTrades;
		public int xp;
		public double priceMultiplier;
	}
}
