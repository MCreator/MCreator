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
import net.mcreator.preferences.data.Preferences;
import net.mcreator.preferences.entries.PreferenceEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class PreferencesManager {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	/**
	 * <p>Store all preferences using an identifier </p>
	 */
	private static Map<String, List<PreferenceEntry<?>>> PREFERENCES_REGISTRY;
	/**
	 * <p>Default values for all preferences. Used when resetting preferences.</p>
	 */
	private static Map<String, List<PreferenceEntry<?>>> DEFAULT_PREFERENCES;


	/**
	 * <p>MCreator's preferences</p>
	 */
	public static Preferences PREFERENCES;

	/**
	 * <p>Init the system and load MCreator's preferences, so entries used inside the launcher can be get.</p>
	 */
	public static void init() {
		PREFERENCES_REGISTRY = new HashMap<>();
		DEFAULT_PREFERENCES = new HashMap<>();
		PREFERENCES = new Preferences();
		loadPreferences("mcreator");
	}

	/**
	 * <p>Once plugins are loaded, we can now load preferences registered by plugins via {@link ApplicationLoadedEvent}.</p>
	 */
	public static void loadPlugins() {
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
		if (!getFile(identifier).isFile()) {
			if (identifier.equals("mcreator") && UserFolderManager.getFileFromUserFolder("preferences").exists()) {
				LOG.info("Old preferences detected. Converting them to the new format.");
				convertOldPreferences();
			} else {
				LOG.info("Preferences not created yet. Loading defaults.");
				savePreferences(identifier);
			}
		} else {
			try {
				LOG.debug("Loading preferences from " + getFile(identifier));
				PreferenceEntry<?>[] list = gson.fromJson(FileIO.readFileToString(getFile(identifier)),
						PreferenceEntry[].class);
				// Convert values from the file to properly work
				Arrays.stream(list).forEach(entry -> getPreferenceEntries(identifier).stream()
						.filter(preference -> preference.getID().equals(entry.getID())).forEach(preference -> {
							if (preference.get() instanceof Locale)
								preference.set(
										Locale.forLanguageTag(((String) entry.get()).replace("_", "-")));
							else if (preference.get() instanceof Color) {
								preference.set(new Color((int) (double) entry.get()));
							}
							else if (entry.get() instanceof Double val) // fix a problem where Gson read numbers as double values
								preference.set(val.intValue());
							else
								preference.set(entry.get());
						}));
			} catch (Exception e) {
				LOG.error("Failed to load preferences. Reloading defaults!", e);
				savePreferences(identifier);
			}
		}
	}

	/**
	 * <p>Save preferences of all identifiers registered inside PREFERENCES_REGISTRY.</p>
	 */
	public static void savePreferences() {
		PREFERENCES_REGISTRY.forEach((identifier, preferences) -> savePreferences(identifier));
	}

	/**
	 * <p>Save the preferences of the specified {@param identifier} inside its own file of the <i>preferences</i> folder.</p>
	 *
	 * @param identifier <i>Indicate the preferences to save. The identifier is also used for the file's name.</i>
	 */
	public static void savePreferences(String identifier) {
		// We create a temp list, so we can do changes without affecting the cache
		List<PreferenceEntry<?>> list = new ArrayList<>(getPreferenceEntries(identifier));
		list.forEach(entry -> {
			// We change the full Color object to the RGB code, so we can decode it when loading preferences (fixing a problem)
			if (entry.get() instanceof Color color && getFile("mcreator").exists())
				entry.set(color.getRGB());
		});
		FileIO.writeStringToFile(gson.toJson(list), getFile(identifier));
	}

	/**
	 * <p>A method to convert old MCreator's preferences from the old system to the new system.</p>
	 */
	private static void convertOldPreferences() {
		File file = UserFolderManager.getFileFromUserFolder("preferences");
		JsonObject obj = gson.fromJson(FileIO.readFileToString(file), JsonObject.class);
		getPreferenceEntries("mcreator").forEach(entry -> {
			JsonElement value = obj.get(entry.getSection()).getAsJsonObject()
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
			else if (entry.get() instanceof WorkspacePreferenceEnums.IconSize)
				entry.set(WorkspacePreferenceEnums.IconSize.valueOf(value.getAsString()));
			else if (entry.get() instanceof WorkspacePreferenceEnums.SortType)
				entry.set(WorkspacePreferenceEnums.SortType.valueOf(value.getAsString()));
		});
		savePreferences("mcreator");
	}

	/**
	 * <p>Reset to default values all preferences from all identifiers</p>
	 */
	public static void reset() {
		PREFERENCES_REGISTRY.clear();
		PREFERENCES_REGISTRY.putAll(DEFAULT_PREFERENCES);
	}

	public static <T, S extends PreferenceEntry<T>> S register(String identifier, S entry) {
		if (PREFERENCES_REGISTRY.containsKey(identifier)) {
			PREFERENCES_REGISTRY.get(identifier).add(entry);
			DEFAULT_PREFERENCES.get(identifier).add(entry);
		} else {
			PREFERENCES_REGISTRY.put(identifier, new ArrayList<>() {{
				add(entry);
			}});
			DEFAULT_PREFERENCES.put(identifier, new ArrayList<>() {{
				add(entry);
			}});
		}
		return entry;
	}

	public static Map<String, List<PreferenceEntry<?>>> getPreferencesRegistry() {
		return PREFERENCES_REGISTRY;
	}

	public static List<PreferenceEntry<?>> getPreferenceEntries(String identifier) {
		return PREFERENCES_REGISTRY.get(identifier);
	}

	private static File getFile(String identifier) {
		return UserFolderManager.getFileFromUserFolder("options/" + identifier + ".json");
	}
}