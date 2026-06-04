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

import net.mcreator.element.parts.ProfessionEntry;
import net.mcreator.element.types.VillagerTrade;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.villagers.JVillagerTradeEntryList;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class VillagerTradeGUI extends ModElementGUI<VillagerTrade> {

	private JVillagerTradeEntryList trades;
	private final DataListComboBox villagerProfession = new DataListComboBox(mcreator,
			ElementUtil.loadAllVillagerProfessions(mcreator.getWorkspace()));

	public VillagerTradeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane = new JPanel(new BorderLayout(10, 10));
		pane.setOpaque(false);

		JPanel properties = new JPanel(new GridLayout(1, 2, 4, 2));
		properties.setOpaque(false);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagertrades/profession"),
				L10N.label("elementgui.villager_trade.profession")));
		properties.add(villagerProfession);

		villagerProfession.setPrototypeDisplayValue(new DataListEntry.Dummy("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));

		villagerProfession.addActionListener(_ -> trades.setWanderingTrader(
				"WANDERING_TRADER".equals(villagerProfession.getSelectedItem().getName())));

		trades = new JVillagerTradeEntryList(mcreator, this);

		pane.add("Center", PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, properties), trades));
		addPage(pane, false).lazyValidate(trades::getValidationResult);

		// Add first trade
		if (!isEditingMode()) {
			trades.setEntries(Collections.singletonList(new VillagerTrade.TradeEntry()));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(villagerProfession,
				ElementUtil.loadAllVillagerProfessions(mcreator.getWorkspace()));
	}

	@Override public void openInEditingMode(VillagerTrade villagerTrade) {
		villagerProfession.setSelectedItem(villagerTrade.villagerProfession);
		trades.setEntries(villagerTrade.trades);
	}

	@Override public VillagerTrade getElementFromGUI() {
		VillagerTrade villagerTrade = new VillagerTrade(modElement);
		villagerTrade.villagerProfession = new ProfessionEntry(mcreator.getWorkspace(),
				villagerProfession.getSelectedItem());
		villagerTrade.trades = trades.getEntries();
		return villagerTrade;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-villager-trades");
	}

}
