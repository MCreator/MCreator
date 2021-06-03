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

package net.mcreator.ui.workspace.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RecentWorkspaces {

	private final List<RecentWorkspaceEntry> list;

	public RecentWorkspaces() {
		list = new ArrayList<>();
	}

	public RecentWorkspaces(Collection<RecentWorkspaceEntry> recentWorkspacesFiltered) {
		list = new ArrayList<>(recentWorkspacesFiltered);
	}

	public List<RecentWorkspaceEntry> getList() {
		return list;
	}

}
