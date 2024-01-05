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

package net.mcreator.ui.procedure;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;

public class NumberProcedureSelector extends RetvalProcedureSelector<Double, NumberProcedure> {

	@Nullable private final JSpinner fixedValue;

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, @Nullable JSpinner fixedValue,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, 50,
				providedDependencies);
	}

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, @Nullable JSpinner fixedValue,
			int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, width,
				providedDependencies);
	}

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JSpinner fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, fixedValue, width, providedDependencies);
	}

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable JSpinner fixedValue, int width, Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.NUMBER, helpContext, mcreator, eventName, side, allowInlineEditor,
				fixedValue, width, providedDependencies);

		this.fixedValue = fixedValue;
	}

	@Override public NumberProcedure getSelectedProcedure() {
		ProcedureEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new NumberProcedure(null, getFixedValue());
		return new NumberProcedure(selected.string, getFixedValue());
	}

	@Override public void setSelectedProcedure(Procedure procedure) {
		if (procedure instanceof NumberProcedure numberProcedure) {
			if (numberProcedure.getName() != null)
				procedures.setSelectedItem(new ProcedureEntry(numberProcedure.getName(), null));

			setFixedValue(numberProcedure.getFixedValue());
		}
	}

	@Override public Double getFixedValue() {
		Double value = (double) 0;

		if (fixedValue != null) {
			Object rawValue = fixedValue.getValue();
			if (rawValue instanceof Double)
				value = (Double) rawValue;
			else if (rawValue instanceof Float)
				value = Double.valueOf((Float) rawValue);
			else if (rawValue instanceof Integer)
				value = Double.valueOf((Integer) rawValue);
			else if (rawValue instanceof Short)
				value = Double.valueOf((Short) rawValue);
			else if (rawValue instanceof Byte)
				value = Double.valueOf((Byte) rawValue);
		}

		return value;
	}

	@Override public void setFixedValue(Double value) {
		if (fixedValue != null && value != null)
			fixedValue.setValue(value);
	}

}
