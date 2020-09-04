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

import com.esotericsoftware.yamlbeans.YamlReader;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MappingLoader {

	private static final Logger LOG = LogManager.getLogger("Name Mapper");

	private final Map<String, Map<?, ?>> mappings = new ConcurrentHashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" }) public MappingLoader(GeneratorConfiguration generatorConfiguration) {
		Set<String> fileNames = PluginLoader.INSTANCE
				.getResources(generatorConfiguration.getGeneratorName() + ".mappings", Pattern.compile(".*\\.yaml"));

		for (String res : fileNames) {
			String mappingName = res.split("mappings/")[1].replace(".yaml", "");
			String mappingResource = generatorConfiguration.getGeneratorName() + "/mappings/" + mappingName + ".yaml";

			try {
				Enumeration<URL> resources = PluginLoader.INSTANCE.getResources(mappingResource);
				Collections.list(resources).forEach(resource -> {
					String config = FileIO.readResourceToString(resource);

					YamlReader reader = new YamlReader(config);

					try {
						Map<?, ?> mappingsFromFile = new ConcurrentHashMap<>((Map<?, ?>) reader.read());
						if (mappings.get(mappingName) == null) {
							mappings.put(mappingName, mappingsFromFile);
						} else {
							Map merged = new ConcurrentHashMap();
							merged.putAll(mappings.get(mappingName));
							merged.putAll(mappingsFromFile);
							mappings.put(mappingName, merged);
						}
					} catch (Exception e) {
						LOG.error("[" + mappingName + "] Error: " + e.getMessage());
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
