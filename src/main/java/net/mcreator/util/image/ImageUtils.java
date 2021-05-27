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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.Random;

public class ImageUtils {

	public static Color getAverageColor(BufferedImage image) {
		long redBucket = 0;
		long greenBucket = 0;
		long blueBucket = 0;
		long pixelCount = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color c = new Color(image.getRGB(x, y));
				if (c.getAlpha() >= 127) {
					redBucket += c.getRed();
					greenBucket += c.getGreen();
					blueBucket += c.getBlue();
					pixelCount++;
				}
			}
		}
		return new Color((int) Math.min(redBucket / pixelCount, 255), (int) Math.min(greenBucket / pixelCount, 255),
				(int) Math.min(blueBucket / pixelCount, 255));
	}

	public static float getAverageLuminance(BufferedImage image) {
		float luminance = 0;
		long pixelCount = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int color = image.getRGB(x, y);
				int red = (color >>> 16) & 0xFF;
				int green = (color >>> 8) & 0xFF;
				int blue = color & 0xFF;

				// SRGB luminance constants
				luminance += (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;

				pixelCount++;
			}
		}
		return luminance / (float) pixelCount;
	}

	public static ImageIcon drawOver(ImageIcon i, ImageIcon wh) {
		Image original = i.getImage();
		Image over = wh.getImage();

		int x = original.getWidth(null);
		int y = original.getHeight(null);

		BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(original, 0, 0, x, y, null);
		g.drawImage(over, 0, 0, x, y, null);
		g.dispose();

		return new ImageIcon(resizedImage);
	}

	public static ImageIcon drawOver(ImageIcon i, ImageIcon wh, int xp, int yp, int w, int h) {
		Image original = i.getImage();
		Image over = wh.getImage();

		int x = original.getWidth(null);
		int y = original.getHeight(null);

		BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(original, 0, 0, x, y, null);
		g.drawImage(over, xp, yp, w, h, null);
		g.dispose();

		return new ImageIcon(resizedImage);
	}

	public static ImageIcon drawOverNoScale(ImageIcon i, ImageIcon wh) {
		Image original = i.getImage();
		Image over = wh.getImage();

		int x = original.getWidth(null);
		int y = original.getHeight(null);

		BufferedImage resizedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(original, 0, 0, x, y, null);
		g.drawImage(over, 0, 0, over.getWidth(null), over.getHeight(null), null);
		g.dispose();

		return new ImageIcon(resizedImage);
	}

	private static Color[][] bufferedImageToColorArray(BufferedImage buf) {
		Color[][] car = new Color[buf.getHeight()][buf.getWidth()];
		for (int i = 0; i < buf.getHeight(); i++)
			for (int j = 0; j < buf.getWidth(); j++)
				car[i][j] = new Color(buf.getRGB(j, i), true);
		return car;
	}

	private static BufferedImage colorArrayToBufferedImage(Color[][] car) {
		BufferedImage buf = new BufferedImage(car[0].length, car.length, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < car.length; i++)
			for (int j = 0; j < car[0].length; j++)
				buf.setRGB(j, i, car[i][j].getRGB());
		return buf;
	}

	public static ImageIcon colorize(ImageIcon icon, Color modifier, boolean type) {
		if (type)
			return colorize1(icon, modifier);
		else
			return colorize2(icon, modifier);
	}

	private static ImageIcon colorize1(ImageIcon icon, Color modifier) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		BufferedImageOp colorizeFilter = createColorizeOp((short) modifier.getRed(), (short) modifier.getGreen(),
				(short) modifier.getBlue());
		BufferedImage targetImage = colorizeFilter.filter(bi, null);
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(targetImage.getSource()));
	}

	private static ImageIcon colorize2(ImageIcon icon, Color modifier) {
		float[] mod = Color.RGBtoHSB(modifier.getRed(), modifier.getGreen(), modifier.getBlue(), null);
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		Color[][] car = bufferedImageToColorArray(bi);
		for (int i = 0; i < car.length; i++)
			for (int j = 0; j < car[0].length; j++) {
				int alpha = car[i][j].getAlpha();
				float[] colr = Color.RGBtoHSB(car[i][j].getRed(), car[i][j].getGreen(), car[i][j].getBlue(), null);
				Color hsb = Color.getHSBColor(mod[0], mod[1], colr[2]);
				car[i][j] = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), alpha);
			}
		return new ImageIcon(colorArrayToBufferedImage(car));
	}

	public static ImageIcon changeSaturation(ImageIcon icon, float modifier) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		Color[][] car = bufferedImageToColorArray(bi);

		for (int i = 0; i < car.length; i++)
			for (int j = 0; j < car[0].length; j++) {
				int alpha = car[i][j].getAlpha();
				float[] colr = Color.RGBtoHSB(car[i][j].getRed(), car[i][j].getGreen(), car[i][j].getBlue(), null);
				Color hsb = Color.getHSBColor(colr[0], colr[1] * modifier, colr[2]);
				car[i][j] = new Color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(), alpha);
			}

		return new ImageIcon(colorArrayToBufferedImage(car));
	}

	private static LookupOp createColorizeOp(short R1, short G1, short B1) {
		short[] alpha = new short[256];
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];

		for (short i = 0; i < 256; i++) {
			alpha[i] = i;
			red[i] = (short) ((R1 + i) / 2);
			green[i] = (short) ((G1 + i) / 2);
			blue[i] = (short) ((B1 + i) / 2);
		}

		short[][] data = new short[][] { red, green, blue, alpha };

		LookupTable lookupTable = new ShortLookupTable(0, data);
		return new LookupOp(lookupTable, null);
	}

	public static BufferedImage resize(Image image, int size) {
		return resizeImageWithHint(toBufferedImage(image), size, size);
	}

	public static BufferedImage resize(Image image, int size, int y) {
		return resizeImageWithHint(toBufferedImage(image), size, y);
	}

	public static BufferedImage resizeAA(Image image, int size) {
		return resizeImageWithHintAA(toBufferedImage(image), size, size);
	}

	public static BufferedImage resizeAA(Image image, int size, int y) {
		return resizeImageWithHintAA(toBufferedImage(image), size, y);
	}

	public static BufferedImage emptyImageWithSize(int size, int y, Color color) {
		BufferedImage resizedImage = new BufferedImage(size, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		if (color != null) {
			g.setColor(color);
			g.fillRect(0, 0, size, y);
		}
		g.dispose();
		return resizedImage;
	}

	private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int size, int y) {
		BufferedImage resizedImage = new BufferedImage(size, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, size, y, null);
		g.dispose();
		return resizedImage;
	}

	private static BufferedImage resizeImageWithHintAA(BufferedImage originalImage, int size, int y) {
		BufferedImage resizedImage = new BufferedImage(size, y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(originalImage, 0, 0, size, y, null);
		g.dispose();
		return resizedImage;
	}

	public static BufferedImage eraseRect(Image image, int x, int y, int w, int h) {
		BufferedImage out = toBufferedImage(image);
		Graphics2D g = out.createGraphics();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(x, y, w, h);
		g.dispose();
		return out;
	}

	public static BufferedImage eraseExceptRect(Image image, int x, int y, int w, int h) {
		BufferedImage out = toBufferedImage(image);
		Graphics2D g = out.createGraphics();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, out.getWidth(), y);
		g.clearRect(0, 0, x, out.getHeight());
		g.clearRect(x + w, 0, out.getWidth(), out.getHeight());
		g.clearRect(0, y + h, out.getWidth(), out.getHeight());
		g.dispose();
		return out;
	}

	public static BufferedImage maskTransparency(BufferedImage base, BufferedImage mask) {
		Color[][] baseCol = bufferedImageToColorArray(base);
		Color[][] maskCol = bufferedImageToColorArray(mask);
		for (int i = 0; i < baseCol.length; i++)
			for (int j = 0; j < baseCol[0].length; j++) {
				if (maskCol[i][j].getAlpha() == 0)
					baseCol[i][j] = new Color(0, 0, 0, 0);
			}
		return colorArrayToBufferedImage(baseCol);
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;

		int width = img.getWidth(null) > 0 ? img.getWidth(null) : 1;
		int height = img.getHeight(null) > 0 ? img.getHeight(null) : 1;

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public static BufferedImage translate(Image img, double x, double y) {
		BufferedImage image = toBufferedImage(img);
		AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		return op.filter(image, null);
	}

	public static BufferedImage rotate(Image img, int degrees) {
		BufferedImage image = toBufferedImage(img);
		double rotationRequired = Math.toRadians(degrees);
		double locationX = image.getWidth() / 2.0;
		double locationY = image.getHeight() / 2.0;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		return op.filter(image, null);
	}

	public static ImageIcon rotate(ImageIcon ic, int degrees) {
		BufferedImage image = rotate(ic.getImage(), degrees);

		return new ImageIcon(image);

	}

	public static BufferedImage crop(BufferedImage src, Rectangle rect) {
		return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
	}

	public static Image generateTransparentImage(int w, int h) {
		return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}

	public static Image cover(Image image, Dimension dimension) {
		BufferedImage buf = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);

		float imgRatio = (float) image.getHeight(null) / (float) image.getWidth(null);
		float panelRatio = (float) dimension.height / (float) dimension.width;

		int w, h, x, y;
		if (panelRatio > imgRatio) {
			h = dimension.height;
			w = (int) ((float) dimension.height / imgRatio);
		} else {
			w = dimension.width;
			h = (int) ((float) dimension.width * imgRatio);
		}

		x = (dimension.width - w) / 2;
		y = (dimension.height - h) / 2;

		buf.getGraphics().drawImage(image, x, y, w, h, null);
		return buf;
	}

	public static BufferedImage darken(BufferedImage image) {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				g2d.setPaint(new Color(image.getRGB(x, y), true).darker());
				g2d.drawLine(x, y, x, y);
			}
		}
		return image;
	}

	public static BufferedImage brighten(BufferedImage image) {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				g2d.setPaint(new Color(image.getRGB(x, y), true).brighter());
				g2d.drawLine(x, y, x, y);
			}
		}
		return image;
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null)
				.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
	}

	public static BufferedImage noiseHSV(BufferedImage image, float hfactor, float sfactor, float vfactor, long seed) {
		BufferedImage copy = deepCopy(image);
		Random generator = new Random(seed);
		for (int x = 0; x < copy.getWidth(); x++)
			for (int y = 0; y < copy.getHeight(); y++) {
				Color c = new Color(copy.getRGB(x, y), true);
				int alpha = c.getAlpha();
				if (alpha != 0) {
					float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

					float h = limitDeviation(hsb[0], generator.nextFloat() * hfactor, nextSign(generator));
					float s = limitDeviation(hsb[1], generator.nextFloat() * sfactor, nextSign(generator));
					float b = limitDeviation(hsb[2], generator.nextFloat() * vfactor, nextSign(generator));

					Color noalpha = Color.getHSBColor(h, s, b);
					copy.setRGB(x, y,
							new Color(noalpha.getRed(), noalpha.getGreen(), noalpha.getBlue(), alpha).getRGB());
				}
			}
		return copy;
	}

	public static ImageIcon fit(Image image, int size) {
		return fit(image, size, size);
	}

	public static ImageIcon fit(Image image, int width, int height) {
		BufferedImage fit = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = fit.createGraphics();
		if (width * 1.0 / height > image.getWidth(null) * 1.0 / image.getHeight(null)) {
			int iwidth = (int) Math.round(image.getWidth(null) * 1.0 / image.getHeight(null) * height);
			graphics.drawImage(image, Math.abs(iwidth - width) / 2, 0, iwidth, height, null);
		} else {
			int iheight = (int) Math.round(image.getHeight(null) * 1.0 / image.getWidth(null) * width);
			graphics.drawImage(image, 0, Math.abs(iheight - height) / 2, width, iheight, null);
		}
		graphics.dispose();
		return new ImageIcon(fit);
	}

	private static int nextSign(Random random) {
		return random.nextBoolean() ? 1 : -1;
	}

	private static float limitDeviation(float original, float deviation, int sign) {
		return Math.max(Math.min(original + deviation * sign, 1), 0);
	}

	public static BufferedImage autoCropTile(BufferedImage tile) {
		if (tile.getHeight() > tile.getWidth())
			return crop(tile, new Rectangle(0, 0, tile.getWidth(), tile.getWidth()));
		else
			return crop(tile, new Rectangle(0, 0, tile.getHeight(), tile.getHeight()));
	}

	public static Image randomTile(BufferedImage tile, Random random) {
		try {
			int tilesize = Math.min(tile.getWidth(), tile.getHeight());
			TiledImageUtils tiu = new TiledImageUtils(tile, tilesize, tilesize);
			int tilex = random.nextInt(tiu.getWidthInTiles()) + 1;
			int tiley = random.nextInt(tiu.getHeightInTiles()) + 1;
			ImageIcon imge = tiu.getIcon(tilex, tiley);
			return imge.getImage();
		} catch (InvalidTileSizeException e) {
			return autoCropTile(tile);
		}
	}

	public static Image randomTile(BufferedImage tile) {
		return randomTile(tile, new Random());
	}

	public static BufferedImage resizeAndCrop(Image image, int size) {
		return resizeImageWithHint(autoCropTile(toBufferedImage(image)), size, size);
	}

	public static BufferedImage generateCuboidImage(Image texture, int x, int y, int z, int xOff, int yOff, int zOff) {
		return generateCuboidImage(texture, texture, texture, x, y, z, xOff, yOff, zOff);
	}

	public static BufferedImage generateCuboidImage(Image top, Image front, Image side, int x, int y, int z,
			int xOff, int yOff, int zOff) {
		BufferedImage out = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) out.getGraphics();

		int xTot = x + xOff;
		int yTot = y + yOff;
		int zTot = z + zOff;

		// Top face
		Point2D t2 = new Point2D.Double(16, 16 - yTot), t3 = new Point2D.Double(0, 24 - yTot),
				t4 = new Point2D.Double(16, 32 - yTot), t1 = new Point2D.Double(32, 24 - yTot);
		g2d.drawImage(ImageTransformUtil.computeImage(brighten(eraseExceptRect(
				resizeAndCrop(top, 32), 32-2*zTot, 32-2*xTot, 2*z, 2*x)),
				t4, t1,	t2,	t3), null, null);

		// Front face
		Point2D f1 = new Point2D.Double(16 - xTot, xTot/2d), f2 = new Point2D.Double(16 - xTot, 16 + xTot/2d),
				f3 = new Point2D.Double(32 - xTot, 24 + xTot/2d), f4 = new Point2D.Double(32 - xTot, 8 + xTot/2d);
		g2d.drawImage(ImageTransformUtil.computeImage(eraseExceptRect(
				resizeAndCrop(front, 32), 2*zOff, 32-2*yTot, 2*z, 2*y), f1, f2, f3, f4),
				null, null);

		// Side face
		Point2D r1 = new Point2D.Double(zTot, 8 + zTot/2d), r2 = new Point2D.Double(zTot, 24 + zTot/2d),
				r3 = new Point2D.Double(16 + zTot, 16 + zTot/2d), r4 = new Point2D.Double(16 + zTot, zTot/2d);
		g2d.drawImage(ImageTransformUtil.computeImage(darken(eraseExceptRect(
				resizeAndCrop(side, 32), 32-2*xTot, 32-2*yTot, 2*x, 2*y)), r1, r2, r3, r4), null, null);

		g2d.dispose();
		return out;
	}
}
