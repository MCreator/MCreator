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

import org.eclipse.jgit.diff.DiffEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiffResult<T> {

	private final Set<T> changed;
	private final Set<T> removed;
	private final Set<T> added;

	DiffResult(Set<T> changed, Set<T> removed, Set<T> added) {
		this.changed = changed;
		this.removed = removed;
		this.added = added;
	}

	public Set<T> getChanged() {
		return changed;
	}

	public Set<T> getRemoved() {
		return removed;
	}

	public Set<T> getAdded() {
		return added;
	}

	List<AffectedObjectWithType<T>> getAffected() {
		List<AffectedObjectWithType<T>> retval = new ArrayList<>();
		retval.addAll(changed.stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.MODIFY))
				.collect(Collectors.toList()));
		retval.addAll(removed.stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.DELETE))
				.collect(Collectors.toList()));
		retval.addAll(added.stream().map(e -> new AffectedObjectWithType<>(e, DiffEntry.ChangeType.ADD))
				.collect(Collectors.toList()));
		return retval;
	}

}
