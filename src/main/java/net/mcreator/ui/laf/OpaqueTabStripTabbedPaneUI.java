/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;

import javax.swing.*;
import java.awt.*;

public class OpaqueTabStripTabbedPaneUI extends FlatTabbedPaneUI {

	@Override
	public void paint(Graphics g, JComponent c) {
		Rectangle tr = null;

		if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
			tr = tabViewport.getBounds();
			for (Component child : tabPane.getComponents()) {
				if (child instanceof FlatTabAreaButton && child.isVisible()) {
					tr = tr.union(child.getBounds());
				}
			}
		} else {
			for (Rectangle r : rects) {
				tr = (tr != null) ? tr.union(r) : r;
			}
		}

		if (tr != null) {
			g.setColor(tabPane.getBackground());

			int placement = tabPane.getTabPlacement();

			switch (placement) {
			case JTabbedPane.LEFT:
			case JTabbedPane.RIGHT:
				// Vertical tab bar
				g.fillRect(tr.x, 0, tr.width, tabPane.getHeight());
				break;

			case JTabbedPane.TOP:
			case JTabbedPane.BOTTOM:
			default:
				// Horizontal tab bar
				g.fillRect(0, tr.y, tabPane.getWidth(), tr.height);
				break;
			}
		}

		super.paint(g, c);
	}

}
