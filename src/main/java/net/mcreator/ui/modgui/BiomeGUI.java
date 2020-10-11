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
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.minecraft.BiomeDictionaryTypeListField;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.DefaultFeaturesListField;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.spawntrees.JTreeEntriesList;
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

	private final JSpinner grassPerChunk = new JSpinner(new SpinnerNumberModel(4, 0, 256, 1));
	private final JSpinner seagrassPerChunk = new JSpinner(new SpinnerNumberModel(20, 0, 256, 1));
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
	private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(0.5, -1.0, 2.0, 0.1));

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

	private final JSpawnEntriesList spawnEntriesList = new JSpawnEntriesList(mcreator);
	private final JTreeEntriesList treeEntriesList = new JTreeEntriesList(mcreator);

	private MCItemHolder groundBlock;
	private MCItemHolder undergroundBlock;

	private final JColor airColor = new JColor(mcreator, true);
	private final JColor grassColor = new JColor(mcreator, true);
	private final JColor foliageColor = new JColor(mcreator, true);
	private final JColor waterColor = new JColor(mcreator, true);
	private final JColor waterFogColor = new JColor(mcreator, true);

	private final JSpinner biomeWeight = new JSpinner(new SpinnerNumberModel(10, 0, 1024, 1));
	private final JComboBox<String> biomeType = new JComboBox<>(new String[] { "WARM", "DESERT", "COOL", "ICY" });

	private final JComboBox<String> biomeCategory = new JComboBox<>(
			new String[] { "NONE", "TAIGA", "EXTREME_HILLS", "JUNGLE", "MESA", "PLAINS", "SAVANNA", "ICY", "THEEND",
					"BEACH", "FOREST", "OCEAN", "DESERT", "RIVER", "SWAMP", "MUSHROOM", "NETHER" });

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

		biomeType.setRenderer(new ItemTexturesComboBoxRenderer());

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

		JPanel spawnproperties = new JPanel(new GridLayout(8, 2, 5, 2));
		spawnproperties.setOpaque(false);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/generate_overworld"),
				L10N.label("elementgui.biome.generate_overworld")));
		spawnproperties.add(spawnBiome);
		spawnBiome.setSelected(true);
		spawnBiome.setOpaque(false);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/weight"), L10N.label("elementgui.biome.weight")));
		spawnproperties.add(biomeWeight);

		spawnproperties
				.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/type"), L10N.label("elementgui.biome.type")));
		spawnproperties.add(biomeType);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/category"), L10N.label("elementgui.biome.category")));
		spawnproperties.add(biomeCategory);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/parent"), L10N.label("elementgui.biome.parent")));
		spawnproperties.add(parent);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/dictionary"),
				new JLabel(L10N.t("elementgui.biome.dictionnary"))));
		spawnproperties.add(biomeDictionaryTypes);

		spawnproperties.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/base_height"), L10N.label("elementgui.biome.height")));
		spawnproperties.add(baseHeight);

		spawnproperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/height_variation"),
				L10N.label("elementgui.biome.height_variation")));
		spawnproperties.add(heightVariation);

		pane5.add("Center", PanelUtils.totalCenterInPanel(spawnproperties));
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp2));

		JPanel sbbp3 = new JPanel(new GridLayout(12, 2, 10, 2));

		defaultFeatures.setPreferredSize(new Dimension(340, 36));

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
		sbbp3.add(sandPathcesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/gravel_patches_per_chunk"),
				L10N.label("elementgui.biome.gravel_patches_per_chunk")));
		sbbp3.add(gravelPatchesPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/reeds_per_chunk"),
				L10N.label("elementgui.biome.reeds_per_chunk")));
		sbbp3.add(reedsPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/cacti_per_chunk"),
				L10N.label("elementgui.biome.cacti_per_chunk")));
		sbbp3.add(cactiPerChunk);

		sbbp3.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/raining_possibility"),
				L10N.label("elementgui.biome.raining_possibility")));
		sbbp3.add(rainingPossibility);

		sbbp3.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/temperature"), L10N.label("elementgui.biome.temperature")));
		sbbp3.add(temperature);

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

		JPanel pane6 = new JPanel(new GridLayout());

		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/spawn_trees"),
						new JLabel(L10N.t("elementgui.biome.spawn_trees"))), treeEntriesList);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane6.add(component);

		pane6.setOpaque(false);

		JPanel sbbp4 = new JPanel(new GridLayout(8, 2, 0, 2));

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/name"), L10N.label("elementgui.biome.name")));
		sbbp4.add(name);

		sbbp4.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/ground_block"), L10N.label("elementgui.biome.ground_block"),
						new Color(206, 109, 109).brighter()));
		sbbp4.add(PanelUtils.join(groundBlock));

		sbbp4.add(HelpUtils.wrapWithHelpButton(this.withEntry("biome/underground_block"),
				L10N.label("elementgui.biome.undeground_block"), new Color(179, 94, 26).brighter()));
		sbbp4.add(PanelUtils.join(undergroundBlock));

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

		sbbp4.setOpaque(false);

		pane4.setOpaque(false);

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

		JComponent component2 = PanelUtils.northAndCenterElement(HelpUtils
				.wrapWithHelpButton(this.withEntry("biome/spawn_entities"),
						new JLabel(L10N.t("elementgui.biome.spawn_entities"))), spawnEntriesList);

		component2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		pane1.add(component2);

		pane1.setOpaque(false);

		page1group.addValidationElement(name);
		page1group.addValidationElement(groundBlock);
		page1group.addValidationElement(undergroundBlock);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.biome.needs_name")));
		groundBlock.setValidator(new MCItemHolderValidator(groundBlock));
		undergroundBlock.setValidator(new MCItemHolderValidator(undergroundBlock));

		addPage(L10N.t("elementgui.biome.general_properties"), pane4);
		addPage(L10N.t("elementgui.biome.features"), pane3);
		addPage(L10N.t("elementgui.biome.trees"), pane6);
		addPage(L10N.t("elementgui.biome.structures"), pane2);
		addPage(L10N.t("elementgui.biome.entity_spawning"), pane1);
		addPage(L10N.t("elementgui.biome.biome_generation"), pane5);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);

			defaultFeatures.setListElements(Arrays.asList("Caves", "MonsterRooms", "Structures", "Ores"));
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

	@Override public void openInEditingMode(Biome biome) {
		name.setText(biome.name);
		groundBlock.setBlock(biome.groundBlock);
		undergroundBlock.setBlock(biome.undergroundBlock);

		airColor.setColor(biome.airColor);
		grassColor.setColor(biome.grassColor);
		foliageColor.setColor(biome.foliageColor);
		waterColor.setColor(biome.waterColor);
		waterFogColor.setColor(biome.waterFogColor);
		grassPerChunk.setValue(biome.grassPerChunk);
		seagrassPerChunk.setValue(biome.seagrassPerChunk);
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
		defaultFeatures.setListElements(biome.defaultFeatures);
		spawnEntriesList.setSpawns(biome.spawnEntries);
		treeEntriesList.setSpawns(biome.spawnTrees);
	}

	@Override public Biome getElementFromGUI() {
		Biome biome = new Biome(modElement);
		biome.name = name.getText();
		biome.groundBlock = groundBlock.getBlock();
		biome.undergroundBlock = undergroundBlock.getBlock();
		biome.airColor = airColor.getColor();
		biome.grassColor = grassColor.getColor();
		biome.foliageColor = foliageColor.getColor();
		biome.waterColor = waterColor.getColor();
		biome.waterFogColor = waterFogColor.getColor();
		biome.grassPerChunk = (int) grassPerChunk.getValue();
		biome.seagrassPerChunk = (int) seagrassPerChunk.getValue();
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
		biome.defaultFeatures = defaultFeatures.getListElements();
		biome.spawnEntries = spawnEntriesList.getSpawns();
		biome.spawnTrees = treeEntriesList.getSpawns();
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
