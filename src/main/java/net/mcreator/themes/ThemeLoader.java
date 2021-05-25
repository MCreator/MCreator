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

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * ThemeLoader defines to load and use available Themes.
 */
public class
ThemeLoader {
	private static final Logger LOG = LogManager.getLogger("Theme Loader");

	private static final LinkedHashSet<Theme> THEMES = new LinkedHashSet<>();
	public static Theme CURRENT_THEME;

	/**
	 * <p>This method loads the {@link net.mcreator.themes.Theme} of all plugins loaded into the current {@link net.mcreator.plugin.PluginLoader} instance.</p>
	 */
	public static void initUIThemes() {
		LOG.debug("Loading UI themes");

		final Gson gson = new Gson();

		// Load themes
		Set<String> files = PluginLoader.INSTANCE.getResources("themes", Pattern.compile("theme.json"));
		for (String file : files) {
			Theme theme = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), Theme.class);
			// The ID will be used to get images from this theme if the user select it.
			theme.setId(new File(file).getParentFile().getName());

			if (theme.getColorScheme() != null) {
				// Check if the color scheme contains the Blockly CSS file
				if (PluginLoader.INSTANCE.getResource(
						"themes/" + theme.getID() + "/styles/blockly.css") == null) {
					LOG.warn("Color scheme of " + theme.getID()
							+ " does not define the Blockly Panel colors! Default_dark's file will be used!");
				}

				// Check if the color scheme contains the code editor XML file
				if (PluginLoader.INSTANCE.getResource(
						"themes/" + theme.getID() + "/styles/code_editor.xml") == null) {
					LOG.warn("Color scheme of " + theme.getID()
							+ " does not define the code editor colors! Default_dark's file will be used!");
				}
			}

			// Load the custom icon if provided otherwise, load the default one
			String identifier = "themes/" + theme.getID() + "/icon.png";
			if (PluginLoader.INSTANCE.getResource(identifier) != null) {
				ImageIcon icon = UIRES.getImageFromResourceID(identifier);
				icon = new ImageIcon(ImageUtils.resize(icon.getImage(), 64));
				theme.setIcon(icon);
			} else {
				theme.setIcon(UIRES.get("icon.png"));
			}
			THEMES.add(theme);

			LOG.debug("Loaded " + theme.getID());
		}

		// We check if the last theme selected by the user still exists
		// If the theme has been deleted since the last time, we load the default_dark theme
		if (ThemeLoader.getTheme(PreferencesManager.PREFERENCES.hidden.uiTheme) == null)
			PreferencesManager.PREFERENCES.hidden.uiTheme = "default_dark";

		CURRENT_THEME = getTheme(PreferencesManager.PREFERENCES.hidden.uiTheme);
		LOG.info("Current UI theme: " + CURRENT_THEME.getID());
	}

	public static LinkedHashSet<Theme> getThemes() {
		return THEMES;
	}

	/**
	 * <p>This method gets the ID of each loaded {@link net.mcreator.themes.Theme}.</p>
	 *
	 * @return Returns a {@link java.util.List} of all loaded theme IDs
	 */
	public static List<String> getIDs() {
		List<String> ids = new ArrayList<>();
		for (Theme rp : THEMES) {
			ids.add(rp.getID());
		}
		return ids;
	}

	/**
	 * <p>This method checks in all loaded themes to get the theme matching the ID.</p>
	 *
	 * @param id The theme's id we want to get
	 * @return Returns the {@link net.mcreator.themes.Theme}, if found in the cache, otherwise null
	 */
	public static Theme getTheme(String id) {
		for (Theme pack : THEMES) {
			if (pack.getID().equals(id))
				return pack;
		}
		return null;
	}
}
