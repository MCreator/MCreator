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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ListDiff {

	public static <T> DiffResult<T> getListDiff(Collection<T> oldList, Collection<T> newList) {
		Set<T> changed = new HashSet<>();
		Set<T> removed = new HashSet<>();
		Set<T> added = new HashSet<>();

		// check for for changed
		for (T elemetInOldList : oldList) {
			// check for changes
			for (T elementInNewList : newList)
				if (elementInNewList
						.equals(elemetInOldList)) // if they are the same by means of equals, we check if they changed
					if (!GSONCompare.deepEquals(elemetInOldList, elementInNewList))
						changed.add(elemetInOldList);

			// check for elements that were removed
			if (!newList.contains(elemetInOldList))
				removed.add(elemetInOldList);
		}

		// check for any new elements
		for (T elementInNewList : newList)
			if (!oldList.contains(elementInNewList))
				added.add(elementInNewList);

		return new DiffResult<>(changed, removed, added);
	}

}
