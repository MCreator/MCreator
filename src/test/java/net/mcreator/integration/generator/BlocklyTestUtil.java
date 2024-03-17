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

package net.mcreator.integration.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.blockly.data.RepeatingField;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class BlocklyTestUtil {

	private static final Logger LOG = LogManager.getLogger(BlocklyTestUtil.class);

	protected static boolean validateToolboxBlock(ToolboxBlock toolboxBlock, Set<String> generatorBlocks,
			Workspace workspace) {
		// skip procedure blocks not supported by this generator
		if (!generatorBlocks.contains(toolboxBlock.getMachineName()))
			return false;

		if (toolboxBlock.getRequiredAPIs() != null) {
			for (String required_api : toolboxBlock.getRequiredAPIs()) {
				if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
					return false;
				}
			}
		}

		return true;
	}

	protected static boolean validateInputs(ToolboxBlock toolboxBlock) {
		if (!toolboxBlock.getAllInputs().isEmpty() || !toolboxBlock.getAllRepeatingInputs().isEmpty()) {
			boolean templatesDefined = true;

			if (toolboxBlock.getToolboxInitStatements() != null) {
				for (String input : toolboxBlock.getAllInputs()) {
					boolean match = false;
					for (String toolboxtemplate : toolboxBlock.getToolboxInitStatements()) {
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

				for (String input : toolboxBlock.getAllRepeatingInputs()) {
					Pattern pattern = Pattern.compile("<value name=\"" + input + "\\d+\">");
					boolean match = false;
					for (String toolboxtemplate : toolboxBlock.getToolboxInitStatements()) {
						if (pattern.matcher(toolboxtemplate).find()) {
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
				LOG.warn("Skipping Blockly block with incomplete template: " + toolboxBlock.getMachineName());
				return false;
			}
		}

		return true;
	}

	protected static boolean populateFields(ToolboxBlock toolboxBlock, Workspace workspace, Random random,
			StringBuilder additionalXML) {
		if (toolboxBlock.getFields() != null) {
			int processed = 0;

			if (toolboxBlock.getBlocklyJSON().has("args0")) {
				for (String field : toolboxBlock.getFields()) {
					JsonArray args0 = toolboxBlock.getBlocklyJSON().get("args0").getAsJsonArray();
					for (int i = 0; i < args0.size(); i++) {
						JsonObject arg = args0.get(i).getAsJsonObject();
						if (arg.has("name") && arg.get("name").getAsString().equals(field)) {
							processed += appendFieldXML(workspace, random, additionalXML, arg, field);
							break;
						} else if (toolboxBlock.getToolboxTestXML().contains("<field name=\"" + field + "\">")) {
							processed++;
							break;
						}
					}
				}
			}

			if (processed != toolboxBlock.getFields().size()) {
				LOG.warn("Skipping Blockly block with special fields: " + toolboxBlock.getMachineName());
				return false;
			}
		}

		if (toolboxBlock.getRepeatingFields() != null) {
			int processedFields = 0;
			int totalFields = 0;
			for (RepeatingField fieldEntry : toolboxBlock.getRepeatingFields()) {
				if (fieldEntry.field_definition() != null) {
					int count = 3;
					if (fieldEntry.field_definition().has("testCount")) {
						count = fieldEntry.field_definition().get("testCount").getAsInt();
					}
					totalFields += count;
					for (int i = 0; i < count; i++) {
						processedFields += BlocklyTestUtil.appendFieldXML(workspace, random, additionalXML,
								fieldEntry.field_definition(), fieldEntry.name() + i);
					}
				}
			}
			if (processedFields != totalFields) {
				LOG.warn("Skipping Blockly block with incorrectly defined repeating field: "
						+ toolboxBlock.getMachineName());
				return false;
			}
		}

		return true;
	}

	protected static int appendFieldXML(Workspace workspace, Random random, StringBuilder additionalXML, JsonObject arg,
			String field) {
		int processed = 0;
		switch (arg.get("type").getAsString()) {
		case "field_checkbox" -> {
			additionalXML.append("<field name=\"").append(field).append("\">TRUE</field>");
			processed++;
		}
		case "field_number" -> {
			if (arg.has("precision") && arg.get("precision").getAsInt() == 1)
				additionalXML.append("<field name=\"").append(field).append("\">1</field>");
			else
				additionalXML.append("<field name=\"").append(field).append("\">1.23</field>");
			processed++;
		}
		case "field_input", "field_javaname" -> {
			additionalXML.append("<field name=\"").append(field).append("\">test</field>");
			processed++;
		}
		case "field_dropdown" -> {
			JsonArray opts = arg.get("options").getAsJsonArray();
			JsonArray opt = opts.get((int) (Math.random() * opts.size())).getAsJsonArray();
			additionalXML.append("<field name=\"").append(field).append("\">").append(opt.get(1).getAsString())
					.append("</field>");
			processed++;
		}
		case "field_mcitem_selector" -> {
			additionalXML.append("<field name=\"").append(field).append("\">Blocks.STONE</field>");
			processed++;
		}
		case "field_data_list_selector", "field_data_list_dropdown" -> {
			String type = arg.get("datalist").getAsString();

			// Get the optional properties
			JsonElement optTypeFilter = null, optCustomEntryProviders = null, optTestValue = arg.get("testValue");
			if (arg.get("type").getAsString().equals("field_data_list_selector")) {
				optTypeFilter = arg.get("typeFilter");
				optCustomEntryProviders = arg.get("customEntryProviders");
			}

			String typeFilter = optTypeFilter == null ? null : optTypeFilter.getAsString();
			String customEntryProviders =
					optCustomEntryProviders == null ? null : optCustomEntryProviders.getAsString();
			String value = optTestValue == null ? null : optTestValue.getAsString();

			if (value == null) {
				String[] values = getDataListFieldValues(workspace, type, typeFilter, customEntryProviders);
				if (values.length > 0 && !values[0].isEmpty()) {
					value = ListUtils.getRandomItem(random, values);
				}
			}

			if (value != null && !value.isEmpty()) {
				additionalXML.append("<field name=\"").append(field).append("\">").append(value).append("</field>");
				processed++;
			}
		}
		case "field_ai_condition_selector" -> {
			additionalXML.append("<field name=\"").append(field).append("\">condition1,condition2</field>");
			processed++;
		}
		}
		return processed;
	}

	private static String[] getDataListFieldValues(Workspace workspace, String datalist, String typeFilter,
			String customEntryProviders) {
		switch (datalist) {
		case "entity":
			return ElementUtil.loadAllEntities(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "spawnableEntity":
			return ElementUtil.loadAllSpawnableEntities(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "gui":
			return ElementUtil.loadBasicGUIs(workspace).toArray(String[]::new);
		case "direction":
			return ElementUtil.loadDirections();
		case "biome":
			return ElementUtil.loadAllBiomes(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "dimensionCustom":
			return workspace.getModElements().stream().filter(m -> m.getType() == ModElementType.DIMENSION)
					.map(m -> "CUSTOM:" + m.getName()).toArray(String[]::new);
		case "fluid":
			return ElementUtil.loadAllFluids(workspace).stream().map(DataListEntry::getName).toArray(String[]::new);
		case "gamerulesboolean":
			return ElementUtil.getAllBooleanGameRules(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "gamerulesnumber":
			return ElementUtil.getAllNumberGameRules(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		case "sound":
			return ElementUtil.getAllSounds(workspace);
		case "structure":
			return workspace.getFolderManager().getStructureList().toArray(String[]::new);
		case "procedure":
			return workspace.getModElements().stream().filter(mel -> mel.getType() == ModElementType.PROCEDURE)
					.map(ModElement::getName).toArray(String[]::new);
		case "arrowProjectile":
			return ElementUtil.loadArrowProjectiles(workspace).stream().map(DataListEntry::getName)
					.toArray(String[]::new);
		default: {
			if (datalist.startsWith("procedure_retval_")) {
				var variableType = VariableTypeLoader.INSTANCE.fromName(
						StringUtils.removeStart(datalist, "procedure_retval_"));
				return ElementUtil.getProceduresOfType(workspace, variableType);
			} else if (!DataListLoader.loadDataList(datalist).isEmpty()) {
				return ElementUtil.loadDataListAndElements(workspace, datalist, false, typeFilter,
								StringUtils.split(customEntryProviders, ',')).stream().map(DataListEntry::getName)
						.toArray(String[]::new);
			}
		}
		}
		return new String[] { "" };
	}

}
