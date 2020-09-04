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

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;

import javax.swing.*;
import java.awt.event.MouseEvent;

public abstract class AbstractModificationTool extends AbstractTool {
	public AbstractModificationTool(String name, String description, ImageIcon icon, Canvas canvas,
			ColorSelector colorSelector, VersionManager versionManager) {
		super(name, description, icon, canvas, colorSelector, versionManager);
	}

	@Override public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			layer.createOverlay();
		}
		super.mousePressed(e);
	}

	@Override public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			layer.mergeOverlay();
			versionManager.addRevision(new Modification(canvas, layer));
			super.mouseReleased(e);
		}
	}
}
