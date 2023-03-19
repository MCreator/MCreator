/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class UnsupportedComponent extends JPanel {

	/**
	 * Helper util method that marks provided component as not supported.
	 *
	 * @param comp The component to be marked.
	 */
	public static void markUnsupported(Component comp) {
		Container parent = comp.getParent();
		if (parent != null) {
			int index = Arrays.asList(parent.getComponents()).indexOf(comp);
			parent.remove(index);
			parent.add(new UnsupportedComponent(comp), index);
		}
	}

	private final Image warning = UIRES.get("18px.warning").getImage();

	/**
	 * Constructs a panel that displays an overlay with "unsupported" warning over the component marked as such.
	 *
	 * @param origin The component to be marked.
	 */
	public UnsupportedComponent(Component origin) {
		setLayout(new GridLayout());
		setOpaque(false);

		// disable origin component and prevent any mouse clicks/key presses from being handled by it
		origin.setEnabled(false);
		Arrays.stream(origin.getMouseListeners()).forEach(origin::removeMouseListener);
		Arrays.stream(origin.getKeyListeners()).forEach(origin::removeKeyListener);

		add(origin);
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		g.setColor(new Color(0.3f, 0.3f, 0, 0.4f));
		g.fillRect(0, 0, getWidth(), getHeight());

		int x = (this.getWidth() - warning.getWidth(null)) / 2;
		int y = (this.getHeight() - warning.getHeight(null)) / 2;

		if (getWidth() > 100) {
			g.setFont(g.getFont().deriveFont(12f));
			g.drawImage(warning, x - g.getFontMetrics().stringWidth("Not supported") / 2, y, null);
			g.setColor((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			g.drawString("Not supported", x - g.getFontMetrics().stringWidth("Not supported") / 2 + 18 + 4, y + 13);
		} else {
			g.drawImage(warning, x, y, null);
		}
	}
}
