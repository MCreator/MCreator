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

package net.mcreator.element.types.interfaces;

import net.mcreator.ui.init.L10N;

import java.util.List;

@SuppressWarnings("unused") public interface IColors {

	default List<ColorEntry> colorEntries(String description, String key) {
		return List.of(new ColorEntry("blue", description, key), new ColorEntry("white", description, key),
				new ColorEntry("orange", description, key), new ColorEntry("magenta", description, key),
				new ColorEntry("light_blue", description, key), new ColorEntry("yellow", description, key),
				new ColorEntry("lime", description, key), new ColorEntry("pink", description, key),
				new ColorEntry("gray", description, key), new ColorEntry("light_gray", description, key),
				new ColorEntry("cyan", description, key), new ColorEntry("purple", description, key),
				new ColorEntry("brown", description, key), new ColorEntry("green", description, key),
				new ColorEntry("red", description, key), new ColorEntry("black", description, key));
	}

	record ColorEntry(String color, String description, String key) {
		public String getDescription() {
			return L10N.t(key + color, description);
		}
	}
}