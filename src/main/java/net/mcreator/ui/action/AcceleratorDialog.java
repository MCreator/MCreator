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

package net.mcreator.ui.action;

import net.mcreator.ui.init.L10N;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.SortedSet;
import java.util.TreeSet;

class AcceleratorDialog {

	static void showAcceleratorMapDialog(Window parent, AcceleratorMap acceleratorMap) {
		JTable map = new JTable(new DefaultTableModel(
				new Object[] { L10N.t("dialog.accelerators.action"), L10N.t("dialog.accelerators.accelerator") }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		map.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		map.setSelectionBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		map.setSelectionForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		map.setBorder(BorderFactory.createEmptyBorder());
		map.setGridColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		map.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DefaultTableModel model = (DefaultTableModel) map.getModel();
		SortedSet<BasicAction> keys = new TreeSet<>(acceleratorMap.getActionKeyStrokeMap().keySet());
		for (BasicAction key : keys) {
			KeyStroke keyStroke = acceleratorMap.getActionKeyStrokeMap().get(key);
			String acceleratorText = "";
			if (keyStroke != null) {
				int modifiers = keyStroke.getModifiers();
				if (modifiers > 0) {
					acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
					acceleratorText += " + ";
				}
				acceleratorText += KeyEvent.getKeyText(keyStroke.getKeyCode());
			}

			model.addRow(new Object[] { key.getName(), acceleratorText });
		}

		JOptionPane.showMessageDialog(parent, new JScrollPane(map), L10N.t("dialog.accelerators.title"),
				JOptionPane.PLAIN_MESSAGE);
	}

}
