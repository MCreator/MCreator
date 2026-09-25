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

public class ApplicationFullyLoadedEvent extends MCREvent {

	private final MCreatorApplication mcreatorApplication;

	/**
	 * <p>An event triggered when the application loader has finished. Unlike {@link ApplicationLoadedEvent}, which
	 * is triggered right after plugins are loaded, this event is triggered after generators, integrations such as
	 * the MCP server and the workspace selector have been initialized. Opening of initial MCreator windows may
	 * still be in progress on the Swing thread when this event is triggered.</p>
	 *
	 * @param mcreatorApplication <p>The application instance that finished loading</p>
	 */
	public ApplicationFullyLoadedEvent(MCreatorApplication mcreatorApplication) {
		this.mcreatorApplication = mcreatorApplication;
	}

	public MCreatorApplication getMCreatorApplication() {
		return mcreatorApplication;
	}

}
