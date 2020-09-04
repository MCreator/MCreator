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

package net.mcreator.ui.views.editor.image.versioning.change;

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;

public class CanvasResize extends Change {
	private final int prevwidth;
	private final int prevheight;
	private final int afterwidth;
	private final int afterheight;

	public CanvasResize(Canvas canvas, Layer layer, int width, int height) {
		super(canvas, layer);
		prevwidth = canvas.getWidth();
		prevheight = canvas.getHeight();
		afterwidth = width;
		afterheight = height;
	}

	@Override public void apply() {
		canvas.setWidth(afterwidth);
		canvas.setHeight(afterheight);
		canvas.getCanvasRenderer().recalculateBounds();
		canvas.getCanvasRenderer().repaint();
	}

	@Override public void revert() {
		canvas.setWidth(prevwidth);
		canvas.setHeight(prevheight);
		canvas.getCanvasRenderer().recalculateBounds();
		canvas.getCanvasRenderer().repaint();
	}
}
