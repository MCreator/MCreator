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

import net.mcreator.io.OS;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.InputEvent;
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
		map.setBackground(Theme.current().getBackgroundColor());
		map.setSelectionBackground(Theme.current().getForegroundColor());
		map.setSelectionForeground(Theme.current().getBackgroundColor());
		map.setBorder(BorderFactory.createEmptyBorder());
		map.setGridColor(Theme.current().getAltBackgroundColor());
		map.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (OS.getOS() == OS.MAC) {
			map.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (column == 1)
						c.setFont(new Font(".SF NS Text", Font.PLAIN, 12));
					return c;
				}
			});
		}

		DefaultTableModel model = (DefaultTableModel) map.getModel();
		SortedSet<BasicAction> keys = new TreeSet<>(acceleratorMap.getActionKeyStrokeMap().keySet());
		for (BasicAction key : keys) {
			KeyStroke keyStroke = acceleratorMap.getActionKeyStrokeMap().get(key);
			String acceleratorText = "";
			if (keyStroke != null) {
				int modifiers = keyStroke.getModifiers();
				if (modifiers > 0) {
					acceleratorText = InputEvent.getModifiersExText(modifiers);
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
