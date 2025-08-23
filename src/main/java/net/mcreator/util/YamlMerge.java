/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.util;

import net.mcreator.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;

import java.net.URL;
import java.util.*;

public class YamlMerge {

	private static final Logger LOG = LogManager.getLogger(YamlMerge.class);

	public static Map<?, ?> multiLoadYAML(Enumeration<URL> resources) {
		List<Map<?, ?>> loadedMaps = new ArrayList<>();

		Collections.list(resources).forEach(resource -> {
			String yamlString = FileIO.readResourceToString(resource);

			if (!yamlString.isEmpty()) {
				loadedMaps.add((Map<?, ?>) new Load(YamlUtil.getSimpleLoadSettings()).loadFromString(yamlString));
			}
		});

		return deepMerge(loadedMaps);
	}

	private static Map<Object, Object> deepMerge(List<Map<?, ?>> maps) {
		Map<Object, Object> result = new HashMap<>();
		maps.forEach(map -> mergeInto(result, map));
		return result;
	}

	@SuppressWarnings("unchecked") private static void mergeInto(Map<Object, Object> target, Map<?, ?> source) {
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (!target.containsKey(key)) {
				target.put(key, value);
				continue;
			}

			Object existing = target.get(key);

			if (existing instanceof Map && value instanceof Map) {
				try {
					mergeInto((Map<Object, Object>) existing, (Map<?, ?>) value);
				} catch (Throwable t) {
					LOG.warn("Failed to merge key {}", key, t);
				}
			} else if (existing instanceof List && value instanceof List) {
				try {
					((List<Object>) existing).addAll((List<Object>) value);
				} catch (Throwable t) {
					LOG.warn("Failed to merge key {}", key, t);
				}
			}
			// No need to check scalars, scalar already exists -> first wins
		}
	}

}
