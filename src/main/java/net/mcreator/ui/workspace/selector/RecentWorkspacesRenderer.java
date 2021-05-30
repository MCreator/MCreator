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

package net.mcreator.ui.workspace.selector;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

class RecentWorkspacesRenderer extends JLabel implements ListCellRenderer<RecentWorkspaceEntry> {

	@Override
	public Component getListCellRendererComponent(JList<? extends RecentWorkspaceEntry> list,
			RecentWorkspaceEntry value, int index, boolean isSelected, boolean cellHasFocus) {
		setOpaque(isSelected);
		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setForeground(isSelected ?
				(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
				(Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		setBorder(BorderFactory.createEmptyBorder(2, 5, 3, 0));

		setFont(MCreatorTheme.main_font.deriveFont(16.0f));

		String path = value.getPath().getParentFile().getAbsolutePath().replace("\\", "/");

		if (value.getType() != GeneratorFlavor.UNKNOWN) {
			ImageIcon icon = new ImageIcon(
					ImageUtils.darken(ImageUtils.toBufferedImage(value.getType().getIcon().getImage())));

			if (isSelected) {
				setIcon(ImageUtils.colorize(icon, (Color) UIManager.get("MCreatorLAF.MAIN_TINT"), false));
			} else {
				setIcon(icon);
			}

			setIconTextGap(10);
			setText("<html><font style=\"font-size: 15px;\">" + StringUtils.abbreviateString(value.getName(), 18)
					+ "</font><small><br>" + StringUtils.abbreviateStringInverse(path, 34));
		} else {
			setIcon(null);

			setIconTextGap(0);
			setText("<html><font style=\"font-size: 15px;\">" + StringUtils.abbreviateString(value.getName(), 20)
					+ "</font><small><br>" + StringUtils.abbreviateStringInverse(path, 37));
		}

		return this;
	}

}
