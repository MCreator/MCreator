/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.plugin.events.workspace;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;

public class WorkspaceRefactoringEvent extends MCREvent {
	private MCreator mcreator;
	private WorkspaceSettingsChange change;

	/**
	 * <p>An event triggered each time MCreator refactors a {@link net.mcreator.workspace.Workspace}.
	 * This is called BEFORE MCreator starts refactoring, but when the refactor is sure to happen.</p>
	 *
	 * @param mcreator <p>The {@link MCreator} of the workspace</p>
	 * @param change   <p>This variable contains the new workspace settings.</p>
	 */
	public WorkspaceRefactoringEvent(MCreator mcreator, WorkspaceSettingsChange change) {
		this.mcreator = mcreator;
		this.change = change;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public WorkspaceSettingsChange getChange() {
		return change;
	}
}
