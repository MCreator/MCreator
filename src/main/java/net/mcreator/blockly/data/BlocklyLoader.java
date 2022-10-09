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

import java.util.LinkedHashMap;
import java.util.Map;

public class BlocklyLoader {

	public static BlocklyLoader INSTANCE;

	public static void init() {
		INSTANCE = new BlocklyLoader();
	}

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

	public void addBlockLoader(String name) {
		blockLoaders.put(name, new ExternalBlockLoader(name));
	}

	public Map<String, ExternalBlockLoader> getBlockLoaders() {
		return blockLoaders;
	}

	public ExternalBlockLoader getSpecificBlockLoader(String name) {
		return blockLoaders.get(name);
	}

	public ExternalTriggerLoader getExternalTriggerLoader() {
		return externalTriggerLoader;
	}
}
