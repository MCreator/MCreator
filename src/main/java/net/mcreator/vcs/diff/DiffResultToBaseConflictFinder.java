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

import net.mcreator.util.diff.DiffResult;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiffResultToBaseConflictFinder {

	public static <T> Set<MergeHandle<T>> findConflicts(DiffResult<T> baseToLocal, DiffResult<T> baseToRemote) {
		Set<MergeHandle<T>> mergeHandlesSet = new HashSet<>();

		List<AffectedObjectWithType<T>> affectedLocal = getAffected(baseToLocal);
		List<AffectedObjectWithType<T>> affectedRemote = getAffected(baseToRemote);

		for (AffectedObjectWithType<T> local : affectedLocal) {
			for (AffectedObjectWithType<T> remote : affectedRemote) {
				// intersect changes in A and B compared to base
				// such intersections are most likely conflicts, handle them
				if (local.equals(remote)) { // same object (equals of T should compare by key, not by content)
					if (local.changeType() == remote.changeType() && local.changeType() == DiffEntry.ChangeType.DELETE)
						continue; // if both actions are delete, this is actually not a conflict

					mergeHandlesSet.add(new MergeHandle<>(local.affected(), remote.affected(), local.changeType(),
							remote.changeType()));
				}
			}
		}

		return mergeHandlesSet;
	}

	private static <T> List<AffectedObjectWithType<T>> getAffected(DiffResult<T> diffResult) {
		List<AffectedObjectWithType<T>> retval = new ArrayList<>();
		retval.addAll(
				diffResult.changed().stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.MODIFY))
						.toList());
		retval.addAll(
				diffResult.removed().stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.DELETE))
						.toList());
		retval.addAll(diffResult.added().stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.ADD))
				.toList());
		return retval;
	}

}
