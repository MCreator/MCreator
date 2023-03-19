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

package net.mcreator.ui.minecraft.loottable;

import net.mcreator.element.types.LootTable;
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

public class JLootTablePoolsList extends JEntriesList {

	private final List<JLootTablePool> poolList = new ArrayList<>();

	private final JPanel pools = new JPanel(new GridLayout(0, 1, 5, 5));

	public JLootTablePoolsList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		pools.setOpaque(false);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		add.setText(L10N.t("elementgui.loot_table.add_pool"));
		add.addActionListener(e -> {
			JLootTablePool pool = new JLootTablePool(mcreator, gui, pools, poolList);
			registerEntryUI(pool);
			pool.addInitialEntry();
		});
		bar.add(add);
		add("North", bar);

		JScrollPane sp = new JScrollPane(PanelUtils.pullElementUp(pools)) {
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

	public void reloadDataLists() {
		poolList.forEach(JLootTablePool::reloadDataLists);
	}

	public void addInitialPool() {
		JLootTablePool pool = new JLootTablePool(mcreator, gui, pools, poolList);
		registerEntryUI(pool);
		pool.addInitialEntry();
	}

	public List<LootTable.Pool> getPools() {
		return poolList.stream().map(JLootTablePool::getPool).filter(Objects::nonNull).toList();
	}

	public void setPools(List<LootTable.Pool> lootTablePools) {
		lootTablePools.forEach(e -> {
			JLootTablePool pool = new JLootTablePool(mcreator, gui, pools, poolList);
			registerEntryUI(pool);
			pool.setPool(e);
		});
	}
}
