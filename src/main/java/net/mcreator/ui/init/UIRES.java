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

import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UIRES {

	private static final Logger LOG = LogManager.getLogger("Resource Pack Loader");

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	private static final Pattern pngPattern = Pattern.compile(".*\\.(png|gif)");

	public static void preloadImages() {
		LOG.debug("Loading resource packs");
		ImageIO.setUseCache(false); // we use custom image cache for this
		new Reflections("resourcepacks", new ResourcesScanner(), PluginLoader.INSTANCE)
				.getResources(pngPattern).parallelStream()
				.forEach(element -> fromResourceID(element.replace("/", ".")));
		ImageIO.setUseCache(true);
	}

	public static ImageIcon get(String identifier) {
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
			identifier += ".png";
		return UIRES.fromResourceID("resourcepacks.net.mcreator.ui.res." + identifier);
	}

	public static ImageIcon fromResourceID(String identifier) {
		// parse identifier
		int lastDot = identifier.lastIndexOf('.');
		identifier = identifier.substring(0, lastDot).replace(".", "/") + identifier.substring(lastDot);

		if (CACHE.get(identifier) != null)
			return CACHE.get(identifier);
		else {
			ImageIcon newItem = new ImageIcon(Toolkit.getDefaultToolkit()
					.createImage(PluginLoader.INSTANCE.getResource(identifier)));
			CACHE.put(identifier, newItem);
			return newItem;
		}
	}

}
