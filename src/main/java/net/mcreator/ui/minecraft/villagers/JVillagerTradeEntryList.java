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

package net.mcreator.ui.minecraft.villagers;

import net.mcreator.element.types.VillagerTrade;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

public class JVillagerTradeEntryList extends JSimpleEntriesList<JVillagerTradeEntry, VillagerTrade.TradeEntry> {

	public JVillagerTradeEntryList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));
		add.setText(L10N.t("elementgui.villager_trade.add_entry"));

		ComponentUtils.borderWrap(this);
	}

	@Nullable @Override
	protected JVillagerTradeEntry newEntry(JPanel parent, List<JVillagerTradeEntry> entryList, boolean userAction) {
		return new JVillagerTradeEntry(mcreator, gui, entries, entryList);
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		entryList.forEach(e -> validationResult.addValidationGroup(e.getValidationResult()));
		return validationResult;
	}
}
