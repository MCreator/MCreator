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

public class TransparentToolBar extends JToolBar {

	private final Color c;

	public TransparentToolBar() {
		this(new Color(0.3f, 0.3f, 0.3f, 0.4f));
	}

	public TransparentToolBar(Color c) {
		this.c = c;
		setFloatable(false);
		setOpaque(false);
	}

	@Override public void paintComponent(Graphics g) {
		g.setColor(c);
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
