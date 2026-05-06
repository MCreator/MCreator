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

import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.element.util.AnnotationUtils;
import net.mcreator.ui.component.JMinMaxSpinner;

import javax.swing.*;

public class ComponentFromAnnotation {

	public static JSpinner spinner(Class<?> type, String field) {
		Numeric annotation = AnnotationUtils.getAnnotation(type, field, Numeric.class);
		JSpinner retval;
		if (isInteger(annotation)) {
			retval = new JSpinner(
					new SpinnerNumberModel((int) annotation.init(), (int) annotation.min(), (int) annotation.max(),
							(int) annotation.step()));
		} else {
			retval = new JSpinner(
					new SpinnerNumberModel(annotation.init(), annotation.min(), annotation.max(), annotation.step()));
		}
		retval.putClientProperty("MCreator.fieldName", field);
		return retval;
	}

	public static JMinMaxSpinner minMaxSpinner(Class<?> type, String minField, String maxField) {
		Numeric minAnnotation = AnnotationUtils.getAnnotation(type, minField, Numeric.class);
		Numeric maxAnnotation = AnnotationUtils.getAnnotation(type, maxField, Numeric.class);

		if (minAnnotation.step() != maxAnnotation.step())
			throw new IllegalArgumentException("Min and max fields must have the same step value");
		if (minAnnotation.min() != maxAnnotation.min())
			throw new IllegalArgumentException("Min field must be less than or equal to max field");
		if (minAnnotation.max() != maxAnnotation.max())
			throw new IllegalArgumentException("Max field must be greater than or equal to min field");

		JMinMaxSpinner retval;
		if (isInteger(minAnnotation) && isInteger(maxAnnotation)) {
			retval = new JMinMaxSpinner((int) minAnnotation.init(), (int) maxAnnotation.init(),
					(int) minAnnotation.min(), (int) minAnnotation.max(), (int) minAnnotation.step());
		} else {
			retval = new JMinMaxSpinner(minAnnotation.init(), maxAnnotation.init(), minAnnotation.min(),
					minAnnotation.max(), minAnnotation.step());
		}

		retval.putClientProperty("MCreator.fieldName", minField + ", " + maxField);
		return retval;
	}

	private static boolean isInteger(Numeric annotation) {
		return isInteger(annotation.init()) && isInteger(annotation.min()) && isInteger(annotation.max())
				&& isInteger(annotation.step());
	}

	private static boolean isInteger(double d) {
		return Double.isFinite(d) && d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE && d == Math.rint(d);
	}

}
