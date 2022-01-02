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

import net.mcreator.Launcher;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * <p>A Plugin is a mod for MCreator allowing to alter, improve or extend features. Most of elements inside MCreator are plugin driven.</p>
 */
public class Plugin implements Comparable<Plugin> {

	transient File file;
	transient boolean builtin;
	transient boolean loaded;

	private String id;
	private int weight = 0;

	private long minversion = -1;
	private long maxversion = -1;

	private PluginInfo info;

	public File getFile() {
		return file;
	}

	/**
	 * MCreator detects if a plugin is included or not. This method is mainly used with plugins in preferences.
	 *
	 * @return <p>The plugin is builtin, so included with MCreator.</p>
	 */
	public boolean isBuiltin() {
		return builtin;
	}

	/**
	 * A plugin is loaded when all plugin dependencies are present.
	 *
	 * @return <p>The plugin is loaded.</p>
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * The ID is the plugin's registry name. It is used to differentiate each plugin in the code.
	 * A plugin defines its own ID.
	 *
	 * @return <p>The plugin's ID</p>
	 */
	public String getID() {
		return id;
	}

	/**
	 * It returns a {@link PluginInfo} object containing additional and optional info about the plugin.
	 * This object is only used inside the plugin panel.
	 *
	 * @return <p> Other info about the plugin</p>
	 */
	public PluginInfo getInfo() {
		return info;
	}

	/**
	 * <p>The weight of a plugin is its priority to be loaded. Higher this value is, higher its priority is.
	 * This also determines how MCreator loads plugins. After MCreator has detected all plugins, it will check
	 * for their weight and then, load plugins with the highest value first. It can be used to override another plugin,
	 * but the value should never be higher than 10 as this is the value or core plugins.</p>
	 *
	 * @return <p>The weight of the plugin</p>
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * <p>The plugin is compatible when the minimal version is lower or equal to the current MCreator's version and
	 * the maximal version is higher or equal to the current version. When a field is not defined, the field is considered as compatible.</p>
	 *
	 * @return <p>If the plugin is compatible with the version used.</p>
	 */
	public boolean isCompatible() {
		if (minversion != -1) {
			if (Launcher.version.versionlong < minversion)
				return false;
		}

		if (maxversion != -1) {
			return Launcher.version.versionlong <= maxversion;
		}

		return true;
	}

	/**
	 * @return <p>The minimal version of MCreator needed to use the plugin.</p>
	 */
	public long getMinVersion() {
		return minversion;
	}

	/**
	 * @return <p>The MCreator's version when builtin and the plugin's version in the other case</p>
	 */
	public String getPluginVersion() {
		if (isBuiltin()) {
			return Launcher.version.getFullString();
		}

		return info.getVersion();
	}

	@Override public String toString() {
		return "Plugin{" + "file=" + file + ", id='" + id + '\'' + '}';
	}

	@Override public boolean equals(Object element) {
		return element instanceof Plugin && id.equals(((Plugin) element).getID());
	}

	@Override public int hashCode() {
		return id.hashCode();
	}

	@Override public int compareTo(@Nonnull Plugin p) {
		return p.weight - weight;
	}
}
