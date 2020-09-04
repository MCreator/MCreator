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

import net.mcreator.ui.component.util.ComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class VerticalTabButton extends JButton {

	public VerticalTabButton(String text) {
		super(text);

		ComponentUtils.deriveFont(this, 12);
		int width = getFontMetrics(getFont()).stringWidth(text);

		setPreferredSize(new Dimension(22, width + 30));
		setMinimumSize(new Dimension(22, width + 30));
		setMaximumSize(new Dimension(22, width + 30));
	}

	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		Font rotatedFont = getFont().deriveFont(affineTransform);
		g2d.setFont(rotatedFont);
		g2d.setColor(getForeground());
		g2d.drawString(getText(), 15, 14 + getFontMetrics(getFont()).stringWidth(getText()));
	}

}
