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
import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxType;
import net.mcreator.blockly.feature.BlocklyToFeature;
import net.mcreator.element.types.Feature;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.*;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.validators.ItemListFieldSingleTagValidator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeatureGUI extends ModElementGUI<Feature> implements IBlocklyPanelHolder {

	private ProcedureSelector generateCondition;
	private BiomeListField restrictionBiomes;
	private final JComboBox<String> generationStep = new JComboBox<>(
			ElementUtil.getDataListAsStringArray("generationsteps"));

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private Map<String, ToolboxBlock> externalBlocks;
	private final List<BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	public FeatureGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override public void addBlocklyChangedListener(BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override protected void initGUI() {
		generateCondition = new ProcedureSelector(this.withEntry("feature/generation_condition"), mcreator,
				L10N.t("elementgui.feature.additional_generation_condition"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		if (!isEditingMode())
			generationStep.setSelectedItem("SURFACE_STRUCTURES");

		restrictionBiomes = new BiomeListField(mcreator, true);
		restrictionBiomes.setValidator(new ItemListFieldSingleTagValidator(restrictionBiomes));
		restrictionBiomes.setPreferredSize(new Dimension(380, -1));

		JPanel page1 = new JPanel(new BorderLayout(10, 10));
		JPanel properties = new JPanel(new GridLayout(2, 2, 4, 2));

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("feature/generation_stage"),
				L10N.label("elementgui.feature.generation_stage")));
		properties.add(generationStep);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				L10N.label("elementgui.common.restrict_to_biomes")));
		properties.add(restrictionBiomes);

		properties.setOpaque(false);

		JComponent propertiesAndCondition = PanelUtils.northAndCenterElement(properties,
				PanelUtils.westAndCenterElement(new JEmptyBox(4, 4), generateCondition), 0, 2);

		propertiesAndCondition.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE).getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.FEATURE);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.FEATURE);
			blocklyPanel.getJSBridge().setJavaScriptEventListener(
					() -> new Thread(FeatureGUI.this::regenerateFeature, "FeatureRegenerate").start());
			if (!isEditingMode()) {
				blocklyPanel.setXML(Feature.XML_BASE);
			}
		});

		JPanel blocklyAndToolbarPanel = new JPanel(new GridLayout());
		blocklyAndToolbarPanel.setOpaque(false);
		BlocklyEditorToolbar blocklyEditorToolbar = new BlocklyEditorToolbar(mcreator, BlocklyEditorType.FEATURE,
				blocklyPanel);
		blocklyEditorToolbar.setTemplateLibButtonWidth(175);
		blocklyAndToolbarPanel.add(PanelUtils.northAndCenterElement(blocklyEditorToolbar, blocklyPanel));

		JPanel featureProcedure = (JPanel) PanelUtils.centerAndSouthElement(blocklyAndToolbarPanel, compileNotesPanel);
		featureProcedure.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.feature.feature_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		featureProcedure.setPreferredSize(new Dimension(0, 460));

		page1.add("Center", PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, propertiesAndCondition),
				featureProcedure));

		page1.setOpaque(false);
		addPage(page1);
	}

	private synchronized void regenerateFeature() {
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
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel));
		});
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(new ValidationGroup().addValidationElement(restrictionBiomes),
				new BlocklyAggregatedValidationResult(compileNotesPanel.getCompileNotes()));
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		generateCondition.refreshListKeepSelected();
	}

	@Override protected void openInEditingMode(Feature feature) {
		generationStep.setSelectedItem(feature.generationStep);
		restrictionBiomes.setListElements(feature.restrictionBiomes);
		generateCondition.setSelectedProcedure(feature.generateCondition);

		blocklyPanel.setXMLDataOnly(feature.featurexml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(feature.featurexml);
			blocklyPanel.triggerEventFunction();
		});
	}

	@Override public Feature getElementFromGUI() {
		Feature feature = new Feature(modElement);
		feature.generationStep = (String) generationStep.getSelectedItem();
		feature.restrictionBiomes = restrictionBiomes.getListElements();
		feature.generateCondition = generateCondition.getSelectedProcedure();

		feature.featurexml = blocklyPanel.getXML();

		return feature;
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-feature");
	}

	@Override public boolean isInitialXMLValid() {
		return false;
	}

}
