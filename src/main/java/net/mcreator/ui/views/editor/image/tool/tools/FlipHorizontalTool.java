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

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FlipHorizontalTool extends AbstractModificationTool {
	public FlipHorizontalTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.flip_horizontal"),
				L10N.t("dialog.image_maker.tools.types.flip_horizontal_description"),
				UIRES.get("img_editor.fliphorizontal"), canvas, colorSelector, versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		BufferedImage image = layer.getRaster();
		Selection selection = canvas.getSelection();
		// Skip pixels outside the selection
		int minX = selection.hasSurface() ? selection.getLeft() : image.getMinX();
		int maxX = selection.hasSurface() ? selection.getRight() : image.getWidth();
		int minY = selection.hasSurface() ? selection.getTop() : image.getMinY();
		int maxY = selection.hasSurface() ? selection.getBottom() : image.getHeight();
		int width = maxX - minX, height = maxY - minY;

		int temp;
		for (int i = 0; i < width / 2; i++) {
			for (int j = 0; j < height; j++) {
				temp = image.getRGB(maxX - i - 1, minY + j);
				image.setRGB(maxX - i - 1, minY + j, image.getRGB(minX + i, minY + j));
				image.setRGB(minX + i, minY + j, temp);
			}
		}
		return true;
	}

	@Override public void mouseDragged(MouseEvent e) {
	}
}
