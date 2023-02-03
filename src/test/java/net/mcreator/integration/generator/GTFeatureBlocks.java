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

package net.mcreator.integration.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Feature;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTFeatureBlocks {
	private static final List<String> specialCases = List.of("block_predicate_not", "int_provider_clamped",
			"block_predicate_all_of", "block_predicate_any_of");

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		// silently skip if features are not supported by this generator
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.FEATURE)
				== GeneratorStats.CoverageStatus.NONE) {
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.FEATURE);

		for (ToolboxBlock featureBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!generatorBlocks.contains(featureBlock.machine_name)) {
				LOG.warn("[" + generatorName + "] Skipping feature block that is not defined by generator: "
						+ featureBlock.machine_name);
				continue;
			}

			if (featureBlock.toolboxXML == null) {
				LOG.warn("[" + generatorName + "] Skipping feature block without default XML defined: "
						+ featureBlock.machine_name);
				continue;
			}

			if (featureBlock.getInputs() != null && !featureBlock.getInputs().isEmpty()) {
				boolean templatesDefined = true;

				if (featureBlock.toolbox_init != null) {
					for (String input : featureBlock.getInputs()) {
						boolean match = false;
						for (String toolboxtemplate : featureBlock.toolbox_init) {
							if (toolboxtemplate.contains("<value name=\"" + input + "\">")) {
								match = true;
								break;
							}
						}

						if (!match) {
							templatesDefined = false;
							break;
						}
					}
				} else {
					templatesDefined = false;
				}

				if (!templatesDefined && !specialCases.contains(featureBlock.machine_name)) {
					LOG.warn("[" + generatorName + "] Skipping feature block with incomplete template: "
							+ featureBlock.machine_name);
					continue;
				}
			}

			if (featureBlock.getFields() != null) {
				int processed = 0;

				for (String field : featureBlock.getFields()) {
					try {
						JsonArray args0 = featureBlock.blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
						for (int i = 0; i < args0.size(); i++) {
							JsonObject arg = args0.get(i).getAsJsonObject();
							if (arg.get("name").getAsString().equals(field)) {
								switch (arg.get("type").getAsString()) {
								case "field_checkbox" -> {
									additionalXML.append("<field name=\"").append(field).append("\">TRUE</field>");
									processed++;
								}
								case "field_number" -> {
									additionalXML.append("<field name=\"").append(field).append("\">4</field>");
									processed++;
								}
								case "field_input" -> {
									additionalXML.append("<field name=\"").append(field).append("\">test</field>");
									processed++;
								}
								case "field_dropdown" -> {
									JsonArray opts = arg.get("options").getAsJsonArray();
									JsonArray opt = opts.get((int) (Math.random() * opts.size())).getAsJsonArray();
									additionalXML.append("<field name=\"").append(field).append("\">")
											.append(opt.get(1).getAsString()).append("</field>");
									processed++;
								}
								}
								break;
							}
						}
					} catch (Exception ignored) {
					}
				}

				if (processed != featureBlock.getFields().size()) {
					LOG.warn("[" + generatorName + "] Skipping feature block with special fields: "
							+ featureBlock.machine_name);
					continue;
				}
			}

			ModElement modElement = new ModElement(workspace, "TestFeatureBlock" + featureBlock.machine_name,
					ModElementType.FEATURE);

			String testXML = featureBlock.toolboxXML;

			// Set block selectors to some value
			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			// Add missing inputs for the hardcoded feature blocks
			switch (featureBlock.machine_name) {
				case "block_predicate_not" -> additionalXML.append("""
						<value name="condition"><block type="block_predicate_is_air"></block></value>""");
				case "int_provider_clamped" -> additionalXML.append("""
						<value name="toClamp"><block type="int_provider_constant"><field name="value">2</field></block></value>""");
				case "block_predicate_all_of", "block_predicate_any_of" -> additionalXML.append("""
						<value name="condition0"><block type="block_predicate_is_air"></block></value>
						<value name="condition1"><block type="block_predicate_is_air"></block></value>""");
			}

			testXML = testXML.replace("<block type=\"" + featureBlock.machine_name + "\">",
					"<block type=\"" + featureBlock.machine_name + "\">" + additionalXML);

			Feature feature = new Feature(modElement);
			feature.generationStep = TestWorkspaceDataProvider.getRandomItem(random,
					ElementUtil.getDataListAsStringArray("generationsteps"));
			feature.restrictionDimensions = random.nextBoolean() ?
					new ArrayList<>() :
					new ArrayList<>(Arrays.asList("Surface", "Nether"));
			feature.restrictionBiomes = new ArrayList<>();
			feature.generateCondition = random.nextBoolean() ? new Procedure("condition1") : null;

			if (featureBlock.type
					== IBlockGenerator.BlockType.PROCEDURAL) { // It's a placement, we test with the lake feature
				feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_lake">
							<value name="fluid"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
							<value name="border"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
						</block></value><next>%s</next></block></xml>""".formatted(testXML);
			} else {
				switch (featureBlock.getOutputType()) {
				// Features are tested with the "In square" placement
				case "Feature" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature">%s</value><next><block type="placement_in_square"></block></next></block></xml>
						""".formatted(testXML);
				// Vertical anchors are tested with the "Height: At constant height" placement
				case "VerticalAnchor" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_simple_block">
							<value name="block"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
						</block></value><next><block type="placement_height_range">
							<value name="height"><block type="height_provider_constant">
								<value name="value">%s</value></block></value></block></next></block></xml>
						""".formatted(testXML);
				// Other output types (Height provider, block predicate, etc.) are tested with an appropriate placement block
				case "HeightProvider" -> feature.featurexml = getXMLFor("placement_height_range", "height", testXML);
				case "BlockPredicate" ->
						feature.featurexml = getXMLFor("placement_block_predicate_filter", "condition", testXML);
				case "IntProvider" -> feature.featurexml = getXMLFor("placement_count", "count", testXML);
				default -> {
					LOG.warn("[" + generatorName + "] Skipping feature block of unrecognized type: "
							+ featureBlock.machine_name);
					continue;
				}
				}
			}

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(feature));
				workspace.getModElementManager().storeModElement(feature);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating procedure block: " + featureBlock.machine_name);
				t.printStackTrace();
			}
		}
	}

	/**
	 * This method returns an XML string to test feature blocks that are neither placements nor features.
	 * The test is performed using the simple block feature and a placement that accepts the block being tested.
	 *
	 * @param placementType The name of the placement used to test the block
	 * @param valueName     The name of the input accepting to which the block is attached
	 * @param testXML       The XML of the block being tested
	 * @return An XML string representing a feature configuration to test the given block
	 */
	private static String getXMLFor(String placementType, String valueName, String testXML) {
		return """
				<xml xmlns="https://developers.google.com/blockly/xml">
				<block type="feature_container" deletable="false" x="40" y="40">
				<value name="feature"><block type="feature_simple_block">
					<value name="block"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
				</block></value><next><block type="%s">
					<value name="%s">%s</value></block></next></block></xml>
				""".formatted(placementType, valueName, testXML);
	}
}
