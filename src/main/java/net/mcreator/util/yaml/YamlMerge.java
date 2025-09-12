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

package net.mcreator.util.yaml;

import net.mcreator.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class YamlMerge {

	private static final Logger LOG = LogManager.getLogger(YamlMerge.class);

	public static Map<?, ?> multiLoadYAML(URLClassLoader classLoader, String resourcePath)
			throws YamlEngineException, IOException {
		return multiLoadYAML(classLoader, resourcePath, null);
	}

	public static Map<?, ?> multiLoadYAML(URLClassLoader classLoader, String resourcePath, @Nullable MergePolicy policy)
			throws YamlEngineException, IOException {
		List<Map<?, ?>> loadedMaps = new ArrayList<>();

		List<URL> yamlResources = Collections.list(classLoader.getResources(resourcePath));

		// YAML resources from plugins with higher priority are present in the list first, but we want to reverse this
		// as other plugins with lower priority can otherwise apply merge changes to entries loaded from plugins with
		// higher priority. As we reverse the list, it means entries from the plugin with the lowest priority will be
		// loaded first and entries/merge changes from higher priority plugins will be applied later
		yamlResources = yamlResources.reversed();

		yamlResources.forEach(resource -> {
			String yamlString = FileIO.readResourceToString(resource);

			if (!yamlString.isEmpty()) {
				loadedMaps.add((Map<?, ?>) new Load(YamlUtil.getSimpleLoadSettings()).loadFromString(yamlString));
			}
		});

		if (policy == null)
			policy = MergePolicy.DEFAULT;

		return deepMerge(loadedMaps, policy);
	}

	private static Map<Object, Object> deepMerge(List<Map<?, ?>> maps, MergePolicy policy) {
		Map<Object, Object> result = new HashMap<>();
		maps.forEach(map -> mergeInto(result, map, policy));
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void mergeInto(Map<Object, Object> target, Map<?, ?> source, MergePolicy policy) {
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
					mergeInto((Map<Object, Object>) existing, (Map<?, ?>) value, policy);
				} catch (Throwable t) {
					LOG.warn("Failed to merge key {}", key, t);
				}
			} else if (existing instanceof List && value instanceof List) {
				try {
					policy.mergeList(key, (List<Object>) existing, (List<Object>) value);
				} catch (Throwable t) {
					LOG.warn("Failed to merge key {}", key, t);
				}
			} else {
				try {
					target.put(key, policy.mergeScalar(key, existing, value));
				} catch (Throwable t) {
					LOG.warn("Failed to merge key {}", key, t);
				}
			}
		}
	}

	public interface MergePolicy {

		MergePolicy DEFAULT = new MergePolicy() {};

		default void mergeList(Object key, List<Object> existing, List<Object> addition) throws Exception {
			try {
				existing.addAll(addition);
			} catch (Throwable t) {
				throw new Exception(t);
			}
		}

		// Since lower priority plugin data is loaded first, we need to override scalars so scalar from the
		// plugin with the higher priority can override the scalar from the plugin with lower priority
		default Object mergeScalar(Object key, Object existing, Object addition) throws Exception {
			try {
				return addition; // use the new value
			} catch (Throwable t) {
				throw new Exception(t);
			}
		}

	}

}
