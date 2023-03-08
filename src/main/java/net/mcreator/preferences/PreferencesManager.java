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

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.plugin.events.ApplicationLoadedEvent;
import net.mcreator.preferences.data.HiddenSection;
import net.mcreator.preferences.data.PreferencesData;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class PreferencesManager {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");

	private static final File file = UserFolderManager.getFileFromUserFolder("preferences.json");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	/**
	 * <p>Store values when the preferences' file is loaded</p>
	 */
	private static Map<String, Map<String, List<LinkedTreeMap<?, ?>>>> loadedPreferences;

	/**
	 * <p>Store all preferences using an identifier </p>
	 */
	private static Map<String, List<PreferencesEntry<?>>> PREFERENCES_REGISTRY;

	/**
	 * <p>MCreator's preferences</p>
	 */
	public static PreferencesData PREFERENCES;

	/**
	 * <p>Init the system and load MCreator's preferences, so entries used inside the launcher can be get.</p>
	 */
	public static void init() {
		PREFERENCES_REGISTRY = new HashMap<>();
		PREFERENCES = new PreferencesData();

		if (!file.isFile() && UserFolderManager.getFileFromUserFolder("preferences").exists()) {
			LOG.debug("Old preferences detected. Converting them to the new format.");
			convertOldPreferences();
		} else {
			try {
				loadedPreferences = gson.fromJson(FileIO.readFileToString(file), Map.class);
			} catch (JsonSyntaxException e) {
				LOG.error("The preferences file could not be loaded. Default values will be used.", e);
			}
			loadPreferences("mcreator");
		}
	}

	/**
	 * <p>Once plugins are loaded, we can now load preferences registered by them with {@link ApplicationLoadedEvent}.</p>
	 */
	public static void initNonCore() {
		PREFERENCES_REGISTRY.forEach((identifier, preferences) -> {
			if (!identifier.equals("mcreator"))
				loadPreferences(identifier);
		});
	}

	/**
	 * <p>Load all preferences registered under the provided {@param identifier}.</p>
	 *
	 * @param identifier <p> The identifier used to get the preference file to load</p>
	 */
	public static void loadPreferences(String identifier) {
		try {
			LOG.debug("Loading preferences from " + identifier);
			if (loadedPreferences.containsKey(identifier)) {
				// Convert values from the file to properly work
				loadedPreferences.get(identifier).forEach((category, entries) -> entries.forEach(
						entry -> getPreferencesRegistry().get(identifier).stream()
								.filter(preference -> preference.getID().equals(entry.get("id")))
								.forEach(preference -> {
									if (preference.get() instanceof Locale)
										preference.set(
												Locale.forLanguageTag(((String) entry.get("value")).replace("_", "-")));
									else if (preference.get() instanceof Color)
										preference.set(new Color((int) (double) entry.get("value")));
									else if (entry.get("value") instanceof Double d)
										preference.set(d.intValue());
									else
										preference.set(entry.get("value"));
								})));
			} else {
				LOG.debug(identifier + " has no saved values. Default values will be used.");
			}
		} catch (Exception e) {
			LOG.error("Failed to load preferences. Reloading defaults.", e);
			resetSpecific(identifier);
		}
	}

	/**
	 * <p>Save preferences of all identifiers registered inside PREFERENCES_REGISTRY.</p>
	 */
	public static void savePreferences() {
		Map<String, Map<String, List<PreferencesEntry<?>>>> allPreferences = new HashMap<>();

		PREFERENCES_REGISTRY.forEach((identifier, preferences) -> {
			Map<String, List<PreferencesEntry<?>>> identifierPrefs = new HashMap<>();
			preferences.forEach(entry -> {
				// We check if the section doesn't exist to add it
				if (!identifierPrefs.containsKey(entry.getSectionKey()))
					identifierPrefs.put(entry.getSectionKey(), new ArrayList<>());

				// We change the registered value for some types, so we can load them correctly
				if (entry.get() instanceof Color color)
					identifierPrefs.get(entry.getSectionKey())
							.add(new IntegerEntry(entry.getID(), color.getRGB()));
				else if (entry.get() instanceof Locale locale)
					identifierPrefs.get(entry.getSectionKey())
							.add(new StringEntry(entry.getID(), locale.toString()));
				else
					identifierPrefs.get(entry.getSectionKey()).add(entry);

				if (!allPreferences.containsKey(identifier)) {
					allPreferences.put(identifier, identifierPrefs);
				} else {
					allPreferences.get(identifier).putAll(identifierPrefs);
				}
			});
		});

		FileIO.writeStringToFile(gson.toJson(allPreferences), file);
	}

	/**
	 * <p>A method to convert old MCreator's preferences from the old system to the new system.</p>
	 */
	private static void convertOldPreferences() {
		File file = UserFolderManager.getFileFromUserFolder("preferences");
		JsonObject obj = gson.fromJson(FileIO.readFileToString(file), JsonObject.class);
		PREFERENCES_REGISTRY.get("mcreator").forEach(entry -> {
			JsonElement value = obj.get(entry.getSectionKey()).getAsJsonObject()
					.get(entry.getID().replace("autoReloadTabs", "autoreloadTabs").replace("aaText", "aatext")
							.replace("useMacOSMenuBar", "usemacOSMenuBar"));
			if (value == null)
				return; // we use the default value

			if (entry.get() instanceof Number)
				entry.set(value.getAsInt());
			else if (entry.get() instanceof String)
				entry.set(value.getAsString());
			else if (entry.get() instanceof Boolean)
				entry.set(value.getAsBoolean());
			else if (entry.get() instanceof Locale)
				entry.set(Locale.forLanguageTag(value.getAsString().replace("_", "-")));
			else if (entry.get() instanceof Color)
				entry.set(new Color(value.getAsJsonObject().get("value").getAsInt()));
			else if (entry.get() instanceof HiddenSection.IconSize)
				entry.set(HiddenSection.IconSize.valueOf(value.getAsString()));
			else if (entry.get() instanceof HiddenSection.SortType)
				entry.set(HiddenSection.SortType.valueOf(value.getAsString()));
		});
		savePreferences();
	}

	/**
	 * <p>Reset to default values all preferences from all identifiers</p>
	 */
	public static void reset() {
		LOG.debug("Restoring default values for all preferences");
		PREFERENCES_REGISTRY.forEach((identifier, entries) -> resetFromList(entries));
	}

	public static void resetSpecific(String identifier) {
		LOG.debug("Restoring default values for: " + identifier);
		resetFromList(PREFERENCES_REGISTRY.get(identifier));
	}

	private static void resetFromList(List<PreferencesEntry<?>> entries) {
		entries.forEach(PreferencesEntry::reset);
	}

	static <T, S extends PreferencesEntry<T>> void register(String identifier, S entry) {
		if (PREFERENCES_REGISTRY.containsKey(identifier)) {
			PREFERENCES_REGISTRY.get(identifier).add(entry);
		} else {
			PREFERENCES_REGISTRY.put(identifier, new ArrayList<>() {{
				add(entry);
			}});
		}
	}

	public static Map<String, List<PreferencesEntry<?>>> getPreferencesRegistry() {
		return PREFERENCES_REGISTRY;
	}
}