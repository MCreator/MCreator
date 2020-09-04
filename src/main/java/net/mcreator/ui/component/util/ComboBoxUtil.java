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

import net.mcreator.minecraft.DataListEntry;

import javax.swing.*;
import java.util.List;

public class ComboBoxUtil {

	public static <T> void updateComboBoxContents(JComboBox<T> comboBox, List<T> data) {
		Object selected = comboBox.getSelectedItem();
		comboBox.removeAllItems();
		data.forEach(comboBox::addItem);
		if (selected != null && !(selected instanceof DataListEntry.Null))
			comboBox.setSelectedItem(selected);
		else if (comboBox.getItemCount() > 0)
			comboBox.setSelectedIndex(0);
	}

	public static <T> void updateComboBoxContents(JComboBox<T> comboBox, List<T> data, T defaultValue) {
		Object selected = comboBox.getSelectedItem();
		comboBox.removeAllItems();
		data.forEach(comboBox::addItem);
		if (selected != null && !(selected instanceof DataListEntry.Null))
			comboBox.setSelectedItem(selected);
		else
			comboBox.setSelectedItem(defaultValue);
	}

}
