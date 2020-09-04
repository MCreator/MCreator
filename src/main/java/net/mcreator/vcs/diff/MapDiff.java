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

package net.mcreator.vcs.diff;

import net.mcreator.util.GSONCompare;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapDiff {

	public static <T, U> DiffResult<T> getMapDiff(Map<T, U> oldMap, Map<T, U> newMap) {
		Set<T> changed = new HashSet<>();
		Set<T> removed = new HashSet<>();
		Set<T> added = new HashSet<>();

		// check for changed values
		for (T keyInOldMap : oldMap.keySet()) {
			// check for changed
			for (T keyInNewMap : newMap.keySet())
				if (keyInOldMap.equals(keyInNewMap)) // overlapping keys, check content
					if (!GSONCompare.deepEquals(oldMap.get(keyInOldMap), newMap.get(keyInNewMap)))
						changed.add(keyInOldMap);

			// check for removed
			if (!newMap.containsKey(keyInOldMap))
				removed.add(keyInOldMap);
		}

		// check for added
		for (T keyInNewMap : newMap.keySet())
			if (!oldMap.containsKey(keyInNewMap))
				added.add(keyInNewMap);

		return new DiffResult<>(changed, removed, added);
	}

}
