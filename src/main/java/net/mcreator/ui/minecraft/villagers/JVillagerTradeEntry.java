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

import net.mcreator.element.types.VillagerTrade;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JVillagerTradeEntry extends JPanel {

	private final MCItemHolder price1;
	private final MCItemHolder price2;
	private final MCItemHolder offer;

	private final JSpinner countPrice1 = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
	private final JSpinner countPrice2 = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
	private final JSpinner countOffer = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));

	private final JComboBox<String> level = new JComboBox<>(
			new String[] { "Novice", "Apprentice", "Journeyman", "Expert", "Master" });

	private final JSpinner maxTrades = new JSpinner(new SpinnerNumberModel(10, 1, 72000, 1));
	private final JSpinner xp = new JSpinner(new SpinnerNumberModel(5, 0, 72000, 1));
	private final JSpinner priceMultiplier = new JSpinner(new SpinnerNumberModel(0.05, 0, 1, 0.01));

	public JVillagerTradeEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JVillagerTradeEntry> entryList) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		setBackground((Theme.current().getAltBackgroundColor()).darker());

		price1 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		price2 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		offer = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		priceMultiplier.setPreferredSize(new Dimension(92, 22));

		JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line1.setOpaque(false);

		line1.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price1"),
				L10N.label("elementgui.villager_trade.price1")));
		line1.add(price1);
		line1.add(L10N.label("elementgui.villager_trade.count_price_sale"));
		line1.add(countPrice1);

		line1.add(new JEmptyBox(15, 5));

		line1.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price2"),
				L10N.label("elementgui.villager_trade.price2")));
		line1.add(price2);
		line1.add(L10N.label("elementgui.villager_trade.count_price_sale"));
		line1.add(countPrice2);

		line1.add(new JEmptyBox(15, 5));

		line1.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/sale"),
				L10N.label("elementgui.villager_trade.sale")));
		line1.add(offer);
		line1.add(L10N.label("elementgui.villager_trade.count_price_sale"));
		line1.add(countOffer);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.villager_trade.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line2.setOpaque(false);

		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/level"),
				L10N.label("elementgui.villager_trade.level")));
		line2.add(level);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/max_trades"),
				L10N.label("elementgui.villager_trade.max_trades")));
		line2.add(maxTrades);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/xp"),
				L10N.label("elementgui.villager_trade.xp")));
		line2.add(xp);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price_multiplier"),
				L10N.label("elementgui.villager_trade.price_multiplier")));
		line2.add(priceMultiplier);

		add(PanelUtils.centerAndEastElement(line1, PanelUtils.join(remove)));
		add(line2);

		parent.revalidate();
		parent.repaint();
	}

	public VillagerTrade.CustomTradeEntry.Entry getEntry() {
		if (!price1.containsItem())
			return null;
		if (!offer.containsItem())
			return null;

		VillagerTrade.CustomTradeEntry.Entry entry = new VillagerTrade.CustomTradeEntry.Entry();
		entry.price1 = price1.getBlock();
		entry.countPrice1 = (int) countPrice1.getValue();
		entry.price2 = price2.getBlock();
		entry.countPrice2 = (int) countPrice2.getValue();
		entry.offer = offer.getBlock();
		entry.countOffer = (int) countOffer.getValue();
		entry.level = level.getSelectedIndex() + 1;
		entry.maxTrades = (int) maxTrades.getValue();
		entry.xp = (int) xp.getValue();
		entry.priceMultiplier = (double) priceMultiplier.getValue();
		return entry;
	}

	public void setEntry(VillagerTrade.CustomTradeEntry.Entry e) {
		price1.setBlock(e.price1);
		countPrice1.setValue(e.countPrice1);
		price2.setBlock(e.price2);
		countPrice2.setValue(e.countPrice2);
		offer.setBlock(e.offer);
		countOffer.setValue(e.countOffer);
		level.setSelectedIndex(e.level - 1);
		maxTrades.setValue(e.maxTrades);
		xp.setValue(e.xp);
		priceMultiplier.setValue(e.priceMultiplier);
	}
}
