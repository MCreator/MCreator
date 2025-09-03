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

import javax.annotation.Nullable;
import java.net.URL;
import java.util.*;

public class YamlMerge {

	private static final Logger LOG = LogManager.getLogger(YamlMerge.class);

	public static Map<?, ?> multiLoadYAML(Enumeration<URL> resources) {
		return multiLoadYAML(resources, null);
	}

	public static Map<?, ?> multiLoadYAML(Enumeration<URL> resources, @Nullable MergePolicy policy) {
		List<Map<?, ?>> loadedMaps = new ArrayList<>();

		Collections.list(resources).forEach(resource -> {
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
					policy.mergeScalar(key, existing, value);
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
				// merge lists so that new entries are added on the top, since most
				// processors use the last match in MCreator's yaml files as the final result
				existing.addAll(0, addition);
			} catch (Throwable t) {
				throw new Exception(t);
			}
		}

		default void mergeScalar(Object key, Object existing, Object addition) throws Exception {
		}

	}

}
