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

package net.mcreator.ui.component.util;

import javax.swing.*;

public class ListUtil {

	public static <T> void setSelectedValues(JList<T> list, Iterable<T> values) {
		list.clearSelection();

		for (T value : values) {
			int index = getIndex(list.getModel(), value);
			if (index >= 0) {
				list.addSelectionInterval(index, index);
			}
		}

		list.ensureIndexIsVisible(list.getSelectedIndex());
	}

	public static <T> int getIndex(ListModel<T> model, T value) {
		if (value == null)
			return -1;

		if (model instanceof DefaultListModel)
			return ((DefaultListModel<T>) model).indexOf(value);

		for (int i = 0; i < model.getSize(); i++)
			if (value.equals(model.getElementAt(i)))
				return i;

		return -1;
	}

}
