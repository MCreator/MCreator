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

package net.mcreator.resourcepacks;

import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;

public class ResourcePack {
	private String id;
	private String name;
	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;

	public String getID() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		if (description != null)
			return description;
		else if (L10N.t("resourcepack." + id + ".description") != null)
			return L10N.t("resourcepack." + id + ".description");
		else
			return null;
	}

	@Nullable public String getCredits() {
		return credits;
	}

	@Nullable public String getVersion() {
		return version;
	}

	@Override public String toString() {
		return getID() + ": " + getName();
	}
}
