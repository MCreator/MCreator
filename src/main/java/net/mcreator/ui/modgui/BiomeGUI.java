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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.DefaultFeaturesListField;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.spawntypes.JSpawnEntriesList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class BiomeGUI extends ModElementGUI<Biome> {

	private final VTextField name = new VTextField(20);

	private final JSpinner treesPerChunk = new JSpinner(new SpinnerNumberModel(1, 0, 256, 1));

	private final JSpinner rainingPossibility = new JSpinner(new SpinnerNumberModel(0.5, 0, 1, 0.1));
	private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(0.5, -1.0, 2.0, 0.1));

	private final JMinMaxSpinner genTemperature = new JMinMaxSpinner(-0.5, 0.5, -2.0, 2.0, 0.0001);
	private final JMinMaxSpinner genHumidity = new JMinMaxSpinner(-0.5, 0.5, -2.0, 2.0, 0.0001);
	private final JMinMaxSpinner genContinentalness = new JMinMaxSpinner(0.3, 1.0, -2.0, 2.0, 0.0001);
	private final JMinMaxSpinner genErosion = new JMinMaxSpinner(-0.5, 0.5, -2.0, 2.0, 0.0001);
	private final JMinMaxSpinner genWeirdness = new JMinMaxSpinner(-1, 1, -2.0, 2.0, 0.0001);

	private final JRadioButton customTrees = L10N.radiobutton("elementgui.biome.custom_trees");
	private final JRadioButton vanillaTrees = L10N.radiobutton("elementgui.biome.vanilla_trees");

	private final JCheckBox spawnBiome = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnInCaves = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnBiomeNether = L10N.checkbox("elementgui.common.enable");

	private JSpawnEntriesList spawnEntries;

	private MCItemHolder groundBlock;
	private MCItemHolder undergroundBlock;
	private MCItemHolder underwaterBlock;

	private final JSpinner minHeight = new JSpinner(new SpinnerNumberModel(7, 0, 32, 1));
	private MCItemHolder treeStem;
	private MCItemHolder treeBranch;
	private MCItemHolder treeVines;
	private MCItemHolder treeFruits;

	private final JColor airColor = new JColor(mcreator, true, false);
	private final JColor fogColor = new JColor(mcreator, true, false);
	private final JColor grassColor = new JColor(mcreator, true, false);
	private final JColor foliageColor = new JColor(mcreator, true, false);
	private final JColor waterColor = new JColor(mcreator, true, false);
	private final JColor waterFogColor = new JColor(mcreator, true, false);

	private final SoundSelector ambientSound = new SoundSelector(mcreator);
	private final SoundSelector moodSound = new SoundSelector(mcreator);
	private final JSpinner moodSoundDelay = new JSpinner(new SpinnerNumberModel(6000, 1, 30000, 1));
	private final SoundSelector additionsSound = new SoundSelector(mcreator);
	private final SoundSelector music = new SoundSelector(mcreator);
	private final JCheckBox spawnParticle = L10N.checkbox("elementgui.common.enable");
	private final DataListComboBox particleToSpawn = new DataListComboBox(mcreator);
	private final JSpinner particlesProbability = new JSpinner(new SpinnerNumberModel(0.5, 0, 100, 0.1));

	private final JComboBox<String> vanillaTreeType = new JComboBox<>(
			new String[] { "Default", "Big trees", "Birch trees", "Savanna trees", "Mega pine trees",
					"Mega spruce trees" });

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page2group = new ValidationGroup();
	private final ValidationGroup page3group = new ValidationGroup();

	private final DefaultFeaturesListField defaultFeatures = new DefaultFeaturesListField(mcreator);

	private final JLabel coverageEstimate = new JLabel();

	public BiomeGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		groundBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		undergroundBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		underwaterBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeVines = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeStem = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeBranch = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeFruits = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		ButtonGroup bg = new ButtonGroup();
		bg.add(customTrees);
		bg.add(vanillaTrees);

		vanillaTrees.setSelected(true);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		name.setOpaque(true);
		airColor.setOpaque(false);
		fogColor.setOpaque(false);
		grassColor.setOpaque(false);
		foliageColor.setOpaque(false);
		waterColor.setOpaque(false);
		waterFogColor.setOpaque(false);

		spawnEntries = new JSpawnEntriesList(mcreator, this);

		JPanel sbbp2 = new JPanel(new GridLayout(13, 2, 4, 2));

		pane2.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(sbbp2, PanelUtils.pullElementUp(pane5), 20, 20)));

		sbbp2.setOpaque(false);
		pane2.setOpaque(false);
		pane5.setOpaque(false);

		spawnBiome.setSelected(true);
		spawnBiome.setOpaque(false);
		spawnInCaves.setOpaque(false);

		spawnBiomeNether.setOpaque(false);

		coverageEstimate.setFont(coverageEstimate.getFont().deriveFont(15.0f));

		JPanel spawnproperties = new JPanel(new GridLayout(12, 2, 25, 2));
		spawnproperties.setOpaque(false);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/ground_block"),
				L10N.label("elementgui.biome.ground_block")));
		spawnproperties.add(PanelUtils.join(groundBlock));

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/underground_block"),
				L10N.label("elementgui.biome.undeground_block")));
		spawnproperties.add(PanelUtils.join(undergroundBlock));

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/underwater_block"),
				L10N.label("elementgui.biome.underwater_block")));
		spawnproperties.add(PanelUtils.join(underwaterBlock));

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/coverage_estimate"),
				L10N.label("elementgui.biome.coverage_estimate")));
		spawnproperties.add(PanelUtils.centerInPanel(coverageEstimate));

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gen_temperature"),
				L10N.label("elementgui.biome.gen_temperature")));
		spawnproperties.add(genTemperature);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gen_humidity"),
				L10N.label("elementgui.biome.gen_humidity")));
		spawnproperties.add(genHumidity);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gen_continentalness"),
				L10N.label("elementgui.biome.gen_continentalness")));
		spawnproperties.add(genContinentalness);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gen_erosion"),
				L10N.label("elementgui.biome.gen_erosion")));
		spawnproperties.add(genErosion);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gen_weirdness"),
				L10N.label("elementgui.biome.gen_weirdness")));
		spawnproperties.add(genWeirdness);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_overworld"),
				L10N.label("elementgui.biome.generate_overworld")));
		spawnproperties.add(spawnBiome);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_overworld_caves"),
				L10N.label("elementgui.biome.generate_overworld_caves")));
		spawnproperties.add(spawnInCaves);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_nether"),
				L10N.label("elementgui.biome.generate_nether")));
		spawnproperties.add(spawnBiomeNether);

		genTemperature.addChangeListener(e -> estimateGenCoverage());
		genHumidity.addChangeListener(e -> estimateGenCoverage());
		genContinentalness.addChangeListener(e -> estimateGenCoverage());
		genErosion.addChangeListener(e -> estimateGenCoverage());
		genWeirdness.addChangeListener(e -> estimateGenCoverage());

		pane5.add("Center", PanelUtils.totalCenterInPanel(spawnproperties));

		JPanel sbbp3 = new JPanel(new GridLayout(1, 2, 0, 0));
		sbbp3.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.biome.vanilla_features"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/default_features"),
				L10N.label("elementgui.biome.default_features")));
		defaultFeatures.setPreferredSize(new Dimension(340, 36));
		sbbp3.add(defaultFeatures);

		sbbp3.setOpaque(false);
		pane3.setOpaque(false);

		JPanel sbbp5 = new JPanel(new GridLayout(8, 2, 0, 2));
		sbbp5.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.biome.custom_tree_properties"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		sbbp5.setOpaque(false);

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/trees_per_chunk"),
				L10N.label("elementgui.biome.trees_per_chunk")));
		sbbp5.add(treesPerChunk);

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/tree_shape"),
				L10N.label("elementgui.biome.tree_shape")));
		sbbp5.add(vanillaTreeType);

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/tree_definition"),
				L10N.label("elementgui.biome.tree_definition")));
		sbbp5.add(PanelUtils.join(FlowLayout.LEFT, vanillaTrees, customTrees));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/minimal_tree_height"),
				L10N.label("elementgui.biome.minimal_tree_height")));
		sbbp5.add(minHeight);

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/stem_block"),
				L10N.label("elementgui.biome.stem_block")));
		sbbp5.add(PanelUtils.join(treeStem));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/branch_block"),
				L10N.label("elementgui.biome.branch_block")));
		sbbp5.add(PanelUtils.join(treeBranch));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/vines_block"),
				L10N.label("elementgui.biome.vines_block")));
		sbbp5.add(PanelUtils.join(treeVines));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/fruits_block"),
				L10N.label("elementgui.biome.fruits_block")));
		sbbp5.add(PanelUtils.join(treeFruits));

		pane3.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(sbbp3, sbbp5)));

		JPanel sbbp4 = new JPanel(new GridLayout(9, 2, 35, 2));

		name.setPreferredSize(new Dimension(350, 36));

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/name"), L10N.label("elementgui.biome.name")));
		sbbp4.add(name);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/raining_possibility"),
				L10N.label("elementgui.biome.raining_possibility")));
		sbbp4.add(rainingPossibility);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/temperature"),
				L10N.label("elementgui.biome.temperature")));
		sbbp4.add(temperature);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/air_color"),
				L10N.label("elementgui.biome.air_color")));
		sbbp4.add(airColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/fog_color"),
				L10N.label("elementgui.biome.fog_color")));
		sbbp4.add(fogColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/grass_color"),
				L10N.label("elementgui.biome.grass_color")));
		sbbp4.add(grassColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/foliage_color"),
				L10N.label("elementgui.biome.foliage_color")));
		sbbp4.add(foliageColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_color"),
				L10N.label("elementgui.biome.water_color")));
		sbbp4.add(waterColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_fog_color"),
				L10N.label("elementgui.biome.water_fog_color")));
		sbbp4.add(waterFogColor);

		customTrees.addActionListener(event -> updateBiomeTreesForm());
		vanillaTrees.addActionListener(event -> updateBiomeTreesForm());

		customTrees.setOpaque(false);
		vanillaTrees.setOpaque(false);
		minHeight.setOpaque(false);

		sbbp4.setOpaque(false);
		pane4.setOpaque(false);

		pane4.add("Center", PanelUtils.totalCenterInPanel(sbbp4));

		JPanel pane1 = new JPanel(new GridLayout());

		JComponent component = PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/spawn_entities"),
						L10N.label("elementgui.biome.spawn_entities")), spawnEntries);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane1.add(component);

		pane1.setOpaque(false);

		JPanel effectsPane = new JPanel(new BorderLayout());

		JPanel sounds = new JPanel(new GridLayout(5, 2, 0, 2));

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/music"), L10N.label("elementgui.biome.music")));
		sounds.add(music);

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/ambient_sound"),
				L10N.label("elementgui.biome.ambient_sound")));
		sounds.add(ambientSound);

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/additions_sound"),
				L10N.label("elementgui.biome.additions_sound")));
		sounds.add(additionsSound);

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/mood_sound"),
				L10N.label("elementgui.biome.mood_sound")));
		sounds.add(moodSound);

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/mood_sound_delay"),
				L10N.label("elementgui.biome.mood_sound_delay")));
		sounds.add(moodSoundDelay);

		sounds.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.biome.sounds"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		sounds.setOpaque(false);

		JPanel particles = new JPanel(new GridLayout(3, 2, 0, 2));

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/spawn_particle"),
				L10N.label("elementgui.biome.enable_particles")));
		particles.add(spawnParticle);

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/particle_type"),
				L10N.label("elementgui.biome.particle_type")));
		particles.add(particleToSpawn);

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/particle_probability"),
				L10N.label("elementgui.biome.particle_probability")));
		particles.add(particlesProbability);

		particles.setOpaque(false);

		particles.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.biome.particles"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		spawnParticle.setOpaque(false);
		spawnParticle.addActionListener(event -> updateParticleParameters());

		effectsPane.setOpaque(false);
		effectsPane.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(sounds, PanelUtils.pullElementUp(particles))));

		page1group.addValidationElement(name);
		page2group.addValidationElement(groundBlock);
		page2group.addValidationElement(undergroundBlock);
		page3group.addValidationElement(treeStem);
		page3group.addValidationElement(treeBranch);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.biome.needs_name")));
		groundBlock.setValidator(new MCItemHolderValidator(groundBlock));
		undergroundBlock.setValidator(new MCItemHolderValidator(undergroundBlock));
		treeStem.setValidator(new MCItemHolderValidator(treeStem, customTrees));
		treeBranch.setValidator(new MCItemHolderValidator(treeBranch, customTrees));

		addPage(L10N.t("elementgui.biome.general_properties"), pane4);
		addPage(L10N.t("elementgui.biome.biome_generation"), pane5);
		addPage(L10N.t("elementgui.biome.features"), pane3);
		addPage(L10N.t("elementgui.biome.entity_spawning"), pane1, false);
		addPage(L10N.t("elementgui.biome.effects"), effectsPane);

		updateBiomeTreesForm();
		updateParticleParameters();

		estimateGenCoverage();

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);

			defaultFeatures.setListElements(Arrays.asList("Caves", "Ores", "FrozenTopLayer"));
		}
	}

	private void estimateGenCoverage() {
		double temperatureCoverage =
				(Math.min(genTemperature.getMaxValue(), 1.0) - Math.max(genTemperature.getMinValue(), -1.0)) / 2.0;
		double humidityCoverage =
				(Math.min(genHumidity.getMaxValue(), 1.0) - Math.max(genHumidity.getMinValue(), -1.0)) / 2.0;
		double continentalnessCoverage =
				(Math.min(genContinentalness.getMaxValue(), 1.0) - Math.max(genContinentalness.getMinValue(), -1.0))
						/ 2.0;
		double erosionCoverage =
				(Math.min(genErosion.getMaxValue(), 1.0) - Math.max(genErosion.getMinValue(), -1.0)) / 2.0;
		double weirdnessCoverage =
				(Math.min(genWeirdness.getMaxValue(), 1.0) - Math.max(genWeirdness.getMinValue(), -1.0)) / 2.0;

		double totalCoverage =
				temperatureCoverage * humidityCoverage * continentalnessCoverage * erosionCoverage * weirdnessCoverage
						* 100.0;

		if (totalCoverage >= 99.999) {
			totalCoverage = 99.999;
		} else if (totalCoverage <= 0.001) {
			totalCoverage = 0.001;
		}

		coverageEstimate.setText(new DecimalFormat("#0.000").format(totalCoverage) + " %");
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(particleToSpawn, ElementUtil.loadAllParticles(mcreator.getWorkspace()));
		spawnEntries.reloadDataLists();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		else if (page == 1)
			return new AggregatedValidationResult(page2group);
		else if (page == 2)
			return new AggregatedValidationResult(page3group);
		return new AggregatedValidationResult.PASS();
	}

	private void updateBiomeTreesForm() {
		if (customTrees.isSelected()) {
			minHeight.setEnabled(true);
			treeVines.setEnabled(true);
			treeStem.setEnabled(true);
			treeBranch.setEnabled(true);
			treeFruits.setEnabled(true);
		} else {
			minHeight.setEnabled(false);
			treeVines.setEnabled(false);
			treeStem.setEnabled(false);
			treeBranch.setEnabled(false);
			treeFruits.setEnabled(false);
		}
	}

	private void updateParticleParameters() {
		if (spawnParticle.isSelected()) {
			particleToSpawn.setEnabled(true);
			particlesProbability.setEnabled(true);
		} else {
			particleToSpawn.setEnabled(false);
			particlesProbability.setEnabled(false);
		}
	}

	@Override protected void afterGeneratableElementGenerated() {
		super.afterGeneratableElementGenerated();

		// if we are in editing mode, changes affecting dimensions using biome may be made
		if (isEditingMode()) {
			for (ModElement element : mcreator.getWorkspace().getModElements()) {
				if (element.getType() != ModElementType.DIMENSION)
					continue;

				// if this mod element is not locked and has procedures, we try to update dependencies
				// in this case, we (re)generate mod element code so dependencies get updated in the trigger code
				if (!element.isCodeLocked()) {
					GeneratableElement generatableElement = element.getGeneratableElement();
					if (generatableElement instanceof net.mcreator.element.types.Dimension dimension) {
						if (dimension.biomesInDimension.contains(
								new BiomeEntry(modElement.getWorkspace(), new DataListEntry.Custom(modElement)))) {
							mcreator.getGenerator().generateElement(generatableElement);
						}
					}
				}
			}
		}
	}

	@Override public void openInEditingMode(Biome biome) {
		name.setText(biome.name);
		groundBlock.setBlock(biome.groundBlock);
		undergroundBlock.setBlock(biome.undergroundBlock);
		underwaterBlock.setBlock(biome.underwaterBlock);
		treeVines.setBlock(biome.treeVines);
		treeStem.setBlock(biome.treeStem);
		treeBranch.setBlock(biome.treeBranch);
		treeFruits.setBlock(biome.treeFruits);

		if (biome.treeType == biome.TREES_CUSTOM) {
			vanillaTrees.setSelected(false);
			customTrees.setSelected(true);
		} else {
			vanillaTrees.setSelected(true);
			customTrees.setSelected(false);
		}

		ambientSound.setSound(biome.ambientSound);
		moodSound.setSound(biome.moodSound);
		moodSoundDelay.setValue(biome.moodSoundDelay);
		additionsSound.setSound(biome.additionsSound);
		music.setSound(biome.music);
		spawnParticle.setSelected(biome.spawnParticles);
		particleToSpawn.setSelectedItem(biome.particleToSpawn);
		particlesProbability.setValue(biome.particlesProbability);

		minHeight.setValue(biome.minHeight);
		airColor.setColor(biome.airColor);
		fogColor.setColor(biome.fogColor);
		grassColor.setColor(biome.grassColor);
		foliageColor.setColor(biome.foliageColor);
		waterColor.setColor(biome.waterColor);
		waterFogColor.setColor(biome.waterFogColor);
		treesPerChunk.setValue(biome.treesPerChunk);
		rainingPossibility.setValue(biome.rainingPossibility);
		spawnBiome.setSelected(biome.spawnBiome);
		spawnBiomeNether.setSelected(biome.spawnBiomeNether);
		spawnInCaves.setSelected(biome.spawnInCaves);

		temperature.setValue(biome.temperature);
		defaultFeatures.setListElements(biome.defaultFeatures);
		vanillaTreeType.setSelectedItem(biome.vanillaTreeType);
		spawnEntries.setEntries(biome.spawnEntries);

		genTemperature.setMinValue(biome.genTemperature.min);
		genTemperature.setMaxValue(biome.genTemperature.max);
		genHumidity.setMinValue(biome.genHumidity.min);
		genHumidity.setMaxValue(biome.genHumidity.max);
		genContinentalness.setMinValue(biome.genContinentalness.min);
		genContinentalness.setMaxValue(biome.genContinentalness.max);
		genErosion.setMinValue(biome.genErosion.min);
		genErosion.setMaxValue(biome.genErosion.max);
		genWeirdness.setMinValue(biome.genWeirdness.min);
		genWeirdness.setMaxValue(biome.genWeirdness.max);

		updateBiomeTreesForm();
		updateParticleParameters();

		estimateGenCoverage();
	}

	@Override public Biome getElementFromGUI() {
		Biome biome = new Biome(modElement);
		biome.name = name.getText();
		biome.groundBlock = groundBlock.getBlock();
		biome.undergroundBlock = undergroundBlock.getBlock();
		biome.underwaterBlock = underwaterBlock.getBlock();
		if (customTrees.isSelected())
			biome.treeType = biome.TREES_CUSTOM;
		else
			biome.treeType = biome.TREES_VANILLA;
		biome.airColor = airColor.getColor();
		biome.fogColor = fogColor.getColor();
		biome.grassColor = grassColor.getColor();
		biome.foliageColor = foliageColor.getColor();
		biome.waterColor = waterColor.getColor();
		biome.waterFogColor = waterFogColor.getColor();

		biome.ambientSound = ambientSound.getSound();
		biome.moodSound = moodSound.getSound();
		biome.moodSoundDelay = (int) moodSoundDelay.getValue();
		biome.additionsSound = additionsSound.getSound();
		biome.music = music.getSound();
		biome.spawnParticles = spawnParticle.isSelected();
		biome.particleToSpawn = new Particle(mcreator.getWorkspace(), particleToSpawn.getSelectedItem());
		biome.particlesProbability = (double) particlesProbability.getValue();

		biome.treesPerChunk = (int) treesPerChunk.getValue();
		biome.rainingPossibility = (double) rainingPossibility.getValue();
		biome.temperature = (double) temperature.getValue();
		biome.defaultFeatures = defaultFeatures.getListElements();
		biome.vanillaTreeType = (String) vanillaTreeType.getSelectedItem();
		biome.spawnEntries = spawnEntries.getEntries();
		biome.minHeight = (int) minHeight.getValue();
		biome.treeVines = treeVines.getBlock();
		biome.treeStem = treeStem.getBlock();
		biome.treeBranch = treeBranch.getBlock();
		biome.treeFruits = treeFruits.getBlock();
		biome.spawnBiome = spawnBiome.isSelected();
		biome.spawnBiomeNether = spawnBiomeNether.isSelected();
		biome.spawnInCaves = spawnInCaves.isSelected();

		biome.genTemperature = new Biome.ClimatePoint(genTemperature.getMinValue(), genTemperature.getMaxValue());
		biome.genHumidity = new Biome.ClimatePoint(genHumidity.getMinValue(), genHumidity.getMaxValue());
		biome.genContinentalness = new Biome.ClimatePoint(genContinentalness.getMinValue(),
				genContinentalness.getMaxValue());
		biome.genErosion = new Biome.ClimatePoint(genErosion.getMinValue(), genErosion.getMaxValue());
		biome.genWeirdness = new Biome.ClimatePoint(genWeirdness.getMinValue(), genWeirdness.getMaxValue());

		return biome;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-biome");
	}

}
