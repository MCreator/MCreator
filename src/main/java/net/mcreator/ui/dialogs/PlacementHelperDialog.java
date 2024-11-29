/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.validators.ItemListFieldSingleTagValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class PlacementHelperDialog extends BlocklyHelperDialog {
	private final JSpinner rarity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
	private final JMinMaxSpinner frequencyOnChunk = new JMinMaxSpinner(1, 4, 0, 256, 1).allowEqualValues();

	private final TranslatedComboBox heightPlacementType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("HEIGHTMAP", "dialog.tools.placement_helper.on_heightmap"),
			Map.entry("HEIGHT RANGE", "dialog.tools.placement_helper.height_range"),
			Map.entry("ON EVERY LAYER", "dialog.tools.placement_helper.on_every_layer")
			//@formatter:on
	);

	// Height range specific settings
	private final JMinMaxSpinner heightRange = new JMinMaxSpinner(0, 64, -1024, 1024, 1).allowEqualValues();
	private final TranslatedComboBox distributionType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("uniform", "dialog.tools.placement_helper.uniform"),
			Map.entry("triangular", "dialog.tools.placement_helper.triangular")
			//@formatter:on
	);

	// Heightmap specific settings
	private final TranslatedComboBox heightmapType = new TranslatedComboBox(
			//@formatter:off
			Map.entry("OCEAN_FLOOR_WG", "dialog.tools.placement_helper.ocean_floor_wg"),
			Map.entry("OCEAN_FLOOR", "dialog.tools.placement_helper.ocean_floor"),
			Map.entry("WORLD_SURFACE_WG", "dialog.tools.placement_helper.world_surface_wg"),
			Map.entry("WORLD_SURFACE", "dialog.tools.placement_helper.world_surface"),
			Map.entry("MOTION_BLOCKING", "dialog.tools.placement_helper.motion_blocking"),
			Map.entry("MOTION_BLOCKING_NO_LEAVES", "dialog.tools.placement_helper.motion_blocking_no_leaves")
			//@formatter:on
	);
	private final JSpinner maxWaterDepth = new JSpinner(new SpinnerNumberModel(-1, -1, 1024, 1));
	private final JCheckBox onlyPlaceUnderwater = L10N.checkbox("elementgui.common.enable");

	// Offset and conditions
	private final JMinMaxSpinner verticalOffset = new JMinMaxSpinner(0, 0, -15, 15, 1).allowEqualValues();
	private final MCItemHolder blockSurvivalCondition;
	private final JCheckBox requireSolidBelow = L10N.checkbox("elementgui.common.enable");
	private final MCItemListField requiredBlocks;


	public PlacementHelperDialog(BlocklyPanel blocklyPanel, MCreator mcreator) {
		super(blocklyPanel, mcreator, L10N.t("dialog.tools.placement_helper.title"),
				L10N.t("dialog.tools.placement_helper.confirm"), 1000, 400);
		blockSurvivalCondition = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		requiredBlocks = new MCItemListField(mcreator, ElementUtil::loadBlocks, false, true);
		requiredBlocks.setPreferredSize(new Dimension(280, -1));
		requiredBlocks.setValidator(new ItemListFieldSingleTagValidator(requiredBlocks));
		requiredBlocks.addAdditionalTagSuggestions("minecraft:dirt", "minecraft:sand", "minecraft:terracotta",
				"minecraft:ice", "minecraft:nylium", "minecraft:base_stone_overworld", "minecraft:base_stone_nether");

		// Rarity and frequency on single chunk settings
		JPanel rarityFrequencySettings = new JPanel(new GridLayout(2, 2, 4, 4));
		rarityFrequencySettings.setOpaque(false);

		rarityFrequencySettings.add(L10N.label("dialog.tools.placement_helper.rarity"));
		rarityFrequencySettings.add(rarity);
		rarityFrequencySettings.add(L10N.label("dialog.tools.placement_helper.frequency_on_chunk"));
		rarityFrequencySettings.add(frequencyOnChunk);

		rarityFrequencySettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("dialog.tools.placement_helper.rarity_and_frequency"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		// Height settings
		JPanel heightPlacementSettings = new JPanel(new GridLayout(1, 2, 4, 4));
		heightPlacementSettings.add(L10N.label("dialog.tools.placement_helper.height_placement_type"));
		heightPlacementSettings.add(heightPlacementType);

		CardLayout heightTypeLayout = new CardLayout();
		JPanel heightCardPanel = new JPanel(heightTypeLayout);

		// Height range page
		JPanel heightRangeSettings = new JPanel(new GridLayout(2, 2, 4, 4));

		heightRangeSettings.add(L10N.label("dialog.tools.placement_helper.at_height"));
		heightRangeSettings.add(heightRange);
		heightRangeSettings.add(L10N.label("dialog.tools.placement_helper.distribution_type"));
		heightRangeSettings.add(distributionType);

		// Heightmap page
		JPanel heightmapSettings = new JPanel(new GridLayout(3, 2, 4, 4));

		heightmapSettings.add(L10N.label("dialog.tools.placement_helper.heightmap_type"));
		heightmapSettings.add(heightmapType);
		heightmapSettings.add(L10N.label("dialog.tools.placement_helper.max_water_depth"));
		heightmapSettings.add(maxWaterDepth);
		heightmapSettings.add(L10N.label("dialog.tools.placement_helper.only_place_underwater"));
		heightmapSettings.add(onlyPlaceUnderwater);
		heightmapType.addActionListener(e -> {
			String heightType = heightmapType.getSelectedItem();
			onlyPlaceUnderwater.setEnabled("OCEAN_FLOOR_WG".equals(heightType) || "OCEAN_FLOOR".equals(heightType));
		});

		heightCardPanel.add(heightmapSettings, "HEIGHTMAP");
		heightCardPanel.add(heightRangeSettings, "HEIGHT RANGE");
		heightCardPanel.add(new JPanel(), "ON EVERY LAYER");
		heightTypeLayout.show(heightCardPanel, "HEIGHTMAP");
		heightPlacementType.addActionListener(
				e -> heightTypeLayout.show(heightCardPanel, heightPlacementType.getSelectedItem()));
		JPanel heightSettings = new JPanel(new BorderLayout(5, 5));
		heightSettings.add(PanelUtils.northAndCenterElement(heightPlacementSettings, heightCardPanel, 2, 2));

		heightSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("dialog.tools.placement_helper.height_settings"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		// Offset and conditions
		JPanel conditionsPanel = new JPanel(new GridLayout(4, 2, 4, 4));

		conditionsPanel.add(L10N.label("dialog.tools.placement_helper.vertical_offset"));
		conditionsPanel.add(verticalOffset);
		conditionsPanel.add(L10N.label("dialog.tools.placement_helper.block_survival_condition"));
		conditionsPanel.add(PanelUtils.centerInPanel(blockSurvivalCondition));
		conditionsPanel.add(L10N.label("dialog.tools.placement_helper.require_solid_block"));
		conditionsPanel.add(requireSolidBelow);
		conditionsPanel.add(L10N.label("dialog.tools.placement_helper.required_blocks"));
		conditionsPanel.add(requiredBlocks);

		conditionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("dialog.tools.placement_helper.offset_and_conditions"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		this.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndEastElement(
				PanelUtils.northAndCenterElement(rarityFrequencySettings, PanelUtils.pullElementUp(heightSettings)),
				PanelUtils.pullElementUp(conditionsPanel))));
		this.setVisible(true);
	}

	@Override public AggregatedValidationResult getValidationResult() {
		return new AggregatedValidationResult(requiredBlocks);
	}

	@Override protected String getXML() {
		StringBuilder xml = new StringBuilder("<xml>");
		int blocksToClose = 0;

		// Handle feature rarity
		int rarityValue = (int) rarity.getValue();
		if (rarityValue != 1) {
			xml.append("""
				<block type="placement_rarity">
					<field name="rarity">%d</field>
					<next>""".formatted(rarityValue));
			blocksToClose++;
		}

		// Handle frequency on single chunk
		int frequencyMin = frequencyOnChunk.getIntMinValue();
		int frequencyMax = frequencyOnChunk.getIntMaxValue();
		String heightPlacement = heightPlacementType.getSelectedItem();
		String frequencyIntProvider = toConstantOrUniformProvider(frequencyMin, frequencyMax);

		if ("ON EVERY LAYER".equals(heightPlacement)) {
			xml.append("""
					<block type="placement_count_on_every_layer">
						<value name="count">%s</value>
						<next>""".formatted(frequencyIntProvider));
			blocksToClose++;
		}
		else if (frequencyMin != 1 || frequencyMax != 1) {
			xml.append("""
					<block type="placement_count">
						<value name="count">%s</value>
						<next>""".formatted(frequencyIntProvider));
			blocksToClose++;
		}

		// Handle the "With random XZ" placement
		if (!"ON EVERY LAYER".equals(heightPlacement)) { // "On every layer" already randomizes XZ pos
			xml.append("<block type=\"placement_in_square\"><next>");
			blocksToClose++;
		}

		// Handle height distribution
		if ("HEIGHT RANGE".equals(heightPlacement)) {
			int heightMin = heightRange.getIntMinValue();
			int heightMax = heightRange.getIntMaxValue();
			xml.append("""
					<block type="placement_height_%s">
						<value name="min">
							<block type="vertical_anchor_absolute">
								<field name="value">%d</field>
							</block>
						</value>
						<value name="max">
							<block type="vertical_anchor_absolute">
								<field name="value">%d</field>
							</block>
						</value>
						<next>""".formatted(distributionType.getSelectedItem(), heightMin, heightMax));
			blocksToClose++;
		}
		else if ("HEIGHTMAP".equals(heightPlacement)) {
			int maxWaterDepthValue = (int) maxWaterDepth.getValue();
			if (maxWaterDepthValue != -1) {
				xml.append("""
					<block type="placement_surface_water_depth">
						<field name="depth">%d</field>
						<next>""".formatted(maxWaterDepthValue));
				blocksToClose++;
			}

			xml.append("""
					<block type="placement_heightmap">
						<field name="heightmap">%s</field>
						<next>""".formatted(heightmapType.getSelectedItem()));
			blocksToClose++;

			if (onlyPlaceUnderwater.isSelected() && onlyPlaceUnderwater.isEnabled()) {
				xml.append("""
					<block type="placement_surface_relative_threshold">
						<field name="heightmap">WORLD_SURFACE_WG</field>
						<field name="min">-4096</field>
						<field name="max">0</field>
						<next>""");
				blocksToClose++;
			}
		}

		// Handle conditions and offset
		int minYOffset = verticalOffset.getIntMinValue();
		int maxYOffset = verticalOffset.getIntMaxValue();
		if (minYOffset != 0 || maxYOffset != 0) {
			xml.append("""
					<block type="placement_offset">
						<value name="xz">
							<block type="int_provider_constant"><field name="value">0</field></block>
						</value>
						<value name="y">%s</value>
						<next>""".formatted(toConstantOrUniformProvider(minYOffset, maxYOffset)));
			blocksToClose++;
		}

		if (blockSurvivalCondition.containsItemOrAir()) {
			xml.append("""
					<block type="placement_block_survival_filter">
						<value name="block">
							<block type="blockstate_selector"><mutation inputs="0"/>
								<field name="block">%s</field>
							</block>
						</value>
						<next>""".formatted(blockSurvivalCondition.getBlock().getUnmappedValue()));
			blocksToClose++;
		}

		if (requireSolidBelow.isSelected()) {
			xml.append("""
					<block type="placement_block_predicate_filter">
						<value name="condition">
							<block type="block_predicate_solid">
								<field name="x">0</field>
								<field name="y">-1</field>
								<field name="z">0</field>
							</block>
						</value>
						<next>""");
			blocksToClose++;
		}

		if (!requiredBlocks.getListElements().isEmpty()) {
			java.util.List<MItemBlock> blocks = requiredBlocks.getListElements();
			xml.append("""
				<block type="placement_block_predicate_filter">
					<value name="condition">
						<block type="block_predicate_matching_blocks">
							<field name="x">0</field>
							<field name="y">-1</field>
							<field name="z">0</field>
							<value name="blockSet">%s</value>
						</block>
					</value>
					<next>""".formatted(toTagOrListBlockHolderset(blocks)));
			blocksToClose++;
		}

		// Add the biome filter block (no need for blocksToClose as it does not open next)
		xml.append("<block type=\"placement_biome_filter\"></block>");

		// Close all the remaining blocks
		xml.append("</next></block>".repeat(blocksToClose)).append("</xml>");

		return xml.toString();
	}

	private String toConstantOrUniformProvider(int min, int max) {
		return min == max ? """
				<block type="int_provider_constant">
					<field name="value">%d</field>
				</block>""".formatted(min) : """
				<block type="int_provider_uniform">
					<field name="min">%d</field>
					<field name="max">%d</field>
				</block>""".formatted(min, max);
	}

	private String toTagOrListBlockHolderset(java.util.List<MItemBlock> blocks) {
		if (blocks.getFirst().getUnmappedValue().startsWith("TAG:")) {
			return """
					<block type="block_holderset_tag">
						<field name="tag">%s</field>
					</block>""".formatted(blocks.getFirst().getUnmappedValue().substring(4));
		} else {
			StringBuilder result = new StringBuilder();
			result.append("<block type=\"block_holderset_list\"><mutation inputs=\"%d\"></mutation>".formatted(blocks.size()));
			for (int i = 0; i < blocks.size(); i++) {
				result.append("<field name=\"block%d\">%s</field>".formatted(i, blocks.get(i).getUnmappedValue()));
			}
			result.append("</block>");
			return result.toString();
		}
	}
}
