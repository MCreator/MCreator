/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

public class StringListProcedureSelector extends RetvalProcedureSelector<List<String>, StringListProcedure> {

	@Nullable private final JStringListField fixedValue;

	public StringListProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JStringListField fixedValue, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, 200,
				providedDependencies);
	}

	public StringListProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JStringListField fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, width,
				providedDependencies);
	}

	public StringListProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			Side side, @Nullable JStringListField fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, fixedValue, width, providedDependencies);
	}

	public StringListProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			Side side, boolean allowInlineEditor, @Nullable JStringListField fixedValue, int width,
			Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.STRING, helpContext, mcreator, eventName, side, allowInlineEditor,
				fixedValue, width, providedDependencies);

		this.fixedValue = fixedValue;

		if (fixedValue != null) {
			fixedValue.setBackground(Theme.current().getBackgroundColor());
			fixedValue.setBorder(BorderFactory.createMatteBorder(1, 5, 1, allowInlineEditor ? 5 : 0, getBackground()));
		}
	}

	@Override public StringListProcedure getSelectedProcedure() {
		ProcedureEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new StringListProcedure(null, getFixedValue());
		return new StringListProcedure(selected.string, getFixedValue());
	}

	@Override public void setSelectedProcedure(Procedure procedure) {
		if (procedure instanceof StringListProcedure stringListProcedure) {
			if (stringListProcedure.getName() != null)
				procedures.setSelectedItem(new ProcedureEntry(stringListProcedure.getName(), null));

			setFixedValue(stringListProcedure.getFixedValue());
		}
	}

	@Override public List<String> getFixedValue() {
		return fixedValue != null ? fixedValue.getTextList() : List.of();
	}

	@Override public void setFixedValue(List<String> value) {
		if (fixedValue != null && value != null)
			fixedValue.setTextList(value);
	}

}
