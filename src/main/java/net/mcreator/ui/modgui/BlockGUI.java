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
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.InputSlot;
import net.mcreator.element.parts.gui.OutputSlot;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.GeneralTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.boundingboxes.JBoundingBoxList;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.CommaSeparatedNumbersValidator;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockGUI extends ModElementGUI<Block> {

	private final DataListComboBox material = new DataListComboBox(mcreator, ElementUtil.loadMaterials());

	private TextureHolder texture;
	private TextureHolder textureTop;
	private TextureHolder textureLeft;
	private TextureHolder textureFront;
	private TextureHolder textureRight;
	private TextureHolder textureBack;

	private TextureHolder itemTexture;
	private TextureHolder particleTexture;

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

	private ProcedureSelector particleCondition;
	private NumberProcedureSelector emittedRedstonePower;
	private ProcedureSelector placingCondition;
	private ProcedureSelector generateCondition;

	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(1, -1, 64000, 0.05));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 0.5));
	private final VTextField name = new VTextField(19);

	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));
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
	private final JComboBox<String> colorOnMap = new JComboBox<>();
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final JComboBox<String> generationShape = new JComboBox<>(new String[] { "UNIFORM", "TRIANGLE" });
	private final JSpinner minGenerateHeight = new JSpinner(new SpinnerNumberModel(0, -2032, 2016, 1));
	private final JSpinner maxGenerateHeight = new JSpinner(new SpinnerNumberModel(64, -2032, 2016, 1));
	private final JSpinner frequencyPerChunks = new JSpinner(new SpinnerNumberModel(10, 1, 64, 1));
	private final JSpinner frequencyOnChunk = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));
	private BiomeListField restrictionBiomes;
	private MCItemListField blocksToReplace;
	private DimensionListField spawnWorldTypes;

	private final JCheckBox plantsGrowOn = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isLadder = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> reactionToPushing = new JComboBox<>(
			new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "NONE", "XZ", "XYZ" });
	private final JComboBox<String> aiPathNodeType = new JComboBox<>();

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final DataListComboBox particleToSpawn = new DataListComboBox(mcreator);

	private final JComboBox<String> particleSpawningShape = new JComboBox<>(
			new String[] { "Spread", "Top", "Tube", "Plane" });

	private final JSpinner particleSpawningRadious = new JSpinner(new SpinnerNumberModel(0.5, 0, 100, 0.1));
	private final JSpinner particleAmount = new JSpinner(new SpinnerNumberModel(4, 0, 1000, 1));
	private final JSpinner slipperiness = new JSpinner(new SpinnerNumberModel(0.6, 0.01, 5, 0.1));
	private final JSpinner speedFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));
	private final JSpinner jumpFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));

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
	private final JSpinner breakHarvestLevel = new JSpinner(new SpinnerNumberModel(1, -1, 100, 1));
	private final JCheckBox requiresCorrectTool = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox spawnParticles = L10N.checkbox("elementgui.block.spawn_particles");

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model singleTexture = new Model.BuiltInModel("Single texture");
	private final Model cross = new Model.BuiltInModel("Cross model");
	private final Model crop = new Model.BuiltInModel("Crop model");
	private final Model grassBlock = new Model.BuiltInModel("Grass block");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(
			new Model[] { normal, singleTexture, cross, crop, grassBlock });

	private final JComboBox<String> transparencyType = new JComboBox<>(
			new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" });

	private final JCheckBox hasInventory = L10N.checkbox("elementgui.block.has_inventory");

	private final JCheckBox openGUIOnRightClick = L10N.checkbox("elementgui.common.enable");
	private final JComboBox<String> guiBoundTo = new JComboBox<>();

	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));
	private final JCheckBox inventoryDropWhenDestroyed = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox inventoryComparatorPower = L10N.checkbox("elementgui.common.enable");

	private final VTextField outSlotIDs = new VTextField(18);
	private final VTextField inSlotIDs = new VTextField(18);

	private final JTextField specialInfo = new JTextField(25);

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page3group = new ValidationGroup();

	private final JComboBox<String> blockBase = new JComboBox<>(
			new String[] { "Default basic block", "Stairs", "Slab", "Fence", "Wall", "Leaves", "TrapDoor", "Pane",
					"Door", "FenceGate", "EndRod", "PressurePlate", "Button" });

	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	private final JCheckBox useLootTableForDrops = L10N.checkbox("elementgui.common.use_table_loot_drops");

	public BlockGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		destroyTool.setRenderer(new ItemTexturesComboBoxRenderer());

		blocksToReplace = new MCItemListField(mcreator, ElementUtil::loadBlocks);
		restrictionBiomes = new BiomeListField(mcreator);
		spawnWorldTypes = new DimensionListField(mcreator);

		fluidRestrictions = new FluidListField(mcreator);

		boundingBoxList = new JBoundingBoxList(mcreator, this);

		blocksToReplace.setListElements(
				new ArrayList<>(Collections.singleton(new MItemBlock(mcreator.getWorkspace(), "Blocks.STONE"))));

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
				L10N.t("elementgui.block.event_on_block_destroyed_by_explosion"),
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

		particleCondition = new ProcedureSelector(this.withEntry("block/particle_condition"), mcreator,
				L10N.t("elementgui.block.event_particle_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).makeInline();

		emittedRedstonePower = new NumberProcedureSelector(null, mcreator,
				new JSpinner(new SpinnerNumberModel(15, 0, 15, 1)), Dependency.fromString(
				"x:number/y:number/z:number/world:world/direction:direction/blockstate:blockstate"));

		placingCondition = new ProcedureSelector(this.withEntry("block/placing_condition"), mcreator,
				L10N.t("elementgui.block.event_placing_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		generateCondition = new ProcedureSelector(this.withEntry("block/generation_condition"), mcreator,
				L10N.t("elementgui.block.event_generate_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		blockBase.addActionListener(e -> {
			renderType.setEnabled(true);
			disableOffset.setEnabled(true);
			boundingBoxList.setEnabled(true);
			rotationMode.setEnabled(true);
			hasGravity.setEnabled(true);
			transparencyType.setEnabled(true);
			hasTransparency.setEnabled(true);
			material.setEnabled(true);
			connectedSides.setEnabled(true);
			isWaterloggable.setEnabled(true);

			if (blockBase.getSelectedItem() != null && blockBase.getSelectedItem().equals("Pane")) {
				connectedSides.setEnabled(false);
				renderType.setEnabled(false);
				isWaterloggable.setEnabled(false);
				rotationMode.setEnabled(false);
				disableOffset.setEnabled(false);
				boundingBoxList.setEnabled(false);

				connectedSides.setSelected(false);
				renderType.setSelectedItem(singleTexture);
				isWaterloggable.setSelected(false);
				rotationMode.setSelectedIndex(0);

				if (!isEditingMode()) {
					transparencyType.setSelectedItem("CUTOUT_MIPPED");
					lightOpacity.setValue(0);
				}
			} else if (blockBase.getSelectedItem() != null && blockBase.getSelectedItem().equals("Leaves")) {
				material.setEnabled(false);
				renderType.setEnabled(false);
				rotationMode.setEnabled(false);
				hasTransparency.setEnabled(false);
				transparencyType.setEnabled(false);
				isWaterloggable.setSelected(false);
				disableOffset.setEnabled(false);
				boundingBoxList.setEnabled(false);

				material.setSelectedItem("LEAVES");
				renderType.setSelectedItem(singleTexture);
				rotationMode.setSelectedIndex(0);
				hasTransparency.setSelected(false);
				transparencyType.setSelectedItem("SOLID");
				isWaterloggable.setEnabled(false);

				if (!isEditingMode()) {
					lightOpacity.setValue(1);
				}
			} else if (blockBase.getSelectedItem() != null && blockBase.getSelectedIndex() != 0) {
				renderType.setSelectedItem(singleTexture);
				renderType.setEnabled(false);
				disableOffset.setEnabled(false);
				boundingBoxList.setEnabled(false);
				hasGravity.setEnabled(false);
				rotationMode.setEnabled(false);
				isWaterloggable.setEnabled(false);

				hasGravity.setSelected(false);
				rotationMode.setSelectedIndex(0);

				if (!isEditingMode()) {
					lightOpacity.setValue(0);
					if (blockBase.getSelectedItem().equals("Wall") || blockBase.getSelectedItem().equals("Fence")
							|| blockBase.getSelectedItem().equals("TrapDoor") || blockBase.getSelectedItem()
							.equals("Door") || blockBase.getSelectedItem().equals("FenceGate")
							|| blockBase.getSelectedItem().equals("EndRod") || blockBase.getSelectedItem()
							.equals("PressurePlate") || blockBase.getSelectedItem().equals("Button")) {
						hasTransparency.setSelected(true);
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

		pane8.setOpaque(false);

		JPanel destal = new JPanel(new GridLayout(3, 4));
		destal.setOpaque(false);

		texture = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK)).flipOnX();
		textureTop = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK)).flipOnX();

		textureLeft = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK));
		textureFront = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK));
		textureRight = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK));
		textureBack = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK));

		itemTexture = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.ITEM), 32);
		particleTexture = new TextureHolder(new GeneralTextureSelector(mcreator, TextureType.BLOCK), 32);

		itemTexture.setOpaque(false);
		particleTexture.setOpaque(false);
		texture.setOpaque(false);
		textureTop.setOpaque(false);
		textureLeft.setOpaque(false);
		textureFront.setOpaque(false);
		textureRight.setOpaque(false);
		textureBack.setOpaque(false);

		isReplaceable.setOpaque(false);
		canProvidePower.setOpaque(false);

		destal.add(new JLabel());
		destal.add(ComponentUtils.squareAndBorder(textureTop, L10N.t("elementgui.block.texture_place_top")));
		destal.add(new JLabel());
		destal.add(new JLabel());

		destal.add(ComponentUtils.squareAndBorder(textureLeft, new Color(126, 196, 255),
				L10N.t("elementgui.block.texture_place_left_overlay")));
		destal.add(ComponentUtils.squareAndBorder(textureFront, L10N.t("elementgui.block.texture_place_front_side")));
		destal.add(ComponentUtils.squareAndBorder(textureRight, L10N.t("elementgui.block.texture_place_right")));
		destal.add(ComponentUtils.squareAndBorder(textureBack, L10N.t("elementgui.block.texture_place_back")));

		textureLeft.setActionListener(event -> {
			if (!(texture.has() || textureTop.has() || textureBack.has() || textureFront.has() || textureRight.has())) {
				texture.setTextureFromTextureName(textureLeft.getID());
				textureTop.setTextureFromTextureName(textureLeft.getID());
				textureBack.setTextureFromTextureName(textureLeft.getID());
				textureFront.setTextureFromTextureName(textureLeft.getID());
				textureRight.setTextureFromTextureName(textureLeft.getID());
			}
		});

		destal.add(new JLabel());
		destal.add(ComponentUtils.squareAndBorder(texture, new Color(125, 255, 174),
				L10N.t("elementgui.block.texture_place_bottom_main")));
		destal.add(new JLabel());
		destal.add(new JLabel());

		JPanel txblock4 = new JPanel(new BorderLayout());
		txblock4.setOpaque(false);
		txblock4.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.block_base_item_texture"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		txblock4.add("Center", PanelUtils.gridElements(3, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("block/base"), L10N.label("elementgui.block.block_base")),
				blockBase, HelpUtils.wrapWithHelpButton(this.withEntry("block/item_texture"),
						L10N.label("elementgui.block.item_texture")), PanelUtils.centerInPanel(itemTexture),
				HelpUtils.wrapWithHelpButton(this.withEntry("block/particle_texture"),
						L10N.label("elementgui.block.particle_texture")), PanelUtils.centerInPanel(particleTexture)));

		JPanel sbbp2 = new JPanel(new BorderLayout(1, 5));

		JPanel sbbp22 = PanelUtils.totalCenterInPanel(destal);

		sbbp2.setOpaque(false);

		plantsGrowOn.setOpaque(false);

		sbbp22.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.block_textures"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel topnbot = new JPanel(new BorderLayout());
		topnbot.setOpaque(false);

		topnbot.add("Center", sbbp22);

		JPanel txblock3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		txblock3.setOpaque(false);
		txblock3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.special_information_title"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		ComponentUtils.deriveFont(specialInfo, 16);

		txblock3.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"),
				L10N.label("elementgui.block.special_information_tip")));
		txblock3.add(specialInfo);

		sbbp2.add("Center", topnbot);

		JPanel render = new JPanel();
		render.setLayout(new BoxLayout(render, BoxLayout.PAGE_AXIS));

		ComponentUtils.deriveFont(transparencyType, 16);
		ComponentUtils.deriveFont(blockBase, 16);
		ComponentUtils.deriveFont(tintType, 16);

		JPanel transparencySettings = new JPanel(new GridLayout(4, 2, 0, 2));
		transparencySettings.setOpaque(false);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/has_transparency"),
				L10N.label("elementgui.block.has_trasparency")));
		transparencySettings.add(hasTransparency);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/transparency_type"),
				L10N.label("elementgui.block.transparency_type")));
		transparencySettings.add(transparencyType);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/connected_sides"),
				L10N.label("elementgui.block.connected_sides")));
		transparencySettings.add(connectedSides);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fluid_overlay"),
				L10N.label("elementgui.block.fluid_overlay")));
		transparencySettings.add(displayFluidOverlay);

		ComponentUtils.deriveFont(renderType, 16);
		ComponentUtils.deriveFont(rotationMode, 16);

		JPanel rent = new JPanel(new GridLayout(4, 2, 0, 2));
		rent.setOpaque(false);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/model"), L10N.label("elementgui.block.model")));
		rent.add(renderType);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/rotation_mode"),
				L10N.label("elementgui.block.rotation_mode")));
		rent.add(rotationMode);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/enable_pitch"),
				L10N.label("elementgui.block.enable_pitch")));
		rent.add(enablePitch);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_waterloggable"),
				L10N.label("elementgui.block.is_waterloggable")));
		rent.add(isWaterloggable);

		renderType.setPreferredSize(new Dimension(320, 42));
		rotationMode.setPreferredSize(new Dimension(320, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		enablePitch.setOpaque(false);
		enablePitch.setEnabled(false);
		rotationMode.addActionListener(e -> {
			enablePitch.setEnabled(rotationMode.getSelectedIndex() == 1 || rotationMode.getSelectedIndex() == 3);
			if (!enablePitch.isEnabled())
				enablePitch.setSelected(false);
		});

		JPanel tintPanel = new JPanel(new GridLayout(2, 2, 0, 2));
		tintPanel.setOpaque(false);
		isItemTinted.setOpaque(false);

		tintPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tint_type"),
				L10N.label("elementgui.common.tint_type")));
		tintPanel.add(tintType);
		tintPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_item_tinted"),
				L10N.label("elementgui.block.is_item_tinted")));
		tintPanel.add(isItemTinted);

		topnbot.add("South", PanelUtils.northAndCenterElement(tintPanel, txblock4));

		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.render_type"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		transparencySettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.transparency"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		tintPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.block_tint"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		render.add(rent);
		render.add(transparencySettings);
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
			boundingBoxList.setBoundingBoxes(Collections.singletonList(new IBlockWithBoundingBox.BoxEntry()));

		boundingBoxList.addPropertyChangeListener("boundingBoxChanged", e -> updateParametersBasedOnBoundingBoxSize());

		JPanel selp = new JPanel(new GridLayout(14, 2, 0, 2));
		JPanel selp3 = new JPanel(new GridLayout(8, 2, 0, 2));
		JPanel soundProperties = new JPanel(new GridLayout(7, 2, 0, 2));

		JPanel advancedProperties = new JPanel(new GridLayout(12, 2, 0, 2));

		hasGravity.setOpaque(false);
		tickRandomly.setOpaque(false);
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

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/material"),
				L10N.label("elementgui.block.material")));
		selp.add(material);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		selp.add(creativeTab);

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

		creativeTab.setPrototypeDisplayValue(new DataListEntry.Dummy("BUILDING_BLOCKS"));
		creativeTab.addPopupMenuListener(new ComboBoxFullWidthPopup());

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				L10N.label("elementgui.common.custom_drop")));
		selp3.add(PanelUtils.centerInPanel(customDrop));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"),
				L10N.label("elementgui.common.drop_amount")));
		selp3.add(dropAmount);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				L10N.label("elementgui.common.use_loot_table_for_drop")));
		selp3.add(PanelUtils.centerInPanel(useLootTableForDrops));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"),
				L10N.label("elementgui.common.creative_pick_item")));
		selp3.add(PanelUtils.centerInPanel(creativePickItem));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/harvest_tool"),
				L10N.label("elementgui.block.harvest_tool")));
		selp3.add(destroyTool);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/harvest_level"),
				L10N.label("elementgui.block.harvest_level")));
		selp3.add(breakHarvestLevel);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/requires_correct_tool"),
				L10N.label("elementgui.block.requires_correct_tool")));
		selp3.add(requiresCorrectTool);

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

		JComponent advancedWithCondition = PanelUtils.northAndCenterElement(advancedProperties, placingCondition, 5, 5);

		isWaterloggable.setOpaque(false);
		canRedstoneConnect.setOpaque(false);
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_general"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		selp3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_dropping"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		soundProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_sound"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		advancedWithCondition.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.properties_advanced_block"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		selp.setOpaque(false);
		soundProperties.setOpaque(false);

		pane3.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(selp, PanelUtils.centerAndSouthElement(selp3, soundProperties))));
		pane3.setOpaque(false);

		JPanel events2 = new JPanel(new GridLayout(4, 5, 5, 5));
		events2.setOpaque(false);

		events2.add(onRightClicked);
		events2.add(onBlockAdded);
		events2.add(onNeighbourBlockChanges);
		events2.add(onTickUpdate);
		events2.add(onDestroyedByPlayer);
		events2.add(onDestroyedByExplosion);
		events2.add(onStartToDestroy);
		events2.add(onEntityCollides);
		events2.add(onEntityWalksOn);
		events2.add(onHitByProjectile);
		events2.add(onBlockPlayedBy);
		events2.add(onRedstoneOn);
		events2.add(onRedstoneOff);
		events2.add(onRandomUpdateEvent);

		pane4.add("Center", PanelUtils.totalCenterInPanel(events2));

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

		JPanel props = new JPanel(new GridLayout(8, 2, 25, 2));
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

		guiBoundTo.addActionListener(e -> {
			if (!isEditingMode()) {
				String selected = (String) guiBoundTo.getSelectedItem();
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.energy_storage"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		fluidTank.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.fluid_tank"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

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

		props.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.settings_inventory"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		invblock.add("Center", props);

		invblock.add("North", HelpUtils.wrapWithHelpButton(this.withEntry("block/has_inventory"), hasInventory));

		pane8.add("Center", PanelUtils.totalCenterInPanel(invblock));

		JPanel enderpanel2 = new JPanel(new BorderLayout(30, 15));

		JPanel genPanel = new JPanel(new GridLayout(8, 2, 20, 2));

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				L10N.label("elementgui.block.spawn_world_types")));
		genPanel.add(spawnWorldTypes);

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

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_min_height"),
				L10N.label("elementgui.block.gen_min_height")));
		genPanel.add(minGenerateHeight);
		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_max_height"),
				L10N.label("elementgui.block.gen_max_height")));
		genPanel.add(maxGenerateHeight);

		genPanel.setOpaque(false);

		enderpanel2.add("West", PanelUtils.totalCenterInPanel(new JLabel(UIRES.get("chunk"))));
		enderpanel2.add("Center", PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(genPanel,
				PanelUtils.westAndCenterElement(new JEmptyBox(5, 5), generateCondition), 5, 5)));

		enderpanel2.setOpaque(false);

		JPanel particleParameters = new JPanel(new GridLayout(5, 2, 0, 2));

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_particles"), spawnParticles));
		particleParameters.add(new JLabel());

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_type"),
				L10N.label("elementgui.block.particle_gen_type")));
		particleParameters.add(particleToSpawn);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_shape"),
				L10N.label("elementgui.block.particle_gen_shape")));
		particleParameters.add(particleSpawningShape);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_spawn_radius"),
				L10N.label("elementgui.block.particle_gen_spawn_radius")));
		particleParameters.add(particleSpawningRadious);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_average_amount"),
				L10N.label("elementgui.block.particle_gen_average_amount")));
		particleParameters.add(particleAmount);

		JComponent parpar = PanelUtils.northAndCenterElement(particleParameters, particleCondition, 5, 5);

		parpar.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.properties_particle"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		particleParameters.setOpaque(false);

		JPanel redstoneParameters = new JPanel(new GridLayout(3, 2, 0, 2));
		redstoneParameters.setOpaque(false);

		redstoneParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/redstone_connect"),
				L10N.label("elementgui.block.redstone_connect")));
		redstoneParameters.add(canRedstoneConnect);

		redstoneParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emits_redstone"),
				L10N.label("elementgui.block.emits_redstone")));
		redstoneParameters.add(canProvidePower);

		redstoneParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/redstone_power"),
				L10N.label("elementgui.block.redstone_power")));
		redstoneParameters.add(emittedRedstonePower);

		redstoneParameters.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.properties_redstone"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JComponent parred = PanelUtils.centerAndSouthElement(parpar, PanelUtils.pullElementUp(redstoneParameters));

		canProvidePower.addActionListener(e -> refreshRedstoneEmitted());
		refreshRedstoneEmitted();

		particleSpawningRadious.setOpaque(false);
		spawnParticles.setOpaque(false);

		renderType.addActionListener(e -> {
			Model selected = renderType.getSelectedItem();
			if (selected != null) {
				if (!selected.equals(normal) && !selected.equals(singleTexture) && !selected.equals(grassBlock)) {
					hasTransparency.setSelected(true);
					lightOpacity.setValue(0);
				}
				if (!isEditingMode() && selected.equals(grassBlock)) {
					transparencyType.setSelectedItem("CUTOUT_MIPPED");
				}
			}
		});

		pane7.add(PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(advancedWithCondition, PanelUtils.pullElementUp(parred))));

		pane7.setOpaque(false);
		pane9.setOpaque(false);

		pane9.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(enderpanel2)));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

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

		addPage(L10N.t("elementgui.common.page_visual"), pane2);
		addPage(L10N.t("elementgui.common.page_bounding_boxes"), bbPane);
		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), pane7);
		addPage(L10N.t("elementgui.block.page_tile_entity"), pane8);
		addPage(L10N.t("elementgui.block.page_energy_fluid_storage"), pane10);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);
		addPage(L10N.t("elementgui.common.page_generation"), pane9);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}

		updateSoundType();
	}

	private void refreshFieldsTileEntity() {
		inventorySize.setEnabled(hasInventory.isSelected());
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
	}

	private void refreshRedstoneEmitted() {
		emittedRedstonePower.setEnabled(canProvidePower.isSelected());
	}

	private void updateTextureOptions() {
		texture.setVisible(false);
		textureTop.setVisible(false);
		textureLeft.setVisible(false);
		textureFront.setVisible(false);
		textureRight.setVisible(false);
		textureBack.setVisible(false);

		if (normal.equals(renderType.getSelectedItem())) {
			texture.setVisible(true);
			textureTop.setVisible(true);
			textureLeft.setVisible(true);
			textureFront.setVisible(true);
			textureRight.setVisible(true);
			textureBack.setVisible(true);
		} else if (grassBlock.equals(renderType.getSelectedItem())) {
			texture.setVisible(true);
			textureTop.setVisible(true);
			textureLeft.setVisible(true);
			textureFront.setVisible(true);
		} else if ("Pane".equals(blockBase.getSelectedItem()) || "Door".equals(blockBase.getSelectedItem())) {
			textureTop.setVisible(true);
			texture.setVisible(true);
		} else if ("Stairs".equals(blockBase.getSelectedItem()) || "Slab".equals(blockBase.getSelectedItem())) {
			textureTop.setVisible(true);
			textureFront.setVisible(true);
			texture.setVisible(true);
		} else {
			texture.setVisible(true);
		}
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

		particleCondition.refreshListKeepSelected();
		emittedRedstonePower.refreshListKeepSelected();
		placingCondition.refreshListKeepSelected();
		generateCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(renderType,
				ListUtils.merge(Arrays.asList(normal, singleTexture, cross, crop, grassBlock),
						Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
								.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
								.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(guiBoundTo, ListUtils.merge(Collections.singleton("<NONE>"),
				mcreator.getWorkspace().getModElements().stream().filter(var -> var.getType() == ModElementType.GUI)
						.map(ModElement::getName).collect(Collectors.toList())), "<NONE>");

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(colorOnMap,
				Arrays.asList(ElementUtil.getDataListAsStringArray("mapcolors")), "DEFAULT");
		ComboBoxUtil.updateComboBoxContents(aiPathNodeType,
				Arrays.asList(ElementUtil.getDataListAsStringArray("pathnodetypes")), "DEFAULT");

		ComboBoxUtil.updateComboBoxContents(particleToSpawn, ElementUtil.loadAllParticles(mcreator.getWorkspace()));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		else if (page == 2)
			return new AggregatedValidationResult(page3group);
		else if (page == 4)
			return new AggregatedValidationResult(outSlotIDs, inSlotIDs);
		else if (page == 7) {
			if ((int) minGenerateHeight.getValue() >= (int) maxGenerateHeight.getValue()) {
				return new AggregatedValidationResult.FAIL(L10N.t("elementgui.block.error_minimal_generation_height"));
			}
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Block block) {
		itemTexture.setTextureFromTextureName(block.itemTexture);
		particleTexture.setTextureFromTextureName(block.particleTexture);
		texture.setTextureFromTextureName(block.texture);
		textureTop.setTextureFromTextureName(block.textureTop);
		textureLeft.setTextureFromTextureName(block.textureLeft);
		textureFront.setTextureFromTextureName(block.textureFront);
		textureRight.setTextureFromTextureName(block.textureRight);
		textureBack.setTextureFromTextureName(block.textureBack);
		guiBoundTo.setSelectedItem(block.guiBoundTo);
		rotationMode.setSelectedIndex(block.rotationMode);
		enablePitch.setSelected(block.enablePitch);
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
		maxGenerateHeight.setValue(block.maxGenerateHeight);
		minGenerateHeight.setValue(block.minGenerateHeight);
		frequencyPerChunks.setValue(block.frequencyPerChunks);
		frequencyOnChunk.setValue(block.frequencyOnChunk);
		spawnParticles.setSelected(block.spawnParticles);
		particleToSpawn.setSelectedItem(block.particleToSpawn);
		particleSpawningShape.setSelectedItem(block.particleSpawningShape);
		particleCondition.setSelectedProcedure(block.particleCondition);
		emittedRedstonePower.setSelectedProcedure(block.emittedRedstonePower);
		generateCondition.setSelectedProcedure(block.generateCondition);
		particleSpawningRadious.setValue(block.particleSpawningRadious);
		particleAmount.setValue(block.particleAmount);
		hardness.setValue(block.hardness);
		resistance.setValue(block.resistance);
		hasGravity.setSelected(block.hasGravity);
		isWaterloggable.setSelected(block.isWaterloggable);
		emissiveRendering.setSelected(block.emissiveRendering);
		tickRandomly.setSelected(block.tickRandomly);
		creativeTab.setSelectedItem(block.creativeTab);
		destroyTool.setSelectedItem(block.destroyTool);
		soundOnStep.setSelectedItem(block.soundOnStep.getUnmappedValue());
		breakSound.setSound(block.breakSound);
		fallSound.setSound(block.fallSound);
		hitSound.setSound(block.hitSound);
		placeSound.setSound(block.placeSound);
		stepSound.setSound(block.stepSound);
		defaultSoundType.setSelected(!block.isCustomSoundType);
		customSoundType.setSelected(block.isCustomSoundType);
		luminance.setValue(block.luminance);
		breakHarvestLevel.setValue(block.breakHarvestLevel);
		requiresCorrectTool.setSelected(block.requiresCorrectTool);
		customDrop.setBlock(block.customDrop);
		dropAmount.setValue(block.dropAmount);
		isNotColidable.setSelected(block.isNotColidable);
		unbreakable.setSelected(block.unbreakable);
		canRedstoneConnect.setSelected(block.canRedstoneConnect);
		lightOpacity.setValue(block.lightOpacity);
		material.setSelectedItem(block.material.getUnmappedValue());
		transparencyType.setSelectedItem(block.transparencyType);
		tintType.setSelectedItem(block.tintType);
		isItemTinted.setSelected(block.isItemTinted);

		if (block.blockBase == null) {
			blockBase.setSelectedIndex(0);
		} else {
			blockBase.setSelectedItem(block.blockBase);
		}

		plantsGrowOn.setSelected(block.plantsGrowOn);
		hasInventory.setSelected(block.hasInventory);
		useLootTableForDrops.setSelected(block.useLootTableForDrops);
		openGUIOnRightClick.setSelected(block.openGUIOnRightClick);
		inventoryDropWhenDestroyed.setSelected(block.inventoryDropWhenDestroyed);
		inventoryComparatorPower.setSelected(block.inventoryComparatorPower);
		inventorySize.setValue(block.inventorySize);
		inventoryStackSize.setValue(block.inventoryStackSize);
		tickRate.setValue(block.tickRate);

		spawnWorldTypes.setListElements(block.spawnWorldTypes);
		blocksToReplace.setListElements(block.blocksToReplace);
		restrictionBiomes.setListElements(block.restrictionBiomes);
		fluidRestrictions.setListElements(block.fluidRestrictions);

		isReplaceable.setSelected(block.isReplaceable);
		canProvidePower.setSelected(block.canProvidePower);
		colorOnMap.setSelectedItem(block.colorOnMap);
		offsetType.setSelectedItem(block.offsetType);
		aiPathNodeType.setSelectedItem(block.aiPathNodeType);
		creativePickItem.setBlock(block.creativePickItem);
		placingCondition.setSelectedProcedure(block.placingCondition);

		beaconColorModifier.setColor(block.beaconColorModifier);

		flammability.setValue(block.flammability);
		fireSpreadSpeed.setValue(block.fireSpreadSpeed);

		isLadder.setSelected(block.isLadder);
		reactionToPushing.setSelectedItem(block.reactionToPushing);
		slipperiness.setValue(block.slipperiness);
		jumpFactor.setValue(block.jumpFactor);
		speedFactor.setValue(block.speedFactor);

		disableOffset.setSelected(block.disableOffset);
		boundingBoxList.setBoundingBoxes(block.boundingBoxes);

		specialInfo.setText(
				block.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		refreshFieldsTileEntity();
		refreshRedstoneEmitted();

		tickRate.setEnabled(!tickRandomly.isSelected());

		Model model = block.getItemModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
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
		block.guiBoundTo = (String) guiBoundTo.getSelectedItem();
		block.rotationMode = rotationMode.getSelectedIndex();
		block.enablePitch = enablePitch.isSelected();
		block.enchantPowerBonus = (double) enchantPowerBonus.getValue();
		block.hardness = (double) hardness.getValue();
		block.resistance = (double) resistance.getValue();
		block.hasGravity = hasGravity.isSelected();
		block.isWaterloggable = isWaterloggable.isSelected();
		block.emissiveRendering = emissiveRendering.isSelected();
		block.tickRandomly = tickRandomly.isSelected();
		block.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
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
		block.lightOpacity = (int) lightOpacity.getValue();
		block.material = new Material(mcreator.getWorkspace(), material.getSelectedItem());
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
		block.breakHarvestLevel = (int) breakHarvestLevel.getValue();
		block.spawnParticles = spawnParticles.isSelected();
		block.particleToSpawn = new Particle(mcreator.getWorkspace(), particleToSpawn.getSelectedItem());
		block.particleSpawningShape = (String) particleSpawningShape.getSelectedItem();
		block.particleSpawningRadious = (double) particleSpawningRadious.getValue();
		block.particleAmount = (int) particleAmount.getValue();
		block.particleCondition = particleCondition.getSelectedProcedure();
		block.emittedRedstonePower = emittedRedstonePower.getSelectedProcedure();
		block.generateCondition = generateCondition.getSelectedProcedure();
		block.hasInventory = hasInventory.isSelected();
		block.useLootTableForDrops = useLootTableForDrops.isSelected();
		block.openGUIOnRightClick = openGUIOnRightClick.isSelected();
		block.inventorySize = (int) inventorySize.getValue();
		block.inventoryStackSize = (int) inventoryStackSize.getValue();
		block.inventoryDropWhenDestroyed = inventoryDropWhenDestroyed.isSelected();
		block.inventoryComparatorPower = inventoryComparatorPower.isSelected();
		if (outSlotIDs.getText().trim().equals(""))
			block.inventoryOutSlotIDs = new ArrayList<>();
		else
			block.inventoryOutSlotIDs = Stream.of(outSlotIDs.getText().split(",")).filter(e -> !e.equals(""))
					.map(Integer::parseInt).collect(Collectors.toList());
		if (inSlotIDs.getText().trim().equals(""))
			block.inventoryInSlotIDs = new ArrayList<>();
		else
			block.inventoryInSlotIDs = Stream.of(inSlotIDs.getText().split(",")).filter(e -> !e.equals(""))
					.map(Integer::parseInt).collect(Collectors.toList());
		block.frequencyPerChunks = (int) frequencyPerChunks.getValue();
		block.frequencyOnChunk = (int) frequencyOnChunk.getValue();
		block.generationShape = (String) generationShape.getSelectedItem();
		block.minGenerateHeight = (int) minGenerateHeight.getValue();
		block.maxGenerateHeight = (int) maxGenerateHeight.getValue();
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
		block.texture = texture.getID();
		block.itemTexture = itemTexture.getID();
		block.particleTexture = particleTexture.getID();
		block.textureTop = textureTop.getID();
		block.textureLeft = textureLeft.getID();
		block.textureFront = textureFront.getID();
		block.textureRight = textureRight.getID();
		block.textureBack = textureBack.getID();

		block.disableOffset = disableOffset.isSelected();
		block.boundingBoxes = boundingBoxList.getBoundingBoxes();

		block.beaconColorModifier = beaconColorModifier.getColor();

		block.spawnWorldTypes = spawnWorldTypes.getListElements();
		block.restrictionBiomes = restrictionBiomes.getListElements();
		block.fluidRestrictions = fluidRestrictions.getListElements();
		block.blocksToReplace = blocksToReplace.getListElements();

		block.isReplaceable = isReplaceable.isSelected();
		block.canProvidePower = canProvidePower.isSelected();
		block.colorOnMap = (String) colorOnMap.getSelectedItem();
		block.offsetType = (String) offsetType.getSelectedItem();
		block.aiPathNodeType = (String) aiPathNodeType.getSelectedItem();
		block.creativePickItem = creativePickItem.getBlock();
		block.placingCondition = placingCondition.getSelectedProcedure();

		block.flammability = (int) flammability.getValue();
		block.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();

		block.isLadder = isLadder.isSelected();
		block.reactionToPushing = (String) reactionToPushing.getSelectedItem();
		block.slipperiness = (double) slipperiness.getValue();
		block.speedFactor = (double) speedFactor.getValue();
		block.jumpFactor = (double) jumpFactor.getValue();

		block.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());

		if (blockBase.getSelectedIndex() != 0)
			block.blockBase = (String) blockBase.getSelectedItem();

		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		block.renderType = 10;
		if (model.getType() == Model.Type.JSON)
			block.renderType = 2;
		else if (model.getType() == Model.Type.OBJ)
			block.renderType = 3;
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
