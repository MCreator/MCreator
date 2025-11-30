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

package net.mcreator.minecraft;

import net.mcreator.io.OS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class MinecraftFolderUtils {

	public static File getJavaEditionFolder() {
		if (OS.getOS() == OS.WINDOWS) {
			String appdata = System.getenv("APPDATA");
			if (appdata != null) {
				File candidate = new File(appdata + "/.minecraft");
				if (candidate.isDirectory())
					return candidate;
			}
		} else if (OS.getOS() == OS.MAC) {
			File candidate = new File(System.getProperty("user.home") + "/Library/Application Support/minecraft");
			if (candidate.isDirectory())
				return candidate;
		} else {
			File candidate = new File(System.getProperty("user.home") + "/.minecraft");
			if (candidate.isDirectory())
				return candidate;
		}

		return null;
	}

	public static File getBedrockEditionFolder() {
		if (OS.getOS() != OS.WINDOWS)
			return null;

		// we first check for the new launcher's folder
		String appData = System.getenv("APPDATA");
		if (appData == null)
			return null;

		File newLauncherFolder = new File(appData, "Minecraft Bedrock/Users/Shared/games/com.mojang/");
		if (newLauncherFolder.exists() && newLauncherFolder.isDirectory())
			return newLauncherFolder;

		// Then, if the user doesn't have the new launcher, we try the old one
		String localAppData = System.getenv("LOCALAPPDATA");
		if (localAppData == null)
			return null;

		File packagesDir = new File(localAppData, "Packages");
		if (!packagesDir.isDirectory())
			return null;

		File[] candidates = packagesDir.listFiles();
		if (candidates == null)
			return null;

		for (File pkg : candidates) {
			if (!pkg.isDirectory())
				continue;

			File worldFolder = new File(pkg, "LocalState/games/com.mojang");
			if (worldFolder.isDirectory() && new File(worldFolder, "minecraftpe").isDirectory()) {
				return worldFolder;
			}
		}

		return null;
	}

}
