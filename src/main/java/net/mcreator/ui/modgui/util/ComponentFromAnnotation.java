/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.modgui.util;

import net.mcreator.element.util.AnnotationUtils;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class ComponentFromAnnotation {

	public static TranslatedComboBox translatedOptions(Class<?> type, String field, String translationPrefix) {
		return new TranslatedComboBox(AnnotationUtils.getLimitedOptionsList(type, field).stream().collect(
				Collectors.toMap(o -> o, o -> L10N.t(translationPrefix + o.replace(' ', '_').toLowerCase(Locale.ROOT)),
						(_, b) -> b, LinkedHashMap::new)));
	}

	public static JComboBox<String> options(Class<?> type, String field) {
		return new JComboBox<>(AnnotationUtils.getLimitedOptionsList(type, field).toArray(new String[0]));
	}

}
