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
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.Item;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.states.item.JItemPropertiesStatesList;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemGUI extends ModElementGUI<Item> {

	private TextureSelectionButton texture;

	private StringListProcedureSelector specialInformation;

	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 1, 99, 1));
	private final VTextField name = new VTextField(20);
	private final TranslatedComboBox rarity = new TranslatedComboBox(
			//@formatter:off
			Map.entry("COMMON", "elementgui.common.rarity_common"),
			Map.entry("UNCOMMON", "elementgui.common.rarity_uncommon"),
			Map.entry("RARE", "elementgui.common.rarity_rare"),
			Map.entry("EPIC", "elementgui.common.rarity_epic")
			//@formatter:on
	);

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
	private final JCheckBox rangedItemChargesPower = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox projectileDisableAmmoCheck = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector onRangedItemUsed;
	private ProcedureSelector rangedUseCondition;

	private final DataListComboBox projectile = new DataListComboBox(mcreator);

	private final TabListField creativeTabs = new TabListField(mcreator);

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
	private final ValidationGroup page5group = new ValidationGroup();

	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private SingleModElementSelector guiBoundTo;
	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));

	// Food parameters
	private final JCheckBox isFood = L10N.checkbox("elementgui.common.enable");
	private final JSpinner nutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner saturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));
	private final JCheckBox isMeat = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isAlwaysEdible = L10N.checkbox("elementgui.common.enable");
	private final TranslatedComboBox animation = new TranslatedComboBox(
			//@formatter:off
			Map.entry("none", "elementgui.item.item_animation_none"),
			Map.entry("eat", "elementgui.item.item_animation_eat"),
			Map.entry("block", "elementgui.item.item_animation_block"),
			Map.entry("bow", "elementgui.item.item_animation_bow"),
			Map.entry("crossbow", "elementgui.item.item_animation_crossbow"),
			Map.entry("drink", "elementgui.item.item_animation_drink"),
			Map.entry("spear", "elementgui.item.item_animation_spear")
			//@formatter:on
	);
	private final MCItemHolder eatResultItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	// Music disc parameters
	private final JCheckBox isMusicDisc = L10N.checkbox("elementgui.common.enable");
	private final SoundSelector musicDiscMusic = new SoundSelector(mcreator);
	private final VTextField musicDiscDescription = new VTextField(20);
	private final JSpinner musicDiscLengthInTicks = new JSpinner(new SpinnerNumberModel(100, 1, 20 * 3600, 1));
	private final JSpinner musicDiscAnalogOutput = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));

	private ModElementListField providedBannerPatterns;

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
		guiBoundTo = new SingleModElementSelector(mcreator, ModElementType.GUI);
		guiBoundTo.setDefaultText(L10N.t("elementgui.common.no_gui"));

		providedBannerPatterns = new ModElementListField(mcreator, ModElementType.BANNERPATTERN);

		guiBoundTo.addEntrySelectedListener(e -> {
			if (!isEditingMode()) {
				String selected = guiBoundTo.getEntry();
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

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
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

		JPanel subpane2 = new JPanel(new GridLayout(15, 2, 65, 2));

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		subpane2.add(name);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		subpane2.add(rarity);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.common.creative_tabs")));
		subpane2.add(creativeTabs);

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
		damageCount.addChangeListener(e -> updateCraftingSettings());
		immuneToFire.setOpaque(false);
		destroyAnyBlock.setOpaque(false);
		stayInGridWhenCrafting.setOpaque(false);
		stayInGridWhenCrafting.addActionListener(e -> updateCraftingSettings());
		damageOnCrafting.setOpaque(false);

		updateCraftingSettings();

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

		JPanel musicDiscBannerProperties = new JPanel(new GridLayout(6, 2, 35, 2));
		musicDiscBannerProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.section_musicdisc_banner"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));
		musicDiscBannerProperties.setOpaque(false);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/musicdisc"),
				L10N.label("elementgui.item.musicdisc")));
		musicDiscBannerProperties.add(isMusicDisc);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/musicdisc_music"),
				L10N.label("elementgui.item.musicdisc_music")));
		musicDiscBannerProperties.add(musicDiscMusic);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/musicdisc_description"),
				L10N.label("elementgui.item.musicdisc_description")));
		musicDiscBannerProperties.add(musicDiscDescription);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/musicdisc_length"),
				L10N.label("elementgui.item.musicdisc_length")));
		musicDiscBannerProperties.add(musicDiscLengthInTicks);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/musicdisc_analog_output"),
				L10N.label("elementgui.item.musicdisc_analog_output")));
		musicDiscBannerProperties.add(musicDiscAnalogOutput);

		musicDiscBannerProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/provided_banner_patterns"),
				L10N.label("elementgui.item.provided_banner_patterns")));
		musicDiscBannerProperties.add(providedBannerPatterns);

		ComponentUtils.deriveFont(musicDiscDescription, 16);

		updateMusicDiscBannerPanel();

		isMusicDisc.addActionListener(e -> updateMusicDiscBannerPanel());

		JPanel rangedProperties = new JPanel(new GridLayout(5, 2, 2, 2));
		rangedProperties.setOpaque(false);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/enable_ranged_item"),
				L10N.label("elementgui.item.enable_ranged_item")));
		enableRanged.setOpaque(false);
		enableRanged.addActionListener(e -> updateRangedPanel());
		rangedProperties.add(enableRanged);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/projectile"),
				L10N.label("elementgui.item.projectile")));
		rangedProperties.add(projectile);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/projectile_disable_ammo_check"),
				L10N.label("elementgui.item.projectile_disable_ammo_check")));
		projectileDisableAmmoCheck.setOpaque(false);
		rangedProperties.add(projectileDisableAmmoCheck);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/shoot_constantly"),
				L10N.label("elementgui.item.shoot_constantly")));
		shootConstantly.setOpaque(false);
		rangedProperties.add(shootConstantly);

		rangedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/charges_power"),
				L10N.label("elementgui.item.charges_power")));
		rangedItemChargesPower.setOpaque(false);
		rangedProperties.add(rangedItemChargesPower);

		updateRangedPanel();

		shootConstantly.addActionListener((e) -> {
			rangedItemChargesPower.setEnabled(!shootConstantly.isSelected());
			if (shootConstantly.isSelected())
				rangedItemChargesPower.setSelected(false);
		});

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

		advancedProperties.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndEastElement(
				PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(inventoryProperties, musicDiscBannerProperties)),
				PanelUtils.pullElementUp(rangedPanel), 10, 10)));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.item.error_item_needs_name")));
		name.enableRealtimeValidation();

		musicDiscDescription.setValidator(new TextFieldValidator(musicDiscDescription,
				L10N.t("elementgui.item.musicdisc.error_disc_needs_description")));
		musicDiscDescription.enableRealtimeValidation();

		musicDiscMusic.getVTextField().setValidator(new TextFieldValidator(musicDiscMusic.getVTextField(),
				L10N.t("elementgui.item.musicdisc.error_needs_sound")));
		musicDiscMusic.getVTextField().enableRealtimeValidation();

		page5group.addValidationElement(musicDiscDescription);
		page5group.addValidationElement(musicDiscMusic.getVTextField());

		addPage(L10N.t("elementgui.common.page_visual"), pane2).validate(page1group);
		addPage(L10N.t("elementgui.item.page_item_states"), cipp, false).lazyValidate(
				customProperties::getValidationResult);
		addPage(L10N.t("elementgui.common.page_properties"), pane3).validate(name);
		addPage(L10N.t("elementgui.item.food_properties"), foodProperties);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), advancedProperties).validate(page5group);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void updateCraftingSettings() {
		recipeRemainder.setEnabled(stayInGridWhenCrafting.isSelected());
		damageOnCrafting.setEnabled(stayInGridWhenCrafting.isSelected() && ((int) damageCount.getValue() > 0));
	}

	private void updateMusicDiscBannerPanel() {
		boolean isDisc = isMusicDisc.isSelected();
		musicDiscMusic.setEnabled(isDisc);
		musicDiscDescription.setEnabled(isDisc);
		musicDiscLengthInTicks.setEnabled(isDisc);
		musicDiscAnalogOutput.setEnabled(isDisc);
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
			rangedItemChargesPower.setEnabled(!shootConstantly.isSelected());
			projectile.setEnabled(true);
			onRangedItemUsed.setEnabled(true);
			rangedUseCondition.setEnabled(true);
			projectileDisableAmmoCheck.setEnabled(true);
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
			rangedItemChargesPower.setEnabled(false);
			projectile.setEnabled(false);
			onRangedItemUsed.setEnabled(false);
			rangedUseCondition.setEnabled(false);
			projectileDisableAmmoCheck.setEnabled(false);
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

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(ItemGUI.builtinitemmodels),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override public void openInEditingMode(Item item) {
		name.setText(item.name);
		rarity.setSelectedItem(item.rarity);
		texture.setTexture(item.texture);
		onRightClickedInAir.setSelectedProcedure(item.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(item.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(item.onCrafted);
		onEntityHitWith.setSelectedProcedure(item.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(item.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(item.onItemInUseTick);
		onStoppedUsing.setSelectedProcedure(item.onStoppedUsing);
		onEntitySwing.setSelectedProcedure(item.onEntitySwing);
		onDroppedByPlayer.setSelectedProcedure(item.onDroppedByPlayer);
		creativeTabs.setListElements(item.creativeTabs);
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
		guiBoundTo.setEntry(item.guiBoundTo);
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
		projectileDisableAmmoCheck.setSelected(item.projectileDisableAmmoCheck);
		shootConstantly.setSelected(item.shootConstantly);
		rangedItemChargesPower.setSelected(item.rangedItemChargesPower);
		projectile.setSelectedItem(item.projectile);
		rangedUseCondition.setSelectedProcedure(item.rangedUseCondition);
		onRangedItemUsed.setSelectedProcedure(item.onRangedItemUsed);
		isMusicDisc.setSelected(item.isMusicDisc);
		musicDiscMusic.setSound(item.musicDiscMusic);
		musicDiscDescription.setText(item.musicDiscDescription);
		musicDiscLengthInTicks.setValue(item.musicDiscLengthInTicks);
		musicDiscAnalogOutput.setValue(item.musicDiscAnalogOutput);
		providedBannerPatterns.setListElements(
				item.providedBannerPatterns.stream().map(NonMappableElement::new).toList());

		updateCraftingSettings();
		updateFoodPanel();
		updateRangedPanel();
		updateMusicDiscBannerPanel();
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
		item.rarity = rarity.getSelectedItem();
		item.creativeTabs = creativeTabs.getListElements();
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
		item.guiBoundTo = guiBoundTo.getEntry();
		item.isFood = isFood.isSelected();
		item.nutritionalValue = (int) nutritionalValue.getValue();
		item.saturation = (double) saturation.getValue();
		item.isMeat = isMeat.isSelected();
		item.isAlwaysEdible = isAlwaysEdible.isSelected();
		item.animation = animation.getSelectedItem();
		item.onFinishUsingItem = onFinishUsingItem.getSelectedProcedure();
		item.eatResultItem = eatResultItem.getBlock();
		item.enableRanged = enableRanged.isSelected();
		item.projectileDisableAmmoCheck = projectileDisableAmmoCheck.isSelected();
		item.shootConstantly = shootConstantly.isSelected();
		item.rangedItemChargesPower = rangedItemChargesPower.isSelected();
		item.projectile = new ProjectileEntry(mcreator.getWorkspace(), projectile.getSelectedItem());
		item.onRangedItemUsed = onRangedItemUsed.getSelectedProcedure();
		item.rangedUseCondition = rangedUseCondition.getSelectedProcedure();
		item.isMusicDisc = isMusicDisc.isSelected();
		item.musicDiscMusic = musicDiscMusic.getSound();
		item.musicDiscDescription = musicDiscDescription.getText();
		item.musicDiscLengthInTicks = (int) musicDiscLengthInTicks.getValue();
		item.musicDiscAnalogOutput = (int) musicDiscAnalogOutput.getValue();
		item.providedBannerPatterns = providedBannerPatterns.getListElements().stream()
				.map(NonMappableElement::getUnmappedValue).collect(Collectors.toList());

		item.texture = texture.getTextureHolder();
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
