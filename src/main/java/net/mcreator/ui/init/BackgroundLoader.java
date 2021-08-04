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

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class BackgroundLoader {

	private static final Logger LOG = LogManager.getLogger("Background Loader");

	public static List<Image> loadUserBackgrounds() {
		File[] bgfiles = UserFolderManager.getFileFromUserFolder("backgrounds").listFiles();
		if (bgfiles != null) {
			List<Image> images = new ArrayList<>();
			Arrays.stream(bgfiles).forEach(f -> {
				try {
					images.add(ImageIO.read(f));
				} catch (IOException e) {
					LOG.error("Can not load " + f.getName(), e.getMessage());
					e.printStackTrace();
				}
			});
			return images;
		}
		return Collections.emptyList();
	}

	public static List<Image> loadThemeBackgrounds() {
		Set<String> bgFiles = PluginLoader.INSTANCE.getResources(
				"themes." + ThemeLoader.CURRENT_THEME.getID() + ".backgrounds", Pattern.compile("^[^$].*\\.png"));

		List<Image> backgrounds = new ArrayList<>();
		for (String name : bgFiles) {
			try {
				backgrounds.add(Toolkit.getDefaultToolkit().createImage(PluginLoader.INSTANCE.getResource(name)));
			} catch (Exception e) {
				LOG.error("Can not load " + name, e.getMessage());
			}
		}
		return backgrounds;
	}
}
