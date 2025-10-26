/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import com.formdev.flatlaf.ui.FlatSplitPaneUI;

import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.*;

public class OpaqueFlatSplitPaneUI extends FlatSplitPaneUI {

	private Color dividerColor = null;

	@Override public BasicSplitPaneDivider createDefaultDivider() {
		return new FlatSplitPaneDivider(this) {
			@Override public void paint(Graphics g) {
				if (dividerColor != null) {
					Color origColor = g.getColor();
					g.setColor(dividerColor);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(origColor);
				}
				super.paint(g);
			}
		};
	}

	public void setDividerColor(Color dividerColor) {
		this.dividerColor = dividerColor;
	}

}
