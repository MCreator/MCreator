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
import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefinitionsProvider {

	private static final Logger LOG = LogManager.getLogger("Definition Loader");

	private final Map<ModElementType<?>, Map<?, ?>> cache = new ConcurrentHashMap<>();

	private final Map<BaseType, Map<?, ?>> global_cache = new ConcurrentHashMap<>();

	public DefinitionsProvider(String generatorName) {
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			String config = FileIO.readResourceToString(PluginLoader.INSTANCE,
					"/" + generatorName + "/" + type.getRegistryName().toLowerCase(Locale.ENGLISH)
							+ ".definition.yaml");

			if (config.equals("")) // definition not specified
				continue;

			YamlReader reader = new YamlReader(config);
			try {
				cache.put(type, new ConcurrentHashMap<>((Map<?, ?>) reader.read())); // add definition to the cache
			} catch (YamlException e) {
				LOG.error("[" + generatorName + "] Error: " + e.getMessage(), e);
			}
		}

		for (BaseType type : BaseType.values()) {
			String config = FileIO.readResourceToString(PluginLoader.INSTANCE,
					"/" + generatorName + "/common." + type.getPluralName().toLowerCase(Locale.ENGLISH) + ".yaml");

			if (config.equals("")) // definition not specified
				continue;

			YamlReader reader = new YamlReader(config);
			try {
				global_cache.put(type,
						new ConcurrentHashMap<>((Map<?, ?>) reader.read())); // add definition to the cache
			} catch (YamlException e) {
				LOG.info("[" + generatorName + "] Error: " + e.getMessage(), e);
			}
		}
	}

	public Map<?, ?> getModElementDefinition(ModElementType<?> elementType) {
		return cache.get(elementType);
	}

	public Map<?, ?> getBaseTypeDefinition(BaseType baseType) {
		return global_cache.get(baseType);
	}

}
