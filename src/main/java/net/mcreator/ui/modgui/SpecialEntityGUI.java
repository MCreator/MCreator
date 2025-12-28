/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.SpecialEntity;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.TabListField;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class SpecialEntityGUI extends ModElementGUI<SpecialEntity> {

	private final JComboBox<String> entityType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("Boat", "elementgui.special_entity.entity_type.boat"),
			Map.entry("ChestBoat", "elementgui.special_entity.entity_type.chest_boat")
			//@formatter:on
	);
	private final VTextField name = new VTextField(28).requireValue("elementgui.common.error_entity_needs_name")
			.enableRealtimeValidation();

	private final TextureComboBox entityTexture = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
			"elementgui.living_entity.error_entity_model_needs_texture");
	private final TextureSelectionButton itemTexture = new TextureSelectionButton(
			new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32).requireValue(
			"elementgui.special_entity.error_entity_needs_item_texture");

	private final TabListField creativeTabs = new TabListField(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	public SpecialEntityGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		entityTexture.setAddPNGExtension(false);
		itemTexture.setOpaque(false);

		JPanel properties = new JPanel(new GridLayout(5, 2, 5, 2));
		properties.setOpaque(false);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("special_entity/entity_type"),
				L10N.label("elementgui.special_entity.entity_type")));
		properties.add(entityType);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		properties.add(name);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("special_entity/entity_texture"),
				L10N.label("elementgui.special_entity.entity_texture")));
		properties.add(entityTexture);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("special_entity/item_texture"),
				L10N.label("elementgui.special_entity.item_texture")));
		properties.add(PanelUtils.centerInPanel(itemTexture));

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.common.creative_tabs")));
		properties.add(creativeTabs);

		creativeTabs.setPreferredSize(new java.awt.Dimension(0, 42));

		properties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		page1group.addValidationElement(entityTexture);
		page1group.addValidationElement(itemTexture);
		page1group.addValidationElement(name);

		addPage(PanelUtils.totalCenterInPanel(properties)).validate(page1group);

		if (!isEditingMode()) {
			creativeTabs.setListElements(List.of(new TabEntry(mcreator.getWorkspace(), "TOOLS")));

			String readableName = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableName.endsWith("Chest Boat") ?
					readableName.substring(0, readableName.length() - 10) + "Boat with Chest" : readableName);
			if (readableName.endsWith("Chest Boat"))
				entityType.setSelectedItem("ChestBoat");
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		entityTexture.reload();
	}

	@Override protected void openInEditingMode(SpecialEntity entity) {
		entityType.setSelectedItem(entity.entityType);
		name.setText(entity.name);
		entityTexture.setTextureFromTextureName(entity.entityTexture.getRawTextureName());
		itemTexture.setTexture(entity.itemTexture);
		creativeTabs.setListElements(entity.creativeTabs);
	}

	@Override public SpecialEntity getElementFromGUI() {
		SpecialEntity entity = new SpecialEntity(modElement);
		entity.entityType = (String) entityType.getSelectedItem();
		entity.name = name.getText();
		entity.entityTexture = new TextureHolder(mcreator.getWorkspace(), entityTexture.getTextureName());
		entity.itemTexture = itemTexture.getTextureHolder();
		entity.creativeTabs = creativeTabs.getListElements();
		return entity;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-special-entity");
	}
}
