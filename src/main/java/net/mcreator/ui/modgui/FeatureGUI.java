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

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalBlockLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.feature.BlocklyToFeature;
import net.mcreator.element.types.Feature;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeatureGUI extends ModElementGUI<Feature> {
	private BiomeListField restrictionBiomes;
	private final JComboBox<String> generationStep = new JComboBox<>(
			new String[] { "RAW_GENERATION", "LAKES", "LOCAL_MODIFICATIONS", "UNDERGROUND_STRUCTURES",
					"SURFACE_STRUCTURES", "STRONGHOLDS", "UNDERGROUND_ORES", "UNDERGROUND_DECORATION",
					"FLUID_SPRINGS", "VEGETAL_DECORATION", "TOP_LAYER_MODIFICATION"});

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private boolean hasErrors = false;
	private Map<String, ToolboxBlock> externalBlocks;

	public FeatureGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		restrictionBiomes = new BiomeListField(mcreator);

		JPanel page1 = new JPanel(new BorderLayout(10, 10));
		JPanel properties = new JPanel(new GridLayout(2, 2, 15, 2));

		restrictionBiomes.setPreferredSize(new Dimension(380, -1));

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/generation_stage"),
				L10N.label("elementgui.feature.generation_stage")));
		properties.add(generationStep);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		properties.add(restrictionBiomes);

		properties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.feature.properties"), 0, 0, properties.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		properties.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getFeatureBlockLoader().getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getFeatureBlockLoader()
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ExternalBlockLoader.ToolboxType.EMPTY);
			blocklyPanel.getJSBridge()
					.setJavaScriptEventListener(() -> new Thread(FeatureGUI.this::regenerateFeature).start());
			if (!isEditingMode()) {
				blocklyPanel.setXML("""
						<xml><block type="feature_container" deletable="false" x="40" y="40"/></xml>""");
			}
		});

		JPanel featureProcedure = (JPanel) PanelUtils.centerAndSouthElement(blocklyPanel, compileNotesPanel);
		featureProcedure.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.feature.feature_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Color.white));

		featureProcedure.setPreferredSize(new Dimension(0,500));

		page1.add("Center", PanelUtils.northAndCenterElement(properties, featureProcedure));

		page1.setOpaque(false);
		addPage(page1);
	}

	private void regenerateFeature() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getFeatureProcedures());

		BlocklyToFeature blocklyToFeature;
		try {
			blocklyToFeature = new BlocklyToFeature(mcreator.getWorkspace(), "feature_container",
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToFeature.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			hasErrors = false;
			for (BlocklyCompileNote note : compileNotesArrayList) {
				if (note.type() == BlocklyCompileNote.Type.ERROR) {
					hasErrors = true;
					break;
				}
			}
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
		});
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return hasErrors ?
				new AggregatedValidationResult.MULTIFAIL(
						compileNotesPanel.getCompileNotes().stream().map(BlocklyCompileNote::message)
								.collect(Collectors.toList())) :
				new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(Feature feature) {
		generationStep.setSelectedItem(feature.generationStep);
		restrictionBiomes.setListElements(feature.restrictionBiomes);

		blocklyPanel.setXMLDataOnly(feature.featurexml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(feature.featurexml);
			regenerateFeature();
		});
	}

	@Override public Feature getElementFromGUI() {
		Feature feature = new Feature(modElement);
		feature.generationStep = (String) generationStep.getSelectedItem();
		feature.restrictionBiomes = restrictionBiomes.getListElements();

		feature.featurexml = blocklyPanel.getXML();

		return feature;
	}
}
