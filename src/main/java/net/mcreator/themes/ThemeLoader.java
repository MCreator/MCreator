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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ThemeLoader defines to load and use available Themes.
 */
public class ThemeLoader {

	private static final Logger LOG = LogManager.getLogger("Theme Loader");

	private static final Gson gson = new Gson();

	private static final LinkedHashSet<Theme> THEMES = new LinkedHashSet<>();

	public static Theme CURRENT_THEME;

	/**
	 * <p>This method loads the {@link net.mcreator.themes.Theme} of all plugins loaded into the current {@link net.mcreator.plugin.PluginLoader} instance.</p>
	 */
	public static void initUIThemes() {
		LOG.debug("Loading UI themes");

		// Load all themes
		Set<String> files = PluginLoader.INSTANCE.getResources("themes", Pattern.compile("theme.json"));
		for (String file : files) {
			Theme theme = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), Theme.class);

			// The ID will be used to get images from this theme if the user select it.
			theme.setID(new File(file).getParentFile().getName());

			// Load the custom icon if provided, otherwise load the default one
			if (PluginLoader.INSTANCE.getResource("themes/" + theme.getID() + "/icon.png") != null)
				theme.setIcon(new ImageIcon(ImageUtils
						.resize(UIRES.getImageFromResourceID("themes/" + theme.getID() + "/icon.png").getImage(), 64)));

			THEMES.add(theme);
		}

		CURRENT_THEME = getTheme(PreferencesManager.PREFERENCES.hidden.uiTheme);
		LOG.info("Using MCreator UI theme: " + CURRENT_THEME.getID());
	}

	public static LinkedHashSet<Theme> getThemes() {
		return THEMES;
	}

	/**
	 * <p>This method gets the ID of each loaded {@link net.mcreator.themes.Theme}.</p>
	 *
	 * @return Returns a {@link java.util.List} of all loaded theme IDs
	 */
	public static List<String> getThemeIDList() {
		return THEMES.stream().map(Theme::getID).collect(Collectors.toList());
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

		if (id.equals("default_dark"))
			throw new RuntimeException("No themes present in MCreator");

		LOG.warn("Default theme will be used due to missing theme: " + id);

		return getTheme("default_dark");
	}
}
