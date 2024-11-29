/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.workspace.settings.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceFolderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.File;

public class WorkspaceUserSettingsManager implements Closeable {

	private static final Logger LOG = LogManager.getLogger("Workspace user settings");

	private static final Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();

	private final Workspace workspace;

	private final File userSettingsFile;

	private WorkspaceUserSettings userSettings = new WorkspaceUserSettings();

	public WorkspaceUserSettingsManager(Workspace workspace, WorkspaceFolderManager folderManager) {
		this.workspace = workspace;
		this.userSettingsFile = new File(folderManager.getWorkspaceCacheDir(), "userSettings");

		if (userSettingsFile.isFile()) {
			try {
				String json = FileIO.readFileToString(userSettingsFile);
				userSettings = gson.fromJson(json, WorkspaceUserSettings.class);
			} catch (Exception e) {
				LOG.warn("Failed to read user settings", e);
			}
		}
	}

	public WorkspaceUserSettings getUserSettings() {
		return userSettings;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	@Override public void close() {
		try {
			LOG.debug("Storing user workspace settings");
			FileIO.writeStringToFile(gson.toJson(userSettings), userSettingsFile);
		} catch (Exception e) {
			LOG.warn("Failed to store user settings", e);
		}
	}

}
