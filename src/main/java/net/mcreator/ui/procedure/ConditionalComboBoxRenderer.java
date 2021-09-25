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

package net.mcreator.ui.procedure;

import net.mcreator.ui.init.L10N;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;

class ConditionalComboBoxRenderer implements ListCellRenderer<CBoxEntry> {

	private final BasicComboBoxRenderer renderer = new BasicComboBoxRenderer();

	@Override
	public Component getListCellRendererComponent(JList list, CBoxEntry value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel component = (JLabel) renderer.getListCellRendererComponent(list, value.string, index, isSelected,
				cellHasFocus);

		if (!value.correctDependencies) {
			component.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			component.setText("<html>" + component.getText() + L10N.t("action.procedure.missing_dependencies"));
		}

		if (value.getVariableType() != null && !cellHasFocus && value.correctDependencies) {
			component.setForeground(value.getVariableType().getBlocklyColor().brighter());
		}

		return component;
	}
}
