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

package net.mcreator.ui.init;

import net.mcreator.io.UserFolderManager;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.themes.ThemeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BackgroundLoader {
	private static final Logger LOG = LogManager.getLogger("Background Loader");

	public static List<File> loadUserBackgrounds() {
		File[] bgfiles = UserFolderManager.getFileFromUserFolder("backgrounds").listFiles();
		if (bgfiles != null) {
			return Arrays.stream(bgfiles).filter(e -> e.getName().endsWith(".png")).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public static List<File> loadThemeBackgrounds() {
		Set<String> bgFiles = PluginLoader.INSTANCE
				.getResources("themes." + ThemeLoader.CURRENT_THEME.getID() + ".backgrounds",
						Pattern.compile("^[^$].*\\.png"));

		List<File> backgrounds = new ArrayList<>();
		if (bgFiles != null && !bgFiles.isEmpty()) {
			for (String name : bgFiles) {
				try {
					backgrounds.add(new File(PluginLoader.INSTANCE.getResource(name).toURI()));
				} catch (URISyntaxException e) {
					LOG.error("Can not use " + name, e.getMessage());
				}
			}
		}
		return backgrounds;
	}
}
