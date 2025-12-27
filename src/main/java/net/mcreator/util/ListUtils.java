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

package net.mcreator.util;

import java.util.*;

public class ListUtils {

	private static final Random random = new Random();

	@SafeVarargs public static <T> List<T> merge(Collection<T>... collections) {
		List<T> retval = new ArrayList<>();
		for (Collection<T> collection : collections) {
			retval.addAll(collection);
		}
		return retval;
	}

	@SafeVarargs public static <T> Set<T> mergeNoDuplicates(Collection<T>... collections) {
		Set<T> retval = new HashSet<>();
		for (Collection<T> collection : collections) {
			retval.addAll(collection);
		}
		return retval;
	}

	public static <T> Collection<T> intersect(Collection<T> a, Collection<T> b) {
		Set<T> retval = new HashSet<>(a);
		retval.retainAll(b); // only retain a elements that are in b too
		return retval;
	}

	public static <T> void rearrange(List<T> items, T input) {
		int index = items.indexOf(input);
		if (index >= 0) {
			List<T> copy = new ArrayList<>(items.size());
			copy.add(items.get(index));
			copy.addAll(items.subList(0, index));
			copy.addAll(items.subList(index + 1, items.size()));
			items.clear();
			items.addAll(copy);
		}
	}

	public static <T> T getRandomItem(T[] list) {
		int listSize = list.length;
		int randomIndex = random.nextInt(listSize);
		return list[randomIndex];
	}

	public static <T> T getRandomItem(List<T> list) {
		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}

	public static <T> T getRandomItem(Random random, T[] list) {
		int listSize = list.length;
		int randomIndex = random.nextInt(listSize);
		return list[randomIndex];
	}

	public static <T> T getRandomItem(Random random, List<T> list) {
		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}

	/**
	 * Generates the full Cartesian product of a set of keys and their possible values.
	 *
	 * <p>Example: If the map contains:</p>
	 * <pre>
	 *   A -> [1, 2]
	 *   B -> [true, false]
	 * </pre>
	 *
	 * <p>The output will be a list containing:</p>
	 * <pre>
	 *   {A=1, B=true}
	 *   {A=1, B=false}
	 *   {A=2, B=true}
	 *   {A=2, B=false}
	 * </pre>
	 *
	 * @param <K>   key type
	 * @param <V>   value type
	 * @param input map of keys and their possible values; order of keys defines output order
	 * @return all combinations as a list of maps (each map is a unique combination)
	 */
	public static <K, V> List<Map<K, V>> cartesianProduct(Map<K, ? extends List<V>> input) {
		List<K> keys = new ArrayList<>(input.keySet());
		List<Map<K, V>> result = new ArrayList<>();

		if (keys.isEmpty()) {
			return result;
		}

		generateRecursive(input, keys, 0, new LinkedHashMap<>(), result);
		return result;
	}

	/**
	 * Internal recursive generator.
	 *
	 * @param input   original input map
	 * @param keys    ordered list of keys
	 * @param index   current key index being processed
	 * @param current partial combination being built
	 * @param result  output accumulator
	 */
	private static <K, V> void generateRecursive(Map<K, ? extends List<V>> input, List<K> keys, int index,
			Map<K, V> current, List<Map<K, V>> result) {
		// BASE CASE: all keys processed → add completed combination
		if (index == keys.size()) {
			result.add(new LinkedHashMap<>(current));  // deep copy
			return;
		}

		K key = keys.get(index);
		List<V> values = input.get(key);

		for (V val : values) {
			current.put(key, val);
			generateRecursive(input, keys, index + 1, current, result);
		}
	}

}
