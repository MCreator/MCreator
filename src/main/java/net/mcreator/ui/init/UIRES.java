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
import net.mcreator.util.image.svg.SVGProcessor;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UIRES {

	private static final Map<String, ImageIcon> THEME_CACHE = new ConcurrentHashMap<>();
	private static final Map<String, ImageIcon> FALLBACK_CACHE = new ConcurrentHashMap<>();

	private static final Pattern rasterPattern = Pattern.compile(".*\\.png");
	private static final Pattern vectorPattern = Pattern.compile(".*\\.svg");

	public static void preloadImages() {
		// first, preload textures of the current theme
		preloadRastersForTheme(THEME_CACHE, PreferencesManager.PREFERENCES.hidden.uiTheme.get());
		preloadVectorsForTheme(THEME_CACHE, PreferencesManager.PREFERENCES.hidden.uiTheme.get());

		// we also load default textures in non-default theme does not specify all textures
		if (!PreferencesManager.PREFERENCES.hidden.uiTheme.get().equals("default_dark")) {
			preloadRastersForTheme(FALLBACK_CACHE, "default_dark");
			preloadVectorsForTheme(FALLBACK_CACHE, "default_dark");
		} else {
			FALLBACK_CACHE.putAll(THEME_CACHE);
		}
	}

	private static void preloadRastersForTheme(Map<String, ImageIcon> cache, String theme) {
		ImageIO.setUseCache(false); // we use custom cache
		String themePath = "themes." + theme + ".images";
		PluginLoader.INSTANCE.getResources(themePath, rasterPattern).parallelStream().forEach(
				element -> cache.putIfAbsent(element.replace('/', '.').substring(themePath.length() + 1),
						new ImageIcon(Objects.requireNonNull(PluginLoader.INSTANCE.getResource(element)))));
		ImageIO.setUseCache(true);
	}

	private static void preloadVectorsForTheme(Map<String, ImageIcon> cache, String theme) {
		String themePath = "themes." + theme + ".images";
		PluginLoader.INSTANCE.getResources(themePath, vectorPattern).parallelStream().forEach(
				element -> cache.putIfAbsent(element.replace('/', '.').substring(themePath.length() + 1),
						SVGProcessor.getMultiResolutionIcon(
								SVGProcessor.loadSVG(Objects.requireNonNull(PluginLoader.INSTANCE.getResource(element)),
										null), 0, 0)));
	}

	/**
	 * Gets an image from the current theme or the default theme if the image is not found.
	 * </p>
	 * Image loading priority (top down, first match found):
	 * <ol>
	 *     <li>Current theme SVG</li>
	 *     <li>Current theme PNG</li>
	 *     <li>Default theme SVG</li>
	 *     <li>Default theme PNG</li>
	 *     <li>Throws NullPointerException</li>
	 * </ol>
	 *
	 * @param identifier the identifier of the image
	 * @return the image icon
	 */
	public static ImageIcon get(String identifier) {
		ImageIcon currentThemeSvg = THEME_CACHE.get(identifier + ".svg");
		if (currentThemeSvg != null)
			return currentThemeSvg;

		ImageIcon currentThemePng = THEME_CACHE.get(identifier + ".png");
		if (currentThemePng != null)
			return currentThemePng;

		ImageIcon fallbackThemeSvg = FALLBACK_CACHE.get(identifier + ".svg");
		if (fallbackThemeSvg != null)
			return fallbackThemeSvg;

		ImageIcon fallbackThemePng = FALLBACK_CACHE.get(identifier + ".png");
		if (fallbackThemePng != null)
			return fallbackThemePng;

		throw new NullPointerException("Image not found: " + identifier);
	}

	/**
	 * Returns a built-in image from the MCreator UI resources. Only works for raster images.
	 * Use {@link SVG} for vector images.
	 *
	 * @param identifier the identifier of the image
	 * @return the image icon
	 */
	public static ImageIcon getBuiltIn(String identifier) {
		return THEME_CACHE.computeIfAbsent("@" + identifier, key -> new ImageIcon(Objects.requireNonNull(
				ClassLoader.getSystemClassLoader().getResource("net/mcreator/ui/res/" + identifier + ".png"))));
	}

	public static class SVG {

		private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

		public static ImageIcon getBuiltIn(String identifier, int width, int height) {
			return getBuiltIn(identifier, width, height, null);
		}

		public static ImageIcon getBuiltIn(String identifier, int width, int height, @Nullable Color paint) {
			return CACHE.computeIfAbsent(computeKey("@" + identifier, width, height, paint), id -> {
				URL url = ClassLoader.getSystemClassLoader()
						.getResource("net/mcreator/ui/res/" + identifier.replace('.', '/') + ".svg");
				return SVGProcessor.getMultiResolutionIcon(SVGProcessor.loadSVG(url, paint), width, height);
			});
		}

		private static String computeKey(String identifier, int width, int height, @Nullable Color color) {
			//@formatter:off
			return identifier + ".svg" +
					(width != 0 ? ("." + width) : "") +
					(height != 0 ? ("." + height) : "") +
					(color == null ? "" : ("." + color.getRGB()));
			//@formatter:on
		}

	}

}
