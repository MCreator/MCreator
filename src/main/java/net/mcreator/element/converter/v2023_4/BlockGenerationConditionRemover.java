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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Feature;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BlockGenerationConditionRemover implements IConverter {
	private static final Logger LOG = LogManager.getLogger(BlockGenerationConditionRemover.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Block block = (Block) input;
		try {
			String modElementName = input.getModElement().getName();
			if (workspace.getModElementByName(modElementName + "Feature") == null) {
				JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

				// Check if we need to convert the element
				if (definition.has("spawnWorldTypes") && !definition.getAsJsonArray("spawnWorldTypes").isEmpty()
						&& definition.has("generateCondition")) {
					Feature feature = new Feature(
							new ModElement(workspace, modElementName + "Feature", ModElementType.FEATURE));
					feature.generationStep = "UNDERGROUND_ORES";

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

					// Generate the feature XML
					String placementType = block.generationShape.equals("UNIFORM") ? "uniform" : "triangular";

					String oreXML = getOreXML(workspace, modElementName, block.frequencyOnChunk, block.blocksToReplace);
					String placementXML = """
							<block type="placement_count">
								<value name="count"><block type="int_provider_constant"><field name="value">%d</field></block></value>
							<next>
							<block type="placement_in_square"><next>
							<block type="placement_height_%s">
								<value name="min"><block type="vertical_anchor_absolute"><field name="value">%d</field></block></value>
								<value name="max"><block type="vertical_anchor_absolute"><field name="value">%d</field></block></value>
							<next>
							<block type="placement_biome_filter"></block>
							</next></block></next></block></next></block>""".formatted(block.frequencyPerChunks,
							placementType, block.minGenerateHeight, block.maxGenerateHeight);
					feature.featurexml = """
							<xml><block type="feature_container" deletable="false" x="40" y="40">
							<value name="feature">%s</value>
							<next>%s</next></block></xml>
							""".formatted(oreXML, placementXML);

					feature.getModElement()
							.setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
					workspace.getModElementManager().storeModElementPicture(feature);
					workspace.addModElement(feature.getModElement());
					workspace.getGenerator().generateElement(feature);
					workspace.getModElementManager().storeModElement(feature);

					// Clear the restriction dimensions of the plant, so that it doesn't generate anymore
					block.spawnWorldTypes.clear();
				}
			}
		} catch (Exception e) {
			LOG.warn("Could not remove generation condition from block: " + input.getModElement().getName());
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 48;
	}

	// Returns the XML for the ore feature block attached to the starting point
	private static String getOreXML(Workspace workspace, String elementName, int frequency,
			List<MItemBlock> blocksToReplace) {
		StringBuilder xml = new StringBuilder();
		xml.append("""
				<block type="feature_ore">
				<mutation inputs="%d"></mutation>
				<field name="discardOnAirChance">0</field>
				<field name="size">%d</field>""".formatted(blocksToReplace.size(), frequency));
		for (int i = 0; i < blocksToReplace.size(); i++) {
			xml.append(getOreTargetValue(workspace, i, blocksToReplace.get(i), elementName));
		}
		xml.append("</block>");
		return xml.toString();
	}

	// Returns the XML for an appropriate ore rule test
	private static String getOreTargetValue(Workspace workspace, int i, MItemBlock block, String elementName) {
		if (block.getUnmappedValue().startsWith("TAG:")) {
			// Add a tag match test if the block to replace is a tag
			return """
					<value name="target%d"><block type="ore_target">
						<value name="target"><block type="rule_test_tag_match"><field name="tag">%s</field></block></value>
						<value name="state"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
					</block></value>""".formatted(i, block.getUnmappedValue().substring(4), elementName);
		} else {
			String mappedValue = new NameMapper(workspace, "blocksitems").getMapping(block.getUnmappedValue(), 1);
			if (mappedValue.startsWith("#")) {
				// Add a tag match test if the block is a wildcard
				return """
						<value name="target%d"><block type="ore_target">
							<value name="target"><block type="rule_test_tag_match"><field name="tag">%s</field></block></value>
							<value name="state"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
						</block></value>""".formatted(i, mappedValue.substring(1), elementName);
			} else {
				// Add a blockstate match test otherwise
				return """
						<value name="target%d"><block type="ore_target">
							<value name="target"><block type="rule_test_blockstate_match">
								<value name="blockstate"><block type="mcitem_allblocks"><field name="value">%s</field></block></value>
							</block></value>
							<value name="state"><block type="mcitem_allblocks"><field name="value">CUSTOM:%s</field></block></value>
						</block></value>""".formatted(i, block.getUnmappedValue(), elementName);
			}

		}
	}
}
