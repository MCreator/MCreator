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

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.laf.LafUtil;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>This class detects and then tries to load all {@link Theme}s.</p>
 */
public class ThemeManager {

	private static final Logger LOG = LogManager.getLogger("Theme Loader");

	private static final Gson gson = new Gson();

	private static final LinkedHashSet<Theme> THEMES = new LinkedHashSet<>();

	protected static Theme CURRENT_THEME;

	/**
	 * This method loads all {@link Theme}s and sets the current theme to the one selected by the user.
	 */
	public static void applySelectedTheme() {
		try {
			if (OS.getOS() == OS.LINUX) {
				// We need to call this to enable the window decorations on Linux
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
			}

			Theme theme = Theme.current();

			Map<String, String> flatLafDefaults = new HashMap<>();
			theme.applyFlatLafOverrides(flatLafDefaults);
			FlatLaf.setGlobalExtraDefaults(flatLafDefaults);

			FlatLaf laf;
			String themeName = theme.getFlatLafTheme();
			if (themeName.endsWith(".json")) {
				laf = IntelliJTheme.createLaf(Objects.requireNonNull(
						PluginLoader.INSTANCE.getResourceAsStream("themes/" + theme.getID() + "/" + themeName)));
			} else {
				laf = (FlatLaf) Class.forName("com.formdev.flatlaf." + themeName).getConstructor().newInstance();
			}

			UIManager.setLookAndFeel(laf);

			theme.applyUIDefaultsOverrides(UIManager.getDefaults());

			LafUtil.applyDefaultHTMLStyles();
		} catch (Exception e) {
			LOG.error("Failed to set MCreator UI theme", e);
		}
	}

	/**
	 * <p>This method loads the {@link Theme} of all plugins loaded into the current {@link net.mcreator.plugin.PluginLoader} instance.</p>
	 */
	public static void loadThemes() {
		LOG.debug("Loading UI themes");

		// Load all themes
		Set<String> files = PluginLoader.INSTANCE.getResources("themes", Pattern.compile("theme.json"));
		for (String file : files) {
			Theme theme = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), Theme.class);

			// Initialize the theme and ID - the ID will be used to get images from this theme if the user select it.
			theme.id = new File(file).getParentFile().getName();

			URL url = PluginLoader.INSTANCE.getResource("themes/" + theme.getID() + "/icon.png");
			if (url != null)
				theme.setIcon(new ImageIcon(ImageUtils.resize(new ImageIcon(url).getImage(), 64)));

			THEMES.add(theme);
		}

		CURRENT_THEME = getTheme(PreferencesManager.PREFERENCES.hidden.uiTheme.get()).init();
		LOG.info("Using MCreator UI theme: " + CURRENT_THEME.getID());
	}

	public static LinkedHashSet<Theme> getThemes() {
		return THEMES;
	}

	/**
	 * <p>This method checks in all loaded themes to get the theme matching the ID.</p>
	 *
	 * @param id The theme's id we want to get
	 * @return Returns the {@link Theme}, if found in the cache, otherwise null
	 */
	static Theme getTheme(String id) {
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
