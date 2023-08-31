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
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ColorPickerTool extends AbstractTool {

	private final JCheckBox respectOpacity;

	public ColorPickerTool(Canvas canvas, ColorSelector colorSelector, VersionManager versionManager) {
		super(L10N.t("dialog.image_maker.tools.types.colorpicker"),
				L10N.t("dialog.image_maker.tools.types.colorpicker_description"), UIRES.get("img_editor.picker"),
				canvas, colorSelector, versionManager);

		respectOpacity = new JCheckBox(L10N.t("dialog.image_maker.tools.types.pick_opacity"));
		respectOpacity.setSelected(true);
		settingsPanel.add(respectOpacity);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		mouseMoved(e);
		if (layer.in(e.getX(), e.getY())) {
			Color c = new Color(layer.getRGB(e.getX() - layer.getX(), e.getY() - layer.getY()), true);
			if (e.isShiftDown()) {
				colorSelector.setBackgroundColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
						respectOpacity.isSelected() ? c.getAlpha() : colorSelector.getBackgroundColor().getAlpha()));
			} else {
				colorSelector.setForegroundColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
						respectOpacity.isSelected() ? c.getAlpha() : colorSelector.getForegroundColor().getAlpha()));
			}
			return true;
		}
		return false;
	}

	@Override public void mouseEntered(MouseEvent e) {
		canvas.enablePreview(true);
		super.mouseEntered(e);
	}

	@Override public void mouseExited(MouseEvent e) {
		canvas.enablePreview(false);
		canvas.getCanvasRenderer().repaint();
		super.mouseExited(e);
	}

	@Override public void toolEnabled(ToolActivationEvent e) {
		canvas.enablePreview(true);
		super.toolEnabled(e);
	}

	@Override public void toolDisabled(ToolActivationEvent e) {
		canvas.enablePreview(false);
		canvas.getCanvasRenderer().repaint();
		super.toolDisabled(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
		canvas.updateCustomPreview(e, Shape.SQUARE, 1);
	}
}
