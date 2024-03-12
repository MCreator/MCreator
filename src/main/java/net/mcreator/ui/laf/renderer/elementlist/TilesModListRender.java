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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class TilesModListRender extends JPanel implements ListCellRenderer<IElement> {

	private final JLabel label = new JLabel();
	private final JLabel label_details = new JLabel();
	private final JLabel icon = new JLabel();
	private final JPanel text = new JPanel(new BorderLayout(0, 0));

	public TilesModListRender() {
		super(new BorderLayout(0, 0));
		setBorder(null);
		setBackground(Theme.current().getForegroundColor());

		label.setFont(label.getFont().deriveFont(24.0f));
		label_details.setFont(label.getFont().deriveFont(15.0f));
		text.setOpaque(false);
		text.add("Center", label);
		text.add("South", label_details);
		add("Center", text);
		add("West", icon);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (element != null) {
			if (isSelected) {
				setOpaque(true);
				label.setForeground(Theme.current().getBackgroundColor());
				label_details.setForeground(Theme.current().getBackgroundColor());
			} else {
				setOpaque(false);
				label.setForeground(Theme.current().getForegroundColor());
				label_details.setForeground(Theme.current().getForegroundColor());
			}

			label.setText(StringUtils.abbreviateString(element.getName(), 18));

			if (element instanceof ModElement modElement) {
				label_details.setText("<html><div width=210 style=\"overflow: hidden;\"><small" + (isSelected ?
						(" color=#" + Integer.toHexString((Theme.current().getBackgroundColor()).getRGB())
								.substring(2)) :
						"") + ">" + modElement.getType().getDescription());
				text.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
			} else {
				label_details.setText("");
				text.setBorder(BorderFactory.createEmptyBorder(0, 5, 6, 0));
			}

			if (element instanceof FolderElement) {
				icon.setIcon(UIRES.get("mod_types.folder"));
			} else if (element instanceof ModElement modElement) {
				ImageIcon dva = null;
				if (!modElement.doesCompile()) {
					dva = UIRES.get("mod_types.overlay_err");
				}
				if (modElement.isCodeLocked()) {
					if (dva != null) {
						dva = ImageUtils.drawOver(dva, UIRES.get("mod_types.overlay_locked"));
					} else {
						dva = UIRES.get("mod_types.overlay_locked");
					}
				}

				ImageIcon modIcon = modElement.getElementIcon();
				if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0
						&& modIcon.getIconHeight() > 0 && modIcon != MCItem.DEFAULT_ICON) {
					if (dva != null) {
						icon.setIcon(ImageUtils.drawOver(
								ImageUtils.drawOver(UIRES.get("mod_types.empty"), modIcon, 18, 18, 28, 28), dva));
					} else {
						icon.setIcon(ImageUtils.drawOver(UIRES.get("mod_types.empty"), modIcon, 18, 18, 28, 28));
					}
				} else {
					if (dva != null) {
						icon.setIcon(ImageUtils.drawOver(modElement.getType().getIcon(), dva));
					} else {
						icon.setIcon(modElement.getType().getIcon());
					}
				}
			}
		}

		return this;
	}

}
