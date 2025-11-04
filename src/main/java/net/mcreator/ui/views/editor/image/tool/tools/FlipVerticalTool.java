/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FlipVerticalTool extends AbstractModificationTool {
	public FlipVerticalTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.flip_vertical"),
				L10N.t("dialog.image_maker.tools.types.flip_vertical_description"),
				UIRES.get("img_editor.flipvertical"), canvas, colorSelector, versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		flipImage(layer.getRaster());
		return true;
	}

	@Override public void mouseDragged(MouseEvent e) {
	}

	public void flipImage(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		Selection selection = canvas.getSelection();
		// Skip pixels outside the selection
		int minX = selection.hasSurface() ? selection.getLeft() : image.getMinX();
		int maxX = selection.hasSurface() ? selection.getRight() : image.getWidth();
		int minY = selection.hasSurface() ? selection.getTop() : image.getMinY();
		int maxY = selection.hasSurface() ? selection.getBottom() : image.getHeight();
		int width = maxX - minX, height = maxY - minY;

		BufferedImage mirrored = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				mirrored.setRGB(i, j, image.getRGB(minX + i, maxY - j - 1));
			}
		}
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(minX, minY, width, height);
		g2d.drawImage(mirrored, minX, minY, width, height, null);
		g2d.dispose();
	}
}
