/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.blockly.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlocklyLoader {

	public static BlocklyLoader INSTANCE;

	public static void init() {
		INSTANCE = new BlocklyLoader();
	}

	private static final List<String> builtinCategories = Arrays.asList("other", "apis", "mcelements", "mcvariables",
			"customvariables", "logicloops", "logicoperations", "math", "text", "time", "advanced", "action");
	private final Map<String, ExternalBlockLoader> blockLoaders;
	private final ExternalTriggerLoader externalTriggerLoader;

	private BlocklyLoader() {
		blockLoaders = new LinkedHashMap<>();
		addBlockLoader("procedures");
		addBlockLoader("aitasks");
		addBlockLoader("cmdargs");
		addBlockLoader("jsontriggers");
		externalTriggerLoader = new ExternalTriggerLoader("triggers");
	}

	/**
	 * Create a new {@link ExternalBlockLoader} to load blocks for a specific Blockly panel.
	 *
	 * @param name The folder's name where to load files
	 */
	public void addBlockLoader(String name) {
		blockLoaders.put(name, new ExternalBlockLoader(name));
	}

	/**
	 * Add a usable category for JSON file blocks that has been created inside the custom XML {@link ToolboxType} file.
	 * All custom categories used by a {@link ToolboxType} have to be added before blocks are loaded.
	 *
	 * @param name The category's name as written inside the XML file (e.g. <i>&lt;custom-thisName/&gt;</i>).
	 */
	public static void addBuiltinCategory(String name) {
		builtinCategories.add(name);
	}

	public Map<String, ExternalBlockLoader> getBlockLoaders() {
		return blockLoaders;
	}

	public static List<String> getBuiltinCategories() {
		return builtinCategories;
	}

	public ExternalBlockLoader getSpecificBlockLoader(String name) {
		return blockLoaders.get(name);
	}

	public ExternalTriggerLoader getExternalTriggerLoader() {
		return externalTriggerLoader;
	}
}
