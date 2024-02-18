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

import net.mcreator.ui.blockly.BlocklyEditorType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlocklyLoader {

	public static BlocklyLoader INSTANCE;

	public static void init() {
		INSTANCE = new BlocklyLoader();
	}

	private static final List<String> builtinCategories = new ArrayList<>() {{
		add("other");
		add("apis");
		add("mcelements");
		add("mcvariables");
		add("customvariables");
		add("logicloops");
		add("logicoperations");
		add("math");
		add("text");
		add("time");
		add("advanced");
		add("actions");
		add("aiadvanced");
		add("features");
		add("orefeatures");
		add("treefeatures");
		add("treedecorators");
		add("advancedfeatures");
		add("intproviders");
		add("placements");
		add("heightplacements");
		add("blockpredicates");
		add("blocks");
	}};
	private final Map<BlocklyEditorType, ExternalBlockLoader> blockLoaders;
	private final ExternalTriggerLoader externalTriggerLoader;

	private BlocklyLoader() {
		blockLoaders = new LinkedHashMap<>();
		externalTriggerLoader = new ExternalTriggerLoader("triggers");

		addBlockLoader(BlocklyEditorType.PROCEDURE);
		addBlockLoader(BlocklyEditorType.AI_TASK);
		addBlockLoader(BlocklyEditorType.JSON_TRIGGER);
		addBlockLoader(BlocklyEditorType.COMMAND_ARG);
		addBlockLoader(BlocklyEditorType.FEATURE);
	}

	/**
	 * Create a new {@link ExternalBlockLoader} to load blocks for a specific Blockly panel.
	 *
	 * @param type The type of Blockly editor to register
	 */
	public void addBlockLoader(BlocklyEditorType type) {
		blockLoaders.put(type, new ExternalBlockLoader(type.registryName()));
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

	public Map<BlocklyEditorType, ExternalBlockLoader> getAllBlockLoaders() {
		return blockLoaders;
	}

	public static List<String> getBuiltinCategories() {
		return builtinCategories;
	}

	public ExternalBlockLoader getBlockLoader(BlocklyEditorType type) {
		return blockLoaders.get(type);
	}

	public ExternalTriggerLoader getExternalTriggerLoader() {
		return externalTriggerLoader;
	}
}
