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
import net.mcreator.ui.laf.MCreatorTheme;
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

public class ThemeLoader {
	private static final Logger LOG = LogManager.getLogger("Theme Loader");

	private static final LinkedHashSet<Theme> THEMES = new LinkedHashSet<>();
	public static Theme DARK_THEME;
	public static Theme CURRENT_THEME;

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
				// Set the value for the Blockly Panel file
				// If the file does not exist, we use the default file (from the default_dark them)
				if (PluginLoader.INSTANCE.getResource(
						"themes/" + theme.getID() + "/colors/blockly_" + theme.getColorScheme().getID() + ".css") == null) {
					theme.getColorScheme().setBlocklyCSSFile("dark");
					LOG.warn(theme.getColorScheme().getID()
							+ " color scheme does not define the Blockly Panel colors! Default_dark's file will be used!");
				} else {
					theme.getColorScheme().setBlocklyCSSFile(theme.getColorScheme().getID());
				}

				// Set the value for the Code Editor file
				// If the file does not exist, we use the default file (from the default_dark them)
				if (PluginLoader.INSTANCE.getResource(
						"themes/" + theme.getID() + "/colors/codeeditor_" + theme.getColorScheme().getID() + ".xml") == null) {
					theme.getColorScheme().setCodeEditorFile("dark");
					LOG.warn(theme.getColorScheme().getID()
							+ " color theme does not define the code editor colors! Default_dark's file will be used!");
				} else {
					theme.getColorScheme().setCodeEditorFile(theme.getColorScheme().getID());
				}
			} else if (theme.getID().equals("default_dark")) {
				// We set a default theme, so we can use it for its values instead of throwing an error
				DARK_THEME = theme;
				DARK_THEME.setColorScheme(MCreatorTheme.DARK_SCHEME);
			}

			// Load the custom icon if provided otherwise, load the default one
			String identifier = "themes/" + theme.getID() + "/icon.png";
			if (PluginLoader.INSTANCE.getResource(identifier) != null) {
				ImageIcon icon = UIRES.fromResourceID(identifier);
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

	public static List<String> getIDs() {
		List<String> ids = new ArrayList<>();
		for (Theme rp : THEMES) {
			ids.add(rp.getID());
		}
		return ids;
	}

	public static Theme getTheme(String id) {
		for (Theme pack : THEMES) {
			if (pack.getID().equals(id))
				return pack;
		}
		return null;
	}
}
