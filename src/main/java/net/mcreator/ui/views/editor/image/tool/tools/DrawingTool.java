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
import net.mcreator.ui.init.L10N;
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

public class DrawingTool extends AbstractDrawingTool {

	private int size = 1;
	private Shape shape = Shape.SQUARE;

	public DrawingTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, String name,
			String description, ImageIcon icon, VersionManager versionManager) {
		super(name, description, icon, canvas, colorSelector, versionManager);
		setLayerPanel(layerPanel);

		JComboBox<Shape> shapeBox = new JComboBox<>(Shape.values());
		shapeBox.setSelectedIndex(0);
		JTitledComponentWrapper titledShape = new JTitledComponentWrapper(
				L10N.t("dialog.image_maker.tools.types.shape"), shapeBox);
		shapeBox.addActionListener(e -> shape = (Shape) shapeBox.getSelectedItem());

		JSlidingSpinner sizeSlider = new JSlidingSpinner(L10N.t("dialog.image_maker.tools.types.drawing_size"), 1, 1,
				100, 1);
		sizeSlider.addChangeListener(e -> size = (int) Math.round(sizeSlider.getValue()));

		aliasing = new JCheckBox(L10N.t("dialog.image_maker.tools.types.smooth_edge"));
		connect = new JCheckBox(L10N.t("dialog.image_maker.tools.types.drawing_connect_points"));
		connect.setSelected(true);

		settingsPanel.add(titledShape);
		settingsPanel.add(sizeSlider);
		settingsPanel.add(aliasing);
		settingsPanel.add(connect);
	}

	@Override protected void preProcess(ZoomedMouseEvent e) {
		layer.setOverlayOpacity(colorSelector.getForegroundColor().getAlpha() / 255.0);
		canvas.updateCustomPreview(e, shape, size);
	}

	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		first = true;
	}

	@Override protected Dimension getShapeDimension() {
		return new Dimension(size, size);
	}

	@Override protected void doDrawing(Graphics2D g, int x, int y, Dimension d) {
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
