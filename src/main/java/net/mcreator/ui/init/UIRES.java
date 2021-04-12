/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ThemeLoader;
import org.apache.commons.io.FilenameUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UIRES {
	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	private static final Pattern pngPattern = Pattern.compile(".*\\.(png|gif)");

	public static void preloadImages() {

		ImageIO.setUseCache(false); // we use custom image cache for this
		new Reflections("themes." + PreferencesManager.PREFERENCES.hidden.uiTheme, new ResourcesScanner(),
				PluginLoader.INSTANCE).getResources(pngPattern).parallelStream()
				.forEach(element -> fromResourceID(element.replace("/", ".")));
		// We also load default textures in case a resource pack modify only one texture.
		if (!PreferencesManager.PREFERENCES.hidden.uiTheme.equals("default_dark")) {
			new Reflections("themes.default_dark", new ResourcesScanner(), PluginLoader.INSTANCE)
					.getResources(pngPattern).parallelStream()
					.forEach(element -> fromResourceID(element.replace("/", ".")));
		}
		ImageIO.setUseCache(true);
	}

	public static ImageIcon get(String identifier) {
		String str;
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif"))) {
			str = identifier.replace(".", "/");
			identifier += ".png";
		} else {
			str = FilenameUtils.removeExtension(identifier).replace(".", "/");
		}

		if (PluginLoader.INSTANCE
				.getResource("themes/" + PreferencesManager.PREFERENCES.hidden.uiTheme + "/images/" + str + ".png")
				!= null) {
			//We start by checking if the loaded pack contains the image
			return UIRES.fromResourceID(
					"themes." + PreferencesManager.PREFERENCES.hidden.uiTheme + ".images." + identifier);
		} else {
			// If the loaded pack does not have the image, we load the default one
			return UIRES.fromResourceID("themes.default_dark.images." + identifier);
		}
	}

	public static ImageIcon fromResourceID(String identifier) {
		// parse identifier
		int lastDot = identifier.lastIndexOf('.');
		identifier = identifier.substring(0, lastDot).replace(".", "/") + identifier.substring(lastDot);

		if (CACHE.get(identifier) != null)
			return CACHE.get(identifier);
		else {
			ImageIcon newItem = new ImageIcon(
					Toolkit.getDefaultToolkit().createImage(PluginLoader.INSTANCE.getResource(identifier)));
			CACHE.put(identifier, newItem);
			return newItem;
		}
	}

	public static ImageIcon getInternal(String identifier) {
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
			identifier += ".png";

		identifier = "net.mcreator.ui.res." + identifier;
		int lastDot = identifier.lastIndexOf('.');
		identifier = identifier.substring(0, lastDot).replace(".", "/") + identifier.substring(lastDot);

		ImageIcon newItem = new ImageIcon(Toolkit.getDefaultToolkit()
				.createImage(ClassLoader.getSystemClassLoader().getResource(identifier)));
		CACHE.put(identifier, newItem);
		return newItem;
	}

}
