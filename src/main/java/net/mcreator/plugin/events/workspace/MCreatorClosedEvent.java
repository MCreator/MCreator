/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

public class MCreatorClosedEvent extends MCREvent {

	private final MCreator mcreator;

	/**
	 * <p>An event triggered when a {@link MCreator} window is about to close, meaning its {@link net.mcreator.workspace.Workspace}
	 * is being closed. It is called once the close has been confirmed (no Gradle task blocking it), but before tabs are closed,
	 * the workspace is closed and the window is disposed, so the workspace and UI are still fully accessible.</p>
	 *
	 * @param mcreator <p>The MCreator window that is closing</p>
	 */
	public MCreatorClosedEvent(MCreator mcreator) {
		this.mcreator = mcreator;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

}
