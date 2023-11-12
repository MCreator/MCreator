/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import net.mcreator.element.types.LivingEntity;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTAITaskBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		// silently skip if living entities are not supported by this generator
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.LIVINGENTITY)
				== GeneratorStats.CoverageStatus.NONE) {
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.AI_TASK);

		for (ToolboxBlock aiTask : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK).getDefinedBlocks()
				.values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(aiTask, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(aiTask))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(aiTask, workspace, random, additionalXML))
				continue; // failed to populate all fields

			ModElement modElement = new ModElement(workspace, "TestAITaskBlock" + aiTask.getMachineName(),
					ModElementType.LIVINGENTITY);

			String testXML = aiTask.getToolboxTestXML();

			// Set block selectors to some value
			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			// add additional xml to the AI task block definition
			testXML = testXML.replace("<block type=\"" + aiTask.getMachineName() + "\">",
					"<block type=\"" + aiTask.getMachineName() + "\">" + additionalXML);

			LivingEntity livingentity = TestWorkspaceDataProvider.getLivingEntity(modElement, random, true, true,
					Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

			if (aiTask.getType() == IBlockGenerator.BlockType.PROCEDURAL)
				livingentity.aixml =
						"<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"aitasks_container\""
								+ " deletable=\"false\" x=\"40\" y=\"40\"><next>" + testXML + "</next></block></xml>";

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(livingentity, true);
				workspace.getModElementManager().storeModElement(livingentity);
			} catch (Throwable t) {
				t.printStackTrace();
				fail("[" + generatorName + "] Failed generating AI task block: " + aiTask.getMachineName());
			}
		}

	}

}
