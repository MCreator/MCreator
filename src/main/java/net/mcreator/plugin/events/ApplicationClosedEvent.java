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

package net.mcreator.plugin.events;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreatorApplication;

public class ApplicationClosedEvent extends MCREvent {

	private final MCreatorApplication mcreatorApplication;

	/**
	 * <p>An event triggered when MCreator is about to exit. It is called after all open {@link net.mcreator.ui.MCreator}
	 * windows have been closed successfully, and before preferences are saved, windows are disposed and plugins are unloaded.
	 * This is the last point at which plugins can perform cleanup work or store their state.</p>
	 *
	 * @param mcreatorApplication <p>The application instance that is closing</p>
	 */
	public ApplicationClosedEvent(MCreatorApplication mcreatorApplication) {
		this.mcreatorApplication = mcreatorApplication;
	}

	public MCreatorApplication getMCreatorApplication() {
		return mcreatorApplication;
	}

}
