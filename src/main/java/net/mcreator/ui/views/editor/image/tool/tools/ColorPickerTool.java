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

import java.awt.*;

public class ColorPickerTool extends AbstractTool {

	public ColorPickerTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super("Color picker", "A tool that sets current foreground color to the color under the cursor",
				UIRES.get("img_editor.picker"), canvas, colorSelector, versionManager);
		noSettings(true);
	}

	@Override public boolean process(ZoomedMouseEvent mouseEvent) {
		if (layer.in(mouseEvent.getX(), mouseEvent.getY())) {
			Color c = new Color(layer.getRGB(mouseEvent.getX() - layer.getX(), mouseEvent.getY() - layer.getY()));
			colorSelector.setForegroundColor(c);
			return true;
		}
		return false;
	}
}
