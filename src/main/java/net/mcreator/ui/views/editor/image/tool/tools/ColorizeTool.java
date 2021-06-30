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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.dialogs.imageeditor.RecolorDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

public class ColorizeTool extends AbstractTool {

	private final MCreator window;

	public ColorizeTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager, MCreator window) {
		super("Colorize tool", "A tool for applying color to layers", UIRES.get("img_editor.colorize"), canvas,
				colorSelector, versionManager);
		this.window = window;
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent mouseEvent) {
		RecolorDialog dialog = new RecolorDialog(window, canvas, layer, colorSelector, versionManager);
		dialog.setVisible(true);
		return true;
	}

}
