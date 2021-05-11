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
import org.apache.commons.io.FilenameUtils;
import javax.annotation.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BlockItemIcons {

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	public static void init() {
		ImageIO.setUseCache(false); // we use custom image cache for this
		Map<String, ImageIcon> tmp = new Reflections("datalists.icons", new ResourcesScanner(), PluginLoader.INSTANCE)
				.getResources(Pattern.compile(".*\\.png")).parallelStream().collect(Collectors
						.toMap(resource -> FilenameUtils.removeExtension(FilenameUtils.getName(resource)),
								resource -> new ImageIcon(Toolkit.getDefaultToolkit()
										.createImage(PluginLoader.INSTANCE.getResource(resource)))));
		ImageIO.setUseCache(true);
		CACHE.putAll(tmp);
	}

	public static ImageIcon getIconForItem(@Nullable String itemName) {
		if (itemName != null && CACHE.get(itemName) != null)
			return CACHE.get(itemName);
		else
			return UIRES.get("missingblockicon");
	}

	public static ImageIcon getIconFor(String itemName) {
		String mappedKey = TEXTURE_MAPPINGS.get(itemName);
		if (mappedKey != null)
			return getIconForItem(mappedKey);
		else
			return getIconForItem(itemName);
	}

	//@formatter:off
	private static final HashMap<String, String> TEXTURE_MAPPINGS = new HashMap<String, String>() {{
		//NewToolGUI
		put("Pickaxe", 				"IRON_PICKAXE");
		put("Axe", 					"IRON_AXE");
		put("Sword", 				"IRON_SWORD");
		put("Spade", 				"IRON_SHOVEL");
		put("Hoe", 					"IRON_HOE");
		put("Shears", 				"SHEARS");

		//NewBlockGUI
		put("pickaxe", 				"IRON_PICKAXE");
		put("axe", 					"IRON_AXE");
		put("shovel", 				"IRON_SHOVEL");
		put("hoe",					"IRON_HOE");

		// biome types
		put("WARM", 				"GRASS");
		put("DESERT", 				"SAND#0");
		put("COOL", 				"ICE");
		put("ICY", 					"SNOW");

		//NewDimensionGUI
		put("Normal world gen", 	"GRASS");
		put("Nether like gen", 		"NETHERRACK");
		put("End like gen", 		"END_STONE");

		//NewStructureGenGUI
		put("Surface", 				"GRASS");
		put("Nether", 				"NETHERRACK");

		// food types
		put("eat", 					"BREAD");
		put("drink",				"POTIONITEM");
		put("block",				"GRASS");
		put("bow",					"BOW");
		put("crossbow",				"CROSSBOW");
		put("none",					"BARRIER");
		put("spear",				"ARROW");

		//Other
		put("Not specified", 		"BLANK");
	}};
	//@formatter:on

}
