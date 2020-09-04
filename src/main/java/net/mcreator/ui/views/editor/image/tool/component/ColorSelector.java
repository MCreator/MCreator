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

package net.mcreator.ui.views.editor.image.tool.component;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;

public class ColorSelector extends JPanel {

	private Color foreground = Color.BLACK, background = Color.WHITE;
	private final JButton foregroundColor = new JButton();
	private final JButton backgroundColor = new JButton();

	private static JDialog dialog = null;

	public ColorSelector(MCreator f) {
		super(null);

		foregroundColor.setToolTipText("Foreground color");
		foregroundColor.setMargin(new Insets(0, 0, 0, 0));
		foregroundColor.setBounds(new Rectangle(0, 0, 40, 30));

		backgroundColor.setToolTipText("Background color");
		backgroundColor.setMargin(new Insets(0, 0, 0, 0));
		backgroundColor.setBounds(new Rectangle(20, 20, 40, 30));

		JButton reset = new JButton();
		reset.setToolTipText("Reset foreground and background colors");
		reset.setIcon(UIRES.get("16px.reset"));
		reset.setMargin(new Insets(0, 0, 0, 0));
		reset.setBounds(new Rectangle(0, 34, 16, 16));
		reset.setOpaque(false);
		reset.setBorder(BorderFactory.createEmptyBorder());

		JButton swap = new JButton();
		swap.setToolTipText("Swap foreground and background colors");
		swap.setIcon(UIRES.get("16px.swap"));
		swap.setMargin(new Insets(0, 0, 0, 0));
		swap.setBounds(new Rectangle(44, 0, 16, 16));
		swap.setOpaque(false);
		swap.setBorder(BorderFactory.createEmptyBorder());

		foregroundColor.addActionListener(e -> {
			JColor.colorChooser.setColor(foreground);
			dialog = JColorChooser.createDialog(f, "Select foreground color: ", true, JColor.colorChooser, event -> {
				Color c = JColor.colorChooser.getColor();
				if (c != null) {
					foreground = c;
					updateColors();
				}
				dialog.setVisible(false);
			}, event -> dialog.setVisible(false));
			dialog.setVisible(true);
		});

		backgroundColor.addActionListener(e -> {
			JColor.colorChooser.setColor(background);
			dialog = JColorChooser.createDialog(f, "Select background color: ", true, JColor.colorChooser, event -> {
				Color c = JColor.colorChooser.getColor();
				if (c != null) {
					background = c;
					updateColors();
				}
				dialog.setVisible(false);
			}, event -> dialog.setVisible(false));
			dialog.setVisible(true);
		});

		reset.addActionListener(e -> {
			foreground = Color.BLACK;
			background = Color.WHITE;
			updateColors();
		});

		swap.addActionListener(e -> {
			Color temp = foreground;
			foreground = background;
			background = temp;
			updateColors();
		});

		updateColors();

		add(foregroundColor);
		add(backgroundColor);
		add(reset);
		add(swap);

		setOpaque(false);
		setPreferredSize(new Dimension(60, 50));
	}

	private void updateColors() {
		foregroundColor.setBackground(foreground);
		backgroundColor.setBackground(background);
	}

	public Color getForegroundColor() {
		return new Color(foreground.getRGB(), false);
	}

	public void setForegroundColor(Color foreground) {
		this.foreground = foreground;
		updateColors();
	}

	public Color getBackgroundColor() {
		return new Color(background.getRGB(), false);
	}

	public void setBackgroundColor(Color background) {
		this.background = background;
		updateColors();
	}
}
