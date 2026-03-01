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
import net.mcreator.ui.minecraft.BeBiomeTagsListField;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.core.config.plugins.validation.validators.NotBlankValidator;
import org.apache.logging.log4j.core.config.plugins.validation.validators.RequiredValidator;

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

    private final JSpinner seaFloorDepth = new JSpinner(new SpinnerNumberModel(7, 0, 256, 1));
    private final JSpinner temperature = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 2.0, 0.1));
    private final JSpinner downfall = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
	private final JSpinner replacementAmount = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
	private final JSpinner replacementNoiseFrequencyScale = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 100, 0.1));

	private final JMinMaxSpinner snowAccumulation = new JMinMaxSpinner(0.0, 0.0, 0.0, 1.0, 0.125);

	private final JColor airColor = new JColor(mcreator, true, false);
	private final JColor fogColor = new JColor(mcreator, true, false);
	private final JColor grassColor = new JColor(mcreator, true, false);
	private final JColor foliageColor = new JColor(mcreator, true, false);
	private final JColor waterColor = new JColor(mcreator, true, false);
	private final JColor waterFogColor = new JColor(mcreator, true, false);

    private final JComboBox<String> noiseType = new JComboBox<>(new String[]{"default", "default_mutated", "stone_beach", "deep_ocean", "lowlands", "river", "ocean", "taiga", "mountains", "highlands", "mushroom", "less_extreme", "extreme", "beach", "swamp"});
	private final JComboBox<String> villageType = new JComboBox<>(new String[]{"default", "desert", "ice", "savanna", "taiga"});

	private final BeBiomeTagsListField biomeTags = new BeBiomeTagsListField(mcreator);
	private final BiomeListField biomeReplacements = new BiomeListField(mcreator, false);

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


        JPanel propertiesPanel = new JPanel(new GridLayout(9, 2, 2, 2));
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

		biomeReplacements.setValidator(new ItemListFieldValidator(biomeReplacements,
				L10N.t("elementgui.bebiome.error_must_have_replacement")));

        page2group.addValidationElement(topMaterial);
        page2group.addValidationElement(midMaterial);
		page2group.addValidationElement(foundationMaterial);
		page2group.addValidationElement(biomeReplacements);

        addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(propertiesPanel));
        addPage(L10N.t("elementgui.biome.biome_generation"), PanelUtils.totalCenterInPanel(materialsPanel)).validate(page2group);
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

        noiseType.setSelectedItem(biome.noiseType);
		villageType.setSelectedItem(biome.villageType);

		biomeTags.setListElements(biome.biomeTags);
		biomeReplacements.setListElements(biome.biomeReplacements);
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

        biome.noiseType = (String) noiseType.getSelectedItem();
		biome.villageType = (String) villageType.getSelectedItem();

		biome.biomeTags = biomeTags.getListElements();
		biome.biomeReplacements = biomeReplacements.getListElements();

        return biome;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-biome");
    }
}
