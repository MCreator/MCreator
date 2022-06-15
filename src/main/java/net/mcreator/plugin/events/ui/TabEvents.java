/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.plugin.events.ui;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreatorTabs;

/**
 * <p>Events for different actions of a {@link MCreatorTabs.Tab} component.</p>
 */
public class TabEvents extends MCREvent {

	private final MCreatorTabs.Tab tab;

	public TabEvents(MCreatorTabs.Tab tab) {
		this.tab = tab;
	}

	public MCreatorTabs.Tab getTab() {
		return tab;
	}

	/**
	 * <p>Triggered when a {@link MCreatorTabs.Tab} is added to the UI.</p>
	 */
	public static class AddTabEvent extends TabEvents {

		public AddTabEvent(MCreatorTabs.Tab tab) {
			super(tab);
		}
	}

	/**
	 * <p>Triggered BEFORE the {@link MCreatorTabs.Tab} is closed by the user.</p>
	 */
	public static class CloseTabEvent extends TabEvents {

		public CloseTabEvent(MCreatorTabs.Tab tab) {
			super(tab);
		}
	}

	/**
	 * <p>Triggered when the user clicks on a {@link MCreatorTabs.Tab} and is shown.
	 * This event is not triggered when the {@link MCreatorTabs.Tab} is added.</p>
	 */
	public static class ShowTabEvent extends TabEvents {

		public ShowTabEvent(MCreatorTabs.Tab tab) {
			super(tab);
		}
	}
}
