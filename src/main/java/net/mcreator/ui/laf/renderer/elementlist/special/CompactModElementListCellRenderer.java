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

package net.mcreator.ui.laf.renderer.elementlist.special;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;

import javax.swing.*;
import java.awt.*;

public class CompactModElementListCellRenderer implements ListCellRenderer<ModElement> {

	@Override
	public Component getListCellRendererComponent(JList<? extends ModElement> list, ModElement value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = L10N.label("workspace.elements.list.special.item",
				StringUtils.abbreviateString(value.getName(), 20), value.getType().getReadableName());
		label.setOpaque(true);
		label.setIcon(new ImageIcon(ImageUtils.resizeAA(ModElementManager.getModElementIcon(value).getImage(), 32)));
		label.setIconTextGap(10);
		label.setBackground(
				isSelected ? Theme.current().getForegroundColor() : Theme.current().getSecondAltBackgroundColor());
		label.setForeground(
				isSelected ? Theme.current().getBackgroundColor() : Theme.current().getAltForegroundColor());
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return label;
	}

}
