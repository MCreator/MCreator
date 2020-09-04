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

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JPanel {

	private int max = 1;
	private int curr = -1;
	private int wid = -1;

	private Color emptyColor = new Color(80, 80, 80);
	private Color barColor = (Color) UIManager.get("MCreatorLAF.MAIN_TINT");

	@Override public void paint(Graphics g) {
		if (emptyColor != null) {
			g.setColor(emptyColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		} else {
			super.paint(g);
		}

		g.setColor(barColor);
		if (curr >= 0 && wid == -1 && max != 0)
			g.fillRect(0, 0, (int) (((double) getWidth() / ((double) max)) * (double) curr), getHeight());
		else if (max != 0 && curr >= 0)
			g.fillRect(0, 0, wid, getHeight());
	}

	public void setMaximalValue(int max) {
		this.max = max;
		repaint();
	}

	public void setCurrentValue(final int curr2) {
		wid = -1;
		curr = curr2;
		repaint();
	}

	public void init() {
		curr = 0;
		repaint();
	}

	public void setEmptyColor(Color emptyColor) {
		this.emptyColor = emptyColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}
}
