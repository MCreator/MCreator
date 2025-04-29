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
import net.mcreator.ui.views.editor.image.canvas.SelectedBorder;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;
import net.mcreator.util.image.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class Layer {

	/*
	 * Transient references and fields
	 */
	private transient final UUID uuid;

	// Canvas reference (needs to be set right after creation)
	private transient Canvas canvas;

	// Rendering mode for eraser function
	private transient boolean renderingMode = false;

	// Change preview overlay
	private transient BufferedImage overlay = null;
	private transient double overlayOpacity = 1;

	// Image data
	private transient BufferedImage raster;

	/*
	 * Saved layer properties
	 */
	// Layer properties
	private String name;
	private int x, y;
	private boolean visible = true;

	// If the layer is pasted and not yet solidified/merged down (adds the floating effect)
	private boolean isPasted = false;

	// Only used by serialization
	private Layer() {
		this.uuid = UUID.randomUUID();
	}

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

	public Layer(String name, Image image) {
		this(image.getWidth(null), image.getHeight(null), 0, 0, name);
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

	public boolean isPasted() {
		return isPasted;
	}

	public void setPasted(boolean isPasted) {
		this.isPasted = isPasted;
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
				AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) overlayOpacity);
				g2d.setComposite(alphaComposite);
				g2d.drawImage(overlay, 0, 0, null);
				g2d.setComposite(composite);
			}
		}
		g2d.dispose();
		if (apply)
			overlay = null;
		return temp;
	}

	public void mergeOnTop(Layer topLayer) {
		Graphics2D g2d = createGraphics();
		g2d.drawImage(topLayer.getRaster(), topLayer.x - x, topLayer.y - y, null);
		g2d.dispose();
	}

	public void clear() {
		Graphics2D g2d = createGraphics();
		Composite composite = g2d.getComposite();
		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setComposite(composite);
		g2d.dispose();
	}

	public void clearSelection() {
		Selection selection = canvas.getSelection();
		if (selection.hasSurface() && selection.getEditing() != SelectedBorder.NONE) {
			Graphics2D g2d = createGraphics();
			Composite composite = g2d.getComposite();
			g2d.setComposite(AlphaComposite.Clear);
			int x = selection.getLeft() - getX();
			int y = selection.getTop() - getY();
			g2d.fillRect(x, y, selection.getWidth(), selection.getHeight());
			g2d.setComposite(composite);
			g2d.dispose();
		} else {
			clear();
		}
	}

	public BufferedImage getRaster() {
		return raster;
	}

	public void setRaster(BufferedImage raster) {
		this.raster = raster;
		// Canvas may be null if this is called during deserialization
		if (canvas != null) {
			canvas.getImageMakerView().getCanvasRenderer().repaint();
		}
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
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.CLEAR);
		g.setComposite(alphaComposite);
		g.fillRect(0, 0, image.getWidth(), getHeight());
		g.setComposite(composite);
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}

	public void resize(int width, int height, boolean antialiasing) {
		if (antialiasing)
			setRaster(ImageUtils.toBufferedImage(ImageUtils.resizeAA(raster, width, height)));
		else
			setRaster(ImageUtils.toBufferedImage(ImageUtils.resize(raster, width, height)));
	}

	public void deleteSelection() {
		Layer selected = canvas.getImageMakerView().getLayerPanel().selected();
		selected.clearSelection();
		canvas.getImageMakerView().getVersionManager().addRevision(new Modification(canvas, selected));
	}
}
