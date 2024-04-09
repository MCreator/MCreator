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

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxType;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.GUI;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.*;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.modellayers.JModelLayerList;
import net.mcreator.ui.minecraft.states.entity.JEntityDataList;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ItemListFieldSingleTagValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
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
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LivingEntityGUI extends ModElementGUI<LivingEntity> implements IBlocklyPanelHolder {

	private ProcedureSelector onStruckByLightning;
	private ProcedureSelector whenMobFalls;
	private ProcedureSelector whenMobDies;
	private ProcedureSelector whenMobIsHurt;
	private ProcedureSelector onRightClickedOn;
	private ProcedureSelector whenThisMobKillsAnother;
	private ProcedureSelector onMobTickUpdate;
	private ProcedureSelector onPlayerCollidesWith;
	private ProcedureSelector onInitialSpawn;

	private ProcedureSelector spawningCondition;
	private LogicProcedureSelector transparentModelCondition;
	private LogicProcedureSelector isShakingCondition;
	private LogicProcedureSelector solidBoundingBox;
	private LogicProcedureSelector breatheUnderwater;
	private LogicProcedureSelector pushedByFluids;
	private NumberProcedureSelector visualScale;
	private NumberProcedureSelector boundingBoxScale;

	private final SoundSelector livingSound = new SoundSelector(mcreator);
	private final SoundSelector hurtSound = new SoundSelector(mcreator);
	private final SoundSelector deathSound = new SoundSelector(mcreator);
	private final SoundSelector stepSound = new SoundSelector(mcreator);

	private final VTextField mobName = new VTextField();

	private final JSpinner attackStrength = new JSpinner(new SpinnerNumberModel(3, 0, 10000, 1));
	private final JSpinner movementSpeed = new JSpinner(new SpinnerNumberModel(0.3, 0, 50, 0.1));
	private final JSpinner stepHeight = new JSpinner(new SpinnerNumberModel(0.6, 0, 255, 0.1));
	private final JSpinner armorBaseValue = new JSpinner(new SpinnerNumberModel(0.0, 0, 100, 0.1));
	private final JSpinner health = new JSpinner(new SpinnerNumberModel(10, 0, 1024, 1));
	private final JSpinner knockbackResistance = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.1));
	private final JSpinner attackKnockback = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.1));

	private final JSpinner trackingRange = new JSpinner(new SpinnerNumberModel(64, 0, 10000, 1));
	private final JSpinner followRange = new JSpinner(new SpinnerNumberModel(16, 0, 2048, 1));

	private final JSpinner rangedAttackInterval = new JSpinner(new SpinnerNumberModel(20, 0, 1024, 1));
	private final JSpinner rangedAttackRadius = new JSpinner(new SpinnerNumberModel(10, 0, 1024, 0.1));

	private final JSpinner spawningProbability = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
	private final JMinMaxSpinner numberOfMobsPerGroup = new JMinMaxSpinner(4, 4, 1, 1000, 1);

	private final JSpinner modelWidth = new JSpinner(new SpinnerNumberModel(0.6, 0, 1024, 0.1));
	private final JSpinner modelHeight = new JSpinner(new SpinnerNumberModel(1.8, 0, 1024, 0.1));
	private final JSpinner mountedYOffset = new JSpinner(new SpinnerNumberModel(0, -1024, 1024, 0.1));
	private final JSpinner modelShadowSize = new JSpinner(new SpinnerNumberModel(0.5, 0, 20, 0.1));
	private final JCheckBox disableCollisions = L10N.checkbox("elementgui.living_entity.disable_collisions");

	private final JSpinner xpAmount = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));

	private final JCheckBox hasAI = L10N.checkbox("elementgui.living_entity.has_ai");
	private final JCheckBox isBoss = new JCheckBox();

	private final JCheckBox immuneToFire = L10N.checkbox("elementgui.living_entity.immune_fire");
	private final JCheckBox immuneToArrows = L10N.checkbox("elementgui.living_entity.immune_arrows");
	private final JCheckBox immuneToFallDamage = L10N.checkbox("elementgui.living_entity.immune_fall_damage");
	private final JCheckBox immuneToCactus = L10N.checkbox("elementgui.living_entity.immune_cactus");
	private final JCheckBox immuneToDrowning = L10N.checkbox("elementgui.living_entity.immune_drowning");
	private final JCheckBox immuneToLightning = L10N.checkbox("elementgui.living_entity.immune_lightning");
	private final JCheckBox immuneToPotions = L10N.checkbox("elementgui.living_entity.immune_potions");
	private final JCheckBox immuneToPlayer = L10N.checkbox("elementgui.living_entity.immune_player");
	private final JCheckBox immuneToExplosion = L10N.checkbox("elementgui.living_entity.immune_explosions");
	private final JCheckBox immuneToTrident = L10N.checkbox("elementgui.living_entity.immune_trident");
	private final JCheckBox immuneToAnvil = L10N.checkbox("elementgui.living_entity.immune_anvil");
	private final JCheckBox immuneToWither = L10N.checkbox("elementgui.living_entity.immune_wither");
	private final JCheckBox immuneToDragonBreath = L10N.checkbox("elementgui.living_entity.immune_dragon_breath");

	private final JCheckBox waterMob = L10N.checkbox("elementgui.living_entity.is_water_mob");
	private final JCheckBox flyingMob = L10N.checkbox("elementgui.living_entity.is_flying_mob");

	private final JCheckBox hasSpawnEgg = new JCheckBox();
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final JComboBox<String> mobSpawningType = new JComboBox<>(
			ElementUtil.getDataListAsStringArray("mobspawntypes"));

	private MCItemHolder mobDrop;
	private MCItemHolder equipmentMainHand;
	private MCItemHolder equipmentHelmet;
	private MCItemHolder equipmentBody;
	private MCItemHolder equipmentLeggings;
	private MCItemHolder equipmentBoots;
	private MCItemHolder equipmentOffHand;

	private final SearchableComboBox<String> guiBoundTo = new SearchableComboBox<>();
	private final JSpinner inventorySize = new JSpinner(new SpinnerNumberModel(9, 0, 256, 1));
	private final JSpinner inventoryStackSize = new JSpinner(new SpinnerNumberModel(64, 1, 1024, 1));

	private MCItemHolder rangedAttackItem;

	private final SearchableComboBox<String> rangedItemType = new SearchableComboBox<>();

	private final JTextField mobLabel = new JTextField();

	private final JCheckBox spawnInDungeons = L10N.checkbox("elementgui.living_entity.spawn_dungeons");
	private final JColor spawnEggBaseColor = new JColor(mcreator, false, false);
	private final JColor spawnEggDotColor = new JColor(mcreator, false, false);

	private static final Model biped = new Model.BuiltInModel("Biped");
	private static final Model chicken = new Model.BuiltInModel("Chicken");
	private static final Model cod = new Model.BuiltInModel("Cod");
	private static final Model cow = new Model.BuiltInModel("Cow");
	private static final Model creeper = new Model.BuiltInModel("Creeper");
	private static final Model ghast = new Model.BuiltInModel("Ghast");
	private static final Model ocelot = new Model.BuiltInModel("Ocelot");
	private static final Model pig = new Model.BuiltInModel("Pig");
	private static final Model piglin = new Model.BuiltInModel("Piglin");
	private static final Model salmon = new Model.BuiltInModel("Salmon");
	private static final Model silverfish = new Model.BuiltInModel("Silverfish");
	private static final Model slime = new Model.BuiltInModel("Slime");
	private static final Model spider = new Model.BuiltInModel("Spider");
	private static final Model villager = new Model.BuiltInModel("Villager");
	private static final Model witch = new Model.BuiltInModel("Witch");
	public static final Model[] builtinmobmodels = new Model[] { biped, chicken, cod, cow, creeper, ghast, ocelot, pig,
			piglin, salmon, silverfish, slime, spider, villager, witch };
	private final SearchableComboBox<Model> mobModel = new SearchableComboBox<>(builtinmobmodels);

	private final VComboBox<String> mobModelTexture = new SearchableComboBox<>();

	private JEntityDataList entityDataList;

	private static final BlocklyCompileNote aiUnmodifiableCompileNote = new BlocklyCompileNote(
			BlocklyCompileNote.Type.INFO, L10N.t("blockly.warnings.unmodifiable_ai_bases"));

	private final SearchableComboBox<String> aiBase = new SearchableComboBox<>(
			Stream.of("(none)", "Creeper", "Skeleton", "Enderman", "Blaze", "Slime", "Witch", "Zombie", "MagmaCube",
					"Pig", "Villager", "Wolf", "Cow", "Bat", "Chicken", "Ocelot", "Squid", "Horse", "Spider",
					"IronGolem").sorted().toArray(String[]::new));

	private final JComboBox<String> mobBehaviourType = new JComboBox<>(new String[] { "Mob", "Creature" });
	private final JComboBox<String> mobCreatureType = new JComboBox<>(
			new String[] { "UNDEFINED", "UNDEAD", "ARTHROPOD", "ILLAGER", "WATER" });
	private final JComboBox<String> bossBarColor = new JComboBox<>(
			new String[] { "PINK", "BLUE", "RED", "GREEN", "YELLOW", "PURPLE", "WHITE" });
	private final JComboBox<String> bossBarType = new JComboBox<>(
			new String[] { "PROGRESS", "NOTCHED_6", "NOTCHED_10", "NOTCHED_12", "NOTCHED_20" });

	private final JCheckBox ridable = L10N.checkbox("elementgui.living_entity.is_rideable");

	private final JCheckBox canControlForward = L10N.checkbox("elementgui.living_entity.control_forward");
	private final JCheckBox canControlStrafe = L10N.checkbox("elementgui.living_entity.control_strafe");

	private final JCheckBox breedable = L10N.checkbox("elementgui.living_entity.is_breedable");

	private final JCheckBox tameable = L10N.checkbox("elementgui.living_entity.is_tameable");

	private final JCheckBox ranged = L10N.checkbox("elementgui.living_entity.is_ranged");

	private MCItemListField breedTriggerItems;

	private final JCheckBox spawnThisMob = new JCheckBox();
	private final JCheckBox doesDespawnWhenIdle = new JCheckBox();

	private BiomeListField restrictionBiomes;

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private Map<String, ToolboxBlock> externalBlocks;
	private final List<BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	private boolean editorReady = false;

	private boolean disableMobModelCheckBoxListener = false;

	private JModelLayerList modelLayers;

	private final List<?> unmodifiableAIBases = (List<?>) mcreator.getWorkspace().getGenerator()
			.getGeneratorConfiguration().getDefinitionsProvider().getModElementDefinition(ModElementType.LIVINGENTITY)
			.get("unmodifiable_ai_bases");

	public LivingEntityGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override public void addBlocklyChangedListener(BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	private void setDefaultAISet() {
		blocklyPanel.setXML("""
				<xml xmlns="https://developers.google.com/blockly/xml">
				<block type="aitasks_container" deletable="false" x="40" y="40"><next>
				<block type="attack_on_collide"><field name="speed">1.2</field><field name="longmemory">FALSE</field><field name="condition">null,null</field><next>
				<block type="wander"><field name="speed">1</field><field name="condition">null,null</field><next>
				<block type="attack_action"><field name="callhelp">FALSE</field><field name="condition">null,null</field><next>
				<block type="look_around"><field name="condition">null,null</field><next>
				<block type="swim_in_water"/><field name="condition">null,null</field></next>
				</block></next></block></next></block></next></block></next></block></xml>""");
	}

	private synchronized void regenerateAITasks() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.AI_TASK));

		BlocklyToJava blocklyToJava;
		try {
			blocklyToJava = new BlocklyToJava(mcreator.getWorkspace(), this.modElement, BlocklyEditorType.AI_TASK,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		if (unmodifiableAIBases != null && unmodifiableAIBases.contains(aiBase.getSelectedItem()))
			compileNotesArrayList = List.of(aiUnmodifiableCompileNote);

		List<BlocklyCompileNote> finalCompileNotesArrayList = compileNotesArrayList;
		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(finalCompileNotesArrayList);
			blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel));
		});
	}

	@Override protected void initGUI() {
		onStruckByLightning = new ProcedureSelector(this.withEntry("entity/when_struck_by_lightning"), mcreator,
				L10N.t("elementgui.living_entity.event_struck_by_lightning"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		whenMobFalls = new ProcedureSelector(this.withEntry("entity/when_falls"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_falls"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/damagesource:damagesource"));
		whenMobDies = new ProcedureSelector(this.withEntry("entity/when_dies"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_dies"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/immediatesourceentity:entity/damagesource:damagesource"));
		whenMobIsHurt = new ProcedureSelector(this.withEntry("entity/when_hurt"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_is_hurt"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/immediatesourceentity:entity/damagesource:damagesource")).makeReturnValueOptional();
		onRightClickedOn = new ProcedureSelector(this.withEntry("entity/when_right_clicked"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_right_clicked"),
				VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack")).makeReturnValueOptional();
		whenThisMobKillsAnother = new ProcedureSelector(this.withEntry("entity/when_kills_another"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_kills_another"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/immediatesourceentity:entity/damagesource:damagesource"));
		onMobTickUpdate = new ProcedureSelector(this.withEntry("entity/on_tick_update"), mcreator,
				L10N.t("elementgui.living_entity.event_mob_tick_update"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onPlayerCollidesWith = new ProcedureSelector(this.withEntry("entity/when_player_collides"), mcreator,
				L10N.t("elementgui.living_entity.event_player_collides_with"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity"));
		onInitialSpawn = new ProcedureSelector(this.withEntry("entity/on_initial_spawn"), mcreator,
				L10N.t("elementgui.living_entity.event_initial_spawn"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		spawningCondition = new ProcedureSelector(this.withEntry("entity/condition_natural_spawning"), mcreator,
				L10N.t("elementgui.living_entity.condition_natural_spawn"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world")).setDefaultName(
				L10N.t("condition.common.use_vanilla")).makeInline();
		transparentModelCondition = new LogicProcedureSelector(this.withEntry("entity/condition_is_model_transparent"),
				mcreator, L10N.t("elementgui.living_entity.condition_is_model_transparent"),
				ProcedureSelector.Side.CLIENT, L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		isShakingCondition = new LogicProcedureSelector(this.withEntry("entity/condition_is_shaking"), mcreator,
				L10N.t("elementgui.living_entity.condition_is_shaking"), ProcedureSelector.Side.CLIENT,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		solidBoundingBox = new LogicProcedureSelector(this.withEntry("entity/condition_solid_bounding_box"), mcreator,
				L10N.t("elementgui.living_entity.condition_solid_bounding_box"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		breatheUnderwater = new LogicProcedureSelector(this.withEntry("entity/condition_can_breathe_underwater"),
				mcreator, L10N.t("elementgui.living_entity.condition_can_breathe_underwater"),
				AbstractProcedureSelector.Side.BOTH, L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		pushedByFluids = new LogicProcedureSelector(this.withEntry("entity/condition_fluids_can_push"), mcreator,
				L10N.t("elementgui.living_entity.condition_fluids_can_push"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		visualScale = new NumberProcedureSelector(this.withEntry("entity/visual_scale"), mcreator,
				L10N.t("elementgui.living_entity.visual_scale"), AbstractProcedureSelector.Side.CLIENT,
				new JSpinner(new SpinnerNumberModel(1, 0.01, 1024, 0.01)), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		boundingBoxScale = new NumberProcedureSelector(this.withEntry("entity/bounding_box_scale"), mcreator,
				L10N.t("elementgui.living_entity.bounding_box_scale"), AbstractProcedureSelector.Side.BOTH,
				new JSpinner(new SpinnerNumberModel(1, 0.01, 1024, 0.01)), 210,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		restrictionBiomes = new BiomeListField(mcreator, true);
		restrictionBiomes.setValidator(new ItemListFieldSingleTagValidator(restrictionBiomes));

		breedTriggerItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItemsAndTags, false, true);
		entityDataList = new JEntityDataList(mcreator, this);

		numberOfMobsPerGroup.setAllowEqualValues(true);

		mobModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));

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

		spawnInDungeons.setOpaque(false);
		mobModelTexture.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");

		mobDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentMainHand = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentHelmet = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentBody = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentLeggings = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentBoots = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		equipmentOffHand = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
		rangedAttackItem = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		modelLayers = new JModelLayerList(mcreator, this);

		JPanel pane1 = new JPanel(new BorderLayout(0, 0));
		JPanel pane2 = new JPanel(new BorderLayout(0, 0));
		JPanel pane3 = new JPanel(new BorderLayout(0, 0));
		JPanel pane4 = new JPanel(new BorderLayout(0, 0));
		JPanel pane5 = new JPanel(new BorderLayout(0, 0));
		JPanel pane6 = new JPanel(new BorderLayout(0, 0));
		JPanel pane7 = new JPanel(new BorderLayout(0, 0));
		JPanel pane8 = new JPanel(new BorderLayout(0, 0));

		JPanel subpane1 = new JPanel(new GridLayout(12, 2, 0, 2));

		immuneToFire.setOpaque(false);
		immuneToArrows.setOpaque(false);
		immuneToFallDamage.setOpaque(false);
		immuneToCactus.setOpaque(false);
		immuneToDrowning.setOpaque(false);
		immuneToLightning.setOpaque(false);
		immuneToPotions.setOpaque(false);
		immuneToPlayer.setOpaque(false);
		immuneToExplosion.setOpaque(false);
		immuneToTrident.setOpaque(false);
		immuneToAnvil.setOpaque(false);
		immuneToDragonBreath.setOpaque(false);
		immuneToWither.setOpaque(false);

		subpane1.setOpaque(false);

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/behaviour"),
				L10N.label("elementgui.living_entity.behaviour")));
		subpane1.add(mobBehaviourType);

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/creature_type"),
				L10N.label("elementgui.living_entity.creature_type")));
		subpane1.add(mobCreatureType);

		subpane1.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.living_entity.drop_health_xp_amount"),
				HelpUtils.helpButton(this.withEntry("entity/drop")),
				HelpUtils.helpButton(this.withEntry("entity/health")),
				HelpUtils.helpButton(this.withEntry("entity/xp_amount"))));
		subpane1.add(PanelUtils.westAndCenterElement(PanelUtils.totalCenterInPanel(mobDrop),
				PanelUtils.gridElements(1, 2, 2, 0, health, xpAmount), 8, 8));

		subpane1.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.living_entity.movement_speed_step_height"),
				HelpUtils.helpButton(this.withEntry("entity/movement_speed")),
				HelpUtils.helpButton(this.withEntry("entity/step_height"))));
		subpane1.add(PanelUtils.gridElements(1, 2, 2, 0, movementSpeed, stepHeight));

		subpane1.add(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.living_entity.follow_range_tracking_range"),
						HelpUtils.helpButton(this.withEntry("entity/follow_range")),
						HelpUtils.helpButton(this.withEntry("entity/tracking_range"))));
		subpane1.add(PanelUtils.gridElements(1, 2, 2, 0, followRange, trackingRange));

		subpane1.add(PanelUtils.join(FlowLayout.LEFT,
				L10N.label("elementgui.living_entity.attack_strenght_armor_value_knockback"),
				HelpUtils.helpButton(this.withEntry("entity/attack_strength")),
				HelpUtils.helpButton(this.withEntry("entity/armor_base_value")),
				HelpUtils.helpButton(this.withEntry("entity/attack_knockback")),
				HelpUtils.helpButton(this.withEntry("entity/knockback_resistance"))));
		subpane1.add(PanelUtils.gridElements(1, 4, 2, 0, attackStrength, armorBaseValue, attackKnockback,
				knockbackResistance));

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/ridable"),
				L10N.label("elementgui.living_entity.ridable")));
		subpane1.add(PanelUtils.join(FlowLayout.LEFT, 0, 8, ridable, canControlForward, canControlStrafe));

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/water_entity"),
				L10N.label("elementgui.living_entity.water_mob")));
		subpane1.add(waterMob);

		waterMob.addChangeListener(e -> {
			if (!isEditingMode()) {
				breatheUnderwater.setFixedValue(waterMob.isSelected());
				pushedByFluids.setFixedValue(!waterMob.isSelected());
			}
		});

		subpane1.add(new JEmptyBox());
		subpane1.add(breatheUnderwater);

		subpane1.add(new JEmptyBox());
		subpane1.add(pushedByFluids);

		pushedByFluids.setFixedValue(true);

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/flying_entity"),
				L10N.label("elementgui.living_entity.flying_mob")));
		subpane1.add(flyingMob);

		subpane1.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/equipment"),
				L10N.label("elementgui.living_entity.equipment")));
		subpane1.add(PanelUtils.join(FlowLayout.LEFT, 0, 2, PanelUtils.totalCenterInPanel(
				PanelUtils.join(FlowLayout.LEFT, 2, 0, equipmentMainHand, equipmentOffHand, equipmentHelmet,
						equipmentBody, equipmentLeggings, equipmentBoots))));

		hasAI.setOpaque(false);
		isBoss.setOpaque(false);
		waterMob.setOpaque(false);
		flyingMob.setOpaque(false);
		hasSpawnEgg.setOpaque(false);
		disableCollisions.setOpaque(false);

		livingSound.setText("");
		hurtSound.setText("entity.generic.hurt");
		deathSound.setText("entity.generic.death");

		JPanel subpanel2 = new JPanel(new GridLayout(1, 2, 0, 2));
		subpanel2.setOpaque(false);

		subpanel2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/immunity"),
				L10N.label("elementgui.living_entity.is_immune_to")));
		subpanel2.add(
				PanelUtils.gridElements(3, 5, 0, 0, immuneToFire, immuneToArrows, immuneToFallDamage, immuneToCactus,
						immuneToDrowning, immuneToLightning, immuneToPotions, immuneToPlayer, immuneToExplosion,
						immuneToAnvil, immuneToTrident, immuneToDragonBreath, immuneToWither));

		pane1.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(subpane1, subpanel2)));

		JPanel entityDataListPanel = new JPanel(new GridLayout());

		JComponent entityDataListComp = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/entity_data"),
						L10N.label("elementgui.living_entity.entity_data")), entityDataList);
		entityDataListPanel.setOpaque(false);
		entityDataListComp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		entityDataListPanel.add(entityDataListComp);

		JPanel spo2 = new JPanel(new GridLayout(12, 2, 2, 2));

		spo2.setOpaque(false);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/name"),
				L10N.label("elementgui.living_entity.name")));
		spo2.add(mobName);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/model"),
				L10N.label("elementgui.living_entity.entity_model")));
		spo2.add(mobModel);

		JButton importmobtexture = new JButton(UIRES.get("18px.add"));
		importmobtexture.setToolTipText(L10N.t("elementgui.living_entity.entity_model_import"));
		importmobtexture.setOpaque(false);
		importmobtexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY);
			mobModelTexture.removeAllItems();
			mobModelTexture.addItem("");
			mcreator.getFolderManager().getTexturesList(TextureType.ENTITY)
					.forEach(el -> mobModelTexture.addItem(el.getName()));
		});

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/texture"),
				L10N.label("elementgui.living_entity.texture")));
		spo2.add(PanelUtils.centerAndEastElement(mobModelTexture, importmobtexture, 0, 0));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/bounding_box"),
				L10N.label("elementgui.living_entity.bounding_box")));
		spo2.add(PanelUtils.join(FlowLayout.LEFT, 0, 0, modelWidth, new JEmptyBox(2, 2), modelHeight,
				new JEmptyBox(2, 2), modelShadowSize, new JEmptyBox(2, 2), mountedYOffset, new JEmptyBox(2, 2),
				disableCollisions));

		spo2.add(new JEmptyBox());
		spo2.add(visualScale);

		spo2.add(new JEmptyBox());
		spo2.add(boundingBoxScale);

		spo2.add(new JEmptyBox());
		spo2.add(solidBoundingBox);

		spo2.add(new JEmptyBox());
		spo2.add(transparentModelCondition);

		spo2.add(new JEmptyBox());
		spo2.add(isShakingCondition);

		ComponentUtils.deriveFont(mobModelTexture, 16);
		ComponentUtils.deriveFont(aiBase, 16);
		ComponentUtils.deriveFont(mobModel, 16);
		ComponentUtils.deriveFont(rangedItemType, 16);

		rangedItemType.setPrototypeDisplayValue("XXXXXXXXXXXXX");

		mobModel.setRenderer(new ModelComboBoxRenderer());

		spawnEggBaseColor.setOpaque(false);
		spawnEggDotColor.setOpaque(false);

		modelWidth.setPreferredSize(new Dimension(85, 41));
		mountedYOffset.setPreferredSize(new Dimension(85, 41));
		modelHeight.setPreferredSize(new Dimension(85, 41));
		modelShadowSize.setPreferredSize(new Dimension(85, 41));

		armorBaseValue.setPreferredSize(new Dimension(0, 32));
		movementSpeed.setPreferredSize(new Dimension(0, 32));
		stepHeight.setPreferredSize(new Dimension(0, 32));
		trackingRange.setPreferredSize(new Dimension(0, 32));
		attackStrength.setPreferredSize(new Dimension(0, 32));
		attackKnockback.setPreferredSize(new Dimension(0, 32));
		knockbackResistance.setPreferredSize(new Dimension(0, 32));
		followRange.setPreferredSize(new Dimension(0, 32));
		health.setPreferredSize(new Dimension(0, 32));
		xpAmount.setPreferredSize(new Dimension(0, 32));

		rangedAttackInterval.setPreferredSize(new Dimension(85, 32));
		rangedAttackRadius.setPreferredSize(new Dimension(85, 32));

		mobModel.addActionListener(e -> {
			if (disableMobModelCheckBoxListener)
				return;

			if (biped.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(1.8);
			} else if (chicken.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.4);
				modelHeight.setValue(0.7);
			} else if (cod.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.5);
				modelHeight.setValue(0.3);
			} else if (cow.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.9);
				modelHeight.setValue(1.4);
			} else if (creeper.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(1.7);
			} else if (ghast.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(1.0);
				modelHeight.setValue(1.0);
			} else if (ocelot.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(0.7);
			} else if (pig.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.9);
				modelHeight.setValue(0.9);
			} else if (piglin.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(1.95);
			} else if (salmon.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.7);
				modelHeight.setValue(0.4);
			} else if (slime.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(1.0);
				modelHeight.setValue(1.0);
			} else if (spider.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(1.4);
				modelHeight.setValue(0.9);
			} else if (villager.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(1.95);
			} else if (silverfish.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.4);
				modelHeight.setValue(0.3);
			} else if (witch.equals(mobModel.getSelectedItem())) {
				modelWidth.setValue(0.6);
				modelHeight.setValue(1.95);
			}
		});

		creativeTab.setPrototypeDisplayValue(new DataListEntry.Dummy("XXXXXXXXXXXXXXXX"));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_egg_options"),
				L10N.label("elementgui.living_entity.spawn_egg_options")));
		spo2.add(PanelUtils.join(FlowLayout.LEFT, 0, 0, hasSpawnEgg, new JEmptyBox(2, 2), spawnEggBaseColor,
				new JEmptyBox(2, 2), spawnEggDotColor, new JEmptyBox(5, 5), creativeTab));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/boss_entity"),
				L10N.label("elementgui.living_entity.mob_boss")));
		spo2.add(PanelUtils.join(FlowLayout.LEFT, 0, 0, isBoss, new JEmptyBox(5, 5), bossBarColor, new JEmptyBox(5, 5),
				bossBarType));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/label"),
				L10N.label("elementgui.living_entity.label")));
		spo2.add(mobLabel);

		ComponentUtils.deriveFont(mobLabel, 16);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(spo2));

		JComponent layerList = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/model_layers"),
						L10N.label("elementgui.living_entity.model_layers")), modelLayers);

		layerList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane8.setOpaque(false);
		pane8.add(layerList);

		JPanel spo6 = new JPanel(new GridLayout(4, 2, 2, 2));
		spo6.setOpaque(false);

		spo6.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/sound"),
				L10N.label("elementgui.living_entity.sound")));
		spo6.add(livingSound);

		spo6.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/step_sound"),
				L10N.label("elementgui.living_entity.step_sound")));
		spo6.add(stepSound);

		spo6.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/hurt_sound"),
				L10N.label("elementgui.living_entity.hurt_sound")));
		spo6.add(hurtSound);

		spo6.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/death_sound"),
				L10N.label("elementgui.living_entity.death_sound")));
		spo6.add(deathSound);

		pane6.setOpaque(false);
		pane6.add("Center", PanelUtils.totalCenterInPanel(spo6));

		JPanel aitop = new JPanel(new GridLayout(3, 1, 0, 2));
		aitop.setOpaque(false);
		aitop.add(PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/enable_ai"), hasAI)));

		aitop.add(PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/breedable"), breedable), breedTriggerItems,
				tameable));

		breedTriggerItems.setPreferredSize(new Dimension(230, 32));

		aitop.add(PanelUtils.join(FlowLayout.LEFT, new JEmptyBox(5, 5),
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/base"),
						L10N.label("elementgui.living_entity.mob_base")), aiBase));

		aiBase.setPreferredSize(new Dimension(250, 32));
		aiBase.addActionListener(e -> {
			if (editorReady)
				regenerateAITasks();
		});

		JPanel aitopoveral = new JPanel(new BorderLayout(5, 0));
		aitopoveral.setOpaque(false);

		aitopoveral.add("West", aitop);

		aitopoveral.add("Center", PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(this.withEntry("entity/do_ranged_attacks"), ranged), rangedItemType,
				rangedAttackItem, rangedAttackInterval, rangedAttackRadius));

		rangedItemType.addActionListener(e -> enableOrDisableFields());

		ridable.setOpaque(false);
		canControlStrafe.setOpaque(false);
		canControlForward.setOpaque(false);

		aitopoveral.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.living_entity.ai_parameters"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JPanel aipan = new JPanel(new BorderLayout(0, 5));
		aipan.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK).getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.AI_TASK);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.AI_BUILDER);
			blocklyPanel.getJSBridge().setJavaScriptEventListener(
					() -> new Thread(LivingEntityGUI.this::regenerateAITasks, "AITasksRegenerate").start());
			if (!isEditingMode()) {
				setDefaultAISet();
			}
		});

		aipan.add("North", aitopoveral);

		JPanel bpb = new JPanel(new GridLayout());
		bpb.setOpaque(false);
		bpb.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.living_entity.ai_tasks"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));
		BlocklyEditorToolbar blocklyEditorToolbar = new BlocklyEditorToolbar(mcreator, BlocklyEditorType.AI_TASK,
				blocklyPanel);
		blocklyEditorToolbar.setTemplateLibButtonWidth(157);
		bpb.add(PanelUtils.northAndCenterElement(blocklyEditorToolbar, blocklyPanel));
		aipan.add("Center", bpb);
		aipan.add("South", compileNotesPanel);

		blocklyPanel.setPreferredSize(new Dimension(150, 150));

		pane3.add("Center", ComponentUtils.applyPadding(aipan, 10, true, true, true, true));

		breedable.setOpaque(false);
		tameable.setOpaque(false);
		ranged.setOpaque(false);

		hasAI.setSelected(true);

		breedable.addActionListener(actionEvent -> enableOrDisableFields());
		isBoss.addActionListener(e -> enableOrDisableFields());

		pane3.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(3, 4, 5, 5));
		events.add(onStruckByLightning);
		events.add(whenMobFalls);
		events.add(whenMobDies);
		events.add(whenMobIsHurt);
		events.add(onRightClickedOn);
		events.add(whenThisMobKillsAnother);
		events.add(onMobTickUpdate);
		events.add(onPlayerCollidesWith);
		events.add(onInitialSpawn);
		events.setOpaque(false);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));

		isBoss.setOpaque(false);

		pane4.setOpaque(false);

		JPanel selp = new JPanel(new GridLayout(7, 2, 30, 2));
		selp.setOpaque(false);

		ComponentUtils.deriveFont(mobName, 16);

		spawnThisMob.setSelected(true);
		doesDespawnWhenIdle.setSelected(true);

		spawnThisMob.setOpaque(false);
		doesDespawnWhenIdle.setOpaque(false);

		hasSpawnEgg.setSelected(true);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/enable_spawning"),
				L10N.label("elementgui.living_entity.enable_mob_spawning")));
		selp.add(spawnThisMob);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/despawn_idle"),
				L10N.label("elementgui.living_entity.despawn_idle")));
		selp.add(doesDespawnWhenIdle);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_weight"),
				L10N.label("elementgui.living_entity.spawn_weight")));
		selp.add(spawningProbability);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_type"),
				L10N.label("elementgui.living_entity.spawn_type")));
		selp.add(mobSpawningType);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_group_size"),
				L10N.label("elementgui.living_entity.spawn_group_size")));
		selp.add(numberOfMobsPerGroup);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.living_entity.restrict_to_biomes")));
		selp.add(restrictionBiomes);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_in_dungeons"),
				L10N.label("elementgui.living_entity.does_spawn_in_dungeons")));
		selp.add(spawnInDungeons);

		JComponent selpcont = PanelUtils.northAndCenterElement(selp, PanelUtils.gridElements(1, 2, 30, 2,
				PanelUtils.join(FlowLayout.LEFT, 5, 0, L10N.label("elementgui.living_entity.spawn_general_condition")),
				spawningCondition), 5, 5);

		pane5.add("Center", PanelUtils.totalCenterInPanel(selpcont));

		pane5.setOpaque(false);

		JPanel props = new JPanel(new GridLayout(3, 2, 35, 2));
		props.setOpaque(false);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/bind_gui"),
				L10N.label("elementgui.living_entity.bind_to_gui")));
		props.add(guiBoundTo);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/inventory_size"),
				L10N.label("elementgui.living_entity.inventory_size")));
		props.add(inventorySize);

		props.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/inventory_stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		props.add(inventoryStackSize);

		pane7.add(PanelUtils.totalCenterInPanel(props));
		pane7.setOpaque(false);
		pane7.setOpaque(false);

		mobModelTexture.setValidator(() -> {
			if (mobModelTexture.getSelectedItem() == null || mobModelTexture.getSelectedItem().isEmpty())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.living_entity.error_entity_model_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		mobName.setValidator(
				new TextFieldValidator(mobName, L10N.t("elementgui.living_entity.error_entity_needs_name")));
		mobName.enableRealtimeValidation();

		pane1.setOpaque(false);

		addPage(L10N.t("elementgui.living_entity.page_visual"), pane2);
		addPage(L10N.t("elementgui.living_entity.page_model_layers"), pane8, false);
		addPage(L10N.t("elementgui.living_entity.page_behaviour"), pane1);
		addPage(L10N.t("elementgui.living_entity.page_sound"), pane6);
		addPage(L10N.t("elementgui.living_entity.page_entity_data"), entityDataListPanel, false);
		addPage(L10N.t("elementgui.common.page_inventory"), pane7);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);
		addPage(L10N.t("elementgui.living_entity.page_ai_and_goals"), pane3);
		addPage(L10N.t("elementgui.living_entity.page_spawning"), pane5);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			mobName.setText(readableNameFromModElement);
		}

		enableOrDisableFields();

		editorReady = true;
	}

	@Override public void reloadDataLists() {
		disableMobModelCheckBoxListener = true;

		super.reloadDataLists();
		onStruckByLightning.refreshListKeepSelected();
		whenMobFalls.refreshListKeepSelected();
		whenMobDies.refreshListKeepSelected();
		whenMobIsHurt.refreshListKeepSelected();
		onRightClickedOn.refreshListKeepSelected();
		whenThisMobKillsAnother.refreshListKeepSelected();
		onMobTickUpdate.refreshListKeepSelected();
		onPlayerCollidesWith.refreshListKeepSelected();
		onInitialSpawn.refreshListKeepSelected();

		spawningCondition.refreshListKeepSelected();
		transparentModelCondition.refreshListKeepSelected();
		isShakingCondition.refreshListKeepSelected();
		solidBoundingBox.refreshListKeepSelected();
		breatheUnderwater.refreshListKeepSelected();
		pushedByFluids.refreshListKeepSelected();
		visualScale.refreshListKeepSelected();
		boundingBoxScale.refreshListKeepSelected();

		modelLayers.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(mobModelTexture, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(mobModel, ListUtils.merge(Arrays.asList(builtinmobmodels),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("MISC"));

		ComboBoxUtil.updateComboBoxContents(rangedItemType, ListUtils.merge(Collections.singleton("Default item"),
				mcreator.getWorkspace().getModElements().stream()
						.filter(var -> var.getType() == ModElementType.PROJECTILE).map(ModElement::getName)
						.collect(Collectors.toList())), "Default item");

		ComboBoxUtil.updateComboBoxContents(guiBoundTo, ListUtils.merge(Collections.singleton("<NONE>"),
				mcreator.getWorkspace().getModElements().stream().filter(var -> var.getType() == ModElementType.GUI)
						.map(ModElement::getName).collect(Collectors.toList())), "<NONE>");

		disableMobModelCheckBoxListener = false;
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0) {
			return new AggregatedValidationResult(mobModelTexture, mobName);
		} else if (page == 1) {
			return modelLayers.getValidationResult();
		} else if (page == 7) {
			return new BlocklyAggregatedValidationResult(compileNotesPanel.getCompileNotes(),
					compileNote -> "Living entity AI builder: " + compileNote);
		} else if (page == 8) {
			return new AggregatedValidationResult(restrictionBiomes);
		}
		return new AggregatedValidationResult.PASS();
	}

	private void enableOrDisableFields() {
		if (breedable.isSelected()) {
			hasAI.setSelected(true);
			hasAI.setEnabled(false);
			breedTriggerItems.setEnabled(true);
			tameable.setEnabled(true);
		} else {
			hasAI.setEnabled(true);
			breedTriggerItems.setEnabled(false);
			tameable.setEnabled(false);
		}

		bossBarColor.setEnabled(isBoss.isSelected());
		bossBarType.setEnabled(isBoss.isSelected());

		rangedAttackItem.setEnabled("Default item".equals(rangedItemType.getSelectedItem()));
	}

	@Override public void openInEditingMode(LivingEntity livingEntity) {
		disableMobModelCheckBoxListener = true;
		editorReady = false;

		mobName.setText(livingEntity.mobName);
		mobModelTexture.setSelectedItem(livingEntity.mobModelTexture);
		transparentModelCondition.setSelectedProcedure(livingEntity.transparentModelCondition);
		isShakingCondition.setSelectedProcedure(livingEntity.isShakingCondition);
		solidBoundingBox.setSelectedProcedure(livingEntity.solidBoundingBox);
		visualScale.setSelectedProcedure(livingEntity.visualScale);
		boundingBoxScale.setSelectedProcedure(livingEntity.boundingBoxScale);
		mobSpawningType.setSelectedItem(livingEntity.mobSpawningType);
		rangedItemType.setSelectedItem(livingEntity.rangedItemType);
		spawnEggBaseColor.setColor(livingEntity.spawnEggBaseColor);
		spawnEggDotColor.setColor(livingEntity.spawnEggDotColor);
		mobLabel.setText(livingEntity.mobLabel);
		onStruckByLightning.setSelectedProcedure(livingEntity.onStruckByLightning);
		whenMobFalls.setSelectedProcedure(livingEntity.whenMobFalls);
		whenMobDies.setSelectedProcedure(livingEntity.whenMobDies);
		whenMobIsHurt.setSelectedProcedure(livingEntity.whenMobIsHurt);
		onRightClickedOn.setSelectedProcedure(livingEntity.onRightClickedOn);
		whenThisMobKillsAnother.setSelectedProcedure(livingEntity.whenThisMobKillsAnother);
		onMobTickUpdate.setSelectedProcedure(livingEntity.onMobTickUpdate);
		onPlayerCollidesWith.setSelectedProcedure(livingEntity.onPlayerCollidesWith);
		onInitialSpawn.setSelectedProcedure(livingEntity.onInitialSpawn);
		mobBehaviourType.setSelectedItem(livingEntity.mobBehaviourType);
		mobCreatureType.setSelectedItem(livingEntity.mobCreatureType);
		attackStrength.setValue(livingEntity.attackStrength);
		attackKnockback.setValue(livingEntity.attackKnockback);
		knockbackResistance.setValue(livingEntity.knockbackResistance);
		movementSpeed.setValue(livingEntity.movementSpeed);
		stepHeight.setValue(livingEntity.stepHeight);
		mobDrop.setBlock(livingEntity.mobDrop);
		equipmentMainHand.setBlock(livingEntity.equipmentMainHand);
		equipmentHelmet.setBlock(livingEntity.equipmentHelmet);
		equipmentBody.setBlock(livingEntity.equipmentBody);
		equipmentLeggings.setBlock(livingEntity.equipmentLeggings);
		equipmentBoots.setBlock(livingEntity.equipmentBoots);
		health.setValue(livingEntity.health);
		trackingRange.setValue(livingEntity.trackingRange);
		followRange.setValue(livingEntity.followRange);
		immuneToFire.setSelected(livingEntity.immuneToFire);
		immuneToArrows.setSelected(livingEntity.immuneToArrows);
		immuneToFallDamage.setSelected(livingEntity.immuneToFallDamage);
		immuneToCactus.setSelected(livingEntity.immuneToCactus);
		immuneToDrowning.setSelected(livingEntity.immuneToDrowning);
		immuneToLightning.setSelected(livingEntity.immuneToLightning);
		immuneToPotions.setSelected(livingEntity.immuneToPotions);
		immuneToPlayer.setSelected(livingEntity.immuneToPlayer);
		immuneToExplosion.setSelected(livingEntity.immuneToExplosion);
		immuneToTrident.setSelected(livingEntity.immuneToTrident);
		immuneToAnvil.setSelected(livingEntity.immuneToAnvil);
		immuneToWither.setSelected(livingEntity.immuneToWither);
		immuneToDragonBreath.setSelected(livingEntity.immuneToDragonBreath);
		xpAmount.setValue(livingEntity.xpAmount);
		livingSound.setSound(livingEntity.livingSound);
		hurtSound.setSound(livingEntity.hurtSound);
		deathSound.setSound(livingEntity.deathSound);
		stepSound.setSound(livingEntity.stepSound);
		hasAI.setSelected(livingEntity.hasAI);
		isBoss.setSelected(livingEntity.isBoss);
		hasSpawnEgg.setSelected(livingEntity.hasSpawnEgg);
		disableCollisions.setSelected(livingEntity.disableCollisions);
		aiBase.setSelectedItem(livingEntity.aiBase);
		spawningProbability.setValue(livingEntity.spawningProbability);
		numberOfMobsPerGroup.setMinValue(livingEntity.minNumberOfMobsPerGroup);
		numberOfMobsPerGroup.setMaxValue(livingEntity.maxNumberOfMobsPerGroup);
		spawnInDungeons.setSelected(livingEntity.spawnInDungeons);
		restrictionBiomes.setListElements(livingEntity.restrictionBiomes);
		spawningCondition.setSelectedProcedure(livingEntity.spawningCondition);
		breedTriggerItems.setListElements(livingEntity.breedTriggerItems);
		bossBarColor.setSelectedItem(livingEntity.bossBarColor);
		bossBarType.setSelectedItem(livingEntity.bossBarType);
		equipmentOffHand.setBlock(livingEntity.equipmentOffHand);
		ridable.setSelected(livingEntity.ridable);
		canControlStrafe.setSelected(livingEntity.canControlStrafe);
		canControlForward.setSelected(livingEntity.canControlForward);
		breedable.setSelected(livingEntity.breedable);
		tameable.setSelected(livingEntity.tameable);
		ranged.setSelected(livingEntity.ranged);
		rangedAttackItem.setBlock(livingEntity.rangedAttackItem);
		rangedAttackInterval.setValue(livingEntity.rangedAttackInterval);
		rangedAttackRadius.setValue(livingEntity.rangedAttackRadius);
		spawnThisMob.setSelected(livingEntity.spawnThisMob);
		doesDespawnWhenIdle.setSelected(livingEntity.doesDespawnWhenIdle);
		modelWidth.setValue(livingEntity.modelWidth);
		modelHeight.setValue(livingEntity.modelHeight);
		mountedYOffset.setValue(livingEntity.mountedYOffset);
		modelShadowSize.setValue(livingEntity.modelShadowSize);
		armorBaseValue.setValue(livingEntity.armorBaseValue);
		waterMob.setSelected(livingEntity.waterMob);
		breatheUnderwater.setSelectedProcedure(livingEntity.breatheUnderwater);
		pushedByFluids.setSelectedProcedure(livingEntity.pushedByFluids);
		flyingMob.setSelected(livingEntity.flyingMob);
		guiBoundTo.setSelectedItem(livingEntity.guiBoundTo);
		inventorySize.setValue(livingEntity.inventorySize);
		inventoryStackSize.setValue(livingEntity.inventoryStackSize);
		modelLayers.setEntries(livingEntity.modelLayers);

		entityDataList.setEntries(livingEntity.entityDataEntries);

		if (livingEntity.creativeTab != null)
			creativeTab.setSelectedItem(livingEntity.creativeTab);

		Model model = livingEntity.getEntityModel();
		if (model != null && model.getType() != null && model.getReadableName() != null)
			mobModel.setSelectedItem(model);

		blocklyPanel.setXMLDataOnly(livingEntity.aixml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(livingEntity.aixml);
			blocklyPanel.triggerEventFunction();
		});

		enableOrDisableFields();

		disableMobModelCheckBoxListener = false;
		editorReady = true;
	}

	@Override public LivingEntity getElementFromGUI() {
		LivingEntity livingEntity = new LivingEntity(modElement);
		livingEntity.mobName = mobName.getText();
		livingEntity.mobLabel = mobLabel.getText();
		livingEntity.mobModelTexture = mobModelTexture.getSelectedItem();
		livingEntity.spawnEggBaseColor = spawnEggBaseColor.getColor();
		livingEntity.transparentModelCondition = transparentModelCondition.getSelectedProcedure();
		livingEntity.isShakingCondition = isShakingCondition.getSelectedProcedure();
		livingEntity.solidBoundingBox = solidBoundingBox.getSelectedProcedure();
		livingEntity.visualScale = visualScale.getSelectedProcedure();
		livingEntity.boundingBoxScale = boundingBoxScale.getSelectedProcedure();
		livingEntity.spawnEggDotColor = spawnEggDotColor.getColor();
		livingEntity.hasSpawnEgg = hasSpawnEgg.isSelected();
		livingEntity.disableCollisions = disableCollisions.isSelected();
		livingEntity.isBoss = isBoss.isSelected();
		livingEntity.bossBarColor = (String) bossBarColor.getSelectedItem();
		livingEntity.bossBarType = (String) bossBarType.getSelectedItem();
		livingEntity.equipmentMainHand = equipmentMainHand.getBlock();
		livingEntity.equipmentOffHand = equipmentOffHand.getBlock();
		livingEntity.equipmentHelmet = equipmentHelmet.getBlock();
		livingEntity.equipmentBody = equipmentBody.getBlock();
		livingEntity.equipmentLeggings = equipmentLeggings.getBlock();
		livingEntity.equipmentBoots = equipmentBoots.getBlock();
		livingEntity.mobBehaviourType = (String) mobBehaviourType.getSelectedItem();
		livingEntity.mobCreatureType = (String) mobCreatureType.getSelectedItem();
		livingEntity.attackStrength = (int) attackStrength.getValue();
		livingEntity.attackKnockback = (double) attackKnockback.getValue();
		livingEntity.knockbackResistance = (double) knockbackResistance.getValue();
		livingEntity.movementSpeed = (double) movementSpeed.getValue();
		livingEntity.stepHeight = (double) stepHeight.getValue();
		livingEntity.health = (int) health.getValue();
		livingEntity.trackingRange = (int) trackingRange.getValue();
		livingEntity.followRange = (int) followRange.getValue();
		livingEntity.immuneToFire = immuneToFire.isSelected();
		livingEntity.immuneToArrows = immuneToArrows.isSelected();
		livingEntity.immuneToFallDamage = immuneToFallDamage.isSelected();
		livingEntity.immuneToCactus = immuneToCactus.isSelected();
		livingEntity.immuneToDrowning = immuneToDrowning.isSelected();
		livingEntity.immuneToLightning = immuneToLightning.isSelected();
		livingEntity.immuneToPotions = immuneToPotions.isSelected();
		livingEntity.immuneToPlayer = immuneToPlayer.isSelected();
		livingEntity.immuneToExplosion = immuneToExplosion.isSelected();
		livingEntity.immuneToTrident = immuneToTrident.isSelected();
		livingEntity.immuneToAnvil = immuneToAnvil.isSelected();
		livingEntity.immuneToWither = immuneToWither.isSelected();
		livingEntity.immuneToDragonBreath = immuneToDragonBreath.isSelected();
		livingEntity.xpAmount = (int) xpAmount.getValue();
		livingEntity.ridable = ridable.isSelected();
		livingEntity.canControlForward = canControlForward.isSelected();
		livingEntity.canControlStrafe = canControlStrafe.isSelected();
		livingEntity.mobDrop = mobDrop.getBlock();
		livingEntity.livingSound = livingSound.getSound();
		livingEntity.hurtSound = hurtSound.getSound();
		livingEntity.deathSound = deathSound.getSound();
		livingEntity.stepSound = stepSound.getSound();
		livingEntity.spawningCondition = spawningCondition.getSelectedProcedure();
		livingEntity.onStruckByLightning = onStruckByLightning.getSelectedProcedure();
		livingEntity.whenMobFalls = whenMobFalls.getSelectedProcedure();
		livingEntity.whenMobDies = whenMobDies.getSelectedProcedure();
		livingEntity.whenMobIsHurt = whenMobIsHurt.getSelectedProcedure();
		livingEntity.onRightClickedOn = onRightClickedOn.getSelectedProcedure();
		livingEntity.whenThisMobKillsAnother = whenThisMobKillsAnother.getSelectedProcedure();
		livingEntity.onMobTickUpdate = onMobTickUpdate.getSelectedProcedure();
		livingEntity.onPlayerCollidesWith = onPlayerCollidesWith.getSelectedProcedure();
		livingEntity.onInitialSpawn = onInitialSpawn.getSelectedProcedure();
		livingEntity.hasAI = hasAI.isSelected();
		livingEntity.aiBase = aiBase.getSelectedItem();
		livingEntity.aixml = blocklyPanel.getXML();
		livingEntity.breedable = breedable.isSelected();
		livingEntity.tameable = tameable.isSelected();
		livingEntity.breedTriggerItems = breedTriggerItems.getListElements();
		livingEntity.ranged = ranged.isSelected();
		livingEntity.rangedAttackItem = rangedAttackItem.getBlock();
		livingEntity.rangedAttackInterval = (int) rangedAttackInterval.getValue();
		livingEntity.rangedAttackRadius = (double) rangedAttackRadius.getValue();
		livingEntity.spawnThisMob = spawnThisMob.isSelected();
		livingEntity.doesDespawnWhenIdle = doesDespawnWhenIdle.isSelected();
		livingEntity.spawningProbability = (int) spawningProbability.getValue();
		livingEntity.mobSpawningType = (String) mobSpawningType.getSelectedItem();
		livingEntity.rangedItemType = rangedItemType.getSelectedItem();
		livingEntity.minNumberOfMobsPerGroup = numberOfMobsPerGroup.getIntMinValue();
		livingEntity.maxNumberOfMobsPerGroup = numberOfMobsPerGroup.getIntMaxValue();
		livingEntity.restrictionBiomes = restrictionBiomes.getListElements();
		livingEntity.spawnInDungeons = spawnInDungeons.isSelected();
		livingEntity.modelWidth = (double) modelWidth.getValue();
		livingEntity.modelHeight = (double) modelHeight.getValue();
		livingEntity.mountedYOffset = (double) mountedYOffset.getValue();
		livingEntity.modelShadowSize = (double) modelShadowSize.getValue();
		livingEntity.armorBaseValue = (double) armorBaseValue.getValue();
		livingEntity.mobModelName = Objects.requireNonNull(mobModel.getSelectedItem()).getReadableName();
		livingEntity.waterMob = waterMob.isSelected();
		livingEntity.breatheUnderwater = breatheUnderwater.getSelectedProcedure();
		livingEntity.pushedByFluids = pushedByFluids.getSelectedProcedure();
		livingEntity.flyingMob = flyingMob.isSelected();
		livingEntity.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		livingEntity.inventorySize = (int) inventorySize.getValue();
		livingEntity.inventoryStackSize = (int) inventoryStackSize.getValue();
		livingEntity.guiBoundTo = guiBoundTo.getSelectedItem();
		livingEntity.entityDataEntries = entityDataList.getEntries();
		livingEntity.modelLayers = modelLayers.getEntries();
		return livingEntity;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-mob");
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

}