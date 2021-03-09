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

package net.mcreator.plugin;

public class PluginUpdateInfo {
	private final Plugin plugin;
	private final String newVersion;
	private final String pluginURL;

	public PluginUpdateInfo(Plugin plugin, String newVersion, String pluginURL) {
		this.plugin = plugin;
		this.newVersion = newVersion;
		this.pluginURL = pluginURL;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public String getPluginURL() {
		return pluginURL;
	}
}
