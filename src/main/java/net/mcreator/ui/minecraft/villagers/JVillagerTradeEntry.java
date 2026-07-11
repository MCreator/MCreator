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
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.modgui.util.ComponentFromAnnotation;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JVillagerTradeEntry extends JSimpleListEntry<VillagerTrade.TradeEntry> {

	private boolean isWanderingTrader;

	private final MCItemHolder price1;
	private final MCItemHolder price2;
	private final MCItemHolder offer;

	private final JSpinner countPrice1 = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class, "countPrice1");
	private final JSpinner countPrice2 = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class, "countPrice2");
	private final JSpinner countOffer = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class, "countOffer");

	private final CardLayout tradeLevelLayout = new CardLayout();
	private final JPanel tradeLevelPanel = new JPanel(tradeLevelLayout);
	private final JComboBox<String> level = new JComboBox<>(
			new String[] { "Novice", "Apprentice", "Journeyman", "Expert", "Master" });
	private final JComboBox<String> wanderingTraderCategory = new JComboBox<>(
			new String[] { "Common", "Uncommon", "Buying" });

	private final JSpinner maxTrades = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class, "maxTrades");
	private final JSpinner xp = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class, "xp");
	private final JSpinner priceMultiplier = ComponentFromAnnotation.spinner(VillagerTrade.TradeEntry.class,
			"priceMultiplier");

	public JVillagerTradeEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JVillagerTradeEntry> entryList,
			boolean isWanderingTrader) {
		super(parent, entryList);
		this.isWanderingTrader = isWanderingTrader;

		price1 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems).requireValue(
				"elementgui.villager_trade.error_trade_needs_price", true);
		price2 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		offer = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems).requireValue(
				"elementgui.villager_trade.error_trade_needs_offer", true);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price1"),
				L10N.label("elementgui.villager_trade.price1")));
		line.add(price1);
		line.add(countPrice1);

		line.add(new JEmptyBox(15, 5));

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price2"),
				L10N.label("elementgui.villager_trade.price2")));
		line.add(price2);
		line.add(countPrice2);

		line.add(new JEmptyBox(15, 5));

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/sale"),
				L10N.label("elementgui.villager_trade.sale")));
		line.add(offer);
		line.add(countOffer);

		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line2.setOpaque(false);

		JPanel villagerLevel = PanelUtils.centerAndEastElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/level"),
						L10N.label("elementgui.villager_trade.level")), level);
		JPanel wanderingTraderLevel = PanelUtils.centerAndEastElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/wandering_trader_category"),
						L10N.label("elementgui.villager_trade.wandering_trader_category")), wanderingTraderCategory);
		tradeLevelPanel.add(villagerLevel, "villager");
		tradeLevelPanel.add(wanderingTraderLevel, "wandering_trader");
		tradeLevelPanel.setOpaque(false);

		line2.add(tradeLevelPanel);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/max_trades"),
				L10N.label("elementgui.villager_trade.max_trades")));
		line2.add(maxTrades);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/xp"),
				L10N.label("elementgui.villager_trade.xp")));
		line2.add(xp);
		line2.add(HelpUtils.wrapWithHelpButton(gui.withEntry("villagertrades/price_multiplier"),
				L10N.label("elementgui.villager_trade.price_multiplier")));
		line2.add(priceMultiplier);

		add(line2);

		setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Theme.current().getSecondAltBackgroundColor()));

		updateTradeLevelCard(isWanderingTrader);
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		price1.setEnabled(enabled);
		countPrice1.setEnabled(enabled);
		price2.setEnabled(enabled);
		countPrice2.setEnabled(enabled);
		offer.setEnabled(enabled);
		countOffer.setEnabled(enabled);
		level.setEnabled(enabled);
		wanderingTraderCategory.setEnabled(enabled);
		maxTrades.setEnabled(enabled);
		xp.setEnabled(enabled);
		priceMultiplier.setEnabled(enabled);
	}

	@Override public VillagerTrade.TradeEntry getEntry() {
		VillagerTrade.TradeEntry entry = new VillagerTrade.TradeEntry();
		entry.price1 = price1.getBlock();
		entry.countPrice1 = (int) countPrice1.getValue();
		entry.price2 = price2.getBlock();
		entry.countPrice2 = (int) countPrice2.getValue();
		entry.offer = offer.getBlock();
		entry.countOffer = (int) countOffer.getValue();
		entry.level = (isWanderingTrader ? wanderingTraderCategory.getSelectedIndex() : level.getSelectedIndex()) + 1;
		entry.maxTrades = (int) maxTrades.getValue();
		entry.xp = (int) xp.getValue();
		entry.priceMultiplier = (double) priceMultiplier.getValue();
		return entry;
	}

	@Override public void setEntry(VillagerTrade.TradeEntry e) {
		price1.setBlock(e.price1);
		countPrice1.setValue(e.countPrice1);
		price2.setBlock(e.price2);
		countPrice2.setValue(e.countPrice2);
		offer.setBlock(e.offer);
		countOffer.setValue(e.countOffer);
		level.setSelectedIndex(e.level - 1);
		wanderingTraderCategory.setSelectedIndex(Math.min(e.level - 1, 2));
		maxTrades.setValue(e.maxTrades);
		xp.setValue(e.xp);
		priceMultiplier.setValue(e.priceMultiplier);
	}

	public void updateTradeLevelCard(boolean isWanderingTrader) {
		this.isWanderingTrader = isWanderingTrader;
		tradeLevelLayout.show(tradeLevelPanel, isWanderingTrader ? "wandering_trader" : "villager");
		for (Component comp : tradeLevelPanel.getComponents()) {
			if (comp.isVisible()) {
				tradeLevelPanel.setPreferredSize(comp.getPreferredSize());
			}
		}
	}

	public AggregatedValidationResult getValidationResult() {
		return new AggregatedValidationResult(price1, offer);
	}
}
