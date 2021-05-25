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

import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class TilesIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	public TilesIconModListRender() {
		super(new BorderLayout(0, 0));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		removeAll();
		setBorder(null);

		JLabel label = new JLabel();
		JLabel label_details = new JLabel();

		JLabel icon = new JLabel();
		if (element != null) {
			if (isSelected) {
				label.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label_details.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label_details.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
			} else {
				label.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label_details.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			label.setText(StringUtils.abbreviateString(element.getName(), 18));
			ImageIcon dva = null;

			if (element instanceof ModElement) {
				JPanel text = new JPanel();
				text.setLayout(new BoxLayout(text, BoxLayout.PAGE_AXIS));
				text.setOpaque(false);
				text.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

				ModElement ma = (ModElement) element;

				label_details.setText(
						"<html><div width=210 height=42 style=\"overflow: hidden;\"><small" + (isSelected ?
								(" color=#" + Integer
										.toHexString(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).getRGB())
										.substring(2)) :
								"") + ">" + ma.getType().getDescription());

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

				text.add(label);
				text.add(label_details);

				add("Center", text);
				add("West", icon);
			} else {
				label.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));

				add("Center", PanelUtils.join(FlowLayout.LEFT, label));
				add("West", icon);
			}

			if (element instanceof FolderElement) {
				icon.setIcon(UIRES.get("folder"));
			} else if (element instanceof ModElement) {
				ImageIcon modIcon = ((ModElement) element).getElementIcon();

				if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
						&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
					if (dva != null) {
						icon.setIcon(ImageUtils.drawOver(
								ImageUtils.drawOver(TiledImageCache.getModTypeIcon(null), modIcon, 18, 18, 28, 28),
								dva));
					} else {
						icon.setIcon(
								ImageUtils.drawOver(TiledImageCache.getModTypeIcon(null), modIcon, 18, 18, 28, 28));
					}
				} else {
					if (dva != null) {
						icon.setIcon(ImageUtils
								.drawOver(TiledImageCache.getModTypeIcon(((ModElement) element).getType()), dva));
					} else {
						icon.setIcon(TiledImageCache.getModTypeIcon(((ModElement) element).getType()));
					}
				}
			}
		}

		return this;
	}

}
