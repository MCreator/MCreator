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

package net.mcreator.ui.laf;

import javax.swing.*;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;

public class PlainToolbarBorder extends MetalBorders.ToolBarBorder {

	@Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		super.paintBorder(c, g, x, y, w, h);

		g.translate(x, y);

		if (((JToolBar) c).getOrientation() == HORIZONTAL) {
			g.setColor(UIManager.getColor("ToolBar.background"));
			g.drawLine(0, h - 2, w, h - 2);
			g.setColor((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			g.drawLine(0, h - 1, w, h - 1);
		}

		g.translate(-x, -y);
	}

	@Override public Insets getBorderInsets(Component c, Insets newInsets) {
		Insets insets = super.getBorderInsets(c, newInsets);

		if (((JToolBar) c).getOrientation() == HORIZONTAL) {
			newInsets.top = insets.top - 1;
			newInsets.bottom = insets.bottom - 2;
		}

		return newInsets;
	}

}
