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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
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
					LOG.error("Can not load user background: {}", f.getName(), e);
				}
			});
			return images;
		}
		return Collections.emptyList();
	}

	public static List<Image> loadThemeBackgrounds() {
		Set<String> bgFiles = PluginLoader.INSTANCE.getResources("themes." + Theme.current().getID() + ".backgrounds",
				Pattern.compile("^[^$].*\\.png$"));

		List<Image> backgrounds = new ArrayList<>();
		for (String name : bgFiles) {
			try {
				backgrounds.add(Toolkit.getDefaultToolkit().createImage(PluginLoader.INSTANCE.getResource(name)));
			} catch (Exception e) {
				LOG.error("Can not load theme background: {}", name, e);
			}
		}
		return backgrounds;
	}

	@Nullable public static Image getBackgroundImage() {
		UserFolderManager.getFileFromUserFolder("backgrounds").mkdirs();

		// Load backgrounds depending on the background source
		List<Image> bgimages = new ArrayList<>();
		switch (PreferencesManager.PREFERENCES.ui.backgroundSource.get()) {
		case "All":
			bgimages.addAll(BackgroundLoader.loadThemeBackgrounds());
			bgimages.addAll(BackgroundLoader.loadUserBackgrounds());
			break;
		case "Current theme":
			bgimages = BackgroundLoader.loadThemeBackgrounds();
			break;
		case "Custom":
			bgimages = BackgroundLoader.loadUserBackgrounds();
			break;
		}

		Image bgimage = null;
		if (!bgimages.isEmpty()) {
			bgimage = ListUtils.getRandomItem(bgimages);
			float avg = ImageUtils.getAverageLuminance(ImageUtils.toBufferedImage(bgimage));
			if (avg > 0.1) {
				avg = (float) Math.min(avg * 2, 0.85);
				bgimage = ImageUtils.drawOver(new ImageIcon(bgimage), new ImageIcon(
								ImageUtils.emptyImageWithSize(bgimage.getWidth(null), bgimage.getHeight(null),
										ColorUtils.applyAlpha(Theme.current().getSecondAltBackgroundColor(), Math.round(avg * 255)))))
						.getImage();
			}
		}

		return bgimage;
	}

}
