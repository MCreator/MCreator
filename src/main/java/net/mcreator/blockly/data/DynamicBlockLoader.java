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

package net.mcreator.blockly.data;

import com.google.gson.JsonParser;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicBlockLoader {

	private static final Map<BlocklyEditorType, List<DynamicToolboxBlock>> dynamicToolboxBlocks = new HashMap<>();

	public static void preload() {
		dynamicToolboxBlocks.put(BlocklyEditorType.PROCEDURE, getAllForProcedures());
		dynamicToolboxBlocks.put(BlocklyEditorType.SCRIPT, getAllForScripts());
	}

	public static List<DynamicToolboxBlock> getDynamicBlocks(BlocklyEditorType blocklyEditorType) {
		return dynamicToolboxBlocks.computeIfAbsent(blocklyEditorType, _ -> List.of());
	}

	public static void loadBlocksAndCategoriesInPanel(BlocklyPanel pane) {
		GeneratorConfiguration configuration = pane.getMCreator().getGeneratorConfiguration();

		List<DynamicToolboxBlock> blockGenerators = getDynamicBlocks(pane.getType());
		if (blockGenerators == null)
			return;

		List<String> definitions = new ArrayList<>();
		for (DynamicToolboxBlock toolboxBlock : blockGenerators) {
			if (toolboxBlock.shouldLoad(configuration)) {
				definitions.add(toolboxBlock.getBlocklyJSON().toString());
			}
		}

		pane.executeLocalScript("Blockly.defineBlocksWithJsonArray([" + String.join(",", definitions) + "])");
	}

	private static List<DynamicToolboxBlock> getAllForProcedures() {
		List<DynamicToolboxBlock> list = new ArrayList<>();
		for (VariableType variableType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
			list.add(createVariablesGetBlock(variableType));
			list.add(createVariablesSetBlock(variableType));
			list.add(createCustomDependencyBlock(variableType));
			list.add(createProcedureRetvalBlock(variableType));
			list.add(createReturnBlock(variableType));
		}
		return list;
	}

	private static List<DynamicToolboxBlock> getAllForScripts() {
		List<DynamicToolboxBlock> list = new ArrayList<>();
		for (VariableType variableType : List.of(VariableTypeLoader.BuiltInTypes.LOGIC,
				VariableTypeLoader.BuiltInTypes.NUMBER, VariableTypeLoader.BuiltInTypes.STRING,
				VariableTypeLoader.BuiltInTypes.ITEMSTACK, VariableTypeLoader.BuiltInTypes.BLOCKSTATE)) {
			list.add(createVariablesGetBlock(variableType));
			list.add(createVariablesSetBlock(variableType));
			list.add(createCustomDependencyBlock(variableType));
		}
		return list;
	}

	private static DynamicToolboxBlock createVariablesGetBlock(VariableType variableType) {
		DynamicToolboxBlock block = createVariableScopedBlock(variableType);
		block.machine_name = "variables_get_" + variableType.getName();
		block.type = IBlockGenerator.BlockType.OUTPUT;
		if (variableType.isNullable()) {
			block.blocklyJSON = JsonParser.parseString(nullableGetVariableBlock(variableType)).getAsJsonObject();
		} else {
			block.blocklyJSON = JsonParser.parseString(getVariableBlock(variableType)).getAsJsonObject();
		}
		return block;
	}

	private static DynamicToolboxBlock createVariablesSetBlock(VariableType variableType) {
		DynamicToolboxBlock block = createVariableScopedBlock(variableType);
		block.machine_name = "variables_set_" + variableType.getName();
		block.type = IBlockGenerator.BlockType.PROCEDURAL;
		block.blocklyJSON = JsonParser.parseString(setVariableBlock(variableType)).getAsJsonObject();
		return block;
	}

	private static DynamicToolboxBlock createCustomDependencyBlock(VariableType variableType) {
		DynamicToolboxBlock block = new DynamicToolboxBlock();
		block.machine_name = "custom_dependency_" + variableType.getName();
		block.toolbox_id = "advanced";
		block.type = IBlockGenerator.BlockType.OUTPUT;
		block.blocklyJSON = JsonParser.parseString(customDependencyBlock(variableType)).getAsJsonObject();
		return block;
	}

	private static DynamicToolboxBlock createProcedureRetvalBlock(VariableType variableType) {
		DynamicToolboxBlock block = new DynamicToolboxBlock();
		block.machine_name = "procedure_retval_" + variableType.getName();
		block.toolbox_id = "advanced";
		if (variableType.isNullable()) {
			block.blocklyJSON = JsonParser.parseString(nullableProcedureReturnValueBlock(variableType))
					.getAsJsonObject();
		} else {
			block.blocklyJSON = JsonParser.parseString(procedureReturnValueBlock(variableType)).getAsJsonObject();
		}
		block.type = IBlockGenerator.BlockType.OUTPUT;
		return block;
	}

	private static DynamicToolboxBlock createReturnBlock(VariableType variableType) {
		DynamicToolboxBlock block = new DynamicToolboxBlock();
		block.machine_name = "return_" + variableType.getName();
		block.toolbox_id = "logicloops";
		block.type = IBlockGenerator.BlockType.PROCEDURAL;
		block.blocklyJSON = JsonParser.parseString(returnBlock(variableType)).getAsJsonObject();
		return block;
	}

	private static DynamicToolboxBlock createVariableScopedBlock(VariableType variableType) {
		DynamicToolboxBlock block = new DynamicToolboxBlock() {
			@Override public boolean shouldLoad(GeneratorConfiguration configuration) {
				return variableType.canBeLocal(configuration) || variableType.canBeGlobal(configuration);
			}
		};
		block.toolbox_id = "customvariables";
		return block;
	}

	private static String getVariableBlock(VariableType variableType) {
		return """
				{
					"type": "variables_get_%s",
					"message0": "%s %%1",
					"args0": [
						{
							"type": "input_dummy",
							"name": "var"
						}
					],
					"extensions": [
						"%s_variables"
					],
					"inputsInline": true,
					"output": "%s",
					"colour": "%s",
					"mutator": "variable_entity_input"
				}""".formatted(variableType.getName(), getBlockText("get_var", variableType), variableType.getName(),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	// Same as the normal "Get variable" block, but with the NULL icon at the beginning
	private static String nullableGetVariableBlock(VariableType variableType) {
		return """
				{
					"type": "variables_get_%s",
					"message0": "%%2 %s %%1",
					"args0": [
						{
							"type": "input_dummy",
							"name": "var"
						},
						{
							"type": "field_image",
							"src": "./res/null.png",
							"width": 8,
							"height": 24
						}
					],
					"extensions": [
						"%s_variables"
					],
					"inputsInline": true,
					"output": "%s",
					"colour": "%s",
					"mutator": "variable_entity_input"
				}""".formatted(variableType.getName(), getBlockText("get_var", variableType), variableType.getName(),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	private static String setVariableBlock(VariableType variableType) {
		return """
				{
					"type": "variables_set_%s",
					"message0": "%s %%1 %s %%2",
					"args0": [
						{
							"type": "input_dummy",
							"name": "var"
						},
						{
							"type": "input_value",
							"name": "VAL",
							"check": "%s"
						}
					],
					"extensions": [
						"%s_variables"
					],
					"inputsInline": true,
					"previousStatement": null,
					"nextStatement": null,
					"colour": "%s",
					"mutator": "variable_entity_input"
				}""".formatted(variableType.getName(), getBlockText("set_var", variableType),
				L10N.t("blockly.block.set_to"), variableType.getBlocklyVariableType(), variableType.getName(),
				variableType.getColor());
	}

	private static String customDependencyBlock(VariableType variableType) {
		return """
				{
					"type": "custom_dependency_%s",
					"message0": "%s %%1",
					"args0": [
						{
							"type": "field_javaname",
							"name": "NAME",
							"text": "dependencyName"
						}
					],
					"output": "%s",
					"colour": "%s"
				}""".formatted(variableType.getName(), getBlockText("custom_dependency", variableType),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	private static String procedureReturnValueBlock(VariableType variableType) {
		return """
				{
					"type": "procedure_retval_%s",
					"message0": "%s %%1",
					"args0": [
						{
							"type": "field_data_list_selector",
							"name": "procedure",
							"datalist": "procedure_retval_%s"
						}
					],
					"extensions": [
						"procedure_dependencies_tooltip",
						"procedure_dependencies_onchange_mixin"
					],
					"output": "%s",
					"colour": "%s",
					"mutator": "procedure_dependencies_mutator"
				}""".formatted(variableType.getName(), getBlockText("procedure_retval", variableType),
				variableType.getName(), variableType.getBlocklyVariableType(), variableType.getColor());
	}

	// Same as the "Procedure return value" block, but with the NULL icon at the beginning
	private static String nullableProcedureReturnValueBlock(VariableType variableType) {
		return """
				{
					"type": "procedure_retval_%s",
					"message0": "%%1 %s %%2",
					"args0": [
						{
							"type": "field_image",
							"src": "./res/null.png",
							"width": 8,
							"height": 24
						},
						{
							"type": "field_data_list_selector",
							"name": "procedure",
							"datalist": "procedure_retval_%s"
						}
					],
					"extensions": [
						"procedure_dependencies_tooltip",
						"procedure_dependencies_onchange_mixin"
					],
					"output": "%s",
					"colour": "%s",
					"mutator": "procedure_dependencies_mutator"
				}""".formatted(variableType.getName(), getBlockText("procedure_retval", variableType),
				variableType.getName(), variableType.getBlocklyVariableType(), variableType.getColor());
	}

	private static String returnBlock(VariableType variableType) {
		return """
				{
					"type": "return_%s",
					"message0": "%s %%1",
					"args0": [
						{
							"type": "input_value",
							"name": "return",
							"check": "%s"
						}
					],
					"previousStatement": null,
					"colour": "%s"
				}""".formatted(variableType.getName(), getBlockText("return", variableType),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	private static String getBlockText(String key, VariableType variableType) {
		// If the specific key is missing, fall back to the generic variant
		String translatedText = L10N.t("blockly.block." + key + "_" + variableType.getName()) != null ?
				L10N.t("blockly.block." + key + "_" + variableType.getName()) :
				L10N.t("blockly.block." + key);
		return translatedText.strip().replaceAll("\\r\\n|\\r|\\n", "");
	}

	public static class DynamicToolboxBlock extends ToolboxBlock {

		public boolean shouldLoad(GeneratorConfiguration configuration) {
			return true;
		}

	}

}
