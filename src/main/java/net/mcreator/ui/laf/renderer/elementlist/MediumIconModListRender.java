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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class MediumIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	private final JLabel label = new JLabel();
	private final JLabel icon = new JLabel();

	public MediumIconModListRender() {
		setLayout(new BorderLayout(5, 0));
		setBorder(null);
		setBackground(Theme.current().getForegroundColor());

		label.setFont(Theme.current().getSecondaryFont().deriveFont(19.0f));

		icon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

		add("Center", label);
		add("West", icon);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (element != null) {
			if (isSelected) {
				setOpaque(true);
				label.setForeground(Theme.current().getBackgroundColor());
			} else {
				setOpaque(false);
				label.setForeground(Theme.current().getForegroundColor());
			}

			label.setText(StringUtils.abbreviateString(element.getName(), 25));

			ImageIcon dva = null;
			if (element instanceof ModElement ma) {
				if (!ma.doesCompile()) {
					dva = UIRES.get("mod_types.overlay_err");
				}
				if (ma.isCodeLocked()) {
					if (dva != null) {
						dva = ImageUtils.drawOver(dva, UIRES.get("mod_types.overlay_locked"));
					} else {
						dva = UIRES.get("mod_types.overlay_locked");
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

		return this;
	}

}
