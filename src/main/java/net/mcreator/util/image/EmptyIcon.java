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

package net.mcreator.util.image;

import javax.swing.*;
import java.awt.*;

public final class EmptyIcon implements Icon {

	private final int width;
	private final int height;

	public EmptyIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	}

	public static final class ImageIcon extends javax.swing.ImageIcon {

		public ImageIcon(int width, int height) {
			super(ImageUtils.emptyImageWithSize(width, height, null));
		}

	}

	public static final class ColorIcon extends javax.swing.ImageIcon {

		public ColorIcon(int width, int height, Color color) {
			super(ImageUtils.emptyImageWithSize(width, height, color));
		}

	}

}
