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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class DetailsIconModListRender extends JPanel implements ListCellRenderer<IElement> {

	private final JLabel label = new JLabel();
	private final JLabel label2 = new JLabel();
	private final JLabel label3 = new JLabel();
	private final JLabel label4 = new JLabel();
	private final JLabel label5 = new JLabel();
	private final JLabel icon = new JLabel();

	public DetailsIconModListRender() {
		setLayout(new BorderLayout(15, 0));
		setBorder(null);
		setBackground(Theme.current().getForegroundColor());

		label.setFont(label.getFont().deriveFont(14.0f));
		label2.setFont(label.getFont());
		label3.setFont(label.getFont());
		label4.setFont(label.getFont());
		label5.setFont(label.getFont());

		icon.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));

		add("Center", PanelUtils.gridElements(1, 6, label, label2, label3, label4, label5));
		add("West", icon);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends IElement> list, IElement element, int index,
			boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setOpaque(true);
			label.setForeground(Theme.current().getBackgroundColor());
			label2.setForeground(Theme.current().getBackgroundColor());
			label3.setForeground(Theme.current().getBackgroundColor());
			label4.setForeground(Theme.current().getBackgroundColor());
			label5.setForeground(Theme.current().getBackgroundColor());
		} else {
			setOpaque(false);
			label.setForeground(Theme.current().getForegroundColor());
			label2.setForeground(Theme.current().getForegroundColor());
			label3.setForeground(Theme.current().getForegroundColor());
			label4.setForeground(Theme.current().getForegroundColor());
			label5.setForeground(Theme.current().getForegroundColor());
		}

		label.setText(StringUtils.abbreviate(element.getName(), 24));
		if (element instanceof ModElement ma) {
			label2.setText(StringUtils.abbreviate(ma.getRegistryName(), 24));
			label3.setText(ma.getType().getReadableName());
			label4.setText(ma.isCodeLocked() ?
					L10N.t("workspace.elements.list.locked") :
					L10N.t("workspace.elements.list.notlocked"));
			label5.setText(ma.doesCompile() ?
					L10N.t("workspace.elements.list.compiles") :
					L10N.t("workspace.elements.list.compile_errors"));
		} else {
			label2.setText("-");
			label3.setText(L10N.t("workspace.elements.list.folder"));
			label4.setText("-");
			label5.setText("-");
		}

		ImageIcon modIcon = element instanceof ModElement ?
				((ModElement) element).getElementIcon() :
				UIRES.get("laf.directory");

		if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0 && modIcon.getIconHeight() > 0
				&& modIcon != MCItem.DEFAULT_ICON) {
			icon.setIcon(new ImageIcon(ImageUtils.resize(modIcon.getImage(), 16)));
		} else if (element instanceof ModElement) {
			icon.setIcon(new ImageIcon(ImageUtils.resize(((ModElement) element).getType().getIcon().getImage(), 16)));
		}
		setToolTipText(element.getName());

		return this;
	}

}
