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

package net.mcreator.util;

import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class MCreatorVersionNumber {

	private static final String buildFormat = "wwuHH";

	public String full;
	public String major;
	public String build;

	public boolean snapshot;

	public long majorlong;
	public long buildlong;
	public long versionlong;

	public MCreatorVersionNumber(Properties properties) {
		major = properties.getProperty("mcreator");

		String snapshotText = FileIO.readResourceToString("/snapshot.conf");
		build = StringUtils.repeat('9', buildFormat
				.length()); // by default use the largest to prevent regenerating workspace and updating during development

		try {
			Enumeration<URL> resources = Launcher.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				Manifest manifest = new Manifest(resources.nextElement().openStream());
				Attributes attributes = manifest.getMainAttributes();
				if (attributes.getValue("MCreator-Version") != null) {
					String buildDateManifest = attributes.getValue("Build-Date");
					if (buildDateManifest != null)
						build = buildDateManifest;
					break;
				}
			}
		} catch (Exception ignored) {
		}

		this.snapshot = snapshotText != null && !snapshotText.isEmpty();

		this.full = this.major + "." + this.build;

		this.buildlong = Long.parseLong(this.build);
		this.majorlong = majorStringToLong(this.major);
		this.versionlong = this.majorlong * (long) Math.pow(10, buildFormat.length()) + this.buildlong;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public String getMajorString() {
		return major + (snapshot ? " EAP (" + build + ")" : "");
	}

	public String getFullString() {
		return full + (snapshot ? " EAP (" + build + ")" : "");
	}

	@Override public String toString() {
		return this.getFullString() + " - " + versionlong;
	}

	public static boolean isBuildNumberDevelopment(long full) {
		return full % (long) Math.pow(10, buildFormat.length()) == Integer
				.parseInt(StringUtils.repeat('9', buildFormat.length()));
	}

	public static long majorStringToLong(String major) {
		String majorA = major.split("\\.")[0];
		String majorB = major.split("\\.")[1];
		return Long.parseLong(majorA) * (long) Math.pow(10, 3) + Long.parseLong(majorB);
	}

}
