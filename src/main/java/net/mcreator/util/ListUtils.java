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

	public static <T> List<T> merge(Collection<T> a, Collection<T> b) {
		List<T> retval = new ArrayList<>(a);
		retval.addAll(b);
		return retval;
	}

	public static <T> Set<T> mergeNoDuplicates(Collection<T> a, Collection<T> b) {
		Set<T> retval = new HashSet<>(a);
		retval.addAll(b);
		return retval;
	}

	public static <T> List<T> intersect(Collection<T> a, Collection<T> b) {
		List<T> retval = new ArrayList<>(a);
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

}
