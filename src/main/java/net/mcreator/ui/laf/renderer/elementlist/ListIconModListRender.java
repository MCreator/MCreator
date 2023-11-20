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

package net.mcreator.ui.laf.renderer.elementlist;

import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class ListIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	private final JLabel label = new JLabel();
	private final JLabel icon = new JLabel();

	public ListIconModListRender() {
		setLayout(new BorderLayout(5, 0));
		setBorder(null);
		setBackground(Theme.current().getForegroundColor());

		icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		label.setFont(Theme.current().getSecondaryFont().deriveFont(14.0f));

		add("Center", label);
		add("West", icon);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setOpaque(true);
			label.setForeground(Theme.current().getBackgroundColor());
		} else {
			setOpaque(false);
			label.setForeground(Theme.current().getForegroundColor());
		}

		label.setText(StringUtils.abbreviateString(element.getName(), 200));

		ImageIcon dva = null;
		if (element instanceof ModElement ma) {
			if (!ma.doesCompile()) {
				dva = TiledImageCache.modTabRed;
			}
			if (ma.isCodeLocked()) {
				if (dva != null) {
					dva = ImageUtils.drawOver(dva, TiledImageCache.modTabPurple);
				} else {
					dva = TiledImageCache.modTabPurple;
				}
			}
		}

		if (element instanceof FolderElement) {
			icon.setIcon(new ImageIcon(ImageUtils.resize(UIRES.get("folder").getImage(), 22)));
		} else if (element instanceof ModElement) {
			ImageIcon modIcon = ((ModElement) element).getElementIcon();

			if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
					&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils.drawOver(modIcon, dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 22)));
				} else {
					icon.setIcon(new ImageIcon(ImageUtils.resize(modIcon.getImage(), 22)));
				}
			} else {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils.drawOver(((ModElement) element).getType().getIcon(), dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 22)));
				} else {
					icon.setIcon(new ImageIcon(
							ImageUtils.resizeAA(((ModElement) element).getType().getIcon().getImage(), 22)));
				}
			}
		}

		setToolTipText(element.getName());

		return this;
	}
}
