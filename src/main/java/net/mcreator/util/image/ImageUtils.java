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
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.util.Random;
import java.util.function.Function;

public class ImageUtils {

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

	public static ImageIcon createColorSquare(Color color, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();

		return new ImageIcon(bi);
	}

	public static ImageIcon colorize(ImageIcon icon, Color modifier, boolean type) {
		return type ? colorize1(icon, modifier) : colorize2(icon, modifier);
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

	public static Image resize(Image image, int size) {
		return resize(image, size, size);
	}

	public static Image resizeAA(Image image, int size) {
		return resizeAA(image, size, size);
	}

	public static Image resize(Image image, int w, int h) {
		if (image instanceof BaseMultiResolutionImage) {
			Image[] sourceImages = ((BaseMultiResolutionImage) image).getResolutionVariants().toArray(new Image[0]);
			double widthMultiplier = (double) w / sourceImages[0].getWidth(null);
			double heightMultiplier = (double) h / sourceImages[0].getHeight(null);

			if (widthMultiplier == 1 && heightMultiplier == 1)
				return image;

			return applyOperationToAllResolutions((BaseMultiResolutionImage) image,
					i -> resize(i, (int) (w * widthMultiplier), (int) (h * heightMultiplier)));
		}

		return resizeImage(toBufferedImage(image), w, h);
	}

	public static Image resizeAA(Image image, int w, int h) {
		if (image instanceof BaseMultiResolutionImage) {
			Image[] sourceImages = ((BaseMultiResolutionImage) image).getResolutionVariants().toArray(new Image[0]);
			double widthMultiplier = (double) w / sourceImages[0].getWidth(null);
			double heightMultiplier = (double) h / sourceImages[0].getHeight(null);

			if (widthMultiplier == 1 && heightMultiplier == 1)
				return image;

			return applyOperationToAllResolutions((BaseMultiResolutionImage) image,
					i -> resizeAA(i, (int) (w * widthMultiplier), (int) (h * heightMultiplier)));
		}

		return resizeImageWithAA(toBufferedImage(image), w, h);
	}

	public static BufferedImage resizeAndCrop(Image image, int size) {
		return resizeImage(autoCropTile(toBufferedImage(image)), size, size);
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
		if (img instanceof BufferedImage bufferedImage)
			return bufferedImage;

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
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(),
				bi.getHeight());
	}

	public static BufferedImage noiseHSV(BufferedImage image, float hfactor, float sfactor, float vfactor, long seed) {
		Random generator = new Random(seed);
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++) {
				Color c = new Color(image.getRGB(x, y), true);
				int alpha = c.getAlpha();
				if (alpha != 0) {
					float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

					float h = limitDeviation(hsb[0], generator.nextFloat() * hfactor, nextSign(generator));
					float s = limitDeviation(hsb[1], generator.nextFloat() * sfactor, nextSign(generator));
					float b = limitDeviation(hsb[2], generator.nextFloat() * vfactor, nextSign(generator));

					Color noalpha = Color.getHSBColor(h, s, b);
					image.setRGB(x, y,
							new Color(noalpha.getRed(), noalpha.getGreen(), noalpha.getBlue(), alpha).getRGB());
				}
			}
		return image;
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

	public static BufferedImage autoCropTile(BufferedImage tile) {
		if (tile.getHeight() == tile.getWidth())
			return tile;
		else if (tile.getHeight() > tile.getWidth())
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

	public static BufferedImage generateCuboidImage(Image texture, int x, int y, int z, int xOff, int yOff, int zOff) {
		return generateCuboidImage(texture, texture, texture, x, y, z, xOff, yOff, zOff);
	}

	/**
	 * Generates a cuboid image given the textures, the dimensions, and the offsets on the axes.
	 *
	 * @param top   <p>The top face texture</p>
	 * @param front <p>The front face texture</p>
	 * @param side  <p>The side (right) face texture</p>
	 * @param x     <p>The length of the cuboid (width of the side face)</p>
	 * @param y     <p>The height of the cuboid</p>
	 * @param z     <p>The width of the cuboid (width of the front face)</p>
	 * @param xOff  <p>The horizontal offset of the cuboid, towards the front face</p>
	 * @param yOff  <p>The vertical offset of the cuboid, towards the top face</p>
	 * @param zOff  <p>The horizontal of the cuboid, towards the side face</p>
	 * @return <p>Returns generated image.</p>
	 */
	public static BufferedImage generateCuboidImage(Image top, Image front, Image side, int x, int y, int z, int xOff,
			int yOff, int zOff) {
		BufferedImage out = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) out.getGraphics();

		int xTot = x + xOff;
		int yTot = y + yOff;
		int zTot = z + zOff;

		// Top face
		Point2D t2 = new Point2D.Double(16, 16 - yTot), t3 = new Point2D.Double(0, 24 - yTot), t4 = new Point2D.Double(
				16, 32 - yTot), t1 = new Point2D.Double(32, 24 - yTot);
		g2d.drawImage(ImageTransformUtil.computeImage(
				brighten(eraseExceptRect(resizeAndCrop(top, 32), 32 - 2 * zTot, 32 - 2 * xTot, 2 * z, 2 * x)), t4, t1,
				t2, t3), null, null);

		// Front face
		Point2D f1 = new Point2D.Double(16 - xTot, xTot / 2d), f2 = new Point2D.Double(16 - xTot,
				16 + xTot / 2d), f3 = new Point2D.Double(32 - xTot, 24 + xTot / 2d), f4 = new Point2D.Double(32 - xTot,
				8 + xTot / 2d);
		g2d.drawImage(ImageTransformUtil.computeImage(
						eraseExceptRect(resizeAndCrop(front, 32), 2 * zOff, 32 - 2 * yTot, 2 * z, 2 * y), f1, f2, f3, f4), null,
				null);

		// Side face
		Point2D r1 = new Point2D.Double(zTot, 8 + zTot / 2d), r2 = new Point2D.Double(zTot,
				24 + zTot / 2d), r3 = new Point2D.Double(16 + zTot, 16 + zTot / 2d), r4 = new Point2D.Double(16 + zTot,
				zTot / 2d);
		g2d.drawImage(ImageTransformUtil.computeImage(
				darken(eraseExceptRect(resizeAndCrop(side, 32), 32 - 2 * xTot, 32 - 2 * yTot, 2 * x, 2 * y)), r1, r2,
				r3, r4), null, null);

		g2d.dispose();
		return out;
	}

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

	private static BufferedImage resizeImage(BufferedImage originalImage, int w, int h) {
		// Optimization to not resize images that are already the correct size
		if (originalImage.getWidth() == w && originalImage.getHeight() == h)
			return originalImage;

		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		g.dispose();
		return resizedImage;
	}

	private static BufferedImage resizeImageWithAA(BufferedImage originalImage, int w, int h) {
		// Optimization to not resize images that are already the correct size
		if (originalImage.getWidth() == w && originalImage.getHeight() == h)
			return originalImage;

		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(originalImage, 0, 0, w, h, null);
		g.dispose();
		return resizedImage;
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

	private static ImageIcon colorize1(ImageIcon icon, Color modifier) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return new ImageIcon(createColorizeOp(modifier).filter(bi, null));
	}

	private static LookupOp createColorizeOp(Color color) {
		short[] alpha = new short[256];
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];

		for (short i = 0; i < 256; i++) {
			alpha[i] = i;
			red[i] = (short) (((short) color.getRed() + i) / 2);
			green[i] = (short) (((short) color.getGreen() + i) / 2);
			blue[i] = (short) (((short) color.getBlue() + i) / 2);
		}

		short[][] data = new short[][] { red, green, blue, alpha };

		LookupTable lookupTable = new ShortLookupTable(0, data);
		return new LookupOp(lookupTable, null);
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

	// Internal math helpers

	private static int nextSign(Random random) {
		return random.nextBoolean() ? 1 : -1;
	}

	private static float limitDeviation(float original, float deviation, int sign) {
		return Math.max(Math.min(original + deviation * sign, 1), 0);
	}

	/**
	 * Merges two images to make a single image
	 *
	 * @param first   <p>The first image to draw on the new image</p>
	 * @param second  <p>The second image to draw on the new image</p>
	 * @param width   <p>The width of the final image</p>
	 * @param height  <p>The height of the final image</p>
	 * @param xFirst  <p>The x position of the first image on the final image</p>
	 * @param yFirst  <p>The y position of the first image on the final image</p>
	 * @param xSecond <p>The x position of the second image on the final image</p>
	 * @param ySecond <p>The y position of the second image on the final image</p>
	 * @return <p>Returns the generated image.</p>
	 */
	public static BufferedImage mergeTwoImages(Image first, Image second, int width, int height, int xFirst, int yFirst,
			int xSecond, int ySecond) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
		graphics.drawImage(first, xFirst, yFirst, null);
		graphics.drawImage(second, xSecond, ySecond, null);
		return bi;
	}

	public static boolean checkIfSameWidth(Image first, Image second) {
		return first.getWidth(null) == second.getWidth(null);
	}

	public static boolean checkIfSameHeight(Image first, Image second) {
		return first.getHeight(null) == second.getHeight(null);
	}

	/**
	 * Checks if two images have the same width and the same height
	 *
	 * @param first  <p>The first image</p>
	 * @param second <p>The second image</p>
	 * @return <p>Returns true if the provided images have the same width and the same height</p>
	 */
	public static boolean checkIfSameSize(Image first, Image second) {
		return checkIfSameWidth(first, second) && checkIfSameHeight(first, second);
	}

	/**
	 * Generates a smooth squircle shape
	 *
	 * @param color    <p>Color of the generated smooth squircle</p>
	 * @param fac      <p>Upscale factor (generates a bigger image before downscaling to get better visual results)</p>
	 * @param radius   <p>The squircle corner radius</p>
	 * @param width    <p>Squircle width</p>
	 * @param height   <p>Squircle height</p>
	 * @param observer <p>Observer used when drawing the image (can be the current swing component)</p>
	 * @return <p>The generated image</p>
	 */
	public static Image generateSquircle(Color color, int fac, int radius, int width, int height,
			ImageObserver observer) {
		BufferedImage sim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = sim.createGraphics();
		g2d.setColor(color);

		RoundRectangle2D.Double squircle = new RoundRectangle2D.Double(0, 0, width, height, radius, radius);

		g2d.setClip(squircle);
		g2d.fillRect(0, 0, width, height);

		g2d.dispose();

		return cropSquircle(sim, fac, radius, width, height, observer);
	}

	/**
	 * Crops the input image in a squircle shape
	 *
	 * @param original <p>The original image</p>
	 * @param fac      <p>Upscale factor (upscales the image before cropping to produce smooth edges after downscaling to the desired size)</p>
	 * @param radius   <p>The squircle corner radius</p>
	 * @param width    <p>Squircle width</p>
	 * @param height   <p>Squircle height</p>
	 * @param observer <p>Observer used when drawing the image (can be the current swing component)</p>
	 * @return <p>The cropped image</p>
	 */
	public static Image cropSquircle(Image original, int fac, int radius, int width, int height,
			ImageObserver observer) {
		BufferedImage sim = new BufferedImage(width * fac, height * fac, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = sim.createGraphics();

		RoundRectangle2D.Double squircle = new RoundRectangle2D.Double(0, 0, width * fac, height * fac, radius * fac,
				radius * fac);
		g2d.setClip(squircle);
		g2d.drawImage(ImageUtils.cover(original, new Dimension(width * fac, height * fac)), 0, 0, observer);

		g2d.dispose();

		return sim.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	/**
	 * Generates a shadow that fits squircle cropped images.
	 *
	 * @param radius          <p>The squircle corner radius</p>
	 * @param shadowRadius    <p>Width of the blur shadow</p>
	 * @param shadowExtension <p>The squircle extension</p>
	 * @param width           <p>Squircle width</p>
	 * @param height          <p>Squircle height</p>
	 * @return <p>Returns the generated shadow.</p>
	 */
	public static Image generateShadow(int radius, int shadowRadius, int shadowExtension, int width, int height) {
		BufferedImage im = new BufferedImage(width + 2 * ((shadowRadius * 2) + shadowExtension),
				height + 2 * ((shadowRadius * 2) + shadowExtension), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = im.createGraphics();

		g2d.setColor(Color.black);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		RoundRectangle2D.Double squircle = new RoundRectangle2D.Double(shadowRadius * 2, shadowRadius * 2,
				width + 2 * shadowExtension, height + 2 * shadowExtension, radius, radius);
		g2d.fill(squircle);

		g2d.dispose();

		Kernel kernel = new Kernel(shadowRadius, shadowRadius, generateGaussianKernel(shadowRadius));
		ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

		return op.filter(im, null).getSubimage(shadowRadius, shadowRadius, width + 2 * (shadowRadius + shadowExtension),
				height + 2 * (shadowRadius + shadowExtension));
	}

	/**
	 * Generates a gaussian kernel compatible with java.awt.image.Kernel objects.
	 *
	 * @param radius <p>The gaussian kernel radius</p>
	 * @return <p>The generated kernel</p>
	 */
	public static float[] generateGaussianKernel(int radius) {
		int shrad = radius * 2 + 1;
		int elements = shrad * shrad;

		float[] data = new float[elements];

		float sigma = shrad / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 1.0f;

		for (int i = 0; i < data.length; i++) {
			int x = i % shrad;
			int y = i / shrad;

			int dx = x - radius;
			int dy = y - radius;

			double distance = Math.sqrt(dx * dx + dy * dy);

			data[i] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[i];
		}

		for (int i = 0; i < data.length; i++)
			data[i] /= total;

		return data;
	}

	public static BaseMultiResolutionImage applyOperationToAllResolutions(BaseMultiResolutionImage image,
			Function<Image, Image> operation) {
		return new BaseMultiResolutionImage(
				image.getResolutionVariants().stream().map(operation).toArray(Image[]::new));
	}

}
