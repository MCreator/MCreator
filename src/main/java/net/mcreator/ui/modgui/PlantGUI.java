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
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Plant;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.resources.Model;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlantGUI extends ModElementGUI<Plant> {

	private TextureHolder texture;

	private final JLabel stl = new JLabel(TiledImageCache.plantStaticYes);
	private final JLabel dyn = new JLabel(TiledImageCache.plantGrowingNo);

	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(0, -1, 64000, 0.1));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(0, 0, 64000, 0.5));
	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0.00, 0, 1, 0.01));
	private final JSpinner frequencyOnChunks = new JSpinner(new SpinnerNumberModel(5, 0, 40, 1));
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 200, 1));

	private final JCheckBox useLootTableForDrops = new JCheckBox("Use loot table for drops");
	private final JCheckBox unbreakable = new JCheckBox("Check to enable");
	private final JCheckBox forceTicking = new JCheckBox("Check to enable");
	private final JCheckBox hasTileEntity = new JCheckBox("Check to enable");
	private final JCheckBox emissiveRendering = new JCheckBox("Check to enable");

	private final VTextField name = new VTextField(18);

	private final JTextField specialInfo = new JTextField(20);

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator, ElementUtil.loadStepSounds());

	private final JCheckBox isReplaceable = new JCheckBox("Is replaceable");
	private final JComboBox<String> colorOnMap = new JComboBox<>();
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final Model cross = new Model.BuiltInModel("Cross model");
	private final Model crop = new Model.BuiltInModel("Crop model");
	private final JRadioButton normalType = new JRadioButton("<html><b>Use static plant type");
	private final JComboBox<String> growapableSpawnType = new JComboBox<>(
			new String[] { "Plains", "Desert", "Beach", "Cave", "Water", "Nether", "Crop" });
	private final JSpinner growapableMaxHeight = new JSpinner(new SpinnerNumberModel(3, 1, 14, 1));

	private final JComboBox<String> staticPlantGenerationType = new JComboBox<>(new String[] { "Flower", "Grass" });

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);
	private final JRadioButton growapableType = new JRadioButton("<html><b>Use growable plant type");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>();

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "XZ", "XYZ", "NONE" });
	private final JComboBox<String> aiPathNodeType = new JComboBox<>();

	private ProcedureSelector onNeighbourBlockChanges;
	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onDestroyedByPlayer;
	private ProcedureSelector onDestroyedByExplosion;
	private ProcedureSelector onStartToDestroy;
	private ProcedureSelector onEntityCollides;
	private ProcedureSelector onRightClicked;

	private ProcedureSelector generateCondition;

	private DimensionListField spawnWorldTypes;
	private BiomeListField restrictionBiomes;

	private final ValidationGroup page2group = new ValidationGroup();

	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	public PlantGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		restrictionBiomes = new BiomeListField(mcreator);

		onNeighbourBlockChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				"When neighbour block changes", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator, "Update tick",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onDestroyedByPlayer = new ProcedureSelector(this.withEntry("block/when_destroyed_player"), mcreator,
				"When plant destroyed by player",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				"When plant destroyed by explosion", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onStartToDestroy = new ProcedureSelector(this.withEntry("block/when_destroy_start"), mcreator,
				"When start to destroy", Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				"When mob/player collides with plant",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onRightClicked = new ProcedureSelector(this.withEntry("block/when_right_clicked"), mcreator,
				"When plant right clicked",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/direction:direction"));

		generateCondition = new ProcedureSelector(this.withEntry("block/generation_condition"), mcreator,
				"Additional generation condition", VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world"))
				.setDefaultName("(no additional condition)");

		spawnWorldTypes = new DimensionListField(mcreator);
		spawnWorldTypes.setListElements(Collections.singletonList("Surface"));

		ComponentUtils.deriveFont(specialInfo, 16);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));
		JPanel destal = new JPanel(new BorderLayout(20, 20));

		destal.setOpaque(false);

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		texture.setOpaque(false);

		destal.add("South", PanelUtils.gridElements(1, 2, HelpUtils
						.wrapWithHelpButton(this.withEntry("item/special_information"), new JLabel(
								"<html>Special information about the plant:<br><small>Separate entries with comma, to use comma in description use \\,")),
				specialInfo));

		JPanel rent = new JPanel();
		rent.setLayout(new BoxLayout(rent, BoxLayout.PAGE_AXIS));
		rent.setOpaque(false);
		rent.add(PanelUtils.join(HelpUtils.wrapWithHelpButton(this.withEntry("block/model"),
				new JLabel("<html>Plant model:<br><small>Select the plant model to be used. Supported: JSON, OBJ")),
				PanelUtils.join(renderType)));
		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());
		rent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2), "Plant 3D model",
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		destal.add("Center", PanelUtils
				.westAndCenterElement(ComponentUtils.squareAndBorder(texture, "Plant texture"), rent, 10, 10));

		JPanel sbbp2 = new JPanel(new BorderLayout());

		JPanel sbbp22 = new JPanel(new BorderLayout(20, 50));

		sbbp22.setOpaque(false);
		sbbp2.setOpaque(false);

		sbbp22.add("North", destal);

		ButtonGroup bg = new ButtonGroup();
		bg.add(normalType);
		bg.add(growapableType);

		normalType.setSelected(true);

		normalType.setOpaque(false);
		growapableType.setOpaque(false);

		emissiveRendering.setOpaque(false);

		isReplaceable.setOpaque(false);

		ActionListener planttypeselected = event -> {
			if (normalType.isSelected())
				stl.setIcon(TiledImageCache.plantStaticYes);
			else
				stl.setIcon(TiledImageCache.plantStaticNo);
			if (growapableType.isSelected())
				dyn.setIcon(TiledImageCache.plantGrowingYes);
			else
				dyn.setIcon(TiledImageCache.plantGrowingNo);
		};

		normalType.addActionListener(planttypeselected);
		growapableType.addActionListener(planttypeselected);

		JPanel ptipe = new JPanel(new BorderLayout());
		ptipe.setOpaque(false);

		JPanel ptipe1 = new JPanel(new BorderLayout());
		ptipe1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Static plant type", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe1.add("West", stl);
		ptipe1.add("Center", PanelUtils.join(HelpUtils
						.wrapWithHelpButton(this.withEntry("plant/static_generation_type"), new JLabel("Generator type:")),
				staticPlantGenerationType));
		ptipe1.add("North", normalType);
		ptipe1.setOpaque(false);

		JPanel ptipe2 = new JPanel(new BorderLayout());
		ptipe2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Growable plant type", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe2.add("West", dyn);

		JPanel mlo = new JPanel(new GridLayout(1, 2));

		mlo.setOpaque(false);

		mlo.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/max_height"), new JLabel("Max. height: ")));
		mlo.add(growapableMaxHeight);

		ptipe2.add("Center", PanelUtils.join(mlo));
		ptipe2.add("North", growapableType);
		ptipe2.setOpaque(false);

		ptipe.add("West", ptipe1);
		ptipe.add("East", ptipe2);

		sbbp22.add("Center", PanelUtils.totalCenterInPanel(ptipe));

		sbbp2.add("North", sbbp22);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel selp = new JPanel(new GridLayout(10, 2, 25, 4));
		JPanel selp2 = new JPanel(new GridLayout(11, 2, 25, 4));

		useLootTableForDrops.setOpaque(false);
		unbreakable.setOpaque(false);
		forceTicking.setOpaque(false);
		hasTileEntity.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Name in GUI:")));
		selp.add(name);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Creative inventory tab:")));
		selp.add(creativeTab);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/color_on_map"), new JLabel("Plant color on the map:")));
		selp.add(colorOnMap);

		hardness.setOpaque(false);
		resistance.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hardness"), new JLabel("Hardness:")));
		selp.add(hardness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"), new JLabel("Resistance:")));
		selp.add(resistance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/block_sound"), new JLabel("Block sound:")));
		selp.add(soundOnStep);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"), new JLabel("Luminance:")));
		selp.add(luminance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/type"), new JLabel("Plant type: ")));
		selp.add(growapableSpawnType);

		selp.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/flammability"), new JLabel("Plant flammability:")));
		selp.add(flammability);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				new JLabel("<html>Fire spreading speed:<br><small>Leave 0 for vanilla handling")));
		selp.add(fireSpreadSpeed);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				new JLabel("Enable emissive rendering (glow):")));
		selp2.add(emissiveRendering);

		selp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("plant/force_ticking"), new JLabel("Force plant ticking?")));
		selp2.add(forceTicking);

		selp2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/unbreakable"), new JLabel("Is plant unbreakable?")));
		selp2.add(unbreakable);

		selp2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/replaceable"), new JLabel("Is plant replaceable?")));
		selp2.add(isReplaceable);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"),
				new JLabel("<html>Creative pick item:<br><small>Leave empty for default creative pick item")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, creativePickItem));

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				new JLabel("<html>Custom drop:<br><small>Leave empty for default drop")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, customDrop));

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"), new JLabel("Drop amount:")));
		selp2.add(dropAmount);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				new JLabel("<html>Use loot table for drops:<br><small>If checked, you need to define loot table for drops")));
		selp2.add(useLootTableForDrops);

		selp2.setOpaque(false);

		selp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/ai_path_node_type"), new JLabel("AI path node type:")));
		selp2.add(aiPathNodeType);

		selp2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("block/offset_type"), new JLabel("Random model offset:")));
		selp2.add(offsetType);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/has_tile_entity"), new JLabel(
				"<html>Has tile entity?<br>" + "<small>Enable ONLY if you want to use NBT tags with this plant")));
		selp2.add(hasTileEntity);

		dropAmount.setOpaque(false);

		selp.setOpaque(false);

		useLootTableForDrops.addActionListener(e -> {
			customDrop.setEnabled(!useLootTableForDrops.isSelected());
			dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		});

		pane3.add("Center", PanelUtils.totalCenterInPanel(PanelUtils
				.westAndEastElement(PanelUtils.centerInPanel(selp), PanelUtils.centerInPanel(selp2), 20, 20)));

		pane3.setOpaque(false);

		JPanel events2 = new JPanel(new GridLayout(3, 3, 8, 8));
		events2.setOpaque(false);
		events2.add(onNeighbourBlockChanges);
		events2.add(onTickUpdate);
		events2.add(onDestroyedByPlayer);
		events2.add(onDestroyedByExplosion);
		events2.add(onStartToDestroy);
		events2.add(onEntityCollides);
		events2.add(onRightClicked);
		events2.add(new JLabel(""));

		JPanel spawning = new JPanel(new GridLayout(3, 2, 5, 5));
		spawning.setOpaque(false);

		spawning.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("plant/gen_chunk_count"), new JLabel("Spawn frequency on chunks:")));
		spawning.add(frequencyOnChunks);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				new JLabel("Spawn in these dimensions (leave empty to disable spawning):")));
		spawning.add(spawnWorldTypes);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				new JLabel("Restriction biomes (leave empty for no restriction):")));
		spawning.add(restrictionBiomes);

		JPanel lastPan = new JPanel(new BorderLayout(15, 15));
		lastPan.setOpaque(false);
		lastPan.add("North", spawning);

		pane4.add("Center", PanelUtils.totalCenterInPanel(lastPan));
		pane4.add("South", PanelUtils.join(FlowLayout.LEFT, generateCondition));

		pane4.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		name.setValidator(new TextFieldValidator(name, "Plant needs a name"));
		name.enableRealtimeValidation();

		page2group.addValidationElement(name);

		addPage("Visual and type", pane2);
		addPage("Properties", pane3);
		addPage("Triggers", PanelUtils.totalCenterInPanel(events2));
		addPage("Generation", PanelUtils.totalCenterInPanel(pane4));

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onNeighbourBlockChanges.refreshListKeepSelected();
		onTickUpdate.refreshListKeepSelected();
		onDestroyedByPlayer.refreshListKeepSelected();
		onDestroyedByExplosion.refreshListKeepSelected();
		onStartToDestroy.refreshListKeepSelected();
		onEntityCollides.refreshListKeepSelected();
		onRightClicked.refreshListKeepSelected();

		generateCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("DECORATIONS"));

		ComboBoxUtil.updateComboBoxContents(colorOnMap, Arrays.asList(ElementUtil.loadMapColors()), "DEFAULT");

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(cross, crop),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(aiPathNodeType, Arrays.asList(ElementUtil.loadPathNodeTypes()), "DEFAULT");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(texture);
		else if (page == 1)
			return new AggregatedValidationResult(page2group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Plant plant) {
		texture.setTextureFromTextureName(plant.texture);
		name.setText(plant.name);
		hardness.setValue(plant.hardness);
		resistance.setValue(plant.resistance);
		soundOnStep.setSelectedItem(plant.soundOnStep.getUnmappedValue());
		luminance.setValue(plant.luminance);
		unbreakable.setSelected(plant.unbreakable);
		forceTicking.setSelected(plant.forceTicking);
		hasTileEntity.setSelected(plant.hasTileEntity);
		frequencyOnChunks.setValue(plant.frequencyOnChunks);
		emissiveRendering.setSelected(plant.emissiveRendering);
		useLootTableForDrops.setSelected(plant.useLootTableForDrops);
		customDrop.setBlock(plant.customDrop);
		dropAmount.setValue(plant.dropAmount);
		creativeTab.setSelectedItem(plant.creativeTab);
		onNeighbourBlockChanges.setSelectedProcedure(plant.onNeighbourBlockChanges);
		onTickUpdate.setSelectedProcedure(plant.onTickUpdate);
		onDestroyedByPlayer.setSelectedProcedure(plant.onDestroyedByPlayer);
		onDestroyedByExplosion.setSelectedProcedure(plant.onDestroyedByExplosion);
		onStartToDestroy.setSelectedProcedure(plant.onStartToDestroy);
		onEntityCollides.setSelectedProcedure(plant.onEntityCollides);
		onRightClicked.setSelectedProcedure(plant.onRightClicked);
		growapableMaxHeight.setValue(plant.growapableMaxHeight);
		spawnWorldTypes.setListElements(plant.spawnWorldTypes);
		restrictionBiomes.setListElements(plant.restrictionBiomes);
		isReplaceable.setSelected(plant.isReplaceable);
		colorOnMap.setSelectedItem(plant.colorOnMap);
		offsetType.setSelectedItem(plant.offsetType);
		aiPathNodeType.setSelectedItem(plant.aiPathNodeType);
		creativePickItem.setBlock(plant.creativePickItem);
		flammability.setValue(plant.flammability);
		fireSpreadSpeed.setValue(plant.fireSpreadSpeed);
		specialInfo.setText(
				plant.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		generateCondition.setSelectedProcedure(plant.generateCondition);

		Model model = plant.getItemModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
			renderType.setSelectedItem(model);

		if (plant.plantType.equals("normal")) {
			normalType.setSelected(true);
			growapableType.setSelected(false);
		} else {
			normalType.setSelected(false);
			growapableType.setSelected(true);
		}

		growapableSpawnType.setSelectedItem(plant.growapableSpawnType);
		staticPlantGenerationType.setSelectedItem(plant.staticPlantGenerationType);

		customDrop.setEnabled(!useLootTableForDrops.isSelected());
		dropAmount.setEnabled(!useLootTableForDrops.isSelected());

		if (normalType.isSelected())
			stl.setIcon(TiledImageCache.plantStaticYes);
		else
			stl.setIcon(TiledImageCache.plantStaticNo);
		if (growapableType.isSelected())
			dyn.setIcon(TiledImageCache.plantGrowingYes);
		else
			dyn.setIcon(TiledImageCache.plantGrowingNo);
	}

	@Override public Plant getElementFromGUI() {
		Plant plant = new Plant(modElement);
		plant.name = name.getText();
		plant.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		plant.texture = texture.getID();
		plant.plantType = normalType.isSelected() ? "normal" : "growapable";
		plant.growapableSpawnType = (String) growapableSpawnType.getSelectedItem();
		plant.staticPlantGenerationType = (String) staticPlantGenerationType.getSelectedItem();
		plant.growapableMaxHeight = (int) growapableMaxHeight.getValue();
		plant.hardness = (double) hardness.getValue();
		plant.resistance = (double) resistance.getValue();
		plant.luminance = (double) luminance.getValue();
		plant.unbreakable = unbreakable.isSelected();
		plant.forceTicking = forceTicking.isSelected();
		plant.hasTileEntity = hasTileEntity.isSelected();
		plant.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		plant.useLootTableForDrops = useLootTableForDrops.isSelected();
		plant.customDrop = customDrop.getBlock();
		plant.dropAmount = (int) dropAmount.getValue();
		plant.frequencyOnChunks = (int) frequencyOnChunks.getValue();
		plant.onNeighbourBlockChanges = onNeighbourBlockChanges.getSelectedProcedure();
		plant.onTickUpdate = onTickUpdate.getSelectedProcedure();
		plant.onDestroyedByPlayer = onDestroyedByPlayer.getSelectedProcedure();
		plant.onDestroyedByExplosion = onDestroyedByExplosion.getSelectedProcedure();
		plant.onStartToDestroy = onStartToDestroy.getSelectedProcedure();
		plant.onEntityCollides = onEntityCollides.getSelectedProcedure();
		plant.onRightClicked = onRightClicked.getSelectedProcedure();
		plant.spawnWorldTypes = spawnWorldTypes.getListElements();
		plant.restrictionBiomes = restrictionBiomes.getListElements();
		plant.isReplaceable = isReplaceable.isSelected();
		plant.colorOnMap = (String) colorOnMap.getSelectedItem();
		plant.offsetType = (String) offsetType.getSelectedItem();
		plant.aiPathNodeType = (String) aiPathNodeType.getSelectedItem();
		plant.creativePickItem = creativePickItem.getBlock();
		plant.flammability = (int) flammability.getValue();
		plant.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();
		plant.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());
		plant.generateCondition = generateCondition.getSelectedProcedure();
		plant.emissiveRendering = emissiveRendering.isSelected();

		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		plant.renderType = 12;
		if (model.getType() == Model.Type.JSON)
			plant.renderType = 2;
		else if (model.getType() == Model.Type.OBJ)
			plant.renderType = 3;
		else if (model.equals(cross))
			plant.renderType = 12;
		else if (model.equals(crop))
			plant.renderType = 13;
		plant.customModelName = model.getReadableName();

		return plant;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-plant");
	}

}
