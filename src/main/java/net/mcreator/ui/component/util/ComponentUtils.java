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
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import javax.swing.border.Border;
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
		component.setFont(component.getFont().deriveFont(param));
		return component;
	}

	public static void normalizeButton2(JButton button) {
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"), 1),
						BorderFactory.createLineBorder(new Color(30, 30, 30), 4))));
		button.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
	}

	public static void normalizeButton2(JToggleButton button) {
		Border off = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"), 1),
						BorderFactory.createLineBorder(new Color(30, 30, 30), 4)));
		Border on = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"), 1),
						BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 4)));
		button.setBorder(button.isSelected() ? on : off);
		button.setBackground(
				button.isSelected() ? (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") : new Color(30, 30, 30));
		button.addChangeListener(e -> {
			button.setBorder(button.isSelected() ? on : off);
			button.setBackground(
					button.isSelected() ? (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") : new Color(30, 30, 30));
		});
	}

	public static void normalizeButton4(AbstractButton button) {
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 1),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 1),
						BorderFactory.createMatteBorder(1, 3, 1, 3, new Color(0, 0, 0, 0)))));
		button.setBackground(new Color(0, 0, 0, 0));
		button.setOpaque(false);
		button.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		deriveFont(button, 11);
	}

	public static void normalizeButton5(AbstractButton button) {
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x5B6452), 1),
				BorderFactory.createMatteBorder(1, 3, 1, 3, new Color(0, 0, 0, 0))));
		button.setBackground(new Color(0, 0, 0, 0));
		button.setOpaque(false);
		button.setForeground(new Color(0x9CB482));
		deriveFont(button, 11);
	}

	public static Component wrapWithInfoButton(Component ca, String url) {
		JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		p.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createLineBorder(color, 2), text, TitledBorder.LEADING,
						TitledBorder.BOTTOM, gor.getFont(), color));
		return p;
	}

	public static JPanel squareAndBorder(Component gor, String text) {
		return squareAndBorder(gor, (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), text);
	}

}
