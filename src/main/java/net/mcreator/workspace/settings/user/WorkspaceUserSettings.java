/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.workspace.settings.user;

import net.mcreator.ui.component.tree.SerializableTreeExpansionState;

import javax.annotation.Nullable;

public class WorkspaceUserSettings {

	public IconSize workspacePanelIconSize = IconSize.TILES;
	public SortType workspacePanelSortType = SortType.CREATED;
	public boolean workspacePanelSortAscending = true;

	public int projectBrowserSplitPos = 0;
	@Nullable public SerializableTreeExpansionState projectBrowserState = null;

	public enum SortType {
		NAME, CREATED, TYPE
	}

	public enum IconSize {
		TILES, LARGE, MEDIUM, SMALL, LIST, DETAILS
	}

}
