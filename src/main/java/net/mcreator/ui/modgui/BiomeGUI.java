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
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.StringUtils;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.minecraft.BiomeDictionaryTypeListField;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.spawntypes.JSpawnEntriesList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class BiomeGUI extends ModElementGUI<Biome> {

	private final JLabel laba = new JLabel();

	private VTextField name = new VTextField(20);

	private final JSpinner treesPerChunk = new JSpinner(new SpinnerNumberModel(3, 0, 256, 1));
	private final JSpinner grassPerChunk = new JSpinner(new SpinnerNumberModel(4, 0, 256, 1));
	private final JSpinner seaGrassPerChunk = new JSpinner(new SpinnerNumberModel(20, 0, 256, 1));
	private final JSpinner flowersPerChunk = new JSpinner(new SpinnerNumberModel(4, 0, 256, 1));
	private final JSpinner mushroomsPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner bigMushroomsChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner sandPathcesPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner gravelPatchesPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner reedsPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner cactiPerChunk = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner rainingPossibility = new JSpinner(new SpinnerNumberModel(0.5, 0, 1, 0.1));
	private final JSpinner baseHeight = new JSpinner(new SpinnerNumberModel(0.1, -10, 10, 0.1));
	private final JSpinner heightVariation = new JSpinner(new SpinnerNumberModel(0.2, 0, 2, 0.1));
	private final JSpinner minHeight = new JSpinner(new SpinnerNumberModel(7, 0, 1000, 1));
	private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(0.5, -1.0, 2.0, 0.1));

	private final JRadioButton customTrees = new JRadioButton("Custom trees");
	private final JRadioButton vanillaTrees = new JRadioButton("Vanilla trees");

	private final JCheckBox spawnVines = new JCheckBox("Spawn vines");
	private final JCheckBox spawnBiome = new JCheckBox();
	private final JCheckBox spawnStronghold = new JCheckBox();
	private final JCheckBox spawnMineshaft = new JCheckBox();
	private final JCheckBox spawnPillagerOutpost = new JCheckBox();
	private final JComboBox<String> villageType = new JComboBox<>(
			new String[] {"none", "desert", "plains", "savanna", "snowy", "taiga"});
	private final JCheckBox spawnWoodlandMansion = new JCheckBox();
	private final JCheckBox spawnJungleTemple = new JCheckBox();
	private final JCheckBox spawnDesertPyramid = new JCheckBox();
	private final JCheckBox spawnIgloo = new JCheckBox();
	private final JCheckBox spawnOceanMonument = new JCheckBox();
	private final JCheckBox spawnShipwreck = new JCheckBox();
	private final JComboBox<String> oceanRuinType = new JComboBox<>(
			new String[]{ "NONE", "COLD", "WARM"});

	private final JSpawnEntriesList spawnEntriesList = new JSpawnEntriesList(mcreator);

	private MCItemHolder groundBlock;
	private MCItemHolder undergroundBlock;

	private MCItemHolder treeVines;
	private MCItemHolder treeStem;
	private MCItemHolder treeBranch;
	private MCItemHolder treeFruits;

	private final JColor airColor = new JColor(mcreator, true);
	private final JColor grassColor = new JColor(mcreator, true);
	private final JColor foliageColor = new JColor(mcreator, true);
	private final JColor waterColor = new JColor(mcreator, true);
	private final JColor waterFogColor = new JColor(mcreator, true);

	private final JCheckBox generateLakes = new JCheckBox("Select to enable");

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
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		name.setOpaque(true);
		airColor.setOpaque(false);
		grassColor.setOpaque(false);
		foliageColor.setOpaque(false);
		waterColor.setOpaque(false);
		waterFogColor.setOpaque(false);

		laba.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")),
						"Preview of biome colors", 0, 0, getFont(), Color.white));

		JPanel sbbp2 = new JPanel(new GridLayout(11, 2, 4, 4));

		sbbp2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Biome structures", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_strongholds"),
				new JLabel("Generate Strongholds in the biome?")));
		sbbp2.add(spawnStronghold);
		spawnStronghold.setSelected(false);
		spawnStronghold.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_mineshafts"),
				new JLabel("Generate Mineshafts in the biome?")));
		sbbp2.add(spawnMineshaft);
		spawnMineshaft.setSelected(false);
		spawnMineshaft.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_pillager_outposts"),
				new JLabel("Generate Pillager Outposts in the biome?")));
		sbbp2.add(spawnPillagerOutpost);
		spawnPillagerOutpost.setSelected(false);
		spawnPillagerOutpost.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/village"),
				new JLabel("Generate Villages of type: ")));
		sbbp2.add(villageType);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_woodland_mansions"),
				new JLabel("Generate Woodland Mansions in the biome?")));
		sbbp2.add(spawnWoodlandMansion);
		spawnWoodlandMansion.setSelected(false);
		spawnWoodlandMansion.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_jungle_temples"),
				new JLabel("Generate Jungle Temples in the biome?")));
		sbbp2.add(spawnJungleTemple);
		spawnJungleTemple.setSelected(false);
		spawnJungleTemple.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_desert_pyramids"),
				new JLabel("Generate Desert Pyramids in the biome?")));
		sbbp2.add(spawnDesertPyramid);
		spawnDesertPyramid.setSelected(false);
		spawnDesertPyramid.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_igloo"),
				new JLabel("Generate Igloos in the biome?")));
		sbbp2.add(spawnIgloo);
		spawnIgloo.setSelected(false);
		spawnIgloo.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_ocean_monuments"),
				new JLabel("Generate Ocean Monuments in the biome?")));
		sbbp2.add(spawnOceanMonument);
		spawnOceanMonument.setSelected(false);
		spawnOceanMonument.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_shipwrecks"),
				new JLabel("Generate Shipwrecks in the biome?")));

		sbbp2.add(spawnShipwreck);
		spawnShipwreck.setSelected(false);
		spawnShipwreck.setOpaque(false);

		sbbp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_ocean_ruins"),
				new JLabel("Generate Ocean Ruins in the biome? ")));
		sbbp2.add(oceanRuinType);

		sbbp2.setOpaque(false);
		pane2.setOpaque(false);

		JPanel allmost = new JPanel(new BorderLayout(60, 10));
		allmost.setOpaque(false);

		JPanel spawnproperties = new JPanel(new GridLayout(8, 2, 5, 2));
		spawnproperties.setOpaque(false);

		spawnproperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Biome generator properties", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_overworld"),
				new JLabel("Generate biome in overworld?")));
		spawnproperties.add(spawnBiome);
		spawnBiome.setSelected(true);
		spawnBiome.setOpaque(false);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/weight"),
				new JLabel("<html>Biome weight:<br><small>Smaller is rarer biome")));
		spawnproperties.add(biomeWeight);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/type"), new JLabel("Biome type (temperature group):")));
		spawnproperties.add(biomeType);

		spawnproperties
				.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/category"), new JLabel("Biome category:")));
		spawnproperties.add(biomeCategory);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/parent"), new JLabel("Biome parent:")));
		spawnproperties.add(parent);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/dictionary"), new JLabel(
				"<html>Biome dictionary types:<br><small>"
						+ "Used by some mods to identify biome types for spawning")));
		spawnproperties.add(biomeDictionaryTypes);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/base_height"), new JLabel("Biome base height:")));
		spawnproperties.add(baseHeight);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/height_variation"), new JLabel("Biome height variation:")));
		spawnproperties.add(heightVariation);

		allmost.add("East", PanelUtils.pullElementUp(spawnproperties));
		allmost.add("Center", sbbp2);

		pane2.add("Center", PanelUtils.totalCenterInPanel(allmost));


		JPanel sbbp3 = new JPanel(new GridLayout(12, 2, 4, 4));

		sbbp3.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/trees_per_chunk"), new JLabel("Trees per chunk:")));
		sbbp3.add(treesPerChunk);

		sbbp3.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/grass_per_chunk"), new JLabel("Grass per chunk:")));
		sbbp3.add(grassPerChunk);

		sbbp3.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/sea_grass_per_chunk"), new JLabel("Sea grass per chunk:")));
		sbbp3.add(seaGrassPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/flowers_per_chunk"),
				new JLabel("<html>Vanilla flowers per chunk:")));
		sbbp3.add(flowersPerChunk);

		sbbp3.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/mushrooms_per_chunk"), new JLabel("Mushrooms per chunk:")));
		sbbp3.add(mushroomsPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/big_mushrooms_per_chunk"),
				new JLabel("Big mushrooms per chunk:")));
		sbbp3.add(bigMushroomsChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/sand_patches_per_chunk"),
				new JLabel("Sand patches per chunk:")));
		sbbp3.add(sandPathcesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gravel_patches_per_chunk"),
				new JLabel("Gravel patches per chunk:")));
		sbbp3.add(gravelPatchesPerChunk);

		sbbp3.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/reeds_per_chunk"), new JLabel("Reeds per chunk:")));
		sbbp3.add(reedsPerChunk);

		sbbp3.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("biome/cacti_per_chunk"), new JLabel("Cacti per chunk:")));
		sbbp3.add(cactiPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/raining_possibility"),
				new JLabel("Raining possibility (0-1):")));
		sbbp3.add(rainingPossibility);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/temperature"), new JLabel("Biome temperature:")));
		sbbp3.add(temperature);

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

		JPanel sbbp4 = new JPanel(new GridLayout(10, 2, 0, 5));
		generateLakes.setOpaque(false);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/name"), new JLabel("Name:")));
		sbbp4.add(name);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/ground_block"),
				new JLabel("<html>Ground block:<br><small>Tip: Ground block should use GRASS for material"),
				new Color(206, 109, 109).brighter()));
		sbbp4.add(PanelUtils.join(groundBlock));

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/underground_block"),
				new JLabel("<html>Underground block:<br><small>Tip: Underground block should use EARTH for material"),
				new Color(179, 94, 26).brighter()));
		sbbp4.add(PanelUtils.join(undergroundBlock));

		sbbp4.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/generate_lakes"), new JLabel("Generate small water lakes?")));
		sbbp4.add(PanelUtils.join(generateLakes));

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/air_color"), new JLabel("Air color:")));
		sbbp4.add(airColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/grass_color"), new JLabel("Grass color:")));
		sbbp4.add(grassColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/foliage_color"), new JLabel("Foliage color:")));
		sbbp4.add(foliageColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_color"), new JLabel("Water color:")));
		sbbp4.add(waterColor);

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/water_fog_color"), new JLabel("Water fog color:")));
		sbbp4.add(waterFogColor);

		sbbp4.add(vanillaTrees);
		sbbp4.add(PanelUtils.join(vanillaTreeType));

		JPanel sbbp5 = new JPanel(new GridLayout(3, 4, 5, 5));

		sbbp5.add(PanelUtils.join(customTrees));
		sbbp5.add(new JLabel("Minimal height:"));

		sbbp5.add(minHeight);
		sbbp5.add(PanelUtils.join(spawnVines));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/stem_block"), new JLabel("Block for stem:"),
				new Color(49, 148, 53)));
		sbbp5.add(PanelUtils.join(treeStem));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/branch_block"), new JLabel("Block for branch:"),
				new Color(196, 104, 205)));
		sbbp5.add(PanelUtils.join(treeBranch));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/vines_block"), new JLabel("Block for vines:"),
				new Color(148, 248, 252)));
		sbbp5.add(PanelUtils.join(treeVines));

		sbbp5.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/fruits_block"), new JLabel("Block for fruits:"),
				new Color(255, 255, 0)));
		sbbp5.add(PanelUtils.join(treeFruits));

		customTrees.addActionListener(event -> updateBiomeTreesForm());
		vanillaTrees.addActionListener(event -> updateBiomeTreesForm());

		customTrees.setOpaque(false);
		vanillaTrees.setOpaque(false);
		spawnVines.setOpaque(false);
		minHeight.setOpaque(false);

		sbbp4.setOpaque(false);

		pane4.setOpaque(false);

		sbbp5.setOpaque(false);

		sbbp5.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Custom tree properties (if selected)", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JPanel panels = new JPanel(new BorderLayout(15, 25));

		JLabel tt = new JLabel(UIRES.get("biomeblocks"));

		panels.add("Center", PanelUtils.join(sbbp4));
		panels.add("South", sbbp5);

		panels.setOpaque(false);

		JPanel cont = new JPanel(new BorderLayout(30, 30));
		cont.setOpaque(false);

		cont.add("East", tt);
		cont.add("Center", PanelUtils.join(panels));

		pane4.add("Center", PanelUtils.totalCenterInPanel(cont));

		JPanel pane1 = new JPanel(new GridLayout());

		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
						.wrapWithHelpButton(this.withEntry("biome/spawn_entities"), new JLabel(
								"<html>Entities to spawn in this biome (if custom entity already declares spawning in this biome, do not add it here again):<br>"
										+ "<small>Note: Additional spawning conditions of custom entites will only work if spawning is defined from the custom entity itself, not here")),
				spawnEntriesList);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane1.add(component);

		pane1.setOpaque(false);

		page1group.addValidationElement(name);
		page1group.addValidationElement(groundBlock);
		page1group.addValidationElement(undergroundBlock);
		page1group.addValidationElement(treeVines);
		page1group.addValidationElement(treeStem);
		page1group.addValidationElement(treeBranch);
		page1group.addValidationElement(treeFruits);

		name.setValidator(new TextFieldValidator(name, "Biome needs a name"));
		groundBlock.setValidator(new MCItemHolderValidator(groundBlock));
		undergroundBlock.setValidator(new MCItemHolderValidator(undergroundBlock));
		treeVines.setValidator(new MCItemHolderValidator(treeVines, customTrees));
		treeStem.setValidator(new MCItemHolderValidator(treeStem, customTrees));
		treeBranch.setValidator(new MCItemHolderValidator(treeBranch, customTrees));
		treeFruits.setValidator(new MCItemHolderValidator(treeFruits, customTrees));

		addPage("Properties", pane4);
		addPage("Features", pane3);
		addPage("Generation", pane2);
		addPage("Entity spawning", pane1);

		updateBiomeTreesForm();
		
		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(parent,
				ListUtils.merge(Collections.singleton(noparent), ElementUtil.loadAllBiomes(mcreator.getWorkspace())),
				noparent);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	private void updateBiomeTreesForm() {
		if (customTrees.isSelected()) {
			vanillaTreeType.setEnabled(false);
			minHeight.setEnabled(true);
			spawnVines.setEnabled(true);
			treeVines.setEnabled(true);
			treeStem.setEnabled(true);
			treeBranch.setEnabled(true);
			treeFruits.setEnabled(true);
		} else {
			vanillaTreeType.setEnabled(true);
			minHeight.setEnabled(false);
			spawnVines.setEnabled(false);
			treeVines.setEnabled(false);
			treeStem.setEnabled(false);
			treeBranch.setEnabled(false);
			treeFruits.setEnabled(false);
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
		generateLakes.setSelected(biome.generateLakes);

		if (biome.treeType == biome.TREES_CUSTOM) {
			vanillaTrees.setSelected(false);
			customTrees.setSelected(true);
		} else {
			vanillaTrees.setSelected(true);
			customTrees.setSelected(false);
		}

		spawnVines.setSelected(biome.spawnVines);
		minHeight.setValue(biome.minHeight);
		airColor.setColor(biome.airColor);
		grassColor.setColor(biome.grassColor);
		foliageColor.setColor(biome.foliageColor);
		waterColor.setColor(biome.waterColor);
		waterFogColor.setColor(biome.waterFogColor);
		treesPerChunk.setValue(biome.treesPerChunk);
		grassPerChunk.setValue(biome.grassPerChunk);
		seaGrassPerChunk.setValue(biome.seaGrassPerChunk);
		flowersPerChunk.setValue(biome.flowersPerChunk);
		mushroomsPerChunk.setValue(biome.mushroomsPerChunk);
		sandPathcesPerChunk.setValue(biome.sandPathcesPerChunk);
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
		vanillaTreeType.setSelectedItem(biome.vanillaTreeType);
		spawnEntriesList.setSpawns(biome.spawnEntries);

		updateBiomeTreesForm();
	}

	@Override public Biome getElementFromGUI() {
		Biome biome = new Biome(modElement);
		biome.name = name.getText();
		biome.groundBlock = groundBlock.getBlock();
		biome.undergroundBlock = undergroundBlock.getBlock();
		biome.generateLakes = generateLakes.isSelected();
		if (customTrees.isSelected())
			biome.treeType = biome.TREES_CUSTOM;
		else
			biome.treeType = biome.TREES_VANILLA;
		biome.airColor = airColor.getColor();
		biome.grassColor = grassColor.getColor();
		biome.foliageColor = foliageColor.getColor();
		biome.waterColor = waterColor.getColor();
		biome.waterFogColor = waterFogColor.getColor();
		biome.treesPerChunk = (int) treesPerChunk.getValue();
		biome.grassPerChunk = (int) grassPerChunk.getValue();
		biome.seaGrassPerChunk = (int) seaGrassPerChunk.getValue();
		biome.flowersPerChunk = (int) flowersPerChunk.getValue();
		biome.mushroomsPerChunk = (int) mushroomsPerChunk.getValue();
		biome.bigMushroomsChunk = (int) bigMushroomsChunk.getValue();
		biome.sandPathcesPerChunk = (int) sandPathcesPerChunk.getValue();
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
		biome.vanillaTreeType = (String) vanillaTreeType.getSelectedItem();
		biome.spawnEntries = spawnEntriesList.getSpawns();
		biome.minHeight = (int) minHeight.getValue();
		biome.spawnVines = spawnVines.isSelected();
		biome.treeVines = treeVines.getBlock();
		biome.treeStem = treeStem.getBlock();
		biome.treeBranch = treeBranch.getBlock();
		biome.treeFruits = treeFruits.getBlock();
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
