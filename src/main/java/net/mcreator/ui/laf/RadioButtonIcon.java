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

public class RadioButtonIcon implements Icon {

	private int getControlSize() {
		return 14;
	}

	@Override public void paintIcon(Component c, Graphics g, int x, int y) {
		JRadioButton cb = (JRadioButton) c;
		ButtonModel model = cb.getModel();
		int controlSize = getControlSize();
		if (model.isEnabled()) {
			if (model.isPressed() && model.isArmed()) {
				g.setColor(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).brighter());
				g.fillOval(x, y, controlSize - 1, controlSize - 1);
			} else if (model.isRollover()) {
				g.setColor(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());
				g.fillOval(x, y, controlSize - 1, controlSize - 1);
			} else {
				g.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				g.fillOval(x, y, controlSize - 1, controlSize - 1);
			}

			g.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));

		} else {
			g.setColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			g.fillOval(x, y, controlSize - 1, controlSize - 1);
		}

		if (model.isSelected()) {
			drawDot(g, x, y);
		}
	}

	private void drawDot(Graphics g, int x, int y) {
		int controlSize = getControlSize();
		g.fillOval(x + 3, y + 3, controlSize - 7, controlSize - 7);
	}

	@Override public int getIconWidth() {
		return getControlSize();
	}

	@Override public int getIconHeight() {
		return getControlSize();
	}
}

