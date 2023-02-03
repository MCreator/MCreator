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
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
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
import java.util.Objects;
import java.util.stream.Collectors;

public class ToolGUI extends ModElementGUI<Tool> {

	private TextureHolder texture;

	private final JSpinner harvestLevel = new JSpinner(new SpinnerNumberModel(1, 0, 128000, 1));
	private final JSpinner efficiency = new JSpinner(new SpinnerNumberModel(4, 0, 128000, 0.5));
	private final JSpinner enchantability = new JSpinner(new SpinnerNumberModel(2, 0, 128000, 1));
	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(4, 0, 128000, 0.1));
	private final JSpinner attackSpeed = new JSpinner(new SpinnerNumberModel(1, 0, 100, 0.1));
	private final JSpinner usageCount = new JSpinner(new SpinnerNumberModel(100, 0, 128000, 1));

	private final VTextField name = new VTextField(28);

	private final JComboBox<String> toolType = new JComboBox<>(
			new String[] { "Pickaxe", "Axe", "Sword", "Spade", "Hoe", "Shield", "Shears", "Fishing rod", "Special",
					"MultiTool" });

	private final JCheckBox immuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox stayInGridWhenCrafting = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox damageOnCrafting = L10N.checkbox("elementgui.common.enable");

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model mirrored = new Model.BuiltInModel("Mirrored");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(new Model[] { normal });
	private final SearchableComboBox<Model> blockingModel = new SearchableComboBox<>(new Model[] { mirrored });

	private final JCheckBox hasGlow = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector glowCondition;

	private final JTextField specialInfo = new JTextField(20);

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onBlockDestroyedWithTool;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
	private ProcedureSelector onStoppedUsing;
	private ProcedureSelector onEntitySwing;

	private MCItemListField blocksAffected;

	private MCItemListField repairItems;

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

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
		onStoppedUsing = new ProcedureSelector(this.withEntry("item/when_stopped_using"), mcreator,
				L10N.t("elementgui.tool.event_stopped_using"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/time:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.tool.event_swings"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		glowCondition = new ProcedureSelector(this.withEntry("item/condition_glow"), mcreator, "Make item glow",
				ProcedureSelector.Side.CLIENT, true, VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeInline();

		blocksAffected = new MCItemListField(mcreator, ElementUtil::loadBlocks);

		repairItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItems);

		toolType.setRenderer(new ItemTexturesComboBoxRenderer());

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		JPanel destal = new JPanel();
		destal.setOpaque(false);

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		texture.setOpaque(false);

		hasGlow.setOpaque(false);
		hasGlow.setSelected(false);

		immuneToFire.setOpaque(false);
		stayInGridWhenCrafting.setOpaque(false);
		damageOnCrafting.setOpaque(false);

		destal.add(ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.tool.texture")));

		JPanel rent = new JPanel();
		rent.setLayout(new BoxLayout(rent, BoxLayout.PAGE_AXIS));

		rent.setOpaque(false);
		rent.add(PanelUtils.join(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")),
				PanelUtils.join(renderType)));

		ComponentUtils.deriveFont(specialInfo, 16);

		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());
		renderType.addActionListener((e) -> {
			if (renderType.getSelectedItem() != null)
				updateFields();
		});

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.tool.tool_3d_model"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(PanelUtils.join(destal, rent), PanelUtils.gridElements(1, 2,
						HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"),
								L10N.label("elementgui.tool.tool_special_information")), specialInfo))));
		JComponent glow = PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(this.withEntry("item/glowing_effect"),
						L10N.label("elementgui.tool.glowing_effect")), hasGlow, glowCondition);

		JComponent visualBottom = PanelUtils.centerAndSouthElement(PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"),
						L10N.label("elementgui.tool.tooltip_tip")), specialInfo), glow, 10, 10);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(PanelUtils.join(destal, rent), visualBottom)));

		JPanel selp = new JPanel(new GridLayout(15, 2, 10, 2));
		selp.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		harvestLevel.setOpaque(false);
		efficiency.setOpaque(false);

		hasGlow.addActionListener(e -> updateGlowElements());

		blockingModel.setFont(blockingModel.getFont().deriveFont(16.0f));
		blockingModel.setRenderer(new ModelComboBoxRenderer());
		blockingModel.setEnabled(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/type"), L10N.label("elementgui.tool.type")));
		selp.add(toolType);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		selp.add(creativeTab);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/harvest_level"),
				L10N.label("elementgui.tool.harvest_level")));
		selp.add(harvestLevel);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/efficiency"),
				L10N.label("elementgui.tool.efficiency")));
		selp.add(efficiency);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/enchantability"),
				L10N.label("elementgui.common.enchantability")));
		selp.add(enchantability);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/attack_speed"),
				L10N.label("elementgui.tool.attack_speed")));
		selp.add(attackSpeed);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				L10N.label("elementgui.tool.damage_vs_entity")));
		selp.add(damageVsEntity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.tool.usage_count")));
		selp.add(usageCount);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/shield_blocking_model"),
				L10N.label("elementgui.tool.shield_blocking_model")));
		selp.add(blockingModel);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/repair_items"),
				L10N.label("elementgui.common.repair_items")));
		selp.add(repairItems);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tool/blocks_affected"),
				L10N.label("elementgui.tool.blocks_affected")));
		selp.add(blocksAffected);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.tool.is_immune_to_fire")));
		selp.add(immuneToFire);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item"),
				L10N.label("elementgui.tool.stays_in_grid_when_crafting")));
		selp.add(stayInGridWhenCrafting);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item_damage"),
				L10N.label("elementgui.tool.damaged_on_crafting")));
		selp.add(damageOnCrafting);

		blocksAffected.setEnabled(false);

		toolType.addActionListener(event -> updateFields());

		pane4.setOpaque(false);

		pane4.add("Center", PanelUtils.totalCenterInPanel(selp));

		pane3.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(3, 3, 10, 10));
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onBlockDestroyedWithTool);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onStoppedUsing);
		events.add(onEntitySwing);
		events.setOpaque(false);
		pane3.add(PanelUtils.totalCenterInPanel(events));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.tool.needs_a_name")));
		name.enableRealtimeValidation();

		addPage(L10N.t("elementgui.common.page_visual"), pane2);
		addPage(L10N.t("elementgui.common.page_properties"), pane4);
		addPage(L10N.t("elementgui.common.page_triggers"), pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void updateFields() {
		if (toolType.getSelectedItem() != null) {
			harvestLevel.setEnabled(true);
			efficiency.setEnabled(true);
			damageVsEntity.setEnabled(true);
			attackSpeed.setEnabled(true);
			blocksAffected.setEnabled(true);
			repairItems.setEnabled(true);
			blockingModel.setEnabled(true);

			if (!toolType.getSelectedItem().equals("Shield") || (renderType.getSelectedItem() != null ? renderType.getSelectedItem().getType() == Model.Type.BUILTIN : true))
				blockingModel.setEnabled(false);

			if (toolType.getSelectedItem().equals("Special")) {
				harvestLevel.setEnabled(false);
				repairItems.setEnabled(false);
			} else if (toolType.getSelectedItem().equals("Fishing rod") || toolType.getSelectedItem().equals("Shield")) {
				harvestLevel.setEnabled(false);
				efficiency.setEnabled(false);
				damageVsEntity.setEnabled(false);
				attackSpeed.setEnabled(false);
				blocksAffected.setEnabled(false);
			} else if (toolType.getSelectedItem().equals("Shears")) {
				harvestLevel.setEnabled(false);
				damageVsEntity.setEnabled(false);
				attackSpeed.setEnabled(false);
				blocksAffected.setEnabled(false);
				repairItems.setEnabled(false);
			} else {
				blocksAffected.setEnabled(false);
			}
		}
	}

	private void updateGlowElements() {
		glowCondition.setEnabled(hasGlow.isSelected());
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onRightClickedInAir.refreshListKeepSelected();
		onCrafted.refreshListKeepSelected();
		onRightClickedOnBlock.refreshListKeepSelected();
		onBlockDestroyedWithTool.refreshListKeepSelected();
		onEntityHitWith.refreshListKeepSelected();
		onItemInInventoryTick.refreshListKeepSelected();
		onItemInUseTick.refreshListKeepSelected();
		onStoppedUsing.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();
		glowCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("TOOLS"));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(blockingModel, ListUtils.merge(Collections.singletonList(mirrored),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 1)
			return new AggregatedValidationResult(name);
		else if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Tool tool) {
		creativeTab.setSelectedItem(tool.creativeTab);
		name.setText(tool.name);
		texture.setTextureFromTextureName(tool.texture);
		toolType.setSelectedItem(tool.toolType);
		harvestLevel.setValue(tool.harvestLevel);
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
		onStoppedUsing.setSelectedProcedure(tool.onStoppedUsing);
		onEntitySwing.setSelectedProcedure(tool.onEntitySwing);
		hasGlow.setSelected(tool.hasGlow);
		glowCondition.setSelectedProcedure(tool.glowCondition);
		repairItems.setListElements(tool.repairItems);
		specialInfo.setText(
				tool.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		stayInGridWhenCrafting.setSelected(tool.stayInGridWhenCrafting);
		immuneToFire.setSelected(tool.immuneToFire);
		damageOnCrafting.setSelected(tool.damageOnCrafting);

		blocksAffected.setListElements(tool.blocksAffected);

		updateGlowElements();
		updateFields();

		if (toolType.getSelectedItem() != null)
			blocksAffected.setEnabled(toolType.getSelectedItem().equals("Special"));

		Model model = tool.getItemModel();
		Model blockingModel = tool.getBlockingModel();
		if (model != null)
			this.renderType.setSelectedItem(model);
		if (blockingModel != null)
			this.blockingModel.setSelectedItem(tool.getBlockingModel());
	}

	@Override public Tool getElementFromGUI() {
		Tool tool = new Tool(modElement);
		tool.name = name.getText();
		tool.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		tool.toolType = (String) toolType.getSelectedItem();
		tool.harvestLevel = (int) harvestLevel.getValue();
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
		tool.onStoppedUsing = onStoppedUsing.getSelectedProcedure();
		tool.onEntitySwing = onEntitySwing.getSelectedProcedure();
		tool.hasGlow = hasGlow.isSelected();
		tool.glowCondition = glowCondition.getSelectedProcedure();
		tool.repairItems = repairItems.getListElements();
		tool.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());

		tool.stayInGridWhenCrafting = stayInGridWhenCrafting.isSelected();
		tool.immuneToFire = immuneToFire.isSelected();
		tool.damageOnCrafting = damageOnCrafting.isSelected();

		tool.texture = texture.getID();

		Model.Type modelType = (Objects.requireNonNull(renderType.getSelectedItem())).getType();
		Model.Type blockingModelType = (Objects.requireNonNull(blockingModel.getSelectedItem().getType()));
		tool.renderType = 0;
		tool.blockingRenderType = 0;
		if (modelType == Model.Type.JSON)
			tool.renderType = 1;
		else if (modelType == Model.Type.OBJ)
			tool.renderType = 2;
		if (blockingModelType == Model.Type.JSON)
			tool.blockingRenderType = 1;
		else if (blockingModelType == Model.Type.OBJ)
			tool.blockingRenderType = 2;
		tool.customModelName = (Objects.requireNonNull(renderType.getSelectedItem())).getReadableName();
		tool.blockingModelName = (Objects.requireNonNull(blockingModel.getSelectedItem().getReadableName()));

		return tool;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-tool");
	}

}
