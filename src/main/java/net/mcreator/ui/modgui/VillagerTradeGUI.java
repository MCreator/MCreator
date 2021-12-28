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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.villagers.JVillagerTradeProfession;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VillagerTradeGUI extends ModElementGUI<VillagerTrade> {

	private final List<JVillagerTradeProfession> professionList = new ArrayList<>();

	private final JPanel professions = new JPanel(new GridLayout(0, 1, 5, 5));

	public VillagerTradeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane = new JPanel(new BorderLayout());
		pane.setOpaque(false);

		JPanel maineditor = new JPanel(new BorderLayout());
		maineditor.setOpaque(false);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		JButton addTrade = L10N.button("elementgui.villager_trade.add_profession_trades");
		addTrade.setIcon(UIRES.get("16px.add.gif"));
		bar.add(addTrade);

		maineditor.add("North", bar);

		professions.setOpaque(false);

		JScrollPane sp = new JScrollPane(PanelUtils.pullElementUp(professions)) {
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
		maineditor.add("Center", sp);

		pane.add(maineditor);
		addPage(pane);

		addTrade.addActionListener(
				e -> new JVillagerTradeProfession(mcreator, this, professions, professionList).addInitialEntry());

		// Add first pool
		if (!isEditingMode()) {
			new JVillagerTradeProfession(mcreator, this, professions, professionList).addInitialEntry();
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		professionList.forEach(JVillagerTradeProfession::reloadDataLists);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(VillagerTrade villagerTrade) {
		villagerTrade.tradeEntries
				.forEach(e -> new JVillagerTradeProfession(mcreator, this, professions, professionList).setTradeEntries(e));
	}

	@Override public VillagerTrade getElementFromGUI() {
		VillagerTrade villagerTrade = new VillagerTrade(modElement);
		villagerTrade.tradeEntries = professionList.stream().map(JVillagerTradeProfession::getTradeEntry)
				.filter(Objects::nonNull).collect(Collectors.toList());
		return villagerTrade;
	}
}
