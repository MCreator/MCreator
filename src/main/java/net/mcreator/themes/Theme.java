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

package net.mcreator.themes;

import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;
import javax.swing.*;

public class Theme {
	private String id;
	private String name;
	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;
	@Nullable private ColorScheme colorScheme;
	private ImageIcon icon;

	public Theme(String id, String name, @Nullable ColorScheme colorScheme) {
		this.id = id;
		this.name = name;
		this.colorScheme = colorScheme;
	}

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
		// Description inside the JSON file
		if (description != null)
			return description;
		// Localized description
		else if (!L10N.t("theme." + id + ".description").equals("theme." + id + ".description"))
			return L10N.t("theme." + id + ".description");
		// No description
		else
			return "";
	}

	@Nullable public String getCredits() {
		return credits;
	}

	@Nullable public String getVersion() {
		return version;
	}

	public ColorScheme getColorScheme() {
		if(colorScheme != null)
			return colorScheme;
		// We are sure the Dark theme's color scheme exists as it's in the main one, so we can get it
		else
			return ThemeLoader.getTheme("default_dark").getColorScheme();
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	@Override public String toString() {
		return getID() + ": " + getName();
	}
}
