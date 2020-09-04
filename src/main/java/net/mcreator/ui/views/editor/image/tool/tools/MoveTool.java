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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Relocation;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MoveTool extends AbstractTool {
	private Point original = null;
	private Point prev = null;
	private Relocation relocation;

	public MoveTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super("Move tool", "A tool for moving active layers", UIRES.get("img_editor.move"), canvas, colorSelector,
				versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		if (prev != null) {
			int x = layer.getX() + e.getX() - prev.x;
			int y = layer.getY() + e.getY() - prev.y;
			layer.setX(x);
			layer.setY(y);
			prev = e.getPoint();
			relocation.setAfter(layer);
			return true;
		}
		return false;
	}

	@Override public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (layer.in(e.getX(), e.getY())) {
			prev = e.getPoint();
			original = new Point(layer.getX(), layer.getY());
			relocation = new Relocation(canvas, layer);
			versionManager.addRevision(relocation);
		}
	}

	@Override public void mouseReleased(MouseEvent e) {
		prev = null;
		if (original.x != layer.getX() && original.y != layer.getY()) {
			relocation.setAfter(layer);
		}
		super.mouseReleased(e);
	}
}
