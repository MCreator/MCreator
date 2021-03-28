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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableElementType;

public class JSScriptTemplates {

	public static String variableListExtension(VariableElementType var) {
		return "Blockly.Extensions.register('" + var.getDependencyType() + "_variables',function () {"
				+ "this.getInput(\"var\").appendField(new Blockly.FieldDropdown(getVariablesOfType(\"" + var
				.getBlocklyVariableType() + "\")), 'VAR');" + "});";
	}

	public static String procedureListExtensions(VariableElementType var) {
		return "Blockly.Extensions.register('procedure_retval_" + var.getType() + "',function () {"
				+ "this.appendDummyInput().appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf(\"procedure_retval_"
				+ var.getType() + "\"))), 'procedure');" + "});";
	}

	public static String getVariableBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([{\"type\":\"variables_get_" + var.getBlockName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.get_var")
				+ " %1\",\"args0\":[{\"type\": \"input_dummy\",\"name\": \"var\"" + "}]," + "\"extensions\": [\"" + var
				.getDependencyType() + "_variables\"],\"inputsInline\": true,\"output\": \"" + var
				.getBlocklyVariableType() + "\",\"colour\":" + BlocklyBlockUtil.getHUEFromRGB(var) + "}]);";
	}

	public static String setVariableBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"variables_set_" + var.getBlockName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.set_var") + " %1 " + L10N.t("blockly.block.set_to")
				+ " %2\",\"args0\":[{\"type\":\"input_dummy\",\"name\":\"var\"},{\"type\":\"input_value\",\"name\":\"VAL\","
				+ "\"check\":\"" + var.getBlocklyVariableType() + "\"}],\"extensions\":[\"" + var.getDependencyType()
				+ "_variables\"],\"inputsInline\":true,\"previousStatement\": null,\"nextStatement\": null,\"colour\":"
				+ BlocklyBlockUtil.getHUEFromRGB(var) + "}]);";
	}

	public static String customDependencyBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"custom_dependency_" + var.getBlockName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.custom_dependency_" + var.getBlockName())
				+ " %1\",\"args0\":[{\"type\":\"field_input\",\"name\":\"NAME\",\"text\":\"dependencyName\"}],\"output\":\""
				+ var.getBlocklyVariableType() + "\",\"colour\":" + BlocklyBlockUtil.getHUEFromRGB(var) + "}]);";
	}

	public static String procedureReturnValueBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"procedure_retval_" + var.getBlockName()
				+ "\",\"message0\": \"" + L10N.t("blockly.block.procedure_retval")
				+ "\",\"extensions\": [\"procedure_retval_" + var.getType() + "\"],\"output\": \"" + var
				.getBlocklyVariableType() + "\",\"inputsInline\": true,\"colour\": " + BlocklyBlockUtil
				.getHUEFromRGB(var) + "}]);";
	}

	public static String returnBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"return_" + var.getBlockName() + "\",\"message0\":\""
				+ L10N.t("blockly.block.return")
				+ " %1\",\"args0\":[{\"type\":\"input_value\",\"name\":\"return\",\"check\":\"" + var
				.getBlocklyVariableType() + "\"}],\"previousStatement\":null,\"nextStatement\":false,\"colour\":"
				+ BlocklyBlockUtil.getHUEFromRGB(var) + "}]);";
	}
}
