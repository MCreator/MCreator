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
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.blockly.BlocklyJavascriptTemplates;
import net.mcreator.util.FilenameUtilsPatched;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VariableTypeLoader {

	private static final Logger LOG = LogManager.getLogger("Variable loader");

	public static VariableTypeLoader INSTANCE;

	private final Map<VariableType, String> VARIABLE_TYPES_LIST = new LinkedHashMap<>();
	private final String variableBlocklyJS;

	public static void loadVariableTypes() {
		INSTANCE = new VariableTypeLoader();
	}

	public VariableTypeLoader() {
		LOG.debug("Loading variable types");

		final Gson gson = new Gson();

		StringBuilder variableBlocklyJSBuilder = new StringBuilder();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("variables", Pattern.compile("^[^$].*\\.json"));
		for (String file : fileNames) {
			String variableJSON = FileIO.readResourceToString(PluginLoader.INSTANCE, file);
			VariableType variableType = gson.fromJson(variableJSON, VariableType.class);
			variableType.setName(FilenameUtilsPatched.getBaseName(file));

			VARIABLE_TYPES_LIST.put(variableType, variableType.getName());

			//We begin by creating the extensions needed for other blocks
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.variableListExtension(variableType));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.procedureListExtensions(variableType));

			//Then, we create the blocks related to variables
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.getVariableBlock(variableType));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.setVariableBlock(variableType));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.customDependencyBlock(variableType));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.procedureReturnValueBlock(variableType));
			variableBlocklyJSBuilder.append(BlocklyJavascriptTemplates.returnBlock(variableType));

			//We check the type of the variable, if it is a global var, we instantiate it with this variable.
			switch (variableType.getName()) {
			case "logic":
				BuiltInTypes.LOGIC = variableType;
				break;
			case "number":
				BuiltInTypes.NUMBER = variableType;
				break;
			case "string":
				BuiltInTypes.STRING = variableType;
				break;
			case "direction":
				BuiltInTypes.DIRECTION = variableType;
				break;
			case "itemstack":
				BuiltInTypes.ITEMSTACK = variableType;
				break;
			case "blockstate":
				BuiltInTypes.BLOCKSTATE = variableType;
				break;
			case "actionresulttype":
				BuiltInTypes.ACTIONRESULTTYPE = variableType;
				break;
			}
		}

		variableBlocklyJS = variableBlocklyJSBuilder.toString();
	}

	public VariableType fromName(String type) {
		for (VariableType varType : VARIABLE_TYPES_LIST.keySet())
			if (varType.getName().equalsIgnoreCase(type) || varType.getBlocklyVariableType().equalsIgnoreCase(type))
				return varType;

		return null;
	}

	public boolean doesVariableTypeExist(String name) {
		return VARIABLE_TYPES_LIST.containsValue(name);
	}

	public Collection<VariableType> getGlobalVariableTypes(GeneratorConfiguration generatorConfiguration) {
		return VARIABLE_TYPES_LIST.keySet().stream().filter(type -> type.canBeGlobal(generatorConfiguration))
				.sorted(Comparator.comparing(VariableType::toString)).collect(Collectors.toList());
	}

	public Collection<VariableType> getLocalVariableTypes(GeneratorConfiguration generatorConfiguration) {
		return VARIABLE_TYPES_LIST.keySet().stream().filter(type -> type.canBeLocal(generatorConfiguration))
				.sorted(Comparator.comparing(VariableType::toString)).collect(Collectors.toList());
	}

	public Collection<VariableType> getAllVariableTypes() {
		return VARIABLE_TYPES_LIST.keySet().stream().sorted(Comparator.comparing(VariableType::toString))
				.collect(Collectors.toList());
	}

	public String getVariableBlocklyJS() {
		return variableBlocklyJS;
	}

	public static class BuiltInTypes {
		public static VariableType LOGIC;
		public static VariableType NUMBER;
		public static VariableType STRING;
		public static VariableType DIRECTION;
		public static VariableType BLOCKSTATE;
		public static VariableType ITEMSTACK;
		public static VariableType ACTIONRESULTTYPE;
	}
}
