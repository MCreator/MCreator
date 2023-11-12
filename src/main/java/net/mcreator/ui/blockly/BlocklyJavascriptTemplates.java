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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.blockly;

import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableType;

public class BlocklyJavascriptTemplates {

	public static String variableListExtension(VariableType variableType) {
		return """
				Blockly.Extensions.register('%s_variables', function () {
					this.getInput("var").appendField(new Blockly.FieldDropdown(getVariablesOfType("%s")), 'VAR');
					this.getField('VAR').setValidator(function (variable) {
						var isPlayerVar = javabridge.isPlayerVariable(variable);
						this.getSourceBlock().updateShape_(isPlayerVar, true);
					});
				});""".formatted(variableType.getName(), variableType.getBlocklyVariableType());
	}

	public static String getVariableBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
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
				}]);""".formatted(variableType.getName(), L10N.t("blockly.block.get_var"), variableType.getName(),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	// Same as the normal "Get variable" block, but with the NULL icon at the beginning
	public static String nullableGetVariableBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
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
				}]);""".formatted(variableType.getName(), L10N.t("blockly.block.get_var"), variableType.getName(),
				variableType.getBlocklyVariableType(), variableType.getColor());
	}

	public static String setVariableBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
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
				}]);""".formatted(variableType.getName(), L10N.t("blockly.block.set_var"),
				L10N.t("blockly.block.set_to"), variableType.getBlocklyVariableType(), variableType.getName(),
				variableType.getColor());
	}

	public static String customDependencyBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
					"type": "custom_dependency_%s",
					"message0": "%%1",
					"args0": [
						{
							"type": "field_javaname",
							"name": "NAME",
							"text": "dependencyName"
						}
					],
					"output": "%s",
					"colour": "%s"
				}]);""".formatted(variableType.getName(), variableType.getBlocklyVariableType(),
				variableType.getColor());
	}

	public static String procedureReturnValueBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
					"type": "procedure_retval_%s",
					"message0": "%s %%1",
					"args0": [
						{
							"type": "field_data_list_selector",
							"name": "procedure",
							"datalist": "procedure_retval_%s"
						}
					],
					"output": "%s",
					"inputsInline": true,
					"colour": "%s"
				}]);""".formatted(variableType.getName(), L10N.t("blockly.block.procedure_retval"),
				variableType.getName(), variableType.getBlocklyVariableType(), variableType.getColor());
	}

	// Same as the "Procedure return value" block, but with the NULL icon at the beginning
	public static String nullableProcedureReturnValueBlock(VariableType variableType) {
		return """
				Blockly.defineBlocksWithJsonArray([{
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
					"output": "%s",
					"inputsInline": true,
					"colour": "%s"
				}]);""".formatted(variableType.getName(), L10N.t("blockly.block.procedure_retval"),
				variableType.getName(), variableType.getBlocklyVariableType(), variableType.getColor());
	}

	public static String returnBlock(VariableType variableType) {
		// The previous "Return" text is used as fallback if the specific key is missing
		String blockText = L10N.t("blockly.block.return_" + variableType.getName()) != null ?
				L10N.t("blockly.block.return_" + variableType.getName()) :
				L10N.t("blockly.block.return");
		return """
				Blockly.defineBlocksWithJsonArray([{
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
				}]);""".formatted(variableType.getName(), blockText, variableType.getBlocklyVariableType(),
				variableType.getColor());
	}
}
