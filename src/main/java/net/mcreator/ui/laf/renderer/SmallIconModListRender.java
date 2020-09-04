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
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.laf.AbstractMCreatorTheme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class SmallIconModListRender extends JPanel implements ListCellRenderer<ModElement> {

	private final boolean showText;

	public SmallIconModListRender(boolean showText) {
		if (showText)
			setLayout(new BorderLayout(5, 0));

		this.showText = showText;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends ModElement> list, ModElement ma, int index,
			boolean isSelected, boolean cellHasFocus) {
		removeAll();
		setBorder(null);

		JLabel label = new JLabel();

		JLabel icon = new JLabel();
		if (ma != null) {
			if (isSelected) {
				label.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			} else {
				label.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			label.setText(StringUtils.abbreviateString(ma.getName(), 24));

			label.setFont(AbstractMCreatorTheme.light_font.deriveFont(18.0f));

			ImageIcon dva = null;

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

			ImageIcon modIcon = ma.getElementIcon();
			if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
					&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils.drawOver(modIcon, dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 25)));
				} else {
					icon.setIcon(new ImageIcon(ImageUtils.resize(modIcon.getImage(), 25)));
				}
			} else {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils.drawOver(TiledImageCache.getModTypeIcon(ma.getType()), dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 25)));
				} else {
					icon.setIcon(new ImageIcon(
							ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(ma.getType()).getImage(), 25)));
				}
			}

			setToolTipText(ma.getName());
		}

		if (showText)
			add("Center", label);

		add("West", icon);
		return this;
	}

}
