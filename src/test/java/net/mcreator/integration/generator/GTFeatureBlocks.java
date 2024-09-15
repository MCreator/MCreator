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

import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Feature;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTFeatureBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.FEATURE);

		for (ToolboxBlock featureBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.FEATURE)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(featureBlock, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(featureBlock))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(featureBlock, workspace, random, additionalXML))
				continue; // failed to populate all fields

			ModElement modElement = new ModElement(workspace, "TestFeatureBlock" + featureBlock.getMachineName(),
					ModElementType.FEATURE);

			String testXML = featureBlock.getToolboxTestXML();

			// Set block selectors to some value
			testXML = testXML.replace(
					"<block type=\"blockstate_selector\"><mutation inputs=\"0\"/><field name=\"block\"></field></block>",
					"<block type=\"blockstate_selector\"><mutation inputs=\"0\"/><field name=\"block\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			testXML = testXML.replace("<block type=\"" + featureBlock.getMachineName() + "\">",
					"<block type=\"" + featureBlock.getMachineName() + "\">" + additionalXML);

			Feature feature = new Feature(modElement);
			feature.generationStep = TestWorkspaceDataProvider.getRandomItem(random,
					ElementUtil.getDataListAsStringArray("generationsteps"));
			feature.restrictionBiomes = new ArrayList<>();
			feature.generateCondition = null;

			if (featureBlock.getType()
					== IBlockGenerator.BlockType.PROCEDURAL) { // It's a placement, we test with the empty feature
				feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_no_op"></block></value>
						<next>%s</next></block></xml>""".formatted(testXML);
			} else {
				switch (featureBlock.getOutputTypeForTests()) {
				case "Feature" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature">%s</value></block></xml>
						""".formatted(testXML);
				// Placed features are tested with the "Random patch" feature
				case "PlacedFeature" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_random_patch">
							<value name="feature">%s</value>
							<field name="tries">128</field><field name="xzSpread">7</field><field name="ySpread">3</field>
						</block></value></block></xml>
						""".formatted(testXML);
				// Vertical anchors are tested with the "Height: At constant height" placement
				case "VerticalAnchor" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_no_op"></block></value>
						<next><block type="placement_height_range">
							<value name="height"><block type="height_provider_constant">
								<value name="value">%s</value></block></value></block></next></block></xml>
						""".formatted(testXML);
				// Block holder sets are tested with the "Only if block is in block list or tag" block predicate
				case "BlockHolderSet" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_no_op"></block></value>
						<next><block type="placement_block_predicate_filter">
							<value name="condition"><block type="block_predicate_matching_blocks">
								<field name="x">0</field><field name="y">0</field><field name="z">0</field>
								<value name="blockSet">%s</value></block></value></block></next></block></xml>
						""".formatted(testXML);
				// Rule tests are tested with the "Replace single block" feature
				case "RuleTest" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_replace_single_block">
							<mutation inputs="1"></mutation>
							<value name="target0"><block type="ore_target">
								<value name="target">%s</value>
								<value name="state"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							</block></value>
						</block></value></block></xml>
						""".formatted(testXML);
				// The "Ore target" block is also tested with the "Replace single block" feature
				case "OreTarget" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_replace_single_block">
							<mutation inputs="1"></mutation>
							<value name="target0">%s</value>
						</block></value></block></xml>
						""".formatted(testXML);
				// Blockstate providers and blockstate selector are tested with the simple block feature
				case "BlockStateProvider", "MCItemBlock" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_simple_block">
							<value name="block">%s</value>
						</block></value></block></xml>
						""".formatted(testXML);
				// Tree decorators are tested with the simple tree feature
				case "TreeDecorator" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_tree_simple">
							<mutation inputs="1"></mutation>
							<value name="decorator0">%s</value>
							<value name="dirt"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="trunk"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="foliage"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="root_placer"><block type="root_placer_none"></block></value>
							<field name="type">oak</field>
							<field name="base_height">0</field>
							<field name="height_variation_a">0</field><field name="height_variation_b">0</field>
							<field name="force_dirt">FALSE</field><field name="ignore_vines">TRUE</field>
						</block></value></block></xml>
						""".formatted(testXML);
				// Root placers are also tested with the simple tree feature
				case "RootPlacer" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_tree_simple">
							<mutation inputs="0"></mutation>
							<value name="dirt"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="trunk"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="foliage"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="root_placer">%s</value>
							<field name="type">oak</field>
							<field name="base_height">0</field>
							<field name="height_variation_a">0</field><field name="height_variation_b">0</field>
							<field name="force_dirt">FALSE</field><field name="ignore_vines">TRUE</field>
						</block></value></block></xml>
						""".formatted(testXML);
				// Count providers are tested with the "Placement count" feature, but clamped so valid input is provided
				case "IntProvider" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_no_op"></block></value>
							<next><block type="placement_count"><value name="count">
								<block type="int_provider_clamped">
						 			<field name="min">0</field>
						 			<field name="max">100</field>
						 			<value name="toClamp">%s</value>
						 		</block>
							</value></block></next>
						</block></xml>
						""".formatted(testXML);
				// Block column layers are tested with the "Block column" feature
				case "BlockColumnLayer" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_block_column">
							<mutation inputs="1"></mutation>
							<value name="allowed_placement"><block type="block_predicate_is_air"></block></value>
							<field name="direction">DOWN</field><field name="prioritize_tip">FALSE</field>
							<value name="layer0">%s</value>
						</block></value></block></xml>
						""".formatted(testXML);
				// Disk rules are tested with the "Disk rule" feature
				case "DiskRule" -> feature.featurexml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="feature_container" deletable="false" x="40" y="40">
						<value name="feature"><block type="feature_disk">
							<mutation inputs="1"></mutation>
							<value name="fallback"><block type="blockstate_selector"><mutation inputs="0"/><field name="block">Blocks.STONE</field></block></value>
							<value name="target"><block type="block_predicate_is_air"></block></value>
							<value name="radius"><block type="int_provider_constant"><field name="value">4</field></block></value>
							<field name="half_height">1</field>
							<value name="rule0">%s</value>
						</block></value></block></xml>
						""".formatted(testXML);
				// Other output types (Height provider, block predicate, etc.) are tested with an appropriate placement block
				case "HeightProvider" -> feature.featurexml = getXMLFor("placement_height_range", "height", testXML);
				case "BlockPredicate" ->
						feature.featurexml = getXMLFor("placement_block_predicate_filter", "condition", testXML);
				default -> {
					LOG.warn("[{}] Skipping feature block of unrecognized type: {}", generatorName,
							featureBlock.getMachineName());
					continue;
				}
				}
			}

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(feature));
				workspace.getModElementManager().storeModElement(feature);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating procedure block: " + featureBlock.getMachineName(), t);
			}
		}
	}

	/**
	 * This method returns an XML string to test feature blocks that are neither placements nor features.
	 * The test is performed using the empty feature and a placement that accepts the block being tested.
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
				<value name="feature"><block type="feature_no_op"></block></value>
				<next><block type="%s">
					<value name="%s">%s</value></block></next></block></xml>
				""".formatted(placementType, valueName, testXML);
	}

}
