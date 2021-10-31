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

package net.mcreator.ui.dialogs;

import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringSelectorDialog extends ListSelectorDialog<String> {
	public StringSelectorDialog(MCreator mcreator, Function<Workspace, String[]> entryProvider) {
		super(mcreator, entryProvider.andThen(Arrays::asList));
		list.setCellRenderer(new StringListCellRenderer());
	}

	@Override Predicate<String> getFilter(String term) {
		return s -> s.toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH));
	}

	public static String openSelectorDialog(MCreator mcreator, Function<Workspace, String[]> entryProvider,
			String title, String message) {
		var stringSelector = new StringSelectorDialog(mcreator, entryProvider);
		stringSelector.setMessage(message);
		stringSelector.setTitle(title);
		stringSelector.setVisible(true);
		return stringSelector.list.getSelectedValue();
	}

	public static List<String> openMultiSelectorDialog(MCreator mcreator, Function<Workspace, String[]> entryProvider,
			String title, String message) {
		var dataListSelector = new StringSelectorDialog(mcreator, entryProvider);
		dataListSelector.setMessage(message);
		dataListSelector.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dataListSelector.setTitle(title);
		dataListSelector.setVisible(true);
		return dataListSelector.list.getSelectedValuesList();
	}

	private class StringListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			var label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setText(value.toString().replace("CUSTOM:", ""));
			if (value.toString().contains("CUSTOM:"))
				setIcon(new ImageIcon(ImageUtils.resize(
						MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), value.toString()).getImage(), 18)));
			return label;
		}
	}
}