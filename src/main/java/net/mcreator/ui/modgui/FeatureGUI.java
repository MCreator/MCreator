/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Feature;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.minecraft.DimensionListField;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class FeatureGUI extends ModElementGUI<Feature> {

	// Common
	private final JComboBox<String> generationType = new JComboBox<>(new String[] { "Ore" });
	private final BiomeListField restrictionBiomes = new BiomeListField(mcreator);
	private final MCItemListField blocksToReplace = new MCItemListField(mcreator, ElementUtil::loadBlocks);
	private final DimensionListField spawnWorldTypes = new DimensionListField(mcreator);
	private ProcedureSelector generateCondition;


	private final MCItemHolder blockToGenerate = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

	// Ore
	private final JComboBox<String> generationShape = new JComboBox<>(new String[] { "UNIFORM", "TRIANGLE" });
	private final JSpinner minGenerateHeight = new JSpinner(new SpinnerNumberModel(0, -2032, 2016, 1));
	private final JSpinner maxGenerateHeight = new JSpinner(new SpinnerNumberModel(64, -2032, 2016, 1));
	private final JSpinner frequencyPerChunks = new JSpinner(new SpinnerNumberModel(10, 1, 64, 1));
	private final JSpinner frequencyOnChunk = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));


	private final CardLayout genTypesLayout = new CardLayout();
	private final JPanel genTypeParams = new JPanel(genTypesLayout);

	private final ValidationGroup page1group = new ValidationGroup();

	public FeatureGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		blocksToReplace.setListElements(
				new ArrayList<>(Collections.singleton(new MItemBlock(mcreator.getWorkspace(), "Blocks.STONE"))));

		generateCondition = new ProcedureSelector(this.withEntry("feature/generation_condition"), mcreator,
				L10N.t("elementgui.feature.event_generate_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();
		generateCondition.refreshListKeepSelected();

		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		JPanel commonParams = new JPanel(new GridLayout(4, 2, 20, 2));
		commonParams.setOpaque(false);

		commonParams.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/generation_type"),
				L10N.label("elementgui.feature.generation_type")));
		commonParams.add(generationType);

		generationType.addActionListener(e -> updateGenTypeParametersUI());

		commonParams.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/spawn_world_types"),
				L10N.label("elementgui.feature.spawn_world_types")));
		commonParams.add(spawnWorldTypes);

		commonParams.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/gen_replace_blocks"),
				L10N.label("elementgui.feature.gen_replace_blocks")));
		commonParams.add(blocksToReplace);

		commonParams.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		commonParams.add(restrictionBiomes);


		JPanel generationFeature = new JPanel(new BorderLayout(30, 15));
		generationFeature.setOpaque(false);

		JPanel oreFeature = new JPanel(new GridLayout(6, 2, 20, 2));
		oreFeature.setOpaque(false);

		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/block_to_generate"),
				L10N.label("elementgui.feature.block_to_generate")));
		oreFeature.add(PanelUtils.centerInPanel(blockToGenerate));

		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/generation_shape"),
				L10N.label("elementgui.feature.generation_shape")));
		oreFeature.add(generationShape);

		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/ore/gen_chunk_count"),
				L10N.label("elementgui.feature.gen_chunck_count")));
		oreFeature.add(frequencyPerChunks);

		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/ore/gen_group_size"),
				L10N.label("elementgui.feature.gen_group_size")));
		oreFeature.add(frequencyOnChunk);

		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/ore/gen_min_height"),
				L10N.label("elementgui.feature.gen_min_height")));
		oreFeature.add(minGenerateHeight);
		oreFeature.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/ore/gen_max_height"),
				L10N.label("elementgui.feature.gen_max_height")));
		oreFeature.add(maxGenerateHeight);


		genTypeParams.setOpaque(false);
		genTypeParams.add(oreFeature, "Ore");
		generationFeature.add("Center", genTypeParams);

		panel.add("West", PanelUtils.totalCenterInPanel(new JLabel(UIRES.get("chunk"))));
		panel.add("Center", PanelUtils.northAndCenterElement(PanelUtils.centerInPanel(PanelUtils.pullElementUp(
				PanelUtils.northAndCenterElement(commonParams, PanelUtils.westAndCenterElement(new JEmptyBox(5, 5),
						generateCondition), 5, 5))), generationFeature));
		addPage(PanelUtils.totalCenterInPanel(panel));

		updateGenTypeParametersUI();
	}

	private void updateGenTypeParametersUI() {
		genTypesLayout.show(genTypeParams, (String) generationType.getSelectedItem());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(Feature feature) {
		generationType.setSelectedItem(feature.generationType);
		spawnWorldTypes.setListElements(feature.spawnWorldTypes);
		restrictionBiomes.setListElements(feature.restrictionBiomes);
		blocksToReplace.setListElements(feature.blocksToReplace);
		generateCondition.setSelectedProcedure(feature.generateCondition);

		blockToGenerate.setBlock(feature.blockToGenerate);

		//Ore
		generationShape.setSelectedItem(feature.generationShape);
		maxGenerateHeight.setValue(feature.maxGenerateHeight);
		minGenerateHeight.setValue(feature.minGenerateHeight);
		frequencyPerChunks.setValue(feature.frequencyPerChunks);
		frequencyOnChunk.setValue(feature.frequencyOnChunk);
	}

	@Override public Feature getElementFromGUI() {
		Feature feature = new Feature(modElement);
		feature.generationType = (String) generationType.getSelectedItem();
		feature.spawnWorldTypes = spawnWorldTypes.getListElements();
		feature.restrictionBiomes = restrictionBiomes.getListElements();
		feature.blocksToReplace = blocksToReplace.getListElements();
		feature.generateCondition = generateCondition.getSelectedProcedure();

		feature.blockToGenerate = blockToGenerate.getBlock();

		//Ore
		feature.generationShape = (String) generationShape.getSelectedItem();
		feature.frequencyPerChunks = (int) frequencyPerChunks.getValue();
		feature.frequencyOnChunk = (int) frequencyOnChunk.getValue();
		feature.minGenerateHeight = (int) minGenerateHeight.getValue();
		feature.maxGenerateHeight = (int) maxGenerateHeight.getValue();

		return feature;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-feature");
	}
}
