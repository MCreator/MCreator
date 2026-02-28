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

import java.util.*;

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
		add("components");
	}};

	private final Map<BlocklyEditorType, ExternalBlockLoader> blockLoaders = new HashMap<>();
	private final Map<BlocklyEditorType, ExternalTriggerLoader> externalTriggerLoaders = new HashMap<>();

	private BlocklyLoader() {
		registerExternalTriggerLoader(BlocklyEditorType.PROCEDURE, "triggers");
		registerExternalTriggerLoader(BlocklyEditorType.SCRIPT, "jstriggers");

		registerBlockLoader(BlocklyEditorType.PROCEDURE);
		registerBlockLoader(BlocklyEditorType.AI_TASK);
		registerBlockLoader(BlocklyEditorType.JSON_TRIGGER);
		registerBlockLoader(BlocklyEditorType.COMMAND_ARG);
		registerBlockLoader(BlocklyEditorType.FEATURE);
		registerBlockLoader(BlocklyEditorType.SCRIPT);
	}

	/**
	 * Create a new {@link ExternalBlockLoader} to load blocks for a specific Blockly panel.
	 *
	 * @param type The type of Blockly editor to register
	 */
	public void registerBlockLoader(BlocklyEditorType type) {
		blockLoaders.put(type, new ExternalBlockLoader(type));
	}

	public void registerExternalTriggerLoader(BlocklyEditorType type, String resourceFolder) {
		externalTriggerLoaders.put(type, new ExternalTriggerLoader(resourceFolder));
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

	public ExternalTriggerLoader getExternalTriggerLoader(BlocklyEditorType type) {
		return externalTriggerLoaders.get(type);
	}

	public Map<BlocklyEditorType, ExternalTriggerLoader> getAllExternalTriggerLoaders() {
		return externalTriggerLoaders;
	}

}
