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

package net.mcreator.util;

import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GifUtil {

	private static final Logger LOG = LogManager.getLogger("Gif Util");

	public static BufferedImage[] readAnimatedGif(File file) {
		try {
			String[] imageatt = new String[] { "imageLeftPosition", "imageTopPosition", "imageWidth", "imageHeight" };

			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			ImageInputStream ciis = ImageIO.createImageInputStream(file);
			reader.setInput(ciis, false);

			int noi = reader.getNumImages(true);

			List<BufferedImage> retval = new ArrayList<>();

			BufferedImage master = null;
			for (int i = 0; i < noi; i++) {
				BufferedImage image = reader.read(i);
				NodeList children = reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0").getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node nodeItem = children.item(j);
					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						Map<String, Integer> imageAttr = new HashMap<>();

						for (String anImageatt : imageatt) {
							NamedNodeMap attr = nodeItem.getAttributes();
							Node attnode = attr.getNamedItem(anImageatt);
							imageAttr.put(anImageatt, Integer.valueOf(attnode.getNodeValue()));
						}
						if (i == 0) {
							master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"),
									BufferedImage.TYPE_INT_ARGB);
						}
						if (master != null) {
							master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"),
									imageAttr.get("imageTopPosition"), null);
						}
					}
				}

				if (master != null)
					retval.add(ImageUtils.resize(master, master.getWidth(), master.getHeight()));
			}

			return retval.toArray(new BufferedImage[0]);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new BufferedImage[0];
	}

}
