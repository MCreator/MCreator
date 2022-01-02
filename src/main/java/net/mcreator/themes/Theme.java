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
	@Nullable private String defaultFont;
	private boolean useDefaultFontForSecondary;
	private int fontSize;

	@Nullable private ColorScheme colorScheme;

	private ImageIcon icon;

	/**
	 * The ID is the theme's registry name. It is used to differentiate each theme in the code.
	 * This ID is also the main folder's name of the theme.
	 *
	 * @return <p>The theme's ID</p>
	 */
	public String getID() {
		return id;
	}

	/**
	 * This method sets the id of this theme using the name of its main folder.
	 *
	 * @param id <p>The theme's ID</p>
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * @return <p>Its displayed name</p>
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return <p>A description displayed in the {@link net.mcreator.ui.dialogs.preferences.ThemesPanel} if provided.</p>
	 */
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

	/**
	 * @return <p>A String with optional credits to give to someone.</p>
	 */
	@Nullable public String getCredits() {
		return credits;
	}

	/**
	 * @return <p>The theme's version if provided</p>
	 */
	@Nullable public String getVersion() {
		return version;
	}

	/**
	 * <p>The main font size changes the size of the text for the main font. Usually, this parameter should not be changed except if the font is too big or too small with the default value.</p>
	 *
	 * @return <p>The main font size</p>
	 */
	public int getFontSize() {
		if (fontSize != 0)
			return fontSize;
		else
			return 12;
	}

	/**
	 * @return The default font to use with some languages.
	 */
	public String getDefaultFont() {
		if (defaultFont != null)
			return defaultFont;
		else
			return "Sans-Serif";
	}

	/**
	 * @return <p>Use the default font as the main font</p>
	 */
	public boolean useDefaultFontForSecondary() {
		return useDefaultFontForSecondary;
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

	/**
	 * This icon is only with {@link net.mcreator.ui.dialogs.preferences.ThemesPanel}.
	 *
	 * @return <p>An {@link ImageIcon} representing the plugin.</p>
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * <p>To be detected, the name of the image file needs to be "icon.png" located into the main folder.</p>
	 *
	 * @param icon <p>An {@link ImageIcon} to display in {@link net.mcreator.ui.dialogs.preferences.ThemesPanel}</p>
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	@Override public String toString() {
		return getID() + ": " + getName();
	}
}
