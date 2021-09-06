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

package net.mcreator.minecraft.modbases;

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ToolTypesLoader {

	private static final Logger LOG = LogManager.getLogger("Tool Type Loader");

	private static final List<ToolType> toolTypes = new ArrayList<>();

	public static ToolTypesLoader INSTANCE;

	public ToolTypesLoader() {
		LOG.debug("Loading tool types");

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("modbases.tooltypes",
				Pattern.compile("^[^$].*\\.json"));

		final Gson gson = new Gson();

		for (String file : fileNames) {
			ToolType tooltype = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), ToolType.class);
			toolTypes.add(tooltype);
		}
	}

	public static void init() {
		INSTANCE = new ToolTypesLoader();
	}

	public List<ToolType> getToolTypes() {
		return toolTypes;
	}

	public boolean contains(String toolType) {
		return toolTypes.stream().anyMatch(tool -> tool.getName().equals(toolType));
	}

	public ToolType getToolType(String name) {
		return toolTypes.stream().filter(toolType -> toolType.getName().equals(name)).findFirst().orElse(null);

	}

}
