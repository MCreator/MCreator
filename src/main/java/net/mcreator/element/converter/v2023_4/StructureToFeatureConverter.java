/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Feature;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class StructureToFeatureConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(StructureToFeatureConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		// Note: onStructureGenerated procedure needs to be manually added to the condition procedure

		try {
			String modElementName = input.getModElement().getName();
			JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			Feature feature = new Feature(new ModElement(workspace,
					ConverterUtils.findSuitableModElementName(workspace, modElementName + "Feature"),
					ModElementType.FEATURE));

			if (definition.has("restrictionBiomes") && !definition.getAsJsonArray("restrictionBiomes")
					.isEmpty()) { // Copy the restriction biomes if there are any
				definition.getAsJsonArray("restrictionBiomes").iterator().forEachRemaining(
						e -> feature.restrictionBiomes.add(
								new BiomeEntry(workspace, e.getAsJsonObject().get("value").getAsString())));
			} else if (definition.has("spawnWorldTypes")) {
				JsonArray spawnWorldTypes = definition.getAsJsonObject().get("spawnWorldTypes").getAsJsonArray();
				if (spawnWorldTypes.size() == 1) { // If there are no restriction biomes, consider restiction dimensions
					String spawnWorldType = spawnWorldTypes.get(0).getAsString();
					if (spawnWorldType.equals("Surface")) {
						feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_overworld"));
					} else if (spawnWorldType.equals("Nether")) {
						feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_nether"));
					} else if (spawnWorldType.equals("End")) {
						feature.restrictionBiomes.add(new BiomeEntry(workspace, "#is_end"));
					} else if (spawnWorldType.startsWith("CUSTOM:")) {
						ModElement modElement = workspace.getModElementByName(
								spawnWorldType.replaceFirst("CUSTOM:", ""));
						if (modElement != null) {
							GeneratableElement generatableElement = modElement.getGeneratableElement();
							if (generatableElement instanceof Dimension dimension) {
								feature.restrictionBiomes.addAll(dimension.biomesInDimension);
							}
						}
					}
				}
			}

			String spawnLocation = definition.get("spawnLocation").getAsString();
			if (spawnLocation.equals("Air")) {
				feature.generationStep = "RAW_GENERATION";
			} else if (spawnLocation.equals("Underground")) {
				feature.generationStep = "UNDERGROUND_STRUCTURES";
			} else {
				feature.generationStep = "SURFACE_STRUCTURES";
			}

			// Copy the generation condition
			if (definition.has("generateCondition") && definition.getAsJsonObject("generateCondition").has("name")) {
				feature.generateCondition = new Procedure(
						definition.getAsJsonObject("generateCondition").get("name").getAsString());
			}

			String structure = definition.get("structure").getAsString();
			int spawnXOffset = definition.has("spawnXOffset") ? definition.get("spawnXOffset").getAsInt() : 0;
			int spawnYOffset = definition.has("spawnHeightOffset") ? definition.get("spawnHeightOffset").getAsInt() : 0;
			int spawnZOffset = definition.has("spawnZOffset") ? definition.get("spawnZOffset").getAsInt() : 0;
			boolean randomlyRotateStructure =
					!definition.has("randomlyRotateStructure") || definition.get("randomlyRotateStructure")
							.getAsBoolean();
			String ignoreBlocks = definition.has("ignoreBlocks") ?
					definition.get("ignoreBlocks").getAsString() :
					"STRUCTURE_BLOCK";
			String patchXML = getFeatureXML(structure, spawnXOffset, spawnYOffset, spawnZOffset,
					randomlyRotateStructure, ignoreBlocks);

			String surfaceDetectionType = definition.has("surfaceDetectionType") ?
					definition.get("surfaceDetectionType").getAsString() :
					"First motion blocking block";
			int spawnProbability = definition.get("spawnProbability").getAsInt();
			int minCountPerChunk = definition.has("minCountPerChunk") ?
					definition.get("minCountPerChunk").getAsInt() :
					1;
			int maxCountPerChunk = definition.has("maxCountPerChunk") ?
					definition.get("maxCountPerChunk").getAsInt() :
					1;
			List<String> restrictionBlocks = definition.has("restrictionBlocks") ?
					definition.getAsJsonArray("restrictionBlocks").asList().stream()
							.map(e -> e.getAsJsonObject().get("value").getAsString()).toList() :
					List.of();
			String placementXML = getPlacementXML(surfaceDetectionType, spawnProbability, minCountPerChunk,
					maxCountPerChunk, spawnLocation, restrictionBlocks);

			feature.featurexml = """
					<xml><block type="feature_container" deletable="false" x="40" y="40">
					<value name="feature">%s</value>
					<next>%s</next></block></xml>
					""".formatted(patchXML, placementXML);

			return feature;
		} catch (Exception e) {
			LOG.warn("Could not convert old structure to a feature", e);
		}

		return null;
	}

	@Override public int getVersionConvertingTo() {
		return 54;
	}

	// Returns the XML for the random patch block attached to the feature starting point
	private static String getFeatureXML(String structure, int ox, int oy, int oz, boolean randomlyRotateStructure,
			String ignoreBlocks) {
		String ignoredBlocks;
		if (ignoreBlocks.equals("STRUCTURE_BLOCK")) {
			ignoredBlocks = "<mutation inputs=\"1\"></mutation><field name=\"block0\">Blocks.STRUCTURE_BLOCK</field>";
		} else if (ignoreBlocks.equals("AIR_AND_STRUCTURE_BLOCK")) {
			ignoredBlocks = "<mutation inputs=\"2\"></mutation><field name=\"block0\">Blocks.STRUCTURE_BLOCK</field><field name=\"block1\">Blocks.AIR</field>";
		} else {
			ignoredBlocks = "<mutation inputs=\"1\"></mutation><field name=\"block0\">Blocks.AIR</field>";
		}
		return """
				<block type="feature_custom_structure">
					<field name="structure">%s</field>
					<field name="x">%d</field>
					<field name="y">%d</field>
					<field name="z">%d</field>
					<field name="random_rotation">%s</field>
					<field name="random_mirror">%s</field>
					<value name="ignored_blocks">
						<block type="block_holderset_list">%s</block>
					</value>
				</block>
				""".formatted(structure, ox, oy, oz, randomlyRotateStructure ? "TRUE" : "FALSE",
				randomlyRotateStructure ? "TRUE" : "FALSE", ignoredBlocks);
	}

	// Returns the XML for the feature placement
	private static String getPlacementXML(String surfaceDetectionTypeRaw, int spawnProbability, int minCountPerChunk,
			int maxCountPerChunk, String spawnLocation, List<String> restrictionBlocks) {
		StringBuilder xml = new StringBuilder();
		int blocksToClose = 0;

		if (spawnProbability <= 0)
			spawnProbability = 1;

		int placementRarity = (int) Math.max(1, Math.round(1000000 / (double) spawnProbability));

		xml.append("""
				<block type="placement_rarity">
					<field name="rarity">%d</field>
					<next>""".formatted(placementRarity));
		blocksToClose++;

		if (minCountPerChunk != 1 || maxCountPerChunk != 1) {
			xml.append("""
					<block type="placement_count">
						<value name="count">
							<block type="int_provider_uniform">
								<field name="min">%d</field>
								<field name="max">%d</field>
							</block>
						</value>
						<next>""".formatted(minCountPerChunk, maxCountPerChunk));
			blocksToClose++;
		}

		xml.append("<block type=\"placement_in_square\"><next>");
		blocksToClose++;

		// Height definition
		String surfaceDetectionType;
		if (surfaceDetectionTypeRaw.equals("First block")) {
			surfaceDetectionType = "WORLD_SURFACE_WG";
		} else {
			surfaceDetectionType = "OCEAN_FLOOR_WG";
		}

		if (spawnLocation.equals("Air")) {
			xml.append("""
					<block type="placement_height_uniform">
						<value name="min">
							<block type="vertical_anchor_absolute">
								<field name="value">63</field>
							</block>
						</value>
						<value name="max">
							<block type="vertical_anchor_below_top">
								<field name="value">0</field>
							</block>
						</value>
						<next>
							<block type="placement_surface_relative_threshold">
								<field name="heightmap">%s</field>
								<field name="min">16</field>
								<field name="max">80</field>
						<next>""".formatted(surfaceDetectionType));
			blocksToClose += 2;
		} else if (spawnLocation.equals("Underground")) {
			xml.append("""
					<block type="placement_height_uniform">
						<value name="min">
							<block type="vertical_anchor_above_bottom">
								<field name="value">8</field>
							</block>
						</value>
						<value name="max">
							<block type="vertical_anchor_absolute">
								<field name="value">128</field>
							</block>
						</value>
						<next>
							<block type="placement_surface_relative_threshold">
								<field name="heightmap">%s</field>
								<field name="min">-192</field>
								<field name="max">-8</field>
						<next>""".formatted(surfaceDetectionType));
			blocksToClose += 2;
		} else { // Surface
			xml.append("""
					<block type="placement_heightmap">
						<field name="heightmap">%s</field>
						<next>""".formatted(surfaceDetectionType));
			blocksToClose++;
		}

		// Restriction blocks
		if (!restrictionBlocks.isEmpty()) {
			StringBuilder blocks = new StringBuilder();
			int idx = 0;
			for (String block : restrictionBlocks) {
				blocks.append("<field name=\"block").append(idx).append("\">").append(block).append("</field>");
				idx++;
			}
			xml.append("""
					<block type="placement_block_predicate_filter">
						<value name="condition">
							<block type="block_predicate_matching_blocks">
								<field name="x">0</field>
								<field name="y">-1</field>
								<field name="z">0</field>
								<value name="blockSet">
									<block type="block_holderset_list">
										<mutation inputs="%d"></mutation>
										%s
									</block>
								</value>
							</block>
						</value>
						<next>""".formatted(restrictionBlocks.size(), blocks.toString()));
			blocksToClose++;
		}

		// Add the biome filter block (no need for blocksToClose as it does not open next)
		xml.append("<block type=\"placement_biome_filter\"></block>");

		// Close all the remaining blocks
		xml.append("</next></block>".repeat(blocksToClose));

		return xml.toString();
	}

}
