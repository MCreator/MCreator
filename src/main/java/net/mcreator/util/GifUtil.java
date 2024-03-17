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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class GifUtil {

	private static final Logger LOG = LogManager.getLogger("Gif Util");

	public static BufferedImage[] readAnimatedGif(File file) {
		try {
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			reader.setInput(ImageIO.createImageInputStream(new FileInputStream(file)));

			ArrayList<GifFrame> frames = new ArrayList<>();

			int width = -1;
			int height = -1;

			IIOMetadata metadata = reader.getStreamMetadata();
			if (metadata != null) {
				IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(
						metadata.getNativeMetadataFormatName());
				NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
				if (globalScreenDescriptor.getLength() > 0) {
					IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

					if (screenDescriptor != null) {
						width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
						height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
					}
				}
			}

			BufferedImage master = null;
			Graphics2D masterGraphics = null;
			for (int frameIndex = 0; ; frameIndex++) {
				BufferedImage image;
				try {
					image = reader.read(frameIndex);
				} catch (IndexOutOfBoundsException io) {
					break;
				}

				if (width == -1 || height == -1) {
					width = image.getWidth();
					height = image.getHeight();
				}

				IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex)
						.getAsTree("javax_imageio_gif_image_1.0");
				IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
				int delay = Integer.parseInt(gce.getAttribute("delayTime"));
				String disposal = gce.getAttribute("disposalMethod");

				int x = 0;
				int y = 0;

				if (master == null) {
					master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					masterGraphics = master.createGraphics();
					masterGraphics.setBackground(new Color(0, 0, 0, 0));
				} else {
					NodeList children = root.getChildNodes();
					for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
						Node nodeItem = children.item(nodeIndex);
						if (nodeItem.getNodeName().equals("ImageDescriptor")) {
							NamedNodeMap map = nodeItem.getAttributes();
							x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
							y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
						}
					}
				}
				masterGraphics.drawImage(image, x, y, null);

				BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null),
						master.isAlphaPremultiplied(), null);
				frames.add(new GifFrame(copy, disposal));

				if (disposal.equals("restoreToPrevious")) {
					BufferedImage from = null;
					for (int i = frameIndex - 1; i >= 0; i--) {
						if (!frames.get(i).disposal().equals("restoreToPrevious") || frameIndex == 0) {
							from = frames.get(i).image();
							break;
						}
					}

					if (from != null) {
						master = new BufferedImage(from.getColorModel(), from.copyData(null),
								from.isAlphaPremultiplied(), null);
						masterGraphics = master.createGraphics();
						masterGraphics.setBackground(new Color(0, 0, 0, 0));
					}
				} else if (disposal.equals("restoreToBackgroundColor")) {
					masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
				}
			}
			reader.dispose();

			return frames.stream().map(GifFrame::image).toArray(BufferedImage[]::new);
		} catch (Exception e) {
			LOG.error("Failed to read animated gif", e);
			return new BufferedImage[0];
		}
	}

	private record GifFrame(BufferedImage image, String disposal) {}

}
