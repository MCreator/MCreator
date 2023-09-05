/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Plant;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.boundingboxes.JBoundingBoxList;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
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
	private JBoundingBoxList boundingBoxList;

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
	private final JCheckBox isSolid = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(18);

	private final JTextField specialInfo = new JTextField(20);

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator);
	private final JRadioButton defaultSoundType = L10N.radiobutton("elementgui.common.default_sound_type");
	private final JRadioButton customSoundType = L10N.radiobutton("elementgui.common.custom_sound_type");
	private final SoundSelector breakSound = new SoundSelector(mcreator);
	private final SoundSelector stepSound = new SoundSelector(mcreator);
	private final SoundSelector placeSound = new SoundSelector(mcreator);
	private final SoundSelector hitSound = new SoundSelector(mcreator);
	private final SoundSelector fallSound = new SoundSelector(mcreator);

	private final JCheckBox isReplaceable = L10N.checkbox("elementgui.plant.is_replaceable");
	private final DataListComboBox colorOnMap = new DataListComboBox(mcreator, ElementUtil.loadMapColors());
	private final MCItemHolder creativePickItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final Model cross = new Model.BuiltInModel("Cross model");
	private final Model crop = new Model.BuiltInModel("Crop model");
	private final JRadioButton normalType = L10N.radiobutton("elementgui.plant.use_static_plant_type");
	private final JComboBox<String> growapableSpawnType = new JComboBox<>();
	private final JSpinner growapableMaxHeight = new JSpinner(new SpinnerNumberModel(3, 1, 14, 1));

	private final JComboBox<String> suspiciousStewEffect = new JComboBox<>();
	private final JSpinner suspiciousStewDuration = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));

	private final JRadioButton doubleType = L10N.radiobutton("elementgui.plant.use_double_plant_type");

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);
	private final JRadioButton growapableType = L10N.radiobutton("elementgui.plant.use_growable_plant_type");
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(new Model[] { cross, crop });

	private final JComboBox<String> offsetType = new JComboBox<>(new String[] { "XZ", "XYZ", "NONE" });
	private final JComboBox<String> aiPathNodeType = new JComboBox<>();

	private final JComboBox<String> tintType = new JComboBox<>(
			new String[] { "No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage", "Water",
					"Sky", "Fog", "Water fog" });
	private final JCheckBox isItemTinted = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox isBonemealable = L10N.checkbox("elementgui.common.enable");

	private MCItemListField canBePlacedOn;

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
	private ProcedureSelector onEntityWalksOn;
	private ProcedureSelector onHitByProjectile;
	private ProcedureSelector onBonemealSuccess;

	private ProcedureSelector placingCondition;
	private ProcedureSelector isBonemealTargetCondition;
	private ProcedureSelector bonemealSuccessCondition;

	private DimensionListField spawnWorldTypes;
	private BiomeListField restrictionBiomes;
	private final JSpinner patchSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));
	private final JCheckBox generateAtAnyHeight = L10N.checkbox("elementgui.common.enable");
	private final JComboBox<String> generationType = new JComboBox<>(new String[] { "Flower", "Grass" });

	private final ValidationGroup page3group = new ValidationGroup();

	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(100, 0, 1024, 1));
	private final JSpinner fireSpreadSpeed = new JSpinner(new SpinnerNumberModel(60, 0, 1024, 1));
	private final JSpinner speedFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));
	private final JSpinner jumpFactor = new JSpinner(new SpinnerNumberModel(1.0, -1000, 1000, 0.1));

	public PlantGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		restrictionBiomes = new BiomeListField(mcreator);
		canBePlacedOn = new MCItemListField(mcreator, ElementUtil::loadBlocks);

		boundingBoxList = new JBoundingBoxList(mcreator, this, renderType::getSelectedItem);
		renderType.addActionListener(e -> boundingBoxList.modelChanged());

		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator,
				L10N.t("elementgui.plant.event_on_added"), Dependency.fromString(
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
				L10N.t("elementgui.plant.event_on_destroyed_by_player"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onDestroyedByExplosion = new ProcedureSelector(this.withEntry("block/when_destroyed_explosion"), mcreator,
				L10N.t("elementgui.plant.event_on_destroyed_by_explosion"),
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onStartToDestroy = new ProcedureSelector(this.withEntry("block/when_destroy_start"), mcreator,
				L10N.t("elementgui.plant.event_on_start_to_destroy"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				L10N.t("elementgui.plant.event_on_entity_collides"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onBlockPlacedBy = new ProcedureSelector(this.withEntry("block/when_block_placed_by"), mcreator,
				L10N.t("elementgui.common.event_on_block_placed_by"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/blockstate:blockstate"));
		onRightClicked = new ProcedureSelector(this.withEntry("block/when_right_clicked"), mcreator,
				L10N.t("elementgui.plant.event_on_right_clicked"), VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/direction:direction/blockstate:blockstate/hitX:number/hitY:number/hitZ:number")).makeReturnValueOptional();
		onEntityWalksOn = new ProcedureSelector(this.withEntry("block/when_entity_walks_on"), mcreator,
				L10N.t("elementgui.block.event_on_entity_walks_on"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/blockstate:blockstate"));
		onHitByProjectile = new ProcedureSelector(this.withEntry("block/on_hit_by_projectile"), mcreator,
				L10N.t("elementgui.common.event_on_block_hit_by_projectile"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/direction:direction/blockstate:blockstate/hitX:number/hitY:number/hitZ:number"));
		onBonemealSuccess = new ProcedureSelector(this.withEntry("block/on_bonemeal_success"), mcreator,
				L10N.t("elementgui.common.event_on_bonemeal_success"), ProcedureSelector.Side.SERVER,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).makeInline();

		placingCondition = new ProcedureSelector(this.withEntry("plant/placing_condition"), mcreator,
				L10N.t("elementgui.plant.condition_additional_placing"), VariableTypeLoader.BuiltInTypes.LOGIC,
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

		spawnWorldTypes = new DimensionListField(mcreator);
		spawnWorldTypes.setListElements(Collections.singletonList("Surface"));

		ComponentUtils.deriveFont(specialInfo, 16);
		ComponentUtils.deriveFont(tintType, 16);
		ComponentUtils.deriveFont(growapableSpawnType, 16);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(2, 2));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));
		JPanel bbPane = new JPanel(new BorderLayout(10, 10));

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureBottom = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		texture.setOpaque(false);
		textureBottom.setOpaque(false);
		textureBottom.setVisible(false);

		itemTexture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 32);
		itemTexture.setOpaque(false);
		particleTexture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK), 32);
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

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/model"),
				L10N.label("elementgui.plant.block_model")));
		rent.add(renderType);

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/item_texture"),
				L10N.label("elementgui.plant.item_texture")));
		rent.add(PanelUtils.centerInPanel(itemTexture));

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/particle_texture"),
				L10N.label("elementgui.plant.particle_texture")));
		rent.add(PanelUtils.centerInPanel(particleTexture));

		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/tint_type"),
				L10N.label("elementgui.common.tint_type")));
		rent.add(tintType);
		rent.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/is_item_tinted"),
				L10N.label("elementgui.plant.is_item_tinted")));
		rent.add(isItemTinted);

		renderType.setFont(renderType.getFont().deriveFont(16.0f));
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		JPanel texturesAndRent = new JPanel(new BorderLayout(50, 0));
		texturesAndRent.setOpaque(false);

		texturesAndRent.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.gridElements(2, 1,
				ComponentUtils.squareAndBorder(texture, new Color(125, 255, 174),
						L10N.t("elementgui.plant.texture_place_top_main")),
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
		isSolid.setOpaque(false);

		isReplaceable.setOpaque(false);
		isBonemealable.setOpaque(false);

		ActionListener planttypeselected = event -> updatePlantType();
		normalType.addActionListener(planttypeselected);
		growapableType.addActionListener(planttypeselected);
		doubleType.addActionListener(planttypeselected);

		JPanel ptipe = new JPanel(new BorderLayout());
		ptipe.setOpaque(false);

		JPanel ptipe1 = new JPanel(new BorderLayout());
		ptipe1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.type_static"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel staticPlantProperties = new JPanel(new GridLayout(2, 2, 0, 4));
		staticPlantProperties.setOpaque(false);
		staticPlantProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/suspicious_stew_effect"),
				L10N.label("elementgui.plant.suspicious_stew_effect")));
		staticPlantProperties.add(suspiciousStewEffect);
		staticPlantProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/suspicious_stew_duration"),
				L10N.label("elementgui.plant.suspicious_stew_duration")));
		staticPlantProperties.add(suspiciousStewDuration);

		ptipe1.add("West", stl);
		ptipe1.add("Center", PanelUtils.pullElementUp(staticPlantProperties));
		ptipe1.add("North", normalType);
		ptipe1.setOpaque(false);

		JPanel ptipe2 = new JPanel(new BorderLayout());
		ptipe2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.type_growable"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe2.add("West", dyn);

		JPanel mlo = new JPanel(new GridLayout(1, 2));

		mlo.setOpaque(false);

		mlo.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/max_height"),
				L10N.label("elementgui.plant.max_height")));
		mlo.add(growapableMaxHeight);

		ptipe2.add("Center", PanelUtils.join(mlo));
		ptipe2.add("North", growapableType);
		ptipe2.setOpaque(false);

		JPanel ptipe3 = new JPanel(new BorderLayout());
		ptipe3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.type_double"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		ptipe3.add("Center", dbl);
		ptipe3.add("North", doubleType);
		ptipe3.setOpaque(false);

		ptipe.add("West", ptipe1);
		ptipe.add("Center", ptipe2);
		ptipe.add("East", ptipe3);

		sbbp2.add("North", render);
		sbbp2.add("Center", PanelUtils.totalCenterInPanel(ptipe));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel northPanel = new JPanel(new GridLayout(2, 2, 10, 2));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/custom_bounding_box"),
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

		JPanel selp = new JPanel(new GridLayout(9, 2, 5, 2));
		selp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_general"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		selp.setOpaque(false);

		JPanel selp2 = new JPanel(new GridLayout(6, 2, 5, 2));
		selp2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_dropping"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		selp2.setOpaque(false);

		JPanel soundProperties = new JPanel(new GridLayout(7, 2, 0, 2));
		soundProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_sound"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		soundProperties.setOpaque(false);

		useLootTableForDrops.setOpaque(false);
		unbreakable.setOpaque(false);
		hardness.setOpaque(false);
		resistance.setOpaque(false);
		dropAmount.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		selp.add(creativeTab);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hardness"),
				L10N.label("elementgui.common.hardness")));
		selp.add(hardness);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"),
				L10N.label("elementgui.common.resistance")));
		selp.add(resistance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/jump_factor"),
				L10N.label("elementgui.block.jump_factor")));
		selp.add(jumpFactor);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/speed_factor"),
				L10N.label("elementgui.block.speed_factor")));
		selp.add(speedFactor);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"),
				L10N.label("elementgui.common.luminance")));
		selp.add(luminance);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/emissive_rendering"),
				L10N.label("elementgui.common.emissive_rendering")));
		selp.add(emissiveRendering);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/is_solid"),
				L10N.label("elementgui.plant.is_solid")));
		selp.add(isSolid);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				L10N.label("elementgui.common.custom_drop")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, customDrop));

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"),
				L10N.label("elementgui.common.drop_amount")));
		selp2.add(dropAmount);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/use_loot_table_for_drops"),
				L10N.label("elementgui.common.use_loot_table_for_drop")));
		selp2.add(useLootTableForDrops);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/creative_pick_item"),
				L10N.label("elementgui.common.creative_pick_item")));
		selp2.add(PanelUtils.join(FlowLayout.LEFT, creativePickItem));

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/unbreakable"),
				L10N.label("elementgui.plant.is_unbreakable")));
		selp2.add(unbreakable);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/replaceable"),
				L10N.label("elementgui.plant.plant_is_replaceable")));
		selp2.add(isReplaceable);

		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(defaultSoundType);
		bg2.add(customSoundType);
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

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/step_sound"),
				L10N.label("elementgui.common.soundtypes.step_sound")));
		soundProperties.add(stepSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/place_sound"),
				L10N.label("elementgui.common.soundtypes.place_sound")));
		soundProperties.add(placeSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hit_sound"),
				L10N.label("elementgui.common.soundtypes.hit_sound")));
		soundProperties.add(hitSound);

		soundProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fall_sound"),
				L10N.label("elementgui.common.soundtypes.fall_sound")));
		soundProperties.add(fallSound);

		useLootTableForDrops.addActionListener(e -> {
			customDrop.setEnabled(!useLootTableForDrops.isSelected());
			dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		});

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

		isBonemealable.addActionListener(e -> refreshBonemealProperties());
		refreshBonemealProperties();

		JComponent bonemealMerger = PanelUtils.northAndCenterElement(bonemealPanel, bonemealEvents, 2, 2);
		bonemealMerger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.properties_bonemeal"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane3.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.westAndEastElement(
				PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(selp, bonemealMerger)),
				PanelUtils.centerAndSouthElement(selp2, soundProperties))));
		pane3.setOpaque(false);

		JPanel advancedProperties = new JPanel(new GridLayout(9, 2, 10, 2));
		advancedProperties.setOpaque(false);

		forceTicking.setOpaque(false);
		hasTileEntity.setOpaque(false);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/has_tile_entity"),
				L10N.label("elementgui.plant.has_tile_entity")));
		advancedProperties.add(hasTileEntity);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/force_ticking"),
				L10N.label("elementgui.plant.force_ticking")));
		advancedProperties.add(forceTicking);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/color_on_map"),
				L10N.label("elementgui.plant.color_on_map")));
		advancedProperties.add(colorOnMap);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/flammability"),
				L10N.label("elementgui.plant.flammability")));
		advancedProperties.add(flammability);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				L10N.label("elementgui.common.fire_spread_speed")));
		advancedProperties.add(fireSpreadSpeed);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/ai_path_node_type"),
				L10N.label("elementgui.common.ai_path_node_type")));
		advancedProperties.add(aiPathNodeType);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/offset_type"),
				L10N.label("elementgui.common.offset_type")));
		advancedProperties.add(offsetType);

		advancedProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("plant/type"), L10N.label("elementgui.plant.type")));
		advancedProperties.add(growapableSpawnType);

		advancedProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/can_be_placed_on"),
				L10N.label("elementgui.plant.can_be_placed_on")));
		advancedProperties.add(canBePlacedOn);

		JComponent plocb = PanelUtils.northAndCenterElement(advancedProperties,
				PanelUtils.westAndCenterElement(new JEmptyBox(5, 5), placingCondition), 2, 2);
		plocb.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.plant.properties_advanced_plant"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		plocb.setOpaque(false);

		pane5.add("Center", PanelUtils.totalCenterInPanel(plocb));
		pane5.setOpaque(false);

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
		events2.add(onEntityWalksOn);
		events2.add(onHitByProjectile);

		JPanel spawning = new JPanel(new GridLayout(6, 2, 5, 2));
		spawning.setOpaque(false);
		generateAtAnyHeight.setOpaque(false);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/gen_chunk_count"),
				L10N.label("elementgui.plant.gen_chunk_count")));
		spawning.add(frequencyOnChunks);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/patch_size"),
				L10N.label("elementgui.plant.patch_size")));
		spawning.add(patchSize);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/generate_at_any_height"),
				L10N.label("elementgui.plant.generate_at_any_height")));
		spawning.add(generateAtAnyHeight);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("plant/generation_type"),
				L10N.label("elementgui.plant.generation_type")));
		spawning.add(generationType);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				L10N.label("elementgui.plant.spawn_world_types")));
		spawning.add(spawnWorldTypes);

		spawning.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		spawning.add(restrictionBiomes);

		pane4.add("Center", PanelUtils.totalCenterInPanel(spawning));

		pane4.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.plant.error_plant_needs_name")));
		name.enableRealtimeValidation();

		page3group.addValidationElement(name);

		breakSound.getVTextField().setValidator(new ConditionalTextFieldValidator(breakSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		stepSound.getVTextField().setValidator(new ConditionalTextFieldValidator(stepSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		placeSound.getVTextField().setValidator(new ConditionalTextFieldValidator(placeSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		hitSound.getVTextField().setValidator(new ConditionalTextFieldValidator(hitSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));
		fallSound.getVTextField().setValidator(new ConditionalTextFieldValidator(fallSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null"), customSoundType, true));

		page3group.addValidationElement(breakSound.getVTextField());
		page3group.addValidationElement(stepSound.getVTextField());
		page3group.addValidationElement(placeSound.getVTextField());
		page3group.addValidationElement(hitSound.getVTextField());
		page3group.addValidationElement(fallSound.getVTextField());

		addPage(L10N.t("elementgui.plant.page_visual_and_type"), pane2);
		addPage(L10N.t("elementgui.common.page_bounding_boxes"), bbPane);
		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_advanced_properties"), pane5);
		addPage(L10N.t("elementgui.common.page_triggers"), PanelUtils.totalCenterInPanel(events2));
		addPage(L10N.t("elementgui.common.page_generation"), PanelUtils.totalCenterInPanel(pane4));

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}

		updateSoundType();
		updatePlantType();
	}

	private void updatePlantType() {
		if (normalType.isSelected()) {
			stl.setIcon(TiledImageCache.plantStaticYes);
			generationType.setEnabled(true);
			renderType.setEnabled(true);
			suspiciousStewEffect.setEnabled(true);
			suspiciousStewDuration.setEnabled(true);
			growapableMaxHeight.setEnabled(false);
		} else {
			stl.setIcon(TiledImageCache.plantStaticNo);
		}

		if (growapableType.isSelected()) {
			dyn.setIcon(TiledImageCache.plantGrowingYes);
			generationType.setEnabled(false);
			renderType.setEnabled(true);
			suspiciousStewEffect.setEnabled(false);
			suspiciousStewDuration.setEnabled(false);
			growapableMaxHeight.setEnabled(true);
		} else {
			dyn.setIcon(TiledImageCache.plantGrowingNo);
		}

		if (doubleType.isSelected()) {
			generationType.setEnabled(true);
			dbl.setIcon(TiledImageCache.plantDoubleYes);
			renderType.setSelectedItem(cross);
			renderType.setEnabled(false);
			suspiciousStewEffect.setEnabled(false);
			suspiciousStewDuration.setEnabled(false);
			growapableMaxHeight.setEnabled(false);
		} else {
			dbl.setIcon(TiledImageCache.plantDoubleNo);
		}

		texture.setVisible(false);
		textureBottom.setVisible(false);

		if (doubleType.isSelected()) {
			texture.setVisible(true);
			textureBottom.setVisible(true);
		} else {
			texture.setVisible(true);
		}

	}

	private void updateSoundType() {
		if (customSoundType.isSelected()) {
			breakSound.setEnabled(true);
			stepSound.setEnabled(true);
			placeSound.setEnabled(true);
			hitSound.setEnabled(true);
			fallSound.setEnabled(true);
			soundOnStep.setEnabled(false);
		} else {
			breakSound.setEnabled(false);
			stepSound.setEnabled(false);
			placeSound.setEnabled(false);
			hitSound.setEnabled(false);
			fallSound.setEnabled(false);
			soundOnStep.setEnabled(true);
		}
	}

	private void refreshBonemealProperties() {
		isBonemealTargetCondition.setEnabled(isBonemealable.isSelected());
		bonemealSuccessCondition.setEnabled(isBonemealable.isSelected());
		onBonemealSuccess.setEnabled(isBonemealable.isSelected());
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
		onEntityWalksOn.refreshListKeepSelected();
		onHitByProjectile.refreshListKeepSelected();
		onBonemealSuccess.refreshListKeepSelected();

		placingCondition.refreshListKeepSelected();
		isBonemealTargetCondition.refreshListKeepSelected();
		bonemealSuccessCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("DECORATIONS"));

		ComboBoxUtil.updateComboBoxContents(soundOnStep, ElementUtil.loadStepSounds(),
				new DataListEntry.Dummy("PLANT"));

		ComboBoxUtil.updateComboBoxContents(growapableSpawnType,
				Arrays.asList(ElementUtil.getDataListAsStringArray("planttypes")), "Plains");

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(cross, crop),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(aiPathNodeType,
				Arrays.asList(ElementUtil.getDataListAsStringArray("pathnodetypes")), "DEFAULT");

		ComboBoxUtil.updateComboBoxContents(suspiciousStewEffect,
				ElementUtil.loadAllPotionEffects(mcreator.getWorkspace()).stream().map(DataListEntry::getName)
						.collect(Collectors.toList()), "SPEED");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(texture);
		else if (page == 2)
			return new AggregatedValidationResult(page3group);
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
		breakSound.setSound(plant.breakSound);
		stepSound.setSound(plant.stepSound);
		placeSound.setSound(plant.placeSound);
		fallSound.setSound(plant.fallSound);
		defaultSoundType.setSelected(!plant.isCustomSoundType);
		customSoundType.setSelected(plant.isCustomSoundType);
		hitSound.setSound(plant.hitSound);
		luminance.setValue(plant.luminance);
		unbreakable.setSelected(plant.unbreakable);
		forceTicking.setSelected(plant.forceTicking);
		hasTileEntity.setSelected(plant.hasTileEntity);
		frequencyOnChunks.setValue(plant.frequencyOnChunks);
		emissiveRendering.setSelected(plant.emissiveRendering);
		isSolid.setSelected(plant.isSolid);
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
		onEntityWalksOn.setSelectedProcedure(plant.onEntityWalksOn);
		onHitByProjectile.setSelectedProcedure(plant.onHitByProjectile);
		growapableMaxHeight.setValue(plant.growapableMaxHeight);
		spawnWorldTypes.setListElements(plant.spawnWorldTypes);
		restrictionBiomes.setListElements(plant.restrictionBiomes);
		canBePlacedOn.setListElements(plant.canBePlacedOn);
		isReplaceable.setSelected(plant.isReplaceable);
		colorOnMap.setSelectedItem(plant.colorOnMap);
		offsetType.setSelectedItem(plant.offsetType);
		aiPathNodeType.setSelectedItem(plant.aiPathNodeType);
		creativePickItem.setBlock(plant.creativePickItem);
		flammability.setValue(plant.flammability);
		fireSpreadSpeed.setValue(plant.fireSpreadSpeed);
		jumpFactor.setValue(plant.jumpFactor);
		speedFactor.setValue(plant.speedFactor);
		patchSize.setValue(plant.patchSize);
		generateAtAnyHeight.setSelected(plant.generateAtAnyHeight);
		isBonemealable.setSelected(plant.isBonemealable);
		isBonemealTargetCondition.setSelectedProcedure(plant.isBonemealTargetCondition);
		bonemealSuccessCondition.setSelectedProcedure(plant.bonemealSuccessCondition);
		onBonemealSuccess.setSelectedProcedure(plant.onBonemealSuccess);

		specialInfo.setText(
				plant.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		placingCondition.setSelectedProcedure(plant.placingCondition);

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
		generationType.setSelectedItem(plant.generationType);

		suspiciousStewEffect.setSelectedItem(plant.suspiciousStewEffect);
		suspiciousStewDuration.setValue(plant.suspiciousStewDuration);

		tintType.setSelectedItem(plant.tintType);
		isItemTinted.setSelected(plant.isItemTinted);

		customDrop.setEnabled(!useLootTableForDrops.isSelected());
		dropAmount.setEnabled(!useLootTableForDrops.isSelected());
		disableOffset.setEnabled(customBoundingBox.isSelected());
		boundingBoxList.setEnabled(customBoundingBox.isSelected());

		updatePlantType();
		updateSoundType();
		refreshBonemealProperties();
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
		plant.growapableMaxHeight = (int) growapableMaxHeight.getValue();
		plant.suspiciousStewEffect = (String) suspiciousStewEffect.getSelectedItem();
		plant.suspiciousStewDuration = (int) suspiciousStewDuration.getValue();
		plant.hardness = (double) hardness.getValue();
		plant.resistance = (double) resistance.getValue();
		plant.luminance = (int) luminance.getValue();
		plant.unbreakable = unbreakable.isSelected();
		plant.forceTicking = forceTicking.isSelected();
		plant.hasTileEntity = hasTileEntity.isSelected();
		plant.isCustomSoundType = customSoundType.isSelected();
		plant.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		plant.breakSound = breakSound.getSound();
		plant.stepSound = stepSound.getSound();
		plant.placeSound = placeSound.getSound();
		plant.hitSound = hitSound.getSound();
		plant.fallSound = fallSound.getSound();
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
		plant.onEntityWalksOn = onEntityWalksOn.getSelectedProcedure();
		plant.onHitByProjectile = onHitByProjectile.getSelectedProcedure();
		plant.spawnWorldTypes = spawnWorldTypes.getListElements();
		plant.restrictionBiomes = restrictionBiomes.getListElements();
		plant.patchSize = (int) patchSize.getValue();
		plant.generateAtAnyHeight = generateAtAnyHeight.isSelected();
		plant.generationType = (String) generationType.getSelectedItem();
		plant.canBePlacedOn = canBePlacedOn.getListElements();
		plant.isReplaceable = isReplaceable.isSelected();
		plant.colorOnMap = colorOnMap.getSelectedItem().toString();
		plant.offsetType = (String) offsetType.getSelectedItem();
		plant.aiPathNodeType = (String) aiPathNodeType.getSelectedItem();
		plant.creativePickItem = creativePickItem.getBlock();
		plant.flammability = (int) flammability.getValue();
		plant.fireSpreadSpeed = (int) fireSpreadSpeed.getValue();
		plant.speedFactor = (double) speedFactor.getValue();
		plant.jumpFactor = (double) jumpFactor.getValue();
		plant.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());
		plant.placingCondition = placingCondition.getSelectedProcedure();
		plant.emissiveRendering = emissiveRendering.isSelected();
		plant.isSolid = isSolid.isSelected();
		plant.isBonemealable = isBonemealable.isSelected();
		plant.isBonemealTargetCondition = isBonemealTargetCondition.getSelectedProcedure();
		plant.bonemealSuccessCondition = bonemealSuccessCondition.getSelectedProcedure();
		plant.onBonemealSuccess = onBonemealSuccess.getSelectedProcedure();

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

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-plant");
	}

}
