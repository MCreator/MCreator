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

package net.mcreator.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventMap extends HashMap<Class<? extends MCREvent>, List<MCREventListener<?>>> {

	public <T extends MCREvent> void addEvent(Class<T> key, MCREventListener<T> value) {
		super.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
	}

	@SuppressWarnings("unchecked")
	public <T extends MCREvent> List<MCREventListener<T>> get(Class<? extends MCREvent> key) {
		List<MCREventListener<T>> retval = new ArrayList<>();

		if (super.containsKey(key)) {
			for (MCREventListener<?> listener : super.get(key)) {
				retval.add((MCREventListener<T>) listener);
			}
		}

		return retval;
	}

}
