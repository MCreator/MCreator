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
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
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
	private TextureHolder textureBottom;

	private TextureHolder itemTexture;
	private TextureHolder particleTexture;

	private final JLabel stl = new JLabel(TiledImageCache.plantStaticYes);
	private final JLabel dyn = new JLabel(TiledImageCache.plantGrowingNo);
	private final JLabel dbl = new JLabel(TiledImageCache.plantDoubleNo);

	private final JCheckBox customBoundingBox = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox disableOffset = L10N.checkbox("elementgui.common.enable");
	private final JBoundingBoxList boundingBoxList = new JBoundingBoxList(mcreator);

	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(0, -1, 64000, 0.1));
	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 0.5));
	private final JSpinner frequencyOnChunks = new JSpinner(new SpinnerNumberModel(5, 0, 40, 1));
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 200, 1));

	private final JCheckBox useLootTableForDrops = L10N.checkbox("elementgui.common.use_table_loot_drops");
	private final JCheckBox unbreakable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox forceTicking = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox hasTileEntity = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox emissiveRendering = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(18);

	private final JTextField specialInfo = new JTextField(20);

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator);

	private final JCheckBox isReplaceable = L10N.checkbox("elementgui.plant.is_replaceable");
	private final JComboBox<String> colorOnMap = new JComboBox<>();
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final Model cross = new Model.BuiltInModel("Cross model");
	private final Model crop = new Model.BuiltInModel("Crop model");
	private final JRadioButton normalType = L10N.radiobutton("elementgui.plant.use_static_plant_type");
	private final JComboBox<String> growapableSpawnType = new JComboBox<>(
			new String[] { "Plains", "Desert", "Beach", "Cave", "Water", "Nether", "Crop" });
	private final JSpinner growapableMaxHeight = new JSpinner(new SpinnerNumberModel(3, 1, 14, 1));

	private final JComboBox<String> staticPlantGenerationType = new JComboBox<>(new String[] { "Flower", "Grass" });

	private final JRadioButton doubleType = new JRadioButton("<html><b>Use double plant type");
	private final JComboBox<String> doublePlantGenerationType = new JComboBox<>(new String[] { "Flower", "Grass" });

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);
	private final JRadioButton growapableType = L10N.radiobutton("elementgui.plant.use_growable_plant_type");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>();

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "XZ", "XYZ", "NONE" });
	private final JComboBox<String> aiPathNodeType = new JComboBox<>();

	private final JComboBox<String> tintType = new JComboBox<>(
			new String[] { "No tint", "Grass", "Foliage", "Water", "Sky", "Fog", "Water fog" });
	private final JCheckBox isItemTinted = L10N.checkbox("elementgui.common.enable");

	private ProcedureSelector onBlockAdded;
	private ProcedureSelector onNeighbourBlockChanges;
	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onRandomUpdateEvent;
	private ProcedureSelector onDestroyedByPlayer;
	private ProcedureSelector onDestroyedByExplosion;
	private ProcedureSelector onStartToDestroy;
	private ProcedureSelector onEntityCollides;
	private ProcedureSelector onBlockPlacedBy;
	private ProcedureSelector onRightClicked;

	private ProcedureSelector generateCondition;

	private DimensionListField spawnWorldTypes;
	private BiomeListField restrictionBiomes;

	private final ValidationGroup page2group = new ValidationGroup();

	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(100, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(60, 0, 1024, 1));

	public PlantGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		restrictionBiomes = new BiomeListField(mcreator);

		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator,
				L10N.t("elementgui.plant.event_on_added"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onNeighbourBlockChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				L10N.t("elementgui.common.event_on_neighbour_block_changes"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator,
				L10N.t("elementgui.common.event_on_update_tick"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onRandomUpdateEvent = new ProcedureSelector(this.withEntry("block/display_tick_update"), mcreator,
				L10N.t("elementgui.common.event_on_random_update"), ProcedureSelector.Side.CLIENT,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onDestroyedByPlayer = new ProcedureSelector(this.withEntry("block/when_destroyed_player"), mcreator,
				L10N.t("elementgui.plant.event_on_destroyed_by_player"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				L10N.t("elementgui.plant.event_on_destroyed_by_explosion"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onStartToDestroy = new ProcedureSelector(this.withEntry("block/when_destroy_start"), mcreator,
				L10N.t("elementgui.plant.event_on_start_to_destroy"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				L10N.t("elementgui.plant.event_on_entity_collides"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onBlockPlacedBy = new ProcedureSelector(this.withEntry("block/when_block_placed_by"), mcreator,
				L10N.t("elementgui.common.event_on_block_placed_by"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClicked = new ProcedureSelector(this.withEntry("block/when_right_clicked"), mcreator,
				L10N.t("elementgui.plant.event_on_right_clicked"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/direction:direction"));

		generateCondition = new ProcedureSelector(this.withEntry("block/generation_condition"), mcreator,
				L10N.t("elementgui.plant.event_additional_generation_condition"), VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world"))
				.setDefaultName(L10N.t("elementgui.plant.no_additional_condition"));

		spawnWorldTypes = new DimensionListField(mcreator);
		spawnWorldTypes.setListElements(Collections.singletonList("Surface"));

		ComponentUtils.deriveFont(specialInfo, 16);
		ComponentUtils.deriveFont(tintType, 16);
		ComponentUtils.deriveFont(growapableSpawnType, 16);
		ComponentUtils.deriveFont(doublePlantGenerationType, 16);
		ComponentUtils.deriveFont(staticPlantGenerationType, 16);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));
		JPanel bbPane = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.BLOCK));
		textureBottom = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.BLOCK));
		texture.setOpaque(false);
		textureBottom.setOpaque(false);
		textureBottom.setVisible(false);

		itemTexture = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM), 32);
		itemTexture.setOpaque(false);
		particleTexture = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.BLOCK), 32);
		particleTexture.setOpaque(false);

		JPanel tintPanel = new JPanel(new GridLayout(1, 2, 0, 2));
		tintPanel.setOpaque(false);
		isItemTinted.setOpaque(false);
		tintPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.plant_info"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		tintPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"),
				L10N.label("elementgui.plant.special_information_tip")));
		tintPanel.add(specialInfo);

		JPanel rent = new JPanel(new GridLayout(5, 2, 2, 2));
		rent.setOpaque(false);

		rent.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/model"), L10N.label("elementgui.plant.block_model")));
		rent.add(renderType);

		rent.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/item_texture"), L10N.label("elementgui.plant.item_texture")));
		rent.add(PanelUtils.centerInPanel(itemTexture));

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/particle_texture"),
				L10N.label("elementgui.plant.particle_texture")));
		rent.add(PanelUtils.centerInPanel(particleTexture));

		rent.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/tint_type"), L10N.label("elementgui.common.tint_type")));
		rent.add(tintType);
		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_item_tinted"),
				L10N.label("elementgui.plant.is_item_tinted")));
		rent.add(isItemTinted);

		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		JPanel texturesAndRent = new JPanel(new BorderLayout(50, 0));
		texturesAndRent.setOpaque(false);

		texturesAndRent.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.gridElements(2, 1, ComponentUtils
						.squareAndBorder(texture, new Color(125, 255, 174), L10N.t("elementgui.plant.texture_place_top_main")),
				ComponentUtils.squareAndBorder(textureBottom, L10N.t("elementgui.plant.texture_place_bottom")))));
		texturesAndRent.add("East", rent);

		texturesAndRent.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.textures_and_model"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel render = new JPanel();
		render.setLayout(new BoxLayout(render, BoxLayout.PAGE_AXIS));
		render.setOpaque(false);

		render.add(texturesAndRent);
		render.add(tintPanel);

		JPanel sbbp2 = new JPanel(new BorderLayout());

		sbbp2.setOpaque(false);

		ButtonGroup bg = new ButtonGroup();
		bg.add(normalType);
		bg.add(growapableType);
		bg.add(doubleType);

		normalType.setSelected(true);

		normalType.setOpaque(false);
		growapableType.setOpaque(false);
		doubleType.setOpaque(false);

		emissiveRendering.setOpaque(false);

		isReplaceable.setOpaque(false);

		ActionListener planttypeselected = event -> {
			renderType.setEnabled(true);
			if (normalType.isSelected())
				stl.setIcon(TiledImageCache.plantStaticYes);
			else
				stl.setIcon(TiledImageCache.plantStaticNo);
			if (growapableType.isSelected())
				dyn.setIcon(TiledImageCache.plantGrowingYes);
			else
				dyn.setIcon(TiledImageCache.plantGrowingNo);
			if (doubleType.isSelected()) {
				dbl.setIcon(TiledImageCache.plantDoubleYes);
				renderType.setSelectedItem(cross);
				renderType.setEnabled(false);
			} else
				dbl.setIcon(TiledImageCache.plantDoubleNo);
			updateTextureOptions();
		};

		normalType.addActionListener(planttypeselected);
		growapableType.addActionListener(planttypeselected);
		doubleType.addActionListener(planttypeselected);

		JPanel ptipe = new JPanel(new BorderLayout());
		ptipe.setOpaque(false);

		JPanel ptipe1 = new JPanel(new BorderLayout());
		ptipe1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.plant.type_static"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe1.add("West", stl);
		ptipe1.add("Center", PanelUtils.join(HelpUtils
				.wrapWithHelpButton(this.withEntry("plant/static_generation_type"),
						L10N.label("elementgui.plant.type_generator")), staticPlantGenerationType));
		ptipe1.add("North", normalType);
		ptipe1.setOpaque(false);

		JPanel ptipe2 = new JPanel(new BorderLayout());
		ptipe2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.plant.type_growable"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe2.add("West", dyn);

		JPanel mlo = new JPanel(new GridLayout(1, 2));

		mlo.setOpaque(false);

		mlo.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("plant/max_height"), L10N.label("elementgui.plant.max_height")));
		mlo.add(growapableMaxHeight);

		ptipe2.add("Center", PanelUtils.join(mlo));
		ptipe2.add("North", growapableType);
		ptipe2.setOpaque(false);

		JPanel ptipe3 = new JPanel(new BorderLayout());
		ptipe3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.plant.type_double"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe3.add("West", dbl);
		ptipe3.add("Center", PanelUtils.join(HelpUtils
				.wrapWithHelpButton(this.withEntry("plant/static_generation_type"),
						L10N.label("elementgui.plant.type_generator")), doublePlantGenerationType));
		ptipe3.add("North", doubleType);
		ptipe3.setOpaque(false);

		ptipe.add("West", ptipe1);
		ptipe.add("Center", ptipe3);
		ptipe.add("East", ptipe2);

		sbbp2.add("North", render);
		sbbp2.add("Center", PanelUtils.totalCenterInPanel(ptipe));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel northPanel = new JPanel(new GridLayout(2, 2, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_bounding_box"),
				L10N.label("elementgui.common.custom_bounding_box")));
		northPanel.add(customBoundingBox);
		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/disable_offset"),
				L10N.label("elementgui.common.disable_offset")));
		northPanel.add(disableOffset);

		customBoundingBox.setOpaque(false);
		disableOffset.setOpaque(false);

		bbPane.add(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, northPanel), boundingBoxList));
		bbPane.setOpaque(false);

		bbPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		customBoundingBox.addActionListener(e -> {
			disableOffset.setEnabled(customBoundingBox.isSelected());
			if (!customBoundingBox.isSelected()) {
				disableOffset.setSelected(false);
			}
			boundingBoxList.setEnabled(customBoundingBox.isSelected());
		});

		if (!isEditingMode()) { // Add first bounding box, disable custom bounding box options
			boundingBoxList.setBoundingBoxes(Collections.singletonList(new IBlockWithBoundingBox.BoxEntry()));
			disableOffset.setEnabled(false);
			boundingBoxList.setEnabled(false);
		}

		JPanel selp = new JPanel(new GridLayout(10, 2, 25, 2));
		JPanel selp2 = new JPanel(new GridLayout(11, 2, 25, 2));

		useLootTableForDrops.setOpaque(false);
		unbreakable.setOpaque(false);
		forceTicking.setOpaque(false);
		hasTileEntity.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/gui_name"), L10N.label("elementgui.common.name_in_gui")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		selp.add(creativeTab);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/color_on_map"), L10N.label("elementgui.plant.color_on_map")));
		selp.add(colorOnMap);

		hardness.setOpaque(false);
		resistance.setOpaque(false);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/hardness"), L10N.label("elementgui.common.hardness")));
		selp.add(hardness);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/resistance"), L10N.label("elementgui.common.resistance")));
		selp.add(resistance);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/block_sound"), L10N.label("elementgui.common.block_sound")));
		selp.add(soundOnStep);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/luminance"), L10N.label("elementgui.common.luminance")));
		selp.add(luminance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/type"), L10N.label("elementgui.plant.type")));
		selp.add(growapableSpawnType);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/flammability"), L10N.label("elementgui.plant.flammability")));
		selp.add(flammability);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				L10N.label("elementgui.common.fire_spread_speed")));
		selp.add(fireSpreadSpeed);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				L10N.label("elementgui.common.emissive_rendering")));
		selp2.add(emissiveRendering);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/force_ticking"),
				L10N.label("elementgui.plant.force_ticking")));
		selp2.add(forceTicking);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/unbreakable"),
				L10N.label("elementgui.plant.is_unbreakable")));
		selp2.add(unbreakable);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/replaceable"),
				L10N.label("elementgui.plant.plant_is_replaceable")));
		selp2.add(isReplaceable);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"),
				L10N.label("elementgui.common.creative_pick_item")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, creativePickItem));

		selp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/custom_drop"), L10N.label("elementgui.common.custom_drop")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, customDrop));

		selp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/drop_amount"), L10N.label("elementgui.common.drop_amount")));
		selp2.add(dropAmount);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				L10N.label("elementgui.common.use_loot_table_for_drop")));
		selp2.add(useLootTableForDrops);

		selp2.setOpaque(false);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/ai_path_node_type"),
				L10N.label("elementgui.common.ai_path_node_type")));
		selp2.add(aiPathNodeType);

		selp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/offset_type"), L10N.label("elementgui.common.offset_type")));
		selp2.add(offsetType);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/has_tile_entity"),
				L10N.label("elementgui.plant.has_tile_entity")));
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

		JPanel events2 = new JPanel(new GridLayout(3, 4, 5, 5));
		events2.setOpaque(false);
		events2.add(onRightClicked);
		events2.add(onBlockAdded);
		events2.add(onNeighbourBlockChanges);
		events2.add(onTickUpdate);
		events2.add(onDestroyedByPlayer);
		events2.add(onDestroyedByExplosion);
		events2.add(onStartToDestroy);
		events2.add(onEntityCollides);
		events2.add(onBlockPlacedBy);
		events2.add(onRandomUpdateEvent);
		events2.add(new JLabel(""));

		JPanel spawning = new JPanel(new GridLayout(3, 2, 5, 5));
		spawning.setOpaque(false);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/gen_chunk_count"),
				L10N.label("elementgui.plant.gen_chunk_count")));
		spawning.add(frequencyOnChunks);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				L10N.label("elementgui.plant.spawn_world_types")));
		spawning.add(spawnWorldTypes);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		spawning.add(restrictionBiomes);

		JPanel lastPan = new JPanel(new BorderLayout(15, 15));
		lastPan.setOpaque(false);
		lastPan.add("North", spawning);

		pane4.add("Center", PanelUtils.totalCenterInPanel(lastPan));
		pane4.add("South", PanelUtils.join(FlowLayout.LEFT, generateCondition));

		pane4.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.plant.error_plant_needs_name")));
		name.enableRealtimeValidation();

		page2group.addValidationElement(name);

		addPage(L10N.t("elementgui.plant.page_visual_and_type"), pane2);
		addPage(L10N.t("elementgui.common.page_bounding_boxes"), bbPane);
		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_triggers"), PanelUtils.totalCenterInPanel(events2));
		addPage(L10N.t("elementgui.common.page_generation"), PanelUtils.totalCenterInPanel(pane4));

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void updateTextureOptions() {
		texture.setVisible(false);
		textureBottom.setVisible(false);

		if (doubleType.isSelected()) {
			texture.setVisible(true);
			textureBottom.setVisible(true);
		} else {
			texture.setVisible(true);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onBlockAdded.refreshListKeepSelected();
		onNeighbourBlockChanges.refreshListKeepSelected();
		onTickUpdate.refreshListKeepSelected();
		onRandomUpdateEvent.refreshListKeepSelected();
		onDestroyedByPlayer.refreshListKeepSelected();
		onDestroyedByExplosion.refreshListKeepSelected();
		onStartToDestroy.refreshListKeepSelected();
		onEntityCollides.refreshListKeepSelected();
		onBlockPlacedBy.refreshListKeepSelected();
		onRightClicked.refreshListKeepSelected();

		generateCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("DECORATIONS"));

		ComboBoxUtil
				.updateComboBoxContents(soundOnStep, ElementUtil.loadStepSounds(), new DataListEntry.Dummy("PLANT"));

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
		itemTexture.setTextureFromTextureName(plant.itemTexture);
		particleTexture.setTextureFromTextureName(plant.particleTexture);
		texture.setTextureFromTextureName(plant.texture);
		textureBottom.setTextureFromTextureName(plant.textureBottom);
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
		onBlockAdded.setSelectedProcedure(plant.onBlockAdded);
		onNeighbourBlockChanges.setSelectedProcedure(plant.onNeighbourBlockChanges);
		onTickUpdate.setSelectedProcedure(plant.onTickUpdate);
		onRandomUpdateEvent.setSelectedProcedure(plant.onRandomUpdateEvent);
		onDestroyedByPlayer.setSelectedProcedure(plant.onDestroyedByPlayer);
		onDestroyedByExplosion.setSelectedProcedure(plant.onDestroyedByExplosion);
		onStartToDestroy.setSelectedProcedure(plant.onStartToDestroy);
		onEntityCollides.setSelectedProcedure(plant.onEntityCollides);
		onBlockPlacedBy.setSelectedProcedure(plant.onBlockPlacedBy);
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

		customBoundingBox.setSelected(plant.customBoundingBox);
		disableOffset.setSelected(plant.disableOffset);
		boundingBoxList.setBoundingBoxes(plant.boundingBoxes);

		Model model = plant.getItemModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
			renderType.setSelectedItem(model);

		if (plant.plantType.equals("normal")) {
			normalType.setSelected(true);
			doubleType.setSelected(false);
			growapableType.setSelected(false);
		} else if (plant.plantType.equals("double")) {
			normalType.setSelected(false);
			doubleType.setSelected(true);
			growapableType.setSelected(false);
			textureBottom.setVisible(true);
			renderType.setEnabled(false);
		} else {
			normalType.setSelected(false);
			doubleType.setSelected(false);
			growapableType.setSelected(true);
		}

		growapableSpawnType.setSelectedItem(plant.growapableSpawnType);
		staticPlantGenerationType.setSelectedItem(plant.staticPlantGenerationType);
		doublePlantGenerationType.setSelectedItem(plant.doublePlantGenerationType);

		tintType.setSelectedItem(plant.tintType);
		isItemTinted.setSelected(plant.isItemTinted);

		customDrop.setEnabled(!useLootTableForDrops.isSelected());
		dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		disableOffset.setEnabled(customBoundingBox.isSelected());
		boundingBoxList.setEnabled(customBoundingBox.isSelected());

		if (normalType.isSelected())
			stl.setIcon(TiledImageCache.plantStaticYes);
		else
			stl.setIcon(TiledImageCache.plantStaticNo);

		if (growapableType.isSelected())
			dyn.setIcon(TiledImageCache.plantGrowingYes);
		else
			dyn.setIcon(TiledImageCache.plantGrowingNo);

		if (doubleType.isSelected())
			dbl.setIcon(TiledImageCache.plantDoubleYes);
		else
			dbl.setIcon(TiledImageCache.plantDoubleNo);

		updateTextureOptions();
	}

	@Override public Plant getElementFromGUI() {
		Plant plant = new Plant(modElement);
		plant.name = name.getText();
		plant.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		plant.texture = texture.getID();
		plant.textureBottom = textureBottom.getID();
		plant.itemTexture = itemTexture.getID();
		plant.particleTexture = particleTexture.getID();
		plant.tintType = (String) tintType.getSelectedItem();
		plant.isItemTinted = isItemTinted.isSelected();
		if (normalType.isSelected())
			plant.plantType = "normal";
		else if (growapableType.isSelected())
			plant.plantType = "growapable";
		else
			plant.plantType = "double";
		plant.growapableSpawnType = (String) growapableSpawnType.getSelectedItem();
		plant.staticPlantGenerationType = (String) staticPlantGenerationType.getSelectedItem();
		plant.doublePlantGenerationType = (String) doublePlantGenerationType.getSelectedItem();
		plant.growapableMaxHeight = (int) growapableMaxHeight.getValue();
		plant.hardness = (double) hardness.getValue();
		plant.resistance = (double) resistance.getValue();
		plant.luminance = (int) luminance.getValue();
		plant.unbreakable = unbreakable.isSelected();
		plant.forceTicking = forceTicking.isSelected();
		plant.hasTileEntity = hasTileEntity.isSelected();
		plant.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		plant.useLootTableForDrops = useLootTableForDrops.isSelected();
		plant.customDrop = customDrop.getBlock();
		plant.dropAmount = (int) dropAmount.getValue();
		plant.frequencyOnChunks = (int) frequencyOnChunks.getValue();
		plant.onBlockAdded = onBlockAdded.getSelectedProcedure();
		plant.onNeighbourBlockChanges = onNeighbourBlockChanges.getSelectedProcedure();
		plant.onTickUpdate = onTickUpdate.getSelectedProcedure();
		plant.onRandomUpdateEvent = onRandomUpdateEvent.getSelectedProcedure();
		plant.onDestroyedByPlayer = onDestroyedByPlayer.getSelectedProcedure();
		plant.onDestroyedByExplosion = onDestroyedByExplosion.getSelectedProcedure();
		plant.onStartToDestroy = onStartToDestroy.getSelectedProcedure();
		plant.onEntityCollides = onEntityCollides.getSelectedProcedure();
		plant.onBlockPlacedBy = onBlockPlacedBy.getSelectedProcedure();
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

		plant.customBoundingBox = customBoundingBox.isSelected();
		plant.disableOffset = disableOffset.isSelected();
		plant.boundingBoxes = boundingBoxList.getBoundingBoxes();

		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		plant.renderType = 12;
		if (model.getType() == Model.Type.JSON)
			plant.renderType = 2;
		else if (model.getType() == Model.Type.OBJ)
			plant.renderType = 3;
		else if (model.equals(cross))
			plant.renderType = "No tint".equals(tintType.getSelectedItem()) ? 12 : 120;
		else if (model.equals(crop))
			plant.renderType = 13;
		plant.customModelName = model.getReadableName();

		return plant;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-plant");
	}

}
