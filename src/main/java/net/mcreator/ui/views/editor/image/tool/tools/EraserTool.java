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

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

public class EraserTool extends DrawingTool {

	public EraserTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel,
			VersionManager versionManager) {
		super(canvas, colorSelector, layerPanel, "Eraser", "A basic erasing tool", UIRES.get("img_editor.rubber"),
				versionManager);
	}

	@Override public void toolEnabled(ToolActivationEvent e) {
		layer.setRenderingMode(true);
		super.toolEnabled(e);
	}

	@Override public void toolDisabled(ToolActivationEvent e) {
		layer.setRenderingMode(false);
		super.toolDisabled(e);
	}
}
