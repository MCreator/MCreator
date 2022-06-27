/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.component.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TableUtil {

	public static boolean isEditingCell(JTable table, int row, int column) {
		return table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column;
	}

	public static List<String> getRowContents(JTable table, int row) {
		List<String> retVal = new ArrayList<>();
		for (int i = 0; i < table.getColumnCount(); i++) {
			retVal.add(isEditingCell(table, row, i) ?
					table.getCellEditor().getCellEditorValue().toString() :
					table.getValueAt(row, i).toString());
		}
		return retVal;
	}

	public static List<String> getColumnContents(JTable table, int column) {
		List<String> retVal = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			retVal.add(isEditingCell(table, i, column) ?
					table.getCellEditor().getCellEditorValue().toString() :
					table.getValueAt(i, column).toString());
		}
		return retVal;
	}

}
