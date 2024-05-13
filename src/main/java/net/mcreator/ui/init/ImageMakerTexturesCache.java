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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageMakerTexturesCache {

	private static final Logger LOG = LogManager.getLogger("Texture Cache");

	public static final Map<ResourcePointer, ImageIcon> CACHE = new ConcurrentHashMap<>();

	public static void init() {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.texturemaker", "png");
		ImageIO.setUseCache(false); // we use custom image cache for this
		templatesSorted.forEach(resourcePointer -> {
			try {
				CACHE.put(resourcePointer, new ImageIcon(ImageIO.read(resourcePointer.getStream())));
			} catch (Exception e) {
				LOG.warn("Failed to load texture from templates: {}", resourcePointer.identifier);
			}
		});
		ImageIO.setUseCache(true);
	}

}
