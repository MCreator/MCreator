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

import javax.swing.*;
import java.awt.*;

public class JTitledComponentWrapper extends JPanel {
	public JTitledComponentWrapper(String title, JComponent component) {
		super(new GridBagLayout());
		JLabel propertyLabel = new JLabel(title);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.weighty = 1;
		c.weightx = 1;

		c.ipady = 5;
		c.gridx = 0;
		c.gridy = 0;
		add(propertyLabel, c);

		c.ipady = 0;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 1;
		add(component, c);

		setOpaque(false);
	}
}
