/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.modgui.bedrock;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxType;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.types.bedrock.BEEntity;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
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
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class BEEntityGUI extends ModElementGUI<BEEntity> implements IBlocklyPanelHolder {

	private final VTextField entityName = new VTextField().requireValue("elementgui.common.error_entity_needs_name")
			.enableRealtimeValidation();

	private static final Model biped = new Model.BuiltInModel("Biped");
	private static final Model chicken = new Model.BuiltInModel("Chicken");
	private static final Model cow = new Model.BuiltInModel("Cow");
	private static final Model creeper = new Model.BuiltInModel("Creeper");
	private static final Model ghast = new Model.BuiltInModel("Ghast");
	private static final Model pig = new Model.BuiltInModel("Pig");
	private static final Model silverfish = new Model.BuiltInModel("Silverfish");
	private static final Model slime = new Model.BuiltInModel("Slime");
	private static final Model spider = new Model.BuiltInModel("Spider");
	private static final Model villager = new Model.BuiltInModel("Villager");
	public static final Model[] builtinmobmodels = new Model[] { biped, chicken, cow, creeper, ghast, pig, silverfish,
			slime, spider, villager };
	private final SearchableComboBox<Model> entityModel = new SearchableComboBox<>(builtinmobmodels);
	private TextureComboBox modelTexture;
	private final JSpinner collisionBoxWidth = new JSpinner(new SpinnerNumberModel(0.6, 0, 1024, 0.1));
	private final JSpinner collisionBoxHeight = new JSpinner(new SpinnerNumberModel(1.9, 0, 1024, 0.1));

	private final JComboBox<String> entityBehaviourType = new JComboBox<>(new String[] { "Mob", "Creature" });
	private final JCheckBox isSummonable = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox waterEntity = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canFly = L10N.checkbox("elementgui.common.enable");
	private final JSpinner flyingSpeedValue = new JSpinner(new SpinnerNumberModel(0.3, 0, 50, 0.1));

	private final JSpinner attackDamage = new JSpinner(new SpinnerNumberModel(3, -10000, 10000, 1));
	private final JSpinner speedValue = new JSpinner(new SpinnerNumberModel(0.3, 0, 50, 0.1));
	private final JSpinner healthValue = new JSpinner(new SpinnerNumberModel(20, 0, 1024, 1));
	private final JSpinner followRangeValue = new JSpinner(new SpinnerNumberModel(64, 0, 10000, 1));
	private final JSpinner xpAmountOnDeath = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
	private MCItemHolder entityDrop;
	private final JCheckBox isPushable = L10N.checkbox("elementgui.beentity.is_pushable");
	private final JCheckBox isPushableByPiston = L10N.checkbox("elementgui.beentity.is_pushable_by_piston");
	private final JCheckBox isImmuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isImmuneToFallDamage = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isImmuneToDrowning = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox hasSpawnEgg = L10N.checkbox("elementgui.common.enable");
	private final JColor spawnEggBaseColor = new JColor(mcreator, false, false).withColorTextColumns(5);
	private final JColor spawnEggDotColor = new JColor(mcreator, false, false).withColorTextColumns(5);

	private final JCheckBox spawnNaturally = L10N.checkbox("elementgui.common.enable");
	private final JComboBox<String> populationControl = new JComboBox<>(
			ElementUtil.getDataListAsStringArray("mobspawntypes"));
	private final JSpinner spawningProbability = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
	private final JMinMaxSpinner entityHerd = new JMinMaxSpinner(4, 4, 1, 1000, 1).allowEqualValues();

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private Map<String, ToolboxBlock> externalBlocks;
	private final List<IBlocklyPanelHolder.BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	public BEEntityGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		entityDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		JPanel visualPanel = new JPanel(new BorderLayout(10, 10));
		visualPanel.setOpaque(false);
		JPanel behaviourPanel = new JPanel(new BorderLayout(10, 10));
		behaviourPanel.setOpaque(false);
		JPanel aiPanel = new JPanel(new BorderLayout(10, 10));
		aiPanel.setOpaque(false);
		JPanel spawningPanel = new JPanel(new BorderLayout(10, 10));
		spawningPanel.setOpaque(false);

		JPanel visualProps = new JPanel(new GridLayout(4, 2, 30, 2));
		visualProps.setOpaque(false);

		visualProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/name"),
				L10N.label("elementgui.living_entity.name")));
		visualProps.add(entityName);

		visualProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/model"),
				L10N.label("elementgui.beentity.entity_model")));
		visualProps.add(entityModel);

		visualProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/texture"),
				L10N.label("elementgui.living_entity.texture")));
		modelTexture = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
				"elementgui.living_entity.error_entity_model_needs_texture");
		visualProps.add(modelTexture);

		visualProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/bounding_box"),
				L10N.label("elementgui.beentity.collision_box")));
		visualProps.add(PanelUtils.gridElements(1, 2, collisionBoxWidth, collisionBoxHeight));

		visualPanel.add("Center", PanelUtils.totalCenterInPanel(visualProps));

		JPanel behaviourProps = new JPanel(new GridLayout(15, 2, 30, 2));
		behaviourProps.setOpaque(false);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/behaviour"),
				L10N.label("elementgui.living_entity.behaviour")));
		behaviourProps.add(entityBehaviourType);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/is_summonable"),
				L10N.label("elementgui.beentity.is_summonable")));
		behaviourProps.add(isSummonable);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/water_entity"),
				L10N.label("elementgui.living_entity.water_mob")));
		behaviourProps.add(waterEntity);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/flying_entity"),
				L10N.label("elementgui.beentity.flying_properties")));
		behaviourProps.add(PanelUtils.westAndCenterElement(canFly, flyingSpeedValue, 2, 2));
		canFly.addActionListener(e -> refreshFlyProperties());

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/health"),
				L10N.label("elementgui.beentity.health")));
		behaviourProps.add(healthValue);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/xp_amount"),
				L10N.label("elementgui.beentity.xp_amount")));
		behaviourProps.add(xpAmountOnDeath);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/drop"),
				L10N.label("elementgui.beentity.entity_drop")));
		behaviourProps.add(PanelUtils.totalCenterInPanel(entityDrop));

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/movement_speed"),
				L10N.label("elementgui.beentity.speed_value")));
		behaviourProps.add(speedValue);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/follow_range"),
				L10N.label("elementgui.beentity.follow_range_value")));
		behaviourProps.add(followRangeValue);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/attack_damage"),
				L10N.label("elementgui.beentity.attack_damage")));
		behaviourProps.add(attackDamage);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/pushable_behavior"),
				L10N.label("elementgui.beentity.pushable_behavior")));
		behaviourProps.add(PanelUtils.join(FlowLayout.LEFT, isPushable, new JEmptyBox(2, 2), isPushableByPiston));

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/is_immune_to_fire"),
				L10N.label("elementgui.beentity.is_immune_to_fire")));
		behaviourProps.add(isImmuneToFire);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/is_immune_to_fall_damage"),
				L10N.label("elementgui.beentity.is_immune_to_fall_damage")));
		behaviourProps.add(isImmuneToFallDamage);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("beentity/is_immune_to_drowning"),
				L10N.label("elementgui.beentity.is_immune_to_drowning")));
		behaviourProps.add(isImmuneToDrowning);

		behaviourProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_egg_options"),
				L10N.label("elementgui.beentity.spawn_egg_options")));
		behaviourProps.add(PanelUtils.join(FlowLayout.LEFT, 0, 0, hasSpawnEgg, new JEmptyBox(5, 2), spawnEggBaseColor,
				new JEmptyBox(2, 2), spawnEggDotColor));

		hasSpawnEgg.addActionListener(e -> refreshEggProperties());

		behaviourPanel.add("Center", PanelUtils.totalCenterInPanel(behaviourProps));

		JPanel aiTasks = new JPanel(new BorderLayout(0, 2));
		aiTasks.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK).getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.AI_TASK);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.AI_BUILDER);
			blocklyPanel.addChangeListener(
					changeEvent -> new Thread(() -> regenerateBlockAssemblies(true), "AITasksRegenerate").start());
		});
		if (!isEditingMode()) {
			blocklyPanel.setInitialXML("""
					<xml xmlns="https://developers.google.com/blockly/xml">
					<block type="aitasks_container" deletable="false" x="40" y="40"><next>
					<block type="attack_on_collide"><field name="speed">1.2</field><field name="longmemory">FALSE</field><field name="condition">null,null</field><next>
					<block type="wander"><field name="speed">1</field><field name="condition">null,null</field><next>
					<block type="attack_action"><field name="callhelp">FALSE</field><field name="condition">null,null</field><next>
					<block type="look_around"><field name="condition">null,null</field><next>
					<block type="swim_in_water"><field name="condition">null,null</field></block></next>
					</block></next></block></next></block></next></block></next></block></xml>""");
		}

		ComponentUtils.makeSection(aiTasks, L10N.t("elementgui.living_entity.ai_tasks"));
		BlocklyEditorToolbar blocklyEditorToolbar = new BlocklyEditorToolbar(mcreator, BlocklyEditorType.AI_TASK,
				blocklyPanel);
		blocklyEditorToolbar.setTemplateLibButtonWidth(155);
		aiTasks.add("North", blocklyEditorToolbar);
		aiTasks.add("Center", blocklyPanel);
		blocklyPanel.setPreferredSize(new Dimension(150, 150));
		aiTasks.add("South", compileNotesPanel);

		aiPanel.add("Center", aiTasks);

		JPanel spawningProps = new JPanel(new GridLayout(4, 2, 30, 2));
		spawningProps.setOpaque(false);

		spawningProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/enable_spawning"),
				L10N.label("elementgui.living_entity.enable_mob_spawning")));
		spawningProps.add(spawnNaturally);
		spawnNaturally.addActionListener(e -> refreshSpawnProperties());

		spawningProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_type"),
				L10N.label("elementgui.beentity.spawn_type")));
		spawningProps.add(populationControl);

		spawningProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_group_size"),
				L10N.label("elementgui.living_entity.spawn_group_size")));
		spawningProps.add(entityHerd);

		spawningProps.add(HelpUtils.wrapWithHelpButton(this.withEntry("entity/spawn_weight"),
				L10N.label("elementgui.living_entity.spawn_weight")));
		spawningProps.add(spawningProbability);

		spawningPanel.add("Center", PanelUtils.totalCenterInPanel(spawningProps));

		addPage(L10N.t("elementgui.living_entity.page_visual"), visualPanel).validate(modelTexture)
				.validate(entityName);
		addPage(L10N.t("elementgui.living_entity.page_behaviour"), behaviourPanel);
		addPage(L10N.t("elementgui.living_entity.page_ai_and_goals"), aiPanel).lazyValidate(
				BlocklyAggregatedValidationResult.blocklyValidator(this,
						compileNote -> "Entity AI builder: " + compileNote));
		addPage(L10N.t("elementgui.living_entity.page_spawning"), spawningPanel);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			entityName.setText(readableNameFromModElement);
			isSummonable.setSelected(true);
			isPushable.setSelected(true);
			isPushableByPiston.setSelected(true);
			hasSpawnEgg.setSelected(true);
		}

		refreshEggProperties();
		refreshSpawnProperties();
		refreshFlyProperties();
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(entityModel, Arrays.asList(builtinmobmodels));
	}

	@Override public void addBlocklyChangedListener(IBlocklyPanelHolder.BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override public synchronized List<BlocklyCompileNote> regenerateBlockAssemblies(boolean jsEventTriggeredChange) {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.AI_TASK));

		BlocklyToJava blocklyToJava;
		try {
			blocklyToJava = new BlocklyToJava(mcreator.getWorkspace(), this.modElement, BlocklyEditorType.AI_TASK,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			TestUtil.failIfTestingEnvironment();
			return List.of(); // should not be possible to happen here
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		SwingUtilities.invokeLater(() -> compileNotesPanel.updateCompileNotes(compileNotesArrayList));

		blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel, jsEventTriggeredChange));

		return compileNotesArrayList;
	}

	private void refreshSpawnProperties() {
		boolean canSpawn = spawnNaturally.isSelected();

		entityHerd.setEnabled(canSpawn);
		populationControl.setEnabled(canSpawn);
		spawningProbability.setEnabled(canSpawn);
	}

	private void refreshEggProperties() {
		boolean isSelected = hasSpawnEgg.isSelected();

		spawnEggBaseColor.setEnabled(isSelected);
		spawnEggDotColor.setEnabled(isSelected);
	}

	private void refreshFlyProperties() {
		flyingSpeedValue.setEnabled(canFly.isSelected());
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

	@Override protected void openInEditingMode(BEEntity entity) {
		entityName.setText(entity.entityName);

		Model model = entity.getEntityModel();
		if (model != null)
			entityModel.setSelectedItem(model);

		modelTexture.setTextureFromTextureName(entity.modelTexture);
		collisionBoxHeight.setValue(entity.collisionBoxHeight);
		collisionBoxWidth.setValue(entity.collisionBoxWidth);
		entityBehaviourType.setSelectedItem(entity.entityBehaviourType);
		isSummonable.setSelected(entity.isSummonable);
		xpAmountOnDeath.setValue(entity.xpAmountOnDeath);
		entityDrop.setBlock(entity.entityDrop);
		healthValue.setValue(entity.healthValue);
		attackDamage.setValue(entity.attackDamage);
		speedValue.setValue(entity.speedValue);
		waterEntity.setSelected(entity.waterEntity);
		canFly.setSelected(entity.canFly);
		flyingSpeedValue.setValue(entity.flyingSpeedValue);
		followRangeValue.setValue(entity.followRangeValue);
		isImmuneToDrowning.setSelected(entity.isImmuneToDrowning);
		isImmuneToFire.setSelected(entity.isImmuneToFire);
		isImmuneToFallDamage.setSelected(entity.isImmuneToFallDamage);
		isPushable.setSelected(entity.isPushable);
		isPushableByPiston.setSelected(entity.isPushableByPiston);

		spawnNaturally.setSelected(entity.spawnNaturally);
		populationControl.setSelectedItem(entity.populationControl);
		spawningProbability.setValue(entity.spawningProbability);
		entityHerd.setMinValue(entity.minHerdSize);
		entityHerd.setMaxValue(entity.maxHerdSize);
		hasSpawnEgg.setSelected(entity.hasSpawnEgg);
		spawnEggBaseColor.setColor(entity.spawnEggBaseColor);
		spawnEggDotColor.setColor(entity.spawnEggDotColor);
		blocklyPanel.setInitialXML(entity.aixml);

		refreshEggProperties();
		refreshSpawnProperties();
		refreshFlyProperties();
	}

	@Override public BEEntity getElementFromGUI() {
		BEEntity entity = new BEEntity(this.modElement);
		entity.entityName = entityName.getText();
		entity.modelName = Objects.requireNonNull(entityModel.getSelectedItem()).toString();
		entity.modelTexture = modelTexture.getTextureName();
		entity.collisionBoxHeight = (double) collisionBoxHeight.getValue();
		entity.collisionBoxWidth = (double) collisionBoxWidth.getValue();
		entity.entityBehaviourType = (String) entityBehaviourType.getSelectedItem();
		entity.isSummonable = isSummonable.isSelected();
		entity.xpAmountOnDeath = (int) xpAmountOnDeath.getValue();
		entity.entityDrop = entityDrop.getBlock();
		entity.healthValue = (int) healthValue.getValue();
		entity.attackDamage = (int) attackDamage.getValue();
		entity.speedValue = (double) speedValue.getValue();
		entity.waterEntity = waterEntity.isSelected();
		entity.canFly = canFly.isSelected();
		entity.flyingSpeedValue = (double) flyingSpeedValue.getValue();
		entity.followRangeValue = (int) followRangeValue.getValue();
		entity.isImmuneToDrowning = isImmuneToDrowning.isSelected();
		entity.isImmuneToFire = isImmuneToFire.isSelected();
		entity.isImmuneToFallDamage = isImmuneToFallDamage.isSelected();
		entity.isPushable = isPushable.isSelected();
		entity.isPushableByPiston = isPushableByPiston.isSelected();
		entity.spawnNaturally = spawnNaturally.isSelected();
		entity.populationControl = (String) populationControl.getSelectedItem();
		entity.spawningProbability = (int) spawningProbability.getValue();
		entity.minHerdSize = entityHerd.getIntMinValue();
		entity.maxHerdSize = entityHerd.getIntMaxValue();
		entity.hasSpawnEgg = hasSpawnEgg.isSelected();
		entity.spawnEggBaseColor = spawnEggBaseColor.getColor();
		entity.spawnEggDotColor = spawnEggDotColor.getColor();
		entity.aixml = blocklyPanel.getXML();
		return entity;
	}

	@Override @Nullable public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-entity");
	}
}
