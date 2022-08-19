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
import com.google.gson.JsonElement;
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

public class PreferencesManager {

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
			if (UserFolderManager.getFileFromUserFolder("preferences").exists()) {
				LOG.info("Old preferences detected. Converting them to the new format.");
				convertOldPreferences(UserFolderManager.getFileFromUserFolder("preferences"));
				savePreferences();
				return;
			}
			LOG.info("Preferences not created yet. Loading defaults.");
			savePreferences();
		} else {
			try {
				LOG.debug("Loading preferences from " + preferencesFile);
				PreferenceEntry<?>[] list = gson.fromJson(FileIO.readFileToString(preferencesFile),
						PreferenceEntry[].class);
				Arrays.stream(list).forEach(entry -> PREFERENCE_ENTRIES.forEach(preference -> {
					if (preference.getID().equals(entry.getID())) {
						if (preference.getValue() instanceof Locale)
							preference.setValue(Locale.forLanguageTag(((String) entry.getValue()).replace("_", "-")));
						else if (preference.getValue() instanceof Color)
							preference.setValue(new Color((int) (double) entry.getValue()));
						else if (entry.getValue() instanceof Double val) // fix a problem where Gson read numbers as double
							preference.setValue(val.intValue());
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
			if (entry.getValue() instanceof Color color && preferencesFile.exists())
				entry.setValue(color.getRGB());
		});
		FileIO.writeStringToFile(gson.toJson(list), preferencesFile);
	}

	public static void convertOldPreferences(File file) {
		JsonObject obj = gson.fromJson(FileIO.readFileToString(file), JsonObject.class);
		PREFERENCE_ENTRIES.forEach(entry -> {
			JsonElement value = obj.get(entry.getSection()).getAsJsonObject()
					.get(entry.getID().replace("autoReloadTabs", "autoreloadTabs").replace("aaText", "aatext")
							.replace("useMacOSMenuBar", "usemacOSMenuBar"));
			if (value == null)
				return; // we use the default value

			if (entry.getValue() instanceof Number)
				entry.setValue(value.getAsInt());
			else if (entry.getValue() instanceof String)
				entry.setValue(value.getAsString());
			else if (entry.getValue() instanceof Boolean)
				entry.setValue(value.getAsBoolean());
			else if (entry.getValue() instanceof Locale)
				entry.setValue(Locale.forLanguageTag(value.getAsString().replace("_", "-")));
			else if (entry.getValue() instanceof Color)
				entry.setValue(new Color(value.getAsJsonObject().get("value").getAsInt()));
			else if (entry.getValue() instanceof WorkspacePreferenceEnums.WorkspaceIconSize)
				entry.setValue(WorkspacePreferenceEnums.WorkspaceIconSize.valueOf(value.getAsString()));
			else if (entry.getValue() instanceof WorkspacePreferenceEnums.WorkspaceSortType)
				entry.setValue(WorkspacePreferenceEnums.WorkspaceSortType.valueOf(value.getAsString()));
		});
	}

	public static void reset() {
		PREFERENCES = new Preferences();
	}

	public static <T, C extends PreferenceEntry<T>> C register(C entry) {
		PREFERENCE_ENTRIES.add(entry);
		return entry;
	}

	public static List<PreferenceEntry<?>> getPreferenceEntries() {
		return PREFERENCE_ENTRIES;
	}
}