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

package net.mcreator.ui.laf.themes;

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.data.PreferencesData;
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A Theme can change images MCreator will use and redefine the colors and the style
 * of {@link net.mcreator.ui.blockly.BlocklyPanel} and {@link net.mcreator.ui.ide.RSyntaxTextAreaStyler} by creating a new {@link ColorScheme}</p>.
 */
@SuppressWarnings("unused") public class Theme {

	private static final Logger LOG = LogManager.getLogger(Theme.class);

	public static Theme current() {
		return ThemeLoader.CURRENT_THEME;
	}

	protected String id;
	private String name;

	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;
	@Nullable private String defaultFont;
	private boolean useDefaultFontForSecondary;
	private int fontSize;

	@Nullable private ColorScheme colorScheme;

	private transient ImageIcon icon;

	private transient Font defaultThemeFont;
	private transient Font secondaryFont;
	private transient Font consoleFont;

	protected Theme init() {
		if (colorScheme != null)
			colorScheme.init();

		try {
			defaultThemeFont = new Font(defaultFont != null ? defaultFont : "Sans-Serif", Font.PLAIN,
					this.getFontSize());
			secondaryFont = defaultThemeFont;

			String lang = L10N.getLocale().getLanguage();
			if (!L10N.SYSTEM_FONT_LANGUAGES.contains(lang) && !useDefaultFontForSecondary) {
				InputStream secondaryFontStream = PluginLoader.INSTANCE.getResourceAsStream(
						"themes/" + id + "/fonts/secondary_font.ttf");
				if (secondaryFontStream != null) { // Font loaded from a file in the theme
					secondaryFont = Font.createFont(Font.TRUETYPE_FONT, secondaryFontStream);
				} else { // Default secondary front (from the default_dark theme)
					secondaryFont = Font.createFont(Font.TRUETYPE_FONT,
							PluginLoader.INSTANCE.getResourceAsStream("themes/default_dark/fonts/secondary_font.ttf"));
					LOG.info("Main font from default_dark will be used.");
				}
			}

			InputStream consoleFontStream = PluginLoader.INSTANCE.getResourceAsStream(
					"themes/" + id + "/fonts/console_font.ttf");
			if (consoleFontStream != null) {
				consoleFont = Font.createFont(Font.TRUETYPE_FONT, consoleFontStream);
			} else {
				// Default main front (from the default_dark theme)
				consoleFont = Font.createFont(Font.TRUETYPE_FONT,
						PluginLoader.INSTANCE.getResourceAsStream("themes/default_dark/fonts/console_font.ttf"));
				LOG.info("Console font from default_dark will be used.");
			}
		} catch (NullPointerException | FontFormatException | IOException e2) {
			LOG.info("Failed to init MCreator Theme! Error " + e2.getMessage());
		}

		return this;
	}

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

	public Font getFont() {
		return defaultThemeFont;
	}

	public Font getSecondaryFont() {
		return secondaryFont;
	}

	public Font getConsoleFont() {
		return consoleFont;
	}

	/**
	 * <p>This methods gets the {@link ColorScheme} to use with the theme</p>
	 *
	 * @return Returns the {@link ColorScheme} of the Theme if one is defined. If the Theme does not create a new {@link ColorScheme}, the Dark's theme {@link ColorScheme} will be used.
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

	// Color scheme getters below (to keep code shorter)

	/**
	 * @return Background of UI panels
	 */
	public Color getBackgroundColor() {
		return getColorScheme().getBackgroundColor();
	}

	/**
	 * @return Background of components (e.g. text fields, checkboxes and sound selectors)
	 */
	public Color getAltBackgroundColor() {
		return getColorScheme().getAltBackgroundColor();
	}

	/**
	 * @return Second background color used (e.g. workspace background)
	 */
	public Color getSecondAltBackgroundColor() {
		return getColorScheme().getSecondAltBackgroundColor();
	}

	/**
	 * @return <p>Secondary text color </p>
	 */
	public Color getAltForegroundColor() {
		return getColorScheme().getAltForegroundColor();
	}

	/**
	 * @return <p>Color used for most of texts </p>
	 */
	public Color getForegroundColor() {
		return getColorScheme().getForegroundColor();
	}

	/**
	 * @return <p>Returns the interfaceAccentColor if defined by theme, otherwise the one defined by the user in {@link PreferencesData}</p>
	 */
	public Color getInterfaceAccentColor() {
		return getColorScheme().getInterfaceAccentColor();
	}

}
