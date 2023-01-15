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
import net.mcreator.workspace.Workspace;


public class WorkspaceSavedEvent extends MCREvent {
	private final Workspace workspace;

	/**
	 * <p>This event is never called. It only aims to group all events inside a single class.</p>
	 *
	 * @param workspace <p>The {@link Workspace} that will try to be saved.</p>
	 */
	protected WorkspaceSavedEvent(Workspace workspace) {
		this.workspace = workspace;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	/**
	 * <p>It is called every time the method to save the workspace is called. This means that even if the workspace should not and can not be saved,
	 * this event is still triggered. When something happens during this event, the other sub-events are still called and executed,
	 * so a behaviour can happen twice if executed in multiple events.</p>
	 */
	public static class CalledSavingMethod extends WorkspaceSavedEvent {
		public CalledSavingMethod(Workspace workspace) {
			super(workspace);
		}
	}

	/**
	 * <p>This event is called when MCreator started the process of saving a workspace and it detected the workspace can and should be saved.
	 * At this point, the backup is not made yet, but MCreator is sure the workspace file is valid and not unchanged.</p>
	 */
	public static class BeforeSaving extends WorkspaceSavedEvent {

		public BeforeSaving(Workspace workspace) {
			super(workspace);
		}
	}

	/**
	 * <p>This event is called when MCreator started the process of saving a workspace and it detected the workspace can and should be saved.
	 * At this point, the backup is not made yet, but MCreator is sure the workspace file is valid and not unchanged.</p>
	 */
	public static class AfterSaving extends WorkspaceSavedEvent {

		public AfterSaving(Workspace workspace) {
			super(workspace);
		}
	}
}
