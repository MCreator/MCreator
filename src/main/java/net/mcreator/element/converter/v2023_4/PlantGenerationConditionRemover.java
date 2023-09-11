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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Feature;
import net.mcreator.element.types.Plant;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlantGenerationConditionRemover implements IConverter {
	private static final Logger LOG = LogManager.getLogger(PlantGenerationConditionRemover.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Plant plant = (Plant) input;
		try {
			String modElementName = input.getModElement().getName();
			if (workspace.getModElementByName(modElementName + "Feature") == null) {
				JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

				// Check if we need to convert the element
				if (definition.has("spawnWorldTypes") && !definition.getAsJsonArray("spawnWorldTypes").isEmpty()
						&& definition.has("generateCondition")) {
					Feature feature = new Feature(
							new ModElement(workspace, modElementName + "Feature", ModElementType.FEATURE));
					feature.generationStep = "VEGETAL_DECORATION";

					// Copy the restriction dimensions
					definition.getAsJsonArray("spawnWorldTypes").iterator()
							.forEachRemaining(e -> feature.restrictionDimensions.add(e.getAsString()));

					// Copy the restriction biomes
					if (definition.has("restrictionBiomes") && !definition.getAsJsonArray("restrictionBiomes")
							.isEmpty()) {
						definition.getAsJsonArray("restrictionBiomes").iterator().forEachRemaining(
								e -> feature.restrictionBiomes.add(
										new BiomeEntry(workspace, e.getAsJsonObject().get("value").getAsString())));
					}

					// Copy the generation condition
					feature.generateCondition = new Procedure(
							definition.getAsJsonObject("generateCondition").get("name").getAsString());

					// Plant parameters that affect generation
					String plantType = plant.plantType;
					String genType = plant.generationType;
					boolean genAtAnyHeight = plant.generateAtAnyHeight;
					boolean isRarer = plantType.equals("growapable") || (
							(plantType.equals("normal") || plantType.equals("double")) && genType.equals("Flower"));
					boolean onMotionBlocking = plantType.equals("growapable") || (
							(plantType.equals("normal") || plantType.equals("double")) && genType.equals("Grass"));

					String patchXML = getPatchXML(plantType, plant.patchSize, modElementName);
					String placementXML = getPlacementXML(plant.frequencyOnChunks, isRarer, genAtAnyHeight,
							onMotionBlocking);
					feature.featurexml = """
							<xml><block type="feature_container" deletable="false" x="40" y="40">
							<value name="feature">%s</value>
							<next>%s</next></block></xml>
							""".formatted(patchXML, placementXML);

					feature.getModElement()
							.setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
					workspace.getModElementManager().storeModElementPicture(feature);
					workspace.addModElement(feature.getModElement());
					workspace.getGenerator().generateElement(feature);
					workspace.getModElementManager().storeModElement(feature);

					// Clear the restriction dimensions of the plant, so that it doesn't generate anymore
					plant.spawnWorldTypes.clear();
				}
			}
		} catch (Exception e) {
			LOG.warn("Could not remove generation condition from plant: " + input.getModElement().getName());
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 48;
	}

	// Returns the XML for the random patch block attached to the feature starting point
	private static String getPatchXML(String plantType, int patchSize, String modElementName) {
		if (plantType.equals("growapable")) {
			return """
					<block type="feature_random_patch" inline="false">
						<field name="tries">%d</field>
						<field name="xzSpread">7</field>
						<field name="ySpread">3</field>
						<value name="feature"><block type="placed_feature_inline">
							<value name="feature"><block type="feature_block_column_simple">
								<value name="height"><block type="int_provider_biased_to_bottom">
									<field name="min">2</field><field name="max">4</field>
								</block></value>
								<value name="block"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
							</block></value>
							<statement name="placement"><block type="placement_block_predicate_filter">
								<value name="condition"><block type="block_predicate_all_of">
									<mutation inputs="2"></mutation>
									<value name="condition0"><block type="block_predicate_is_air"></block></value>
									<value name="condition1"><block type="block_predicate_would_survive">
										<field name="x">0</field><field name="y">0</field><field name="z">0</field>
										<value name="block"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
									</block></value>
								</block></value>
							</block></statement>
						</block></value>
					</block>
					""".formatted(patchSize, modElementName, modElementName);
		} else {
			return """
					<block type="feature_random_patch_simple">
						<field name="tries">%d</field><field name="xzSpread">7</field><field name="ySpread">3</field>
						<value name="block"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
						<value name="condition"><block type="block_predicate_is_air"></block></value>
					</block>
					""".formatted(patchSize, modElementName);
		}
	}

	// Returns the XML for the feature placement
	private static String getPlacementXML(int frequency, boolean isRarer, boolean generateAtAnyHeight,
			boolean onMotionBlocking) {
		StringBuilder xml = new StringBuilder();
		int blocksToClose = 2; // We always have the "In square" and one height placement blocks to close

		// Add "Repeated x times" block if frequency on chunks isn't 1
		if (frequency != 1) {
			xml.append(
					"<block type=\"placement_count\"><value name=\"count\"><block type=\"int_provider_constant\"><field name=\"value\">");
			xml.append(frequency);
			xml.append("</field></block></value><next>");
			blocksToClose++;
		}
		// Add "With a chance of 1/32" block if plant is growapable or has flower-like generation
		if (isRarer) {
			xml.append("<block type=\"placement_rarity\"><field name=\"rarity\">32</field><next>");
			blocksToClose++;
		}
		// Add "With random XZ" block
		xml.append("<block type=\"placement_in_square\"><next>");
		// Add the correct height placement block
		if (generateAtAnyHeight) {
			xml.append("""
					<block type="placement_height_uniform">
					<value name="min"><block type="vertical_anchor_above_bottom"><field name="value">0</field></block></value>
					<value name="max"><block type="vertical_anchor_below_top"><field name="value">0</field></block></value>
					<next>""");
		} else if (onMotionBlocking) {
			xml.append("<block type=\"placement_heightmap\"><field name=\"heightmap\">MOTION_BLOCKING</field><next>");
		} else {
			xml.append("<block type=\"placement_heightmap\"><field name=\"heightmap\">WORLD_SURFACE_WG</field><next>");
		}
		// Add the biome filter block
		xml.append("<block type=\"placement_biome_filter\"></block>");
		// Close all the remaining blocks
		xml.append("</next></block>".repeat(blocksToClose));

		return xml.toString();
	}

}
