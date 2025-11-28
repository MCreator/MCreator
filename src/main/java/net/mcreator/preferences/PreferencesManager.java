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
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.plugin.events.ApplicationLoadedEvent;
import net.mcreator.preferences.data.PreferencesData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesManager {

	public static final Logger LOG = LogManager.getLogger("Preferences Manager");

	private static final File PREFERENCES_FILE = UserFolderManager.getFileFromUserFolder("userpreferences");

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create();

	/**
	 * <p>Stores values when the preferences file is loaded</p>
	 */
	@Nullable private static PreferencesFileCache preferencesFileCache;

	/**
	 * <p>Store all preferences using an identifier </p>
	 */
	private static final Map<String, List<PreferencesEntry<?>>> PREFERENCES_REGISTRY = new HashMap<>();

	/**
	 * <p>MCreator's preferences structure holder</p>
	 */
	public static PreferencesData PREFERENCES;

	/**
	 * <p>Init the system and load MCreator's preferences, so entries used inside the launcher can be obtained.</p>
	 */
	public static void init() {
		PREFERENCES = new PreferencesData();

		if (!PREFERENCES_FILE.isFile() && UserFolderManager.getFileFromUserFolder("preferences").exists()) {
			LOG.info("Old preferences file found. Converting the file to the new format.");
			convertOldPreferences();
		}

		loadPreferences(PreferencesData.CORE_PREFERENCES_KEY);
	}

	/**
	 * <p>Once plugins are loaded, we can now load preferences registered by them with {@link ApplicationLoadedEvent}.</p>
	 */
	public static void initNonCore() {
		PREFERENCES_REGISTRY.forEach((identifier, preferences) -> {
			if (!identifier.equals(PreferencesData.CORE_PREFERENCES_KEY))
				loadPreferences(identifier);
		});
	}

	/**
	 * <p>Load all preferences registered under the provided {@code identifier}.</p>
	 *
	 * @param identifier <p> The identifier used to get the preference file to load</p>
	 */
	public static void loadPreferences(String identifier) {
		// Load preferences data from file and cache it
		if (preferencesFileCache == null && PREFERENCES_FILE.isFile()) {
			try {
				preferencesFileCache = gson.fromJson(FileIO.readFileToString(PREFERENCES_FILE),
						PreferencesFileCache.class);
			} catch (JsonSyntaxException e) {
				LOG.error("The preferences file could not be loaded. Default values will be used.", e);
			}
		}

		boolean failedToLoad = false;

		try {
			LOG.debug("Loading preferences from {}", identifier);
			if (preferencesFileCache != null && preferencesFileCache.containsKey(identifier)) {
				// Convert values from the file to properly work
				preferencesFileCache.get(identifier).forEach((section, entries) -> entries.keySet().forEach(
						entryKey -> PREFERENCES_REGISTRY.get(identifier).stream()
								.filter(preference -> preference.getID().equals(entryKey) && preference.getSectionKey()
										.equals(section)).forEach(preference -> {
									JsonElement value = entries.get(entryKey);
									if (value != null && value != JsonNull.INSTANCE) {
										preference.setValueFromJsonElement(value);
									}
								})));
			} else {
				LOG.info("Preferences with identifier {} have no saved values. Default values will be used.",
						identifier);
				failedToLoad = true;
			}
		} catch (Exception e) {
			LOG.warn("Failed to load preferences. Reloading defaults for identifier {}", identifier, e);
			failedToLoad = true;
		}

		if (failedToLoad) {
			resetFromList(PREFERENCES_REGISTRY.get(identifier));
			savePreferences(); // defaults were reloaded, save the preferences file
		}
	}

	/**
	 * <p>Save preferences of all identifiers registered inside PREFERENCES_REGISTRY.</p>
	 */
	public static void savePreferences() {
		PreferencesFileCache allPreferences = new PreferencesFileCache();

		PREFERENCES_REGISTRY.forEach((identifier, preferences) -> {
			PreferencesFileCacheIdentifierGroup identifierPrefs = new PreferencesFileCacheIdentifierGroup();
			preferences.forEach(entry -> {
				// We check if the section doesn't exist to add it
				if (!identifierPrefs.containsKey(entry.getSectionKey()))
					identifierPrefs.put(entry.getSectionKey(), new JsonObject());

				identifierPrefs.get(entry.getSectionKey()).add(entry.getID(), entry.getSerializedValue());

				if (!allPreferences.containsKey(identifier)) {
					allPreferences.put(identifier, identifierPrefs);
				} else {
					allPreferences.get(identifier).putAll(identifierPrefs);
				}
			});
		});

		FileIO.writeStringToFile(gson.toJson(preferencesFileCache = allPreferences), PREFERENCES_FILE);
	}

	/**
	 * <p>A method to convert old MCreator's preferences from the old system to the new system.</p>
	 */
	private static void convertOldPreferences() {
		File file = UserFolderManager.getFileFromUserFolder("preferences");
		JsonObject oldPreferences = gson.fromJson(FileIO.readFileToString(file), JsonObject.class);
		PREFERENCES_REGISTRY.get(PreferencesData.CORE_PREFERENCES_KEY).forEach(entry -> {
			// if the entry section did not exist in old preferences, skip this entry and use its default value
			if (!oldPreferences.has(entry.getSectionKey()))
				return;

			String oldPreferencesKey = entry.getID().replace("autoReloadTabs", "autoreloadTabs")
					.replace("aaText", "aatext").replace("useMacOSMenuBar", "usemacOSMenuBar");

			JsonElement value = oldPreferences.get(entry.getSectionKey()).getAsJsonObject().get(oldPreferencesKey);

			// if not defined in old preferences, we use the default value
			if (value == null || value == JsonNull.INSTANCE)
				return;

			entry.setValueFromJsonElement(value);
		});

		// We save the new preferences
		savePreferences();
	}

	public static void resetFromList(List<PreferencesEntry<?>> entries) {
		entries.forEach(PreferencesEntry::reset);
	}

	/**
	 * <p>This method is a method used by the preferences system in order to register each {@link PreferencesEntry} added by a {@link PreferencesSection}.
	 * It checks if the provided {@code identifier} already exists inside the system and then, proceed to either add the provided {@link PreferencesEntry} with {@code entry} to the existing {@code identifier}'s list of entries or create a new list linked tto this specific {@code identifier}.
	 * This is the method allowing to add a {@link PreferencesEntry} to the loading and saving processes. However, the system already has the capacity to automatically register all {@link PreferencesEntry}, so this method should not be called somewhere else.</p>
	 *
	 * <p>For Java plugins: See {@link PreferencesSection#addEntry(PreferencesEntry)} and {@link PreferencesSection#addPluginEntry(String, PreferencesEntry)} to add custom entries.</p>
	 *
	 * @param identifier A unique {@link String} acting like a mod's id for this system.
	 * @param entry The {@link PreferencesEntry} added via a {@link PreferencesSection}
	 */
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

	private static class PreferencesFileCache extends HashMap<String, PreferencesFileCacheIdentifierGroup> {}

	private static class PreferencesFileCacheIdentifierGroup extends HashMap<String, JsonObject> {}

}