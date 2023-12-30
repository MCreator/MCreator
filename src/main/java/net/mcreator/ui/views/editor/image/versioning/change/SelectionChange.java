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

package net.mcreator.ui.views.editor.image.versioning.change;

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.SelectedBorder;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.layer.Layer;

import java.awt.*;

public class SelectionChange extends Change {
	private final Selection selection;

	// State before the changes
	private final Point firstBefore, secondBefore;
	private final SelectedBorder editingBefore;

	// State after the changes
	private Point firstAfter, secondAfter;
	private SelectedBorder editingAfter;

	public SelectionChange(Canvas canvas, Layer layer) {
		super(canvas, layer);
		selection = canvas.getSelection();

		firstBefore = selection.getFirst().getLocation();
		secondBefore = selection.getSecond().getLocation();

		editingBefore = selection.getVisibilityState();
	}

	@Override public void apply() {
		selection.getFirst().setLocation(firstAfter);
		selection.getSecond().setLocation(secondAfter);

		selection.setEditing(editingAfter);

		selection.setEditStarted(false);
	}

	@Override public void revert() {
		selection.getFirst().setLocation(firstBefore);
		selection.getSecond().setLocation(secondBefore);

		selection.setEditing(editingBefore);

		selection.setEditStarted(false);
	}

	public void setAfter() {
		firstAfter = selection.getFirst().getLocation();
		secondAfter = selection.getSecond().getLocation();

		editingAfter = selection.getVisibilityState();
	}

	public boolean isChanged() {
		return !firstBefore.equals(firstAfter) || !secondBefore.equals(secondAfter) || editingBefore != editingAfter;
	}
}
