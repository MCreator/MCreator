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

package net.mcreator.preferences;

import net.mcreator.io.UserFolderManager;
import net.mcreator.preferences.entry.PreferenceEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreferencesRegistry {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");
	private static final File preferencesFile = UserFolderManager.getFileFromUserFolder("preferences");

	public static Preferences PREFERENCES;
	private static List<PreferenceEntry<?>> PREFERENCE_ENTRIES;

	public static void init() {
		PREFERENCE_ENTRIES = new ArrayList<>();
		PREFERENCES = new Preferences();
	}

	public static void reset() {
		PREFERENCES = new Preferences();
	}

	public static <T> PreferenceEntry<T> register(PreferenceEntry<T> entry) {
		PREFERENCE_ENTRIES.add(entry);
		return entry;
	}

	public static List<PreferenceEntry<?>> getPreferences() {
		return PREFERENCE_ENTRIES;
	}
}