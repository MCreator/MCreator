/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.bedrock.BEBiome;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.BEBiomeListField;
import net.mcreator.ui.minecraft.BEBiomeTagsListField;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.modgui.util.ComponentFromAnnotation;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class BEBiomeGUI extends ModElementGUI<BEBiome> {

	private MCItemHolder topMaterial;
	private MCItemHolder midMaterial;
	private MCItemHolder foundationMaterial;
	private MCItemHolder seaFloorMaterial;
	private MCItemHolder seaMaterial;

	private final JSpinner seaFloorDepth = ComponentFromAnnotation.spinner(BEBiome.class, "seaFloorDepth");
	private final JSpinner temperature = ComponentFromAnnotation.spinner(BEBiome.class, "temperature");
	private final JSpinner downfall = ComponentFromAnnotation.spinner(BEBiome.class, "downfall");
	private final JSpinner replacementAmount = ComponentFromAnnotation.spinner(BEBiome.class, "replacementAmount");
	private final JSpinner replacementNoiseFrequencyScale = ComponentFromAnnotation.spinner(BEBiome.class,
			"replacementNoiseFrequencyScale");
	private final JSpinner particleDensity = ComponentFromAnnotation.spinner(BEBiome.class, "particleDensity");

	private final JMinMaxSpinner snowAccumulation = ComponentFromAnnotation.minMaxSpinner(BEBiome.class, "minSnow",
			"maxSnow").allowEqualValues();

	private final JCheckBox spawnParticles = L10N.checkbox("elementgui.common.enable");

	private final JColor airColor = new JColor(mcreator, true, false);
	private final JColor fogColor = new JColor(mcreator, true, false);
	private final JColor grassColor = new JColor(mcreator, true, false);
	private final JColor foliageColor = new JColor(mcreator, true, false);
	private final JColor waterColor = new JColor(mcreator, true, false);
	private final JColor waterFogColor = new JColor(mcreator, true, false);

	private final JComboBox<String> noiseType = ComponentFromAnnotation.options(BEBiome.class, "noiseType");
	private final JComboBox<String> villageType = ComponentFromAnnotation.options(BEBiome.class, "villageType");
	private final JComboBox<String> particleToSpawn = ComponentFromAnnotation.options(BEBiome.class, "particleToSpawn");

	private final BEBiomeTagsListField biomeTags = new BEBiomeTagsListField(mcreator);
	private final BEBiomeListField biomeReplacements = new BEBiomeListField(mcreator, false);

	private final SoundSelector ambientSound = new SoundSelector(mcreator);
	private final SoundSelector moodSound = new SoundSelector(mcreator);
	private final SoundSelector additionsSound = new SoundSelector(mcreator);
	private final SoundSelector music = new SoundSelector(mcreator);

	private final ValidationGroup page2group = new ValidationGroup();

	public BEBiomeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		topMaterial = new MCItemHolder(mcreator, ElementUtil::loadBlocks).requireValue(
				"elementgui.biome.error_biome_needs_ground_block");
		midMaterial = new MCItemHolder(mcreator, ElementUtil::loadBlocks).requireValue(
				"elementgui.biome.error_bebiome_needs_middle_block");
		foundationMaterial = new MCItemHolder(mcreator, ElementUtil::loadBlocks).requireValue(
				"elementgui.biome.error_biome_needs_underground_block");
		seaFloorMaterial = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		seaMaterial = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		JPanel propertiesPanel = new JPanel(new GridLayout(9, 2, 40, 2));
		propertiesPanel.setOpaque(false);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/raining_possibility"),
				L10N.label("elementgui.biome.raining_possibility")));
		propertiesPanel.add(downfall);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/temperature"),
				L10N.label("elementgui.biome.temperature")));
		propertiesPanel.add(temperature);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/snow_accumulation"),
				L10N.label("elementgui.bebiome.snow_accumulation")));
		propertiesPanel.add(snowAccumulation);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/air_color"),
				L10N.label("elementgui.biome.air_color")));
		propertiesPanel.add(airColor);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/fog_color"),
				L10N.label("elementgui.biome.fog_color")));
		propertiesPanel.add(fogColor);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/grass_color"),
				L10N.label("elementgui.biome.grass_color")));
		propertiesPanel.add(grassColor);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/foliage_color"),
				L10N.label("elementgui.biome.foliage_color")));
		propertiesPanel.add(foliageColor);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_color"),
				L10N.label("elementgui.biome.water_color")));
		propertiesPanel.add(waterColor);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_fog_color"),
				L10N.label("elementgui.biome.water_fog_color")));
		propertiesPanel.add(waterFogColor);

		JPanel materialsPanel = new JPanel(new GridLayout(12, 2, 25, 2));
		materialsPanel.setOpaque(false);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/ground_block"),
				L10N.label("elementgui.bebiome.ground_block")));
		materialsPanel.add(PanelUtils.join(topMaterial));

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/middle_block"),
				L10N.label("elementgui.bebiome.middle_block")));
		materialsPanel.add(PanelUtils.join(midMaterial));

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/underground_block"),
				L10N.label("elementgui.bebiome.underground_block")));
		materialsPanel.add(PanelUtils.join(foundationMaterial));

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/underwater_block"),
				L10N.label("elementgui.bebiome.underwater_block")));
		materialsPanel.add(PanelUtils.join(seaFloorMaterial));

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/ocean_block"),
				L10N.label("elementgui.bebiome.ocean_block")));
		materialsPanel.add(PanelUtils.join(seaMaterial));

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/sea_floor_depth"),
				L10N.label("elementgui.bebiome.ocean_floor_depth")));
		materialsPanel.add(seaFloorDepth);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/noise_type"),
				L10N.label("elementgui.bebiome.terrain_noise_type")));
		materialsPanel.add(noiseType);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/village_type"),
				L10N.label("elementgui.bebiome.village_type")));
		materialsPanel.add(villageType);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/biome_tags"),
				L10N.label("elementgui.bebiome.biome_tags")));
		materialsPanel.add(biomeTags);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/biome_replacements"),
				L10N.label("elementgui.bebiome.biome_replacements")));
		materialsPanel.add(biomeReplacements);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/biome_replacement_amount"),
				L10N.label("elementgui.bebiome.replacement_amount")));
		materialsPanel.add(replacementAmount);

		materialsPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/biome_replacement_freq_scale"),
				L10N.label("elementgui.bebiome.replacement_noise_freq_scale")));
		materialsPanel.add(replacementNoiseFrequencyScale);

		JPanel effectsPane = new JPanel(new BorderLayout());

		JPanel sounds = new JPanel(new GridLayout(4, 2, 25, 2));

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

		ComponentUtils.makeSection(sounds, L10N.t("elementgui.biome.sounds"));

		sounds.setOpaque(false);

		JPanel particles = new JPanel(new GridLayout(3, 2, 25, 2));

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/spawn_particle"),
				L10N.label("elementgui.biome.enable_particles")));
		particles.add(spawnParticles);

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/particle_type"),
				L10N.label("elementgui.biome.particle_type")));
		particles.add(particleToSpawn);
		particleToSpawn.setPreferredSize(new Dimension(100, 40));

		particles.add(HelpUtils.wrapWithHelpButton(this.withEntry("bebiome/particle_density"),
				L10N.label("elementgui.bebiome.particle_density")));
		particles.add(particleDensity);
		particleDensity.setPreferredSize(new Dimension(100, 40));

		particles.setOpaque(false);

		ComponentUtils.makeSection(particles, L10N.t("elementgui.biome.particles"));

		spawnParticles.setOpaque(false);
		spawnParticles.addActionListener(_ -> updateParticleParameters());

		updateParticleParameters();

		effectsPane.setOpaque(false);
		effectsPane.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(sounds, PanelUtils.pullElementUp(particles))));

		biomeReplacements.setValidator(new ItemListFieldValidator(biomeReplacements,
				L10N.t("elementgui.bebiome.error_must_have_replacement")));

		page2group.addValidationElement(topMaterial);
		page2group.addValidationElement(midMaterial);
		page2group.addValidationElement(foundationMaterial);
		page2group.addValidationElement(biomeReplacements);

		if (!isEditingMode()) {
			topMaterial.setBlock(new MItemBlock(mcreator.getWorkspace(), "Blocks.GRASS"));
			midMaterial.setBlock(new MItemBlock(mcreator.getWorkspace(), "Blocks.DIRT#0"));
			foundationMaterial.setBlock(new MItemBlock(mcreator.getWorkspace(), "Blocks.STONE#0"));
		}

		addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(propertiesPanel));
		addPage(L10N.t("elementgui.biome.biome_generation"), PanelUtils.totalCenterInPanel(materialsPanel)).validate(
				page2group);
		addPage(L10N.t("elementgui.biome.effects"), effectsPane);
	}

	private void updateParticleParameters() {
		if (spawnParticles.isSelected()) {
			particleToSpawn.setEnabled(true);
			particleDensity.setEnabled(true);
		} else {
			particleToSpawn.setEnabled(false);
			particleDensity.setEnabled(false);
		}
	}

	@Override protected void openInEditingMode(BEBiome biome) {
		topMaterial.setBlock(biome.topMaterial);
		midMaterial.setBlock(biome.midMaterial);
		foundationMaterial.setBlock(biome.foundationMaterial);
		seaFloorMaterial.setBlock(biome.seaFloorMaterial);
		seaMaterial.setBlock(biome.seaMaterial);

		airColor.setColor(biome.airColor);
		fogColor.setColor(biome.fogColor);
		grassColor.setColor(biome.grassColor);
		foliageColor.setColor(biome.foliageColor);
		waterColor.setColor(biome.waterColor);
		waterFogColor.setColor(biome.waterFogColor);

		temperature.setValue(biome.temperature);
		downfall.setValue(biome.downfall);
		snowAccumulation.setMinValue(biome.minSnow);
		snowAccumulation.setMaxValue(biome.maxSnow);
		seaFloorDepth.setValue(biome.seaFloorDepth);
		replacementAmount.setValue(biome.replacementAmount);
		replacementNoiseFrequencyScale.setValue(biome.replacementNoiseFrequencyScale);
		particleDensity.setValue(biome.particleDensity);

		spawnParticles.setSelected(biome.spawnParticles);

		noiseType.setSelectedItem(biome.noiseType);
		villageType.setSelectedItem(biome.villageType);
		particleToSpawn.setSelectedItem(biome.particleToSpawn);

		biomeTags.setListElements(biome.biomeTags);
		biomeReplacements.setListElements(biome.biomeReplacements);

		ambientSound.setSound(biome.ambientSound);
		additionsSound.setSound(biome.additionsSound);
		music.setSound(biome.music);
		moodSound.setSound(biome.moodSound);

		updateParticleParameters();
	}

	@Override public BEBiome getElementFromGUI() {
		BEBiome biome = new BEBiome(modElement);

		biome.topMaterial = topMaterial.getBlock();
		biome.midMaterial = midMaterial.getBlock();
		biome.foundationMaterial = foundationMaterial.getBlock();
		biome.seaFloorMaterial = seaFloorMaterial.getBlock();
		biome.seaMaterial = seaMaterial.getBlock();

		biome.airColor = airColor.getColor();
		biome.fogColor = fogColor.getColor();
		biome.grassColor = grassColor.getColor();
		biome.foliageColor = foliageColor.getColor();
		biome.waterColor = waterColor.getColor();
		biome.waterFogColor = waterFogColor.getColor();

		biome.temperature = (double) temperature.getValue();
		biome.downfall = (double) downfall.getValue();
		biome.minSnow = snowAccumulation.getMinValue();
		biome.maxSnow = snowAccumulation.getMaxValue();
		biome.seaFloorDepth = (int) seaFloorDepth.getValue();
		biome.replacementAmount = (double) replacementAmount.getValue();
		biome.replacementNoiseFrequencyScale = (double) replacementNoiseFrequencyScale.getValue();
		biome.particleDensity = (double) particleDensity.getValue();

		biome.spawnParticles = spawnParticles.isSelected();

		biome.noiseType = (String) noiseType.getSelectedItem();
		biome.villageType = (String) villageType.getSelectedItem();
		biome.particleToSpawn = (String) particleToSpawn.getSelectedItem();

		biome.biomeTags = biomeTags.getListElements();
		biome.biomeReplacements = biomeReplacements.getListElements();

		biome.ambientSound = ambientSound.getSound();
		biome.additionsSound = additionsSound.getSound();
		biome.music = music.getSound();
		biome.moodSound = moodSound.getSound();

		return biome;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-biome");
	}

}
