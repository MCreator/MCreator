/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JLootTablePool extends JEntriesList {

	private final JMinMaxSpinner rolls = new JMinMaxSpinner(1, 1, 0, 64000, 1);
	private final JMinMaxSpinner bonusrolls = new JMinMaxSpinner(1, 1, 0, 64000, 1);
	private final JCheckBox hasbonusrolls = L10N.checkbox("elementgui.loot_table.enable_pool_rolls");

	private final List<JLootTableEntry> entryList = new ArrayList<>();

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JLootTablePool(MCreator mcreator, IHelpContext gui, JPanel parent, List<JLootTablePool> pollList) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		pollList.add(this);

		setBackground((Theme.current().getBackgroundColor()).brighter());

		rolls.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getSecondAltBackgroundColor()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		rolls.setAllowEqualValues(true);
		bonusrolls.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getSecondAltBackgroundColor()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		bonusrolls.setAllowEqualValues(true);
		hasbonusrolls.setOpaque(false);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		topbar.add(L10N.label("elementgui.loot_table.rolls"));
		topbar.add(rolls);

		topbar.add(new JEmptyBox(15, 5));

		topbar.add(hasbonusrolls);
		topbar.add(L10N.label("elementgui.loot_table.bonus_rolls"));
		topbar.add(bonusrolls);

		topbar.add(Box.createHorizontalGlue());

		JButton add = new JButton(UIRES.get("16px.add"));
		add.setText(L10N.t("elementgui.loot_table.add_pool_entry"));

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.loot_table.remove_pool"));
		remove.addActionListener(e -> {
			pollList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		JComponent component = PanelUtils.centerAndEastElement(topbar, PanelUtils.join(FlowLayout.RIGHT, add, remove));
		component.setOpaque(true);

		component.setBackground((Theme.current().getBackgroundColor()).brighter());

		add("North", component);

		entries.setOpaque(false);

		add.addActionListener(e -> {
			JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList);
			registerEntryUI(entry);
		});

		add("Center", entries);

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.loot_table.pool"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		parent.revalidate();
		parent.repaint();
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JLootTableEntry::reloadDataLists);
	}

	public void addInitialEntry() {
		JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList); // initial add
		registerEntryUI(entry);
	}

	public LootTable.Pool getPool() {
		LootTable.Pool pool = new LootTable.Pool();
		pool.hasbonusrolls = hasbonusrolls.isSelected();
		pool.minrolls = rolls.getIntMinValue();
		pool.maxrolls = rolls.getIntMaxValue();
		pool.minbonusrolls = bonusrolls.getIntMinValue();
		pool.maxbonusrolls = bonusrolls.getIntMaxValue();
		pool.entries = entryList.stream().map(JLootTableEntry::getEntry).filter(Objects::nonNull).toList();

		return pool.entries.isEmpty() ? null : pool;
	}

	public void setPool(LootTable.Pool pool) {
		hasbonusrolls.setSelected(pool.hasbonusrolls);
		rolls.setMinValue(pool.minrolls);
		rolls.setMaxValue(pool.maxrolls);
		bonusrolls.setMinValue(pool.minbonusrolls);
		bonusrolls.setMaxValue(pool.maxbonusrolls);

		if (pool.entries != null) {
			pool.entries.forEach(e -> {
				JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList);
				registerEntryUI(entry);
				entry.setEntry(e);
			});
		}
	}

}
