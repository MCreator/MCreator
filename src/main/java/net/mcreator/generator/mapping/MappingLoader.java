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

package net.mcreator.generator.mapping;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.YamlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MappingLoader {

	private static final Logger LOG = LogManager.getLogger("Name Mapper");

	private final Map<String, Map<?, ?>> mappings = new ConcurrentHashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" }) public MappingLoader(GeneratorConfiguration generatorConfiguration) {
		Set<String> fileNames = new LinkedHashSet<>();
		for (String templateLoaderPath : generatorConfiguration.getGeneratorPaths("mappings")) {
			fileNames.addAll(PluginLoader.INSTANCE.getResources(templateLoaderPath.replace('/', '.'),
					Pattern.compile(".*\\.yaml")));
		}

		Load yamlLoad = new Load(YamlUtil.getSimpleLoadSettings());

		for (String mappingResource : fileNames) {
			String mappingName = mappingResource.split("mappings/")[1].replace(".yaml", "");

			try {
				Enumeration<URL> resources = PluginLoader.INSTANCE.getResources(mappingResource);
				Collections.list(resources).forEach(resource -> {
					String config = FileIO.readResourceToString(resource);

					try {
						Map<?, ?> mappingsFromFile = Collections.synchronizedMap(
								new LinkedHashMap<>((Map<?, ?>) yamlLoad.loadFromString(config)));

						boolean mergeWithExisting = true;
						if (mappingsFromFile.containsKey("_merge_with_existing"))
							mergeWithExisting = Boolean.parseBoolean(
									mappingsFromFile.get("_merge_with_existing").toString());

						if (mappings.get(mappingName) == null) {
							mappings.put(mappingName, mappingsFromFile);
						} else if (mergeWithExisting) { // merge new mappings with existing (existing have priority), if mappings allow this
							Map merged = Collections.synchronizedMap(new LinkedHashMap());
							merged.putAll(mappingsFromFile); // put new mappings first
							merged.putAll(
									mappings.get(mappingName)); // so they are overriden by old ones in this statement
							mappings.put(mappingName, merged);
						}
					} catch (Exception e) {
						LOG.error("[" + mappingName + "] Error: " + e.getMessage() + " for mapping file "
								+ mappingResource);
					}
				});
			} catch (IOException e) {
				LOG.error("Failed to load mapping resource", e);
			}
		}

		mappings.forEach((name, mapping) -> {
			Set<?> mappingKeys = mapping.keySet();
			DataListLoader.loadDataList(name).stream().filter(entry -> mappingKeys.contains(entry.getName()))
					.forEach(dataListEntry -> dataListEntry.addSupportedGenerator(generatorConfiguration));
		});
	}

	public Map<?, ?> getMapping(String mappingName) {
		if (mappingName == null)
			return null;

		return mappings.get(mappingName);
	}

}
