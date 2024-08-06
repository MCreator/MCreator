/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import javafx.util.Pair;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class KeyAndValueSelectorDialog extends ListSelectorDialog<String> {
	final Map<String, String> map = new HashMap<>();

	public KeyAndValueSelectorDialog(MCreator mcreator, Map<String, String> map) {
		super(mcreator, e -> map.keySet().stream().toList());
		list.setCellRenderer(new StringListCellRenderer());

		this.map.putAll(map);
	}

	public String getValueByKey(String key) {
		return map.get(key);
	}

	public String getSelectedValue() {
		return map.get(list.getSelectedValue());
	}

	public List<String> getSelectedValuesList() {
		return map.keySet().stream().filter(e -> list.getSelectedValuesList().contains(e)).map(map::get).toList();
	}

	public String getSelectedKey() {
		return list.getSelectedValue();
	}

	public List<String> getSelectedKeysList() {
		return list.getSelectedValuesList();
	}

	@Override Predicate<String> getFilter(String term) {
		return e -> e.toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH));
	}

	public static Pair<String, String> openSelectorDialog(MCreator mcreator,
			Map<String, String> map, String title, String message) {
		var keyAndValueSelector = new KeyAndValueSelectorDialog(mcreator, map);
		keyAndValueSelector.setMessage(message);
		keyAndValueSelector.setTitle(title);
		keyAndValueSelector.setVisible(true);
		return new Pair<>(keyAndValueSelector.getSelectedValue(), keyAndValueSelector.getSelectedKey()); // ("value", "readable name")
	}

	public static List<Pair<String, String>> openMultiSelectorDialog(MCreator mcreator,
			Map<String, String> map, String title, String message) {
		var keyAndValueSelector = new KeyAndValueSelectorDialog(mcreator, map);
		keyAndValueSelector.setMessage(message);
		keyAndValueSelector.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		keyAndValueSelector.setTitle(title);
		keyAndValueSelector.setVisible(true);
		return keyAndValueSelector.getSelectedKeysList().stream().map(e -> new Pair<>(keyAndValueSelector.getValueByKey(e), e)).toList();
	}

	private class StringListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			var label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setText(value.toString().replace("CUSTOM:", ""));
			if (value.toString().contains("CUSTOM:")) {
				String[] parts = value.toString().split(":");
				setIcon(new ImageIcon(ImageUtils.resize(
						MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), parts[0] + ":" + parts[1]).getImage(),
						18)));
			}
			return label;
		}
	}
}
