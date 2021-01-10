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

import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.minecraft.spawntypes.JSpawnEntriesList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

public class BiomeGUI extends ModElementGUI<Biome> {

	private final VTextField name = new VTextField(20);

	private final JSpinner treesPerChunk = new JSpinner(new SpinnerNumberModel(1, 0, 256, 1));
	private final JSpinner grassPerChunk = new JSpinner(new SpinnerNumberModel(4, 0, 256, 1));
	private final JSpinner seagrassPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner flowersPerChunk = new JSpinner(new SpinnerNumberModel(4, 0, 256, 1));
	private final JSpinner mushroomsPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner bigMushroomsChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner sandPatchesPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner gravelPatchesPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner reedsPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner cactiPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner rainingPossibility = new JSpinner(new SpinnerNumberModel(0.5, 0, 1, 0.1));
	private final JSpinner baseHeight = new JSpinner(new SpinnerNumberModel(0.1, -10, 10, 0.1));
	private final JSpinner heightVariation = new JSpinner(new SpinnerNumberModel(0.2, 0, 2, 0.1));
	private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(0.5, -1.0, 2.0, 0.1));

	private final JRadioButton customTrees = L10N.radiobutton("elementgui.biome.custom_trees");
	private final JRadioButton vanillaTrees = L10N.radiobutton("elementgui.biome.vanilla_trees");

	private final JCheckBox spawnBiome = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnStronghold = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnMineshaft = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnPillagerOutpost = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnWoodlandMansion = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnJungleTemple = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnDesertPyramid = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnIgloo = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnOceanMonument = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox spawnShipwreck = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> villageType = new JComboBox<>(
			new String[] { "none", "desert", "plains", "savanna", "snowy", "taiga" });
	private final JComboBox<String> oceanRuinType = new JComboBox<>(new String[] { "NONE", "COLD", "WARM" });

	private final JSpawnEntriesList spawnEntries = new JSpawnEntriesList(mcreator);

	private MCItemHolder groundBlock;
	private MCItemHolder undergroundBlock;

	private final JSpinner minHeight = new JSpinner(new SpinnerNumberModel(7, 0, 1000, 1));
	private final JSpinner maxWaterDepth = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
	private MCItemHolder treeVines;
	private MCItemHolder treeStem;
	private MCItemHolder treeBranch;
	private MCItemHolder treeFruits;

	private final JColor airColor = new JColor(mcreator, true);
	private final JColor grassColor = new JColor(mcreator, true);
	private final JColor foliageColor = new JColor(mcreator, true);
	private final JColor waterColor = new JColor(mcreator, true);
	private final JColor waterFogColor = new JColor(mcreator, true);

	private final SoundSelector ambientSound = new SoundSelector(mcreator);
	private final SoundSelector moodSound = new SoundSelector(mcreator);
	private final JSpinner moodSoundDelay = new JSpinner(new SpinnerNumberModel(6000, 1, 30000, 1));
	private final SoundSelector additionsSound = new SoundSelector(mcreator);
	private final SoundSelector music = new SoundSelector(mcreator);
	private final JCheckBox spawnParticle = L10N.checkbox("elementgui.common.enable");
	private final DataListComboBox particleToSpawn = new DataListComboBox(mcreator);
	private final JSpinner particlesProbability = new JSpinner(new SpinnerNumberModel(0.5, 0, 100, 0.1));

	private final JSpinner biomeWeight = new JSpinner(new SpinnerNumberModel(10, 0, 1024, 1));
	private final JComboBox<String> biomeType = new JComboBox<>(new String[] { "WARM", "DESERT", "COOL", "ICY" });

	private final JComboBox<String> biomeCategory = new JComboBox<>(
			new String[] { "NONE", "TAIGA", "EXTREME_HILLS", "JUNGLE", "MESA", "PLAINS", "SAVANNA", "ICY", "THEEND",
					"BEACH", "FOREST", "OCEAN", "DESERT", "RIVER", "SWAMP", "MUSHROOM", "NETHER" });

	private final JComboBox<String> vanillaTreeType = new JComboBox<>(
			new String[] { "Default", "Big trees", "Birch trees", "Savanna trees", "Mega pine trees",
					"Mega spruce trees" });

	private final DataListComboBox parent = new DataListComboBox(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	private final BiomeDictionaryTypeListField biomeDictionaryTypes = new BiomeDictionaryTypeListField(mcreator);
	private final DefaultFeaturesListField defaultFeatures = new DefaultFeaturesListField(mcreator);

	private final DataListEntry.Dummy noparent = new DataListEntry.Dummy("No parent");

	public BiomeGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		groundBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		undergroundBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeVines = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeStem = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeBranch = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeFruits = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		biomeType.setRenderer(new ItemTexturesComboBoxRenderer());

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
		grassColor.setOpaque(false);
		foliageColor.setOpaque(false);
		waterColor.setOpaque(false);
		waterFogColor.setOpaque(false);

		JPanel sbbp2 = new JPanel(new GridLayout(11, 2, 4, 2));

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_strongholds"),
				L10N.label("elementgui.biome.generate_strongholds")));
		sbbp2.add(spawnStronghold);
		spawnStronghold.setSelected(false);
		spawnStronghold.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_mineshafts"),
				L10N.label("elementgui.biome.generate_mineshafts")));
		sbbp2.add(spawnMineshaft);
		spawnMineshaft.setSelected(false);
		spawnMineshaft.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_pillager_outposts"),
				L10N.label("elementgui.biome.generate_pillager_outposts")));
		sbbp2.add(spawnPillagerOutpost);
		spawnPillagerOutpost.setSelected(false);
		spawnPillagerOutpost.setOpaque(false);

		sbbp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/village"), L10N.label("elementgui.biome.generate_village")));
		sbbp2.add(villageType);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_woodland_mansions"),
				L10N.label("elementgui.biome.generate_mansions")));
		sbbp2.add(spawnWoodlandMansion);
		spawnWoodlandMansion.setSelected(false);
		spawnWoodlandMansion.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_jungle_temples"),
				L10N.label("elementgui.biome.generate_jungle_temples")));
		sbbp2.add(spawnJungleTemple);
		spawnJungleTemple.setSelected(false);
		spawnJungleTemple.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_desert_pyramids"),
				L10N.label("elementgui.biome.generate_desert_pyramids")));
		sbbp2.add(spawnDesertPyramid);
		spawnDesertPyramid.setSelected(false);
		spawnDesertPyramid.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_igloo"),
				L10N.label("elementgui.biome.generate_igloos")));
		sbbp2.add(spawnIgloo);
		spawnIgloo.setSelected(false);
		spawnIgloo.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_ocean_monuments"),
				L10N.label("elementgui.biome.generate_monuments")));
		sbbp2.add(spawnOceanMonument);
		spawnOceanMonument.setSelected(false);
		spawnOceanMonument.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_shipwrecks"),
				L10N.label("elementgui.biome.generate_shipwrecks")));

		sbbp2.add(spawnShipwreck);
		spawnShipwreck.setSelected(false);
		spawnShipwreck.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_ocean_ruins"),
				L10N.label("elementgui.biome.generate_ocean_ruins")));
		sbbp2.add(oceanRuinType);

		oceanRuinType.setPreferredSize(new Dimension(200, 36));

		sbbp2.setOpaque(false);
		pane2.setOpaque(false);
		pane5.setOpaque(false);

		JPanel spawnproperties = new JPanel(new GridLayout(10, 2, 5, 2));
		spawnproperties.setOpaque(false);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_overworld"),
				L10N.label("elementgui.biome.generate_overworld")));
		spawnproperties.add(spawnBiome);
		spawnBiome.setSelected(true);
		spawnBiome.setOpaque(false);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/weight"), L10N.label("elementgui.biome.weight")));
		spawnproperties.add(biomeWeight);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/temperature"), L10N.label("elementgui.biome.temperature")));
		spawnproperties.add(temperature);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/raining_possibility"),
				L10N.label("elementgui.biome.raining_possibility")));
		spawnproperties.add(rainingPossibility);

		spawnproperties
				.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/type"), L10N.label("elementgui.biome.type")));
		spawnproperties.add(biomeType);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/category"), L10N.label("elementgui.biome.category")));
		spawnproperties.add(biomeCategory);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/parent"), L10N.label("elementgui.biome.parent")));
		spawnproperties.add(parent);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/dictionary"), L10N.label("elementgui.biome.dictionnary")));
		spawnproperties.add(biomeDictionaryTypes);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/base_height"), L10N.label("elementgui.biome.height")));
		spawnproperties.add(baseHeight);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/height_variation"),
				L10N.label("elementgui.biome.height_variation")));
		spawnproperties.add(heightVariation);

		pane5.add("Center", PanelUtils.totalCenterInPanel(spawnproperties));
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel sbbp3 = new JPanel(new GridLayout(11, 2, 10, 2));

		defaultFeatures.setPreferredSize(new Dimension(340, 36));

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/trees_per_chunk"),
				L10N.label("elementgui.biome.trees_per_chunk")));
		sbbp3.add(treesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/grass_per_chunk"),
				L10N.label("elementgui.biome.grass_per_chunk")));
		sbbp3.add(grassPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/seagrass_per_chunk"),
				L10N.label("elementgui.biome.seagrass_per_chunk")));
		sbbp3.add(seagrassPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/flowers_per_chunk"),
				L10N.label("elementgui.biome.flowers_per_chunk")));
		sbbp3.add(flowersPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/mushrooms_per_chunk"),
				L10N.label("elementgui.biome.mushrooms_per_chunk")));
		sbbp3.add(mushroomsPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/big_mushrooms_per_chunk"),
				L10N.label("elementgui.biome.big_mushrooms_per_chunk")));
		sbbp3.add(bigMushroomsChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/sand_patches_per_chunk"),
				L10N.label("elementgui.biome.sand_patches_per_chunk")));
		sbbp3.add(sandPatchesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gravel_patches_per_chunk"),
				L10N.label("elementgui.biome.gravel_patches_per_chunk")));
		sbbp3.add(gravelPatchesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/reeds_per_chunk"),
				L10N.label("elementgui.biome.reeds_per_chunk")));
		sbbp3.add(reedsPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/cacti_per_chunk"),
				L10N.label("elementgui.biome.cacti_per_chunk")));
		sbbp3.add(cactiPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/default_features"),
				L10N.label("elementgui.biome.default_features")));
		sbbp3.add(defaultFeatures);

		sbbp3.setOpaque(false);
		pane3.setOpaque(false);

		temperature.addChangeListener(new ChangeListener() {
			private double oldVal = (double) temperature.getValue();

			@Override public void stateChanged(ChangeEvent e) {
				double val = (double) temperature.getValue();
				if (val > 0.1 && val < 0.2) {
					double dv = oldVal - val;
					if (Math.abs(dv) > 0.02) {
						if (val < 0.15)
							SwingUtilities.invokeLater(() -> temperature.setValue(0.1));
						else
							SwingUtilities.invokeLater(() -> temperature.setValue(0.2));
					} else {
						if (dv < 0)
							SwingUtilities.invokeLater(() -> temperature.setValue(0.2));
						else
							SwingUtilities.invokeLater(() -> temperature.setValue(0.1));
					}
				}
				oldVal = val;
			}
		});

		pane3.add("Center", PanelUtils.totalCenterInPanel(sbbp3));

		JPanel sbbp4 = new JPanel(new GridLayout(7, 2, 0, 2));
		JPanel sbbp4n = new JPanel(new GridLayout(2, 2, 0, 2));

		sbbp4n.setOpaque(false);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/name"), L10N.label("elementgui.biome.name")));
		sbbp4.add(name);

		sbbp4n.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/ground_block"), L10N.label("elementgui.biome.ground_block"),
						new Color(206, 109, 109).brighter()));
		sbbp4n.add(PanelUtils.join(groundBlock));

		sbbp4n.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/underground_block"),
				L10N.label("elementgui.biome.undeground_block"), new Color(179, 94, 26).brighter()));
		sbbp4n.add(PanelUtils.join(undergroundBlock));

		sbbp4.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/air_color"), L10N.label("elementgui.biome.air_color")));
		sbbp4.add(airColor);

		sbbp4.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/grass_color"), L10N.label("elementgui.biome.grass_color")));
		sbbp4.add(grassColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/foliage_color"),
				L10N.label("elementgui.biome.foliage_color")));
		sbbp4.add(foliageColor);

		sbbp4.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/water_color"), L10N.label("elementgui.biome.water_color")));
		sbbp4.add(waterColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_fog_color"),
				L10N.label("elementgui.biome.water_fog_color")));
		sbbp4.add(waterFogColor);

		sbbp4.add(PanelUtils.join(FlowLayout.LEFT, vanillaTrees, customTrees, new JEmptyBox(15, 2),
				L10N.label("elementgui.biome.tree_type")));
		sbbp4.add(vanillaTreeType);

		JPanel sbbp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		sbbp5.add(L10N.label("elementgui.biome.minimal_height"));
		sbbp5.add(minHeight);

		sbbp5.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/max_water_depth"), L10N.label("elementgui.biome.max_water_depth")));
		sbbp5.add(maxWaterDepth);

		sbbp5.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/stem_block"), L10N.label("elementgui.biome.stem_block"),
						new Color(49, 148, 53)));
		sbbp5.add(PanelUtils.join(treeStem));

		sbbp5.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/branch_block"), L10N.label("elementgui.biome.branch_block"),
						new Color(196, 104, 205)));
		sbbp5.add(PanelUtils.join(treeBranch));

		sbbp5.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/vines_block"), L10N.label("elementgui.biome.vines_block"),
						new Color(148, 248, 252)));
		sbbp5.add(PanelUtils.join(treeVines));

		sbbp5.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/fruits_block"), L10N.label("elementgui.biome.fruits_block"),
						new Color(255, 255, 0)));
		sbbp5.add(PanelUtils.join(treeFruits));

		customTrees.addActionListener(event -> updateBiomeTreesForm());
		vanillaTrees.addActionListener(event -> updateBiomeTreesForm());

		customTrees.setOpaque(false);
		vanillaTrees.setOpaque(false);
		minHeight.setOpaque(false);

		sbbp4.setOpaque(false);
		pane4.setOpaque(false);
		sbbp5.setOpaque(false);

		sbbp5.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.biome.custom_tree_properties"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel panels = new JPanel(new BorderLayout(10, 10));
		panels.setOpaque(false);

		panels.add("Center", PanelUtils.northAndCenterElement(sbbp4n, sbbp4, 5, 5));
		panels.add("East", new JLabel(UIRES.get("biomeblocks")));

		pane4.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndSouthElement(panels, sbbp5, 15, 15)));

		JPanel pane1 = new JPanel(new GridLayout());

		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/spawn_entities"),
						L10N.label("elementgui.biome.spawn_entities")), spawnEntries);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane1.add(component);

		pane1.setOpaque(false);


		JPanel effectsPane = new JPanel(new BorderLayout());

		JPanel sounds = new JPanel(new GridLayout(5, 2, 0, 2));

		sounds.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/music"),
				L10N.label("elementgui.biome.music")));
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.biome.sounds"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.biome.particles"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		spawnParticle.setOpaque(false);
		spawnParticle.addActionListener(event -> updateParticleParameters());

		effectsPane.setOpaque(false);
		effectsPane.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.westAndEastElement(sounds, PanelUtils.pullElementUp(particles))));

		page1group.addValidationElement(name);
		page1group.addValidationElement(groundBlock);
		page1group.addValidationElement(undergroundBlock);
		page1group.addValidationElement(treeStem);
		page1group.addValidationElement(treeBranch);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.biome.needs_name")));
		groundBlock.setValidator(new MCItemHolderValidator(groundBlock));
		undergroundBlock.setValidator(new MCItemHolderValidator(undergroundBlock));
		treeStem.setValidator(new MCItemHolderValidator(treeStem, customTrees));
		treeBranch.setValidator(new MCItemHolderValidator(treeBranch, customTrees));

		addPage(L10N.t("elementgui.biome.general_properties"), pane4);
		addPage(L10N.t("elementgui.biome.features"), pane3);
		addPage(L10N.t("elementgui.biome.structures"), pane2);
		addPage(L10N.t("elementgui.biome.effects"), effectsPane);
		addPage(L10N.t("elementgui.biome.entity_spawning"), pane1);
		addPage(L10N.t("elementgui.biome.biome_generation"), pane5);

		updateBiomeTreesForm();
		updateParticleParameters();

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);

			defaultFeatures.setListElements(Arrays.asList("Caves", "Ores"));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(parent,
				ListUtils.merge(Collections.singleton(noparent), ElementUtil.loadAllBiomes(mcreator.getWorkspace())),
				noparent);
		ComboBoxUtil.updateComboBoxContents(particleToSpawn, ElementUtil.loadAllParticles(mcreator.getWorkspace()));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	private void updateBiomeTreesForm() {
		if (customTrees.isSelected()) {
			minHeight.setEnabled(true);
			maxWaterDepth.setEnabled(true);
			treeVines.setEnabled(true);
			treeStem.setEnabled(true);
			treeBranch.setEnabled(true);
			treeFruits.setEnabled(true);
			maxWaterDepth.setEnabled(true);
		} else {
			minHeight.setEnabled(false);
			maxWaterDepth.setEnabled(false);
			treeVines.setEnabled(false);
			treeStem.setEnabled(false);
			treeBranch.setEnabled(false);
			treeFruits.setEnabled(false);
			maxWaterDepth.setEnabled(false);
		}
	}

	private void updateParticleParameters(){
		if(spawnParticle.isSelected()){
			particleToSpawn.setEnabled(true);
			particlesProbability.setEnabled(true);
		}else {
			particleToSpawn.setEnabled(false);
			particlesProbability.setEnabled(false);
		}
	}

	@Override public void openInEditingMode(Biome biome) {
		name.setText(biome.name);
		groundBlock.setBlock(biome.groundBlock);
		undergroundBlock.setBlock(biome.undergroundBlock);
		treeVines.setBlock(biome.treeVines);
		treeStem.setBlock(biome.treeStem);
		treeBranch.setBlock(biome.treeBranch);
		treeFruits.setBlock(biome.treeFruits);
		maxWaterDepth.setValue(biome.maxWaterDepth);

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
		grassColor.setColor(biome.grassColor);
		foliageColor.setColor(biome.foliageColor);
		waterColor.setColor(biome.waterColor);
		waterFogColor.setColor(biome.waterFogColor);
		treesPerChunk.setValue(biome.treesPerChunk);
		grassPerChunk.setValue(biome.grassPerChunk);
		seagrassPerChunk.setValue(biome.seagrassPerChunk);
		flowersPerChunk.setValue(biome.flowersPerChunk);
		mushroomsPerChunk.setValue(biome.mushroomsPerChunk);
		sandPatchesPerChunk.setValue(biome.sandPatchesPerChunk);
		reedsPerChunk.setValue(biome.reedsPerChunk);
		cactiPerChunk.setValue(biome.cactiPerChunk);
		rainingPossibility.setValue(biome.rainingPossibility);
		baseHeight.setValue(biome.baseHeight);
		heightVariation.setValue(biome.heightVariation);
		spawnBiome.setSelected(biome.spawnBiome);
		spawnStronghold.setSelected(biome.spawnStronghold);
		spawnMineshaft.setSelected(biome.spawnMineshaft);
		spawnPillagerOutpost.setSelected(biome.spawnPillagerOutpost);
		villageType.setSelectedItem(biome.villageType);
		spawnWoodlandMansion.setSelected(biome.spawnWoodlandMansion);
		spawnJungleTemple.setSelected(biome.spawnJungleTemple);
		spawnDesertPyramid.setSelected(biome.spawnDesertPyramid);
		spawnIgloo.setSelected(biome.spawnIgloo);
		spawnOceanMonument.setSelected(biome.spawnOceanMonument);
		spawnShipwreck.setSelected(biome.spawnShipwreck);
		oceanRuinType.setSelectedItem(biome.oceanRuinType);

		temperature.setValue(biome.temperature);
		bigMushroomsChunk.setValue(biome.bigMushroomsChunk);
		gravelPatchesPerChunk.setValue(biome.gravelPatchesPerChunk);
		biomeWeight.setValue(biome.biomeWeight);
		biomeType.setSelectedItem(biome.biomeType);
		biomeCategory.setSelectedItem(biome.biomeCategory);
		parent.setSelectedItem(biome.parent);
		biomeDictionaryTypes.setListElements(biome.biomeDictionaryTypes);
		defaultFeatures.setListElements(biome.defaultFeatures);
		vanillaTreeType.setSelectedItem(biome.vanillaTreeType);
		spawnEntries.setSpawns(biome.spawnEntries);

		updateBiomeTreesForm();
		updateParticleParameters();
	}

	@Override public Biome getElementFromGUI() {
		Biome biome = new Biome(modElement);
		biome.name = name.getText();
		biome.groundBlock = groundBlock.getBlock();
		biome.undergroundBlock = undergroundBlock.getBlock();
		if (customTrees.isSelected())
			biome.treeType = biome.TREES_CUSTOM;
		else
			biome.treeType = biome.TREES_VANILLA;
		biome.airColor = airColor.getColor();
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
		biome.grassPerChunk = (int) grassPerChunk.getValue();
		biome.seagrassPerChunk = (int) seagrassPerChunk.getValue();
		biome.flowersPerChunk = (int) flowersPerChunk.getValue();
		biome.mushroomsPerChunk = (int) mushroomsPerChunk.getValue();
		biome.bigMushroomsChunk = (int) bigMushroomsChunk.getValue();
		biome.sandPatchesPerChunk = (int) sandPatchesPerChunk.getValue();
		biome.gravelPatchesPerChunk = (int) gravelPatchesPerChunk.getValue();
		biome.reedsPerChunk = (int) reedsPerChunk.getValue();
		biome.cactiPerChunk = (int) cactiPerChunk.getValue();
		biome.rainingPossibility = (double) rainingPossibility.getValue();
		biome.baseHeight = (double) baseHeight.getValue();
		biome.heightVariation = (double) heightVariation.getValue();
		biome.temperature = (double) temperature.getValue();
		biome.biomeWeight = (int) biomeWeight.getValue();
		biome.biomeType = (String) biomeType.getSelectedItem();
		biome.biomeCategory = (String) biomeCategory.getSelectedItem();
		biome.parent = new BiomeEntry(mcreator.getWorkspace(), parent.getSelectedItem());
		biome.biomeDictionaryTypes = biomeDictionaryTypes.getListElements();
		biome.defaultFeatures = defaultFeatures.getListElements();
		biome.vanillaTreeType = (String) vanillaTreeType.getSelectedItem();
		biome.spawnEntries = spawnEntries.getSpawns();
		biome.minHeight = (int) minHeight.getValue();
		biome.treeVines = treeVines.getBlock();
		biome.treeStem = treeStem.getBlock();
		biome.treeBranch = treeBranch.getBlock();
		biome.treeFruits = treeFruits.getBlock();
		biome.maxWaterDepth = (int) maxWaterDepth.getValue();
		biome.spawnBiome = spawnBiome.isSelected();
		biome.spawnMineshaft = spawnMineshaft.isSelected();
		biome.spawnStronghold = spawnStronghold.isSelected();
		biome.spawnPillagerOutpost = spawnPillagerOutpost.isSelected();
		biome.villageType = (String) villageType.getSelectedItem();
		biome.spawnWoodlandMansion = spawnWoodlandMansion.isSelected();
		biome.spawnJungleTemple = spawnJungleTemple.isSelected();
		biome.spawnDesertPyramid = spawnDesertPyramid.isSelected();
		biome.spawnIgloo = spawnIgloo.isSelected();
		biome.spawnOceanMonument = spawnOceanMonument.isSelected();
		biome.spawnShipwreck = spawnShipwreck.isSelected();
		biome.oceanRuinType = (String) oceanRuinType.getSelectedItem();
		return biome;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-biome");
	}

}
