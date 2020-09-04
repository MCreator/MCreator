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

package net.mcreator.ui.laf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SlickDarkScrollBarUI extends BasicScrollBarUI {

	private final Color one;
	private final Color two;
	private boolean active, drag = false;

	private final boolean hasButtons;

	public static ComponentUI createUI(JComponent var0) {
		return new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), var0);
	}

	public SlickDarkScrollBarUI(Color bg, Color fg, final JComponent bar) {
		this(bg, fg, bar, false);
	}

	public SlickDarkScrollBarUI(Color bg, Color fg, final JComponent bar, boolean hasButtons) {
		this.one = bg;
		this.two = fg;
		this.hasButtons = hasButtons;

		bar.addMouseListener(new MouseAdapter() {
			@Override public void mouseReleased(MouseEvent mouseEvent) {
				active = false;
				drag = false;
				bar.repaint();
			}

			@Override public void mouseEntered(MouseEvent mouseEvent) {
				active = true;
				bar.repaint();
			}

			@Override public void mouseExited(MouseEvent mouseEvent) {
				if (!drag) {
					active = false;
					bar.repaint();
				}
			}
		});

		bar.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(MouseEvent mouseEvent) {
				super.mouseDragged(mouseEvent);
				drag = true;
			}
		});
	}

	@Override protected JButton createDecreaseButton(int orientation) {
		if (hasButtons) {
			JButton bt = super.createDecreaseButton(orientation);
			bt.setBackground(one);
			bt.setForeground(two);
			return bt;
		}
		JButton button = new JButton();
		Dimension noSize = new Dimension(0, 0);
		button.setPreferredSize(noSize);
		button.setMinimumSize(noSize);
		button.setMaximumSize(noSize);
		return button;
	}

	@Override protected JButton createIncreaseButton(int orientation) {
		if (hasButtons) {
			JButton bt = super.createIncreaseButton(orientation);
			bt.setBackground(one);
			bt.setForeground(two);
			return bt;
		}
		JButton button = new JButton();
		Dimension noSize = new Dimension(0, 0);
		button.setPreferredSize(noSize);
		button.setMinimumSize(noSize);
		button.setMaximumSize(noSize);
		return button;
	}

	@Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		g.setColor(one);
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}

	@Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (!active) {
			g.setColor(two);
			g.translate(thumbBounds.x, thumbBounds.y);
			g.fillRect(0, 0, thumbBounds.width, thumbBounds.height);
			g.translate(-thumbBounds.x, -thumbBounds.y);
		} else {
			g.setColor(brighter(two, 0.9d));
			g.translate(thumbBounds.x, thumbBounds.y);
			g.fillRect(0, 0, thumbBounds.width, thumbBounds.height);
			g.translate(-thumbBounds.x, -thumbBounds.y);

			g.setColor(brighter(two, 0.8d));
			g.translate(thumbBounds.x, thumbBounds.y);
			g.drawRect(0, 0, thumbBounds.width, thumbBounds.height);
			g.translate(-thumbBounds.x, -thumbBounds.y);
		}

	}

	private Color brighter(Color color, double factor) {
		int var1 = color.getRed();
		int var2 = color.getGreen();
		int var3 = color.getBlue();
		int var4 = color.getAlpha();
		byte var5 = 5;
		if (var1 == 0 && var2 == 0 && var3 == 0) {
			return new Color(var5, var5, var5, var4);
		} else {
			if (var1 > 0 && var1 < var5) {
				var1 = var5;
			}

			if (var2 > 0 && var2 < var5) {
				var2 = var5;
			}

			if (var3 > 0 && var3 < var5) {
				var3 = var5;
			}

			return new Color(Math.min((int) ((double) var1 / factor), 255),
					Math.min((int) ((double) var2 / factor), 255), Math.min((int) ((double) var3 / factor), 255), var4);
		}
	}

}
