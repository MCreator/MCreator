/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class PreferencesManager {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");

	public static PreferencesData PREFERENCES = new PreferencesData();

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static final File preferencesFile = UserFolderManager.getFileFromUserFolder("preferences");

	public static void loadPreferences() {
		if (!UserFolderManager.getFileFromUserFolder("preferences").isFile()) {
			storePreferences(new PreferencesData());
			LOG.info("Preferences not created yet. Loading defaults.");
		} else {
			try {
				PREFERENCES = gson.fromJson(FileIO.readFileToString(preferencesFile), PreferencesData.class);
				if (PREFERENCES == null)
					throw new NullPointerException("Preferences are null!");
				LOG.debug("Loading preferences from " + preferencesFile);
			} catch (Exception e) {
				LOG.error("Failed to load preferences. Reloading defaults!", e);
				storePreferences(new PreferencesData());
			}
		}
	}

	public static void storePreferences(PreferencesData data) {
		PREFERENCES = data;
		FileIO.writeStringToFile(gson.toJson(data), preferencesFile);
	}

}
