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

	private static String pack = PreferencesManager.PREFERENCES.hidden.resourcePack;

	public static void preloadImages() {

		ImageIO.setUseCache(false); // we use custom image cache for this
		new Reflections("resourcepacks." + pack, new ResourcesScanner(), PluginLoader.INSTANCE).getResources(pngPattern)
				.parallelStream().forEach(element -> fromResourceID(element.replace("/", ".")));
		// We also load default textures in case a resource pack modify only one texture.
		if (!pack.equals("default")) {
			new Reflections("resourcepacks.default", new ResourcesScanner(), PluginLoader.INSTANCE)
					.getResources(pngPattern).parallelStream()
					.forEach(element -> fromResourceID(element.replace("/", ".")));
		}
		ImageIO.setUseCache(true);
	}

	public static ImageIcon get(String identifier) {
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
			identifier += ".png";
		System.out.println("resourcepacks." + pack + ".res." + identifier);
		if (PluginLoader.INSTANCE.getResource("resourcepacks/" + pack + "/res/" + identifier) != null) {
			System.out.println("1");
			//We start by checking if the loaded pack contains the image
			return UIRES.fromResourceID("resourcepacks." + pack + ".res." + identifier);
		}
		else {
			// If the loaded pack does not have the image, we load the default one
			System.out.println("2");
			return UIRES.fromResourceID("resourcepacks.default.res." + identifier);
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

}
