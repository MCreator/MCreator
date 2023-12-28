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
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;

public class LogicProcedureSelector extends RetvalProcedureSelector<Boolean, LogicProcedure> {

	@Nullable private final JCheckBox fixedValue;

	public LogicProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, @Nullable JCheckBox fixedValue,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, 0,
				providedDependencies);
	}

	public LogicProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, @Nullable JCheckBox fixedValue,
			int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, width,
				providedDependencies);
	}

	public LogicProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JCheckBox fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, fixedValue, width, providedDependencies);
	}

	public LogicProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable JCheckBox fixedValue, int width, Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.LOGIC, helpContext, mcreator, eventName, side, allowInlineEditor,
				fixedValue, width, providedDependencies);

		this.fixedValue = fixedValue;

		if (fixedValue != null) {
			fixedValue.setBorderPainted(true);
			fixedValue.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 5, 1, allowInlineEditor ? 5 : 0, this.getBackground()),
					BorderFactory.createMatteBorder(0, 5, 0, 5, fixedValue.getBackground())));
		}
	}

	@Override public LogicProcedure getSelectedProcedure() {
		ProcedureEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new LogicProcedure(null, getFixedValue());
		return new LogicProcedure(selected.string, getFixedValue());
	}

	@Override public void setSelectedProcedure(Procedure procedure) {
		if (procedure instanceof LogicProcedure logicProcedure) {
			if (logicProcedure.getName() != null)
				procedures.setSelectedItem(new ProcedureEntry(logicProcedure.getName(), null));

			setFixedValue(logicProcedure.getFixedValue());
		}
	}

	@Override public Boolean getFixedValue() {
		if (fixedValue != null)
			return fixedValue.isSelected();
		return false;
	}

	@Override public void setFixedValue(Boolean value) {
		if (fixedValue != null && value != null)
			fixedValue.setSelected(value);
	}
}
