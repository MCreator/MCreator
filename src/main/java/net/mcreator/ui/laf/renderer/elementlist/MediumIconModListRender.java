/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class MediumIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	public MediumIconModListRender() {
		setLayout(new BorderLayout(5, 0));

	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		removeAll();
		setBorder(null);

		JLabel label = new JLabel();

		JLabel icon = new JLabel();
		if (element != null) {
			if (isSelected) {
				label.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			} else {
				label.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			label.setText(StringUtils.abbreviateString(element.getName(), 25));
			label.setFont(MCreatorTheme.secondary_font.deriveFont(19.0f));

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
				icon.setIcon(new ImageIcon(ImageUtils.resize(UIRES.get("folder").getImage(), 42)));
			} else if (element instanceof ModElement) {
				ImageIcon modIcon = ((ModElement) element).getElementIcon();

				if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
						&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
					if (dva != null) {
						ImageIcon iconbig = ImageUtils.drawOver(modIcon, dva);
						icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 42)));
					} else {
						icon.setIcon(new ImageIcon(ImageUtils.resize(modIcon.getImage(), 42)));
					}
				} else {
					if (dva != null) {
						ImageIcon iconbig = ImageUtils.drawOver(((ModElement) element).getType().getIcon(), dva);
						icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 42)));
					} else {
						icon.setIcon(new ImageIcon(
								ImageUtils.resizeAA(((ModElement) element).getType().getIcon().getImage(), 42)));
					}
				}
			}

			setToolTipText(element.getName());
		}

		icon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

		add("Center", label);
		add("West", icon);

		return this;
	}

}
