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
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.SelectedBorder;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Relocation;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SelectionTool extends AbstractTool {
	private Point prev = null;
	private Cursor usingCursor = null;

	public SelectionTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.select"),
				L10N.t("dialog.image_maker.tools.types.select_description"), UIRES.get("img_editor.select"), canvas,
				colorSelector, versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		canvas.getSelection().getSecond().x = e.getX();
		canvas.getSelection().getSecond().y = e.getY();
		if (prev != null) {
			//int x = e.getX() - prev.x;
			//int y = e.getY() - prev.y;
			//prev = e.getPoint();
			//relocation.setAfter(layer);
			return true;
		}
		return false;
	}

	@Override public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		canvas.getSelection().setActive(true);
		canvas.getSelection().getFirst().x = e.getX();
		canvas.getSelection().getFirst().y = e.getY();
		if (layer.in(e.getX(), e.getY())) {
			//prev = e.getPoint();
			//original = new Point(layer.getX(), layer.getY());
			//relocation = new Relocation(canvas, layer);
		}
	}

	@Override public void mouseReleased(MouseEvent e) {
		//prev = null;
		//if (layer.in(e.getX(), e.getY()) && original.x != layer.getX() && original.y != layer.getY()) {
		//relocation.setAfter(layer);
		//versionManager.addRevision(relocation);
		//}
		canvas.getSelection().setEditing(SelectedBorder.ANY);

		super.mouseReleased(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
		ZoomedMouseEvent event = (ZoomedMouseEvent) e;
		SelectedBorder border = canvas.getSelection().checkEditing((int) event.getRawX(), (int) event.getRawY());

		// change the cursor based on which border we are floating above
		switch (border) {
		case TOP_LEFT:
		case BOTTOM_RIGHT:
			usingCursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			break;
		case TOP_RIGHT:
		case BOTTOM_LEFT:
			usingCursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			break;
		case TOP:
		case BOTTOM:
			usingCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			break;
		case LEFT:
		case RIGHT:
			usingCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			break;
		default:
			usingCursor = null;
			break;
		}
	}

	@Override public Cursor getHoverCursor() {
		if (usingCursor != null)
			return usingCursor;
		return super.getUsingCursor();
	}

	@Override public void toolDisabled(ToolActivationEvent event) {
		super.toolDisabled(event);

		canvas.getSelection().setActive(false);
	}
}
