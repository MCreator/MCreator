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

package net.mcreator.ui.views.editor.image.layer;

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.util.image.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class Layer {
	private int x, y;
	private String name;
	private boolean visible = true;
	private Canvas canvas;
	private final UUID uuid;

	private BufferedImage raster;
	private BufferedImage overlay;
	private double overlayOpacity = 1;

	private boolean renderingMode = false;

	public Layer(int width, int height, String name) {
		this(width, height, 0, 0, name);
	}

	public Layer(int width, int height, int x, int y, String name) {
		this(width, height, x, y, name, UUID.randomUUID());
	}

	private Layer(int width, int height, int x, int y, String name, UUID uuid) {
		raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.x = x;
		this.y = y;
		this.name = name;
		this.uuid = uuid;
	}

	public Layer(int width, int height, int x, int y, String name, Color color) {
		this(width, height, x, y, name);
		Graphics2D g2d = createGraphics();
		g2d.setColor(color);
		g2d.fillRect(0, 0, width, height);
		g2d.dispose();
	}

	public Layer(int width, int height, int x, int y, String name, Image image) {
		this(width, height, x, y, name);
		Graphics2D g = createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public static Layer toLayer(Image image, String name) {
		Layer l = new Layer(image.getWidth(null), image.getHeight(null), 0, 0, name);
		Graphics2D g = l.getRaster().createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return l;
	}

	@Override public String toString() {
		return name;
	}

	@Override public int hashCode() {
		return uuid.hashCode();
	}

	@Override public boolean equals(Object obj) {
		return obj instanceof Layer && ((Layer) obj).uuid.equals(uuid);
	}

	private UUID getUuid() {
		return uuid;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		canvas.update(this);
	}

	public boolean in(int x, int y) {
		return (x >= getX() && x < (getX() + getWidth())) && (y >= getY() && y < (getY() + getHeight()));
	}

	public void createOverlay() {
		if (overlay == null)
			overlay = new BufferedImage(getWidth(), getHeight(), getType());
	}

	public void resetOverlay() {
		overlay = new BufferedImage(getWidth(), getHeight(), getType());
	}

	private BufferedImage mergeOverlay(boolean apply, boolean eraser) {
		renderingMode = eraser;
		BufferedImage temp;
		Graphics2D g2d;
		if (apply) {
			temp = raster;
			g2d = createGraphics();
		} else {
			temp = new BufferedImage(getWidth(), getHeight(), getType());
			g2d = temp.createGraphics();
			g2d.drawImage(raster, 0, 0, null);
		}
		if (eraser && overlay != null) {
			for (int x = 0; x < getWidth(); x++)
				for (int y = 0; y < getHeight(); y++)
					if ((getRGB(x, y) & 0xFF000000) != 0)
						multiplyPixelAlpha(temp, overlay, x, y);
		} else {
			Composite composite = g2d.getComposite();
			if (overlay != null) {
				AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) overlayOpacity);
				g2d.setComposite(alcom);
				g2d.drawImage(overlay, 0, 0, null);
				g2d.setComposite(composite);
			}
		}
		g2d.dispose();
		if (apply)
			overlay = null;
		return temp;
	}

	public BufferedImage getRaster() {
		return raster;
	}

	public void setRaster(BufferedImage raster) {
		this.raster = raster;
		canvas.getCanvasRenderer().repaint();
	}

	public int getType() {
		return raster.getType();
	}

	public int getWidth() {
		return raster.getWidth();
	}

	public int getHeight() {
		return raster.getHeight();
	}

	public Graphics2D createGraphics() {
		return raster.createGraphics();
	}

	public BufferedImage mergeOverlay(boolean apply) {
		return mergeOverlay(apply, renderingMode);
	}

	public void mergeOverlay() {
		mergeOverlay(true);
	}

	public boolean getRenderingMode() {
		return renderingMode;
	}

	public void setRenderingMode(boolean eraser) {
		renderingMode = eraser;
	}

	public BufferedImage getOverlay() {
		return overlay;
	}

	public double getOverlayOpacity() {
		return overlayOpacity;
	}

	public void setOverlayOpacity(double overlayOpacity) {
		this.overlayOpacity = overlayOpacity;
	}

	public int getRGB(int x, int y) {
		return raster.getRGB(x, y);
	}

	private void multiplyPixelAlpha(BufferedImage image, BufferedImage overlay, int x, int y) {
		int color = image.getRGB(x, y);
		int alpha = (color & 0xFF000000) >>> 24;
		double opacity = (1 - ((overlay.getRGB(x, y) & 0xFF000000) >>> 24) / 255.0 * overlayOpacity);
		image.setRGB(x, y, (color & 0x00FFFFFF) | ((int) (alpha * (opacity)) << 24));
	}

	public Layer copy() {
		Layer layer = new Layer(getWidth(), getHeight(), x, y, name);
		layer.setCanvas(canvas);
		Graphics2D g = layer.createGraphics();
		g.drawImage(raster, 0, 0, null);
		g.dispose();
		return layer;
	}

	public int sizeOf() {
		return getHeight() * getWidth() * 4 * 8;
	}

	public BufferedImage copyImage() {
		BufferedImage copy = new BufferedImage(getWidth(), getHeight(), getType());
		Graphics2D g = copy.createGraphics();
		g.drawImage(raster, 0, 0, null);
		g.dispose();
		return copy;
	}

	public void replaceImage(BufferedImage image) {
		raster = new BufferedImage(image.getWidth(), image.getHeight(), getType());
		Graphics2D g = createGraphics();
		Composite composite = g.getComposite();
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.CLEAR);
		g.setComposite(alcom);
		g.fillRect(0, 0, image.getWidth(), getHeight());
		g.setComposite(composite);
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}

	public void resize(int width, int height, boolean antialiasing) {
		if (antialiasing)
			setRaster(ImageUtils.resizeAA(raster, width, height));
		else
			setRaster(ImageUtils.resize(raster, width, height));
	}
}
