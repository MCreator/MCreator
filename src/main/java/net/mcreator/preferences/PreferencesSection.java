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

package net.mcreator.preferences;

public abstract class PreferencesSection {

	private final String preferencesIdentifier;

	public PreferencesSection(String preferencesIdentifier) {
		this.preferencesIdentifier = preferencesIdentifier;
	}

	public final <T, S extends PreferencesEntry<T>> S addEntry(S entry) {
		entry.setSection(this);
		PreferencesManager.register(preferencesIdentifier, entry);
		return entry;
	}

	public final <T, S extends PreferencesEntry<T>> S addPluginEntry(String pluginPreferencesIdentifier, S entry) {
		entry.setSection(this);
		PreferencesManager.register(pluginPreferencesIdentifier, entry);
		return entry;
	}

	public boolean isVisible() {
		return true;
	}

	/**
	 * @return Section key this preferences data collection belongs to
	 */
	public abstract String getSectionKey();

}
