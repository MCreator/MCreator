/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public record JavaReleaseInfo(String vendor, String version) {

	private static final Logger LOG = LogManager.getLogger(JavaReleaseInfo.class);

	public static final JavaReleaseInfo DEFAULT = new JavaReleaseInfo("Java SDK", null);

	/**
	 * Parse the 'release' file from the given JAVA_HOME path.
	 *
	 * @param javaHome path to JAVA_HOME
	 * @return JavaReleaseInfo with vendor and version
	 */
	public static JavaReleaseInfo fromJavaHome(File javaHome) {
		File releaseFile = new File(javaHome, "release");
		if (!releaseFile.isFile()) {
			LOG.warn("JAVA_HOME does not contain a release file");
			return DEFAULT;
		}

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(releaseFile)) {
			props.load(fis);
			String version = stripQuotes(props.getProperty("JAVA_VERSION"));
			String vendor = stripQuotes(props.getProperty("IMPLEMENTOR"));
			return new JavaReleaseInfo(vendor != null ? vendor : "Java SDK", version != null ? version : "");
		} catch (IOException e) {
			LOG.warn("Failed to read release info from JAVA_HOME", e);
			return DEFAULT;
		}
	}

	private static String stripQuotes(String value) {
		if (value == null)
			return null;
		return value.replaceAll("^\"|\"$", "");
	}

	@Nonnull @Override public String toString() {
		return vendor + " " + version;
	}

}
