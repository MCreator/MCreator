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

package net.mcreator.ui.laf.renderer;

import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class ItemTexturesComboBoxRenderer extends JLabel implements ListCellRenderer<String> {

	public ItemTexturesComboBoxRenderer() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setText(value);

		if (value.equals("Special") || value.equals("MultiTool")) {
			setIcon(new ImageIcon(ImageUtils.resize(UIRES.get("mod").getImage(), 30)));
		} else {
			setIcon(new ImageIcon(ImageUtils.resize(BlockItemIcons.getIconFor(value).getImage(), 30)));
		}

		setHorizontalTextPosition(SwingConstants.RIGHT);
		setHorizontalAlignment(SwingConstants.LEFT);

		return this;
	}

}
