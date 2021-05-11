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

import javax.annotation.Nullable;
import java.util.List;

public class Release {

	private String latestBuild;
	@Nullable private List<Build> builds;

	public String getLatestBuild() {
		return latestBuild;
	}

	@Nullable public List<Build> getBuilds() {
		return builds;
	}

	public static class Build {

		private String build;
		@Nullable private List<String> changelog;

		public String getBuild() {
			return build;
		}

		@Nullable public List<String> getChangelog() {
			return changelog;
		}
	}

}
