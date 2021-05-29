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

	private static final Map<VariableElementType, String> VARIABLE_LIST = new HashMap<>();

	public String JS_CACHE = "";

	public static void loadVariableTypes() {
		INSTANCE = new VariableElementTypeLoader();
	}

	public VariableElementTypeLoader() {
		LOG.debug("Loading variables");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("variables", Pattern.compile("^[^$].*\\.json"));
		for (String file : fileNames) {
			VariableElementType variable = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), VariableElementType.class);
			LOG.debug("Added " + variable.getName() + " to variable types");
			VariableElementTypeLoader.addVariableTypeToCache(variable);

			//We begin by creating the extensions needed for other blocks
			JS_CACHE += BlocklyJavascriptTemplates.variableListExtension(variable);
			JS_CACHE += BlocklyJavascriptTemplates.procedureListExtensions(variable);
			//Then, we create the blocks related to variables
			JS_CACHE += BlocklyJavascriptTemplates.getVariableBlock(variable);
			JS_CACHE += BlocklyJavascriptTemplates.setVariableBlock(variable);
			JS_CACHE += BlocklyJavascriptTemplates.customDependencyBlock(variable);
			JS_CACHE += BlocklyJavascriptTemplates.procedureReturnValueBlock(variable);
			JS_CACHE += BlocklyJavascriptTemplates.returnBlock(variable);

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
	}

	public static VariableElementType getVariableTypeFromString(String type) {
		for (VariableElementType varType : VARIABLE_LIST.keySet()) {
			if(varType.getBlocklyVariableType().equalsIgnoreCase(type) || varType.getName().equalsIgnoreCase(type)) {
				return varType;
			}
		}
		return null;
	}

	public static Set<VariableElementType> getVariables() {
		return VARIABLE_LIST.keySet();
	}

	public static void addVariableTypeToCache(VariableElementType var) {
		VARIABLE_LIST.put(var, var.getName());
	}

	public static class BuiltInTypes {

		//Define each global variables. The instantiation is made in VariableElementTypeLoader.
		public static VariableElementType STRING;
		public static VariableElementType LOGIC;
		public static VariableElementType NUMBER;
		public static VariableElementType ITEMSTACK;
	}
}
