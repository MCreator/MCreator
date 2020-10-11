/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

import net.mcreator.element.types.LootTable;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.datapack.loottable.JLootTablePool;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LootTableGUI extends ModElementGUI<LootTable> {

	private final JComboBox<String> namespace = new JComboBox<>(new String[] { "mod", "minecraft" });
	private final VComboBox<String> name = new VComboBox<>();

	private final JComboBox<String> type = new JComboBox<>(
			new String[] { "Generic", "Entity", "Block", "Chest", "Fishing", "Empty", "Advancement reward" });

	private final List<JLootTablePool> poolList = new ArrayList<>();

	private final JPanel pools = new JPanel(new GridLayout(0, 1, 5, 5));

	public LootTableGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI(false);
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		name.setValidator(new RegistryNameValidator(name, "Loot table").setValidChars(Arrays.asList('_', '/')));
		name.enableRealtimeValidation();

		name.addItem("blocks/stone");
		name.addItem("chests/abandoned_mineshaft");
		name.addItem("entities/chicken");
		name.addItem("gameplay/fishing");
		name.addItem("gameplay/hero_of_the_village/armorer_gift");

		name.setEditable(true);
		name.setOpaque(false);

		if (isEditingMode()) {
			name.setEnabled(false);
			namespace.setEnabled(false);
		} else {
			name.getEditor().setItem(RegistryNameFixer.fromCamelCase(modElement.getName()));
		}

		JPanel northPanel = new JPanel(new GridLayout(3, 2, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/registry_name"),
				new JLabel("Loot table registry name:")));
		northPanel.add(name);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/namespace"), new JLabel(
				"<html>Loot table namespace:<br><small>Use minecraft namespace to alter vanilla loot tables")));
		northPanel.add(namespace);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/type"), new JLabel("Loot table type:")));
		northPanel.add(type);

		JPanel maineditor = new JPanel(new BorderLayout());
		maineditor.setOpaque(false);

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		JButton addPool = new JButton("Add loot table pool");
		addPool.setIcon(UIRES.get("16px.add.gif"));
		bar.add(addPool);

		maineditor.add("North", bar);

		pools.setOpaque(false);

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
		maineditor.add("Center", sp);

		pane3.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), maineditor));
		addPage(pane3);

		addPool.addActionListener(e -> new JLootTablePool(mcreator, pools, poolList).addInitialEntry());

		// add first pool
		if (!isEditingMode()) {
			new JLootTablePool(mcreator, pools, poolList).addInitialEntry();
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		poolList.forEach(JLootTablePool::reloadDataLists);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(name);
	}

	@Override public void openInEditingMode(LootTable loottable) {
		type.setSelectedItem(loottable.type);

		namespace.setSelectedItem(loottable.namespace);
		name.getEditor().setItem(loottable.name);

		loottable.pools.forEach(e -> new JLootTablePool(mcreator, pools, poolList).setPool(e));
	}

	@Override public LootTable getElementFromGUI() {
		LootTable loottable = new LootTable(modElement);

		loottable.type = (String) type.getSelectedItem();

		loottable.namespace = (String) namespace.getSelectedItem();
		loottable.name = name.getEditor().getItem().toString();

		loottable.pools = poolList.stream().map(JLootTablePool::getPool).filter(Objects::nonNull)
				.collect(Collectors.toList());

		return loottable;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-loot-table");
	}

}
