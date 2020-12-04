/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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

package net.mcreator.ui.dialogs.tools.plugin;

import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.UIRES;
import org.apache.commons.io.FilenameUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PackMakerToolIcons {

	public static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	public static void init() {
		ImageIO.setUseCache(false); // we use custom image cache for this
		Map<String, ImageIcon> tmp = new Reflections("tools.packmakers.icons", new ResourcesScanner(), PluginLoader.INSTANCE)
				.getResources(Pattern.compile(".*\\.png")).parallelStream().collect(Collectors
						.toMap(resource -> FilenameUtils.removeExtension(FilenameUtils.getName(resource)),
								resource -> new ImageIcon(Toolkit.getDefaultToolkit()
										.createImage(PluginLoader.INSTANCE.getResource(resource)))));
		ImageIO.setUseCache(true);
		CACHE.putAll(tmp);
	}

	public static ImageIcon getIconForItem(String itemName) {
		if (CACHE.get(itemName) != null)
			return CACHE.get(itemName);
		else
			return UIRES.get("missingblockicon");
	}
}
