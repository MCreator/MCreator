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

package net.mcreator.plugin;

import java.util.List;

public record PluginInfo(String name, String description, String author, String credits, String version,
						 List<String> dependencies, String updateJSONURL, int pluginPageID) {

	public static final String VERSION_NOT_SPECIFIED = "not specified";

	public String getVersion() {
		if (version == null)
			return VERSION_NOT_SPECIFIED;
		return version;
	}

	public String getCredits() {
		if (credits == null) {
			return "None";
		}
		return credits;
	}
}
