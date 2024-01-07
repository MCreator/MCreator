/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.Shape;
import java.awt.*;
import java.awt.event.MouseEvent;

public class LineTool extends AbstractModificationTool {

	private int size = 1;

	private final JCheckBox aliasing;
	private Point firstPoint = null;

	public LineTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.linetool"),
				L10N.t("dialog.image_maker.tools.types.linetool_description"), UIRES.get("img_editor.line"), canvas,
				colorSelector, versionManager);
		setLayerPanel(layerPanel);

		JSlidingSpinner sizeSlider = new JSlidingSpinner(L10N.t("dialog.image_maker.tools.types.drawing_size"), 1, 1,
				100, 1);
		sizeSlider.addChangeListener(e -> size = (int) Math.round(sizeSlider.getValue()));

		aliasing = new JCheckBox(L10N.t("dialog.image_maker.tools.types.smooth_edge"));

		settingsPanel.add(sizeSlider);
		settingsPanel.add(aliasing);
	}

	@Override public void mouseReleased(MouseEvent e) {
		firstPoint = null;
		super.mouseReleased(e);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		layer.resetOverlay();
		layer.setOverlayOpacity(colorSelector.getForegroundColor().getAlpha() / 255.0);

		Selection selection = canvas.getSelection();
		Shape validArea = selection.getLayerMask(layer);

		Graphics2D g = layer.getOverlay().createGraphics();
		Shape previousShape = g.getClip();

		if (validArea != null)
			g.setClip(validArea);

		g.setColor(colorSelector.getForegroundColor());
		if (aliasing.isSelected())
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (firstPoint == null)
			firstPoint = new Point(e.getX() - layer.getX(), e.getY() - layer.getY());
		g.setStroke(new BasicStroke(size));
		g.drawLine(firstPoint.x, firstPoint.y, e.getX() - layer.getX(), e.getY() - layer.getY());

		if (validArea != null)
			g.setClip(previousShape);
		g.dispose();

		canvas.getCanvasRenderer().repaint();
		return true;
	}
}
