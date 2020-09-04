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

package net.mcreator.util.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TiledImageUtils {

	private final ImageIcon[][] imageTiles;

	public TiledImageUtils(InputStream stream, int width, int height) throws InvalidTileSizeException, IOException {
		this(convert(ImageIO.read(stream), BufferedImage.TYPE_INT_ARGB), width, height);
	}

	public TiledImageUtils(ImageIcon stream, int width, int height) throws InvalidTileSizeException {
		this(convert(stream.getImage(), BufferedImage.TYPE_INT_ARGB), width, height);
	}

	public TiledImageUtils(BufferedImage buf, int width, int height) throws InvalidTileSizeException {
		if (buf != null && (buf.getWidth() % width == 0 || buf.getWidth() % width == 0)) {

			imageTiles = new ImageIcon[buf.getWidth() / width][buf.getHeight() / height];

			for (int i = 0; i < imageTiles.length; i++)
				for (int j = 0; j < imageTiles[0].length; j++)
					imageTiles[i][j] = new ImageIcon(buf.getSubimage(width * i, height * j, width, height));
		} else
			throw new InvalidTileSizeException(width, height);
	}

	public ImageIcon getIcon(int x, int y) {
		if (!(x < 1 || y < 1 || x > imageTiles.length || y > imageTiles[0].length))
			return imageTiles[x - 1][y - 1];
		else
			return new ImageIcon();
	}

	public int getHeightInTiles() {
		return imageTiles[0].length;
	}

	public int getWidthInTiles() {
		return imageTiles.length;
	}

	public static BufferedImage convert(Image src, int bufImgType) {
		BufferedImage img = new BufferedImage(src.getWidth(null), src.getHeight(null), bufImgType);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(src, 0, 0, null);
		g2d.dispose();
		return img;
	}
}
