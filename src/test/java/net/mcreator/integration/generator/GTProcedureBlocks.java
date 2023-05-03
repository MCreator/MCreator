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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyJavascriptBridge;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTProcedureBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		// silently skip if procedures are not supported by this generator
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE) {
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getGeneratorProcedures();

		for (ToolboxBlock procedureBlock : BlocklyLoader.INSTANCE.getProcedureBlockLoader().getDefinedBlocks()
				.values()) {
			StringBuilder additionalXML = new StringBuilder();

			// silently skip procedure blocks not supported by this generator
			if (!generatorBlocks.contains(procedureBlock.machine_name)) {
				continue;
			}

			if (procedureBlock.toolboxXML == null) {
				LOG.warn("[" + generatorName + "] Skipping procedure block without default XML defined: "
						+ procedureBlock.machine_name);
				continue;
			}

			if (!procedureBlock.getAllInputs().isEmpty() || !procedureBlock.getAllRepeatingInputs().isEmpty()) {
				boolean templatesDefined = true;

				if (procedureBlock.toolbox_init != null) {
					for (String input : procedureBlock.getAllInputs()) {
						boolean match = false;
						for (String toolboxtemplate : procedureBlock.toolbox_init) {
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

					if (!procedureBlock.getAllRepeatingInputs().isEmpty()) {
						try {
							JsonArray args0 = procedureBlock.blocklyJSON.getAsJsonObject().get("args0")
									.getAsJsonArray();
							for (int i = 0; i < args0.size(); i++) {
								if (args0.get(i).getAsJsonObject().get("type").getAsString().equals("input_value")) {
									String name = args0.get(i).getAsJsonObject().get("name").getAsString();

									boolean match = false;
									for (String input : procedureBlock.getAllRepeatingInputs()) {
										if (name.matches(input + "\\d+")) {
											for (String toolboxtemplate : procedureBlock.toolbox_init) {
												if (toolboxtemplate.contains("<value name=\"" + name + "\">")) {
													match = true;
													break;
												}
											}
											if (match)
												break;
										}
									}

									if (!match) {
										templatesDefined = false;
										break;
									}
								}
							}
						} catch (Exception ignored) {
						}
					}
				} else {
					templatesDefined = false;
				}

				if (!templatesDefined) {
					LOG.warn("[" + generatorName + "] Skipping procedure block with incomplete template: "
							+ procedureBlock.machine_name);
					continue;
				}
			}

			if (procedureBlock.getRequiredAPIs() != null) {
				boolean skip = false;

				for (String required_api : procedureBlock.getRequiredAPIs()) {
					if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
						skip = true;
						break;
					}
				}

				if (skip) {
					// We skip API specific blocks without any warnings logged as we do not intend to test them anyway
					continue;
				}
			}

			if (procedureBlock.getFields() != null) {
				int processed = 0;

				for (String field : procedureBlock.getFields()) {
					try {
						JsonArray args0 = procedureBlock.blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
						for (int i = 0; i < args0.size(); i++) {
							JsonObject arg = args0.get(i).getAsJsonObject();
							if (arg.get("name").getAsString().equals(field)) {
								switch (arg.get("type").getAsString()) {
								case "field_checkbox" -> {
									additionalXML.append("<field name=\"").append(field).append("\">TRUE</field>");
									processed++;
								}
								case "field_number" -> {
									additionalXML.append("<field name=\"").append(field).append("\">1.23d</field>");
									processed++;
								}
								case "field_input", "field_javaname" -> {
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
								case "field_data_list_selector" -> {
									String type = arg.get("datalist").getAsString();

									// Get the optional properties
									JsonElement optTypeFilter = arg.get("typeFilter");
									String typeFilter = optTypeFilter == null ? null : optTypeFilter.getAsString();

									JsonElement optCustomEntryProviders = arg.get("customEntryProviders");
									String customEntryProviders = optCustomEntryProviders == null ? null :
											optCustomEntryProviders.getAsString();

									String[] values = getDataListFieldValues(workspace, type, typeFilter,
											customEntryProviders);
									if (values.length > 0 && !values[0].equals("")) {
										String value = ListUtils.getRandomItem(random, values);
										additionalXML.append("<field name=\"").append(field).append("\">").append(value)
												.append("</field>");
										processed++;
									}
								}
								}
								break;
							}
						}
					} catch (Exception ignored) {
					}
				}

				if (procedureBlock.blocklyJSON.getAsJsonObject().get("extensions") != null) {
					JsonArray extensions = procedureBlock.blocklyJSON.getAsJsonObject().get("extensions")
							.getAsJsonArray();
					for (int i = 0; i < extensions.size(); i++) {
						String extension = extensions.get(i).getAsString();
						String suggestedFieldName = extension.replace("_list_provider", "");
						String suggestedDataListName = suggestedFieldName;

						// convert to proper field names in some extension cases
						switch (extension) {
						case "gui_list_provider":
							suggestedFieldName = "guiname";
							suggestedDataListName = "gui";
							break;
						case "dimension_custom_list_provider":
							suggestedFieldName = "dimension";
							suggestedDataListName = "dimension_custom";
							break;
						}

						if (suggestedDataListName.equals("sound_category")) {
							suggestedDataListName = "soundcategories";
							suggestedFieldName = "soundcategory";
						}

						if (suggestedDataListName.equals("plant_type")) {
							suggestedDataListName = "planttype";
							suggestedFieldName = "planttype";
						}

						if (procedureBlock.getFields().contains(suggestedFieldName)) {
							String[] values = BlocklyJavascriptBridge.getListOfForWorkspace(workspace,
									suggestedDataListName);

							if (values.length == 0 || values[0].equals(""))
								values = BlocklyJavascriptBridge.getListOfForWorkspace(workspace,
										suggestedDataListName + "s");

							if (values.length > 0 && !values[0].equals("")) {
								additionalXML.append("<field name=\"").append(suggestedFieldName).append("\">")
										.append(ListUtils.getRandomItem(random, values)).append("</field>");
								processed++;
							}
						}
					}
				}

				if (processed != procedureBlock.getFields().size()) {
					LOG.warn("[" + generatorName + "] Skipping procedure block with special fields: "
							+ procedureBlock.machine_name);
					continue;
				}
			}

			if (procedureBlock.getStatements() != null) {
				for (StatementInput statement : procedureBlock.getStatements()) {
					additionalXML.append("<statement name=\"").append(statement.name).append("\">")
							.append("<block type=\"text_print\"><value name=\"TEXT\"><block type=\"math_number\">"
									+ "<field name=\"NUM\">123.456</field></block></value></block>")
							.append("</statement>\n");
				}
			}

			if (procedureBlock.getRepeatingStatements() != null) {
				try {
					JsonArray args0 = procedureBlock.blocklyJSON.getAsJsonObject().get("args0").getAsJsonArray();
					for (int i = 0; i < args0.size(); i++) {
						if (args0.get(i).getAsJsonObject().get("type").getAsString().equals("input_statement")) {
							String name = args0.get(i).getAsJsonObject().get("name").getAsString();
							for (StatementInput statement : procedureBlock.getRepeatingStatements()) {
								if (name.matches(statement.name + "\\d+")) {
									additionalXML.append("<statement name=\"").append(name).append("\">")
											.append("<block type=\"text_print\"><value name=\"TEXT\">"
													+ "<block type=\"math_number\"><field name=\"NUM\">123.456</field>")
											.append("</block></value></block></statement>\n");
								}
							}
						}
					}
				} catch (Exception ignored) {
				}
			}

			ModElement modElement = new ModElement(workspace, "TestProcedureBlock" + procedureBlock.machine_name,
					ModElementType.PROCEDURE);

			String testXML = procedureBlock.toolboxXML;

			// replace common math blocks with blocks that contain double variable to verify things like type casting
			testXML = testXML.replace("<block type=\"coord_x\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replace("<block type=\"coord_y\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replace("<block type=\"coord_z\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replaceAll("<block type=\"math_number\"><field name=\"NUM\">(.*?)</field></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");

			// replace common logic blocks with blocks that contain logic variable
			testXML = testXML.replace("<block type=\"logic_boolean\"><field name=\"BOOL\">TRUE</field></block>",
					"<block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field></block>");
			testXML = testXML.replace("<block type=\"logic_boolean\"><field name=\"BOOL\">FALSE</field></block>",
					"<block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field></block>");

			// replace common itemstack blocks with blocks that contain logic variable
			testXML = testXML.replace("<block type=\"itemstack_to_mcitem\"></block>",
					"<block type=\"variables_get_itemstack\"><field name=\"VAR\">local:stackvar</field></block>");
			testXML = testXML.replace("<block type=\"mcitem_all\"><field name=\"value\"></field></block>",
					"<block type=\"variables_get_itemstack\"><field name=\"VAR\">local:stackvar</field></block>");

			// set MCItem blocks to some value
			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			testXML = testXML.replace("<block type=\"mcitem_all\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_all\"><field name=\"value\">" + TestWorkspaceDataProvider.getRandomMCItem(
							random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()
							+ "</field></block>");

			// add additional xml to the block definition
			testXML = testXML.replace("<block type=\"" + procedureBlock.machine_name + "\">",
					"<block type=\"" + procedureBlock.machine_name + "\">" + additionalXML);

			Procedure procedure = new Procedure(modElement);

			if (procedureBlock.type == IBlockGenerator.BlockType.PROCEDURAL) {
				procedure.procedurexml = wrapWithBaseTestXML(testXML);
			} else { // output block type
				String rettype = procedureBlock.getOutputType();
				switch (rettype) {
				case "Number":
					procedure.procedurexml = wrapWithBaseTestXML(
							"<block type=\"return_number\"><value name=\"return\">" + testXML + "</value></block>");
					break;
				case "Boolean":
					procedure.procedurexml = wrapWithBaseTestXML(
							"<block type=\"return_logic\"><value name=\"return\">" + testXML + "</value></block>");

					break;
				case "String":
					procedure.procedurexml = wrapWithBaseTestXML(
							"<block type=\"return_string\"><value name=\"return\">" + testXML + "</value></block>");
					break;
				case "MCItem":
					procedure.procedurexml = wrapWithBaseTestXML(
							"<block type=\"return_itemstack\"><value name=\"return\">" + testXML + "</value></block>");
					break;
				case "ProjectileEntity": // Projectile blocks are tested with the "Shoot from entity" procedure
					procedure.procedurexml = wrapWithBaseTestXML("""
						<block type="projectile_shoot_from_entity">
							<value name="projectile">%s</value>
							<value name="entity"><block type="entity_from_deps"></block></value>
							<value name="speed"><block type="math_number"><field name="NUM">1</field></block></value>
							<value name="inaccuracy"><block type="math_number"><field name="NUM">0</field></block></value>
						</block>""".formatted(testXML));
					break;
				default:
					procedure.procedurexml = wrapWithBaseTestXML(
							"<block type=\"text_print\"><value name=\"TEXT\">" + testXML + "</value></block>");
					break;
				}
			}

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(procedure, true);
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				t.printStackTrace();
				fail("[" + generatorName + "] Failed generating procedure block: " + procedureBlock.machine_name);
			}
		}

	}

	public static String wrapWithBaseTestXML(String customXML) {
		return "<xml xmlns=\"https://developers.google.com/blockly/xml\">" + "<variables>"
				+ "<variable type=\"Number\" id=\"test\">test</variable>"
				+ "<variable type=\"Boolean\" id=\"flag\">flag</variable>"
				+ "<variable type=\"MCItem\" id=\"stackvar\">stackvar</variable>" + "</variables>"
				+ "<block type=\"event_trigger\" deletable=\"false\" x=\"59\" y=\"38\">"
				+ "<field name=\"trigger\">no_ext_trigger</field>" + "<next><block type=\"variables_set_logic\">"
				+ "<field name=\"VAR\">local:flag</field><value name=\"VAL\"><block type=\"logic_negate\">"
				+ "<value name=\"BOOL\"><block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field>"
				+ "</block></value></block></value><next><block type=\"variables_set_number\">"
				+ "<field name=\"VAR\">local:test</field><value name=\"VAL\"><block type=\"math_dual_ops\">"
				+ "<field name=\"OP\">ADD</field><value name=\"A\"><block type=\"variables_get_number\">"
				+ "<field name=\"VAR\">local:test</field></block></value><value name=\"B\"><block type=\"math_number\">"
				+ "<field name=\"NUM\">1.23</field></block></value></block></value><next><block type=\"variables_set_itemstack\">"
				+ "<field name=\"VAR\">local:stackvar</field><value name=\"VAL\"><block type=\"mcitem_all\"><field name=\"value\">"
				+ "Blocks.STONE</field></block></value><next>" + customXML
				+ "</next></block></next></block></next></block></next></block></xml>";
	}

	private static String[] getDataListFieldValues(Workspace workspace, String datalist, String typeFilter,
			String customEntryProviders) {
		switch (datalist) {
		case "entity": return ElementUtil.loadAllEntities(workspace)
				.stream().map(DataListEntry::getName).toArray(String[]::new);
		case "spawnableEntity": return ElementUtil.loadAllSpawnableEntities(workspace)
				.stream().map(DataListEntry::getName).toArray(String[]::new);
		case "biome": return ElementUtil.loadAllBiomes(workspace)
				.stream().map(DataListEntry::getName).toArray(String[]::new);
		case "sound": return ElementUtil.getAllSounds(workspace);
		case "procedure": return workspace.getModElements()
				.stream().filter(mel -> mel.getType() == ModElementType.PROCEDURE)
				.map(ModElement::getName).toArray(String[]::new);
		case "arrowProjectile": return ElementUtil.loadArrowProjectiles(workspace)
				.stream().map(DataListEntry::getName).toArray(String[]::new);
		default: {
			if (datalist.startsWith("procedure_retval_")) {
				var variableType = VariableTypeLoader.INSTANCE.fromName(
						StringUtils.removeStart(datalist, "procedure_retval_"));
				return ElementUtil.getProceduresOfType(workspace, variableType);
			}
			if (!DataListLoader.loadDataList(datalist).isEmpty()) {
				return ElementUtil.loadDataListAndElements(workspace, datalist, false, typeFilter,
								StringUtils.split(customEntryProviders, ','))
						.stream().map(DataListEntry::getName).toArray(String[]::new);
			}
		}
		}
		return new String[]{""};
	}
}
