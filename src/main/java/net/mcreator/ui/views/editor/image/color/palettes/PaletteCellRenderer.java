/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.color.palettes;

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class PaletteCellRenderer extends JPanel implements ListCellRenderer<ColorPalette> {

	private final JLabel name = new JLabel();
	private final JLabel icon = new JLabel();

	public PaletteCellRenderer() {
		FlowLayout layout = (FlowLayout) getLayout();
		layout.setAlignment(FlowLayout.LEFT);

		setOpaque(false);

		add(icon);
		add(name);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends ColorPalette> palettes, ColorPalette palette,
			int index, boolean isSelected, boolean cellHasFocus) {
		name.setForeground(Theme.current().getForegroundColor());
		name.setText(palette.getName());

		icon.setIcon(new ImageIcon(palette.getIcon(32, 32)));

		if (isSelected) {
			setOpaque(true);
			setBackground(Theme.current().getInterfaceAccentColor());
		} else if (cellHasFocus) {
			setOpaque(true);
			setBackground(Theme.current().getAltForegroundColor());
		} else
			setOpaque(false);

		return this;
	}
}
