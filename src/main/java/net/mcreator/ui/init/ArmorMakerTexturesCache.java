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

import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorMakerTexturesCache {
	private static final Logger LOG = LogManager.getLogger("Armor Texture Cache");

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	private static String[] TEMPLATE_NAMES = new String[0];

	public static void init() {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.armormaker", "png");
		ImageIO.setUseCache(false); // we use custom image cache for this
		templatesSorted.forEach(resourcePointer -> {
			try {
				CACHE.put(resourcePointer.toString(), new ImageIcon(ImageIO.read(resourcePointer.getStream())));
			} catch (Exception e) {
				LOG.warn("Failed to load armor texture from template: " + resourcePointer.identifier);
			}
		});
		ImageIO.setUseCache(true);

		Set<String> templateNamesSet = new HashSet<>();
		String[] validSuffixes = new String[] { "Bs", "1", "2", "By", "H", "L" };

		CACHE.keySet().forEach(imageName -> {
			for (String suffix : validSuffixes) {
				if (imageName.endsWith(suffix)) {
					imageName = StringUtils.abbreviateString(imageName, imageName.length() - suffix.length(), false);
					templateNamesSet.add(imageName);
				}
			}
		});
		TEMPLATE_NAMES = templateNamesSet.toArray(new String[0]);
	}

	public static ImageIcon getIcon(@Nullable String type, String tpl) {
		if (type != null && CACHE.get(type + tpl) != null)
			return CACHE.get(type + tpl);
		else // Fallback if part is not defined
			return CACHE.get("Standard" + tpl);
	}

	public static String[] getTemplateNames() {
		return TEMPLATE_NAMES;
	}
}
