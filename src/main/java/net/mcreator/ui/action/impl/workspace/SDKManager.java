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
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.dialogs.ProgressDialog;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;

public class SDKManager {

	private static final Logger LOG = LogManager.getLogger("Setup JDK");

	public static void downloadSDK(GeneratorConfiguration generatorConfiguration,
			ProgressDialog.ProgressUnit progressUnit) throws Exception {
		String jdkVersion = generatorConfiguration.getJDKVersionOverride();
		if (jdkVersion != null && !validateCustomJDK(generatorConfiguration)) {
			String url = "https://api.adoptopenjdk.net/v3/binary/version/" + jdkVersion + "/" + OS.getOSName()
					+ "/x64/jdk/hotspot/normal/adoptopenjdk";

			LOG.info("Downloading JDK: " + jdkVersion + " from " + url);

			String fileExtension = OS.getOS() == OS.WINDOWS ? ".zip" : ".tar.gz";

			FileUtils.copyURLToFile(new URL(url), getSDKRootForVersion(jdkVersion + fileExtension));

			progressUnit.setPercent(90);
			if (progressUnit.getProgressDialog() != null)
				progressUnit.getProgressDialog().refreshDisplay();

			LOG.info("Extracting JDK: " + jdkVersion);

			if (OS.getOS() == OS.WINDOWS) {
				ZipIO.unzip(getSDKRootForVersion(jdkVersion + fileExtension).getAbsolutePath(),
						getSDKRootForVersion("").getAbsolutePath());
			} else {
				ZipIO.extractTarGZ(getSDKRootForVersion(jdkVersion + fileExtension).getAbsolutePath(),
						getSDKRootForVersion("").getAbsolutePath());
			}

			progressUnit.setPercent(95);
			if (progressUnit.getProgressDialog() != null)
				progressUnit.getProgressDialog().refreshDisplay();

			getSDKRootForVersion(jdkVersion + fileExtension).delete();
		}
	}

	public static boolean validateCustomJDK(GeneratorConfiguration generatorConfiguration) {
		if (generatorConfiguration.getJDKVersionOverride() == null)
			return true;

		return getJavaHomeForVersion(generatorConfiguration.getJDKVersionOverride()).isDirectory() && (
				(OS.getOS() == OS.WINDOWS && new File(
						getJavaHomeForVersion(generatorConfiguration.getJDKVersionOverride()), "bin/javac.exe")
						.isFile()) || (OS.getOS() != OS.WINDOWS && new File(
						getJavaHomeForVersion(generatorConfiguration.getJDKVersionOverride()), "bin/javac").isFile()));
	}

	public static File getJavaHomeForVersion(String jdkVersion) {
		if (OS.getOS() == OS.MAC) {
			return new File(getSDKRootForVersion(jdkVersion), "Contents/Home");
		} else {
			return getSDKRootForVersion(jdkVersion);
		}
	}

	private static File getSDKRootForVersion(String jdkVersion) {
		return UserFolderManager.getFileFromUserFolder("/sdks/" + jdkVersion);
	}

}
