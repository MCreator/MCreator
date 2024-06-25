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

	ArrayListListModel<Color> getColors() {
		return colors;
	}

	public static ColorPalette generate(String name, String... hexColors) {
		ColorPalette palette = new ColorPalette(name);
		for (String hexColor : hexColors) {
			palette.colors.add(Color.decode(hexColor));
		}
		return palette;
	}

	public Image getIcon(int width, int height) {
		BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = icon.createGraphics();

		int colorCount = Math.min(colors.size(), width * height);

		// calculate column/row count
		int columns = (int) Math.ceil(Math.sqrt(colorCount));
		int rows = (int) Math.ceil((float) colorCount / columns);

		// calculate square cell size based on the width
		int cellSize = (int) Math.floor(((float) width) / columns);

		// calculate offset to center the palette
		int offsetY = (height - (cellSize * rows)) / 2;

		// draw tiles
		for (int i = 0; i < colorCount; i++) {
			int x = (i % columns) * cellSize;
			int y = (i / columns) * cellSize;

			g2d.setColor(colors.get(i));
			g2d.fillRect(x, y + offsetY, cellSize, cellSize);
		}

		g2d.dispose();
		return icon;
	}
}
