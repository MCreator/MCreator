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
import java.awt.*;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ThemeLoader {
	private static final Logger LOG = LogManager.getLogger("Theme Loader");

	private static final LinkedHashSet<Theme> THEMES = new LinkedHashSet<>();

	public static void initUIThemes() {
		LOG.debug("Loading UI themes");

		final Gson gson = new Gson();

		// Load themes
		Set<String> files = PluginLoader.INSTANCE.getResources("themes", Pattern.compile("theme.json"));
		for (String file : files) {
			Theme theme = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), Theme.class);
			// The ID will be used to get images from this theme if the user selected it.
			theme.setId(new File(file).getParentFile().getName());

			// Check if all color themes are ok
			if (theme.getColorThemes() != null) {
				for (ColorTheme color : theme.getColorThemes()) {
					// We set the theme containing this color theme, so we can a color theme from another theme
					color.setTheme(theme);

					// Check if the CSS file for the BlocklyPanel exists
					if(PluginLoader.INSTANCE.getResource("themes/" + color.getBlocklyCSSFile() + ".css") == null)
						color.setBlocklyCSSFile("dark");

					// Check if the XML file for the code editor exists
					if(PluginLoader.INSTANCE.getResource("themes/" + color.getCodeEditorFile() + ".xml") == null)
						color.setCodeEditorFile("dark");

					LOG.debug("Color theme: " + color.getID());
				}
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

		// We check if the last image or color theme selected by the user still exists
		// If the theme has been deleted since the last time, we load the default theme
		if (ThemeLoader.getTheme(PreferencesManager.PREFERENCES.hidden.imageTheme) == null)
			PreferencesManager.PREFERENCES.hidden.imageTheme = "default";
		if (ThemeLoader.getColorTheme(PreferencesManager.PREFERENCES.hidden.colorTheme) == null)
			PreferencesManager.PREFERENCES.hidden.colorTheme = "dark";
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

	private static Theme getTheme(String id) {
		for (Theme pack : THEMES) {
			if (pack.getID().equals(id))
				return pack;
		}
		return null;
	}

	public static List<String> getColorThemeIDs() {
		List<String> ids = new ArrayList<>();
		// We add each Color id for each UI theme
		for(Theme theme : THEMES) {
			if (theme.getColorThemes() != null) {
				for(ColorTheme color : theme.getColorThemes()) {
					ids.add(color.getID());
				}
			}
		}
		return ids;
	}

	public static ColorTheme getColorTheme(String id) {
		for (Theme pack : THEMES) {
			if(pack.getColorThemes() != null) {
				for (ColorTheme color : pack.getColorThemes()) {
					if (color.getID().equals(id))
						return color;
				}
			}
		}
		return null;
	}
}
