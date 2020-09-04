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

public class JEmptyBox extends JPanel {

	public JEmptyBox() {
		this(0, 0);
	}

	public JEmptyBox(int w, int h) {
		setPreferredSize(new Dimension(w, h));
		setSize(new Dimension(w, h));
		setMinimumSize(new Dimension(w, h));
		setMaximumSize(new Dimension(w, h));
		setOpaque(false);
		setBorder(null);
	}

}
