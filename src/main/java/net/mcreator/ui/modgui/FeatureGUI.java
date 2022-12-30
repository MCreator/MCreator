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
import net.mcreator.blockly.data.*;
import net.mcreator.blockly.feature.BlocklyToFeature;
import net.mcreator.element.types.Feature;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorToolbar;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.minecraft.DimensionListField;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeatureGUI extends ModElementGUI<Feature> {
	private ProcedureSelector generateCondition;
	private BiomeListField restrictionBiomes;
	private DimensionListField restrictionDimensions;
	private final JComboBox<String> generationStep = new JComboBox<>();

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
		generateCondition = new ProcedureSelector(this.withEntry("feature/generation_condition"), mcreator,
				L10N.t("elementgui.feature.additional_generation_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		restrictionBiomes = new BiomeListField(mcreator);
		restrictionBiomes.setPreferredSize(new Dimension(380, -1));

		restrictionDimensions = new DimensionListField(mcreator);
		restrictionBiomes.setPreferredSize(new Dimension(380, -1));

		JPanel page1 = new JPanel(new BorderLayout(10, 10));
		JPanel properties = new JPanel(new GridLayout(3, 2, 4, 2));

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/generation_stage"),
				L10N.label("elementgui.feature.generation_stage")));
		properties.add(generationStep);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/restrict_to_dimensions"),
				L10N.label("elementgui.feature.restrict_to_dimensions")));
		properties.add(restrictionDimensions);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		properties.add(restrictionBiomes);

		properties.setOpaque(false);

		JComponent propertiesAndCondition = PanelUtils.northAndCenterElement(properties,
				PanelUtils.westAndCenterElement(new JEmptyBox(4, 4), generateCondition), 0, 2);

		propertiesAndCondition.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE).getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.EMPTY);
			blocklyPanel.getJSBridge()
					.setJavaScriptEventListener(() -> new Thread(FeatureGUI.this::regenerateFeature).start());
			if (!isEditingMode()) {
				blocklyPanel.setXML("""
						<xml><block type="feature_container" deletable="false" x="40" y="40"/></xml>""");
			}
		});

		JPanel blocklyAndToolbarPanel = new JPanel(new GridLayout());
		blocklyAndToolbarPanel.setOpaque(false);
		BlocklyEditorToolbar blocklyEditorToolbar = new BlocklyEditorToolbar(mcreator, BlocklyEditorType.FEATURE,
				blocklyPanel);
		blocklyEditorToolbar.setTemplateLibButtonWidth(156);
		blocklyAndToolbarPanel.add(PanelUtils.northAndCenterElement(blocklyEditorToolbar, blocklyPanel));

		JPanel featureProcedure = (JPanel) PanelUtils.centerAndSouthElement(blocklyAndToolbarPanel, compileNotesPanel);
		featureProcedure.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.feature.feature_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Color.white));

		featureProcedure.setPreferredSize(new Dimension(0,460));

		page1.add("Center", PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, propertiesAndCondition), featureProcedure));

		page1.setOpaque(false);
		addPage(page1);
	}

	private void regenerateFeature() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.FEATURE));

		BlocklyToFeature blocklyToFeature;
		try {
			blocklyToFeature = new BlocklyToFeature(mcreator.getWorkspace(), this.modElement, blocklyPanel.getXML(),
					null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
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

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(generationStep,
				Arrays.asList(ElementUtil.getDataListAsStringArray("generationsteps")), "SURFACE_STRUCTURES");
		generateCondition.refreshListKeepSelected();
	}

	@Override protected void openInEditingMode(Feature feature) {
		generationStep.setSelectedItem(feature.generationStep);
		restrictionDimensions.setListElements(feature.restrictionDimensions);
		restrictionBiomes.setListElements(feature.restrictionBiomes);
		generateCondition.setSelectedProcedure(feature.generateCondition);

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
		feature.restrictionDimensions = restrictionDimensions.getListElements();
		feature.restrictionBiomes = restrictionBiomes.getListElements();
		feature.generateCondition = generateCondition.getSelectedProcedure();

		feature.featurexml = blocklyPanel.getXML();

		return feature;
	}
}
