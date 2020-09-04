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

import java.util.Collection;

public class MergeHandle<T> {

	private final T local;
	private final T remote;
	private final DiffEntry.ChangeType localChange;
	private final DiffEntry.ChangeType remoteChange;

	private ResultSide resultSide = ResultSide.LOCAL; // use local side by default

	public MergeHandle(T local, T remote, DiffEntry.ChangeType localChange, DiffEntry.ChangeType remoteChange) {
		this.local = local;
		this.remote = remote;
		this.localChange = localChange;
		this.remoteChange = remoteChange;
	}

	public void selectResultSide(ResultSide resultSide) {
		this.resultSide = resultSide;
	}

	public T getSelectedResult() {
		return resultSide == ResultSide.LOCAL ? local : remote;
	}

	public DiffEntry.ChangeType getSelectedResultChangeType() {
		return resultSide == ResultSide.LOCAL ? localChange : remoteChange;
	}

	public ResultSide getResultSide() {
		return resultSide;
	}

	public T getLocal() {
		return local;
	}

	public T getRemote() {
		return remote;
	}

	public DiffEntry.ChangeType getLocalChange() {
		return localChange;
	}

	public DiffEntry.ChangeType getRemoteChange() {
		return remoteChange;
	}

	@Override public boolean equals(Object o) {
		return o instanceof MergeHandle && ((MergeHandle) o).local.equals(local) && ((MergeHandle) o).remote
				.equals(remote);
	}

	@Override public int hashCode() {
		return local.hashCode();
	}

	@Override public String toString() {
		return local + "[" + localChange.name() + "] : " + remote + "[" + remoteChange.name() + "]";
	}

	public static <T> boolean isElementNotInMergeHandleCollection(Collection<MergeHandle<T>> collection, T element) {
		for (MergeHandle<T> mergeHandle : collection)
			if (mergeHandle.getLocal().equals(element) || mergeHandle.getRemote().equals(element))
				return false;
		return true;
	}

}
