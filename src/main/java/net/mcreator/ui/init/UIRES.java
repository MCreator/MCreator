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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UIRES {

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	private static final Pattern imagePattern = Pattern.compile(".*\\.(png|gif)");

	public static void preloadImages() {
		// first, preload textures of the current theme
		preloadImagesForTheme(PreferencesManager.PREFERENCES.hidden.uiTheme.get());

		// we also load default textures in non-default theme does not specify all textures
		if (!PreferencesManager.PREFERENCES.hidden.uiTheme.get().equals("default_dark"))
			preloadImagesForTheme("default_dark");
	}

	private static void preloadImagesForTheme(String theme) {
		ImageIO.setUseCache(false); // we use custom cache
		String themePath = "themes." + theme + ".images";
		PluginLoader.INSTANCE.getResources(themePath, imagePattern).parallelStream().forEach(
				element -> CACHE.putIfAbsent(element.replace('/', '.').substring(themePath.length() + 1),
						new ImageIcon(Objects.requireNonNull(PluginLoader.INSTANCE.getResource(element)))));
		ImageIO.setUseCache(true);
	}

	public static ImageIcon get(String identifier) {
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
			identifier += ".png";
		return CACHE.get(identifier);
	}

	public static ImageIcon getBuiltIn(String identifier) {
		if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
			identifier += ".png";
		String finalIdentifier = identifier;
		return CACHE.computeIfAbsent("@" + identifier, key -> new ImageIcon(Objects.requireNonNull(
				ClassLoader.getSystemClassLoader().getResource("net/mcreator/ui/res/" + finalIdentifier))));
	}

}
