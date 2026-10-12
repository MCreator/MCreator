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
import net.mcreator.util.image.IconUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class ItemTexturesComboBoxRenderer extends JLabel implements ListCellRenderer<String> {

	@Nullable private final Function<String, String> textMapper;

	public ItemTexturesComboBoxRenderer() {
		this(null);
	}

	public ItemTexturesComboBoxRenderer(@Nullable Function<String, String> textMapper) {
		this.textMapper = textMapper;

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

		if (textMapper != null)
			setText(textMapper.apply(value));
		else
			setText(value);

		if (value.equals("Special") || value.equals("MultiTool")) {
			setIcon(IconUtils.resize(UIRES.get("mod"), 30, 30));
		} else {
			setIcon(IconUtils.resize(BlockItemIcons.getIconFor(value), 30, 30));
		}

		setHorizontalTextPosition(SwingConstants.RIGHT);
		setHorizontalAlignment(SwingConstants.LEFT);

		return this;
	}

}
