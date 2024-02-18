/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.villagers;

import net.mcreator.element.types.VillagerTrade;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSingleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class JVillagerTradeProfessionsList
		extends JSingleEntriesList<JVillagerTradeProfession, VillagerTrade.CustomTradeEntry> {

	public JVillagerTradeProfessionsList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));

		add.setText(L10N.t("elementgui.villager_trade.add_profession_trades"));
		add.addActionListener(e -> {
			JVillagerTradeProfession profession = new JVillagerTradeProfession(mcreator, gui, entries, entryList);
			registerEntryUI(profession);
			profession.addInitialEntry();
		});

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JVillagerTradeProfession::reloadDataLists);
	}

	public void addInitialTrade() {
		JVillagerTradeProfession profession = new JVillagerTradeProfession(mcreator, gui, entries, entryList);
		registerEntryUI(profession);
		profession.addInitialEntry();
	}

	@Override public List<VillagerTrade.CustomTradeEntry> getEntries() {
		return entryList.stream().map(JVillagerTradeProfession::getTradeEntry).filter(Objects::nonNull).toList();
	}

	@Override public void setEntries(List<VillagerTrade.CustomTradeEntry> tradesEntries) {
		tradesEntries.forEach(e -> {
			JVillagerTradeProfession profession = new JVillagerTradeProfession(mcreator, gui, entries, entryList);
			registerEntryUI(profession);
			profession.setTradeEntries(e);
		});
	}

}
