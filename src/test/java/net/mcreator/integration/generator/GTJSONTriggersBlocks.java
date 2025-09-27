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
import net.mcreator.element.types.Achievement;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTJSONTriggersBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.JSON_TRIGGER);

		for (ToolboxBlock triggerBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.JSON_TRIGGER)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(triggerBlock, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(triggerBlock))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(triggerBlock, workspace, random, additionalXML))
				continue; // failed to populate all fields

			ModElement modElement = new ModElement(workspace, "TestJSONTriggerBlock" + triggerBlock.getMachineName(),
					ModElementType.ADVANCEMENT);

			String testXML = triggerBlock.getToolboxTestXML();

			// Set selectors to some value

			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			testXML = testXML.replace("<block type=\"mcitem_all\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_all\"><field name=\"value\">" + TestWorkspaceDataProvider.getRandomMCItem(
							random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()
							+ "</field></block>");

			testXML = testXML.replace("<block type=\"" + triggerBlock.getMachineName() + "\">",
					"<block type=\"" + triggerBlock.getMachineName() + "\">" + additionalXML);

			Achievement advancement = TestWorkspaceDataProvider.getAdvancementExample(modElement, random, true, true,
					Collections.emptyList(), 1);

			if (triggerBlock.getType() == IBlockGenerator.BlockType.PROCEDURAL) {
				// If the block is not a special case, we can test it as a regular block
				advancement.triggerxml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
						+ "<block type=\"advancement_trigger\" deletable=\"false\" x=\"40\" y=\"80\"><next>" + testXML
						+ "</next></block></xml>";
			}

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(advancement));
				workspace.getModElementManager().storeModElement(advancement);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating procedure block: " + triggerBlock.getMachineName(), t);
			}
		}
	}

}