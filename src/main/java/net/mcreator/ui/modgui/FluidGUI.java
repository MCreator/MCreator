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
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Fluid;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class FluidGUI extends ModElementGUI<Fluid> {

	private TextureHolder textureStill;
	private TextureHolder textureFlowing;

	private final VTextField name = new VTextField(18);
	private final JCheckBox canMultiply = L10N.checkbox("elementgui.common.enable");
	private final JSpinner flowRate = new JSpinner(new SpinnerNumberModel(5, 1, 100000, 1));
	private final JSpinner levelDecrease = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
	private final JSpinner slopeFindDistance = new JSpinner(new SpinnerNumberModel(4, 1, 16, 1));
	private final JCheckBox spawnParticles = L10N.checkbox("elementgui.common.enable");
	private final DataListComboBox dripParticle = new DataListComboBox(mcreator);
	private final JSpinner flowStrength = new JSpinner(new SpinnerNumberModel(1, -25, 25, 0.1));
	private final JComboBox<String> tintType = new JComboBox<>(
			new String[] { "No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage", "Water",
					"Sky", "Fog", "Water fog" });

	private final JSpinner luminosity = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
	private final JSpinner density = new JSpinner(new SpinnerNumberModel(1000, -100000, 100000, 1));
	private final JSpinner viscosity = new JSpinner(new SpinnerNumberModel(1000, 0, 100000, 1));
	private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(300, 0, 100000, 1));

	private final JCheckBox generateBucket = L10N.checkbox("elementgui.common.enable");
	private final VTextField bucketName = new VTextField(18);
	private TextureHolder textureBucket;
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);
	private final SoundSelector emptySound = new SoundSelector(mcreator);
	private final JComboBox<String> rarity = new JComboBox<>(new String[] { "COMMON", "UNCOMMON", "RARE", "EPIC" });
	private StringListProcedureSelector specialInformation;

	private final JComboBox<String> fluidtype = new JComboBox<>(new String[] { "WATER", "LAVA" });

	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 0.5));
	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
	private final JSpinner lightOpacity = new JSpinner(new SpinnerNumberModel(1, 0, 15, 1));
	private final JCheckBox emissiveRendering = L10N.checkbox("elementgui.common.enable");
	private final JSpinner tickRate = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final DataListComboBox colorOnMap = new DataListComboBox(mcreator, ElementUtil.loadMapColors());

	private ProcedureSelector onBlockAdded;
	private ProcedureSelector onNeighbourChanges;
	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onEntityCollides;
	private ProcedureSelector onRandomUpdateEvent;
	private ProcedureSelector onDestroyedByExplosion;
	private ProcedureSelector flowCondition;
	private ProcedureSelector beforeReplacingBlock;

	private final ValidationGroup page1group = new ValidationGroup();

	public FluidGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator,
				L10N.t("elementgui.fluid.when_added"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onNeighbourChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				L10N.t("elementgui.common.event_on_neighbour_block_changes"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator,
				L10N.t("elementgui.common.event_on_update_tick"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				L10N.t("elementgui.fluid.when_entity_collides"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onRandomUpdateEvent = new ProcedureSelector(this.withEntry("block/display_tick_update"), mcreator,
				L10N.t("elementgui.common.event_on_random_update"), ProcedureSelector.Side.CLIENT,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				L10N.t("elementgui.block.event_on_block_destroyed_by_explosion"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		flowCondition = new ProcedureSelector(this.withEntry("fluid/flow_condition"), mcreator,
				L10N.t("elementgui.fluid.event_flow_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/direction:direction/blockstate:blockstate/intostate:blockstate")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();
		beforeReplacingBlock = new ProcedureSelector(this.withEntry("fluid/before_replacing_block"), mcreator,
				L10N.t("elementgui.fluid.event_before_replacing_block"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));

		specialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));

		fluidtype.setRenderer(new ItemTexturesComboBoxRenderer());

		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		pane3.setOpaque(false);

		JPanel destalx = new JPanel(new FlowLayout(FlowLayout.CENTER));
		destalx.setOpaque(false);

		textureStill = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureStill.setOpaque(false);
		textureFlowing = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureFlowing.setOpaque(false);

		destalx.add(ComponentUtils.squareAndBorder(textureStill, L10N.t("elementgui.fluid.texture_still")));
		destalx.add(ComponentUtils.squareAndBorder(textureFlowing, L10N.t("elementgui.fluid.texture_flowing")));

		JPanel destal = new JPanel(new GridLayout(10, 2, 5, 2));
		destal.setOpaque(false);

		canMultiply.setOpaque(false);
		flowRate.setOpaque(false);
		levelDecrease.setOpaque(false);
		slopeFindDistance.setOpaque(false);
		spawnParticles.setOpaque(false);
		flowStrength.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(bucketName, 16);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		destal.add(name);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/type"), L10N.label("elementgui.fluid.type")));
		destal.add(fluidtype);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/flow_rate"),
				L10N.label("elementgui.fluid.flow_rate")));
		destal.add(flowRate);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/flow_strength"),
				L10N.label("elementgui.fluid.flow_strength")));
		destal.add(flowStrength);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/level_decrease"),
				L10N.label("elementgui.fluid.level_decrease")));
		destal.add(levelDecrease);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/slope_find_distance"),
				L10N.label("elementgui.fluid.slope_find_distance")));
		destal.add(slopeFindDistance);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/can_multiply"),
				L10N.label("elementgui.fluid.can_multiply")));
		destal.add(canMultiply);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/spawn_drip_particles"),
				L10N.label("elementgui.fluid.spawn_particles")));
		destal.add(spawnParticles);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/drip_particle"),
				L10N.label("elementgui.fluid.drip_particle")));
		destal.add(dripParticle);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/tint_type"),
				L10N.label("elementgui.fluid.tint_type")));
		destal.add(tintType);

		JPanel bucketProperties = new JPanel(new GridLayout(6, 2, 5, 2));
		bucketProperties.setOpaque(false);

		textureBucket = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32);
		generateBucket.setOpaque(false);
		textureBucket.setOpaque(false);

		bucketProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/enable_bucket"),
				L10N.label("elementgui.fluid.generate_bucket_label")));
		bucketProperties.add(PanelUtils.centerInPanel(generateBucket));

		bucketProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.fluid.bucket_name_in_gui")));
		bucketProperties.add(bucketName);

		bucketProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/bucket_texture"),
				L10N.label("elementgui.fluid.bucket_texture")));
		bucketProperties.add(PanelUtils.centerInPanel(textureBucket));

		bucketProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		bucketProperties.add(creativeTab);

		bucketProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/empty_sound"),
				L10N.label("elementgui.fluid.empty_sound")));
		bucketProperties.add(emptySound);

		bucketProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		bucketProperties.add(rarity);

		JPanel bcProp = new JPanel(new BorderLayout(5, 2));
		bcProp.setOpaque(false);
		bcProp.add("Center", bucketProperties);
		bcProp.add("South", specialInformation);

		generateBucket.setSelected(true);

		generateBucket.addActionListener(e -> {
			bucketName.setEnabled(generateBucket.isSelected());
			textureBucket.setEnabled(generateBucket.isSelected());
			creativeTab.setEnabled(generateBucket.isSelected());
			emptySound.setEnabled(generateBucket.isSelected());
			rarity.setEnabled(generateBucket.isSelected());
			specialInformation.setEnabled(generateBucket.isSelected());
		});

		bcProp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.fluid.bucket_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JComponent destala = PanelUtils.northAndCenterElement(destal,
				PanelUtils.westAndCenterElement(new JEmptyBox(4, 4), flowCondition), 0, 2);

		destala.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.fluid.fluid_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JComponent fluidBucketProperties = PanelUtils.westAndEastElement(destala, PanelUtils.pullElementUp(bcProp));
		fluidBucketProperties.setOpaque(false);
		pane3.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(destalx, fluidBucketProperties)));

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		JPanel blockProperties = new JPanel(new GridLayout(8, 2, 20, 2));
		blockProperties.setOpaque(false);

		resistance.setOpaque(false);
		luminance.setOpaque(false);
		lightOpacity.setOpaque(false);
		emissiveRendering.setOpaque(false);
		tickRate.setOpaque(false);
		flammability.setOpaque(false);
		fireSpreadSpeed.setOpaque(false);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"),
				L10N.label("elementgui.common.resistance")));
		blockProperties.add(resistance);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"),
				L10N.label("elementgui.common.luminance")));
		blockProperties.add(luminance);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/light_opacity"),
				L10N.label("elementgui.common.light_opacity")));
		blockProperties.add(lightOpacity);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tick_rate"),
				L10N.label("elementgui.common.tick_rate")));
		blockProperties.add(tickRate);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/flammability"),
				L10N.label("elementgui.block.flammability")));
		blockProperties.add(flammability);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				L10N.label("elementgui.common.fire_spread_speed")));
		blockProperties.add(fireSpreadSpeed);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/color_on_map"),
				L10N.label("elementgui.block.color_on_map")));
		blockProperties.add(colorOnMap);

		blockProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				L10N.label("elementgui.common.emissive_rendering")));
		blockProperties.add(emissiveRendering);

		blockProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.fluid.block_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JPanel forgeProperties = new JPanel(new GridLayout(4, 2, 20, 2));
		forgeProperties.setOpaque(false);

		luminosity.setOpaque(false);
		density.setOpaque(false);
		viscosity.setOpaque(false);
		temperature.setOpaque(false);
		ComponentUtils.deriveFont(luminosity, 16);
		ComponentUtils.deriveFont(density, 16);
		ComponentUtils.deriveFont(viscosity, 16);
		ComponentUtils.deriveFont(temperature, 16);

		forgeProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/luminosity"),
				L10N.label("elementgui.fluid.luminosity")));
		forgeProperties.add(luminosity);

		forgeProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("fluid/density"), L10N.label("elementgui.fluid.density")));
		forgeProperties.add(density);

		forgeProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/viscosity"),
				L10N.label("elementgui.fluid.viscosity")));
		forgeProperties.add(viscosity);

		forgeProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/temperature"),
				L10N.label("elementgui.fluid.temperature")));
		forgeProperties.add(temperature);

		forgeProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.fluid.modded_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JComponent properties = PanelUtils.westAndEastElement(blockProperties,
				PanelUtils.pullElementUp(forgeProperties));
		properties.setOpaque(false);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(properties));

		JPanel events = new JPanel(new GridLayout(3, 3, 5, 5));
		events.setOpaque(false);
		events.add(onBlockAdded);
		events.add(onNeighbourChanges);
		events.add(onTickUpdate);
		events.add(onEntityCollides);
		events.add(onRandomUpdateEvent);
		events.add(onDestroyedByExplosion);
		events.add(beforeReplacingBlock);
		events.add(new JLabel());
		events.add(new JLabel());
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);

		textureStill.setValidator(new TileHolderValidator(textureStill));
		textureFlowing.setValidator(new TileHolderValidator(textureFlowing));
		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.fluid.error_fluid_needs_name")));
		name.enableRealtimeValidation();

		page1group.addValidationElement(textureStill);
		page1group.addValidationElement(textureFlowing);
		page1group.addValidationElement(name);

		bucketName.setValidator(new TextFieldValidator(bucketName, L10N.t("elementgui.fluid.error_bucket_needs_name")));
		bucketName.enableRealtimeValidation();

		page1group.addValidationElement(bucketName);

		addPage(L10N.t("elementgui.fluid.page_visual_and_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), pane2);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
			bucketName.setText(readableNameFromModElement + " Bucket");
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onBlockAdded.refreshListKeepSelected();
		onNeighbourChanges.refreshListKeepSelected();
		onTickUpdate.refreshListKeepSelected();
		onEntityCollides.refreshListKeepSelected();
		onRandomUpdateEvent.refreshListKeepSelected();
		onDestroyedByExplosion.refreshListKeepSelected();
		flowCondition.refreshListKeepSelected();
		beforeReplacingBlock.refreshListKeepSelected();
		specialInformation.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(dripParticle, ElementUtil.loadAllParticles(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Fluid fluid) {
		textureStill.setTextureFromTextureName(fluid.textureStill);
		textureFlowing.setTextureFromTextureName(fluid.textureFlowing);
		name.setText(fluid.name);
		bucketName.setText(fluid.bucketName);
		canMultiply.setSelected(fluid.canMultiply);
		flowRate.setValue(fluid.flowRate);
		levelDecrease.setValue(fluid.levelDecrease);
		slopeFindDistance.setValue(fluid.slopeFindDistance);
		spawnParticles.setSelected(fluid.spawnParticles);
		dripParticle.setSelectedItem(fluid.dripParticle);
		tintType.setSelectedItem(fluid.tintType);
		flowStrength.setValue(fluid.flowStrength);
		luminosity.setValue(fluid.luminosity);
		density.setValue(fluid.density);
		viscosity.setValue(fluid.viscosity);
		temperature.setValue(fluid.temperature);
		generateBucket.setSelected(fluid.generateBucket);
		textureBucket.setTextureFromTextureName(fluid.textureBucket);
		emptySound.setSound(fluid.emptySound);
		rarity.setSelectedItem(fluid.rarity);
		resistance.setValue(fluid.resistance);
		luminance.setValue(fluid.luminance);
		lightOpacity.setValue(fluid.lightOpacity);
		emissiveRendering.setSelected(fluid.emissiveRendering);
		tickRate.setValue(fluid.tickRate);
		flammability.setValue(fluid.flammability);
		fireSpreadSpeed.setValue(fluid.fireSpreadSpeed);
		colorOnMap.setSelectedItem(fluid.colorOnMap);
		onBlockAdded.setSelectedProcedure(fluid.onBlockAdded);
		onNeighbourChanges.setSelectedProcedure(fluid.onNeighbourChanges);
		onTickUpdate.setSelectedProcedure(fluid.onTickUpdate);
		onEntityCollides.setSelectedProcedure(fluid.onEntityCollides);
		onRandomUpdateEvent.setSelectedProcedure(fluid.onRandomUpdateEvent);
		onDestroyedByExplosion.setSelectedProcedure(fluid.onDestroyedByExplosion);
		flowCondition.setSelectedProcedure(fluid.flowCondition);
		beforeReplacingBlock.setSelectedProcedure(fluid.beforeReplacingBlock);
		fluidtype.setSelectedItem(fluid.type);
		specialInformation.setSelectedProcedure(fluid.specialInformation);

		if (fluid.creativeTab != null)
			creativeTab.setSelectedItem(fluid.creativeTab);

		bucketName.setEnabled(generateBucket.isSelected());
		textureBucket.setEnabled(generateBucket.isSelected());
		creativeTab.setEnabled(generateBucket.isSelected());
		emptySound.setEnabled(generateBucket.isSelected());
		rarity.setEnabled(generateBucket.isSelected());
		specialInformation.setEnabled(generateBucket.isSelected());
	}

	@Override public Fluid getElementFromGUI() {
		Fluid fluid = new Fluid(modElement);
		fluid.name = name.getText();
		fluid.bucketName = bucketName.getText();
		fluid.textureFlowing = textureFlowing.getID();
		fluid.textureStill = textureStill.getID();
		fluid.canMultiply = canMultiply.isSelected();
		fluid.flowRate = (int) flowRate.getValue();
		fluid.levelDecrease = (int) levelDecrease.getValue();
		fluid.slopeFindDistance = (int) slopeFindDistance.getValue();
		fluid.spawnParticles = spawnParticles.isSelected();
		fluid.dripParticle = new Particle(mcreator.getWorkspace(), dripParticle.getSelectedItem());
		fluid.tintType = (String) tintType.getSelectedItem();
		fluid.flowStrength = (double) flowStrength.getValue();
		fluid.luminosity = (int) luminosity.getValue();
		fluid.density = (int) density.getValue();
		fluid.viscosity = (int) viscosity.getValue();
		fluid.temperature = (int) temperature.getValue();
		fluid.generateBucket = generateBucket.isSelected();
		fluid.textureBucket = textureBucket.getID();
		fluid.emptySound = emptySound.getSound();
		fluid.rarity = (String) rarity.getSelectedItem();
		fluid.resistance = (double) resistance.getValue();
		fluid.luminance = (int) luminance.getValue();
		fluid.lightOpacity = (int) lightOpacity.getValue();
		fluid.emissiveRendering = emissiveRendering.isSelected();
		fluid.tickRate = (int) tickRate.getValue();
		fluid.flammability = (int) flammability.getValue();
		fluid.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();
		fluid.colorOnMap = colorOnMap.getSelectedItem().toString();
		fluid.onBlockAdded = onBlockAdded.getSelectedProcedure();
		fluid.onNeighbourChanges = onNeighbourChanges.getSelectedProcedure();
		fluid.onTickUpdate = onTickUpdate.getSelectedProcedure();
		fluid.onEntityCollides = onEntityCollides.getSelectedProcedure();
		fluid.onRandomUpdateEvent = onRandomUpdateEvent.getSelectedProcedure();
		fluid.onDestroyedByExplosion = onDestroyedByExplosion.getSelectedProcedure();
		fluid.flowCondition = flowCondition.getSelectedProcedure();
		fluid.beforeReplacingBlock = beforeReplacingBlock.getSelectedProcedure();
		fluid.type = (String) fluidtype.getSelectedItem();
		fluid.specialInformation = specialInformation.getSelectedProcedure();

		fluid.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		return fluid;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-fluid");
	}

}
