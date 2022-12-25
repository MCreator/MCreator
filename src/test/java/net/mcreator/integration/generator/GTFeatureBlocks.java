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
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTFeatureBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.FEATURE)
				== GeneratorStats.CoverageStatus.NONE) {
			LOG.warn("[" + generatorName
					+ "] Skipping feature blocks test as the current generator does not support them.");
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getFeatureProcedures();

		for (ToolboxBlock featureBlock : BlocklyLoader.INSTANCE.getFeatureBlockLoader().getDefinedBlocks().values()) {
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

				if (!templatesDefined) {
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
					LOG.warn("[" + generatorName + "] Skipping procedure block with special fields: "
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

			testXML = testXML.replace("<block type=\"" + featureBlock.machine_name + "\">",
					"<block type=\"" + featureBlock.machine_name + "\">" + additionalXML);

			Feature feature = new Feature(modElement);
			feature.generationStep = TestWorkspaceDataProvider.getRandomItem(random, ElementUtil.getDataListAsStringArray("generationsteps"));
			feature.restrictionDimensions = random.nextBoolean() ? new ArrayList<>() :
					new ArrayList<>(Arrays.asList("Surface", "Nether"));
			feature.restrictionBiomes = new ArrayList<>();
			feature.generateCondition = random.nextBoolean() ? new Procedure("condition1") : null;

			if (featureBlock.type == IBlockGenerator.BlockType.PROCEDURAL) { // It's a placement, we test with the lake feature
				feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_lake">
							<value name="fluid"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
							<value name="border"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
						</block></value><next>%s</next></block></xml>""".formatted(testXML);
			} else if (featureBlock.blocklyJSON.getAsJsonObject().get("output").getAsString().equals("Feature")) { // It's a feature, we test with in square placement
				feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature">%s</value><next><block type="placement_in_square"></block></next></block></xml>
						""".formatted(testXML);
			} else if (featureBlock.blocklyJSON.getAsJsonObject().get("output").getAsString().equals("HeightProvider")) { // Testing height providers
				feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_simple_block">
							<value name="block"><block type="mcitem_allblocks"><field name="value">Blocks.STONE</field></block></value>
						</block></value><next><block type="placement_height_range">
							<value name="height">%s</value></block></next></block></xml>
						""".formatted(testXML);
			} else {
				LOG.warn("[" + generatorName + "] Skipping feature block of unrecognized type: "
						+ featureBlock.machine_name);
				continue;
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
}
