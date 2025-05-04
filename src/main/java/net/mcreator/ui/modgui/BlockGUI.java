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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.InputSlot;
import net.mcreator.element.parts.gui.OutputSlot;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.*;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.blockentityanimations.JBlockEntityAnimationList;
import net.mcreator.ui.minecraft.boundingboxes.JBoundingBoxList;
import net.mcreator.ui.minecraft.states.block.JBlockPropertiesStatesList;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.*;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockGUI extends ModElementGUI<Block> {

	private BlockTexturesSelector textures;

	private TextureSelectionButton itemTexture;
	private TextureSelectionButton particleTexture;

	private final JCheckBox disableOffset = L10N.checkbox("elementgui.common.enable");
	private JBoundingBoxList boundingBoxList;

	private ProcedureSelector onBlockAdded;
	private ProcedureSelector onNeighbourBlockChanges;
	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onRandomUpdateEvent;
	private ProcedureSelector onDestroyedByPlayer;
	private ProcedureSelector onDestroyedByExplosion;
	private ProcedureSelector onStartToDestroy;
	private ProcedureSelector onEntityCollides;
	private ProcedureSelector onEntityWalksOn;
	private ProcedureSelector onBlockPlayedBy;
	private ProcedureSelector onRightClicked;
	private ProcedureSelector onRedstoneOn;
	private ProcedureSelector onRedstoneOff;
	private ProcedureSelector onHitByProjectile;
	private ProcedureSelector onBonemealSuccess;

	private StringListProcedureSelector specialInformation;
	private NumberProcedureSelector emittedRedstonePower;
	private ProcedureSelector placingCondition;
	private ProcedureSelector isBonemealTargetCondition;
	private ProcedureSelector bonemealSuccessCondition;

	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(1, -1, 64000, 0.05));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 0.5));
	private final VTextField name = new VTextField(19);

	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));
	private final JSpinner lightOpacity = new JSpinner(new SpinnerNumberModel(15, 0, 15, 1));

	private final JSpinner tickRate = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));

	private final JSpinner enchantPowerBonus = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 0.1));

	private final JColor beaconColorModifier = new JColor(mcreator, true, false);

	private final JCheckBox hasGravity = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isWaterloggable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox tickRandomly = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox unbreakable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isNotColidable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canRedstoneConnect = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isBonemealable = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> tintType = new JComboBox<>(
			new String[] { "No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage", "Water",
					"Sky", "Fog", "Water fog" });
	private final JCheckBox isItemTinted = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox hasTransparency = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox connectedSides = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox emissiveRendering = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox displayFluidOverlay = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox hasEnergyStorage = L10N.checkbox("elementgui.block.enable_energy_storage");
	private final JCheckBox isFluidTank = L10N.checkbox("elementgui.block.enable_fluid_storage");

	private final JSpinner energyInitial = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyCapacity = new JSpinner(new SpinnerNumberModel(400000, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyMaxReceive = new JSpinner(new SpinnerNumberModel(200, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyMaxExtract = new JSpinner(new SpinnerNumberModel(200, 0, Integer.MAX_VALUE, 1));
	private final JSpinner fluidCapacity = new JSpinner(new SpinnerNumberModel(8000, 0, Integer.MAX_VALUE, 1));
	private FluidListField fluidRestrictions;

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator, ElementUtil.loadStepSounds());
	private final JRadioButton defaultSoundType = L10N.radiobutton("elementgui.common.default_sound_type");
	private final JRadioButton customSoundType = L10N.radiobutton("elementgui.common.custom_sound_type");
	private final SoundSelector breakSound = new SoundSelector(mcreator);
	private final SoundSelector fallSound = new SoundSelector(mcreator);
	private final SoundSelector hitSound = new SoundSelector(mcreator);
	private final SoundSelector placeSound = new SoundSelector(mcreator);
	private final SoundSelector stepSound = new SoundSelector(mcreator);

	private final JCheckBox isReplaceable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canProvidePower = L10N.checkbox("elementgui.common.enable");
	private final DataListComboBox colorOnMap = new DataListComboBox(mcreator, ElementUtil.loadMapColors());
	private final DataListComboBox noteBlockInstrument = new DataListComboBox(mcreator,
			ElementUtil.loadNoteBlockInstruments());
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final JComboBox<String> generationShape = new JComboBox<>(new String[] { "UNIFORM", "TRIANGLE" });
	private final JMinMaxSpinner generateHeight = new JMinMaxSpinner(0, 64, -2032, 2016, 1).allowEqualValues();
	private final JSpinner frequencyPerChunks = new JSpinner(new SpinnerNumberModel(10, 1, 64, 1));
	private final JSpinner frequencyOnChunk = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));
	private BiomeListField restrictionBiomes;
	private MCItemListField blocksToReplace;
	private final JCheckBox generateFeature = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox plantsGrowOn = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isLadder = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> reactionToPushing = new JComboBox<>(
			new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "NONE", "XZ", "XYZ" });
	private final SearchableComboBox<String> aiPathNodeType = new SearchableComboBox<>();

	private final TabListField creativeTabs = new TabListField(mcreator);

	private final JSpinner slipperiness = new JSpinner(new SpinnerNumberModel(0.6, 0.01, 5, 0.01));
	private final JSpinner speedFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));
	private final JSpinner jumpFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));

	private final JCheckBox sensitiveToVibration = L10N.checkbox("elementgui.common.enable");
	private GameEventListField vibrationalEvents;
	private NumberProcedureSelector vibrationSensitivityRadius;
	private ProcedureSelector canReceiveVibrationCondition;
	private ProcedureSelector onReceivedVibration;

	private final JComboBox<String> rotationMode = new JComboBox<>(
			new String[] { "<html>No rotation<br><small>Fixed block orientation",
					"<html>Y axis rotation (S/W/N/E)<br><small>Rotation from player side",
					"<html>D/U/N/S/W/E rotation<br><small>Rotation from player side",
					"<html>Y axis rotation (S/W/N/E)<br><small>Rotation from block face",
					"<html>D/U/N/S/W/E rotation<br><small>Rotation from block face",
					"<html>Log rotation (X/Y/Z)<br><small>Imitates vanilla log rotation" });
	private final JCheckBox enablePitch = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> destroyTool = new JComboBox<>(
			new String[] { "Not specified", "pickaxe", "axe", "shovel", "hoe" });

	private final JComboBox<String> vanillaToolTier = new JComboBox<>(
			new String[] { "NONE", "STONE", "IRON", "DIAMOND" });

	private ProcedureSelector additionalHarvestCondition;

	private final JCheckBox requiresCorrectTool = L10N.checkbox("elementgui.common.enable");

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model singleTexture = new Model.BuiltInModel("Single texture");
	private final Model cross = new Model.BuiltInModel("Cross model");
	private final Model crop = new Model.BuiltInModel("Crop model");
	private final Model grassBlock = new Model.BuiltInModel("Grass block");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(
			new Model[] { normal, singleTexture, cross, crop, grassBlock });

	private JBlockPropertiesStatesList blockStates;
	private final Map<?, ?> blockBaseProperties = Objects.requireNonNullElse(
			(Map<?, ?>) mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getDefinitionsProvider()
					.getModElementDefinition(modElement.getType()).get("block_base_properties"),
			Collections.emptyMap());

	private final JComboBox<String> transparencyType = new JComboBox<>(
			new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" });

	private final JCheckBox hasInventory = L10N.checkbox("elementgui.block.has_inventory");

	private final JCheckBox openGUIOnRightClick = L10N.checkbox("elementgui.common.enable");
	private SingleModElementSelector guiBoundTo;

	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(99, 1, 1024, 1));
	private final JCheckBox inventoryDropWhenDestroyed = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox inventoryComparatorPower = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector inventoryAutomationTakeCondition;
	private ProcedureSelector inventoryAutomationPlaceCondition;

	private final VTextField outSlotIDs = new VTextField(18);
	private final VTextField inSlotIDs = new VTextField(18);

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page3group = new ValidationGroup();

	private final SearchableComboBox<String> blockBase = new SearchableComboBox<>(
			new String[] { "Default basic block", "Stairs", "Slab", "Fence", "Wall", "Leaves", "TrapDoor", "Pane",
					"Door", "FenceGate", "EndRod", "PressurePlate", "Button" });
	private final JComboBox<String> blockSetType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("OAK", "elementgui.block.block_set_type.oak"),
			Map.entry("STONE", "elementgui.block.block_set_type.stone"),
			Map.entry("IRON", "elementgui.block.block_set_type.iron")
			//@formatter:on
	);

	private final JCheckBox ignitedByLava = L10N.checkbox("elementgui.common.enable");
	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	private final JCheckBox useLootTableForDrops = L10N.checkbox("elementgui.common.use_table_loot_drops");

	private JBlockEntityAnimationList animations;

	public BlockGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		destroyTool.setRenderer(new ItemTexturesComboBoxRenderer());

		blocksToReplace = new MCItemListField(mcreator, ElementUtil::loadBlocksAndTags, false, true);

		restrictionBiomes = new BiomeListField(mcreator, true);
		restrictionBiomes.setValidator(new ItemListFieldSingleTagValidator(restrictionBiomes));

		fluidRestrictions = new FluidListField(mcreator);

		boundingBoxList = new JBoundingBoxList(mcreator, this, renderType::getSelectedItem);

		guiBoundTo = new SingleModElementSelector(mcreator, ModElementType.GUI);
		guiBoundTo.setDefaultText(L10N.t("elementgui.common.no_gui"));

		blocksToReplace.setListElements(List.of(new MItemBlock(mcreator.getWorkspace(), "TAG:stone_ore_replaceables")));

		generateFeature.setOpaque(false);

		vibrationalEvents = new GameEventListField(mcreator, true);

		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator,
				L10N.t("elementgui.block.event_on_block_added"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/blockstate:blockstate/oldState:blockstate/moving:logic"));
		onNeighbourBlockChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				L10N.t("elementgui.common.event_on_neighbour_block_changes"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator,
				L10N.t("elementgui.common.event_on_update_tick"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onRandomUpdateEvent = new ProcedureSelector(this.withEntry("block/display_tick_update"), mcreator,
				L10N.t("elementgui.common.event_on_random_update"), ProcedureSelector.Side.CLIENT,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onDestroyedByPlayer = new ProcedureSelector(this.withEntry("block/when_destroyed_player"), mcreator,
				L10N.t("elementgui.block.event_on_block_destroyed_by_player"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				L10N.t("elementgui.block.event_on_block_destroyed_by_explosion"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onStartToDestroy = new ProcedureSelector(this.withEntry("block/when_destroy_start"), mcreator,
				L10N.t("elementgui.block.event_on_player_starts_destroy"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				L10N.t("elementgui.block.event_on_entity_collides"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onEntityWalksOn = new ProcedureSelector(this.withEntry("block/when_entity_walks_on"), mcreator,
				L10N.t("elementgui.block.event_on_entity_walks_on"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onBlockPlayedBy = new ProcedureSelector(this.withEntry("block/when_block_placed_by"), mcreator,
				L10N.t("elementgui.common.event_on_block_placed_by"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/blockstate:blockstate"));
		onRightClicked = new ProcedureSelector(this.withEntry("block/when_right_clicked"), mcreator,
				L10N.t("elementgui.block.event_on_right_clicked"), VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/direction:direction/blockstate:blockstate/hitX:number/hitY:number/hitZ:number")).makeReturnValueOptional();
		onRedstoneOn = new ProcedureSelector(this.withEntry("block/on_redstone_on"), mcreator,
				L10N.t("elementgui.block.event_on_redstone_on"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onRedstoneOff = new ProcedureSelector(this.withEntry("block/on_redstone_off"), mcreator,
				L10N.t("elementgui.block.event_on_redstone_off"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onHitByProjectile = new ProcedureSelector(this.withEntry("block/on_hit_by_projectile"), mcreator,
				L10N.t("elementgui.common.event_on_block_hit_by_projectile"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/direction:direction/blockstate:blockstate/hitX:number/hitY:number/hitZ:number"));
		onBonemealSuccess = new ProcedureSelector(this.withEntry("block/on_bonemeal_success"), mcreator,
				L10N.t("elementgui.common.event_on_bonemeal_success"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).makeInline();

		emittedRedstonePower = new NumberProcedureSelector(this.withEntry("block/redstone_power"), mcreator,
				L10N.t("elementgui.block.redstone_power"), AbstractProcedureSelector.Side.BOTH,
				new JSpinner(new SpinnerNumberModel(15, 0, 15, 1)), 130, Dependency.fromString(
				"x:number/y:number/z:number/world:world/direction:direction/blockstate:blockstate"));

		specialInformation = new StringListProcedureSelector(this.withEntry("block/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));

		placingCondition = new ProcedureSelector(this.withEntry("block/placing_condition"), mcreator,
				L10N.t("elementgui.block.event_placing_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();
		isBonemealTargetCondition = new ProcedureSelector(this.withEntry("block/bonemeal_target_condition"), mcreator,
				L10N.t("elementgui.common.event_is_bonemeal_target"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/blockstate:blockstate/clientSide:logic")).makeInline();
		bonemealSuccessCondition = new ProcedureSelector(this.withEntry("block/bonemeal_success_condition"), mcreator,
				L10N.t("elementgui.common.event_bonemeal_success_condition"), ProcedureSelector.Side.SERVER, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).makeInline();
		additionalHarvestCondition = new ProcedureSelector(this.withEntry("block/event_additional_harvest_condition"),
				mcreator, L10N.t("elementgui.block.event_additional_harvest_condition"),
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/entity:entity/world:world/blockstate:blockstate")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		inventoryAutomationTakeCondition = new ProcedureSelector(
				this.withEntry("block/inventory_automation_take_condition"), mcreator,
				L10N.t("elementgui.block.inventory_automation_take_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("index:number/itemstack:itemstack/direction:direction")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();
		inventoryAutomationPlaceCondition = new ProcedureSelector(
				this.withEntry("block/inventory_automation_place_condition"), mcreator,
				L10N.t("elementgui.block.inventory_automation_place_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("index:number/itemstack:itemstack/direction:direction")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();
		vibrationSensitivityRadius = new NumberProcedureSelector(this.withEntry("block/vibration_sensitivity_radius"), mcreator,
				L10N.t("elementgui.block.vibration_sensitivity_radius"), AbstractProcedureSelector.Side.SERVER,
				new JSpinner(new SpinnerNumberModel(7, 0, Integer.MAX_VALUE, 1)), 130, Dependency.fromString(
				"x:number/y:number/z:number/world:world/blockstate:blockstate"));
		canReceiveVibrationCondition = new ProcedureSelector(this.withEntry("block/receive_vibration_condition"), mcreator,
				L10N.t("elementgui.block.receive_vibration_condition"), AbstractProcedureSelector.Side.SERVER, true,
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/blockstate:blockstate/entity:entity/vibrationX:number/vibrationY:number/vibrationZ:number")).setDefaultName(
				L10N.t("condition.common.true")).makeInline();
		onReceivedVibration = new ProcedureSelector(this.withEntry("block/on_received_vibration"), mcreator,
				L10N.t("elementgui.block.on_received_vibration"), AbstractProcedureSelector.Side.SERVER, true,
				Dependency.fromString(
				"x:number/y:number/z:number/world:world/blockstate:blockstate/entity:entity/sourceentity:entity/vibrationX:number/vibrationY:number/vibrationZ:number/distance:number")).makeInline();

		blockStates = new JBlockPropertiesStatesList(mcreator, this, this::nonUserProvidedProperties);
		blockStates.setPreferredSize(new Dimension(0, 0)); // prevent resizing beyond the editor tab

		animations = new JBlockEntityAnimationList(mcreator, this);

		blockBase.addActionListener(e -> {
			boolean hasBlockBase = blockBase.getSelectedItem() != null && blockBase.getSelectedIndex() != 0;
			renderType.setEnabled(!hasBlockBase);
			disableOffset.setEnabled(!hasBlockBase);
			boundingBoxList.setEnabled(!hasBlockBase);
			rotationMode.setEnabled(!hasBlockBase);
			isWaterloggable.setEnabled(!hasBlockBase);
			hasGravity.setEnabled(!hasBlockBase);
			transparencyType.setEnabled(true);
			hasTransparency.setEnabled(true);
			connectedSides.setEnabled(true);
			blockSetType.setEnabled(false);

			if (hasBlockBase) {
				rotationMode.setSelectedIndex(0);
				renderType.setSelectedItem(singleTexture);
				isWaterloggable.setSelected(false);
				hasGravity.setSelected(false);

				String selectedBlockBase = blockBase.getSelectedItem();
				switch (selectedBlockBase) {
				case "Pane" -> {
					connectedSides.setEnabled(false);
					connectedSides.setSelected(false);

					if (!isEditingMode()) {
						transparencyType.setSelectedItem("CUTOUT_MIPPED");
						lightOpacity.setValue(0);
					}
				}
				case "Leaves" -> {
					hasTransparency.setEnabled(false);
					transparencyType.setEnabled(false);

					hasTransparency.setSelected(false);
					transparencyType.setSelectedItem("SOLID");

					if (!isEditingMode()) {
						lightOpacity.setValue(1);
					}
				}
				case "PressurePlate", "TrapDoor", "Door", "Button", "Fence" -> {
					blockSetType.setEnabled(true);
					if (!isEditingMode()) {
						lightOpacity.setValue(0);
						hasTransparency.setSelected(true);
					}
				}
				default -> {
					if (!isEditingMode()) {
						lightOpacity.setValue(0);
						if (selectedBlockBase.equals("Wall") || selectedBlockBase.equals("FenceGate") ||
								selectedBlockBase.equals("EndRod")) {
							hasTransparency.setSelected(true);
						}
					}
				}
				}
			}

			updateTextureOptions();
		});

		renderType.addActionListener(e -> updateTextureOptions());

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));
		JPanel pane7 = new JPanel(new BorderLayout(10, 10));
		JPanel pane8 = new JPanel(new BorderLayout(10, 10));
		JPanel pane9 = new JPanel(new BorderLayout(10, 10));
		JPanel bbPane = new JPanel(new BorderLayout(10, 10));
		JPanel bsPane = new JPanel(new BorderLayout(10, 10));
		JPanel animationsPane = new JPanel(new BorderLayout(0, 0));

		pane8.setOpaque(false);

		itemTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32);
		particleTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK), 32);

		itemTexture.setOpaque(false);
		particleTexture.setOpaque(false);

		isReplaceable.setOpaque(false);
		canProvidePower.setOpaque(false);

		JPanel txblock4 = new JPanel(new BorderLayout());
		txblock4.setOpaque(false);
		txblock4.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.block_base_item_texture"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		txblock4.add("Center", PanelUtils.gridElements(4, 2, 2, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("block/base"), L10N.label("elementgui.block.block_base")),
				blockBase, HelpUtils.wrapWithHelpButton(this.withEntry("block/block_set_type"),
						L10N.label("elementgui.block.block_set_type")), blockSetType,
				HelpUtils.wrapWithHelpButton(this.withEntry("block/item_texture"),
						L10N.label("elementgui.block.item_texture")), PanelUtils.centerInPanel(itemTexture),
				HelpUtils.wrapWithHelpButton(this.withEntry("block/particle_texture"),
						L10N.label("elementgui.block.particle_texture")), PanelUtils.centerInPanel(particleTexture)));

		blockSetType.setEnabled(false);
		plantsGrowOn.setOpaque(false);

		textures = new BlockTexturesSelector(mcreator);

		JPanel sbbp2 = new JPanel(new BorderLayout(1, 5));
		sbbp2.setOpaque(false);

		JPanel modelSettings = new JPanel(new GridLayout(1, 2, 0, 2));
		modelSettings.setOpaque(false);
		modelSettings.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/model"), L10N.label("elementgui.block.model")));
		modelSettings.add(renderType);

		renderType.setPreferredSize(new Dimension(300, 42));

		JComponent sbbp22 = PanelUtils.northAndCenterElement(modelSettings, PanelUtils.totalCenterInPanel(textures), 15,
				15);

		sbbp22.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.block_textures_and_model"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JPanel topnbot = new JPanel(new BorderLayout());
		topnbot.setOpaque(false);
		topnbot.add("Center", sbbp22);

		JComponent txblock3 = PanelUtils.gridElements(1, 1, specialInformation);
		txblock3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.special_information"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		sbbp2.add("Center", topnbot);

		JPanel render = new JPanel();
		render.setLayout(new BoxLayout(render, BoxLayout.PAGE_AXIS));

		ComponentUtils.deriveFont(transparencyType, 16);
		ComponentUtils.deriveFont(blockBase, 16);
		ComponentUtils.deriveFont(blockSetType, 16);
		ComponentUtils.deriveFont(tintType, 16);

		JPanel visualRenderingSettings = new JPanel(new GridLayout(6, 2, 0, 2));
		visualRenderingSettings.setOpaque(false);

		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/has_transparency"),
				L10N.label("elementgui.block.has_transparency")));
		visualRenderingSettings.add(hasTransparency);

		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/transparency_type"),
				L10N.label("elementgui.block.transparency_type")));
		visualRenderingSettings.add(transparencyType);

		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/connected_sides"),
				L10N.label("elementgui.block.connected_sides")));
		visualRenderingSettings.add(connectedSides);

		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fluid_overlay"),
				L10N.label("elementgui.block.fluid_overlay")));
		visualRenderingSettings.add(displayFluidOverlay);

		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tint_type"),
				L10N.label("elementgui.common.tint_type")));
		visualRenderingSettings.add(tintType);
		visualRenderingSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_item_tinted"),
				L10N.label("elementgui.block.is_item_tinted")));
		visualRenderingSettings.add(isItemTinted);

		ComponentUtils.deriveFont(renderType, 16);
		ComponentUtils.deriveFont(rotationMode, 16);

		JPanel rent = new JPanel(new GridLayout(3, 2, 0, 2));
		rent.setOpaque(false);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/rotation_mode"),
				L10N.label("elementgui.block.rotation_mode")));
		rent.add(rotationMode);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/enable_pitch"),
				L10N.label("elementgui.block.enable_pitch")));
		rent.add(enablePitch);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_waterloggable"),
				L10N.label("elementgui.block.is_waterloggable")));
		rent.add(isWaterloggable);

		rotationMode.setPreferredSize(new Dimension(320, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		enablePitch.setOpaque(false);
		enablePitch.setEnabled(false);
		rotationMode.addActionListener(e -> {
			enablePitch.setEnabled(rotationMode.getSelectedIndex() == 1 || rotationMode.getSelectedIndex() == 3);
			if (!enablePitch.isEnabled())
				enablePitch.setSelected(false);
		});

		isItemTinted.setOpaque(false);

		topnbot.add("South", txblock4);

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.builtin_states"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		visualRenderingSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.visual_rendering"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		render.add(rent);
		render.add(visualRenderingSettings);
		render.add(txblock3);

		render.setOpaque(false);

		hasTransparency.setOpaque(false);
		connectedSides.setOpaque(false);
		emissiveRendering.setOpaque(false);
		displayFluidOverlay.setOpaque(false);

		sbbp2.add("East", PanelUtils.pullElementUp(render));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel northPanel = new JPanel(new GridLayout(1, 2, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/disable_offset"),
				L10N.label("elementgui.common.disable_offset")));
		northPanel.add(disableOffset);
		disableOffset.setOpaque(false);

		bbPane.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), boundingBoxList));
		bbPane.setOpaque(false);

		bbPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		if (!isEditingMode()) // add first bounding box
			boundingBoxList.setEntries(Collections.singletonList(new IBlockWithBoundingBox.BoxEntry()));

		boundingBoxList.addPropertyChangeListener("boundingBoxChanged", e -> updateParametersBasedOnBoundingBoxSize());

		bsPane.setOpaque(false);
		bsPane.add("Center", blockStates);

		JPanel selp = new JPanel(new GridLayout(13, 2, 0, 2));
		JPanel selp3 = new JPanel(new GridLayout(8, 2, 0, 2));
		JPanel soundProperties = new JPanel(new GridLayout(7, 2, 0, 2));

		JPanel advancedProperties = new JPanel(new GridLayout(14, 2, 0, 2));

		hasGravity.setOpaque(false);
		tickRandomly.setOpaque(false);
		ignitedByLava.setOpaque(false);
		unbreakable.setOpaque(false);
		useLootTableForDrops.setOpaque(false);
		requiresCorrectTool.setOpaque(false);
		destroyTool.addActionListener(e -> updateRequiresCorrectTool());

		selp3.setOpaque(false);
		advancedProperties.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		hardness.setOpaque(false);
		resistance.setOpaque(false);
		lightOpacity.setOpaque(false);
		isNotColidable.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.common.creative_tabs")));
		selp.add(creativeTabs);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hardness"),
				L10N.label("elementgui.common.hardness")));
		selp.add(hardness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"),
				L10N.label("elementgui.common.resistance")));
		selp.add(resistance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/slipperiness"),
				L10N.label("elementgui.block.slipperiness")));
		selp.add(slipperiness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/jump_factor"),
				L10N.label("elementgui.block.jump_factor")));
		selp.add(jumpFactor);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/speed_factor"),
				L10N.label("elementgui.block.speed_factor")));
		selp.add(speedFactor);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"),
				L10N.label("elementgui.common.luminance")));
		selp.add(luminance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/light_opacity"),
				L10N.label("elementgui.common.light_opacity")));
		selp.add(lightOpacity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/has_gravity"),
				L10N.label("elementgui.block.has_gravity")));
		selp.add(hasGravity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/can_walk_through"),
				L10N.label("elementgui.block.can_walk_through")));
		selp.add(isNotColidable);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				L10N.label("elementgui.common.emissive_rendering")));
		selp.add(emissiveRendering);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/replaceable"),
				L10N.label("elementgui.block.is_replaceable")));
		selp.add(isReplaceable);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				L10N.label("elementgui.common.custom_drop")));
		selp3.add(PanelUtils.centerInPanel(customDrop));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"),
				L10N.label("elementgui.common.drop_amount")));
		selp3.add(dropAmount);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				L10N.label("elementgui.common.use_loot_table_for_drop")));
		selp3.add(useLootTableForDrops);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"),
				L10N.label("elementgui.common.creative_pick_item")));
		selp3.add(PanelUtils.centerInPanel(creativePickItem));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/requires_correct_tool"),
				L10N.label("elementgui.block.requires_correct_tool")));
		selp3.add(requiresCorrectTool);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/harvest_tool"),
				L10N.label("elementgui.block.harvest_tool")));
		selp3.add(destroyTool);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/vanilla_tool_tier"),
				L10N.label("elementgui.block.vanilla_tool_tier")));
		selp3.add(vanillaToolTier);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/unbreakable"),
				L10N.label("elementgui.block.is_unbreakable")));
		selp3.add(unbreakable);

		ButtonGroup bg = new ButtonGroup();
		bg.add(defaultSoundType);
		bg.add(customSoundType);
		defaultSoundType.setSelected(true);
		defaultSoundType.setOpaque(false);
		customSoundType.setOpaque(false);

		defaultSoundType.addActionListener(event -> updateSoundType());
		customSoundType.addActionListener(event -> updateSoundType());

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/block_sound"), defaultSoundType));
		soundProperties.add(soundOnStep);

		soundProperties.add(PanelUtils.join(FlowLayout.LEFT, customSoundType));
		soundProperties.add(new JEmptyBox());

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/break_sound"),
				L10N.label("elementgui.common.soundtypes.break_sound")));
		soundProperties.add(breakSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fall_sound"),
				L10N.label("elementgui.common.soundtypes.fall_sound")));
		soundProperties.add(fallSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hit_sound"),
				L10N.label("elementgui.common.soundtypes.hit_sound")));
		soundProperties.add(hitSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/place_sound"),
				L10N.label("elementgui.common.soundtypes.place_sound")));
		soundProperties.add(placeSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/step_sound"),
				L10N.label("elementgui.common.soundtypes.step_sound")));
		soundProperties.add(stepSound);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tick_rate"),
				L10N.label("elementgui.common.tick_rate")));
		advancedProperties.add(tickRate);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tick_randomly"),
				L10N.label("elementgui.block.tick_randomly")));
		advancedProperties.add(tickRandomly);

		tickRandomly.addActionListener(e -> tickRate.setEnabled(!tickRandomly.isSelected()));

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/color_on_map"),
				L10N.label("elementgui.block.color_on_map")));
		advancedProperties.add(colorOnMap);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/note_block_instrument"),
				L10N.label("elementgui.block.note_block_instrument")));
		advancedProperties.add(noteBlockInstrument);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/can_plants_grow"),
				L10N.label("elementgui.block.can_plants_grow")));
		advancedProperties.add(plantsGrowOn);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/beacon_color_modifier"),
				L10N.label("elementgui.block.beacon_color_modifier")));
		advancedProperties.add(beaconColorModifier);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_ladder"),
				L10N.label("elementgui.block.is_ladder")));
		advancedProperties.add(isLadder);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/enchantments_bonus"),
				L10N.label("elementgui.block.enchantments_bonus")));
		advancedProperties.add(enchantPowerBonus);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/ignited_by_lava"),
				L10N.label("elementgui.block.ignited_by_lava")));
		advancedProperties.add(ignitedByLava);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/flammability"),
				L10N.label("elementgui.block.flammability")));
		advancedProperties.add(flammability);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				L10N.label("elementgui.common.fire_spread_speed")));
		advancedProperties.add(fireSpreadSpeed);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/push_reaction"),
				L10N.label("elementgui.block.push_reaction")));
		advancedProperties.add(reactionToPushing);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/ai_path_node_type"),
				L10N.label("elementgui.common.ai_path_node_type")));
		advancedProperties.add(aiPathNodeType);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/offset_type"),
				L10N.label("elementgui.common.offset_type")));
		advancedProperties.add(offsetType);

		JComponent advancedWithCondition = PanelUtils.northAndCenterElement(advancedProperties, placingCondition, 5, 2);

		isWaterloggable.setOpaque(false);
		canRedstoneConnect.setOpaque(false);
		isBonemealable.setOpaque(false);
		isLadder.setOpaque(false);

		useLootTableForDrops.addActionListener(e -> {
			customDrop.setEnabled(!useLootTableForDrops.isSelected());
			dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		});

		isWaterloggable.addActionListener(e -> {
			hasGravity.setEnabled(!isWaterloggable.isSelected());
			if (isWaterloggable.isSelected()) {
				hasGravity.setSelected(false);
			}
		});

		selp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties_general"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		soundProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties_sound"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		advancedWithCondition.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.properties_advanced_block"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), Theme.current().getForegroundColor()));

		selp.setOpaque(false);
		soundProperties.setOpaque(false);

		JComponent selpWrap = PanelUtils.centerAndSouthElement(selp3, additionalHarvestCondition);
		selpWrap.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties_dropping"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		pane3.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(PanelUtils.pullElementUp(selp),
						PanelUtils.centerAndSouthElement(selpWrap, soundProperties))));
		pane3.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(4, 5, 5, 5));
		events.setOpaque(false);

		events.add(onRightClicked);
		events.add(onBlockAdded);
		events.add(onNeighbourBlockChanges);
		events.add(onTickUpdate);
		events.add(onDestroyedByPlayer);
		events.add(onDestroyedByExplosion);
		events.add(onStartToDestroy);
		events.add(onEntityCollides);
		events.add(onEntityWalksOn);
		events.add(onHitByProjectile);
		events.add(onBlockPlayedBy);
		events.add(onRedstoneOn);
		events.add(onRedstoneOff);
		events.add(onRandomUpdateEvent);

		pane4.add("Center", PanelUtils.totalCenterInPanel(events));

		pane4.setOpaque(false);

		JPanel invblock = new JPanel(new BorderLayout(10, 40));
		invblock.setOpaque(false);

		hasInventory.setOpaque(false);
		openGUIOnRightClick.setOpaque(false);

		inventorySize.setOpaque(false);
		inventoryStackSize.setOpaque(false);
		inventoryDropWhenDestroyed.setOpaque(false);
		inventoryComparatorPower.setOpaque(false);
		inventoryDropWhenDestroyed.setSelected(true);
		inventoryComparatorPower.setSelected(true);

		JPanel props = new JPanel(new GridLayout(8, 2, 0, 2));
		props.setOpaque(false);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bind_gui"),
				L10N.label("elementgui.block.bind_gui")));
		props.add(guiBoundTo);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bind_gui_open"),
				L10N.label("elementgui.block.bind_gui_open")));
		props.add(openGUIOnRightClick);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/inventory_size"),
				L10N.label("elementgui.block.inventory_size")));
		props.add(inventorySize);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/inventory_stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		props.add(inventoryStackSize);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_inventory_items"),
				L10N.label("elementgui.block.drop_inventory_items")));
		props.add(inventoryDropWhenDestroyed);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/comparator_data"),
				L10N.label("elementgui.block.comparator_data")));
		props.add(inventoryComparatorPower);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/input_slots"),
				L10N.label("elementgui.block.input_slots")));
		props.add(inSlotIDs);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/output_slots"),
				L10N.label("elementgui.block.output_slots")));
		props.add(outSlotIDs);

		ComponentUtils.deriveFont(outSlotIDs, 16);
		outSlotIDs.setValidator(new CommaSeparatedNumbersValidator(outSlotIDs));
		outSlotIDs.enableRealtimeValidation();

		ComponentUtils.deriveFont(inSlotIDs, 16);
		inSlotIDs.setValidator(new CommaSeparatedNumbersValidator(inSlotIDs));
		inSlotIDs.enableRealtimeValidation();

		guiBoundTo.addEntrySelectedListener(e -> {
			if (!isEditingMode()) {
				String selected = guiBoundTo.getEntry();
				if (selected != null) {
					ModElement element = mcreator.getWorkspace().getModElementByName(selected);
					if (element != null) {
						GeneratableElement generatableElement = element.getGeneratableElement();
						if (generatableElement instanceof GUI gui) {
							inventorySize.setValue(gui.getMaxSlotID() + 1);
							StringBuilder inslots = new StringBuilder();
							StringBuilder outslots = new StringBuilder();
							for (GUIComponent slot : gui.components)
								if (slot instanceof InputSlot)
									inslots.append(((Slot) slot).id).append(",");
								else if (slot instanceof OutputSlot)
									outslots.append(((Slot) slot).id).append(",");
							inSlotIDs.setText(inslots.toString().replaceAll(",$", ""));
							outSlotIDs.setText(outslots.toString().replaceAll(",$", ""));
						}
					}
				}
			}
		});

		JPanel pane10 = new JPanel(new BorderLayout(10, 10));
		pane10.setOpaque(false);

		JPanel energyStorage = new JPanel(new GridLayout(5, 2, 10, 2));
		JPanel fluidTank = new JPanel(new GridLayout(3, 2, 10, 2));

		energyStorage.setOpaque(false);
		fluidTank.setOpaque(false);

		energyStorage.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.energy_storage"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		fluidTank.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.fluid_tank"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		hasEnergyStorage.setOpaque(false);
		isFluidTank.setOpaque(false);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/enable_storage"),
				L10N.label("elementgui.block.enable_storage_energy")));
		energyStorage.add(hasEnergyStorage);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/initial_energy"),
				L10N.label("elementgui.block.initial_energy")));
		energyStorage.add(energyInitial);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/energy_capacity"),
				L10N.label("elementgui.block.energy_max_capacity")));
		energyStorage.add(energyCapacity);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/max_receive"),
				L10N.label("elementgui.block.energy_max_receive")));
		energyStorage.add(energyMaxReceive);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/max_extract"),
				L10N.label("elementgui.block.energy_max_extract")));
		energyStorage.add(energyMaxExtract);

		fluidTank.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluidtank/enable_storage"),
				L10N.label("elementgui.block.enable_storage_fluid")));
		fluidTank.add(isFluidTank);

		fluidTank.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluidtank/fluid_capacity"),
				L10N.label("elementgui.block.fluid_capacity")));
		fluidTank.add(fluidCapacity);

		fluidTank.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluidtank/fluid_restrictions"),
				L10N.label("elementgui.block.fluid_restrictions")));
		fluidTank.add(fluidRestrictions);

		pane10.add(PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(L10N.label("elementgui.block.tile_entity_tip"),
						PanelUtils.westAndEastElement(energyStorage,
								PanelUtils.northAndCenterElement(fluidTank, new JEmptyBox())), 10, 10)));

		hasInventory.addActionListener(e -> refreshFieldsTileEntity());
		refreshFieldsTileEntity();

		JPanel invpropsbottom = new JPanel(new GridLayout(2, 1, 0, 2));
		invpropsbottom.setOpaque(false);
		invpropsbottom.add(inventoryAutomationTakeCondition);
		invpropsbottom.add(inventoryAutomationPlaceCondition);

		JPanel vibrationPanel = new JPanel(new GridLayout(2, 2, 0, 2));
		vibrationPanel.setOpaque(false);

		sensitiveToVibration.setOpaque(false);

		vibrationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/sensitive_to_vibration"),
				L10N.label("elementgui.block.sensitive_to_vibration")));
		vibrationPanel.add(sensitiveToVibration);

		vibrationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/vibrational_events"),
				L10N.label("elementgui.block.vibrational_events")));
		vibrationPanel.add(vibrationalEvents);

		vibrationalEvents.setPreferredSize(new Dimension(280, 0));

		JPanel vibrationEvents = new JPanel(new BorderLayout(0, 2));
		JPanel vibrationEventsBottom = new JPanel(new GridLayout(2, 1, 0, 2));

		vibrationEventsBottom.setOpaque(false);
		vibrationEventsBottom.add(canReceiveVibrationCondition);
		vibrationEventsBottom.add(onReceivedVibration);

		vibrationEvents.setOpaque(false);
		vibrationEvents.add("North", vibrationSensitivityRadius);
		vibrationEvents.add("Center", vibrationEventsBottom);

		JComponent vibrationMerger = PanelUtils.northAndCenterElement(vibrationPanel, vibrationEvents, 2, 2);
		vibrationMerger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.properties_vibration"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JComponent invpropsall = PanelUtils.centerAndSouthElement(props, invpropsbottom, 2, 2);

		invpropsall.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.settings_inventory"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		invblock.add("Center", PanelUtils.westAndEastElement(invpropsall, PanelUtils.pullElementUp(vibrationMerger)));

		invblock.add("North", HelpUtils.wrapWithHelpButton(this.withEntry("block/has_inventory"), hasInventory));

		pane8.add("Center", PanelUtils.totalCenterInPanel(invblock));

		JPanel genPanel = new JPanel(new GridLayout(7, 2, 20, 2));

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/generate_feature"),
				L10N.label("elementgui.block.generate_feature")));
		genPanel.add(generateFeature);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_replace_blocks"),
				L10N.label("elementgui.block.gen_replace_blocks")));
		genPanel.add(blocksToReplace);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		genPanel.add(restrictionBiomes);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/generation_shape"),
				L10N.label("elementgui.block.generation_shape")));
		genPanel.add(generationShape);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_chunk_count"),
				L10N.label("elementgui.block.gen_chunck_count")));
		genPanel.add(frequencyPerChunks);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_group_size"),
				L10N.label("elementgui.block.gen_group_size")));
		genPanel.add(frequencyOnChunk);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_height"),
				L10N.label("elementgui.block.gen_height")));
		genPanel.add(generateHeight);

		genPanel.setOpaque(false);

		JPanel redstoneParameters = new JPanel(new GridLayout(2, 2, 0, 2));
		redstoneParameters.setOpaque(false);

		redstoneParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/redstone_connect"),
				L10N.label("elementgui.block.redstone_connect")));
		redstoneParameters.add(canRedstoneConnect);

		redstoneParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emits_redstone"),
				L10N.label("elementgui.block.emits_redstone")));
		redstoneParameters.add(canProvidePower);

		JComponent redstoneMerger = PanelUtils.northAndCenterElement(redstoneParameters, emittedRedstonePower, 2, 2);

		redstoneMerger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.properties_redstone"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		canProvidePower.addActionListener(e -> refreshRedstoneEmitted());
		refreshRedstoneEmitted();

		JPanel bonemealPanel = new JPanel(new GridLayout(1, 2, 0, 2));
		bonemealPanel.setOpaque(false);

		bonemealPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_bonemealable"),
				L10N.label("elementgui.common.is_bonemealable")));
		bonemealPanel.add(isBonemealable);

		JPanel bonemealEvents = new JPanel(new GridLayout(3, 1, 0, 2));
		bonemealEvents.setOpaque(false);

		bonemealEvents.add(isBonemealTargetCondition);
		bonemealEvents.add(bonemealSuccessCondition);
		bonemealEvents.add(onBonemealSuccess);

		JComponent bonemealMerger = PanelUtils.northAndCenterElement(bonemealPanel, bonemealEvents, 2, 2);
		bonemealMerger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.properties_bonemeal"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		isBonemealable.addActionListener(e -> refreshBonemealProperties());
		refreshBonemealProperties();

		renderType.addActionListener(e -> {
			Model selected = renderType.getSelectedItem();
			if (selected != null) {
				boundingBoxList.modelChanged();
				if (!selected.equals(normal) && !selected.equals(singleTexture) && !selected.equals(grassBlock)) {
					hasTransparency.setSelected(true);
					lightOpacity.setValue(0);
				}
				if (!isEditingMode() && selected.equals(grassBlock)) {
					transparencyType.setSelectedItem("CUTOUT_MIPPED");
				}
			}
		});

		pane7.add(PanelUtils.totalCenterInPanel(PanelUtils.westAndEastElement(advancedWithCondition,
				PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(redstoneMerger, bonemealMerger)))));

		pane7.setOpaque(false);
		pane9.setOpaque(false);

		JComponent genPanelWithChunk = PanelUtils.westAndCenterElement(new JLabel(UIRES.get("chunk")),
				PanelUtils.pullElementUp(genPanel), 25, 0);
		pane9.add("Center", PanelUtils.totalCenterInPanel(genPanelWithChunk));

		JComponent animationsList = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/model_animations"),
						L10N.label("elementgui.block.model_animations")), animations);
		animationsList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		animationsPane.setOpaque(false);
		animationsPane.add("Center", animationsList);

		page1group.addValidationElement(textures);
		page1group.addValidationElement(itemTexture);

		itemTexture.setValidator(new TextureSelectionButtonValidator(itemTexture, () -> {
			Model model = renderType.getSelectedItem();
			return model != null && model.getType() == Model.Type.JAVA;
		}));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.block.error_block_must_have_name")));
		name.enableRealtimeValidation();

		page3group.addValidationElement(name);

		breakSound.getVTextField().setValidator(new ConditionalTextFieldValidator(breakSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		fallSound.getVTextField().setValidator(new ConditionalTextFieldValidator(fallSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		hitSound.getVTextField().setValidator(new ConditionalTextFieldValidator(hitSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		placeSound.getVTextField().setValidator(new ConditionalTextFieldValidator(placeSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		stepSound.getVTextField().setValidator(new ConditionalTextFieldValidator(stepSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));

		page3group.addValidationElement(breakSound.getVTextField());
		page3group.addValidationElement(fallSound.getVTextField());
		page3group.addValidationElement(hitSound.getVTextField());
		page3group.addValidationElement(placeSound.getVTextField());
		page3group.addValidationElement(stepSound.getVTextField());

		addPage(L10N.t("elementgui.common.page_visual"), pane2).validate(page1group);
		addPage(L10N.t("elementgui.common.page_bounding_boxes"), bbPane, false);
		addPage(L10N.t("elementgui.block.page_states"), bsPane, false).lazyValidate(blockStates::getValidationResult);
		addPage(L10N.t("elementgui.block.page_animations"), animationsPane, false);
		addPage(L10N.t("elementgui.common.page_properties"), pane3).validate(page3group);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), pane7);
		addPage(L10N.t("elementgui.block.page_tile_entity"), pane8).validate(outSlotIDs).validate(inSlotIDs);
		addPage(L10N.t("elementgui.block.page_energy_fluid_storage"), pane10);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);
		addPage(L10N.t("elementgui.common.page_generation"), pane9).validate(restrictionBiomes);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}

		updateSoundType();
	}

	private Collection<String> nonUserProvidedProperties() {
		Set<String> props = new HashSet<>();
		String selBlockBase = blockBase.getSelectedItem();
		if (selBlockBase != null && blockBaseProperties.get(selBlockBase) instanceof List<?> blockBaseProps) {
			for (Object blockBaseProp : blockBaseProps)
				props.add(blockBaseProp.toString());
			return props;
		}
		int modeIndex = rotationMode.getSelectedIndex();
		if (modeIndex == 5) {
			props.add("axis");
		} else if (modeIndex != 0) {
			props.add("facing");
			if ((modeIndex == 1 || modeIndex == 3) && enablePitch.isSelected())
				props.add("face");
		}
		if (isWaterloggable.isSelected())
			props.add("waterlogged");
		return props;
	}

	private void refreshFieldsTileEntity() {
		inventorySize.setEnabled(hasInventory.isSelected());
		inventoryAutomationTakeCondition.setEnabled(hasInventory.isSelected());
		inventoryAutomationPlaceCondition.setEnabled(hasInventory.isSelected());
		inventoryStackSize.setEnabled(hasInventory.isSelected());
		inventoryDropWhenDestroyed.setEnabled(hasInventory.isSelected());
		inventoryComparatorPower.setEnabled(hasInventory.isSelected());
		outSlotIDs.setEnabled(hasInventory.isSelected());
		inSlotIDs.setEnabled(hasInventory.isSelected());
		hasEnergyStorage.setEnabled(hasInventory.isSelected());
		energyInitial.setEnabled(hasInventory.isSelected());
		energyCapacity.setEnabled(hasInventory.isSelected());
		energyMaxReceive.setEnabled(hasInventory.isSelected());
		energyMaxExtract.setEnabled(hasInventory.isSelected());
		isFluidTank.setEnabled(hasInventory.isSelected());
		fluidCapacity.setEnabled(hasInventory.isSelected());
		fluidRestrictions.setEnabled(hasInventory.isSelected());
		sensitiveToVibration.setEnabled(hasInventory.isSelected());
		vibrationSensitivityRadius.setEnabled(hasInventory.isSelected());
		vibrationalEvents.setEnabled(hasInventory.isSelected());
		canReceiveVibrationCondition.setEnabled(hasInventory.isSelected());
		onReceivedVibration.setEnabled(hasInventory.isSelected());
	}

	private void refreshRedstoneEmitted() {
		emittedRedstonePower.setEnabled(canProvidePower.isSelected());
	}

	private void refreshBonemealProperties() {
		isBonemealTargetCondition.setEnabled(isBonemealable.isSelected());
		bonemealSuccessCondition.setEnabled(isBonemealable.isSelected());
		onBonemealSuccess.setEnabled(isBonemealable.isSelected());
	}

	private void updateTextureOptions() {
		if (normal.equals(renderType.getSelectedItem())) {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.ALL);
		} else if (grassBlock.equals(renderType.getSelectedItem())) {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.GRASS);
		} else if ("Pane".equals(blockBase.getSelectedItem()) || "Door".equals(blockBase.getSelectedItem())) {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.TOP_BOTTOM);
		} else if ("Stairs".equals(blockBase.getSelectedItem()) || "Slab".equals(blockBase.getSelectedItem())) {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.TOP_BOTTOM_SIDES);
		} else {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.SINGLE_TEXTURE);
		}

		Model model = renderType.getSelectedItem();
		if (model != null && model.getType() == Model.Type.JAVA) {
			hasInventory.setSelected(true);
			hasInventory.setEnabled(false);
		} else {
			hasInventory.setEnabled(true);
		}

		animations.setEnabled(model != null && model.getType() == Model.Type.JAVA);
	}

	public void updateParametersBasedOnBoundingBoxSize() {
		if (!boundingBoxList.isFullCube()) {
			hasTransparency.setSelected(true);
			hasTransparency.setEnabled(false);
		} else {
			hasTransparency.setSelected(false);
			hasTransparency.setEnabled(true);
		}
	}

	private void updateSoundType() {
		breakSound.setEnabled(customSoundType.isSelected());
		fallSound.setEnabled(customSoundType.isSelected());
		hitSound.setEnabled(customSoundType.isSelected());
		placeSound.setEnabled(customSoundType.isSelected());
		stepSound.setEnabled(customSoundType.isSelected());
		soundOnStep.setEnabled(defaultSoundType.isSelected());
	}

	private void updateRequiresCorrectTool() {
		if (!isEditingMode() && "pickaxe".equals(destroyTool.getSelectedItem())) {
			requiresCorrectTool.setSelected(true);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onBlockAdded.refreshListKeepSelected();
		onNeighbourBlockChanges.refreshListKeepSelected();
		onEntityCollides.refreshListKeepSelected();
		onTickUpdate.refreshListKeepSelected();
		onRandomUpdateEvent.refreshListKeepSelected();
		onDestroyedByPlayer.refreshListKeepSelected();
		onDestroyedByExplosion.refreshListKeepSelected();
		onStartToDestroy.refreshListKeepSelected();
		onEntityWalksOn.refreshListKeepSelected();
		onBlockPlayedBy.refreshListKeepSelected();
		onRightClicked.refreshListKeepSelected();
		onRedstoneOn.refreshListKeepSelected();
		onRedstoneOff.refreshListKeepSelected();
		onHitByProjectile.refreshListKeepSelected();
		onBonemealSuccess.refreshListKeepSelected();
		onReceivedVibration.refreshListKeepSelected();

		specialInformation.refreshListKeepSelected();
		emittedRedstonePower.refreshListKeepSelected();
		isBonemealTargetCondition.refreshListKeepSelected();
		bonemealSuccessCondition.refreshListKeepSelected();
		placingCondition.refreshListKeepSelected();
		additionalHarvestCondition.refreshListKeepSelected();
		vibrationSensitivityRadius.refreshListKeepSelected();
		canReceiveVibrationCondition.refreshListKeepSelected();

		inventoryAutomationTakeCondition.refreshListKeepSelected();
		inventoryAutomationPlaceCondition.refreshListKeepSelected();

		animations.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(renderType,
				ListUtils.merge(Arrays.asList(normal, singleTexture, cross, crop, grassBlock),
						Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
								.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
								.collect(Collectors.toList()), Model.getJavaModels(mcreator.getWorkspace())));

		ComboBoxUtil.updateComboBoxContents(aiPathNodeType,
				Arrays.asList(ElementUtil.getDataListAsStringArray("pathnodetypes")), "DEFAULT");
	}

	@Override public void openInEditingMode(Block block) {
		itemTexture.setTexture(block.itemTexture);
		particleTexture.setTexture(block.particleTexture);
		textures.setTextures(block.texture, block.textureTop, block.textureLeft, block.textureFront, block.textureRight,
				block.textureBack);
		guiBoundTo.setEntry(block.guiBoundTo);
		rotationMode.setSelectedIndex(block.rotationMode);
		enablePitch.setSelected(block.enablePitch);
		blockStates.setProperties(block.customProperties);
		enchantPowerBonus.setValue(block.enchantPowerBonus);
		hasTransparency.setSelected(block.hasTransparency);
		connectedSides.setSelected(block.connectedSides);
		displayFluidOverlay.setSelected(block.displayFluidOverlay);
		hasEnergyStorage.setSelected(block.hasEnergyStorage);
		isFluidTank.setSelected(block.isFluidTank);
		energyInitial.setValue(block.energyInitial);
		energyCapacity.setValue(block.energyCapacity);
		energyMaxReceive.setValue(block.energyMaxReceive);
		energyMaxExtract.setValue(block.energyMaxExtract);
		fluidCapacity.setValue(block.fluidCapacity);
		outSlotIDs.setText(block.inventoryOutSlotIDs.stream().map(String::valueOf).collect(Collectors.joining(",")));
		inSlotIDs.setText(block.inventoryInSlotIDs.stream().map(String::valueOf).collect(Collectors.joining(",")));
		onBlockAdded.setSelectedProcedure(block.onBlockAdded);
		onNeighbourBlockChanges.setSelectedProcedure(block.onNeighbourBlockChanges);
		onTickUpdate.setSelectedProcedure(block.onTickUpdate);
		onRandomUpdateEvent.setSelectedProcedure(block.onRandomUpdateEvent);
		onDestroyedByPlayer.setSelectedProcedure(block.onDestroyedByPlayer);
		onDestroyedByExplosion.setSelectedProcedure(block.onDestroyedByExplosion);
		onStartToDestroy.setSelectedProcedure(block.onStartToDestroy);
		onEntityCollides.setSelectedProcedure(block.onEntityCollides);
		onEntityWalksOn.setSelectedProcedure(block.onEntityWalksOn);
		onBlockPlayedBy.setSelectedProcedure(block.onBlockPlayedBy);
		onRightClicked.setSelectedProcedure(block.onRightClicked);
		onRedstoneOn.setSelectedProcedure(block.onRedstoneOn);
		onRedstoneOff.setSelectedProcedure(block.onRedstoneOff);
		onHitByProjectile.setSelectedProcedure(block.onHitByProjectile);
		name.setText(block.name);
		generationShape.setSelectedItem(block.generationShape);
		generateHeight.setMinValue(block.minGenerateHeight);
		generateHeight.setMaxValue(block.maxGenerateHeight);
		frequencyPerChunks.setValue(block.frequencyPerChunks);
		frequencyOnChunk.setValue(block.frequencyOnChunk);
		specialInformation.setSelectedProcedure(block.specialInformation);
		emittedRedstonePower.setSelectedProcedure(block.emittedRedstonePower);
		additionalHarvestCondition.setSelectedProcedure(block.additionalHarvestCondition);
		hardness.setValue(block.hardness);
		resistance.setValue(block.resistance);
		hasGravity.setSelected(block.hasGravity);
		isWaterloggable.setSelected(block.isWaterloggable);
		emissiveRendering.setSelected(block.emissiveRendering);
		tickRandomly.setSelected(block.tickRandomly);
		creativeTabs.setListElements(block.creativeTabs);
		destroyTool.setSelectedItem(block.destroyTool);
		soundOnStep.setSelectedItem(block.soundOnStep);
		breakSound.setSound(block.breakSound);
		fallSound.setSound(block.fallSound);
		hitSound.setSound(block.hitSound);
		placeSound.setSound(block.placeSound);
		stepSound.setSound(block.stepSound);
		defaultSoundType.setSelected(!block.isCustomSoundType);
		customSoundType.setSelected(block.isCustomSoundType);
		luminance.setValue(block.luminance);
		vanillaToolTier.setSelectedItem(block.vanillaToolTier);
		requiresCorrectTool.setSelected(block.requiresCorrectTool);
		customDrop.setBlock(block.customDrop);
		dropAmount.setValue(block.dropAmount);
		isNotColidable.setSelected(block.isNotColidable);
		unbreakable.setSelected(block.unbreakable);
		canRedstoneConnect.setSelected(block.canRedstoneConnect);
		isBonemealable.setSelected(block.isBonemealable);
		isBonemealTargetCondition.setSelectedProcedure(block.isBonemealTargetCondition);
		bonemealSuccessCondition.setSelectedProcedure(block.bonemealSuccessCondition);
		onBonemealSuccess.setSelectedProcedure(block.onBonemealSuccess);
		lightOpacity.setValue(block.lightOpacity);
		transparencyType.setSelectedItem(block.transparencyType);
		tintType.setSelectedItem(block.tintType);
		isItemTinted.setSelected(block.isItemTinted);
		animations.setEntries(block.animations);

		if (block.blockBase == null) {
			blockBase.setSelectedIndex(0);
		} else {
			blockBase.setSelectedItem(block.blockBase);
		}
		blockSetType.setSelectedItem(block.blockSetType);

		plantsGrowOn.setSelected(block.plantsGrowOn);
		hasInventory.setSelected(block.hasInventory);
		useLootTableForDrops.setSelected(block.useLootTableForDrops);
		openGUIOnRightClick.setSelected(block.openGUIOnRightClick);
		inventoryDropWhenDestroyed.setSelected(block.inventoryDropWhenDestroyed);
		inventoryAutomationTakeCondition.setSelectedProcedure(block.inventoryAutomationTakeCondition);
		inventoryAutomationPlaceCondition.setSelectedProcedure(block.inventoryAutomationPlaceCondition);
		inventoryComparatorPower.setSelected(block.inventoryComparatorPower);
		inventorySize.setValue(block.inventorySize);
		inventoryStackSize.setValue(block.inventoryStackSize);
		tickRate.setValue(block.tickRate);

		generateFeature.setSelected(block.generateFeature);
		blocksToReplace.setListElements(block.blocksToReplace);
		restrictionBiomes.setListElements(block.restrictionBiomes);
		fluidRestrictions.setListElements(block.fluidRestrictions);

		isReplaceable.setSelected(block.isReplaceable);
		canProvidePower.setSelected(block.canProvidePower);
		colorOnMap.setSelectedItem(block.colorOnMap);
		noteBlockInstrument.setSelectedItem(block.noteBlockInstrument);
		offsetType.setSelectedItem(block.offsetType);
		aiPathNodeType.setSelectedItem(block.aiPathNodeType);
		creativePickItem.setBlock(block.creativePickItem);
		placingCondition.setSelectedProcedure(block.placingCondition);

		beaconColorModifier.setColor(block.beaconColorModifier);

		ignitedByLava.setSelected(block.ignitedByLava);
		flammability.setValue(block.flammability);
		fireSpreadSpeed.setValue(block.fireSpreadSpeed);

		isLadder.setSelected(block.isLadder);
		reactionToPushing.setSelectedItem(block.reactionToPushing);
		slipperiness.setValue(block.slipperiness);
		jumpFactor.setValue(block.jumpFactor);
		speedFactor.setValue(block.speedFactor);

		disableOffset.setSelected(block.disableOffset);
		boundingBoxList.setEntries(block.boundingBoxes);

		sensitiveToVibration.setSelected(block.sensitiveToVibration);
		vibrationSensitivityRadius.setSelectedProcedure(block.vibrationSensitivityRadius);
		vibrationalEvents.setListElements(block.vibrationalEvents);
		canReceiveVibrationCondition.setSelectedProcedure(block.canReceiveVibrationCondition);
		onReceivedVibration.setSelectedProcedure(block.onReceivedVibration);

		refreshFieldsTileEntity();
		refreshRedstoneEmitted();
		refreshBonemealProperties();

		tickRate.setEnabled(!tickRandomly.isSelected());

		Model model = block.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);

		customDrop.setEnabled(!useLootTableForDrops.isSelected());
		dropAmount.setEnabled(!useLootTableForDrops.isSelected());

		if (hasGravity.isEnabled())
			hasGravity.setEnabled(!isWaterloggable.isSelected());

		updateSoundType();
	}

	@Override public Block getElementFromGUI() {
		Block block = new Block(modElement);
		block.name = name.getText();
		block.hasTransparency = hasTransparency.isSelected();
		block.connectedSides = connectedSides.isSelected();
		block.displayFluidOverlay = displayFluidOverlay.isSelected();
		block.transparencyType = (String) transparencyType.getSelectedItem();
		block.tintType = (String) tintType.getSelectedItem();
		block.isItemTinted = isItemTinted.isSelected();
		block.guiBoundTo = guiBoundTo.getEntry();
		block.rotationMode = rotationMode.getSelectedIndex();
		block.enablePitch = enablePitch.isSelected();
		block.customProperties = blockStates.getProperties();
		block.enchantPowerBonus = (double) enchantPowerBonus.getValue();
		block.hardness = (double) hardness.getValue();
		block.resistance = (double) resistance.getValue();
		block.hasGravity = hasGravity.isSelected();
		block.isWaterloggable = isWaterloggable.isSelected();
		block.emissiveRendering = emissiveRendering.isSelected();
		block.tickRandomly = tickRandomly.isSelected();
		block.creativeTabs = creativeTabs.getListElements();
		block.destroyTool = (String) destroyTool.getSelectedItem();
		block.requiresCorrectTool = requiresCorrectTool.isSelected();
		block.customDrop = customDrop.getBlock();
		block.dropAmount = (int) dropAmount.getValue();
		block.plantsGrowOn = plantsGrowOn.isSelected();
		block.isFluidTank = isFluidTank.isSelected();
		block.hasEnergyStorage = hasEnergyStorage.isSelected();
		block.energyInitial = (int) energyInitial.getValue();
		block.energyCapacity = (int) energyCapacity.getValue();
		block.energyMaxReceive = (int) energyMaxReceive.getValue();
		block.energyMaxExtract = (int) energyMaxExtract.getValue();
		block.fluidCapacity = (int) fluidCapacity.getValue();
		block.isNotColidable = isNotColidable.isSelected();
		block.canRedstoneConnect = canRedstoneConnect.isSelected();
		block.isBonemealable = isBonemealable.isSelected();
		block.isBonemealTargetCondition = isBonemealTargetCondition.getSelectedProcedure();
		block.bonemealSuccessCondition = bonemealSuccessCondition.getSelectedProcedure();
		block.onBonemealSuccess = onBonemealSuccess.getSelectedProcedure();
		block.lightOpacity = (int) lightOpacity.getValue();
		block.tickRate = (int) tickRate.getValue();
		block.isCustomSoundType = customSoundType.isSelected();
		block.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		block.breakSound = breakSound.getSound();
		block.fallSound = fallSound.getSound();
		block.hitSound = hitSound.getSound();
		block.placeSound = placeSound.getSound();
		block.stepSound = stepSound.getSound();
		block.luminance = (int) luminance.getValue();
		block.unbreakable = unbreakable.isSelected();
		block.vanillaToolTier = (String) vanillaToolTier.getSelectedItem();
		block.specialInformation = specialInformation.getSelectedProcedure();
		block.emittedRedstonePower = emittedRedstonePower.getSelectedProcedure();
		block.additionalHarvestCondition = additionalHarvestCondition.getSelectedProcedure();
		block.hasInventory = hasInventory.isSelected();
		block.useLootTableForDrops = useLootTableForDrops.isSelected();
		block.openGUIOnRightClick = openGUIOnRightClick.isSelected();
		block.inventorySize = (int) inventorySize.getValue();
		block.inventoryStackSize = (int) inventoryStackSize.getValue();
		block.inventoryDropWhenDestroyed = inventoryDropWhenDestroyed.isSelected();
		block.inventoryComparatorPower = inventoryComparatorPower.isSelected();
		block.inventoryAutomationTakeCondition = inventoryAutomationTakeCondition.getSelectedProcedure();
		block.inventoryAutomationPlaceCondition = inventoryAutomationPlaceCondition.getSelectedProcedure();
		if (outSlotIDs.getText().isBlank())
			block.inventoryOutSlotIDs = new ArrayList<>();
		else
			block.inventoryOutSlotIDs = Stream.of(outSlotIDs.getText().split(",")).filter(e -> !e.isEmpty())
					.map(Integer::parseInt).collect(Collectors.toList());
		if (inSlotIDs.getText().isBlank())
			block.inventoryInSlotIDs = new ArrayList<>();
		else
			block.inventoryInSlotIDs = Stream.of(inSlotIDs.getText().split(",")).filter(e -> !e.isEmpty())
					.map(Integer::parseInt).collect(Collectors.toList());
		block.frequencyPerChunks = (int) frequencyPerChunks.getValue();
		block.frequencyOnChunk = (int) frequencyOnChunk.getValue();
		block.generationShape = (String) generationShape.getSelectedItem();
		block.minGenerateHeight = generateHeight.getIntMinValue();
		block.maxGenerateHeight = generateHeight.getIntMaxValue();
		block.onBlockAdded = onBlockAdded.getSelectedProcedure();
		block.onNeighbourBlockChanges = onNeighbourBlockChanges.getSelectedProcedure();
		block.onTickUpdate = onTickUpdate.getSelectedProcedure();
		block.onRandomUpdateEvent = onRandomUpdateEvent.getSelectedProcedure();
		block.onDestroyedByPlayer = onDestroyedByPlayer.getSelectedProcedure();
		block.onDestroyedByExplosion = onDestroyedByExplosion.getSelectedProcedure();
		block.onStartToDestroy = onStartToDestroy.getSelectedProcedure();
		block.onEntityCollides = onEntityCollides.getSelectedProcedure();
		block.onEntityWalksOn = onEntityWalksOn.getSelectedProcedure();
		block.onBlockPlayedBy = onBlockPlayedBy.getSelectedProcedure();
		block.onRightClicked = onRightClicked.getSelectedProcedure();
		block.onRedstoneOn = onRedstoneOn.getSelectedProcedure();
		block.onRedstoneOff = onRedstoneOff.getSelectedProcedure();
		block.onHitByProjectile = onHitByProjectile.getSelectedProcedure();
		block.texture = textures.getTexture();
		block.textureTop = textures.getTextureTop();
		block.textureLeft = textures.getTextureLeft();
		block.textureFront = textures.getTextureFront();
		block.textureRight = textures.getTextureRight();
		block.textureBack = textures.getTextureBack();
		block.itemTexture = itemTexture.getTextureHolder();
		block.particleTexture = particleTexture.getTextureHolder();

		block.disableOffset = disableOffset.isSelected();
		block.boundingBoxes = boundingBoxList.getEntries();

		block.beaconColorModifier = beaconColorModifier.getColor();

		block.generateFeature = generateFeature.isSelected();
		block.restrictionBiomes = restrictionBiomes.getListElements();
		block.fluidRestrictions = fluidRestrictions.getListElements();
		block.blocksToReplace = blocksToReplace.getListElements();

		block.isReplaceable = isReplaceable.isSelected();
		block.canProvidePower = canProvidePower.isSelected();
		block.colorOnMap = colorOnMap.getSelectedItem().toString();
		block.noteBlockInstrument = noteBlockInstrument.getSelectedItem().toString();
		block.offsetType = (String) offsetType.getSelectedItem();
		block.aiPathNodeType = aiPathNodeType.getSelectedItem();
		block.creativePickItem = creativePickItem.getBlock();
		block.placingCondition = placingCondition.getSelectedProcedure();

		block.ignitedByLava = ignitedByLava.isSelected();
		block.flammability = (int) flammability.getValue();
		block.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();

		block.isLadder = isLadder.isSelected();
		block.reactionToPushing = (String) reactionToPushing.getSelectedItem();
		block.slipperiness = (double) slipperiness.getValue();
		block.speedFactor = (double) speedFactor.getValue();
		block.jumpFactor = (double) jumpFactor.getValue();

		block.sensitiveToVibration = sensitiveToVibration.isSelected();
		block.vibrationSensitivityRadius = vibrationSensitivityRadius.getSelectedProcedure();
		block.vibrationalEvents = vibrationalEvents.getListElements();
		block.canReceiveVibrationCondition = canReceiveVibrationCondition.getSelectedProcedure();
		block.onReceivedVibration = onReceivedVibration.getSelectedProcedure();

		block.animations = animations.getEntries();

		if (blockBase.getSelectedIndex() != 0)
			block.blockBase = blockBase.getSelectedItem();
		block.blockSetType = (String) blockSetType.getSelectedItem();

		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		block.renderType = 10;
		if (model.getType() == Model.Type.JSON)
			block.renderType = 2;
		else if (model.getType() == Model.Type.OBJ)
			block.renderType = 3;
		else if (model.getType() == Model.Type.JAVA)
			block.renderType = 4;
		else if (model.equals(singleTexture))
			block.renderType = "No tint".equals(tintType.getSelectedItem()) ? 11 : 110;
		else if (model.equals(cross))
			block.renderType = "No tint".equals(tintType.getSelectedItem()) ? 12 : 120;
		else if (model.equals(crop))
			block.renderType = 13;
		else if (model.equals(grassBlock))
			block.renderType = 14;
		block.customModelName = model.getReadableName();

		return block;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-block");
	}

}
