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

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class JColor extends JPanel {

	public static final JColorChooser colorChooser = new JColorChooser();

	private Color currentColor = Color.white;

	private final JTextField colorText;
	private final List<ActionListener> listeners = new ArrayList<>();

	private final TechnicalButton edit = new TechnicalButton(UIRES.get("18px.edit"));
	private final TechnicalButton remove = new TechnicalButton(UIRES.get("18px.remove"));

	private JDialog dialog = null;

	private final boolean allowNullColor;
	private final boolean allowTransparency;

	public JColor(Window window, boolean allowNullColor, boolean allowTransparency) {
		setLayout(new BorderLayout(0, 0));
		setBackground(Theme.current().getBackgroundColor());
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, getBackground()));

		this.allowNullColor = allowNullColor;
		this.allowTransparency = allowTransparency;

		colorText = new JTextField(9);
		colorText.setEditable(false);
		colorText.setPreferredSize(new Dimension(0, 24));
		colorText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		colorText.setHorizontalAlignment(JTextField.CENTER);
		colorText.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					edit.doClick();
			}
		});

		edit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
		remove.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

		edit.addActionListener(e -> {
			colorChooser.setColor(getColor());
			dialog = JColorChooser.createDialog(window, "Select color: ", true, colorChooser, e2 -> {
				Color color = colorChooser.getColor();
				if (color != null)
					setColor(color);
				dialog.setVisible(false);
			}, e2 -> dialog.setVisible(false));
			dialog.setVisible(true);
		});
		remove.addActionListener(e -> setColor(null));

		JPanel controls = PanelUtils.totalCenterInPanel(
				allowNullColor ? PanelUtils.gridElements(1, 2, 2, 0, edit, remove) : edit);
		controls.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		controls.setOpaque(true);
		controls.setBackground(getBackground());

		for (AbstractColorChooserPanel panel : colorChooser.getChooserPanels())
			panel.setColorTransparencySelectionEnabled(allowTransparency);

		add("Center", colorText);
		add("East", controls);

		if (allowNullColor) {
			setColor(null);
		} else {
			setColor(Color.white);
		}
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		colorText.setEnabled(enabled);
		edit.setEnabled(enabled);
		remove.setEnabled(enabled);
	}

	public void addColorSelectedListener(ActionListener a) {
		listeners.add(a);
	}

	public void setColor(Color color) {
		if (color == null && !allowNullColor)
			color = Color.white;

		currentColor = allowTransparency || color == null ? color : new Color(color.getRGB(), false);

		if (currentColor == null) {
			colorText.setOpaque(false);
			colorText.setText("DEFAULT");
			colorText.setForeground(Theme.current().getForegroundColor());
		} else {
			colorText.setText(String.format("#%06X", 0xFFFFFF & color.getRGB()));
			colorText.setOpaque(true);
			colorText.setBackground(color);
			colorText.setForeground(getColorLuminance(color) > 128 ? Color.black : Color.white);
		}

		listeners.forEach(l -> l.actionPerformed(new ActionEvent("", 0, "")));
	}

	public Color getColor() {
		return currentColor;
	}

	private static double getColorLuminance(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		if (r == g && r == b)
			return r;
		return 0.299 * r + 0.587 * g + 0.114 * b;
	}

}
