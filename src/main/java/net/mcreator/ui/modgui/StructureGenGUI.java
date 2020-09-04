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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.types.Structure;
import net.mcreator.io.FileIO;
import net.mcreator.io.Transliteration;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.minecraft.DimensionListField;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Locale;

public class StructureGenGUI extends ModElementGUI<Structure> {

	private DimensionListField spawnWorldTypes;

	private final JComboBox<String> spawnLocation = new JComboBox<>(new String[] { "Ground", "Air", "Underground" });
	private final JComboBox<String> ignoreBlocks = new JComboBox<>(
			new String[] { "STRUCTURE_BLOCK", "AIR_AND_STRUCTURE_BLOCK", "AIR" });

	private final JSpinner spawnProbability = new JSpinner(new SpinnerNumberModel(10000, 0, 1000000, 1));

	private final JComboBox<String> surfaceDetectionType = new JComboBox<>(
			new String[] { "First motion blocking block", "First block" });

	private final JSpinner spawnHeightOffset = new JSpinner(new SpinnerNumberModel(0, -128, 128, 1));

	private final JSpinner minCountPerChunk = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));
	private final JSpinner maxCountPerChunk = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));

	private BiomeListField restrictionBiomes;
	private MCItemListField restrictionBlocks;

	private final JComboBox<String> structureSelector = new JComboBox<>();

	private final JCheckBox randomlyRotateStructure = new JCheckBox("Check to enable");

	private final ValidationGroup page1group = new ValidationGroup();

	private ProcedureSelector onStructureGenerated;

	private ProcedureSelector generateCondition;

	public StructureGenGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onStructureGenerated = new ProcedureSelector(this.withEntry("structure/on_generated"), mcreator,
				"On structure instance generated", ProcedureSelector.Side.SERVER,
				Dependency.fromString("x:number/y:number/z:number/world:world"));

		generateCondition = new ProcedureSelector(this.withEntry("structure/condition"), mcreator,
				"Additional generation condition", ProcedureSelector.Side.SERVER, true, VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world"))
				.setDefaultName("(no additional condition)");

		restrictionBlocks = new MCItemListField(mcreator, ElementUtil::loadBlocks);
		restrictionBiomes = new BiomeListField(mcreator);
		spawnWorldTypes = new DimensionListField(mcreator);
		spawnWorldTypes.setListElements(Collections.singletonList("Surface"));

		JPanel pane5 = new JPanel(new BorderLayout(3, 3));

		spawnProbability.setOpaque(false);

		ComponentUtils.deriveFont(structureSelector, 16);

		JPanel params = new JPanel(new GridLayout(11, 2, 50, 7));
		params.setOpaque(false);

		JButton importnbt = new JButton(UIRES.get("18px.add"));
		importnbt.setToolTipText("<html>Click this to import nbt structure file<br>"
				+ "Use Structures section in Workspace tab for more options.");
		importnbt.setOpaque(false);
		importnbt.addActionListener(e -> {
			File sch = FileDialogs.getOpenDialog(mcreator, new String[] { ".nbt" });
			if (sch != null) {
				String strname = Transliteration.transliterateString(sch.getName().toLowerCase(Locale.ENGLISH))
						.replace(" ", "_");
				FileIO.copyFile(sch, new File(mcreator.getWorkspace().getFolderManager().getStructuresDir(), strname));
				structureSelector.removeAllItems();
				mcreator.getWorkspace().getFolderManager().getStructureList().forEach(structureSelector::addItem);
				structureSelector.setSelectedItem(FilenameUtils.removeExtension(strname));
			}
		});

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/structure"), new JLabel(
				"<html>Structure to spawn: <br>"
						+ "<small>For more structure import options, use the structures section of the workspace tab")));
		params.add(PanelUtils.centerAndEastElement(structureSelector, importnbt));

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/probability"), new JLabel(
				"<html>Spawn probability (number of structures per 1.000.000 chunks):<br><small>Setting this value too high might cause slow world generation")));
		params.add(spawnProbability);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/group_size"),
				new JLabel("Structure group size (min, max): ")));
		params.add(PanelUtils.gridElements(1, 2, 5, 5, minCountPerChunk, maxCountPerChunk));

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/random_rotation"),
				new JLabel("Randomize structure rotation: ")));
		params.add(randomlyRotateStructure);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/ignore_blocks"),
				new JLabel("<html>Blocks to ignore when placing:")));
		params.add(ignoreBlocks);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/ground_detection"), new JLabel(
				"<html>Type of reference ground detection:<br><small>"
						+ "Top down detection until the condition is met")));
		params.add(surfaceDetectionType);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/spawn_location"),
				new JLabel("<html>Spawn location: <br><small>For nether type dimensions, use underground only")));
		params.add(spawnLocation);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/height_offset"), new JLabel(
				"<html>Spawn height offset:<br><small>Use this is your structure if floating or buried in the ground")));
		params.add(spawnHeightOffset);

		params.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/spawn_world_types"), new JLabel("Spawn world types: ")));
		params.add(spawnWorldTypes);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_blocks"),
				new JLabel("Restrics to block type (leave empty for no restriction): ")));
		params.add(restrictionBlocks);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/restrict_to_biomes"),
				new JLabel("Rectrict to biome types (leave empty for no restriction): ")));
		params.add(restrictionBiomes);

		randomlyRotateStructure.setSelected(true);
		randomlyRotateStructure.setOpaque(false);
		randomlyRotateStructure.setForeground(Color.white);

		pane5.setOpaque(false);

		pane5.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(params,
				PanelUtils.join(FlowLayout.LEFT, generateCondition, onStructureGenerated), 20, 20)));

		spawnWorldTypes.setValidator(new ItemListFieldValidator(spawnWorldTypes, "Select at least one world type!"));
		page1group.addValidationElement(spawnWorldTypes);

		addPage(pane5);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		onStructureGenerated.refreshListKeepSelected();

		generateCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(structureSelector,
				mcreator.getWorkspace().getFolderManager().getStructureList());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (structureSelector.getSelectedItem() == null || structureSelector.getSelectedItem().toString().equals(""))
			return new AggregatedValidationResult.FAIL(
					"You need to select a structure that will be spawned by this structure spawn in order to proceed.");
		else
			return new AggregatedValidationResult(page1group);

	}

	@Override public void openInEditingMode(Structure structure) {
		spawnProbability.setValue(structure.spawnProbability);
		spawnHeightOffset.setValue(structure.spawnHeightOffset);
		minCountPerChunk.setValue(structure.minCountPerChunk);
		maxCountPerChunk.setValue(structure.maxCountPerChunk);
		spawnLocation.setSelectedItem(structure.spawnLocation);
		ignoreBlocks.setSelectedItem(structure.ignoreBlocks);
		surfaceDetectionType.setSelectedItem(structure.surfaceDetectionType);
		spawnWorldTypes.setListElements(structure.spawnWorldTypes);
		randomlyRotateStructure.setSelected(structure.randomlyRotateStructure);
		structureSelector.setSelectedItem(structure.structure);
		restrictionBlocks.setListElements(structure.restrictionBlocks);
		restrictionBiomes.setListElements(structure.restrictionBiomes);
		onStructureGenerated.setSelectedProcedure(structure.onStructureGenerated);
		generateCondition.setSelectedProcedure(structure.generateCondition);
	}

	@Override public Structure getElementFromGUI() {
		Structure structure = new Structure(modElement);
		structure.spawnProbability = (int) spawnProbability.getValue();
		structure.spawnHeightOffset = (int) spawnHeightOffset.getValue();
		structure.minCountPerChunk = (int) minCountPerChunk.getValue();
		structure.maxCountPerChunk = (int) maxCountPerChunk.getValue();
		structure.spawnWorldTypes = spawnWorldTypes.getListElements();
		structure.spawnLocation = (String) spawnLocation.getSelectedItem();
		structure.ignoreBlocks = (String) ignoreBlocks.getSelectedItem();
		structure.surfaceDetectionType = (String) surfaceDetectionType.getSelectedItem();
		structure.restrictionBlocks = restrictionBlocks.getListElements();
		structure.randomlyRotateStructure = randomlyRotateStructure.isSelected();
		structure.restrictionBiomes = restrictionBiomes.getListElements();
		structure.structure = (String) structureSelector.getSelectedItem();
		structure.onStructureGenerated = onStructureGenerated.getSelectedProcedure();
		structure.generateCondition = generateCondition.getSelectedProcedure();
		return structure;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-structure");
	}

}
