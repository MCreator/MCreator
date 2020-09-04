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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.tool.component.JTitledComponentWrapper;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ShapeTool extends AbstractModificationTool {

	private double opacity = 1;
	private Shape shape = Shape.SQUARE;
	private final JCheckBox aliasing;
	private Point firstPoint = null;

	public ShapeTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, VersionManager versionManager) {
		super("Shape", "Shape creation tool", UIRES.get("img_editor.shape"), canvas, colorSelector, versionManager);
		setLayerPanel(layerPanel);
		JSlidingSpinner opacitySlider = new JSlidingSpinner("Opacity:");
		opacitySlider.addChangeListener(e -> opacity = opacitySlider.getValue() / 100.0);

		JComboBox<Shape> shapeBox = new JComboBox<>(Shape.values());
		shapeBox.setSelectedIndex(0);
		JTitledComponentWrapper titledShape = new JTitledComponentWrapper("Shape:", shapeBox);
		shapeBox.addActionListener(e -> {
			shape = (Shape) shapeBox.getSelectedItem();
		});

		aliasing = new JCheckBox("Smooth edge");

		settingsPanel.add(opacitySlider);
		settingsPanel.add(titledShape);
		settingsPanel.add(aliasing);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		layer.resetOverlay();
		layer.setOverlayOpacity(opacity);
		if (layer.in(e.getX(), e.getY())) {
			Graphics2D graphics2D = layer.getOverlay().createGraphics();
			graphics2D.setColor(colorSelector.getForegroundColor());
			if (aliasing.isSelected())
				graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (firstPoint == null)
				firstPoint = new Point(e.getX() - layer.getX(), e.getY() - layer.getY());
			draw(graphics2D, firstPoint.x, firstPoint.y, e.getX() - layer.getX(), e.getY() - layer.getY());
			graphics2D.dispose();
			canvas.getCanvasRenderer().repaint();
			return true;
		}
		return false;
	}

	@Override public void mouseReleased(MouseEvent e) {
		firstPoint = null;
		super.mouseReleased(e);
	}

	private void draw(Graphics2D g, int x0, int y0, int x1, int y1) {
		int sizex = x1 - x0, sizey = y1 - y0;
		if (sizex < 0) {
			x0 += sizex;
			sizex = -sizex;
		}
		if (sizey < 0) {
			y0 += sizey;
			sizey = -sizey;
		}
		switch (shape) {
		case CIRCLE:
			g.fillOval(x0, y0, sizex, sizey);
			break;
		case SQUARE:
			g.fillRect(x0, y0, sizex, sizey);
			break;
		case RING:
			g.drawOval(x0, y0, sizex, sizey);
			break;
		case FRAME:
			g.drawRect(x0, y0, sizex, sizey);
			break;
		}
	}
}
