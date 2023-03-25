/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.layer;

import java.awt.*;

public class PastedLayer extends Layer {
	public PastedLayer(int width, int height, String name) {
		super(width, height, name);
	}

	public PastedLayer(int width, int height, int x, int y, String name) {
		super(width, height, x, y, name);
	}

	public PastedLayer(int width, int height, int x, int y, String name, Color color) {
		super(width, height, x, y, name, color);
	}

	public PastedLayer(int width, int height, int x, int y, String name, Image image) {
		super(width, height, x, y, name, image);
	}

	public PastedLayer(String name, Image image) {
		super(name, image);
	}
}
