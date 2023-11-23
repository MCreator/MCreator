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

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.LootTable;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.loottable.JLootTablePoolsList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

public class LootTableGUI extends ModElementGUI<LootTable> {

	private final JComboBox<String> namespace = new JComboBox<>(new String[] { "mod", "minecraft" });
	private final VComboBox<String> name = new VComboBox<>();

	private final JComboBox<String> type = new JComboBox<>(
			new String[] { "Block", "Entity", "Generic", "Chest", "Fishing", "Empty", "Advancement reward", "Gift",
					"Barter" });

	private JLootTablePoolsList lootTablePools;

	public LootTableGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		//@formatter:off
		name.setValidator(new UniqueNameValidator(
			L10N.t("modelement.loottable"),
			() -> namespace.getSelectedItem() + ":" + ((JTextField) name.getEditor().getEditorComponent()).getText(),
			() -> mcreator.getWorkspace().getModElements().stream()
				.filter(me -> me.getType() == ModElementType.LOOTTABLE)
				.map(ModElement::getGeneratableElement)
				.filter(Objects::nonNull)
				.map(ge -> ((LootTable) ge).namespace + ":" + ((LootTable) ge).name),
			new RegistryNameValidator(name, L10N.t("modelement.loottable")).setValidChars(Arrays.asList('_', '/'))
		).setIsPresentOnList(this::isEditingMode));
		//@formatter:on
		name.enableRealtimeValidation();
		name.addItem("blocks/stone");
		name.addItem("entities/chicken");
		name.addItem("gameplay/fishing");
		name.setEditable(true);

		if (isEditingMode()) {
			name.setEnabled(false);
			namespace.setEnabled(false);
		} else {
			name.getEditor().setItem("blocks/" + RegistryNameFixer.fromCamelCase(modElement.getName()));

			type.addActionListener(e -> {
				String currName = name.getEditor().getItem().toString();
				String currNameNoType = currName == null ? "" : currName.split("/")[currName.split("/").length - 1];
				if (type.getSelectedItem() != null)
					switch (type.getSelectedItem().toString()) {
					case "Block":
						name.getEditor().setItem("blocks/" + currNameNoType);
						break;
					case "Chest":
						name.getEditor().setItem("chests/" + currNameNoType);
						break;
					case "Entity":
					case "Gift":
					case "Barter":
					case "Advancement reward":
						name.getEditor().setItem("entities/" + currNameNoType);
						break;
					default:
						name.getEditor().setItem("gameplay/" + currNameNoType);
						break;
					}
			});
		}

		JPanel northPanel = new JPanel(new GridLayout(3, 2, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/registry_name"),
				L10N.label("elementgui.loot_table.registry_name")));
		northPanel.add(name);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/namespace"),
				L10N.label("elementgui.loot_table.namespace")));
		northPanel.add(namespace);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("loottable/type"),
				L10N.label("elementgui.loot_table.type")));
		northPanel.add(type);

		lootTablePools = new JLootTablePoolsList(mcreator, this);

		pane3.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), lootTablePools));
		addPage(pane3, false);

		// add first pool
		if (!isEditingMode())
			lootTablePools.addInitialPool();
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		lootTablePools.reloadDataLists();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(name);
	}

	@Override public void openInEditingMode(LootTable loottable) {
		type.setSelectedItem(loottable.type);

		namespace.setSelectedItem(loottable.namespace);
		name.getEditor().setItem(loottable.name);

		lootTablePools.setEntries(loottable.pools);
	}

	@Override public LootTable getElementFromGUI() {
		LootTable loottable = new LootTable(modElement);

		loottable.type = (String) type.getSelectedItem();

		loottable.namespace = (String) namespace.getSelectedItem();
		loottable.name = name.getEditor().getItem().toString();

		loottable.pools = lootTablePools.getEntries();

		return loottable;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-loot-table");
	}

}
