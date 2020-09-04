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

import java.awt.image.BufferedImage;

public class Modification extends Change implements IVisualChange {
	private final BufferedImage after;
	private BufferedImage before;

	public Modification(Canvas canvas, Layer layer) {
		super(canvas, layer);
		this.after = layer.copyImage();
	}

	public void setBefore(BufferedImage before) {
		this.before = before;
	}

	@Override public void apply() {
		int index = canvas.indexOf(layer);
		canvas.get(index).replaceImage(after);
		canvas.getCanvasRenderer().repaint();
	}

	@Override public void revert() {
		int index = canvas.indexOf(layer);
		canvas.get(index).replaceImage(before);
		canvas.getCanvasRenderer().repaint();
	}

	@Override public int sizeOf() {
		return layer.sizeOf();
	}

	@Override public BufferedImage getImage() {
		return after;
	}
}
