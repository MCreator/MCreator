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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class ListIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	public ListIconModListRender() {
		setLayout(new BorderLayout(15, 0));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		removeAll();
		setBorder(null);

		JLabel label = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label5 = new JLabel();

		JLabel icon = new JLabel();
		if (element != null) {
			if (isSelected) {
				label.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label2.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label2.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label3.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label3.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label4.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label4.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label5.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				label5.setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			} else {
				label.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label2.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label3.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label4.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				label5.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			label.setFont(MCreatorTheme.secondary_font.deriveFont(14.0f));
			label2.setFont(MCreatorTheme.secondary_font.deriveFont(14.0f));
			label3.setFont(MCreatorTheme.secondary_font.deriveFont(13.0f));
			label4.setFont(MCreatorTheme.secondary_font.deriveFont(11.0f));
			label5.setFont(MCreatorTheme.secondary_font.deriveFont(11.0f));

			ImageIcon dva = null;

			label.setText(element.getName());

			if (element instanceof ModElement) {
				ModElement ma = (ModElement) element;
				label2.setText(ma.getRegistryName());
				label3.setText(ma.getType().getReadableName());
				label4.setText(ma.isCodeLocked() ?
						L10N.t("workspace.elements.list.locked") :
						L10N.t("workspace.elements.list.notlocked"));
				label5.setText(ma.doesCompile() ?
						L10N.t("workspace.elements.list.compiles") :
						L10N.t("workspace.elements.list.compile_errors"));

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
			} else {
				label3.setText(L10N.t("workspace.elements.list.folder"));
			}

			ImageIcon modIcon = element instanceof ModElement ?
					((ModElement) element).getElementIcon() :
					UIRES.get("laf.directory.gif");

			if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
					&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils.drawOver(modIcon, dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 16)));
				} else {
					icon.setIcon(new ImageIcon(ImageUtils.resize(modIcon.getImage(), 16)));
				}
			} else if (element instanceof ModElement) {
				if (dva != null) {
					ImageIcon iconbig = ImageUtils
							.drawOver(TiledImageCache.getModTypeIcon(((ModElement) element).getType()), dva);
					icon.setIcon(new ImageIcon(ImageUtils.resize(iconbig.getImage(), 16)));
				} else {
					icon.setIcon(new ImageIcon(ImageUtils
							.resize(TiledImageCache.getModTypeIcon(((ModElement) element).getType()).getImage(), 16)));
				}
			}

			setToolTipText(element.getName());
		}

		icon.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));

		add("Center", PanelUtils.gridElements(1, 6, label, label2, label3, label4, label5));
		add("West", icon);
		return this;
	}

}
