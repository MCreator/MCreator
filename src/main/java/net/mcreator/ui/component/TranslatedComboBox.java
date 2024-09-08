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

package net.mcreator.ui.component;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VComboBox;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TranslatedComboBox extends VComboBox<String> {

	@SafeVarargs public TranslatedComboBox(Map.Entry<String, String>... entries) {
		final LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		map.forEach((key, value) -> super.addItem(key));
		setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String translationKey = map.get(value.toString());
				if (translationKey != null) {
					return super.getListCellRendererComponent(list, L10N.t(translationKey), index, isSelected,
							cellHasFocus);
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
	}

	@Override public void addItem(String item) {
		throw new UnsupportedOperationException();
	}

	@Override public void removeAllItems() {
		throw new UnsupportedOperationException();
	}

}
