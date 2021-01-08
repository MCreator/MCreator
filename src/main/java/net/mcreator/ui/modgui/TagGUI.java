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
import net.mcreator.element.types.Tag;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.EntityListField;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.minecraft.ModElementListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.TagsNameValidator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class TagGUI extends ModElementGUI<Tag> {

	private final JComboBox<String> namespace = new JComboBox<>(new String[] { "forge", "minecraft", "mod" });
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Items", "Blocks", "Entities", "Functions" });

	private MCItemListField items;
	private MCItemListField blocks;

	private ModElementListField functions;
	private EntityListField entities;

	private final VComboBox<String> name = new VComboBox<>();

	public TagGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		items = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItems);
		blocks = new MCItemListField(mcreator, ElementUtil::loadBlocks);
		functions = new ModElementListField(mcreator, ModElementType.FUNCTION);
		entities = new EntityListField(mcreator);

		name.setValidator(new TagsNameValidator<>(name, false));
		name.enableRealtimeValidation();

		name.addItem("tag");
		name.addItem("category/tag");
		name.addItem("tick");
		name.addItem("load");
		name.addItem("logs");
		name.addItem("beacon_base_blocks");

		name.setEditable(true);
		name.setOpaque(false);

		namespace.setEditable(true);

		CardLayout valuesLayout = new CardLayout();
		JPanel valuesPan = new JPanel(valuesLayout);

		valuesPan.add(items, "Items");
		valuesPan.add(blocks, "Blocks");
		valuesPan.add(functions, "Functions");
		valuesPan.add(entities, "Entities");

		if (isEditingMode()) {
			type.setEnabled(false);
			name.setEnabled(false);
			namespace.setEnabled(false);
		} else {
			name.getEditor().setItem(RegistryNameFixer.fromCamelCase(modElement.getName()));

			name.addActionListener(e -> {
				if (Objects.equals(name.getSelectedItem(), "tick") || Objects.equals(name.getSelectedItem(), "load")) {
					namespace.setSelectedItem("minecraft");
					type.setSelectedItem("Functions");
				}
			});
		}

		JPanel main = new JPanel(new GridLayout(4, 2, 10, 2));
		main.setOpaque(false);

		main.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("tag/registry_name"), L10N.label("elementgui.tag.registry_name")));
		main.add(name);

		main.add(HelpUtils.wrapWithHelpButton(this.withEntry("tag/namespace"), L10N.label("elementgui.tag.namespace")));
		main.add(namespace);

		main.add(HelpUtils.wrapWithHelpButton(this.withEntry("tag/type"), L10N.label("elementgui.tag.type")));
		main.add(type);

		main.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("tag/tag_elements"), L10N.label("elementgui.tag.elements")));
		main.add(valuesPan);

		type.addActionListener(e -> valuesLayout.show(valuesPan, (String) type.getSelectedItem()));

		pane3.add(PanelUtils.totalCenterInPanel(main));

		addPage(pane3);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(name);
	}

	@Override public void openInEditingMode(Tag tag) {
		type.setSelectedItem(tag.type);
		namespace.getEditor().setItem(tag.namespace);
		name.getEditor().setItem(tag.name);

		items.setListElements(tag.items);
		blocks.setListElements(tag.blocks);

		functions.setListElements(tag.functions);

		entities.setListElements(tag.entities);
	}

	@Override public Tag getElementFromGUI() {
		Tag tag = new Tag(modElement);
		tag.namespace = namespace.getEditor().getItem().toString();
		tag.type = (String) type.getSelectedItem();

		tag.items = items.getListElements();
		tag.blocks = blocks.getListElements();
		tag.functions = functions.getListElements();
		tag.entities = entities.getListElements();

		tag.name = name.getEditor().getItem().toString();
		return tag;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-tag");
	}

}
