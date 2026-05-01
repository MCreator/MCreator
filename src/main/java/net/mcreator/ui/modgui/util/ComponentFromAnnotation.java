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

import net.mcreator.element.types.interfaces.NumericParameter;
import net.mcreator.element.util.AnnotationUtils;

import javax.swing.*;

public class ComponentFromAnnotation {

	public static JSpinner spinner(Class<?> type, String field) {
		NumericParameter annotation = AnnotationUtils.getAnnotation(type, field, NumericParameter.class);
		if (isInteger(annotation.step())) {
			return new JSpinner(
					new SpinnerNumberModel((int) annotation.min(), (int) annotation.min(), (int) annotation.max(),
							(int) annotation.step()));
		} else {
			return new JSpinner(
					new SpinnerNumberModel(annotation.min(), annotation.min(), annotation.max(), annotation.step()));
		}
	}

	private static boolean isInteger(double d) {
		return Double.isFinite(d) && d == Math.rint(d);
	}

}
