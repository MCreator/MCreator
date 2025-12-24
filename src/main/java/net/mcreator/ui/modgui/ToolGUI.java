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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Tool;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.AdaptiveGridLayout;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.minecraft.TabListField;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ToolGUI extends ModElementGUI<Tool> {

	private TextureSelectionButton texture;
	private TextureSelectionButton guiTexture;

	private final JSpinner efficiency = new JSpinner(new SpinnerNumberModel(4, 0, 128000, 0.5));
	private final JSpinner enchantability = new JSpinner(new SpinnerNumberModel(2, 1, 128000, 1));
	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(4, 0, 128000, 0.1));
	private final JSpinner attackSpeed = new JSpinner(new SpinnerNumberModel(1, 0, 100, 0.1));
	private final JSpinner usageCount = new JSpinner(new SpinnerNumberModel(100, 0, 128000, 1));

	private final JComboBox<String> blockDropsTier = new JComboBox<>(
			new String[] { "WOOD", "STONE", "IRON", "DIAMOND", "GOLD", "NETHERITE" });

	private ProcedureSelector additionalDropCondition;

	private final VTextField name = new VTextField(26).requireValue("elementgui.tool.needs_a_name")
			.enableRealtimeValidation();

	private final JComboBox<String> toolType = new JComboBox<>(
			new String[] { "Pickaxe", "Axe", "Sword", "Spade", "Hoe", "Shield", "Shears", "Fishing rod", "Special",
					"MultiTool" });

	private final JCheckBox immuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox stayInGridWhenCrafting = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox damageOnCrafting = L10N.checkbox("elementgui.common.enable");

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model normalBlocking = new Model.BuiltInModel("Normal blocking");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(new Model[] { normal });
	private final SearchableComboBox<Model> blockingModel = new SearchableComboBox<>(new Model[] { normalBlocking });

	private LogicProcedureSelector glowCondition;

	private StringListProcedureSelector specialInformation;

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onBlockDestroyedWithTool;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
	private ProcedureSelector onEntitySwing;

	private MCItemListField blocksAffected;

	private MCItemListField repairItems;

	private final TabListField creativeTabs = new TabListField(mcreator);

	private JComponent dropTierPanel;
	private JComponent attackDamagePanel;
	private JComponent attackSpeedPanel;
	private JComponent efficiencyPanel;
	private JComponent blockingModelPanel;
	private JComponent blocksAffectedPanel;

	private final ValidationGroup page1group = new ValidationGroup();

	public ToolGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onRightClickedInAir = new ProcedureSelector(this.withEntry("item/when_right_clicked"), mcreator,
				L10N.t("elementgui.common.event_right_clicked_air"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onCrafted = new ProcedureSelector(this.withEntry("item/on_crafted"), mcreator,
				L10N.t("elementgui.common.event_on_crafted"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClickedOnBlock = new ProcedureSelector(this.withEntry("item/when_right_clicked_block"), mcreator,
				L10N.t("elementgui.common.event_right_clicked_block"), VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/direction:direction/blockstate:blockstate")).makeReturnValueOptional();
		onBlockDestroyedWithTool = new ProcedureSelector(this.withEntry("tool/when_block_destroyed"), mcreator,
				L10N.t("elementgui.tool.event_block_destroyed"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/blockstate:blockstate"));
		onEntityHitWith = new ProcedureSelector(this.withEntry("item/when_entity_hit"), mcreator,
				L10N.t("elementgui.tool.event_entity_hit_with"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack"));
		onItemInInventoryTick = new ProcedureSelector(this.withEntry("item/inventory_tick"), mcreator,
				L10N.t("elementgui.tool.event_in_inventory_tick"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onItemInUseTick = new ProcedureSelector(this.withEntry("item/hand_tick"), mcreator,
				L10N.t("elementgui.tool.event_in_hand_tick"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.tool.event_swings"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		specialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));
		glowCondition = new LogicProcedureSelector(this.withEntry("item/glowing_effect"), mcreator,
				L10N.t("elementgui.item.glowing_effect"), ProcedureSelector.Side.CLIENT,
				L10N.checkbox("elementgui.common.enable"), 160,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		additionalDropCondition = new ProcedureSelector(this.withEntry("tool/event_additional_drop_condition"),
				mcreator, L10N.t("elementgui.tool.event_additional_drop_condition"),
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("itemstack:itemstack/blockstate:blockstate")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		blocksAffected = new MCItemListField(mcreator, ElementUtil::loadBlocksAndTags, false, true);

		repairItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItemsAndTags, false, true);

		toolType.setRenderer(new ItemTexturesComboBoxRenderer());

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM)).requireValue(
				"elementgui.item.error_item_needs_texture");
		texture.setOpaque(false);

		guiTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32);
		guiTexture.setOpaque(false);

		immuneToFire.setOpaque(false);
		stayInGridWhenCrafting.setOpaque(false);
		stayInGridWhenCrafting.addActionListener(e -> updateCraftingSettings());
		damageOnCrafting.setOpaque(false);

		JPanel rent = new JPanel(new GridLayout(-1, 2, 2, 2));
		rent.setOpaque(false);

		rent.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		rent.add(renderType);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/gui_texture"),
				L10N.label("elementgui.common.item_gui_texture")));
		rent.add(PanelUtils.centerInPanel(guiTexture));

		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.tool.tool_3d_model"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JComponent visualBottom = PanelUtils.centerAndSouthElement(glowCondition, specialInformation, 0, 5);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(
				PanelUtils.westAndCenterElement(
						ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.tool.texture")), rent), visualBottom,
				0, 5)));

		JPanel itemProperties = new JPanel(new GridLayout(-1, 2, 2, 2));
		itemProperties.setOpaque(false);

		itemProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties_general"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		itemProperties.add(name);

		ComponentUtils.deriveFont(name, 16);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.common.creative_tabs")));
		itemProperties.add(creativeTabs);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/enchantability"),
				L10N.label("elementgui.common.enchantability")));
		itemProperties.add(enchantability);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.tool.usage_count")));
		itemProperties.add(usageCount);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/repair_items"),
				L10N.label("elementgui.common.repair_items")));
		itemProperties.add(repairItems);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.tool.is_immune_to_fire")));
		itemProperties.add(immuneToFire);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item"),
				L10N.label("elementgui.item.container_item")));
		itemProperties.add(stayInGridWhenCrafting);

		itemProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item_damage"),
				L10N.label("elementgui.item.container_item_damage")));
		itemProperties.add(damageOnCrafting);

		JPanel toolProperties = new JPanel(new AdaptiveGridLayout(-1, 1, 0, 2));
		toolProperties.setOpaque(false);

		toolProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.tool.tool_properties"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		blockDropsTier.setRenderer(new ItemTexturesComboBoxRenderer());

		blockingModel.setRenderer(new ModelComboBoxRenderer());

		toolProperties.add(PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/type"), L10N.label("elementgui.tool.type")),
				toolType));

		toolProperties.add(dropTierPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/blocks_drop_tier"),
						L10N.label("elementgui.tool.blocks_drop_tier")), blockDropsTier));

		toolProperties.add(additionalDropCondition);

		toolProperties.add(efficiencyPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/efficiency"),
						L10N.label("elementgui.tool.efficiency")), efficiency));

		toolProperties.add(attackDamagePanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
						L10N.label("elementgui.tool.damage_vs_entity")), damageVsEntity));

		toolProperties.add(attackSpeedPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/attack_speed"),
						L10N.label("elementgui.tool.attack_speed")), attackSpeed));

		toolProperties.add(blockingModelPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/shield_blocking_model"),
						L10N.label("elementgui.tool.shield_blocking_model")), blockingModel));

		toolProperties.add(blocksAffectedPanel = PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("tool/blocks_affected"),
						L10N.label("elementgui.tool.blocks_affected")), blocksAffected));

		usageCount.addChangeListener(e -> updateCraftingSettings());

		toolType.addActionListener(event -> updateFields());

		pane4.setOpaque(false);

		pane4.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(itemProperties, toolProperties)));

		pane3.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(3, 3, 5, 5));
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onBlockDestroyedWithTool);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onEntitySwing);
		events.setOpaque(false);
		pane3.add(PanelUtils.totalCenterInPanel(events));

		page1group.addValidationElement(texture);

		addPage(L10N.t("elementgui.common.page_visual"), pane2).validate(page1group);
		addPage(L10N.t("elementgui.common.page_properties"), pane4).validate(name);
		addPage(L10N.t("elementgui.common.page_triggers"), pane3);

		if (!isEditingMode()) {
			creativeTabs.setListElements(List.of(new TabEntry(mcreator.getWorkspace(), "TOOLS")));

			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}

		updateCraftingSettings();
		updateFields();
	}

	private void updateCraftingSettings() {
		damageOnCrafting.setEnabled(stayInGridWhenCrafting.isSelected() && ((int) usageCount.getValue() > 0));
	}

	private void updateFields() {
		String selectedToolType = (String) toolType.getSelectedItem();
		if (selectedToolType != null) {
			blockingModelPanel.setVisible(selectedToolType.equals("Shield"));
			blocksAffectedPanel.setVisible(selectedToolType.equals("Special"));

			dropTierPanel.setVisible(true);
			additionalDropCondition.setVisible(true);
			efficiencyPanel.setVisible(true);
			attackDamagePanel.setVisible(true);
			attackSpeedPanel.setVisible(true);

			switch ((String) toolType.getSelectedItem()) {
			case "Special" -> dropTierPanel.setVisible(false);
			case "Fishing rod", "Shield" -> {
				dropTierPanel.setVisible(false);
				additionalDropCondition.setVisible(false);
				efficiencyPanel.setVisible(false);
				attackDamagePanel.setVisible(false);
				attackSpeedPanel.setVisible(false);
			}
			case "Shears" -> {
				dropTierPanel.setVisible(false);
				additionalDropCondition.setVisible(false);
				attackDamagePanel.setVisible(false);
				attackSpeedPanel.setVisible(false);
			}
			}
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		AbstractProcedureSelector.ReloadContext context = AbstractProcedureSelector.ReloadContext.create(
				mcreator.getWorkspace());

		onRightClickedInAir.refreshListKeepSelected(context);
		onCrafted.refreshListKeepSelected(context);
		onRightClickedOnBlock.refreshListKeepSelected(context);
		onBlockDestroyedWithTool.refreshListKeepSelected(context);
		onEntityHitWith.refreshListKeepSelected(context);
		onItemInInventoryTick.refreshListKeepSelected(context);
		onItemInUseTick.refreshListKeepSelected(context);
		onEntitySwing.refreshListKeepSelected(context);
		glowCondition.refreshListKeepSelected(context);
		specialInformation.refreshListKeepSelected(context);

		additionalDropCondition.refreshListKeepSelected(context);

		List<Model> itemModels = Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
				.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
				.collect(Collectors.toList());

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal), itemModels));
		ComboBoxUtil.updateComboBoxContents(blockingModel,
				ListUtils.merge(Collections.singletonList(normalBlocking), itemModels));
	}

	@Override public void openInEditingMode(Tool tool) {
		creativeTabs.setListElements(tool.creativeTabs);
		name.setText(tool.name);
		texture.setTexture(tool.texture);
		guiTexture.setTexture(tool.guiTexture);
		toolType.setSelectedItem(tool.toolType);
		blockDropsTier.setSelectedItem(tool.blockDropsTier);
		additionalDropCondition.setSelectedProcedure(tool.additionalDropCondition);
		efficiency.setValue(tool.efficiency);
		enchantability.setValue(tool.enchantability);
		attackSpeed.setValue(tool.attackSpeed);
		damageVsEntity.setValue(tool.damageVsEntity);
		usageCount.setValue(tool.usageCount);
		onRightClickedInAir.setSelectedProcedure(tool.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(tool.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(tool.onCrafted);
		onBlockDestroyedWithTool.setSelectedProcedure(tool.onBlockDestroyedWithTool);
		onEntityHitWith.setSelectedProcedure(tool.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(tool.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(tool.onItemInUseTick);
		onEntitySwing.setSelectedProcedure(tool.onEntitySwing);
		glowCondition.setSelectedProcedure(tool.glowCondition);
		specialInformation.setSelectedProcedure(tool.specialInformation);
		repairItems.setListElements(tool.repairItems);
		stayInGridWhenCrafting.setSelected(tool.stayInGridWhenCrafting);
		immuneToFire.setSelected(tool.immuneToFire);
		damageOnCrafting.setSelected(tool.damageOnCrafting);

		blocksAffected.setListElements(tool.blocksAffected);

		updateCraftingSettings();
		updateFields();

		Model model = tool.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);

		Model modelBlocking = tool.getBlockingModel();
		if (modelBlocking != null)
			blockingModel.setSelectedItem(modelBlocking);
	}

	@Override public Tool getElementFromGUI() {
		Tool tool = new Tool(modElement);
		tool.name = name.getText();
		tool.creativeTabs = creativeTabs.getListElements();
		tool.toolType = (String) Objects.requireNonNull(toolType.getSelectedItem());
		tool.blockDropsTier = (String) blockDropsTier.getSelectedItem();
		tool.additionalDropCondition = additionalDropCondition.getSelectedProcedure();
		tool.efficiency = (double) efficiency.getValue();
		tool.enchantability = (int) enchantability.getValue();
		tool.attackSpeed = (double) attackSpeed.getValue();
		tool.damageVsEntity = (double) damageVsEntity.getValue();
		tool.usageCount = (int) usageCount.getValue();
		tool.blocksAffected = blocksAffected.getListElements();
		tool.onRightClickedInAir = onRightClickedInAir.getSelectedProcedure();
		tool.onRightClickedOnBlock = onRightClickedOnBlock.getSelectedProcedure();
		tool.onCrafted = onCrafted.getSelectedProcedure();
		tool.onBlockDestroyedWithTool = onBlockDestroyedWithTool.getSelectedProcedure();
		tool.onEntityHitWith = onEntityHitWith.getSelectedProcedure();
		tool.onItemInInventoryTick = onItemInInventoryTick.getSelectedProcedure();
		tool.onItemInUseTick = onItemInUseTick.getSelectedProcedure();
		tool.onEntitySwing = onEntitySwing.getSelectedProcedure();
		tool.glowCondition = glowCondition.getSelectedProcedure();
		tool.specialInformation = specialInformation.getSelectedProcedure();
		tool.repairItems = repairItems.getListElements();

		tool.stayInGridWhenCrafting = stayInGridWhenCrafting.isSelected();
		tool.immuneToFire = immuneToFire.isSelected();
		tool.damageOnCrafting = damageOnCrafting.isSelected();

		tool.texture = texture.getTextureHolder();
		tool.guiTexture = guiTexture.getTextureHolder();

		Model.Type modelType = (Objects.requireNonNull(renderType.getSelectedItem())).getType();
		tool.renderType = 0;
		if (modelType == Model.Type.JSON)
			tool.renderType = 1;
		else if (modelType == Model.Type.OBJ)
			tool.renderType = 2;
		tool.customModelName = (Objects.requireNonNull(renderType.getSelectedItem())).getReadableName();

		Model.Type blockingModelType = Objects.requireNonNull(blockingModel.getSelectedItem()).getType();
		tool.blockingRenderType = 0;
		if (blockingModelType == Model.Type.JSON)
			tool.blockingRenderType = 1;
		else if (blockingModelType == Model.Type.OBJ)
			tool.blockingRenderType = 2;
		tool.blockingModelName = Objects.requireNonNull(blockingModel.getSelectedItem()).getReadableName();

		return tool;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-tool");
	}

}
