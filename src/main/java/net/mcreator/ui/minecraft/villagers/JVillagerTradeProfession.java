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

package net.mcreator.ui.minecraft.villagers;

import net.mcreator.element.parts.VillagerTradeEntry;
import net.mcreator.element.types.VillagerTrade;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JVillagerTradeProfession extends JPanel {

	private final JComboBox<String> villager = new JComboBox<>();

	private final List<JVillagerTradeEntry> entryList = new ArrayList<>();

	private final MCreator mcreator;
	private final Workspace workspace;

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JVillagerTradeProfession(MCreator mcreator, JPanel parent, List<JVillagerTradeProfession> professionList) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;
		this.workspace = mcreator.getWorkspace();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		professionList.add(this);

		ElementUtil.loadAllVIllagerProfessions(workspace).forEach(e -> villager.addItem(e.getName()));

		setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		topbar.add(L10N.label("elementgui.villager_trade.profession"));
		topbar.add(villager);

		topbar.add(Box.createHorizontalGlue());

		JButton add = new JButton(UIRES.get("16px.add.gif"));
		add.setText(L10N.t("elementgui.villager_trade.add_entry"));

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.villager_trade.remove_profession_trades"));
		remove.addActionListener(e -> {
			professionList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		JComponent component = PanelUtils.centerAndEastElement(topbar, PanelUtils.join(FlowLayout.RIGHT, add, remove));
		component.setOpaque(true);
		component.setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());

		add("North", component);
		entries.setOpaque(false);

		add.addActionListener(e -> new JVillagerTradeEntry(mcreator, entries, entryList));
		add("Center", entries);

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.villager_trade.profession_trades"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		entryList.forEach(JVillagerTradeEntry::reloadDataLists);
	}

	public void addInitialEntry() {
		new JVillagerTradeEntry(mcreator, entries, entryList);
	}

	public VillagerTrade.CustomTradeEntry getTradeEntry() {
		VillagerTrade.CustomTradeEntry entry = new VillagerTrade.CustomTradeEntry();
		entry.tradeEntry = new VillagerTradeEntry(workspace, (String) villager.getSelectedItem());
		entry.entries = entryList.stream().map(JVillagerTradeEntry::getEntry).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (entry.entries.isEmpty())
			return null;
		return entry;
	}

	public void setTradeEntries(VillagerTrade.CustomTradeEntry tradeEntry) {
		villager.setSelectedItem(tradeEntry.tradeEntry.getUnmappedValue());
		if (tradeEntry.entries != null)
			tradeEntry.entries.forEach(e -> new JVillagerTradeEntry(mcreator, entries, entryList).setEntry(e));
	}
}
