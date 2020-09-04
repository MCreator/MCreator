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

import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;

public class ModelComboBoxRenderer extends JLabel implements ListCellRenderer<Model> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Model> list, Model value, int index,
			boolean isSelected, boolean cellHasFocus) {

		setOpaque(true);

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value == null) // sometimes value can be null
			return this;

		setText(value.toString());

		if (value.getType() == Model.Type.JSON)
			setIcon(UIRES.get("model.small_json"));
		else if (value.getType() == Model.Type.OBJ)
			setIcon(UIRES.get("model.small_obj"));
		else if (value.getType() == Model.Type.JAVA)
			setIcon(UIRES.get("model.small_java"));
		else if (value.getType() == Model.Type.BUILTIN)
			setIcon(UIRES.get("model.small_builtin"));
		else
			setIcon(new EmptyIcon(32, 32));

		setHorizontalTextPosition(SwingConstants.RIGHT);
		setHorizontalAlignment(SwingConstants.LEFT);

		return this;
	}

}