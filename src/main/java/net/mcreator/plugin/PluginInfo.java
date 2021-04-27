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

public class PluginInfo {

	private String name;
	private String description;
	private String author;
	private String version;
	private List<String> dependencies;
	private String credits;
	private String updateUrl;
	private int pageId;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		if (version == null)
			return "not specified";
		return version;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public String getCredits() {
		if (credits == null) {
			return "None";
		}
		return credits;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public int getPageId() {
		return pageId;
	}
}
