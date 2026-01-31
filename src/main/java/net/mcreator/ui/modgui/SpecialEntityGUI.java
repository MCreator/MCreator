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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.SpecialEntity;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TabListField;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class SpecialEntityGUI extends ModElementGUI<SpecialEntity> {

	private final JComboBox<String> entityType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("Boat", "elementgui.special_entity.entity_type.boat"),
			Map.entry("ChestBoat", "elementgui.special_entity.entity_type.chest_boat"),
			Map.entry("Raft", "elementgui.special_entity.entity_type.raft"),
			Map.entry("ChestRaft", "elementgui.special_entity.entity_type.chest_raft")
			//@formatter:on
	);
	private final VTextField name = new VTextField(28).requireValue("elementgui.common.error_entity_needs_name")
			.enableRealtimeValidation();

	private final TextureComboBox entityTexture = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
			"elementgui.living_entity.error_entity_model_needs_texture");
	private final TextureSelectionButton itemTexture = new TextureSelectionButton(
			new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32).requireValue(
			"elementgui.special_entity.error_entity_needs_item_texture");

	private final TranslatedComboBox rarity = new TranslatedComboBox(
			//@formatter:off
			Map.entry("COMMON", "elementgui.common.rarity_common"),
			Map.entry("UNCOMMON", "elementgui.common.rarity_uncommon"),
			Map.entry("RARE", "elementgui.common.rarity_rare"),
			Map.entry("EPIC", "elementgui.common.rarity_epic")
			//@formatter:on
	);
	private final TabListField creativeTabs = new TabListField(mcreator);

	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onPlayerCollidesWith;

	private final ValidationGroup page1group = new ValidationGroup();

	public SpecialEntityGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		entityTexture.setAddPNGExtension(false);
		itemTexture.setOpaque(false);

		onTickUpdate = new ProcedureSelector(this.withEntry("entity/on_tick_update"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_tick_update"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onPlayerCollidesWith = new ProcedureSelector(this.withEntry("entity/when_player_collides"), mcreator,
				L10N.t("elementgui.living_entity.event_player_collides_with"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity"));

		JPanel properties = new JPanel(new GridLayout(6, 2, 5, 2));
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

		properties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		properties.add(rarity);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.common.creative_tabs")));
		properties.add(creativeTabs);

		creativeTabs.setPreferredSize(new java.awt.Dimension(0, 42));

		page1group.addValidationElement(entityTexture);
		page1group.addValidationElement(itemTexture);
		page1group.addValidationElement(name);

		JPanel procedureTriggers = new JPanel(new GridLayout(1, 2, 5, 5));
		procedureTriggers.setOpaque(false);
		procedureTriggers.add(onTickUpdate);
		procedureTriggers.add(onPlayerCollidesWith);

		addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(properties)).validate(
				page1group);
		addPage(L10N.t("elementgui.common.page_triggers"), PanelUtils.totalCenterInPanel(procedureTriggers));

		if (!isEditingMode()) {
			creativeTabs.setListElements(List.of(new TabEntry(mcreator.getWorkspace(), "TOOLS")));

			String readableName = StringUtils.machineToReadableName(modElement.getName());
			if (readableName.endsWith("Chest Boat")) {
				name.setText(readableName.substring(0, readableName.length() - 10) + "Boat with Chest");
			} else if (readableName.endsWith("Chest Raft")) {
				name.setText(readableName.substring(0, readableName.length() - 10) + "Raft with Chest");
			} else {
				name.setText(readableName);
			}
			// Automatically set the "correct" entity type in some cases
			if (readableName.endsWith("Chest Boat")) {
				entityType.setSelectedItem("ChestBoat");
			} else if (readableName.endsWith("Chest Raft")) {
				entityType.setSelectedItem("ChestRaft");
			} else if (readableName.endsWith("Raft")) {
				entityType.setSelectedItem("Raft");
			}
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		entityTexture.reload();

		AbstractProcedureSelector.ReloadContext context = AbstractProcedureSelector.ReloadContext.create(
				mcreator.getWorkspace());

		onTickUpdate.refreshListKeepSelected(context);
		onPlayerCollidesWith.refreshListKeepSelected(context);
	}

	@Override protected void openInEditingMode(SpecialEntity entity) {
		entityType.setSelectedItem(entity.entityType);
		name.setText(entity.name);
		entityTexture.setTextureFromTextureName(entity.entityTexture.getRawTextureName());
		itemTexture.setTexture(entity.itemTexture);
		rarity.setSelectedItem(entity.rarity);
		creativeTabs.setListElements(entity.creativeTabs);
		onTickUpdate.setSelectedProcedure(entity.onTickUpdate);
		onPlayerCollidesWith.setSelectedProcedure(entity.onPlayerCollidesWith);
	}

	@Override public SpecialEntity getElementFromGUI() {
		SpecialEntity entity = new SpecialEntity(modElement);
		entity.entityType = (String) entityType.getSelectedItem();
		entity.name = name.getText();
		entity.entityTexture = new TextureHolder(mcreator.getWorkspace(), entityTexture.getTextureName());
		entity.itemTexture = itemTexture.getTextureHolder();
		entity.rarity = rarity.getSelectedItem();
		entity.creativeTabs = creativeTabs.getListElements();
		entity.onTickUpdate = onTickUpdate.getSelectedProcedure();
		entity.onPlayerCollidesWith = onPlayerCollidesWith.getSelectedProcedure();
		return entity;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-special-entity");
	}
}
