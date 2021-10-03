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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.CanvasRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorSelector extends JPanel {

	private Color foreground = Color.BLACK, background = Color.WHITE;
	private final JButton foregroundColor = new JButton(generateTransparentPreview(foreground));
	private final JButton backgroundColor = new JButton(generateTransparentPreview(background));

	private static JDialog dialog = null;

	public ColorSelector(MCreator f) {
		super(null);

		foregroundColor.setToolTipText(L10N.t("dialog.image_maker.tools.component.colorselector_foreground"));
		foregroundColor.setMargin(new Insets(0, 0, 0, 0));
		foregroundColor.setBounds(new Rectangle(0, 0, 42, 34));

		backgroundColor.setToolTipText(L10N.t("dialog.image_maker.tools.component.colorselector_background"));
		backgroundColor.setMargin(new Insets(0, 0, 0, 0));
		backgroundColor.setBounds(new Rectangle(21, 22, 42, 34));

		JButton reset = new JButton();
		reset.setToolTipText(L10N.t("dialog.image_maker.tools.component.colorselector_reset"));
		reset.setIcon(UIRES.get("16px.reset"));
		reset.setMargin(new Insets(0, 0, 0, 0));
		reset.setBounds(new Rectangle(1, 39, 16, 16));
		reset.setOpaque(false);
		reset.setBorder(BorderFactory.createEmptyBorder());

		JButton swap = new JButton();
		swap.setToolTipText(L10N.t("dialog.image_maker.tools.component.colorselector_swap"));
		swap.setIcon(UIRES.get("16px.swap"));
		swap.setMargin(new Insets(0, 0, 0, 0));
		swap.setBounds(new Rectangle(46, 1, 16, 16));
		swap.setOpaque(false);
		swap.setBorder(BorderFactory.createEmptyBorder());

		foregroundColor.addActionListener(e -> {
			JColor.colorChooser.setColor(foreground);
			dialog = JColorChooser.createDialog(f,
					L10N.t("dialog.image_maker.tools.component.colorselector_select_foreground"), true,
					JColor.colorChooser, event -> {
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
			dialog = JColorChooser.createDialog(f,
					L10N.t("dialog.image_maker.tools.component.colorselector_select_background"), true,
					JColor.colorChooser, event -> {
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
		setPreferredSize(new Dimension(63, 56));
	}

	private ImageIcon generateTransparentPreview(Color color) {
		BufferedImage preview = new BufferedImage(40, 32, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = preview.createGraphics();

		Paint solid = graphics2D.getPaint();

		graphics2D.setPaint(CanvasRenderer.buildCheckerboardPattern());
		graphics2D.fillRect(0, 0, 40, 40);

		graphics2D.setPaint(solid);
		graphics2D.setColor(color);
		graphics2D.fillRect(0, 0, 40, 40);

		graphics2D.dispose();
		return new ImageIcon(preview);
	}

	private void updateColors() {
		foregroundColor.setIcon(generateTransparentPreview(foreground));
		backgroundColor.setIcon(generateTransparentPreview(background));
	}

	public Color getForegroundColor() {
		return new Color(foreground.getRGB(), true);
	}

	public void setForegroundColor(Color foreground) {
		this.foreground = foreground;
		updateColors();
	}

	public Color getBackgroundColor() {
		return new Color(background.getRGB(), true);
	}

	public void setBackgroundColor(Color background) {
		this.background = background;
		updateColors();
	}
}
