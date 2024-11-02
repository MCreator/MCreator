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

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TransparentToolBar extends JToolBar {

	private final Color color;

	@Nullable private Color gradientColor = null;
	private int gradientWidth = 300;

	public TransparentToolBar() {
		this(ColorUtils.applyAlpha(Theme.current().getAltBackgroundColor(), 100));
	}

	public TransparentToolBar(Color color) {
		this.color = color;
		setFloatable(false);
		setOpaque(false);
	}

	public void setGradientWidth(int gradientWidth) {
		this.gradientWidth = gradientWidth;
	}

	public void setGradientColor(@Nullable Color gradientColor) {
		this.gradientColor = gradientColor;
		revalidate();
		repaint();
	}

	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (gradientColor != null) {
			Paint original = g2d.getPaint();
			int rectX = getWidth() - gradientWidth;

			Color[] colors = { color, gradientColor, color };
			float[] fractions = { 0.0f, 0.94f, 1.0f };
			LinearGradientPaint gradient = new LinearGradientPaint(rectX, 0, rectX + gradientWidth, 0, fractions,
					colors);

			g2d.setPaint(gradient);
			g2d.fill(new Rectangle2D.Double(rectX, 0, gradientWidth, getHeight()));
			g2d.setPaint(original);

			g2d.setColor(color);
			g2d.fillRect(0, 0, getWidth() - gradientWidth, getHeight());
		} else {
			g2d.setColor(color);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paintComponent(g);
	}

}
