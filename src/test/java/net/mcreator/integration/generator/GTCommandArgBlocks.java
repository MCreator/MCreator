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
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Command;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static net.mcreator.integration.TestWorkspaceDataProvider.getRandomItem;
import static org.junit.jupiter.api.Assertions.fail;

public class GTCommandArgBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		// silently skip if commands are not supported by this generator
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.COMMAND)
				== GeneratorStats.CoverageStatus.NONE) {
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.COMMAND_ARG);

		for (ToolboxBlock commandArg : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.COMMAND_ARG)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(commandArg, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(commandArg))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(commandArg, workspace, random, additionalXML))
				continue; // failed to populate all fields

			if (commandArg.getStatements() != null) {
				for (StatementInput statement : commandArg.getStatements()) {
					additionalXML.append("<statement name=\"").append(statement.name).append("\">")
							.append("<block type=\"")
							.append(getRandomItem(random, new String[] { "call_procedure", "old_command" }))
							.append("\"><field name=\"procedure\">procedure1</field></block>").append("</statement>\n");
				}
			}

			ModElement modElement = new ModElement(workspace, "TestCmdArgBlock" + commandArg.getMachineName(),
					ModElementType.COMMAND);

			String testXML = commandArg.getToolboxTestXML();

			// add additional xml to the cmd arg block definition
			testXML = testXML.replace("<block type=\"" + commandArg.getMachineName() + "\">",
					"<block type=\"" + commandArg.getMachineName() + "\">" + additionalXML);

			Command command = new Command(modElement);
			command.commandName = modElement.getName();
			command.permissionLevel = getRandomItem(random, new String[] { "No requirement", "1", "2", "3", "4" });
			command.type = getRandomItem(random,
					new String[] { "STANDARD", "SINGLEPLAYER_ONLY", "MULTIPLAYER_ONLY", "CLIENTSIDE" });

			if (commandArg.getType() == IBlockGenerator.BlockType.PROCEDURAL)
				command.argsxml = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"args_start\""
						+ " deletable=\"false\" x=\"40\" y=\"40\"><next>" + testXML + "</next></block></xml>";

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(command, true);
				workspace.getModElementManager().storeModElement(command);
			} catch (Throwable t) {
				t.printStackTrace();
				fail("[" + generatorName + "] Failed generating command argument block: "
						+ commandArg.getMachineName());
			}
		}

	}

}
