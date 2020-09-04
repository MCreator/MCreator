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

package net.mcreator.ui.views.editor.image.layer;

import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class LayerListCellRenderer extends JPanel implements ListCellRenderer<Layer> {

	private final JLabel name = new JLabel();
	private final JLabel icon = new JLabel();

	private final JLabel visible = new JLabel(UIRES.get("16px.shown"));

	public LayerListCellRenderer() {
		FlowLayout layout = (FlowLayout) getLayout();
		layout.setAlignment(FlowLayout.LEFT);

		setOpaque(false);

		add(visible);
		add(icon);
		add(name);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Layer> list, Layer layer, int index,
			boolean isSelected, boolean cellHasFocus) {
		name.setText(layer.getName());

		ImageIcon iconImage = ImageUtils.fit(layer.getRaster(), 32);
		if (!layer.isVisible())
			iconImage = ImageUtils.changeSaturation(iconImage, 0);
		icon.setIcon(iconImage);

		if (layer.isVisible())
			visible.setIcon(UIRES.get("16px.shown"));
		else
			visible.setIcon(UIRES.get("16px.hidden"));

		if (isSelected) {
			setOpaque(true);
			setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		} else if (cellHasFocus) {
			setOpaque(true);
			setBackground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		} else
			setOpaque(false);

		return this;
	}
}
