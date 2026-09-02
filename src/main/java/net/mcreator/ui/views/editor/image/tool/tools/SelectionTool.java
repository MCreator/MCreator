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

package net.mcreator.ui.views.editor.image.tool.tools;

import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.SelectedBorder;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.SelectionChange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SelectionTool extends AbstractTool {
	private Cursor usingCursor = null;
	private boolean first = true;
	private SelectedBorder editingBorder = SelectedBorder.NONE, lastBorder = SelectedBorder.NONE;
	private Point x = null, y = null;

	SelectionChange change = null;

	public SelectionTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.select"),
				L10N.t("dialog.image_maker.tools.types.select_description"), UIRES.get("img_editor.select"), canvas,
				colorSelector, versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			Selection selection = canvas.getSelection();
			switch (editingBorder) {
			case ANY, NONE -> {
				if (first) {
					selection.setEditing(SelectedBorder.ANY);
					selection.getFirst().x = (int) Math.round(e.getPreciseX());
					selection.getFirst().y = (int) Math.round(e.getPreciseY());
					first = false;
				}
				selection.getSecond().x = (int) Math.round(e.getPreciseX());
				selection.getSecond().y = (int) Math.round(e.getPreciseY());
			}
			case TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT -> {
				x.x = (int) Math.round(e.getPreciseX());
				y.y = (int) Math.round(e.getPreciseY());
			}
			case TOP, BOTTOM -> y.y = (int) Math.round(e.getPreciseY());
			case LEFT, RIGHT -> x.x = (int) Math.round(e.getPreciseX());
			}
			return true;
		}
		return false;
	}

	@Override public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			change = new SelectionChange(canvas, layer);
			Selection selection = canvas.getSelection();

			selection.setEditStarted(true);
			editingBorder = selection.getEditing();

			switch (editingBorder) {
			case TOP_LEFT -> {
				x = selection.getLeftPoint();
				y = selection.getTopPoint();
			}
			case TOP -> {
				x = null;
				y = selection.getTopPoint();
			}
			case TOP_RIGHT -> {
				x = selection.getRightPoint();
				y = selection.getTopPoint();
			}
			case RIGHT -> {
				x = selection.getRightPoint();
				y = null;
			}
			case BOTTOM_RIGHT -> {
				x = selection.getRightPoint();
				y = selection.getBottomPoint();
			}
			case BOTTOM -> {
				x = null;
				y = selection.getBottomPoint();
			}
			case BOTTOM_LEFT -> {
				x = selection.getLeftPoint();
				y = selection.getBottomPoint();
			}
			case LEFT -> {
				x = selection.getLeftPoint();
				y = null;
			}
			}
		}
		super.mousePressed(e);
	}

	@Override public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			Selection selection = canvas.getSelection();

			if (selection.hasSurface())
				selection.setEditing(SelectedBorder.ANY);
			else {
				selection.setEditing(SelectedBorder.NONE);
			}

			first = true;
			selection.setEditStarted(false);

			if (change != null) {
				change.setAfter();
				if (change.isChanged())
					versionManager.addRevision(change);
			}
		}
		super.mouseReleased(e);
	}

	@Override public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (SwingUtilities.isLeftMouseButton(e)) {
			Selection selection = canvas.getSelection();

			ZoomedMouseEvent zme = (ZoomedMouseEvent) e;

			SelectedBorder border = selection.checkHandles((int) zme.getRawX(), (int) zme.getRawY());
			if (border != SelectedBorder.ANY) {
				selection.setEditing(SelectedBorder.NONE);
			}
		}
	}

	@Override public void mouseMoved(MouseEvent e) {
		ZoomedMouseEvent event = (ZoomedMouseEvent) e;
		SelectedBorder border = canvas.getSelection().checkHandles((int) event.getRawX(), (int) event.getRawY());

		// change the cursor based on which border we are floating above if different from the previous one
		if (border != lastBorder) {
			switch (border) {
			case TOP_LEFT, BOTTOM_RIGHT -> usingCursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			case TOP_RIGHT, BOTTOM_LEFT -> usingCursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			case TOP, BOTTOM -> usingCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			case LEFT, RIGHT -> usingCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			default -> usingCursor = null;
			}
			lastBorder = border;
		}
	}

	@Override public Cursor getHoverCursor() {
		if (usingCursor != null)
			return usingCursor;
		return getCursor();
	}

	@Override public Cursor getCursor() {
		return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}

	@Override public void toolDisabled(ToolActivationEvent event) {
		super.toolDisabled(event);
		canvas.getSelection().setEditing(SelectedBorder.ANY);
	}
}
