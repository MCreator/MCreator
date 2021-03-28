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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.regex.Pattern;

public class VariableElementTypeLoader {
	private static final Logger LOG = LogManager.getLogger("Variable loader");

	public static void loadVariableTypes() {
		LOG.debug("Loading variables");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("variables", Pattern.compile("^[^$].*\\.json"));
		for (String file : fileNames) {
			VariableElementType variable = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), VariableElementType.class);
			VariableElement.addVariable(variable);
		}
	}
}
