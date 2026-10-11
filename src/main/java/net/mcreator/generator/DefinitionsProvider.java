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

import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.yaml.AdaptiveYamlMergePolicy;
import net.mcreator.util.yaml.YamlMerge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefinitionsProvider {

	private static final Logger LOG = LogManager.getLogger("Definition Loader");

	private final String generatorName;

	private final Map<ModElementType<?>, Map<?, ?>> cache = new ConcurrentHashMap<>();

	private final Map<BaseType, Map<?, ?>> global_cache = new ConcurrentHashMap<>();

	public DefinitionsProvider(GeneratorConfiguration generatorConfiguration) {
		this.generatorName = generatorConfiguration.getGeneratorName();

		for (ModElementType<?> type : ModElementTypeLoader.getAllModElementTypes()) {
			try {
				Map<?, ?> result = YamlMerge.multiLoadYAML(PluginLoader.INSTANCE,
						generatorName + "/" + type.getRegistryName().toLowerCase(Locale.ENGLISH) + ".definition.yaml",
						new AdaptiveYamlMergePolicy("name"));
				if (result.isEmpty()) // definition not specified
					continue;

				cache.put(type, new ConcurrentHashMap<>(result)); // add definition to the cache
			} catch (YamlEngineException | IOException e) {
				LOG.error("[{}] Error: {}", generatorName, e.getMessage(), e);
			}
		}

		// Preload global definitions for base types known at generator construction time
		for (BaseType type : BaseType.values()) {
			global_cache.put(type, getDefinitionFor(type));
		}
	}

	@Nullable public Map<?, ?> getModElementDefinition(ModElementType<?> elementType) {
		return cache.get(elementType);
	}

	@Nonnull public Map<?, ?> getBaseTypeDefinition(BaseType baseType) {
		return global_cache.computeIfAbsent(baseType, this::getDefinitionFor);
	}

	@Nonnull private Map<?, ?> getDefinitionFor(BaseType baseType) {
		try {
			Map<?, ?> result = YamlMerge.multiLoadYAML(PluginLoader.INSTANCE,
					generatorName + "/common." + baseType.getPluralName() + ".yaml",
					new AdaptiveYamlMergePolicy("name"));
			return result.isEmpty() ? Map.of() : new ConcurrentHashMap<>(result);
		} catch (YamlEngineException | IOException e) {
			LOG.info("[{}] Error: {}", generatorName, e.getMessage());
			return Map.of();
		}
	}

}
