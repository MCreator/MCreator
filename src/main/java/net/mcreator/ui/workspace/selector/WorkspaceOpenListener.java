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

import java.io.File;

public interface WorkspaceOpenListener {

	default void workspaceOpened(File workspaceFolder) {
		workspaceOpened(workspaceFolder, false);
	}

	/**
	 * Called when workspace is opened
	 *
	 * @param workspaceFolder Folder in which workspace file is located
	 * @param forceRegenerate If set to true, the workspace being open should trigger code regenerate action
	 */
	void workspaceOpened(File workspaceFolder, boolean forceRegenerate);

}
