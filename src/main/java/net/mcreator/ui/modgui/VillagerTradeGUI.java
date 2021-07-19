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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.VillagerTrade;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.villagers.JTradeList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

public class VillagerTradeGUI extends ModElementGUI<VillagerTrade> {

	private final JTradeList tradeList = new JTradeList(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	public VillagerTradeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane = new JPanel(new BorderLayout());
		JPanel mainEditor = new JPanel(new GridLayout());

		pane.setOpaque(false);
		mainEditor.setOpaque(false);

		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("villager_trade/trades"),
						L10N.label("elementgui.villager_trade.trades")), tradeList);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		mainEditor.add(component);
		pane.add(PanelUtils.centerInPanel(PanelUtils.join(FlowLayout.LEFT, mainEditor)));
		addPage(pane);

	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0) {
			return new AggregatedValidationResult(page1group);
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(VillagerTrade villagerTrade) {
		tradeList.setTrades(villagerTrade.tradeEntries);
	}

	@Override public VillagerTrade getElementFromGUI() {
		VillagerTrade villagerTrade = new VillagerTrade(modElement);
		villagerTrade.tradeEntries = tradeList.getTrades();
		return villagerTrade;
	}
}
