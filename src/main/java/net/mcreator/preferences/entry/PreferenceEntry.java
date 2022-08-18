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

package net.mcreator.preferences.entry;

import net.mcreator.preferences.PreferencesRegistry;

public class PreferenceEntry<T> {
	private final String id;
	private T value;
	private final PreferenceSection section;

	public PreferenceEntry(String id, T value, PreferenceSection section) {
		this.id = id;
		this.value = value;
		this.section = section;
	}

	public String getID() {
		return id;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public PreferenceSection getSection() {
		return section;
	}

	@Override public String toString() {
		return id;
	}

}
