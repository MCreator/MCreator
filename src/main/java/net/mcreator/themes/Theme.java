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

/**
 * <p>A Theme can change images MCreator will use and redefine the colors and the style
 * of {@link net.mcreator.ui.blockly.BlocklyPanel} and {@link net.mcreator.ui.ide.RSyntaxTextAreaStyler} by creating a new {@link net.mcreator.themes.ColorScheme}</p>.
 */
@SuppressWarnings("unused") public class Theme {

	private String id;
	private String name;

	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;

	@Nullable private ColorScheme colorScheme;

	private ImageIcon icon;

	public String getID() {
		return id;
	}

	public void setID(String id) {
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

	/**
	 * <p>This methods gets the {@link net.mcreator.themes.ColorScheme} to use with the theme</p>
	 *
	 * @return Returns the {@link net.mcreator.themes.ColorScheme} of the Theme if one is defined. If the Theme does not create a new {@link net.mcreator.themes.ColorScheme}, the Dark's theme {@link net.mcreator.themes.ColorScheme} will be used.
	 */
	public ColorScheme getColorScheme() {
		if (colorScheme != null)
			return colorScheme;
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
