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

import java.util.Set;
import java.util.regex.Pattern;

public class VariableElementTypeLoader {
	private static final Logger LOG = LogManager.getLogger("Variable loader");
	public static VariableElementTypeLoader INSTANCE;

	public final StringBuilder JS_CACHE = new StringBuilder();

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
			VariableElement.addVariableTypeToCache(variable);

			//We begin by creating the extensions needed for other blocks
			JS_CACHE.append(BlocklyJavascriptTemplates.variableListExtension(variable));
			JS_CACHE.append(BlocklyJavascriptTemplates.procedureListExtensions(variable));
			//Then, we create the blocks related to variables
			JS_CACHE.append(BlocklyJavascriptTemplates.getVariableBlock(variable));
			JS_CACHE.append(BlocklyJavascriptTemplates.setVariableBlock(variable));
			JS_CACHE.append(BlocklyJavascriptTemplates.customDependencyBlock(variable));
			JS_CACHE.append(BlocklyJavascriptTemplates.procedureReturnValueBlock(variable));
			JS_CACHE.append(BlocklyJavascriptTemplates.returnBlock(variable));

			//We check the type of the variable, if it is a global var, we instantiate it with this variable.
			switch (variable.getName().toLowerCase()) {
			case "logic":
				VariableElementType.LOGIC = variable;
				break;
			case "number":
				VariableElementType.NUMBER = variable;
				break;
			case "string":
				VariableElementType.STRING = variable;
				break;
			case "itemstack":
				VariableElementType.ITEMSTACK = variable;
				break;
			}
		}
	}
}
