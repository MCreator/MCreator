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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.JEntriesList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JLootTablePool extends JEntriesList {

	private final JSpinner minrolls = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JSpinner maxrolls = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JSpinner minbonusrolls = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JSpinner maxbonusrolls = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JCheckBox hasbonusrolls = L10N.checkbox("elementgui.loot_table.enable_pool_rolls");

	private final List<JLootTableEntry> entryList = new ArrayList<>();

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JLootTablePool(MCreator mcreator, IHelpContext gui, JPanel parent, List<JLootTablePool> pollList) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		pollList.add(this);

		setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());

		hasbonusrolls.setOpaque(false);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		topbar.add(L10N.label("elementgui.loot_table.min_rolls"));
		topbar.add(minrolls);
		topbar.add(L10N.label("elementgui.loot_table.max_rolls"));
		topbar.add(maxrolls);

		topbar.add(new JEmptyBox(15, 5));

		topbar.add(hasbonusrolls);
		topbar.add(L10N.label("elementgui.loot_table.min_bonus_rolls"));
		topbar.add(minbonusrolls);
		topbar.add(L10N.label("elementgui.loot_table.max_bonus_rolls"));
		topbar.add(maxbonusrolls);

		topbar.add(Box.createHorizontalGlue());

		JButton add = new JButton(UIRES.get("16px.add.gif"));
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

		component.setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());

		add("North", component);

		entries.setOpaque(false);

		add.addActionListener(e -> {
			JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList);
			registerEntryUI(entry);
		});

		add("Center", entries);

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.loot_table.pool"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		entryList.forEach(JLootTableEntry::reloadDataLists);
	}

	public void addInitialEntry() {
		JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList); // initial add
		registerEntryUI(entry);
	}

	public LootTable.Pool getPool() {
		LootTable.Pool pool = new LootTable.Pool();
		pool.hasbonusrolls = hasbonusrolls.isSelected();
		pool.minrolls = (int) minrolls.getValue();
		pool.maxrolls = (int) maxrolls.getValue();
		pool.minbonusrolls = (int) minbonusrolls.getValue();
		pool.maxbonusrolls = (int) maxbonusrolls.getValue();
		pool.entries = entryList.stream().map(JLootTableEntry::getEntry).filter(Objects::nonNull).toList();

		return pool.entries.isEmpty() ? null : pool;
	}

	public void setPool(LootTable.Pool pool) {
		hasbonusrolls.setSelected(pool.hasbonusrolls);
		minrolls.setValue(pool.minrolls);
		maxrolls.setValue(pool.maxrolls);
		minbonusrolls.setValue(pool.minbonusrolls);
		maxbonusrolls.setValue(pool.maxbonusrolls);

		if (pool.entries != null) {
			pool.entries.forEach(e -> {
				JLootTableEntry entry = new JLootTableEntry(mcreator, entries, entryList);
				registerEntryUI(entry);
				entry.setEntry(e);
			});
		}
	}

}
