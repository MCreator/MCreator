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

import net.mcreator.element.parts.ProfessionEntry;
import net.mcreator.element.types.VillagerTrade;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JVillagerTradeProfession extends JEntriesList {

	private final DataListComboBox villagerProfession;

	private final List<JVillagerTradeEntry> entryList = new ArrayList<>();

	private final Workspace workspace;

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JVillagerTradeProfession(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JVillagerTradeProfession> professionList) {
		super(mcreator, new BorderLayout(), gui);

		this.workspace = mcreator.getWorkspace();

		setOpaque(false);

		villagerProfession = new DataListComboBox(mcreator, ElementUtil.loadAllVillagerProfessions(workspace));
		villagerProfession.setRenderer(new JComboBox<>().getRenderer());

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		professionList.add(this);

		setBackground((Theme.current().getBackgroundColor()).brighter());

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		topbar.add(L10N.label("elementgui.villager_trade.profession"));
		topbar.add(villagerProfession);

		topbar.add(Box.createHorizontalGlue());

		JButton add = new JButton(UIRES.get("16px.add"));
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
		component.setBackground((Theme.current().getBackgroundColor()).brighter());

		add("North", component);
		entries.setOpaque(false);

		add.addActionListener(e -> {
			JVillagerTradeEntry entry = new JVillagerTradeEntry(mcreator, gui, entries, entryList);
			registerEntryUI(entry);
		});
		add("Center", entries);

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.villager_trade.profession_trades"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		parent.revalidate();
		parent.repaint();
	}

	@Override public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(villagerProfession, ElementUtil.loadAllVillagerProfessions(workspace));
	}

	public void addInitialEntry() {
		new JVillagerTradeEntry(mcreator, gui, entries, entryList);
	}

	public VillagerTrade.CustomTradeEntry getTradeEntry() {
		VillagerTrade.CustomTradeEntry entry = new VillagerTrade.CustomTradeEntry();
		entry.villagerProfession = new ProfessionEntry(workspace, villagerProfession.getSelectedItem());
		entry.entries = entryList.stream().map(JVillagerTradeEntry::getEntry).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (entry.entries.isEmpty())
			return null;
		return entry;
	}

	public void setTradeEntries(VillagerTrade.CustomTradeEntry tradeEntry) {
		villagerProfession.setSelectedItem(tradeEntry.villagerProfession);
		if (tradeEntry.entries != null)
			tradeEntry.entries.forEach(e -> {
				JVillagerTradeEntry entry = new JVillagerTradeEntry(mcreator, gui, entries, entryList);
				registerEntryUI(entry);
				entry.setEntry(e);
			});
	}
}
