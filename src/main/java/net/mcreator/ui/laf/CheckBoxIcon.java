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

package net.mcreator.ui.laf;

import javax.swing.*;
import java.awt.*;

public class CheckBoxIcon implements Icon {

	private int getControlSize() {
		return 14;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		JCheckBox cb = (JCheckBox) c;
		ButtonModel model = cb.getModel();
		int controlSize = getControlSize();
		if (model.isEnabled()) {
			if (model.isPressed() && model.isArmed()) {
				g.setColor(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).brighter());
				g.fillRect(x, y, controlSize - 1, controlSize - 1);
			} else if (model.isRollover()) {
				g.setColor(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());
				g.fillRect(x, y, controlSize - 1, controlSize - 1);
			} else {
				g.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				g.fillRect(x, y, controlSize - 1, controlSize - 1);
			}

			g.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));

		} else {
			g.setColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			g.drawRect(x, y, controlSize - 1, controlSize - 1);
		}

		if (model.isSelected()) {
			drawCheck(g, x, y);
		}
	}

	private void drawCheck(Graphics g, int x, int y) {
		int controlSize = getControlSize();
		g.fillRect(x + 3, y + 5, 2, controlSize - 8);
		g.drawLine(x + (controlSize - 4), y + 3, x + 5, y + (controlSize - 6));
		g.drawLine(x + (controlSize - 4), y + 4, x + 5, y + (controlSize - 5));
	}

	public int getIconWidth() {
		return getControlSize();
	}

	public int getIconHeight() {
		return getControlSize();
	}
}

