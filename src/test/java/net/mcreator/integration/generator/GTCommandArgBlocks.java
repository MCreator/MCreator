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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Command;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.blockly.BlocklyJavascriptBridge;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static net.mcreator.integration.TestWorkspaceDataProvider.getRandomItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GTCommandArgBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.COMMAND)
				== GeneratorStats.CoverageStatus.NONE) {
			LOG.warn("[" + generatorName
					+ "] Skipping command argument blocks test as the current generator does not support them.");
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getGeneratorCmdArgs();

		for (ToolboxBlock commandArg : BlocklyLoader.INSTANCE.getCmdArgsBlockLoader().getDefinedBlocks().values()) {

			StringBuilder additionalXML = new StringBuilder();

			if (!generatorBlocks.contains(commandArg.machine_name)) {
				LOG.warn("[" + generatorName + "] Skipping command argument block that is not defined by generator: "
						+ commandArg.machine_name);
				continue;
			}

			if (commandArg.toolboxXML == null) {
				LOG.warn("[" + generatorName + "] Skipping command argument block without default XML defined: "
						+ commandArg.machine_name);
				continue;
			}

			if (commandArg.getInputs() != null) {
				boolean templatesDefined = false;

				if (commandArg.toolbox_init != null) {
					templatesDefined = commandArg.getInputs().stream().noneMatch(
							input -> commandArg.toolbox_init.stream().noneMatch(
									toolboxTemplate -> toolboxTemplate.contains("<value name=\"" + input + "\">")));
				}

				if (!templatesDefined) {
					LOG.warn("[" + generatorName + "] Skipping command argument block with incomplete template: "
							+ commandArg.machine_name);
					continue;
				}
			}

			if (commandArg.getFields() != null) {
				int processed = 0;

				for (String field : commandArg.getFields()) {
					try {
						JsonArray args0 = commandArg.blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
						for (int i = 0; i < args0.size(); i++) {
							JsonObject arg = args0.get(i).getAsJsonObject();
							if (arg.get("name").getAsString().equals(field)) {
								switch (arg.get("type").getAsString()) {
								case "field_checkbox":
									additionalXML.append("<field name=\"").append(field).append("\">TRUE</field>");
									processed++;
									break;
								case "field_number":
									additionalXML.append("<field name=\"").append(field).append("\">1.23d</field>");
									processed++;
									break;
								case "field_input":
									additionalXML.append("<field name=\"").append(field).append("\">test</field>");
									processed++;
									break;
								case "field_dropdown":
									JsonArray opts = arg.get("options").getAsJsonArray();
									JsonArray opt = opts.get((int) (Math.random() * opts.size())).getAsJsonArray();
									additionalXML.append("<field name=\"").append(field).append("\">")
											.append(opt.get(1).getAsString()).append("</field>");
									processed++;
									break;
								}
								break;
							}
						}
					} catch (Exception ignored) {
					}
				}

				try {
					JsonArray extensions = commandArg.blocklyJSON.getAsJsonObject().get("extensions").getAsJsonArray();
					for (int i = 0; i < extensions.size(); i++) {
						String extension = extensions.get(i).getAsString();
						String suggestedFieldName = extension;

						// convert to proper field names in some extension cases
						if ("arg_procedure".equals(extension)) {
							suggestedFieldName = "procedure";
						}

						if (commandArg.getFields().contains(suggestedFieldName)) {
							String[] values = BlocklyJavascriptBridge.getListOfForWorkspace(workspace,
									suggestedFieldName);
							if (values.length > 0 && !values[0].equals("")) {
								additionalXML.append("<field name=\"").append(suggestedFieldName).append("\">")
										.append(ListUtils.getRandomItem(random, values)).append("</field>");
								processed++;
							}
						}
					}
				} catch (Exception ignored) {
				}

				if (processed != commandArg.getFields().size()) {
					LOG.warn("[" + generatorName + "] Skipping command argument block with special fields: "
							+ commandArg.machine_name);
					continue;
				}
			}

			if (commandArg.getStatements() != null) {
				commandArg.getStatements().forEach(
						statement -> additionalXML.append("<statement name=\"").append(statement.name).append("\">")
								.append("<block type=\"basic_literal\"><field name=\"name\">paramName</field>"
										+ "<field name=\"command\">null</field></block>").append("</statement>\n"));
			}

			ModElement modElement = new ModElement(workspace, "TestBlock" + commandArg.machine_name,
					ModElementType.COMMAND);

			String testXML = commandArg.toolboxXML;
			// add additional xml to the block definition
			testXML = testXML.replace("<block type=\"" + commandArg.machine_name + "\">",
					"<block type=\"" + commandArg.machine_name + "\">" + additionalXML);

			Command command = new Command(modElement);
			command.commandName = modElement.getName();
			command.permissionLevel = getRandomItem(random, new String[] { "No requirement", "1", "2", "3", "4" });

			if (commandArg.type == IBlockGenerator.BlockType.PROCEDURAL)
				command.argsxml = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"args_start\""
						+ " deletable=\"false\" x=\"40\" y=\"40\"><next>" + testXML + "</next></block></xml>";

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(command));
				workspace.getModElementManager().storeModElement(command);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating command argument block: " + commandArg.machine_name);
				t.printStackTrace();
			}
		}

	}

}
