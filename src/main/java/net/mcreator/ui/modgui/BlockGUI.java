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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.InputSlot;
import net.mcreator.element.parts.gui.OutputSlot;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.GUI;
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
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.CommaSeparatedNumbersValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.resources.Model;
import org.jetbrains.annotations.Nullable;

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

	private ProcedureSelector particleCondition;
	private ProcedureSelector generateCondition;

	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(1, -1, 64000, 0.05));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(10, 0, 64000, 0.5));
	private final VTextField name = new VTextField(19);

	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1.00, 0.01));
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));
	private final JSpinner lightOpacity = new JSpinner(new SpinnerNumberModel(255, 0, 255, 1));

	private final JSpinner mx = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner my = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner mz = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner Mx = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner My = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner Mz = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner tickRate = new JSpinner(new SpinnerNumberModel(10, 1, 9999999, 1));

	private final JSpinner enchantPowerBonus = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 0.1));

	private final JColor beaconColorModifier = new JColor(mcreator, true);

	private final JCheckBox hasGravity = new JCheckBox("Check to enable");
	private final JCheckBox isWaterloggable = new JCheckBox("Check to enable");
	private final JCheckBox tickRandomly = new JCheckBox("Check to enable");
	private final JCheckBox unbreakable = new JCheckBox("Check to enable");
	private final JCheckBox isNotColidable = new JCheckBox("Check to enable");
	private final JCheckBox canProvidePower = new JCheckBox("Check to enable");

	private final JCheckBox hasTransparency = new JCheckBox("Check to enable");
	private final JCheckBox connectedSides = new JCheckBox("Check to enable");
	private final JCheckBox emissiveRendering = new JCheckBox("Check to enable");

	private final JCheckBox hasEnergyStorage = new JCheckBox("Check to enable energy storage");
	private final JCheckBox isFluidTank = new JCheckBox("Check to enable fluid storage");

	private final JSpinner energyInitial = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyCapacity = new JSpinner(new SpinnerNumberModel(400000, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyMaxReceive = new JSpinner(new SpinnerNumberModel(200, 0, Integer.MAX_VALUE, 1));
	private final JSpinner energyMaxExtract = new JSpinner(new SpinnerNumberModel(200, 0, Integer.MAX_VALUE, 1));
	private final JSpinner fluidCapacity = new JSpinner(new SpinnerNumberModel(8000, 0, Integer.MAX_VALUE, 1));
	private FluidListField fluidRestrictions;

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator, ElementUtil.loadStepSounds());

	private final JCheckBox isReplaceable = new JCheckBox("Check to enable");
	private final JComboBox<String> colorOnMap = new JComboBox<>();
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final JSpinner minGenerateHeight = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner maxGenerateHeight = new JSpinner(new SpinnerNumberModel(64, 0, 256, 1));
	private final JSpinner frequencyPerChunks = new JSpinner(new SpinnerNumberModel(10, 1, 64, 1));
	private final JSpinner frequencyOnChunk = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));
	private BiomeListField restrictionBiomes;
	private MCItemListField blocksToReplace;
	private DimensionListField spawnWorldTypes;

	private final JCheckBox plantsGrowOn = new JCheckBox("Check to enable");
	private final JCheckBox isBeaconBase = new JCheckBox("Check to enable");
	private final JCheckBox isLadder = new JCheckBox("Check to enable");

	private final JComboBox<String> reactionToPushing = new JComboBox<>(
			new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "NONE", "XZ", "XYZ" });
	private final JComboBox<String> aiPathNodeType = new JComboBox<>();

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final JComboBox<String> particleToSpawn = new JComboBox<>(ElementUtil.loadParticles());

	private final JComboBox<String> particleSpawningShape = new JComboBox<>(
			new String[] { "Spread", "Top", "Tube", "Plane" });

	private final JSpinner particleSpawningRadious = new JSpinner(new SpinnerNumberModel(0.5, 0, 2, 0.1f));
	private final JSpinner particleAmount = new JSpinner(new SpinnerNumberModel(4, 0, 1000, 1));
	private final JSpinner slipperiness = new JSpinner(new SpinnerNumberModel(0.6, 0.01, 5, 0.1));

	private final JComboBox<String> rotationMode = new JComboBox<>(
			new String[] { "<html>No rotation<br><small>Fixed block orientation",
					"<html>Y axis rotation (S/W/N/E)<br><small>Rotation from player side",
					"<html>D/U/N/S/W/E rotation<br><small>Rotation from player side",
					"<html>Y axis rotation (S/W/N/E)<br><small>Rotation from block face",
					"<html>D/U/N/S/W/E rotation<br><small>Rotation from block face",
					"<html>Log rotation (X/Y/Z)<br><small>Imitates vanilla log rotation" });

	private final JComboBox<String> destroyTool = new JComboBox<>(
			new String[] { "Not specified", "pickaxe", "axe", "shovel" });
	private final JSpinner breakHarvestLevel = new JSpinner(new SpinnerNumberModel(1, -1, 100, 1));

	private final JCheckBox spawnParticles = new JCheckBox("Spawn particles around block");

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model singleTexture = new Model.BuiltInModel("Single texture");
	private final Model cross = new Model.BuiltInModel("Cross model");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>();

	private final JComboBox<String> transparencyType = new JComboBox<>(
			new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" });

	private final JCheckBox hasInventory = new JCheckBox(
			"<html>Enable tile entity for this block. Check this box if you want any of these:<small>"
					+ "<br>- block that can contain items, can interact with hoppers and/or comparators"
					+ "<br>- block that can store NBT data tags"
					+ "<br>- block that has fluid tank or energy storage capabilities");

	private final JCheckBox openGUIOnRightClick = new JCheckBox("Enable");
	private final JComboBox<String> guiBoundTo = new JComboBox<>();

	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));
	private final JCheckBox inventoryDropWhenDestroyed = new JCheckBox("Enable");
	private final JCheckBox inventoryComparatorPower = new JCheckBox("Enable");

	private final VTextField outSlotIDs = new VTextField(18);
	private final VTextField inSlotIDs = new VTextField(18);

	private final JTextField specialInfo = new JTextField(20);

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page2group = new ValidationGroup();

	private final JComboBox<String> blockBase = new JComboBox<>(
			new String[] { "Default basic block", "Stairs", "Slab", "Fence", "Wall", "Leaves", "TrapDoor", "Pane",
					"Door", "FenceGate" });

	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	private final JCheckBox useLootTableForDrops = new JCheckBox("Use loot table for drops");

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

		blocksToReplace.setListElements(
				new ArrayList<>(Collections.singleton(new MItemBlock(mcreator.getWorkspace(), "Blocks.STONE"))));

		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator, "When block added",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onNeighbourBlockChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				"When neighbour block changes", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator, "Update tick",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onRandomUpdateEvent = new ProcedureSelector(this.withEntry("block/display_tick_update"), mcreator,
				"Client display random tick", ProcedureSelector.Side.CLIENT,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onDestroyedByPlayer = new ProcedureSelector(this.withEntry("block/when_destroyed_player"), mcreator,
				"When block destroyed by player",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				"When block destroyed by explosion", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onStartToDestroy = new ProcedureSelector(this.withEntry("block/when_destroy_start"), mcreator,
				"When player starts to destroy",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				"When entity collides in the block",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onEntityWalksOn = new ProcedureSelector(this.withEntry("block/when_entity_walks_on"), mcreator,
				"When entity walks on the block",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onBlockPlayedBy = new ProcedureSelector(this.withEntry("block/when_block_placed_by"), mcreator,
				"When block is placed by",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClicked = new ProcedureSelector(this.withEntry("block/when_right_clicked"), mcreator,
				"On block right clicked",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/direction:direction"));
		onRedstoneOn = new ProcedureSelector(this.withEntry("block/on_redstone_on"), mcreator, "Redstone on",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onRedstoneOff = new ProcedureSelector(this.withEntry("block/on_redstone_off"), mcreator, "Redstone off",
				Dependency.fromString("x:number/y:number/z:number/world:world"));

		particleCondition = new ProcedureSelector(this.withEntry("block/particle_condition"), mcreator,
				"Particle spawning condition", ProcedureSelector.Side.CLIENT, true, VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world"));

		generateCondition = new ProcedureSelector(this.withEntry("block/generation_condition"), mcreator,
				"Additional generation condition", VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world"))
				.setDefaultName("(no additional condition)");

		blockBase.addActionListener(e -> {
			renderType.setEnabled(true);
			mx.setEnabled(true);
			my.setEnabled(true);
			mz.setEnabled(true);
			Mx.setEnabled(true);
			My.setEnabled(true);
			Mz.setEnabled(true);
			rotationMode.setEnabled(true);
			hasGravity.setEnabled(true);
			hasTransparency.setEnabled(true);
			material.setEnabled(true);
			connectedSides.setEnabled(true);

			if (blockBase.getSelectedItem() != null && blockBase.getSelectedItem().equals("Pane")) {
				connectedSides.setEnabled(false);
				renderType.setEnabled(false);

				connectedSides.setSelected(false);
				renderType.setSelectedItem(singleTexture);

				if (!isEditingMode()) {
					transparencyType.setSelectedItem("CUTOUT_MIPPED");
				}
			} else if (blockBase.getSelectedItem() != null && blockBase.getSelectedItem().equals("Leaves")) {
				material.setEnabled(false);
				renderType.setEnabled(false);
				rotationMode.setEnabled(false);
				hasTransparency.setEnabled(false);
				transparencyType.setEnabled(false);

				material.setSelectedItem("LEAVES");
				renderType.setSelectedItem(singleTexture);
				rotationMode.setSelectedIndex(0);
				hasTransparency.setSelected(false);
				transparencyType.setSelectedItem("SOLID");

				if (!isEditingMode()) {
					lightOpacity.setValue(1);
				}
			} else if (blockBase.getSelectedItem() != null && blockBase.getSelectedIndex() != 0) {
				renderType.setSelectedItem(singleTexture);
				renderType.setEnabled(false);
				mx.setEnabled(false);
				my.setEnabled(false);
				mz.setEnabled(false);
				Mx.setEnabled(false);
				My.setEnabled(false);
				Mz.setEnabled(false);
				hasGravity.setEnabled(false);
				rotationMode.setEnabled(false);

				mx.setValue(0d);
				my.setValue(0d);
				mz.setValue(0d);
				Mx.setValue(1d);
				My.setValue(1d);
				Mz.setValue(1d);
				hasGravity.setSelected(false);
				rotationMode.setSelectedIndex(0);

				if (blockBase.getSelectedItem().equals("Wall") || blockBase.getSelectedItem().equals("Fence")
						|| blockBase.getSelectedItem().equals("TrapDoor") || blockBase.getSelectedItem().equals("Door")
						|| blockBase.getSelectedItem().equals("FenceGate")) {
					if (!isEditingMode()) {
						hasTransparency.setSelected(true);
						lightOpacity.setValue(0);
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

		pane8.setOpaque(false);

		JPanel destal = new JPanel(new GridLayout(3, 4));
		destal.setOpaque(false);

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureTop = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));

		textureLeft = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureFront = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureRight = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureBack = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));

		itemTexture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Item"), 32);

		itemTexture.setOpaque(false);
		texture.setOpaque(false);
		textureTop.setOpaque(false);
		textureLeft.setOpaque(false);
		textureFront.setOpaque(false);
		textureRight.setOpaque(false);
		textureBack.setOpaque(false);

		isReplaceable.setOpaque(false);

		destal.add(new JLabel());
		destal.add(ComponentUtils.squareAndBorder(textureTop, "Top"));
		destal.add(new JLabel());
		destal.add(new JLabel());

		destal.add(ComponentUtils.squareAndBorder(textureLeft, new Color(126, 196, 255), "Left"));
		destal.add(ComponentUtils.squareAndBorder(textureFront, "Front"));
		destal.add(ComponentUtils.squareAndBorder(textureRight, "Right"));
		destal.add(ComponentUtils.squareAndBorder(textureBack, "Back"));

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
		destal.add(ComponentUtils.squareAndBorder(texture, new Color(125, 255, 174), "Bottom / main"));
		destal.add(new JLabel());
		destal.add(new JLabel());

		JPanel sbbp2 = new JPanel(new BorderLayout(1, 5));

		JPanel sbbp22 = new JPanel();

		sbbp22.setOpaque(false);
		sbbp2.setOpaque(false);

		plantsGrowOn.setOpaque(false);
		isBeaconBase.setOpaque(false);

		sbbp22.add(destal);

		sbbp22.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1), "Block textures",
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel topnbot = new JPanel(new BorderLayout());
		topnbot.setOpaque(false);

		topnbot.add("Center", sbbp22);

		JPanel txblock3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		txblock3.setOpaque(false);
		txblock3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Special information", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		ComponentUtils.deriveFont(specialInfo, 16);

		txblock3.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"), new JLabel(
				"<html>Special information about the block:<br><small>Separate entries with comma,<br>to use comma in description use \\,")));
		txblock3.add(specialInfo);

		topnbot.add("South", txblock3);

		sbbp2.add("Center", topnbot);

		JPanel render = new JPanel();
		render.setLayout(new BoxLayout(render, BoxLayout.PAGE_AXIS));

		ComponentUtils.deriveFont(transparencyType, 16);
		ComponentUtils.deriveFont(blockBase, 16);

		JPanel transparencySettings = new JPanel(new GridLayout(3, 2, 0, 2));
		transparencySettings.setOpaque(false);

		transparencySettings.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/transparency_type"), new JLabel("Transparency type:")));
		transparencySettings.add(transparencyType);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/has_transparency"),
				new JLabel("Check this if your block has transparent parts:")));
		transparencySettings.add(hasTransparency);

		transparencySettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/connected_sides"),
				new JLabel("Check this to enable connected sides:")));
		transparencySettings.add(connectedSides);

		ComponentUtils.deriveFont(renderType, 16);
		ComponentUtils.deriveFont(rotationMode, 16);

		JPanel rent = new JPanel(new GridLayout(2, 2, 0, 2));
		rent.setOpaque(false);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/model"),
				new JLabel("<html>Block model:<br><small>Supported: Built-in, JSON, OBJ")));
		rent.add(renderType);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/rotation_mode"),
				new JLabel("<html>Block rotation mode:<br><small>Select how this mod should rotate")));
		rent.add(rotationMode);

		renderType.setPreferredSize(new Dimension(320, 42));
		rotationMode.setPreferredSize(new Dimension(320, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		hasTransparency.setFont(hasTransparency.getFont().deriveFont(12.0f));

		mx.setOpaque(false);
		my.setOpaque(false);
		mz.setOpaque(false);
		Mx.setOpaque(false);
		My.setOpaque(false);
		Mz.setOpaque(false);

		JPanel bound = new JPanel(new GridLayout(6, 2, 0, 2));
		bound.setOpaque(false);

		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Min. X coord: ")));
		bound.add(mx);
		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Min. Y coord: ")));
		bound.add(my);
		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Min. Z coord: ")));
		bound.add(mz);
		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Max. X coord: ")));
		bound.add(Mx);
		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Max. Y coord: ")));
		bound.add(My);
		bound.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bounding_box"), new JLabel("Max. Z coord: ")));
		bound.add(Mz);

		bound.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Block bounding box", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Render type and rotation", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		transparencySettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1), "Transparency", 0,
				0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel txblock4 = new JPanel(new BorderLayout());
		txblock4.setOpaque(false);
		txblock4.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Block base (model and behaviour) and item texture", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		txblock4.add("Center", PanelUtils.gridElements(2, 2, HelpUtils.wrapWithHelpButton(this.withEntry("block/base"),
				new JLabel("<html>Block base:<br><small>Most blocks should leave this on default")), blockBase,
				HelpUtils.wrapWithHelpButton(this.withEntry("block/item_texture"), new JLabel(
						"<html>Block item texture:<br><small>Optional texture that will override block in inventory and hand")),
				PanelUtils.centerInPanel(itemTexture)));

		render.add(txblock4);
		render.add(rent);
		render.add(transparencySettings);
		render.add(bound);

		mx.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());
		my.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());
		mz.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());
		Mx.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());
		My.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());
		Mz.addChangeListener(event -> updateParametersBasedOnBoundingBoxSize());

		int as = 40;
		int bs = 20;

		mx.setPreferredSize(new Dimension(as, bs));
		my.setPreferredSize(new Dimension(as, bs));
		mz.setPreferredSize(new Dimension(as, bs));
		Mx.setPreferredSize(new Dimension(as, bs));
		My.setPreferredSize(new Dimension(as, bs));
		Mz.setPreferredSize(new Dimension(as, bs));

		render.setOpaque(false);

		hasTransparency.setOpaque(false);
		connectedSides.setOpaque(false);
		emissiveRendering.setOpaque(false);

		sbbp2.add("East", PanelUtils.pullElementUp(render));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel selp = new JPanel(new GridLayout(12, 2, 0, 2));
		JPanel selp3 = new JPanel(new GridLayout(8, 2, 0, 2));

		JPanel advancedProperties = new JPanel(new GridLayout(15, 2, 0, 2));

		hasGravity.setOpaque(false);
		tickRandomly.setOpaque(false);
		unbreakable.setOpaque(false);
		useLootTableForDrops.setOpaque(false);

		selp3.setOpaque(false);
		advancedProperties.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		hardness.setOpaque(false);
		resistance.setOpaque(false);
		lightOpacity.setOpaque(false);
		isNotColidable.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Name in GUI:")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/material"), new JLabel("Material:")));
		selp.add(material);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Creative inventory tab:")));
		selp.add(creativeTab);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/block_sound"), new JLabel("Block sound:")));
		selp.add(soundOnStep);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hardness"), new JLabel("Hardness:")));
		selp.add(hardness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"), new JLabel("Resistance:")));
		selp.add(resistance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/slipperiness"),
				new JLabel("<html>Block slipperiness:<br><small>Normal slipperiness is 0.6")));
		selp.add(slipperiness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"), new JLabel("Luminance:")));
		selp.add(luminance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/light_opacity"), new JLabel("Light opacity:")));
		selp.add(lightOpacity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/has_gravity"), new JLabel("Has gravity?")));
		selp.add(hasGravity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/can_walk_through"),
				new JLabel("Can walk through the block?")));
		selp.add(isNotColidable);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				new JLabel("Enable emissive rendering (glow):")));
		selp.add(emissiveRendering);

		creativeTab.setPrototypeDisplayValue(new DataListEntry.Dummy("BUILDING_BLOCKS"));
		creativeTab.addPopupMenuListener(new ComboBoxFullWidthPopup());

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				new JLabel("<html>Custom drop:<br><small>Leave empty to make it drop itself")));
		selp3.add(PanelUtils.centerInPanel(customDrop));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"), new JLabel("Drop amount:")));
		selp3.add(dropAmount);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				new JLabel("<html>Use loot table for drops:<br><small>If checked, you need to define loot table for drops")));
		selp3.add(PanelUtils.centerInPanel(useLootTableForDrops));

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"), new JLabel(
				"<html>Creative pick item:<br><small>" + "Leave empty for default creative mode pick item")));
		selp3.add(PanelUtils.centerInPanel(creativePickItem));

		selp3.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/harvest_tool"), new JLabel("Tool able to destroy it:")));
		selp3.add(destroyTool);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/harvest_level"), new JLabel(
				"<html>Tool harvest level to break with drop:<br><small>Only works with certain materials (e.g. ROCK)")));
		selp3.add(breakHarvestLevel);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/unbreakable"), new JLabel("Is unbreakable?")));
		selp3.add(unbreakable);

		selp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/replaceable"), new JLabel("Is replaceable?")));
		selp3.add(isReplaceable);

		advancedProperties
				.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tick_rate"), new JLabel("Tick rate: ")));
		advancedProperties.add(tickRate);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tick_randomly"),
				new JLabel("<html>Tick randomly:" + "<br><small>Ticks randomly with global tick rate factor")));
		advancedProperties.add(tickRandomly);

		tickRandomly.addActionListener(e -> tickRate.setEnabled(!tickRandomly.isSelected()));

		advancedProperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/color_on_map"), new JLabel("Block color on the map:")));
		advancedProperties.add(colorOnMap);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/can_plants_grow"),
				new JLabel("Can plants sustain on this block?")));
		advancedProperties.add(plantsGrowOn);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/beacon_color_modifier"),
				new JLabel("Beacon color modifier:")));
		advancedProperties.add(beaconColorModifier);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_beacon_base"),
				new JLabel("Can this block be beacon base?")));
		advancedProperties.add(isBeaconBase);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_waterloggable"),
				new JLabel("<html>Is block waterloggable?:<br><small>Block must not be full for this to work and block base must be normal")));
		advancedProperties.add(isWaterloggable);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_ladder"),
				new JLabel("<html>Does block act like ladder?<br><small>Block must not be full for this to work")));
		advancedProperties.add(isLadder);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/redstone_connect"),
				new JLabel("Does redstone connect to this block?")));
		advancedProperties.add(canProvidePower);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/enchantments_bonus"),
				new JLabel("Enchantments power bonus:")));
		advancedProperties.add(enchantPowerBonus);

		advancedProperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/flammability"), new JLabel("Block flammability:")));
		advancedProperties.add(flammability);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				new JLabel("<html>Fire spreading speed:<br><small>Leave 0 for vanilla handling")));
		advancedProperties.add(fireSpreadSpeed);

		advancedProperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/push_reaction"), new JLabel("Reaction to being pushed:")));
		advancedProperties.add(reactionToPushing);

		advancedProperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/ai_path_node_type"), new JLabel("AI path node type:")));
		advancedProperties.add(aiPathNodeType);

		advancedProperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/offset_type"), new JLabel("Random model offset:")));
		advancedProperties.add(offsetType);

		isWaterloggable.setOpaque(false);
		canProvidePower.setOpaque(false);
		isLadder.setOpaque(false);

		blockBase.addActionListener(e -> {
				isWaterloggable.setEnabled(blockBase.getSelectedIndex() == 0);
				isWaterloggable.setSelected(false);
		});

		useLootTableForDrops.addActionListener(e -> {
			customDrop.setEnabled(!useLootTableForDrops.isSelected());
			dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		});

		isWaterloggable.addActionListener(e -> {
			hasGravity.setEnabled(!isWaterloggable.isSelected());
			hasGravity.setSelected(false);
		});

		selp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"General properties", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(), Color.white));
		selp3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Dropping properties", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(), Color.white));

		advancedProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Advanced block properties", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
				Color.white));

		JComponent ploca = PanelUtils.westAndEastElement(selp, PanelUtils.pullElementUp(selp3));

		ploca.setOpaque(false);
		selp.setOpaque(false);

		pane3.add("Center", PanelUtils.totalCenterInPanel(ploca));
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
				new JLabel("<html>Bind this block to GUI:<br><small>Optional: block can have container but no GUI")));
		props.add(guiBoundTo);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/bind_gui_open"),
				new JLabel("Open bound GUI on right click:")));
		props.add(openGUIOnRightClick);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/inventory_size"), new JLabel(
				"<html>Size of inventory (slot count):<br><small>"
						+ "Usually this value should be the <i>biggest slot ID in the GUI this block is bound to + 1</i>")));
		props.add(inventorySize);

		props.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/inventory_stack_size"), new JLabel("Max size of stack:")));
		props.add(inventoryStackSize);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_inventory_items"),
				new JLabel("Drop items from inventory when block destroyed:")));
		props.add(inventoryDropWhenDestroyed);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/comparator_data"),
				new JLabel("Enable block output comparator data:")));
		props.add(inventoryComparatorPower);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/input_slots"), new JLabel(
				"<html>Disable automation (eg. hoppers) taking from these slots"
						+ "<br><small>Enter comma separated list of slot IDs")));
		props.add(inSlotIDs);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/output_slots"), new JLabel(
				"<html>Disable automation (eg. hoppers) inserting into these slots"
						+ "<br><small>Enter comma separated list of slot IDs")));
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
						if (generatableElement instanceof GUI) {
							GUI gui = (GUI) generatableElement;
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1), "Energy storage",
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		fluidTank.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1), "Fluid tank", 0,
				0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		hasEnergyStorage.setOpaque(false);
		isFluidTank.setOpaque(false);

		energyStorage.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("energy/enable_storage"), new JLabel("Enable energy storage:")));
		energyStorage.add(hasEnergyStorage);

		energyStorage.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("energy/initial_energy"), new JLabel("Initial energy:")));
		energyStorage.add(energyInitial);

		energyStorage.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("energy/energy_capacity"), new JLabel("Maximal energy capacity:")));
		energyStorage.add(energyCapacity);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/max_receive"),
				new JLabel("<html>Max energy receive:<br><small>Per insertion")));
		energyStorage.add(energyMaxReceive);

		energyStorage.add(HelpUtils.wrapWithHelpButton(this.withEntry("energy/max_extract"),
				new JLabel("<html>Max energy extract:<br><small>Per extraction")));
		energyStorage.add(energyMaxExtract);

		fluidTank.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("fluidtank/enable_storage"), new JLabel("Enable fluid storage:")));
		fluidTank.add(isFluidTank);

		fluidTank.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluidtank/fluid_capacity"),
				new JLabel("<html>Maximal fluid capacity:<br><small>Unit: milibuckets")));
		fluidTank.add(fluidCapacity);

		fluidTank.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluidtank/fluid_restrictions"), new JLabel(
				"<html>Restrict accepted fluids:<br><small>Empty means not restriction. Include flowing and still fluid")));
		fluidTank.add(fluidRestrictions);

		pane10.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(
				new JLabel("Note: Tile entity needs to be enabled for energy storage and fluid tanks to work."),
				PanelUtils.westAndEastElement(energyStorage,
						PanelUtils.northAndCenterElement(fluidTank, new JEmptyBox())), 10, 10)));

		hasInventory.addActionListener(e -> refreshFiledsTileEntity());
		refreshFiledsTileEntity();

		props.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Inventory settings", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		invblock.add("Center", props);

		invblock.add("North", HelpUtils.wrapWithHelpButton(this.withEntry("block/has_inventory"), hasInventory));

		pane8.add("Center", PanelUtils.totalCenterInPanel(invblock));

		JPanel enderpanel2 = new JPanel(new BorderLayout(30, 15));

		JPanel slip = new JPanel(new GridLayout(7, 2, 20, 2));

		slip.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				new JLabel("Dimensions to generate in (leave empty to disable spawning):")));
		slip.add(spawnWorldTypes);

		slip.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_replace_blocks"),
				new JLabel("Blocks this block can replace:")));
		slip.add(blocksToReplace);

		slip.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				new JLabel("Restriction biomes (leave empty for no restriction):")));
		slip.add(restrictionBiomes);

		slip.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_chunk_count"), new JLabel(
				"<html>Average amount of ore groups per chunk:<br><small>Setting this value too high (>32) might crash world generator")));
		slip.add(frequencyPerChunks);

		slip.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_group_size"), new JLabel(
				"<html>Average number of ores in a group:<br><small>Setting this value too high (>32) might crash world generator")));
		slip.add(frequencyOnChunk);

		slip.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/gen_min_height"), new JLabel("Minimal generation height:")));
		slip.add(minGenerateHeight);
		slip.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/gen_max_height"), new JLabel("Maximal generation height:")));
		slip.add(maxGenerateHeight);

		slip.setOpaque(false);

		enderpanel2.add("West", PanelUtils.totalCenterInPanel(new JLabel(UIRES.get("chunk"))));
		enderpanel2.add("Center",
				PanelUtils.northAndCenterElement(slip, PanelUtils.join(FlowLayout.LEFT, generateCondition)));

		enderpanel2.setOpaque(false);

		JPanel particleParameters = new JPanel(new GridLayout(5, 2, 0, 2));

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_particles"), spawnParticles));
		particleParameters.add(new JLabel());

		particleParameters
				.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_type"), new JLabel("Particle type: ")));
		particleParameters.add(particleToSpawn);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_shape"),
				new JLabel("<html>Particle spawning shape:<br><small>Determines shape boundary")));
		particleParameters.add(particleSpawningShape);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_spawn_radius"),
				new JLabel("Average particle spawning radius: ")));
		particleParameters.add(particleSpawningRadious);

		particleParameters.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gen_average_amount"),
				new JLabel("Average particle amount: ")));
		particleParameters.add(particleAmount);

		JComponent parpar = PanelUtils
				.northAndCenterElement(particleParameters, PanelUtils.join(FlowLayout.LEFT, particleCondition));

		parpar.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				"Particle properties", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		particleParameters.setOpaque(false);

		particleSpawningRadious.setOpaque(false);
		spawnParticles.setOpaque(false);

		renderType.addActionListener(e -> {
			Model selected = renderType.getSelectedItem();
			if (selected != null) {
				if (!selected.equals(normal) && !selected.equals(singleTexture)) {
					hasTransparency.setSelected(true);
					lightOpacity.setValue(0);
				}
			}
		});

		pane7.add(PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(advancedProperties, PanelUtils.pullElementUp(parpar))));

		pane7.setOpaque(false);
		pane9.setOpaque(false);

		pane9.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(enderpanel2)));

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);

		name.setValidator(new TextFieldValidator(name, "Block must have a name"));
		name.enableRealtimeValidation();

		page2group.addValidationElement(name);

		addPage("Visual", pane2);
		addPage("Properties", pane3);
		addPage("Advanced properties", pane7);
		addPage("Tile entity", pane8);
		addPage("Energy & fluid storage", pane10);
		addPage("Triggers", pane4);
		addPage("Generation", pane9);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void refreshFiledsTileEntity() {
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

	private void updateParametersBasedOnBoundingBoxSize() {
		if ((Double) mx.getValue() != 0 || (Double) my.getValue() != 0 || (Double) mz.getValue() != 0
				|| (Double) Mx.getValue() != 1 || (Double) My.getValue() != 1 || (Double) Mz.getValue() != 1) {
			hasTransparency.setSelected(true);
			hasTransparency.setEnabled(false);
		} else {
			hasTransparency.setSelected(false);
			hasTransparency.setEnabled(true);
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

		particleCondition.refreshListKeepSelected();
		generateCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(normal, singleTexture, cross),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(guiBoundTo, ListUtils.merge(Collections.singleton("<NONE>"),
				mcreator.getWorkspace().getModElements().stream().filter(var -> var.getType() == ModElementType.GUI)
						.map(ModElement::getName).collect(Collectors.toList())), "<NONE>");

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(colorOnMap, Arrays.asList(ElementUtil.loadMapColors()), "DEFAULT");
		ComboBoxUtil.updateComboBoxContents(aiPathNodeType, Arrays.asList(ElementUtil.loadPathNodeTypes()), "DEFAULT");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		else if (page == 1)
			return new AggregatedValidationResult(page2group);
		else if (page == 3)
			return new AggregatedValidationResult(outSlotIDs, inSlotIDs);
		else if (page == 6) {
			if ((int) minGenerateHeight.getValue() >= (int) maxGenerateHeight.getValue()) {
				return new AggregatedValidationResult.FAIL(
						"Minimal generation height size can't be bigger than the minimal height");
			}
		}
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Block block) {
		itemTexture.setTextureFromTextureName(block.itemTexture);
		texture.setTextureFromTextureName(block.texture);
		textureTop.setTextureFromTextureName(block.textureTop);
		textureLeft.setTextureFromTextureName(block.textureLeft);
		textureFront.setTextureFromTextureName(block.textureFront);
		textureRight.setTextureFromTextureName(block.textureRight);
		textureBack.setTextureFromTextureName(block.textureBack);
		mx.setValue(block.mx);
		my.setValue(block.my);
		mz.setValue(block.mz);
		Mx.setValue(block.Mx);
		My.setValue(block.My);
		Mz.setValue(block.Mz);
		guiBoundTo.setSelectedItem(block.guiBoundTo);
		rotationMode.setSelectedIndex(block.rotationMode);
		enchantPowerBonus.setValue(block.enchantPowerBonus);
		hasTransparency.setSelected(block.hasTransparency);
		connectedSides.setSelected(block.connectedSides);
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
		name.setText(block.name);
		maxGenerateHeight.setValue(block.maxGenerateHeight);
		minGenerateHeight.setValue(block.minGenerateHeight);
		frequencyPerChunks.setValue(block.frequencyPerChunks);
		frequencyOnChunk.setValue(block.frequencyOnChunk);
		spawnParticles.setSelected(block.spawnParticles);
		particleToSpawn.setSelectedItem(block.particleToSpawn.getUnmappedValue());
		particleSpawningShape.setSelectedItem(block.particleSpawningShape);
		particleCondition.setSelectedProcedure(block.particleCondition);
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
		luminance.setValue(block.luminance);
		breakHarvestLevel.setValue(block.breakHarvestLevel);
		customDrop.setBlock(block.customDrop);
		dropAmount.setValue(block.dropAmount);
		isNotColidable.setSelected(block.isNotColidable);
		unbreakable.setSelected(block.unbreakable);
		canProvidePower.setSelected(block.canProvidePower);
		lightOpacity.setValue(block.lightOpacity);
		material.setSelectedItem(block.material.getUnmappedValue());
		transparencyType.setSelectedItem(block.transparencyType);

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
		isBeaconBase.setSelected(block.isBeaconBase);

		spawnWorldTypes.setListElements(block.spawnWorldTypes);
		blocksToReplace.setListElements(block.blocksToReplace);
		restrictionBiomes.setListElements(block.restrictionBiomes);
		fluidRestrictions.setListElements(block.fluidRestrictions);

		isReplaceable.setSelected(block.isReplaceable);
		colorOnMap.setSelectedItem(block.colorOnMap);
		offsetType.setSelectedItem(block.offsetType);
		aiPathNodeType.setSelectedItem(block.aiPathNodeType);
		creativePickItem.setBlock(block.creativePickItem);

		beaconColorModifier.setColor(block.beaconColorModifier);

		flammability.setValue(block.flammability);
		fireSpreadSpeed.setValue(block.fireSpreadSpeed);

		isLadder.setSelected(block.isLadder);
		reactionToPushing.setSelectedItem(block.reactionToPushing);
		slipperiness.setValue(block.slipperiness);

		specialInfo.setText(
				block.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		refreshFiledsTileEntity();

		tickRate.setEnabled(!tickRandomly.isSelected());

		Model model = block.getItemModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
			renderType.setSelectedItem(model);

		customDrop.setEnabled(!useLootTableForDrops.isSelected());
		dropAmount.setEnabled(!useLootTableForDrops.isSelected());

		hasGravity.setEnabled(!isWaterloggable.isSelected());

		isWaterloggable.setEnabled(blockBase.getSelectedIndex() == 0);
	}

	@Override public Block getElementFromGUI() {
		Block block = new Block(modElement);
		block.name = name.getText();
		block.hasTransparency = hasTransparency.isSelected();
		block.connectedSides = connectedSides.isSelected();
		block.transparencyType = (String) transparencyType.getSelectedItem();
		block.mx = (double) mx.getValue();
		block.my = (double) my.getValue();
		block.mz = (double) mz.getValue();
		block.Mx = (double) Mx.getValue();
		block.My = (double) My.getValue();
		block.Mz = (double) Mz.getValue();
		block.guiBoundTo = (String) guiBoundTo.getSelectedItem();
		block.rotationMode = rotationMode.getSelectedIndex();
		block.enchantPowerBonus = (double) enchantPowerBonus.getValue();
		block.hardness = (double) hardness.getValue();
		block.resistance = (double) resistance.getValue();
		block.hasGravity = hasGravity.isSelected();
		block.isWaterloggable = isWaterloggable.isSelected();
		block.emissiveRendering = emissiveRendering.isSelected();
		block.tickRandomly = tickRandomly.isSelected();
		block.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		block.destroyTool = (String) destroyTool.getSelectedItem();
		block.isBeaconBase = isBeaconBase.isSelected();
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
		block.canProvidePower = canProvidePower.isSelected();
		block.lightOpacity = (int) lightOpacity.getValue();
		block.material = new Material(mcreator.getWorkspace(), material.getSelectedItem());
		block.tickRate = (int) tickRate.getValue();
		block.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		block.luminance = (double) luminance.getValue();
		block.unbreakable = unbreakable.isSelected();
		block.breakHarvestLevel = (int) breakHarvestLevel.getValue();
		block.spawnParticles = spawnParticles.isSelected();
		block.particleToSpawn = new Particle(mcreator.getWorkspace(), (String) particleToSpawn.getSelectedItem());
		block.particleSpawningShape = (String) particleSpawningShape.getSelectedItem();
		block.particleSpawningRadious = (double) particleSpawningRadious.getValue();
		block.particleAmount = (int) particleAmount.getValue();
		block.particleCondition = particleCondition.getSelectedProcedure();
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
		block.texture = texture.getID();
		block.itemTexture = itemTexture.getID();
		block.textureTop = textureTop.getID();
		block.textureLeft = textureLeft.getID();
		block.textureFront = textureFront.getID();
		block.textureRight = textureRight.getID();
		block.textureBack = textureBack.getID();

		block.beaconColorModifier = beaconColorModifier.getColor();

		block.spawnWorldTypes = spawnWorldTypes.getListElements();
		block.restrictionBiomes = restrictionBiomes.getListElements();
		block.fluidRestrictions = fluidRestrictions.getListElements();
		block.blocksToReplace = blocksToReplace.getListElements();

		block.isReplaceable = isReplaceable.isSelected();
		block.colorOnMap = (String) colorOnMap.getSelectedItem();
		block.offsetType = (String) offsetType.getSelectedItem();
		block.aiPathNodeType = (String) aiPathNodeType.getSelectedItem();
		block.creativePickItem = creativePickItem.getBlock();

		block.flammability = (int) flammability.getValue();
		block.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();

		block.isLadder = isLadder.isSelected();
		block.reactionToPushing = (String) reactionToPushing.getSelectedItem();
		block.slipperiness = (double) slipperiness.getValue();

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
			block.renderType = 11;
		else if (model.equals(cross))
			block.renderType = 12;
		block.customModelName = model.getReadableName();

		return block;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-block");
	}

}
