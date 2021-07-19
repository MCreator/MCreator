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
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JTradeListEntry extends JPanel {

	private final JComboBox<String> villager = new JComboBox<>();
	private final MCItemHolder price1;
	private final MCItemHolder price2;
	private final MCItemHolder sale1;
	private final JSpinner level = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
	private final JSpinner countPrice1 = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
	private final JSpinner countPrice2 = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
	private final JSpinner countSale1 = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
	private final JSpinner maxTrades = new JSpinner(new SpinnerNumberModel(10, 1, 72000, 1));
	private final JSpinner xp = new JSpinner(new SpinnerNumberModel(5, 0, 72000, 1));
	private final JSpinner priceMultiplier = new JSpinner(new SpinnerNumberModel(0.05, 0, 1, 1));

	private final Workspace workspace;

	public JTradeListEntry(MCreator mcreator, JPanel parent, List<JTradeListEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));

		price1 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		price2 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		sale1 = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		this.workspace = mcreator.getWorkspace();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		ElementUtil.loadAllVIllagerProfessions(workspace).forEach(e -> villager.addItem(e.getName()));

		add(L10N.label("elementgui.villager_trade.profession"));
		add(villager);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/level"),
				L10N.label("elementgui.villager_trade.level")));
		add(level);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/price1"),
				L10N.label("elementgui.villager_trade.price1")));
		add(price1);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/count_price1"),
				L10N.label("elementgui.villager_trade.count_price_sale")));
		add(countPrice1);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/price2"),
				L10N.label("elementgui.villager_trade.price2")));
		add(price2);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/count_price2"),
				L10N.label("elementgui.villager_trade.count_price_sale")));
		add(countPrice2);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/sale1"),
				L10N.label("elementgui.villager_trade.sale1")));
		add(sale1);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/count_sale1"),
				L10N.label("elementgui.villager_trade.count_price_sale")));
		add(countSale1);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/max_trades"),
				L10N.label("elementgui.villager_trade.max_trades")));
		add(maxTrades);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/xp"),
				L10N.label("elementgui.villager_trade.xp")));
		add(xp);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("villager_trade/price_multiplier"),
				L10N.label("elementgui.villager_trade.price_multiplier")));
		add(priceMultiplier);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.villager_trade.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public VillagerTrade.CustomTradeEntry getEntry() {
		VillagerTrade.CustomTradeEntry entry = new VillagerTrade.CustomTradeEntry();
		entry.tradeEntry = new VillagerTradeEntry(workspace, (String) villager.getSelectedItem());
		entry.level = (int) level.getValue();
		entry.price1 = price1.getBlock();
		entry.countPrice1 = (int) countPrice1.getValue();
		entry.price2 = price2.getBlock();
		entry.countPrice2 = (int) countPrice2.getValue();
		entry.sale1 = sale1.getBlock();
		entry.countSale1 = (int) countSale1.getValue();
		entry.maxTrades = (int) maxTrades.getValue();
		entry.xp = (int) xp.getValue();
		entry.priceMultiplier = (double) priceMultiplier.getValue();
		return entry;
	}

	public void setEntry(VillagerTrade.CustomTradeEntry e) {
		villager.setSelectedItem(e.tradeEntry.getUnmappedValue());
		level.setValue(e.level);
		price1.setBlock(e.price1);
		countPrice1.setValue(e.countPrice1);
		price2.setBlock(e.price2);
		countPrice2.setValue(e.countPrice2);
		sale1.setBlock(e.sale1);
		countSale1.setValue(e.countSale1);
		maxTrades.setValue(e.maxTrades);
		xp.setValue(e.xp);
		priceMultiplier.setValue(e.priceMultiplier);
	}
}
