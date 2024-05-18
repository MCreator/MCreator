/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.color.palette;

import net.mcreator.util.ArrayListListModel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorPalette {

	private String name;

	private final ArrayListListModel<Color> colors = new ArrayListListModel<>();

	public ColorPalette(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayListListModel<Color> getColors() {
		return colors;
	}

	public Image getIcon(int width, int height) {
		BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = icon.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		int colorCount = Math.min(colors.size(), width * height);

		// calculate column/row count
		int columns = (int) Math.ceil(Math.sqrt(colorCount));
		int rows = (int) Math.ceil((float) colorCount / columns);

		// calculate square cell size based on the width
		float cellSize = ((float) width) / columns;

		// calculate offset to center the palette
		int offsetY = (int) ((height - (cellSize * rows)) / 2);

		// draw tiles
		for (int i = 0; i < colorCount; i++) {
			int xi = i % columns;
			int yi = i / columns;

			float x = xi * cellSize;
			float y = yi * cellSize;

			float sizeX = cellSize * (xi + 1) - x;
			float sizeY = cellSize * (yi + 1) - y;

			g2d.setColor(colors.get(i));
			g2d.fillRect(Math.round(x), Math.round(y + offsetY), Math.round(sizeX), Math.round(sizeY));
		}

		g2d.dispose();
		return icon;
	}
}
