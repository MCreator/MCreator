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

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentUtils {

	public static JComponent setForeground(JComponent component, Color color) {
		component.setForeground(color);
		return component;
	}

	public static JComponent deriveFont(JComponent component, float param) {
		Font font = component.getFont();
		if (font == null)
			font = Theme.current().getFont();
		component.setFont(font.deriveFont(param));
		return component;
	}

	public static Component wrapWithInfoButton(Component ca, String url) {
		JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pan.setOpaque(false);
		JLabel lab = new JLabel(UIRES.get("16px.info"));
		lab.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lab.setToolTipText("More info");
		lab.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				DesktopUtils.browseSafe(url);
			}
		});
		pan.add(ca);
		pan.add(lab);
		return pan;
	}

	public static JPanel squareAndBorder(Component gor, Color color, String text) {
		JPanel p = new JPanel();
		p.add(gor);
		p.setOpaque(false);
		p.setPreferredSize(new Dimension(100, 100));
		p.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color, 1), text, TitledBorder.LEADING,
						TitledBorder.BOTTOM, gor.getFont(), color));
		return p;
	}

	public static JPanel squareAndBorder(Component gor, String text) {
		return squareAndBorder(gor, Theme.current().getForegroundColor(), text);
	}

}
