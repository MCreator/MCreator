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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Structure;
import net.mcreator.io.FileIO;
import net.mcreator.io.Transliteration;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.BiomeListField;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.CompoundValidator;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.ItemListFieldSingleTagValidator;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class StructureGUI extends ModElementGUI<Structure> {

	private MCItemListField ignoreBlocks;

	private final JComboBox<String> surfaceDetectionType = new JComboBox<>(
			new String[] { "WORLD_SURFACE_WG", "WORLD_SURFACE", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING",
					"MOTION_BLOCKING_NO_LEAVES" });

	private final JComboBox<String> terrainAdaptation = new JComboBox<>(
			new String[] { "none", "beard_thin", "beard_box", "bury" });

	private final JComboBox<String> projection = new JComboBox<>(new String[] { "rigid", "terrain_matching" });

	private BiomeListField restrictionBiomes;

	private final JMinMaxSpinner separation_spacing = new JMinMaxSpinner(2, 5, 0, 1000000, 1,
			L10N.t("elementgui.structuregen.separation"), L10N.t("elementgui.structuregen.spacing"));

	private SearchableComboBox<String> structureSelector;

	private final JComboBox<String> generationStep = new JComboBox<>(
			ElementUtil.getDataListAsStringArray("generationsteps"));

	private final ValidationGroup page1group = new ValidationGroup();

	public StructureGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		structureSelector = new SearchableComboBox<>(
				mcreator.getFolderManager().getStructureList().toArray(String[]::new));
		restrictionBiomes = new BiomeListField(mcreator, true);
		ignoreBlocks = new MCItemListField(mcreator, ElementUtil::loadBlocks);

		separation_spacing.setAllowEqualValues(false);

		JPanel pane5 = new JPanel(new BorderLayout(3, 3));

		ComponentUtils.deriveFont(structureSelector, 16);

		if (!isEditingMode()) {
			generationStep.setSelectedItem("SURFACE_STRUCTURES");
			ignoreBlocks.setListElements(List.of(new MItemBlock(modElement.getWorkspace(), "Blocks.STRUCTURE_BLOCK")));
		}

		JPanel params = new JPanel(new GridLayout(8, 2, 50, 2));
		params.setOpaque(false);

		JButton importnbt = new JButton(UIRES.get("18px.add"));
		importnbt.setToolTipText(L10N.t("elementgui.structuregen.import_tooltip"));
		importnbt.setOpaque(false);
		importnbt.addActionListener(e -> {
			File sch = FileDialogs.getOpenDialog(mcreator, new String[] { ".nbt" });
			if (sch != null) {
				String strname = Transliteration.transliterateString(sch.getName().toLowerCase(Locale.ENGLISH))
						.replace(" ", "_");
				FileIO.copyFile(sch, new File(mcreator.getFolderManager().getStructuresDir(), strname));
				structureSelector.removeAllItems();
				mcreator.getFolderManager().getStructureList().forEach(structureSelector::addItem);
				structureSelector.setSelectedItem(FilenameUtilsPatched.removeExtension(strname));
			}
		});

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/biomes_to_spawn"),
				L10N.label("elementgui.structuregen.biomes_to_spawn")));
		params.add(restrictionBiomes);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/separation_spacing"),
				L10N.label("elementgui.structuregen.separation_spacing")));
		params.add(separation_spacing);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/generation_step"),
				L10N.label("elementgui.structuregen.generation_stage")));
		params.add(generationStep);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/ground_detection"),
				L10N.label("elementgui.structuregen.surface_detection_type")));
		params.add(surfaceDetectionType);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/terrain_adaptation"),
				L10N.label("elementgui.structuregen.terrain_adaptation")));
		params.add(terrainAdaptation);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/structure"),
				L10N.label("elementgui.structuregen.select_tooltip")));
		params.add(PanelUtils.centerAndEastElement(structureSelector, importnbt));

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/projection"),
				L10N.label("elementgui.structuregen.projection")));
		params.add(projection);

		params.add(HelpUtils.wrapWithHelpButton(this.withEntry("structure/ignore_blocks"),
				L10N.label("elementgui.structuregen.ignore_blocks")));
		params.add(ignoreBlocks);

		pane5.setOpaque(false);

		pane5.add("Center", PanelUtils.totalCenterInPanel(params));

		restrictionBiomes.setValidator(new CompoundValidator(
				new ItemListFieldValidator(restrictionBiomes, L10N.t("elementgui.structuregen.error_select_biomes")),
				new ItemListFieldSingleTagValidator(restrictionBiomes)));
		page1group.addValidationElement(restrictionBiomes);

		structureSelector.setValidator(() -> {
			if (structureSelector.getSelectedItem() == null || structureSelector.getSelectedItem().isEmpty())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.structuregen.error_select_structure_spawn"));
			return Validator.ValidationResult.PASSED;
		});
		page1group.addValidationElement(structureSelector);

		addPage(pane5);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(structureSelector, mcreator.getFolderManager().getStructureList());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Structure structure) {
		ignoreBlocks.setListElements(structure.ignoredBlocks);
		projection.setSelectedItem(structure.projection);
		surfaceDetectionType.setSelectedItem(structure.surfaceDetectionType);
		terrainAdaptation.setSelectedItem(structure.terrainAdaptation);
		structureSelector.setSelectedItem(structure.structure);
		restrictionBiomes.setListElements(structure.restrictionBiomes);
		separation_spacing.setMinValue(structure.separation);
		separation_spacing.setMaxValue(structure.spacing);
		generationStep.setSelectedItem(structure.generationStep);
	}

	@Override public Structure getElementFromGUI() {
		Structure structure = new Structure(modElement);
		structure.ignoredBlocks = ignoreBlocks.getListElements();
		structure.projection = (String) projection.getSelectedItem();
		structure.surfaceDetectionType = (String) surfaceDetectionType.getSelectedItem();
		structure.terrainAdaptation = (String) terrainAdaptation.getSelectedItem();
		structure.restrictionBiomes = restrictionBiomes.getListElements();
		structure.structure = structureSelector.getSelectedItem();
		structure.separation = separation_spacing.getIntMinValue();
		structure.spacing = separation_spacing.getIntMaxValue();
		structure.generationStep = (String) generationStep.getSelectedItem();
		return structure;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-structure");
	}

}
