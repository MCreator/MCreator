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

package net.mcreator.io;

import net.mcreator.preferences.PreferencesManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class UserFolderManager {

	private static File getUserFolder() {
		var mcreator_home = System.getenv("MCREATOR_HOME");
		if (mcreator_home != null){
			return new File(mcreator_home);
		}
		return new File(System.getProperty("user.home") + "/.mcreator/");
	}

	public static boolean createUserFolderIfNotExists() {
		getUserFolder().mkdirs();

		// generate folder structure of user folder too
		getGradleHome().mkdirs();

		getPluginFolder().mkdirs();

		getBackgroundFolder().mkdirs();

		return getUserFolder().isDirectory() && Files.isWritable(getUserFolder().toPath());
	}

	public static File getFileFromUserFolder(String path) {
		return new File(getUserFolder(), path);
	}

	public static File getPluginFolder() {
		return getFileFromUserFolder("plugins");
	}

	public static File getBackgroundFolder() {return getFileFromUserFolder("backgrounds");}

	public static File getGradleHome() {
		return Objects.requireNonNullElseGet(PreferencesManager.PREFERENCES.gradle.gradleHome,
				() -> getFileFromUserFolder("/gradle/"));
	}

}
