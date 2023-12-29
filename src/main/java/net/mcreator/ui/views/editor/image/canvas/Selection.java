/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.canvas;

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.layer.Layer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Class used to transfer selection data between objects
 */
public class Selection {
	private final Canvas canvas;
	private final Point first, second;
	private final int handleSize = 10;
	private final Stroke handleStroke = new BasicStroke(handleSize);
	private SelectedBorder editing = SelectedBorder.NONE;
	private boolean editStarted = false;

	public Selection(Canvas canvas) {
		this(canvas, 0, 0, 0, 0);
	}

	public Selection(Canvas canvas, int xFirst, int yFirst, int xSecond, int ySecond) {
		this.canvas = canvas;
		this.first = new Point(xFirst, yFirst);
		this.second = new Point(xSecond, ySecond);
	}

	public SelectedBorder getEditing() {
		return editing;
	}

	/**
	 * Returns the editing state of the selection if no cursor was hovering over it.
	 * This way we have a way to store the selection visibility state without another variable.
	 *
	 * @return editing visibility state of the selection
	 */
	public SelectedBorder getVisibilityState() {
		return editing == SelectedBorder.NONE ? SelectedBorder.NONE : SelectedBorder.ANY;
	}

	public void setEditing(SelectedBorder editing) {
		this.editing = editing;
	}

	public int getHandleSize() {
		return handleSize;
	}

	public Stroke getHandleStroke() {
		return handleStroke;
	}

	public int getLeft() {
		return Math.min(first.x, second.x);
	}

	public Point getLeftPoint() {
		return first.x == getLeft() ? first : second;
	}

	public int getRight() {
		return Math.max(first.x, second.x);
	}

	public Point getRightPoint() {
		return first.x == getRight() ? first : second;
	}

	public int getTop() {
		return Math.min(first.y, second.y);
	}

	public Point getTopPoint() {
		return first.y == getTop() ? first : second;
	}

	public int getBottom() {
		return Math.max(first.y, second.y);
	}

	public Point getBottomPoint() {
		return first.y == getBottom() ? first : second;
	}

	public int getWidth() {
		return Math.abs(first.x - second.x);
	}

	public int getHeight() {
		return Math.abs(first.y - second.y);
	}

	public boolean hasSurface() {
		return getWidth() > 0 && getHeight() > 0;
	}

	public Point getFirst() {
		return first;
	}

	public Point getSecond() {
		return second;
	}

	public void setEditStarted(boolean editStarted) {
		this.editStarted = editStarted;
	}

	public boolean isEditStarted() {
		return editStarted;
	}

	public void clear() {
		first.x = 0;
		first.y = 0;
		second.x = 0;
		second.y = 0;
		setEditing(SelectedBorder.NONE);
	}

	public Shape getLayerMask(Layer layer) {
		if (hasSurface() && getEditing() != SelectedBorder.NONE) {
			int xStart = getLeft() - layer.getX();
			int yStart = getTop() - layer.getY();

			int xStartLimited = Math.max(0, xStart);
			int yStartLimited = Math.max(0, yStart);

			int widthLimited = Math.min(layer.getWidth() - xStartLimited, getWidth());
			int heightLimited = Math.min(layer.getHeight() - yStartLimited, getHeight());

			return new Rectangle2D.Float(xStartLimited, yStartLimited, widthLimited, heightLimited);
		}
		return null;
	}

	public boolean isInside(Shape shape, int x, int y) {
		return shape == null || shape.contains(x, y);
	}

	public BufferedImage cropCanvas(BufferedImage canvasRender) {
		return cropAndExtend(canvasRender, 0, 0);
	}

	public BufferedImage cropLayer(BufferedImage layer, int xOffset, int yOffset) {
		return cropAndExtend(layer, xOffset, yOffset);
	}

	/**
	 * Crops the image to the selection and extends it to the selection size if the
	 * selection extends out of the image borders.
	 *
	 * @param image   image to crop
	 * @param xOffset x offset of the image (the layer's offset)
	 * @param yOffset y offset of the image (the layer's offset)
	 * @return cropped image
	 */
	private BufferedImage cropAndExtend(BufferedImage image, int xOffset, int yOffset) {
		if (hasSurface() && getEditing() != SelectedBorder.NONE) {
			BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = newImage.createGraphics();
			int xImage = getLeft() - xOffset;
			int yImage = getTop() - yOffset;

			int xLimited = Math.max(0, xImage);
			int yLimited = Math.max(0, yImage);

			int xLimOffset = Math.min(0, xImage);
			int yLimOffset = Math.min(0, yImage);

			int widthLimited = Math.min(image.getWidth() - xLimited, getWidth());
			int heightLimited = Math.min(image.getHeight() - yLimited, getHeight());

			if (xLimited >= image.getWidth() || yLimited >= image.getHeight() || widthLimited <= 0
					|| heightLimited <= 0) {
				g2d.dispose();
				return newImage;
			}

			BufferedImage croppedImage = image.getSubimage(xLimited, yLimited, widthLimited, heightLimited);

			g2d.drawImage(croppedImage, -xLimOffset, -yLimOffset, null);
			g2d.dispose();
			return newImage;
		}
		return image;
	}

	/**
	 * Draws the selection handles depending on the current state of the selection.
	 *
	 * @param g2d Graphics2D object to draw on
	 */
	public void drawHandles(Graphics2D g2d) {
		if (!isEditStarted()) {
			// Save the previous stroke to avoid unwanted changes
			Stroke prevStroke = g2d.getStroke();

			Color baseColor = Theme.current().getForegroundColor();

			Color strokeColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120);
			Color strokeColorHighlighted = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
					100);

			g2d.setPaint(strokeColor);

			int handleSize = getHandleSize();

			double zoom = canvas.getCanvasRenderer().getZoom();
			int x_left = (int) Math.round(getLeft() * zoom);
			int y_top = (int) Math.round(getTop() * zoom);
			int x_right = (int) Math.round(getRight() * zoom);
			int y_bottom = (int) Math.round(getBottom() * zoom);

			// Render the corners
			if (cornersVisible()) {
				// Add highlight to the selected corner
				switch (editing) {
				case TOP_LEFT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.fillRect(x_left - handleSize / 2, y_top - handleSize / 2, handleSize, handleSize);
					g2d.setPaint(strokeColor);
					break;
				}
				case BOTTOM_LEFT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.fillRect(x_left - handleSize / 2, y_bottom - handleSize / 2, handleSize, handleSize);
					g2d.setPaint(strokeColor);
					break;
				}
				case TOP_RIGHT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.fillRect(x_right - handleSize / 2, y_top - handleSize / 2, handleSize, handleSize);
					g2d.setPaint(strokeColor);
					break;
				}
				case BOTTOM_RIGHT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.fillRect(x_right - handleSize / 2, y_bottom - handleSize / 2, handleSize, handleSize);
					g2d.setPaint(strokeColor);
					break;
				}
				}
			}

			g2d.setStroke(getHandleStroke());

			if (horizontalHandlesVisible()) {
				// Add highlight to the selected horizontal handle
				switch (editing) {
				case TOP: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.drawLine(x_left + 2 * handleSize, y_top, x_right - 2 * handleSize, y_top);
					g2d.setPaint(strokeColor);
					break;
				}
				case BOTTOM: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.drawLine(x_left + 2 * handleSize, y_bottom, x_right - 2 * handleSize, y_bottom);
					g2d.setPaint(strokeColor);
					break;
				}
				}
			}

			if (verticalHandlesVisible()) {
				// Add highlight to the selected vertical handle
				switch (editing) {
				case LEFT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.drawLine(x_left, y_top + 2 * handleSize, x_left, y_bottom - 2 * handleSize);
					break;
				}
				case RIGHT: {
					g2d.setPaint(strokeColorHighlighted);
					g2d.drawLine(x_right, y_top + 2 * handleSize, x_right, y_bottom - 2 * handleSize);
					break;
				}
				}
			}

			// Restore the previous stroke
			g2d.setStroke(prevStroke);
		}
	}

	public boolean verticalHandlesVisible() {
		double zoom = canvas.getCanvasRenderer().getZoom();
		int height = (int) Math.round(getHeight() * zoom);
		return height > 4 * handleSize;
	}

	public boolean horizontalHandlesVisible() {
		double zoom = canvas.getCanvasRenderer().getZoom();
		int width = (int) Math.round(getWidth() * zoom);
		return width > 4 * handleSize;
	}

	public boolean cornersVisible() {
		double zoom = canvas.getCanvasRenderer().getZoom();
		int height = (int) Math.round(getHeight() * zoom);
		int width = (int) Math.round(getWidth() * zoom);
		return height > handleSize && width > handleSize;
	}

	public SelectedBorder checkHandles(int x, int y) {
		SelectedBorder detected = SelectedBorder.ANY;

		if (editing != SelectedBorder.NONE) {
			double zoom = canvas.getCanvasRenderer().getZoom();
			int x_left = (int) Math.round(getLeft() * zoom);
			int y_top = (int) Math.round(getTop() * zoom);
			int x_right = (int) Math.round(getRight() * zoom);
			int y_bottom = (int) Math.round(getBottom() * zoom);

			// Check whether we are hovering over any corners
			if (cornersVisible()) {
				if (x >= x_left - handleSize / 2 && x <= x_left + handleSize / 2 && y >= y_top - handleSize / 2
						&& y <= y_top + handleSize / 2) {
					detected = SelectedBorder.TOP_LEFT;
				} else if (x >= x_left - handleSize / 2 && x <= x_left + handleSize / 2
						&& y >= y_bottom - handleSize / 2 && y <= y_bottom + handleSize / 2) {
					detected = SelectedBorder.BOTTOM_LEFT;
				} else if (x >= x_right - handleSize / 2 && x <= x_right + handleSize / 2 && y >= y_top - handleSize / 2
						&& y <= y_top + handleSize / 2) {
					detected = SelectedBorder.TOP_RIGHT;
				} else if (x >= x_right - handleSize / 2 && x <= x_right + handleSize / 2
						&& y >= y_bottom - handleSize / 2 && y <= y_bottom + handleSize / 2) {
					detected = SelectedBorder.BOTTOM_RIGHT;
				}
			}

			// Check whether we are hovering over any vertical handles
			if (detected == SelectedBorder.ANY && verticalHandlesVisible()) {
				if (x >= x_left + 1.5 * handleSize && x <= x_right - 1.5 * handleSize && y >= y_top - handleSize / 2
						&& y <= y_top + handleSize / 2) {
					detected = SelectedBorder.TOP;
				} else if (x >= x_left + 1.5 * handleSize && x <= x_right - 1.5 * handleSize
						&& y >= y_bottom - handleSize / 2 && y <= y_bottom + handleSize / 2) {
					detected = SelectedBorder.BOTTOM;
				}
			}

			// Check whether we are hovering over any horizontal handles
			if (detected == SelectedBorder.ANY && horizontalHandlesVisible()) {
				if (x >= x_left - handleSize / 2 && x <= x_left + handleSize / 2 && y >= y_top + 1.5 * handleSize
						&& y <= y_bottom - 1.5 * handleSize) {
					detected = SelectedBorder.LEFT;
				} else if (x >= x_right - handleSize / 2 && x <= x_right + handleSize / 2
						&& y >= y_top + 1.5 * handleSize && y <= y_bottom - 1.5 * handleSize) {
					detected = SelectedBorder.RIGHT;
				}
			}
		}

		// Update to the detected state
		editing = detected;
		return editing;
	}
}