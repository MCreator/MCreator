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

import java.awt.*;

public class Relocation extends MultiStateChange {
	private final Point before;
	private Point after;

	public Relocation(Canvas canvas, Layer layer) {
		super(canvas, layer);
		this.before = new Point(layer.getX(), layer.getY());
	}

	@Override public void setAfter(Layer after) {
		this.after = new Point(after.getX(), after.getY());
	}

	@Override public void apply() {
		layer.setX(after.x);
		layer.setY(after.y);
		canvas.getImageMakerView().getCanvasRenderer().repaint();
	}

	@Override public void revert() {
		layer.setX(before.x);
		layer.setY(before.y);
		canvas.getImageMakerView().getCanvasRenderer().repaint();
	}
}
