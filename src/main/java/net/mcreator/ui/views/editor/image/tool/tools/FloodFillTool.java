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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.Shape;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FloodFillTool extends AbstractModificationTool {

	private int threshold = 30;
	private final JCheckBox isGlobal;

	public FloodFillTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.floodfill"),
				L10N.t("dialog.image_maker.tools.types.floodfill_description"), UIRES.get("img_editor.bucket"), canvas,
				colorSelector, versionManager);

		JSlidingSpinner thresholdSlider = new JSlidingSpinner(L10N.t("dialog.image_maker.tools.types.threshold"),
				threshold, 0, 255, 1);
		thresholdSlider.addChangeListener(e -> threshold = (int) Math.round(thresholdSlider.getValue()));
		isGlobal = new JCheckBox(L10N.t("dialog.image_maker.tools.types.floodfill_global"));

		settingsPanel.add(thresholdSlider);
		settingsPanel.add(isGlobal);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		Selection selection = canvas.getSelection();
		Shape validArea = selection.getLayerMask(layer);

		if (layer.in(e.getX(), e.getY()) && selection.isInside(validArea, e.getX(), e.getY())) {
			layer.setOverlayOpacity(colorSelector.getForegroundColor().getAlpha() / 255.0);
			if (isGlobal.isSelected()) {
				fillGlobal(layer.getRaster(), layer.getOverlay(), e.getX() - layer.getX(), e.getY() - layer.getY(),
						colorSelector.getForegroundColor());
			} else {
				fillArea(layer.getRaster(), layer.getOverlay(), e.getX() - layer.getX(), e.getY() - layer.getY(),
						colorSelector.getForegroundColor());
			}
			return true;
		}
		return false;
	}

	@Override public void mouseDragged(MouseEvent e) {
	}

	private boolean toPaint(int current, int overlay, int old) {
		int oldalpha = ((old >> 24) & 0xff);
		int currentalpha = ((current >> 24) & 0xff);
		int overlayopacity = ((overlay >> 24) & 0xff);
		if (overlayopacity != 0)
			return false;
		else if (oldalpha <= threshold) {
			return Math.abs(oldalpha - currentalpha) <= threshold;
		} else
			return inThreshold(new Color(current, true), new Color(old, true));
	}

	private boolean inThreshold(Color primary, Color secondary) {
		return ((Math.abs(primary.getRed() - secondary.getRed()) <= threshold)) && (
				Math.abs(primary.getGreen() - secondary.getGreen()) <= threshold) && (
				Math.abs(primary.getBlue() - secondary.getBlue()) <= threshold) && (
				Math.abs(primary.getAlpha() - secondary.getAlpha()) <= threshold);
	}

	public void fillArea(BufferedImage image, BufferedImage overlay, int x, int y, Color fill) {
		Graphics2D g2d = overlay.createGraphics();
		Selection selection = canvas.getSelection();
		Shape validArea = selection.getLayerMask(layer);
		g2d.setColor(fill);
		int originalint = image.getRGB(x, y);
		{
			int maxX = image.getWidth() - 1;
			int maxY = image.getHeight() - 1;
			int[][] stack = new int[(maxX + 1) * (maxY + 1)][2];
			int index = 0;

			stack[0][0] = x;
			stack[0][1] = y;

			g2d.fillRect(x, y, 1, 1);

			while (index >= 0) {
				x = stack[index][0];
				y = stack[index][1];
				index--;

				if ((x > 0) && toPaint(image.getRGB(x - 1, y), overlay.getRGB(x - 1, y), originalint)
						&& selection.isInside(validArea, x - 1, y)) {
					g2d.fillRect(x - 1, y, 1, 1);
					index++;
					stack[index][0] = x - 1;
					stack[index][1] = y;
				}

				if ((x < maxX) && toPaint(image.getRGB(x + 1, y), overlay.getRGB(x + 1, y), originalint)
						&& selection.isInside(validArea, x + 1, y)) {
					g2d.fillRect(x + 1, y, 1, 1);
					index++;
					stack[index][0] = x + 1;
					stack[index][1] = y;
				}

				if ((y > 0) && toPaint(image.getRGB(x, y - 1), overlay.getRGB(x, y - 1), originalint)
						&& selection.isInside(validArea, x, y - 1)) {
					g2d.fillRect(x, y - 1, 1, 1);
					index++;
					stack[index][0] = x;
					stack[index][1] = y - 1;
				}

				if ((y < maxY) && toPaint(image.getRGB(x, y + 1), overlay.getRGB(x, y + 1), originalint)
						&& selection.isInside(validArea, x, y + 1)) {
					g2d.fillRect(x, y + 1, 1, 1);
					index++;
					stack[index][0] = x;
					stack[index][1] = y + 1;
				}
			}
		}
		g2d.dispose();
	}

	public void fillGlobal(BufferedImage image, BufferedImage overlay, int x, int y, Color fill) {
		Graphics2D g2d = overlay.createGraphics();
		Selection selection = canvas.getSelection();
		Shape validArea = selection.getLayerMask(layer);
		g2d.setColor(fill);
		int originalint = image.getRGB(x, y);
		// Skip pixels outside the selection
		int minX = selection.hasSurface() ? selection.getLeft() : image.getMinX();
		int maxX = selection.hasSurface() ? selection.getRight() : image.getWidth();
		int minY = selection.hasSurface() ? selection.getTop() : image.getMinY();
		int maxY = selection.hasSurface() ? selection.getBottom() : image.getHeight();

		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				if (toPaint(image.getRGB(i, j), overlay.getRGB(i, j), originalint) && selection.isInside(validArea, i,
						j)) {
					g2d.fillRect(i, j, 1, 1);
				}
			}
		}
		g2d.dispose();
	}
}
