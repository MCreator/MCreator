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

package net.mcreator.workspace.elements;

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.blockly.BlocklyJavascriptTemplates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class VariableElementTypeLoader {

	private static final Logger LOG = LogManager.getLogger("Variable loader");

	public static VariableElementTypeLoader INSTANCE;

	private final Map<VariableElementType, String> VARIABLE_TYPES_LIST = new HashMap<>();
	private final String variableBlocklyJS;

	public static void loadVariableTypes() {
		INSTANCE = new VariableElementTypeLoader();
	}

	public VariableElementTypeLoader() {
		LOG.debug("Loading variables");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("variables", Pattern.compile("^[^$].*\\.json"));

		StringBuilder variableBlocklyJSBuilder = new StringBuilder();

		for (String file : fileNames) {
			VariableElementType variable = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), VariableElementType.class);
			LOG.debug("Added " + variable.getName() + " to variable types");

			VARIABLE_TYPES_LIST.put(variable, variable.getName());

			//We begin by creating the extensions needed for other blocks
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.variableListExtension(variable));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.procedureListExtensions(variable));

			//Then, we create the blocks related to variables
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.getVariableBlock(variable));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.setVariableBlock(variable));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.customDependencyBlock(variable));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.procedureReturnValueBlock(variable));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.returnBlock(variable));

			//We check the type of the variable, if it is a global var, we instantiate it with this variable.
			switch (variable.getName()) {
			case "logic":
				BuiltInTypes.LOGIC = variable;
				break;
			case "number":
				BuiltInTypes.NUMBER = variable;
				break;
			case "string":
				BuiltInTypes.STRING = variable;
				break;
			case "itemstack":
				BuiltInTypes.ITEMSTACK = variable;
				break;
			}
		}

		variableBlocklyJS = variableBlocklyJSBuilder.toString();
	}

	public VariableElementType getVariableTypeFromString(String type) {
		for (VariableElementType varType : VARIABLE_TYPES_LIST.keySet()) {
			if (varType.getBlocklyVariableType().equalsIgnoreCase(type) || varType.getName().equalsIgnoreCase(type)) {
				return varType;
			}
		}
		return null;
	}

	public Set<VariableElementType> getVariableTypes() {
		return VARIABLE_TYPES_LIST.keySet();
	}

	public String getVariableBlocklyJS() {
		return variableBlocklyJS;
	}

	public static class BuiltInTypes {
		public static VariableElementType STRING;
		public static VariableElementType LOGIC;
		public static VariableElementType NUMBER;
		public static VariableElementType ITEMSTACK;
	}
}
