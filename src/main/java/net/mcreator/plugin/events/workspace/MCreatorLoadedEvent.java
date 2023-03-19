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

public class MCreatorLoadedEvent extends MCREvent {

	private final MCreator mcreator;

	/**
	 * <p>An event triggered when a new {@link MCreator} window is opened, meaning a new {@link net.mcreator.workspace.Workspace} has been created or opened.</p>
	 *
	 * @param mcreator <p>The opened MCreator window</p>
	 */
	public MCreatorLoadedEvent(MCreator mcreator) {
		this.mcreator = mcreator;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

}
