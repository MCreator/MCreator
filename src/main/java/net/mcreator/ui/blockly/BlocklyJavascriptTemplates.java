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
import net.mcreator.workspace.elements.VariableElementType;

public class BlocklyJavascriptTemplates {

	public static String variableListExtension(VariableElementType variableType) {
		return "Blockly.Extensions.register('" + variableType.getName() + "_variables',function () {"
				+ "this.getInput(\"var\").appendField(new Blockly.FieldDropdown(getVariablesOfType(\"" + variableType
				.getBlocklyVariableType() + "\")), 'VAR');" + "});";
	}

	public static String procedureListExtensions(VariableElementType variableType) {
		return "Blockly.Extensions.register('procedure_retval_" + variableType.getName() + "',function () {"
				+ "this.appendDummyInput().appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf(\"procedure_retval_"
				+ variableType.getName() + "\"))), 'procedure');" + "});";
	}

	public static String getVariableBlock(VariableElementType variableType) {
		return "Blockly.defineBlocksWithJsonArray([{\"type\":\"variables_get_" + variableType.getName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.get_var")
				+ " %1\",\"args0\":[{\"type\": \"input_dummy\",\"name\": \"var\"" + "}]," + "\"extensions\": [\"" + variableType
				.getName() + "_variables\"],\"inputsInline\": true,\"output\": \"" + variableType
				.getBlocklyVariableType() + "\",\"colour\":" + variableType.getColor() + "}]);";
	}

	public static String setVariableBlock(VariableElementType variableType) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"variables_set_" + variableType.getName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.set_var") + " %1 " + L10N.t("blockly.block.set_to")
				+ " %2\",\"args0\":[{\"type\":\"input_dummy\",\"name\":\"var\"},{\"type\":\"input_value\",\"name\":\"VAL\","
				+ "\"check\":\"" + variableType.getBlocklyVariableType() + "\"}],\"extensions\":[\"" + variableType.getName()
				+ "_variables\"],\"inputsInline\":true,\"previousStatement\": null,\"nextStatement\": null,\"colour\":"
				+ variableType.getColor() + "}]);";
	}

	public static String customDependencyBlock(VariableElementType variableType) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"custom_dependency_" + variableType.getName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.custom_dependency_" + variableType.getName())
				+ " %1\",\"args0\":[{\"type\":\"field_input\",\"name\":\"NAME\",\"text\":\"dependencyName\"}],\"output\":\""
				+ variableType.getBlocklyVariableType() + "\",\"colour\":" + variableType.getColor() + "}]);";
	}

	public static String procedureReturnValueBlock(VariableElementType variableType) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"procedure_retval_" + variableType.getName()
				+ "\",\"message0\": \"" + L10N.t("blockly.block.procedure_retval")
				+ "\",\"extensions\": [\"procedure_retval_" + variableType.getName() + "\"],\"output\": \"" + variableType
				.getBlocklyVariableType() + "\",\"inputsInline\": true,\"colour\": " + variableType.getColor() + "}]);";
	}

	public static String returnBlock(VariableElementType variableType) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"return_" + variableType.getName() + "\",\"message0\":\""
				+ L10N.t("blockly.block.return")
				+ " %1\",\"args0\":[{\"type\":\"input_value\",\"name\":\"return\",\"check\":\"" + variableType
				.getBlocklyVariableType() + "\"}],\"previousStatement\":null,\"colour\":"
				+ variableType.getColor() + "}]);";
	}
}
