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

package net.mcreator.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import net.mcreator.element.ModElementType;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DefinitionsProvider {

	private static final Logger LOG = LogManager.getLogger("Definition Loader");

	private final Map<ModElementType, Map<?, ?>> cache = new ConcurrentHashMap<>();

	DefinitionsProvider(String generatorName) {
		for (ModElementType type : ModElementType.values()) {
			String config = FileIO.readResourceToString(PluginLoader.INSTANCE,
					"/" + generatorName + "/" + type.name().toLowerCase(Locale.ENGLISH) + ".definition.yaml");

			if (config.equals("")) // definition not specified
				continue;

			YamlReader reader = new YamlReader(config);
			try {
				cache.put(type, new ConcurrentHashMap<>((Map<?, ?>) reader.read())); // add definition to the cache
			} catch (YamlException e) {
				LOG.error(e.getMessage(), e);
				LOG.info("[" + generatorName + "] Error: " + e.getMessage());
			}
		}
	}

	Map<?, ?> getModElementDefinition(ModElementType elementType) {
		return cache.get(elementType);
	}

}
