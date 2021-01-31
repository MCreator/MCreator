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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorMakerTexturesCache {
	private static final Logger LOG = LogManager.getLogger("Armor Texture Cache");
	private static final String MCR_FOLDER = System.getProperty("user.home")+ "\\.mcreator\\";

	public static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();
	public static final List<String> NAMES = new ArrayList<>();

	public static void init() {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.armormaker", "png");
		ImageIO.setUseCache(false); // we use custom image cache for this
		templatesSorted.forEach(resourcePointer -> {
			try {
				CACHE.put(resourcePointer.identifier.toString(), new ImageIcon(ImageIO.read(resourcePointer.getStream())));
			} catch (Exception e) {
				LOG.warn("Failed to load armor texture from template: " + resourcePointer.identifier);
			}
		});
		ImageIO.setUseCache(true);

		CACHE.forEach((string, imageIcon) -> {
			String name = string;
			name = name.replace("templates/textures/armormaker/", "");
			name = name.replace("/", "");
			String[] types = new String[] {"Bs", "1", "2", "By", "H", "L"};
			for(String str : types){
				if(name.contains(str))
					name = name.replace(str + ".png", "");
				String folder = UserFolderManager.getFileFromUserFolder("/templates/textures/armormaker/").getPath();
				if(name.contains(folder)){
					name = name.replace(folder + "\\","");
				}
			}
			if(!NAMES.contains(name))
				NAMES.add(name);
		});
	}

	public static ImageIcon getIcon(@Nullable String itemName) {
		String fullItemName = itemName + ".png";
		String folder = "templates/textures/armormaker/";
		if(itemName != null && CACHE.get(MCR_FOLDER + folder.replace("/", "\\") + fullItemName) != null)
			return CACHE.get(MCR_FOLDER + folder.replace("/", "\\") + fullItemName);
		else if (itemName != null && CACHE.get(folder + fullItemName) != null)
			return CACHE.get(folder + fullItemName);
		else
			return null;
	}
}
