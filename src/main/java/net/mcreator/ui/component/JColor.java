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

package net.mcreator.ui.component;

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JColor extends JPanel {

	public static final JColorChooser colorChooser = new JColorChooser();

	private Color currentColor = Color.white;

	private final JTextField fl1 = new JTextField(10);
	private ActionListener al = null;
	private final JButton bt1 = new JButton("...");

	private final boolean allowNullColor;

	private JDialog dialog = null;

	public JColor(Window window) {
		this(window, false);
	}

	public JColor(Window window, boolean allowNullColor) {
		setLayout(new BorderLayout(2, 0));
		fl1.setText("255,255,255");
		fl1.setBackground(Color.white);
		bt1.setOpaque(false);

		fl1.setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")));
		fl1.setHorizontalAlignment(JTextField.CENTER);

		add("Center", fl1);

		fl1.setEditable(false);
		bt1.addActionListener(aea4 -> {
			colorChooser.setColor(getColor());
			dialog = JColorChooser.createDialog(window, "Select color: ", true, colorChooser, e -> {
				Color c = colorChooser.getColor();
				if (c != null)
					setColor(c);
				dialog.setVisible(false);
			}, e -> dialog.setVisible(false));
			dialog.setVisible(true);
		});

		this.allowNullColor = allowNullColor;

		if (allowNullColor) {
			setColor(null);
			JButton bt2 = new JButton(UIRES.get("16px.delete.gif"));
			bt2.setMargin(new Insets(0, 0, 0, 0));
			bt2.setOpaque(false);
			bt2.addActionListener(e -> setColor(null));
			add("East", PanelUtils.gridElements(1, 2, 2, 0, bt1, bt2));
		} else {
			add("East", bt1);
		}

		setOpaque(false);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fl1.setEnabled(enabled);
		bt1.setEnabled(enabled);
	}

	public void setColorSelectedListener(ActionListener a) {
		this.al = a;
	}

	public void setColor(Color c) {
		if (c == null && !allowNullColor)
			c = Color.white;

		currentColor = c;

		if (currentColor == null) {
			fl1.setOpaque(false);
			fl1.setText("DEFAULT");
			fl1.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		} else {
			String color = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
			fl1.setText(color);
			fl1.setOpaque(true);
			fl1.setBackground(c);
			fl1.setForeground(getColorLuminance(c) > 128 ? Color.black : Color.white);
		}

		if (al != null)
			al.actionPerformed(new ActionEvent("", 0, ""));
	}

	public Color getColor() {
		return currentColor;
	}

	public static double getColorLuminance(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		if (r == g && r == b)
			return r;
		return 0.299 * r + 0.587 * g + 0.114 * b;
	}

}
