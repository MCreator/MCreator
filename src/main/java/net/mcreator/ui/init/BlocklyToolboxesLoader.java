/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.init;

import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class BlocklyToolboxesLoader {

	private static final Logger LOG = LogManager.getLogger("Blockly Toolboxes loader");

	public static BlocklyToolboxesLoader INSTANCE;

	private final Map<String, String> TOOLBOXES = new HashMap<>();

	public static void init() {
		INSTANCE = new BlocklyToolboxesLoader();
	}

	public BlocklyToolboxesLoader() {
		LOG.debug("Loading Blockly toolboxes from plugins");

		Set<String> xmlFiles = PluginLoader.INSTANCE.getResources("blockly.toolboxes", Pattern.compile("^[^$].*\\.xml"));
		for (String fileName : xmlFiles)
			TOOLBOXES.put(fileName, FileIO.readResourceToString(PluginLoader.INSTANCE, fileName));
	}

	public String getSpecificToolBox(String name) {
		return TOOLBOXES.get("blockly/toolboxes/toolbox_" + name + ".xml");
	}
}
