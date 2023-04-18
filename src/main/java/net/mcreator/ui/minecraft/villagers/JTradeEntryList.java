/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.JEntriesList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JTradeEntryList extends JEntriesList {

	private final List<JVillagerTradeEntry> entryList = new ArrayList<>();

	private final IHelpContext gui;

	private final MCreator mcreator;

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JTradeEntryList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		this.mcreator = mcreator;
		this.gui = gui;

		entries.setOpaque(false);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		add.setText(L10N.t("elementgui.living_entity.add_trades"));
		add.addActionListener(e -> {
			JVillagerTradeEntry tradeEntry = new JVillagerTradeEntry(mcreator, gui, entries, entryList);
			registerEntryUI(tradeEntry);
		});
		bar.add(add);
		add("North", bar);

		JScrollPane sp = new JScrollPane(PanelUtils.pullElementUp(entries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add("Center", sp);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		add.setEnabled(enabled);
		entryList.forEach(e -> e.setEnabled(enabled));
	}

	public List<VillagerTrade.CustomTradeEntry.Entry> getTradeEntry() {
		return entryList.stream().map(JVillagerTradeEntry::getEntry).filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	public void setTradeEntries(List<VillagerTrade.CustomTradeEntry.Entry> tradeEntry) {
		entryList.clear(); // Avoid failing tests
		tradeEntry.forEach(e -> {
			JVillagerTradeEntry entry = new JVillagerTradeEntry(mcreator, gui, entries, entryList).setEntryEnabled(
					isEnabled());
			registerEntryUI(entry);
			entry.setEntry(e);
		});
	}
}
