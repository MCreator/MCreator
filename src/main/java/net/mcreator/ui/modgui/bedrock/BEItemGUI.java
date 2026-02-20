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

package net.mcreator.ui.modgui.bedrock;

import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.modgui.ModElementGUI;
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
import java.util.Map;

public class BEItemGUI extends ModElementGUI<BEItem> {

	private TextureSelectionButton texture;
	private final JCheckBox hasGlint = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox handEquipped = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(20).requireValue("elementgui.item.error_item_needs_name")
			.enableRealtimeValidation();
	private final TranslatedComboBox rarity = new TranslatedComboBox(
			//@formatter:off
			Map.entry("common", "elementgui.common.rarity_common"),
			Map.entry("uncommon", "elementgui.common.rarity_uncommon"),
			Map.entry("rare", "elementgui.common.rarity_rare"),
			Map.entry("epic", "elementgui.common.rarity_epic")
			//@formatter:on
	);
	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 1, 64, 1));
	private final JCheckBox enableCreativeTab = new JCheckBox();
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator,
			ElementUtil.loadAllTabs(mcreator.getWorkspace()));
	private final JSpinner maxDurability = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 1));
	private final JSpinner useDuration = new JSpinner(new SpinnerNumberModel(1.6, 0, 128000, 0.1));
	private final JSpinner movementModifier = new JSpinner(new SpinnerNumberModel(0.35, 0, 1, 0.05));
	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private final JCheckBox isHiddenInCommands = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox allowOffHand = L10N.checkbox("elementgui.common.enable");
	private final JSpinner fuelDuration = new JSpinner(new SpinnerNumberModel(0, 0, 107374180, 0.05));
	private final JCheckBox shouldDespawn = L10N.checkbox("elementgui.common.enable");
	private final MCItemHolder blockToPlace = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
	private final MCItemListField blockPlaceableOn = new MCItemListField(mcreator, ElementUtil::loadBlocks);
	private final SingleSpawnableEntitySelector entityToPlace = new SingleSpawnableEntitySelector(mcreator);
	private final MCItemListField entityDispensableOn = new MCItemListField(mcreator, ElementUtil::loadBlocks);
	private final MCItemListField entityPlaceableOn = new MCItemListField(mcreator, ElementUtil::loadBlocks);

	private final JCheckBox isFood = L10N.checkbox("elementgui.common.enable");
	private final JSpinner foodNutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner foodSaturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));
	private final JCheckBox foodCanAlwaysEat = L10N.checkbox("elementgui.common.enable");
	private final MCItemHolder usingConvertsTo = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
	private final TranslatedComboBox animation = new TranslatedComboBox(
			//@formatter:off
			Map.entry("none", "elementgui.item.item_animation_none"),
			Map.entry("eat", "elementgui.item.item_animation_eat"),
			Map.entry("block", "elementgui.item.item_animation_block"),
			Map.entry("bow", "elementgui.item.item_animation_bow"),
			Map.entry("crossbow", "elementgui.item.item_animation_crossbow"),
			Map.entry("drink", "elementgui.item.item_animation_drink"),
			Map.entry("spear", "elementgui.item.item_animation_spear"),
			Map.entry("brush", "elementgui.item.item_animation_brush"),
			Map.entry("spyglass", "elementgui.item.item_animation_spyglass"),
			Map.entry("camera", "elementgui.item.item_animation_camera")
			//@formatter:on
	);

	private final ValidationGroup page1group = new ValidationGroup();

	public BEItemGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel propertiesPanel = new JPanel(new BorderLayout(10, 10));
		propertiesPanel.setOpaque(false);
		JPanel foodPanel = new JPanel(new BorderLayout(10, 10));
		foodPanel.setOpaque(false);
		JPanel advancedPanel = new JPanel(new BorderLayout(10, 10));
		advancedPanel.setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM)).requireValue(
				"elementgui.item.error_item_needs_texture");
		texture.setOpaque(false);

		JPanel basicProperties = new JPanel(new GridLayout(8, 2, 30, 2));
		basicProperties.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		basicProperties.add(name);
		ComponentUtils.deriveFont(name, 16);

		basicProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		basicProperties.add(rarity);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/has_glint"),
				L10N.label("elementgui.beitem.has_glint")));
		hasGlint.setOpaque(false);
		basicProperties.add(hasGlint);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/hand_equipped"),
				L10N.label("elementgui.beitem.hand_equipped")));
		handEquipped.setOpaque(false);
		basicProperties.add(handEquipped);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/creative_tab"),
				L10N.label("elementgui.beitem.creative_tab")));
		basicProperties.add(PanelUtils.westAndCenterElement(enableCreativeTab, creativeTab));
		enableCreativeTab.addActionListener(e -> updateCreativeTab());
		enableCreativeTab.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		basicProperties.add(stackSize);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				L10N.label("elementgui.item.damage_vs_entity")));
		basicProperties.add(PanelUtils.westAndCenterElement(enableMeleeDamage, damageVsEntity));
		enableMeleeDamage.addActionListener(e -> updateMeleeDamage());
		enableMeleeDamage.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.item.number_of_uses")));
		basicProperties.add(maxDurability);

		propertiesPanel.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(
				PanelUtils.totalCenterInPanel(
						ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture"))), basicProperties,
				35, 35)));

		JPanel foodProperties = new JPanel(new GridLayout(8, 2, 65, 2));
		foodProperties.setOpaque(false);

		foodProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_food"), L10N.label("elementgui.item.is_food")));
		foodProperties.add(isFood);
		isFood.addActionListener(e -> updateFoodPanel());
		isFood.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/nutritional_value"),
				L10N.label("elementgui.item.nutritional_value")));
		foodProperties.add(foodNutritionalValue);
		foodNutritionalValue.setPreferredSize(new Dimension(-1, -1));
		foodNutritionalValue.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/saturation"),
				L10N.label("elementgui.item.saturation")));
		foodProperties.add(foodSaturation);
		foodSaturation.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/always_edible"),
				L10N.label("elementgui.item.is_edible")));
		foodProperties.add(foodCanAlwaysEat);
		foodCanAlwaysEat.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/result_item"),
				L10N.label("elementgui.item.eating_result")));
		foodProperties.add(PanelUtils.centerInPanel(usingConvertsTo));

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/animation"),
				L10N.label("elementgui.item.item_animation")));
		foodProperties.add(animation);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/use_duration"),
				L10N.label("elementgui.beitem.use_duration")));
		foodProperties.add(useDuration);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/movement_modifier"),
				L10N.label("elementgui.beitem.movement_modifier")));
		foodProperties.add(movementModifier);

		foodPanel.add("Center", PanelUtils.totalCenterInPanel(foodProperties));

		JPanel advancedProperties = new JPanel(new GridLayout(4, 2, 65, 2));
		advancedProperties.setOpaque(false);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/is_hidden_commands"),
				L10N.label("elementgui.beitem.is_hidden_commands")));
		isHiddenInCommands.setOpaque(false);
		advancedProperties.add(isHiddenInCommands);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/allow_off_hand"),
				L10N.label("elementgui.beitem.allow_off_hand")));
		allowOffHand.setOpaque(false);
		advancedProperties.add(allowOffHand);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/fuel_duration"),
				L10N.label("elementgui.beitem.fuel_duration")));
		advancedProperties.add(fuelDuration);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/should_despawn"),
				L10N.label("elementgui.beitem.should_despawn")));
		shouldDespawn.setOpaque(false);
		advancedProperties.add(shouldDespawn);

		JPanel blockPlacerProps = new JPanel(new GridLayout(2, 2, 65, 2));
		blockPlacerProps.setOpaque(false);

		blockPlacerProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/block_to_place"),
				L10N.label("elementgui.beitem.block_to_place")));
		blockToPlace.setOpaque(false);
		blockPlacerProps.add(PanelUtils.centerInPanel(blockToPlace));
		blockToPlace.addBlockSelectedListener(e -> updateBlockUsableOnList());

		blockPlacerProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/block_placeable_on"),
				L10N.label("elementgui.beitem.placeable_on")));
		blockPlaceableOn.setOpaque(false);
		blockPlacerProps.add(blockPlaceableOn);

		ComponentUtils.makeSection(blockPlacerProps, L10N.t("elementgui.beitem.block_placer_properties"));

		JPanel entityPlacerProps = new JPanel(new GridLayout(3, 2, 65, 2));
		entityPlacerProps.setOpaque(false);

		entityPlacerProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/entity_to_place"),
				L10N.label("elementgui.beitem.entity_to_place")));
		entityToPlace.setOpaque(false);
		entityPlacerProps.add(entityToPlace);

		entityPlacerProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/entity_placeable_on"),
				L10N.label("elementgui.beitem.entity_placeable_on")));
		entityPlaceableOn.setOpaque(false);
		entityPlacerProps.add(entityPlaceableOn);

		entityPlacerProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/entity_dispensable_on"),
				L10N.label("elementgui.beitem.entity_dispensable_on")));
		entityDispensableOn.setOpaque(false);
		entityPlacerProps.add(entityDispensableOn);

		ComponentUtils.makeSection(entityPlacerProps, L10N.t("elementgui.beitem.entity_placer_properties"));

		advancedPanel.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.column(advancedProperties, blockPlacerProps, entityPlacerProps)));

		page1group.addValidationElement(name);
		page1group.addValidationElement(texture);

		addPage(L10N.t("elementgui.common.page_properties"), propertiesPanel).validate(page1group);
		addPage(L10N.t("elementgui.item.food_properties"), foodPanel);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), advancedPanel);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
			shouldDespawn.setSelected(true);
			animation.setSelectedItem("eat");
			enableCreativeTab.setSelected(true);
			creativeTab.setSelectedItem("MATERIALS");
		}

		updateCreativeTab();
		updateMeleeDamage();
		updateFoodPanel();
		updateBlockUsableOnList();
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
	}

	private void updateFoodPanel() {
		if (isFood.isSelected()) {
			foodNutritionalValue.setEnabled(true);
			foodSaturation.setEnabled(true);
			foodCanAlwaysEat.setEnabled(true);
			useDuration.setEnabled(true);
			usingConvertsTo.setEnabled(true);
			animation.setEnabled(true);
			movementModifier.setEnabled(true);
		} else {
			foodNutritionalValue.setEnabled(false);
			foodSaturation.setEnabled(false);
			foodCanAlwaysEat.setEnabled(false);
			useDuration.setEnabled(false);
			usingConvertsTo.setEnabled(false);
			animation.setEnabled(false);
			movementModifier.setEnabled(false);
		}
	}

	private void updateMeleeDamage() {
		damageVsEntity.setEnabled(enableMeleeDamage.isSelected());
	}

	private void updateCreativeTab() {
		creativeTab.setEnabled(enableCreativeTab.isSelected());
	}

	private void updateBlockUsableOnList() {
		blockPlaceableOn.setEnabled(blockToPlace.containsItem());
	}

	@Override protected void openInEditingMode(BEItem item) {
		texture.setTexture(item.texture);
		name.setText(item.name);
		stackSize.setValue(item.stackSize);
		useDuration.setValue(item.useDuration);
		maxDurability.setValue(item.maxDurability);
		hasGlint.setSelected(item.hasGlint);
		damageVsEntity.setValue(item.damageVsEntity);
		enableMeleeDamage.setSelected(item.enableMeleeDamage);
		isFood.setSelected(item.isFood);
		foodCanAlwaysEat.setSelected(item.foodCanAlwaysEat);
		foodNutritionalValue.setValue(item.foodNutritionalValue);
		foodSaturation.setValue(item.foodSaturation);
		handEquipped.setSelected(item.handEquipped);
		rarity.setSelectedItem(item.rarity);
		enableCreativeTab.setSelected(item.enableCreativeTab);
		creativeTab.setSelectedItem(item.creativeTab);
		isHiddenInCommands.setSelected(item.isHiddenInCommands);
		movementModifier.setValue(item.movementModifier);
		allowOffHand.setSelected(item.allowOffHand);
		fuelDuration.setValue(item.fuelDuration);
		shouldDespawn.setSelected(item.shouldDespawn);
		usingConvertsTo.setBlock(item.usingConvertsTo);
		animation.setSelectedItem(item.animation);
		blockToPlace.setBlock(item.blockToPlace);
		blockPlaceableOn.setListElements(item.blockPlaceableOn);
		entityToPlace.setEntry(item.entityToPlace);
		entityDispensableOn.setListElements(item.entityDispensableOn);
		entityPlaceableOn.setListElements(item.entityPlaceableOn);
		updateFoodPanel();
		updateMeleeDamage();
		updateCreativeTab();
		updateBlockUsableOnList();
	}

	@Override public BEItem getElementFromGUI() {
		BEItem item = new BEItem(modElement);
		item.texture = texture.getTextureHolder();
		item.name = name.getText();
		item.stackSize = (int) stackSize.getValue();
		item.useDuration = (double) useDuration.getValue();
		item.maxDurability = (int) maxDurability.getValue();
		item.hasGlint = hasGlint.isSelected();
		item.damageVsEntity = (double) damageVsEntity.getValue();
		item.enableMeleeDamage = enableMeleeDamage.isSelected();
		item.isFood = isFood.isSelected();
		item.foodNutritionalValue = (int) foodNutritionalValue.getValue();
		item.foodSaturation = (double) foodSaturation.getValue();
		item.foodCanAlwaysEat = foodCanAlwaysEat.isSelected();
		item.handEquipped = handEquipped.isSelected();
		item.rarity = rarity.getSelectedItem();
		item.enableCreativeTab = enableCreativeTab.isSelected();
		item.creativeTab = creativeTab.getSelectedItem().toString();
		item.isHiddenInCommands = isHiddenInCommands.isSelected();
		item.movementModifier = (double) movementModifier.getValue();
		item.allowOffHand = allowOffHand.isSelected();
		item.fuelDuration = (double) fuelDuration.getValue();
		item.shouldDespawn = shouldDespawn.isSelected();
		item.usingConvertsTo = usingConvertsTo.getBlock();
		item.animation = animation.getSelectedItem();
		item.blockToPlace = blockToPlace.getBlock();
		item.blockPlaceableOn = blockPlaceableOn.getListElements();
		item.entityToPlace = entityToPlace.getEntry();
		item.entityDispensableOn = entityDispensableOn.getListElements();
		item.entityPlaceableOn = entityPlaceableOn.getListElements();

		return item;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-item");
	}
}
