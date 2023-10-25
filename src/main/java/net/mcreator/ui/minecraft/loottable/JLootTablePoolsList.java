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

package net.mcreator.ui.minecraft.loottable;

import net.mcreator.element.types.LootTable;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSingleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class JLootTablePoolsList extends JSingleEntriesList<JLootTablePool, LootTable.Pool> {

	public JLootTablePoolsList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);
		setOpaque(false);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));

		add.setText(L10N.t("elementgui.loot_table.add_pool"));
		add.addActionListener(e -> {
			JLootTablePool pool = new JLootTablePool(mcreator, gui, entries, entryList);
			registerEntryUI(pool);
			pool.addInitialEntry();
		});

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JLootTablePool::reloadDataLists);
	}

	public void addInitialPool() {
		JLootTablePool pool = new JLootTablePool(mcreator, gui, entries, entryList);
		registerEntryUI(pool);
		pool.addInitialEntry();
	}

	@Override public List<LootTable.Pool> getEntries() {
		return entryList.stream().map(JLootTablePool::getPool).filter(Objects::nonNull).toList();
	}

	@Override public void setEntries(List<LootTable.Pool> lootTablePools) {
		lootTablePools.forEach(e -> {
			JLootTablePool pool = new JLootTablePool(mcreator, gui, entries, entryList);
			registerEntryUI(pool);
			pool.setPool(e);
		});
	}

}
