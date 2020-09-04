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

public class Rename extends MultiStateChange {
	private String newName;
	private final String prevName;

	public Rename(Canvas canvas, Layer layer) {
		super(canvas, layer);
		this.prevName = layer.getName();
	}

	@Override public void setAfter(Layer after) {
		this.newName = layer.getName();
	}

	@Override public void apply() {
		layer.setName(newName);
	}

	@Override public void revert() {
		layer.setName(prevName);
	}
}
