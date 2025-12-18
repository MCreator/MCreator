/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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
import net.mcreator.element.types.Enchantment;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTEnchantmentEffectBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		Set<String> generatorBlocks = workspace.getGeneratorStats()
				.getBlocklyBlocks(BlocklyEditorType.ENCHANTMENT_EFFECTS);

		for (ToolboxBlock effectBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.ENCHANTMENT_EFFECTS)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(effectBlock, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(effectBlock))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(effectBlock, workspace, random, additionalXML))
				continue; // failed to populate all fields

			ModElement modElement = new ModElement(workspace,
					"TestEnchantmentEffectBlock" + effectBlock.getMachineName(), ModElementType.ENCHANTMENT);

			String testXML = effectBlock.getToolboxTestXML();

			// Set block selectors to some value
			testXML = testXML.replace(
					"<block type=\"blockstate_selector\"><mutation inputs=\"0\"/><field name=\"block\"></field></block>",
					"<block type=\"blockstate_selector\"><mutation inputs=\"0\"/><field name=\"block\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			testXML = testXML.replace("<block type=\"" + effectBlock.getMachineName() + "\">",
					"<block type=\"" + effectBlock.getMachineName() + "\">" + additionalXML);

			Enchantment enchantment = new Enchantment(modElement);
			enchantment.name = modElement.getName();
			enchantment.weight = 3;
			enchantment.anvilCost = 2;
			enchantment.maxLevel = 3;

			if (effectBlock.getType() == IBlockGenerator.BlockType.PROCEDURAL) {
				switch (effectBlock.getPreviousStatementConnectionType()) {
				case "EnchantmentComponent" -> enchantment.effectsxml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="enchantment_effects_start" deletable="false" x="40" y="40">
						<next>%s</next></block></xml>""".formatted(testXML);
				// Conditional value effects are tested with the "Block experience" component
				case "ConditionalValueEffect" -> enchantment.effectsxml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="enchantment_effects_start" deletable="false" x="40" y="40">
						<next><block type="ench_component_block_experience"><statement name="conditionalEffect">
						%s</statement></block></next></block></xml>""".formatted(testXML);
				default -> {
					LOG.warn("[{}] Skipping procedural enchantment effect block of unrecognized type: {}",
							generatorName, effectBlock.getMachineName());
					continue;
				}
				}
			} else {
				switch (effectBlock.getOutputType()) {
				// Predicates are tested with the "Block experience" component
				case "Predicate" -> enchantment.effectsxml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="enchantment_effects_start" deletable="false" x="40" y="40">
						<next><block type="ench_component_block_experience"><statement name="conditionalEffect">
						<block type="conditional_value_effect">
						<value name="effect"><block type="value_effect_set"><value name="value">
						<block type="math_number"><field name="NUM">1</field></block></value></block></value>
						<value name="requirements">%s</value>
						</block></statement></block></next></block></xml>
						""".formatted(testXML);
				// Value effects are tested with the "Block experience" component
				case "ValueEffect" -> enchantment.effectsxml = """
						<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="enchantment_effects_start" deletable="false" x="40" y="40">
						<next><block type="ench_component_block_experience"><statement name="conditionalEffect">
						<block type="conditional_value_effect"><value name="effect">%s</value>
						<value name="requirements"><shadow type="predicate_true"></shadow></value>
						</block></statement></block></next></block></xml>
						""".formatted(testXML);
				default -> {
					LOG.warn("[{}] Skipping output enchantment effect block of unrecognized type: {}", generatorName,
							effectBlock.getMachineName());
					continue;
				}
				}
			}

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(enchantment));
				workspace.getModElementManager().storeModElement(enchantment);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating procedure block: " + effectBlock.getMachineName(), t);
			}
		}
	}

}
