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
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class VillagerTrade extends GeneratableElement {

	public ProfessionEntry villagerProfession;
	@ModElementReference public List<TradeEntry> trades;

	private VillagerTrade() {
		this(null);
	}

	public VillagerTrade(ModElement element) {
		super(element);
		trades = new ArrayList<>();
	}

	public boolean isWanderingTrader() {
		return "WANDERING_TRADER".equals(villagerProfession.getUnmappedValue());
	}

	public String professionFolder() {
		String retval = villagerProfession.getMappedValue();
		if (retval.contains(":"))
			return StringUtils.substringAfterLast(retval, ":");
		return retval;
	}

	public static class TradeEntry {

		public MItemBlock price1;
		@Numeric(init = 1, min = 1, max = 99, step = 1) public int countPrice1;

		public MItemBlock price2;
		@Numeric(init = 1, min = 1, max = 99, step = 1)public int countPrice2;

		public MItemBlock offer;
		@Numeric(init = 1, min = 1, max = 99, step = 1)public int countOffer;

		@Numeric(init = 1, min = 1, max = 5, step = 1) public int level;
		@Numeric(init = 10, min = 1, max = 72000, step = 1) public int maxTrades;
		@Numeric(init = 5, min = 1, max = 72000, step = 1) public int xp;

		@Numeric(init = 0.05, min = 0, max = 1, step = 0.01) public double priceMultiplier;

		public TradeEntry() {
			this.countPrice1 = 1;
			this.countPrice2 = 1;
			this.countOffer = 1;
			this.level = 1;
			this.maxTrades = 10;
			this.xp = 5;
			this.priceMultiplier = 0.05;
		}
	}
}
