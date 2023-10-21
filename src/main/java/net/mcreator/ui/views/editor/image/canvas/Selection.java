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

import javax.swing.*;
import java.awt.*;

/**
 * Class used to transfer selection data between objects
 */
public class Selection {
	private final Canvas canvas;
	private Point first, second;

	private boolean active = false;
	private SelectedBorder editing = SelectedBorder.NONE;
	private int handleSize = 10;

	private Stroke handleStroke = new BasicStroke(handleSize);

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

	public void setEditing(SelectedBorder editing) {
		this.editing = editing;
	}

	public int getHandleSize() {
		return handleSize;
	}

	public Stroke getHandleStroke() {
		return handleStroke;
	}

	public void setActive(boolean active) {
		if (!active)
			this.editing = SelectedBorder.NONE;
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public int getLeft() {
		return Math.min(first.x, second.x);
	}

	public int getRight() {
		return Math.max(first.x, second.x);
	}

	public int getTop() {
		return Math.min(first.y, second.y);
	}

	public int getBottom() {
		return Math.max(first.y, second.y);
	}

	public int getWidth() {
		return Math.abs(first.x - second.x);
	}

	public int getHeight() {
		return Math.abs(first.y - second.y);
	}

	public Point getFirst() {
		return first;
	}

	public Point getSecond() {
		return second;
	}

	/**
	 * Draws the selection handles depending on the current state of the selection.
	 *
	 * @param g2d Graphics2D object to draw on
	 */
	public void drawHandles(Graphics2D g2d) {
		Color baseColor = (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR");

		Color strokeColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120);
		Color strokeColorHighlighted = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 100);

		g2d.setPaint(strokeColor);

		int handleSize = getHandleSize();

		double zoom = canvas.getCanvasRenderer().getZoom();
		int x_left = (int) Math.round(getLeft() * zoom);
		int y_top = (int) Math.round(getTop() * zoom);
		int x_right = (int) Math.round(getRight() * zoom);
		int y_bottom = (int) Math.round(getBottom() * zoom);

		// Render the corners
		if (cornersVisible()) {
			// Top left
			g2d.fillRect(x_left - handleSize / 2, y_top - handleSize / 2, handleSize, handleSize);

			// Bottom left
			g2d.fillRect(x_left - handleSize / 2, y_bottom - handleSize / 2, handleSize, handleSize);

			// Top right
			g2d.fillRect(x_right - handleSize / 2, y_top - handleSize / 2, handleSize, handleSize);

			// Bottom right
			g2d.fillRect(x_right - handleSize / 2, y_bottom - handleSize / 2, handleSize, handleSize);

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
			// Top
			g2d.drawLine(x_left + 2 * handleSize, y_top, x_right - 2 * handleSize, y_top);

			// Bottom
			g2d.drawLine(x_left + 2 * handleSize, y_bottom, x_right - 2 * handleSize, y_bottom);

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
			// Left
			g2d.drawLine(x_left, y_top + 2 * handleSize, x_left, y_bottom - 2 * handleSize);

			// Right
			g2d.drawLine(x_right, y_top + 2 * handleSize, x_right, y_bottom - 2 * handleSize);

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

	public SelectedBorder checkEditing(int x, int y) {
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