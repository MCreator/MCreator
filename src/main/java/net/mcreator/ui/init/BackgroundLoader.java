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
import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
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
					Image result = ImageIO.read(f);
					if (result != null)
						images.add(result);
					else
						throw new NullPointerException("ImageIO.read returned null");
				} catch (Exception e) {
					LOG.error("Can not load user background: " + f.getName(), e);
				}
			});
			return images;
		}
		return Collections.emptyList();
	}

	public static List<Image> loadThemeBackgrounds() {
		Set<String> bgFiles = PluginLoader.INSTANCE.getResources("themes." + Theme.current().getID() + ".backgrounds",
				Pattern.compile("^[^$].*\\.png"));

		List<Image> backgrounds = new ArrayList<>();
		for (String name : bgFiles) {
			try {
				backgrounds.add(Toolkit.getDefaultToolkit().createImage(PluginLoader.INSTANCE.getResource(name)));
			} catch (Exception e) {
				LOG.error("Can not load theme background: " + name, e);
			}
		}
		return backgrounds;
	}
}
