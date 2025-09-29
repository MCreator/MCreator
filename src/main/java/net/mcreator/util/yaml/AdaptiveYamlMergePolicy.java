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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptiveYamlMergePolicy implements YamlMerge.MergePolicy {

	private final String uniqueKeySource;

	public AdaptiveYamlMergePolicy(String uniqueKeySource) {
		this.uniqueKeySource = uniqueKeySource;
	}

	/**
	 * Merges two lists. The default implementation appends the addition list to the existing list.
	 * If the lists contain maps with a specified unique key, it merges them based on the merge policy for each entry.
	 * <p/>
	 * Possible merge policies (merging is done for every plugin depending on plugin weight):
	 * - append (default): adds the entry to the end of the list
	 * - append_start: adds the entry to the start of the list
	 * - remove_all: removes all entries that match the unique key and does not add the entry
	 * - override_all: removes all entries that match the unique key and adds the entry
	 * <p>
	 * Keep in mind that a plugin evaluated after this plugin can re-add an entry this plugin requested deletion of
	 */
	// When this method is called, it means we are merging addition onto the existing list
	// where the existing list is from a plugin with higher loading priority
	@Override public void mergeList(Object key, List<Object> existing, List<Object> addition) throws Exception {
		try {
			// Check if the list we are merging is a list of template definitions
			if (!existing.isEmpty() && existing.getFirst() instanceof Map<?, ?> map) {
				if (map.containsKey(uniqueKeySource)) {
					List<Map<?, ?>> appendList = new ArrayList<>();
					List<Map<?, ?>> appendStartList = new ArrayList<>();
					List<Map<?, ?>> deleteList = new ArrayList<>();
					List<Map<?, ?>> overrideList = new ArrayList<>();

					// Iterate additions and based on their policy, add them the correct way
					addition.stream().map(e -> (Map<?, ?>) e).forEach(e -> {
						String mergePolicy = e.get("_merge_policy") instanceof String s ? s : "append";
						switch (mergePolicy) {
						case "append" -> appendList.add(e);
						case "append_start" -> appendStartList.add(e);
						case "remove_all" -> deleteList.add(e);
						case "override_all" -> {
							deleteList.add(e);
							overrideList.add(e);
						}
						}
					});

					// First, we append entries at the end of the list
					existing.addAll(appendList);

					// Then, we append entries at the start of the list
					existing.addAll(0, appendStartList);

					// Then, we remove all entries that match the unique keys of deleteList
					List<Map<?, ?>> toRemove = new ArrayList<>();
					existing.stream().map(e -> (Map<?, ?>) e).forEach(e -> {
						if (e.containsKey(uniqueKeySource) && deleteList.stream()
								.anyMatch(d -> d.get(uniqueKeySource).equals(e.get(uniqueKeySource)))) {
							toRemove.add(e);
						}
					});
					existing.removeAll(toRemove);

					// Finally, we add overrides for entries that were deleted in the previous step
					existing.addAll(overrideList);

					return; // do not fallthrough to default behavior of super.mergeList
				}
			}
		} catch (Throwable t) {
			throw new Exception(t);
		}

		YamlMerge.MergePolicy.super.mergeList(key, existing, addition);
	}

}
