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

package net.mcreator.ui.views.editor.image.tool.tools;

import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.tool.component.JTitledComponentWrapper;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class DrawingTool extends AbstractModificationTool {

	private double opacity = 1;
	private int size = 1;
	private Shape shape = Shape.SQUARE;

	private Point prevPoint = null;

	private final JCheckBox aliasing;
	private final JCheckBox connect;

	private boolean first = true;

	public DrawingTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, String name,
			String description, ImageIcon icon, VersionManager versionManager) {
		super(name, description, icon, canvas, colorSelector, versionManager);
		setLayerPanel(layerPanel);
		JSlidingSpinner opacitySlider = new JSlidingSpinner("Opacity:");
		opacitySlider.addChangeListener(e -> opacity = opacitySlider.getValue() / 100.0);

		JComboBox<Shape> shapeBox = new JComboBox<>(Shape.values());
		shapeBox.setSelectedIndex(0);
		JTitledComponentWrapper titledShape = new JTitledComponentWrapper("Shape:", shapeBox);
		shapeBox.addActionListener(e -> {
			shape = (Shape) shapeBox.getSelectedItem();
		});

		JSlidingSpinner sizeSlider = new JSlidingSpinner("Size:", 1, 1, 100, 1);
		sizeSlider.addChangeListener(e -> size = (int) Math.round(sizeSlider.getValue()));

		aliasing = new JCheckBox("Smooth edge");
		connect = new JCheckBox("Connect points");
		connect.setSelected(true);

		settingsPanel.add(opacitySlider);
		settingsPanel.add(titledShape);
		settingsPanel.add(sizeSlider);
		settingsPanel.add(aliasing);
		settingsPanel.add(connect);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		layer.setOverlayOpacity(opacity);
		canvas.updateCustomPreview(e, shape, size);
		if (layer.in(e.getX(), e.getY())) {
			int sx = e.getX() - layer.getX(), sy = e.getY() - layer.getY();
			Graphics2D graphics2D = layer.getOverlay().createGraphics();
			if (aliasing.isSelected())
				graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			draw(graphics2D, sx, sy, e.getRawX(), e.getRawY(), e.getZoom());
			if (((connect.isSelected() && !first) || e.isShiftDown()) && prevPoint != null) {
				int minx = Math.min(sx, prevPoint.x);
				int maxx = Math.max(sx, prevPoint.x);
				int miny = Math.min(sy, prevPoint.y);
				int maxy = Math.max(sy, prevPoint.y);

				if (sx == prevPoint.getX())
					for (int y = miny + 1; y < maxy; y++)
						draw(graphics2D, sx, y, e.getRawX(), y * e.getZoom(), e.getZoom());
				else if (sy == prevPoint.getY())
					for (int x = minx + 1; x < maxx; x++)
						draw(graphics2D, x, sy, x * e.getZoom(), e.getRawY(), e.getZoom());
				else {
					double part = 1;
					double distance = Point2D.distance(prevPoint.x, prevPoint.y, sx, sy);
					for (double t = 0; t < 1; t += 1 / (distance * part)) {
						int x = (int) Math.round((1 - t) * prevPoint.x + t * sx);
						int y = (int) Math.round((1 - t) * prevPoint.y + t * sy);
						draw(graphics2D, x, y, x * e.getZoom(), y * e.getZoom(), e.getZoom());
					}
				}
			}
			first = false;
			prevPoint = new Point(e.getX() - layer.getX(), e.getY() - layer.getY());
			graphics2D.dispose();
			canvas.getCanvasRenderer().repaint();
			return true;
		}
		return false;
	}

	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		first = true;
	}

	private void draw(Graphics2D g, int tx, int ty, double rx, double ry, double zoom) {
		int x, y;
		if (size % 2 == 1) {
			x = tx - size / 2;
			y = ty - size / 2;
		} else {
			x = Math.round((int) (rx / zoom + 0.5)) - size / 2;
			y = Math.round((int) (ry / zoom + 0.5)) - size / 2;
		}
		g.setColor(colorSelector.getForegroundColor());
		switch (shape) {
		case CIRCLE:
			Ellipse2D.Double shape;
			if (aliasing.isSelected())
				shape = new Ellipse2D.Double(x - 0.5, y - 0.5, size + 1, size + 1);
			else
				shape = new Ellipse2D.Double(x - 0.5, y - 0.5, size, size);
			g.fill(shape);
			break;
		case SQUARE:
			g.fillRect(x, y, size, size);
			break;
		case RING:
			g.drawOval(x, y, size - 1, size - 1);
			break;
		case FRAME:
			g.drawRect(x, y, size - 1, size - 1);
			break;
		}
	}

	@Override public void mouseEntered(MouseEvent e) {
		canvas.enablePreview(true);
		super.mouseEntered(e);
	}

	@Override public void mouseExited(MouseEvent e) {
		canvas.enablePreview(false);
		canvas.getCanvasRenderer().repaint();
		super.mouseExited(e);
	}

	@Override public void toolEnabled(ToolActivationEvent e) {
		canvas.enablePreview(true);
		super.toolEnabled(e);
	}

	@Override public void toolDisabled(ToolActivationEvent e) {
		canvas.enablePreview(false);
		canvas.getCanvasRenderer().repaint();
		super.toolDisabled(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
		canvas.updateCustomPreview(e, shape, size);
	}
}
