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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.preferences.entry.PreferenceEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PreferencesRegistry {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");
	private static final File preferencesFile = UserFolderManager.getFileFromUserFolder("preferences.json");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	public static Preferences PREFERENCES;
	private static List<PreferenceEntry<?>> PREFERENCE_ENTRIES;

	public static void init() {
		PREFERENCE_ENTRIES = new ArrayList<>();
		PREFERENCES = new Preferences();
		loadPreferences();
	}

	public static void loadPreferences() {
		if (!preferencesFile.isFile()) {
			savePreferences();
			LOG.info("Preferences not created yet. Loading defaults.");
		} else {
			try {
				LOG.debug("Loading preferences from " + preferencesFile);
				PreferenceEntry<?>[] list = gson.fromJson(FileIO.readFileToString(preferencesFile), PreferenceEntry[].class);
				Arrays.stream(list).forEach(entry -> PREFERENCE_ENTRIES.forEach(preference -> {
					if (preference.getID().equals(entry.getID())) {
						if (preference.getValue() instanceof Locale)
							preference.setValue(new Locale((String) entry.getValue()));
						else if (preference.getValue() instanceof Color)
							preference.setValue(new Color((int)(double) entry.getValue()));
						else
							preference.setValue(entry.getValue());
					}
				}));
			} catch (Exception e) {
				LOG.error("Failed to load preferences. Reloading defaults!", e);
				savePreferences();
			}
		}
	}

	public static void savePreferences() {
		// We create a temp list, so we can do changes without affecting the cache
		List<PreferenceEntry<?>> list = PREFERENCE_ENTRIES;
		list.forEach(entry -> {
			// We change the full Color object to the RGB code, so we can decode it when loading preferences (fixing a problem)
			if (entry.getValue() instanceof Color color)
				entry.setValue(color.getRGB());
		});
		FileIO.writeStringToFile(gson.toJson(list), preferencesFile);
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