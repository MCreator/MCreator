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
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.validation.component.VComboBox;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TranslatedComboBox extends VComboBox<String> {

	@SafeVarargs public TranslatedComboBox(Map.Entry<String, String>... entries) {
		this(null, entries);
	}

	@SafeVarargs public TranslatedComboBox(@Nullable String translationPrefix, Map.Entry<String, String>... entries) {
		final LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : entries) {
			map.put(entry.getKey(), L10N.t(entry.getValue()));
		}
		this(map, translationPrefix);
	}

	public TranslatedComboBox(LinkedHashMap<String, String> map, @Nullable String translationPrefix) {
		map.forEach((key, _) -> super.addItem(key));

		if (translationPrefix != null) {
			setRenderer(new ItemTexturesComboBoxRenderer(
					value -> L10N.t(translationPrefix + value.replace(' ', '_').toLowerCase(Locale.ROOT))));
		} else {
			setRenderer(new BasicComboBoxRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					String translatedEntryString = map.get(value.toString());
					return super.getListCellRendererComponent(list,
							Objects.requireNonNullElse(translatedEntryString, value), index, isSelected, cellHasFocus);
				}
			});
		}
	}

	@Override public void addItem(String item) {
		throw new UnsupportedOperationException();
	}

	@Override public void removeAllItems() {
		throw new UnsupportedOperationException();
	}

}
