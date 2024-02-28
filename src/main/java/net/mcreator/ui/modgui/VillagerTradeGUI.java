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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.minecraft.villagers.JVillagerTradeProfessionsList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class VillagerTradeGUI extends ModElementGUI<VillagerTrade> {

	private JVillagerTradeProfessionsList villagerTradeProfessions;

	public VillagerTradeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane = new JPanel(new BorderLayout());
		pane.setOpaque(false);

		villagerTradeProfessions = new JVillagerTradeProfessionsList(mcreator, this);

		pane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		pane.add(villagerTradeProfessions);
		addPage(pane, false);

		// Add first pool
		if (!isEditingMode()) {
			villagerTradeProfessions.addInitialTrade();
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		villagerTradeProfessions.reloadDataLists();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(VillagerTrade villagerTrade) {
		villagerTradeProfessions.setEntries(villagerTrade.tradeEntries);
	}

	@Override public VillagerTrade getElementFromGUI() {
		VillagerTrade villagerTrade = new VillagerTrade(modElement);
		villagerTrade.tradeEntries = villagerTradeProfessions.getEntries();
		return villagerTrade;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-villager-trades");
	}

}
