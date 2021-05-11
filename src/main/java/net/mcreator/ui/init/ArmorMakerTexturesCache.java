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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
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

		CACHE.keySet().forEach(imageName -> {
			for (ArmorTexturePart part : ArmorTexturePart.values()) {
				if (imageName.endsWith(part.getSuffix())) {
					templateNamesSet.add(imageName.substring(0, imageName.length() - part.getSuffix().length()));
				}
			}
		});

		TEMPLATE_NAMES = templateNamesSet.toArray(new String[0]);
	}

	public static ImageIcon getIcon(@Nullable String templateName, ArmorTexturePart part) {
		if (templateName != null && CACHE.get(templateName + part.getSuffix()) != null)
			return CACHE.get(templateName + part.getSuffix());
		else // Fallback if part is not defined
			return CACHE.get("Standard" + part.getSuffix());
	}

	public static String[] getTemplateNames() {
		return TEMPLATE_NAMES;
	}

	public enum ArmorTexturePart {

		LAYER1("_layer_1"), LAYER2("_layer_2"), HELMET("_helmet"), BODY("_body"), LEGGINGS("_leggings"), BOOTS(
				"_boots");

		private final String suffix;

		ArmorTexturePart(String suffix) {
			this.suffix = suffix;
		}

		public String getSuffix() {
			return suffix;
		}

	}

}
