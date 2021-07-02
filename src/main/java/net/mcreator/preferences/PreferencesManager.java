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
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;

public class PreferencesManager {

	private static final Logger LOG = LogManager.getLogger("Preferences Manager");

	public static GlobalPreferencesData GlobalPREFERENCES = new GlobalPreferencesData();
	public static PreferencesData PREFERENCES = new PreferencesData();

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static final File preferencesFile = UserFolderManager.getFileFromUserFolder("preferences");

	private static File workspacePreferencesFile;

	public static void loadGlobalPreferences() {
		if (!UserFolderManager.getFileFromUserFolder("preferences").isFile()) {
			storeGlobalPreferences(new GlobalPreferencesData());
			LOG.info("Preferences not created yet. Loading defaults.");
		} else {
			try {
				GlobalPREFERENCES = gson.fromJson(FileIO.readFileToString(preferencesFile), GlobalPreferencesData.class);
				if (GlobalPREFERENCES == null)
					throw new NullPointerException("Preferences are null!");
				LOG.debug("Loading global preferences from " + preferencesFile);
			} catch (Exception e) {
				LOG.error("Failed to load preferences. Reloading defaults!", e);
				storeGlobalPreferences(new GlobalPreferencesData());
			}
		}
	}

	public static void loadWorkspacePreferences(@Nonnull File workspaceFile, @Nonnull Workspace workspace) {
		File workspacePreferencesFile = new File(workspaceFile, "preferences");
		setWorkspacePreferencesFile(workspacePreferencesFile);

		if (!workspacePreferencesFile.isFile()) {
			storeWorkspacePreferences(new PreferencesData());
			LOG.info("Preferences not created yet. Loading defaults.");
		} else {
			try {
				PREFERENCES = gson.fromJson(FileIO.readFileToString(workspacePreferencesFile), PreferencesData.class);
				if (PREFERENCES == null)
					throw new NullPointerException("Preferences are null!");
				LOG.debug("Loading preferences from " + preferencesFile);
			} catch (Exception e) {
				LOG.error("Failed to load preferences. Reloading defaults!", e);
				storeWorkspacePreferences(new PreferencesData());
			}
		}
	}

	public static void setWorkspacePreferencesFile(@Nonnull File workspaceFile) {
		PreferencesManager.workspacePreferencesFile = new File(workspaceFile, "preferences");
	}

	public static void storeGlobalPreferences(GlobalPreferencesData data) {
		GlobalPREFERENCES = data;
		FileIO.writeStringToFile(gson.toJson(data), preferencesFile);
	}

	public static void storeWorkspacePreferences(PreferencesData data) {
		PREFERENCES = data;
		FileIO.writeStringToFile(gson.toJson(data), workspacePreferencesFile.getParentFile());
	}

}
