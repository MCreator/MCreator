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
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.ProjectileEntry;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.Item;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.minecraft.states.item.JItemPropertiesStatesList;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
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
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemGUI extends ModElementGUI<Item> {

	private TextureHolder texture;

	private StringListProcedureSelector specialInformation;

	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 0, 64, 1));
	private final VTextField name = new VTextField(20);
	private final JComboBox<String> rarity = new JComboBox<>(new String[] { "COMMON", "UNCOMMON", "RARE", "EPIC" });

	private final MCItemHolder recipeRemainder = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final JSpinner enchantability = new JSpinner(new SpinnerNumberModel(0, -100, 128000, 1));
	private final JSpinner useDuration = new JSpinner(new SpinnerNumberModel(0, -100, 128000, 1));
	private final JSpinner toolType = new JSpinner(new SpinnerNumberModel(1.0, -100.0, 128000.0, 0.1));
	private final JSpinner damageCount = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 1));

	private final JCheckBox immuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox destroyAnyBlock = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox stayInGridWhenCrafting = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox damageOnCrafting = L10N.checkbox("elementgui.common.enable");

	private LogicProcedureSelector glowCondition;

	private final JCheckBox enableRanged = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox shootConstantly = L10N.checkbox("elementgui.common.enable");

	private ProcedureSelector onRangedItemUsed;
	private ProcedureSelector rangedUseCondition;

	private final DataListComboBox projectile = new DataListComboBox(mcreator);

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private static final Model normal = new Model.BuiltInModel("Normal");
	private static final Model tool = new Model.BuiltInModel("Tool");
	private static final Model rangedItem = new Model.BuiltInModel("Ranged item");
	public static final Model[] builtinitemmodels = new Model[] { normal, tool, rangedItem };
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(builtinitemmodels);
	private JItemPropertiesStatesList customProperties;

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
	private ProcedureSelector onStoppedUsing;
	private ProcedureSelector onEntitySwing;
	private ProcedureSelector onDroppedByPlayer;
	private ProcedureSelector onFinishUsingItem;

	private final ValidationGroup page1group = new ValidationGroup();

	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private final SearchableComboBox<String> guiBoundTo = new SearchableComboBox<>();
	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));

	// Food parameters
	private final JCheckBox isFood = L10N.checkbox("elementgui.common.enable");
	private final JSpinner nutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner saturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));
	private final JCheckBox isMeat = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isAlwaysEdible = L10N.checkbox("elementgui.common.enable");
	private final JComboBox<String> animation = new JComboBox<>(
			new String[] { "none", "eat", "block", "bow", "crossbow", "drink", "spear" });
	private final MCItemHolder eatResultItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	public ItemGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
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
		onEntityHitWith = new ProcedureSelector(this.withEntry("item/when_entity_hit"), mcreator,
				L10N.t("elementgui.item.event_entity_hit"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack"));
		onItemInInventoryTick = new ProcedureSelector(this.withEntry("item/inventory_tick"), mcreator,
				L10N.t("elementgui.item.event_inventory_tick"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onItemInUseTick = new ProcedureSelector(this.withEntry("item/hand_tick"), mcreator,
				L10N.t("elementgui.item.event_hand_tick"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onStoppedUsing = new ProcedureSelector(this.withEntry("item/when_stopped_using"), mcreator,
				L10N.t("elementgui.item.event_stopped_using"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/time:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.item.event_entity_swings"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onDroppedByPlayer = new ProcedureSelector(this.withEntry("item/on_dropped"), mcreator,
				L10N.t("elementgui.item.event_on_dropped"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onFinishUsingItem = new ProcedureSelector(this.withEntry("item/when_stopped_using"), mcreator,
				L10N.t("elementgui.item.player_useitem_finish"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRangedItemUsed = new ProcedureSelector(this.withEntry("item/when_used"), mcreator,
				L10N.t("elementgui.item.event_on_use"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeInline();
		specialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));
		glowCondition = new LogicProcedureSelector(this.withEntry("item/glowing_effect"), mcreator,
				L10N.t("elementgui.item.glowing_effect"), ProcedureSelector.Side.CLIENT,
				L10N.checkbox("elementgui.common.enable"), 160,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		rangedUseCondition = new ProcedureSelector(this.withEntry("item/ranged_use_condition"), mcreator,
				L10N.t("elementgui.item.can_use_ranged"), VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeInline();

		customProperties = new JItemPropertiesStatesList(mcreator, this);
		customProperties.setPreferredSize(new Dimension(0, 0)); // prevent resizing beyond the editor tab

		guiBoundTo.addActionListener(e -> {
			if (!isEditingMode()) {
				String selected = guiBoundTo.getSelectedItem();
				if (selected != null) {
					ModElement element = mcreator.getWorkspace().getModElementByName(selected);
					if (element != null) {
						GeneratableElement generatableElement = element.getGeneratableElement();
						if (generatableElement instanceof GUI) {
							inventorySize.setValue(((GUI) generatableElement).getMaxSlotID() + 1);
						}
					}
				}
			}
		});

		useDuration.addChangeListener(change -> onStoppedUsing.setEnabled((int) useDuration.getValue() > 0));

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel cipp = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel foodProperties = new JPanel(new BorderLayout(10, 10));
		JPanel advancedProperties = new JPanel(new BorderLayout(10, 10));
		JPanel rangedPanel = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		texture.setOpaque(false);

		JPanel destal2 = new JPanel(new BorderLayout(0, 5));
		destal2.setOpaque(false);

		destal2.add("Center", PanelUtils.northAndCenterElement(glowCondition, specialInformation, 0, 5));

		ComponentUtils.deriveFont(renderType, 16);

		JPanel rent = new JPanel();
		rent.setLayout(new BoxLayout(rent, BoxLayout.PAGE_AXIS));

		rent.setOpaque(false);
		rent.add(PanelUtils.join(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")),
				PanelUtils.join(renderType)));

		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.item_3d_model"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		destal2.add("North", PanelUtils.totalCenterInPanel(PanelUtils.westAndCenterElement(
				ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture")), rent)));

		JPanel sbbp2 = new JPanel(new BorderLayout());
		sbbp2.setOpaque(false);

		sbbp2.add("West", destal2);

		pane2.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(sbbp2)));

		pane2.setOpaque(false);

		cipp.setOpaque(false);
		cipp.add("Center", customProperties);

		JPanel subpane2 = new JPanel(new GridLayout(15, 2, 2, 2));

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		subpane2.add(name);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		subpane2.add(rarity);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		subpane2.add(creativeTab);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		subpane2.add(stackSize);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/enchantability"),
				L10N.label("elementgui.common.enchantability")));
		subpane2.add(enchantability);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/destroy_speed"),
				L10N.label("elementgui.item.destroy_speed")));
		subpane2.add(toolType);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				L10N.label("elementgui.item.damage_vs_entity")));
		subpane2.add(PanelUtils.westAndCenterElement(enableMeleeDamage, damageVsEntity));

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.item.number_of_uses")));
		subpane2.add(damageCount);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.item.is_immune_to_fire")));
		subpane2.add(immuneToFire);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/can_destroy_any_block"),
				L10N.label("elementgui.item.can_destroy_any_block")));
		subpane2.add(destroyAnyBlock);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item"),
				L10N.label("elementgui.item.container_item")));
		subpane2.add(stayInGridWhenCrafting);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/container_item_damage"),
				L10N.label("elementgui.item.container_item_damage")));
		subpane2.add(damageOnCrafting);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/recipe_remainder"),
				L10N.label("elementgui.item.recipe_remainder")));
		subpane2.add(PanelUtils.centerInPanel(recipeRemainder));

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/animation"),
				L10N.label("elementgui.item.item_animation")));
		subpane2.add(animation);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/use_duration"),
				L10N.label("elementgui.item.use_duration")));
		subpane2.add(useDuration);

		enchantability.setOpaque(false);
		useDuration.setOpaque(false);
		toolType.setOpaque(false);
		damageCount.setOpaque(false);
		immuneToFire.setOpaque(false);
		destroyAnyBlock.setOpaque(false);
		stayInGridWhenCrafting.setOpaque(false);
		damageOnCrafting.setOpaque(false);

		subpane2.setOpaque(false);

		pane3.setOpaque(false);
		pane3.add("Center", PanelUtils.totalCenterInPanel(subpane2));

		JPanel foodSubpane = new JPanel(new GridLayout(6, 2, 2, 2));
		foodSubpane.setOpaque(false);

		isFood.setOpaque(false);
		isMeat.setOpaque(false);
		isAlwaysEdible.setOpaque(false);
		nutritionalValue.setOpaque(false);
		saturation.setOpaque(false);

		isFood.addActionListener(e -> {
			updateFoodPanel();
			if (!isEditingMode()) {
				animation.setSelectedItem("eat");
				useDuration.setValue(32);
			}
		});

		updateFoodPanel();

		foodSubpane.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_food"), L10N.label("elementgui.item.is_food")));
		foodSubpane.add(isFood);

		foodSubpane.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/nutritional_value"),
				L10N.label("elementgui.item.nutritional_value")));
		foodSubpane.add(nutritionalValue);

		foodSubpane.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/saturation"),
				L10N.label("elementgui.item.saturation")));
		foodSubpane.add(saturation);

		foodSubpane.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/result_item"),
				L10N.label("elementgui.item.eating_result")));
		foodSubpane.add(PanelUtils.centerInPanel(eatResultItem));

		foodSubpane.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_meat"), L10N.label("elementgui.item.is_meat")));
		foodSubpane.add(isMeat);

		foodSubpane.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/always_edible"),
				L10N.label("elementgui.item.is_edible")));
		foodSubpane.add(isAlwaysEdible);

		foodProperties.add("Center", PanelUtils.totalCenterInPanel(foodSubpane));
		foodProperties.setOpaque(false);

		advancedProperties.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(4, 3, 5, 5));
		events.setOpaque(false);
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onStoppedUsing);
		events.add(onEntitySwing);
		events.add(onDroppedByPlayer);
		events.add(onFinishUsingItem);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);

		JPanel inventoryProperties = new JPanel(new GridLayout(3, 2, 35, 2));
		inventoryProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.page_inventory"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));
		inventoryProperties.setOpaque(false);

		inventoryProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/bind_gui"), L10N.label("elementgui.item.bind_gui")));
		inventoryProperties.add(guiBoundTo);

		inventoryProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/inventory_size"),
				L10N.label("elementgui.item.inventory_size")));
		inventoryProperties.add(inventorySize);

		inventoryProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/inventory_stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		inventoryProperties.add(inventoryStackSize);

		updateRangedPanel();

		JPanel rangedProperties = new JPanel(new GridLayout(3, 2, 2, 2));
		rangedProperties.setOpaque(false);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/enable_ranged_item"),
				L10N.label("elementgui.item.enable_ranged_item")));
		enableRanged.setOpaque(false);
		enableRanged.addActionListener(e -> updateRangedPanel());
		rangedProperties.add(enableRanged);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/projectile"),
				L10N.label("elementgui.item.projectile")));
		rangedProperties.add(projectile);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/shoot_constantly"),
				L10N.label("elementgui.item.shoot_constantly")));
		shootConstantly.setOpaque(false);
		rangedProperties.add(shootConstantly);

		JPanel rangedTriggers = new JPanel(new GridLayout(2, 1, 2, 2));
		rangedTriggers.setOpaque(false);
		rangedTriggers.add(rangedUseCondition);
		rangedTriggers.add(onRangedItemUsed);

		rangedPanel.setOpaque(false);
		rangedPanel.add("Center", PanelUtils.centerAndSouthElement(rangedProperties, rangedTriggers));
		rangedPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.ranged_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		advancedProperties.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.centerAndEastElement(PanelUtils.pullElementUp(inventoryProperties), rangedPanel, 10, 10)));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.item.error_item_needs_name")));
		name.enableRealtimeValidation();

		addPage(L10N.t("elementgui.common.page_visual"), pane2);
		addPage(L10N.t("elementgui.item.page_item_states"), cipp, false);
		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.item.food_properties"), foodProperties);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), advancedProperties);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}

		updateFoodPanel();
	}

	private void updateFoodPanel() {
		if (isFood.isSelected()) {
			nutritionalValue.setEnabled(true);
			saturation.setEnabled(true);
			isMeat.setEnabled(true);
			isAlwaysEdible.setEnabled(true);
			eatResultItem.setEnabled(true);
		} else {
			nutritionalValue.setEnabled(false);
			saturation.setEnabled(false);
			isMeat.setEnabled(false);
			isAlwaysEdible.setEnabled(false);
			eatResultItem.setEnabled(false);
		}
	}

	private void updateRangedPanel() {
		if (enableRanged.isSelected()) {
			shootConstantly.setEnabled(true);
			projectile.setEnabled(true);
			onRangedItemUsed.setEnabled(true);
			rangedUseCondition.setEnabled(true);
			if (!isEditingMode()) {
				if ((int) useDuration.getValue() == 0)
					useDuration.setValue(72000);
				if (renderType.getSelectedItem() == normal)
					renderType.setSelectedItem(rangedItem);
				if ("none".equals(animation.getSelectedItem()))
					animation.setSelectedItem("bow");
			}
		} else {
			shootConstantly.setEnabled(false);
			projectile.setEnabled(false);
			onRangedItemUsed.setEnabled(false);
			rangedUseCondition.setEnabled(false);
			if (!isEditingMode()) {
				if ((int) useDuration.getValue() == 72000)
					useDuration.setValue(0);
				if (renderType.getSelectedItem() == rangedItem)
					renderType.setSelectedItem(normal);
				animation.setSelectedItem("none");
			}
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onRightClickedInAir.refreshListKeepSelected();
		onCrafted.refreshListKeepSelected();
		onRightClickedOnBlock.refreshListKeepSelected();
		onEntityHitWith.refreshListKeepSelected();
		onItemInInventoryTick.refreshListKeepSelected();
		onItemInUseTick.refreshListKeepSelected();
		onStoppedUsing.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();
		onDroppedByPlayer.refreshListKeepSelected();
		onFinishUsingItem.refreshListKeepSelected();
		specialInformation.refreshListKeepSelected();
		glowCondition.refreshListKeepSelected();
		onRangedItemUsed.refreshListKeepSelected();
		rangedUseCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(projectile, ElementUtil.loadArrowProjectiles(mcreator.getWorkspace()));

		customProperties.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(ItemGUI.builtinitemmodels),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(guiBoundTo, ListUtils.merge(Collections.singleton("<NONE>"),
				mcreator.getWorkspace().getModElements().stream().filter(var -> var.getType() == ModElementType.GUI)
						.map(ModElement::getName).collect(Collectors.toList())), "<NONE>");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		else if (page == 1)
			return customProperties.getValidationResult();
		else if (page == 2)
			return new AggregatedValidationResult(name);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Item item) {
		name.setText(item.name);
		rarity.setSelectedItem(item.rarity);
		texture.setTextureFromTextureName(item.texture);
		onRightClickedInAir.setSelectedProcedure(item.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(item.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(item.onCrafted);
		onEntityHitWith.setSelectedProcedure(item.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(item.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(item.onItemInUseTick);
		onStoppedUsing.setSelectedProcedure(item.onStoppedUsing);
		onEntitySwing.setSelectedProcedure(item.onEntitySwing);
		onDroppedByPlayer.setSelectedProcedure(item.onDroppedByPlayer);
		creativeTab.setSelectedItem(item.creativeTab);
		stackSize.setValue(item.stackSize);
		enchantability.setValue(item.enchantability);
		toolType.setValue(item.toolType);
		useDuration.setValue(item.useDuration);
		damageCount.setValue(item.damageCount);
		recipeRemainder.setBlock(item.recipeRemainder);
		immuneToFire.setSelected(item.immuneToFire);
		destroyAnyBlock.setSelected(item.destroyAnyBlock);
		stayInGridWhenCrafting.setSelected(item.stayInGridWhenCrafting);
		damageOnCrafting.setSelected(item.damageOnCrafting);
		specialInformation.setSelectedProcedure(item.specialInformation);
		glowCondition.setSelectedProcedure(item.glowCondition);
		damageVsEntity.setValue(item.damageVsEntity);
		enableMeleeDamage.setSelected(item.enableMeleeDamage);
		guiBoundTo.setSelectedItem(item.guiBoundTo);
		inventorySize.setValue(item.inventorySize);
		inventoryStackSize.setValue(item.inventoryStackSize);
		isFood.setSelected(item.isFood);
		isMeat.setSelected(item.isMeat);
		isAlwaysEdible.setSelected(item.isAlwaysEdible);
		onFinishUsingItem.setSelectedProcedure(item.onFinishUsingItem);
		nutritionalValue.setValue(item.nutritionalValue);
		saturation.setValue(item.saturation);
		animation.setSelectedItem(item.animation);
		eatResultItem.setBlock(item.eatResultItem);
		enableRanged.setSelected(item.enableRanged);
		shootConstantly.setSelected(item.shootConstantly);
		projectile.setSelectedItem(item.projectile);
		rangedUseCondition.setSelectedProcedure(item.rangedUseCondition);
		onRangedItemUsed.setSelectedProcedure(item.onRangedItemUsed);

		updateFoodPanel();
		updateRangedPanel();
		onStoppedUsing.setEnabled((int) useDuration.getValue() > 0);

		Model model = item.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);

		customProperties.setProperties(item.customProperties);
		customProperties.setStates(item.states);
	}

	@Override public Item getElementFromGUI() {
		Item item = new Item(modElement);
		item.name = name.getText();
		item.rarity = (String) rarity.getSelectedItem();
		item.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		item.stackSize = (int) stackSize.getValue();
		item.enchantability = (int) enchantability.getValue();
		item.useDuration = (int) useDuration.getValue();
		item.toolType = (double) toolType.getValue();
		item.damageCount = (int) damageCount.getValue();
		item.recipeRemainder = recipeRemainder.getBlock();
		item.immuneToFire = immuneToFire.isSelected();
		item.destroyAnyBlock = destroyAnyBlock.isSelected();
		item.stayInGridWhenCrafting = stayInGridWhenCrafting.isSelected();
		item.damageOnCrafting = damageOnCrafting.isSelected();
		item.specialInformation = specialInformation.getSelectedProcedure();
		item.glowCondition = glowCondition.getSelectedProcedure();
		item.onRightClickedInAir = onRightClickedInAir.getSelectedProcedure();
		item.onRightClickedOnBlock = onRightClickedOnBlock.getSelectedProcedure();
		item.onCrafted = onCrafted.getSelectedProcedure();
		item.onEntityHitWith = onEntityHitWith.getSelectedProcedure();
		item.onItemInInventoryTick = onItemInInventoryTick.getSelectedProcedure();
		item.onItemInUseTick = onItemInUseTick.getSelectedProcedure();
		item.onStoppedUsing = onStoppedUsing.getSelectedProcedure();
		item.onEntitySwing = onEntitySwing.getSelectedProcedure();
		item.onDroppedByPlayer = onDroppedByPlayer.getSelectedProcedure();
		item.damageVsEntity = (double) damageVsEntity.getValue();
		item.enableMeleeDamage = enableMeleeDamage.isSelected();
		item.inventorySize = (int) inventorySize.getValue();
		item.inventoryStackSize = (int) inventoryStackSize.getValue();
		item.guiBoundTo = guiBoundTo.getSelectedItem();
		item.isFood = isFood.isSelected();
		item.nutritionalValue = (int) nutritionalValue.getValue();
		item.saturation = (double) saturation.getValue();
		item.isMeat = isMeat.isSelected();
		item.isAlwaysEdible = isAlwaysEdible.isSelected();
		item.animation = (String) animation.getSelectedItem();
		item.onFinishUsingItem = onFinishUsingItem.getSelectedProcedure();
		item.eatResultItem = eatResultItem.getBlock();
		item.enableRanged = enableRanged.isSelected();
		item.shootConstantly = shootConstantly.isSelected();
		item.projectile = new ProjectileEntry(mcreator.getWorkspace(), projectile.getSelectedItem());
		item.onRangedItemUsed = onRangedItemUsed.getSelectedProcedure();
		item.rangedUseCondition = rangedUseCondition.getSelectedProcedure();

		item.texture = texture.getID();
		item.renderType = Item.encodeModelType(Objects.requireNonNull(renderType.getSelectedItem()).getType());
		item.customModelName = Objects.requireNonNull(renderType.getSelectedItem()).getReadableName();

		item.customProperties = customProperties.getProperties();
		item.states = customProperties.getStates();

		return item;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-item");
	}

}
