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

package net.mcreator.io.net.api.update;

import net.mcreator.Launcher;
import net.mcreator.util.MCreatorVersionNumber;

import java.util.Map;

public class UpdateInfo {

	private String latestMajor;
	private Map<String, Release> releases;

	public static UpdateInfo empty() {
		UpdateInfo info = new UpdateInfo();
		info.latestMajor = "0.0.0";
		info.releases = Map.of();
		return info;
	}

	public String getLatestMajor() {
		return latestMajor;
	}

	public Map<String, Release> getReleases() {
		return releases;
	}

	public boolean isNewUpdateAvailable() {
		long newMajor = MCreatorVersionNumber.majorStringToLong(latestMajor);
		return newMajor > Launcher.version.majorlong;
	}

	public boolean isNewPatchAvailable() {
		Release thisRelease = releases.get(Launcher.version.major);
		if (thisRelease != null) {
			return Long.parseLong(thisRelease.getLatestBuild()) > Launcher.version.buildlong;
		} else {
			return false;
		}
	}

	public String getLatestPatchVersion() {
		Release thisRelease = releases.get(Launcher.version.major);
		if (thisRelease != null) {
			return thisRelease.getLatestBuild();
		} else {
			return null;
		}
	}

}
