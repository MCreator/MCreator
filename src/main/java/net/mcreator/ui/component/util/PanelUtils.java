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

package net.mcreator.ui.component.util;

import javax.swing.*;
import java.awt.*;

public class PanelUtils {

	public static JPanel join(Component... c1) {
		return join(FlowLayout.CENTER, c1);
	}

	public static JPanel join(int align, Component... c1) {
		JPanel skup = new JPanel(new FlowLayout(align));
		skup.setOpaque(false);
		for (Component c : c1) {
			skup.add(c);
		}
		return skup;
	}

	public static JPanel join(int align, int hgap, int vgap, Component... c1) {
		JPanel skup = new JPanel(new FlowLayout(align, hgap, vgap));
		skup.setOpaque(false);
		for (Component c : c1) {
			skup.add(c);
		}
		return skup;
	}

	public static JPanel centerInPanel(Component component) {
		return join(component);
	}

	public static JPanel totalCenterInPanel(Component component) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		p.add(component, new GridBagConstraints());
		return p;
	}

	public static JPanel centerInPanelPadding(Component component, int x, int y) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, x, y));
		p.setOpaque(false);
		p.add(component);
		return p;
	}

	public static JPanel maxMargin(Component what, int margin, boolean top, boolean left, boolean bottom,
			boolean right) {
		JPanel cont = new JPanel(new BorderLayout(margin, margin));
		cont.setOpaque(false);
		cont.add("Center", what);
		if (left)
			cont.add("West", new JLabel());
		if (right)
			cont.add("East", new JLabel());
		if (top)
			cont.add("North", new JLabel());
		if (bottom)
			cont.add("South", new JLabel());
		return cont;
	}

	public static JComponent northAndCenterElement(Component top, Component center) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("North", top);
		p.add("Center", center);
		return p;
	}

	public static  JComponent topToToeElement(Component top, Component center, Component toe) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("North", top);
		p.add("Center", center);
		p.add("South", toe);
		return p;
	}

	public static JComponent centerAndSouthElement(Component top, Component center) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("Center", top);
		p.add("South", center);
		return p;
	}

	public static JComponent centerAndSouthElement(Component top, Component center, int hg, int vg) {
		JPanel p = new JPanel(new BorderLayout(hg, vg));
		p.setOpaque(false);
		p.add("Center", top);
		p.add("South", center);
		return p;
	}

	public static JComponent westAndEastElement(Component west, Component east) {
		return westAndEastElement(west, east, 0, 0);
	}

	public static JComponent gridElements(int row, int col, int mw, int mh, Component... components) {
		JPanel p = new JPanel(new GridLayout(row, col, mw, mh));
		p.setOpaque(false);
		for (Component component : components)
			p.add(component);
		return p;
	}

	public static JComponent gridElements(int row, int col, Component... components) {
		JPanel p = new JPanel(new GridLayout(row, col));
		p.setOpaque(false);
		for (Component component : components)
			p.add(component);
		return p;
	}

	public static JComponent northAndCenterElement(Component top, Component center, int px, int py) {
		JPanel p = new JPanel(new BorderLayout(px, py));
		p.setOpaque(false);
		p.add("North", top);
		p.add("Center", center);
		return p;
	}

	public static JComponent westAndEastElement(Component west, Component east, int px, int py) {
		JPanel p = new JPanel(new BorderLayout(px, py));
		p.setOpaque(false);
		p.add("West", west);
		p.add("East", east);
		return p;
	}

	public static JComponent westAndCenterElement(Component west, Component center) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("West", west);
		p.add("Center", center);
		return p;
	}

	public static JComponent centerAndEastElement(Component center, Component east) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("East", east);
		p.add("Center", center);
		return p;
	}

	public static JComponent centerAndEastElement(Component center, Component east, int px, int py) {
		JPanel p = new JPanel(new BorderLayout(px, py));
		p.setOpaque(false);
		p.add("East", east);
		p.add("Center", center);
		return p;
	}

	public static JComponent pullElementUp(Component element) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add("North", element);
		return p;
	}

	public static JComponent westAndCenterElement(JComponent west, JComponent center, int px, int py) {
		JPanel p = new JPanel(new BorderLayout(px, py));
		p.setOpaque(false);
		p.add("West", west);
		p.add("Center", center);
		return p;
	}

	public static JComponent expandHorizontally(Component element) {
		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;

		p.setOpaque(false);
		p.add(element, gbc);
		return p;
	}

}
