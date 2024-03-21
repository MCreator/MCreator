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

public class ToolGroup extends JPanel {

	private final JPanel tools = new JPanel(new GridLayout(-1, 7, 4, 4));

	public ToolGroup(String name) {
		super(new BorderLayout(4, 4));
		JLabel label = new JLabel(name);

		add(label, BorderLayout.NORTH);
		add(tools, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
	}

	public void register(JToggleButton toolButton) {
		tools.add(toolButton);
	}

	public void register(JButton toolButton) {
		tools.add(toolButton);
	}

}
