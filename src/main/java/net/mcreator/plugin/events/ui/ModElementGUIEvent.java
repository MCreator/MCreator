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

import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.modgui.ModElementGUI;

/**
 * <p>These events are triggered at different states of a {@link ModElementGUI} loading.</p>
 */
public class ModElementGUIEvent extends TabEvents {

	private final ModElementGUI<?> modElementGUI;

	public ModElementGUIEvent(MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
		super(tab);
		this.modElementGUI = modElementGUI;
	}

	public ModElementGUI<?> getModElementGUI() {
		return modElementGUI;
	}

	public static class Pre extends ModElementGUIEvent {
		public Pre(MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
			super(tab, modElementGUI);
		}
	}

	public static class Post extends ModElementGUIEvent {
		public Post(MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
			super(tab, modElementGUI);
		}
	}
}