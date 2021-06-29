/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.action.impl.workspace;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class SDKDownloader {

	private static final Logger LOG = LogManager.getLogger("Setup JDK");

	public static void downloadJDK(GeneratorConfiguration genConfig) throws Exception {
		String jdkVersion = genConfig.getJDKVersionOverride();
		if (jdkVersion != null) {
			if (!UserFolderManager.getStoredJDKFolderForVersion(jdkVersion + "/").exists()) {
				LOG.info("Downloading JDK: " + jdkVersion);
				String fileExtension;
				if (OS.getOS() == OS.WINDOWS)
					fileExtension = ".zip";
				else
					fileExtension = ".tar.gz";
				FileUtils.copyURLToFile(
						new URL("https://api.adoptopenjdk.net/v3/binary/version/" + jdkVersion + "/" + OS.getOSName()
								+ "/x64/jdk/hotspot/normal/adoptopenjdk"),
						UserFolderManager.getStoredJDKFolderForVersion(jdkVersion + fileExtension));

			} else {
				LOG.info(jdkVersion + " is already downloaded.");
			}
		}
	}
}
