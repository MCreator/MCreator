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

	public static String getVariableBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\n" + "  \"type\": \"variables_get_" + var.getBlockName()
				+ "\",\n" + "  \"message0\": \"" + L10N.t("blockly.block.get_var") + " %1\"" + ",\n"
				+ "  \"args0\": [\n" + "    {\n" + "      \"type\": \"input_dummy\",\n" + "      \"name\": \"var\"\n"
				+ "    }\n" + "  ],\n" + "  \"extensions\": [\n" + "    \"" + var.getDependencyType() + "_variables\"\n"
				+ "  ],\n" + "  \"inputsInline\": true,\n" + "  \"output\": \"" + var.getBlocklyVariableType() + "\",\n"
				+ "  \"colour\": " + BlocklyBlockUtil.getHUEFromRGB(var) + ",\n" + "  \"mcreator\": {\n"
				+ "    \"toolbox_id\": \"customvariables\",\n" + "    \"fields\": [\n" + "      \"VAR\"\n" + "    ]\n"
				+ "  }\n" + "}" + "]);";
	}

	public static String setVariableBlock(VariableElementType var) {
		return "Blockly.defineBlocksWithJsonArray([" + "{\"type\":\"variables_set_" + var.getBlockName()
				+ "\",\"message0\":\"" + L10N.t("blockly.block.set_var") + " %1 " + L10N.t("blockly.block.set_to")
				+ " %2\",\"args0\":[{\"type\":\"input_dummy\",\"name\":\"var\"},{\"type\":\"input_value\",\"name\":\"value\","
				+ "\"check\":\"" + var.getBlocklyVariableType() + "\"}],\"extensions\":[\"" + var.getDependencyType()
				+ "_variables\"],\"inputsInline\":true,\n\"previousStatement\": null,\"nextStatement\": null,\"colour\":"
				+ BlocklyBlockUtil.getHUEFromRGB(var)
				+ ",\"mcreator\":{\"toolbox_id\":\"customvariables\",\"fields\":[\"VAR\"],\"inputs\":[\"VAL\"]}}"
				+ "]);";
	}
}
