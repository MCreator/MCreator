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
import net.mcreator.io.UserFolderManager;
import net.mcreator.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorMakerTexturesCache {
	private static final Logger LOG = LogManager.getLogger("Armor Texture Cache");

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();
	private static String[] NAMES;

	public static void init() {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.armormaker", "png");
		ImageIO.setUseCache(false); // we use custom image cache for this
		templatesSorted.forEach(resourcePointer -> {
			try {
				String id = resourcePointer.identifier.toString();
				String folder = UserFolderManager.getFileFromUserFolder("/templates/textures/armormaker/").getPath();
				if (id.contains(folder))
					id = id.replace(folder, "");
				else
					id = id.replace("templates/textures/armormaker/", "");
				id = id.replace("/", "");
				id = id.replace(".png", "");
				CACHE.put(id, new ImageIcon(ImageIO.read(resourcePointer.getStream())));
			} catch (Exception e) {
				LOG.warn("Failed to load armor texture from template: " + resourcePointer.identifier);
			}
		});
		ImageIO.setUseCache(true);

		Set<String> localNames = new HashSet<>();
		CACHE.forEach((string, imageIcon) -> {
			String[] types = new String[] { "Bs", "1", "2", "By", "H", "L" };
			for (String str : types) {
				if (string.endsWith(str)) {
					string = StringUtils.abbreviateString(string, string.length() - str.length(), false);
				}
			}
			localNames.add(string);
		});
		NAMES = localNames.toArray(new String[0]);
	}

	public static ImageIcon getIcon(@Nullable String type, String tpl) {
		String itemName = type + tpl;
		if (type != null && CACHE.get(itemName) != null)
			return CACHE.get(itemName);
		else
			return CACHE.get("Standard" + tpl);
	}

	public static String[] getNAMES() {
		return NAMES;
	}
}
