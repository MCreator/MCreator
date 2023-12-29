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
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.Shape;
import java.awt.*;
import java.awt.geom.Point2D;

public abstract class AbstractDrawingTool extends AbstractModificationTool {

	protected Point prevPoint = null;

	protected boolean first = true;
	protected JCheckBox aliasing;
	protected JCheckBox connect;

	public AbstractDrawingTool(String name, String description, ImageIcon icon, Canvas canvas,
			ColorSelector colorSelector, VersionManager versionManager) {
		super(name, description, icon, canvas, colorSelector, versionManager);
	}

	protected abstract void preProcess(ZoomedMouseEvent e);

	@Override public boolean process(ZoomedMouseEvent e) {
		preProcess(e);
		int sx = e.getX() - layer.getX(), sy = e.getY() - layer.getY();

		Selection selection = canvas.getSelection();
		Shape validArea = selection.getLayerMask(layer);

		Graphics2D graphics2D = layer.getOverlay().createGraphics();
		Shape previousShape = graphics2D.getClip();

		if (validArea != null)
			graphics2D.setClip(validArea);

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

		if (validArea != null)
			graphics2D.setClip(previousShape);

		graphics2D.dispose();

		canvas.getCanvasRenderer().repaint();
		return true;
	}

	protected abstract Dimension getShapeDimension();

	protected abstract void doDrawing(Graphics2D g, int x, int y, Dimension d);

	private void draw(Graphics2D g, int tx, int ty, double rx, double ry, double zoom) {
		int x, y;
		Dimension d = getShapeDimension();

		if (d.width % 2 == 1)
			x = tx - d.width / 2;
		else
			x = (int) (rx / zoom + 0.5) - d.width / 2;

		if (d.height % 2 == 1)
			y = ty - d.height / 2;
		else
			y = (int) (ry / zoom + 0.5) - d.height / 2;

		doDrawing(g, x, y, d);
	}
}
