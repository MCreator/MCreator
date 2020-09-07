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
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Plugin implements Comparable<Plugin> {

	transient File file;
	transient boolean builtin;

	private String id;
	private int weight = 0;

	private long minversion = -1;
	private long maxversion = -1;

	private PluginInfo info;

	public File getFile() {
		return file;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public String getID() {
		return id;
	}

	public PluginInfo getInfo() {
		return info;
	}

	public int getWeight() {
		return weight;
	}

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

	public long getMinVersion() {
		return minversion;
	}

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

	@Override public int compareTo(@NotNull Plugin p) {
		return p.weight - weight;
	}
}
