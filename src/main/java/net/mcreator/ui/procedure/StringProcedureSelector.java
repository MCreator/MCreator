/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class StringProcedureSelector extends RetvalProcedureSelector<String, StringProcedure> {

	@Nullable private final JTextComponent fixedValue;

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JTextComponent fixedValue, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, fixedValue, 200,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JTextComponent fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, fixedValue, width,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JTextComponent fixedValue, int width, Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.STRING, helpContext, mcreator, eventName, side, fixedValue, width,
				providedDependencies);

		this.fixedValue = fixedValue;

		if (this.fixedValue != null) {
			this.fixedValue.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			this.fixedValue.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 5, 1, 5, this.getBackground()),
					BorderFactory.createMatteBorder(0, 5, 0, 5, this.fixedValue.getBackground())
			));
		}
	}

	@Override public StringProcedure getSelectedProcedure() {
		String value = "";

		if (fixedValue != null) {
			value = fixedValue.getText();
		}

		CBoxEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new StringProcedure(null, value);
		return new StringProcedure(selected.string, value);
	}

	@Override public void setSelectedProcedure(Procedure procedure) {
		if (procedure instanceof StringProcedure stringProcedure) {
			if (stringProcedure.getName() != null)
				procedures.setSelectedItem(new CBoxEntry(stringProcedure.getName(), null));

			if (fixedValue != null)
				fixedValue.setText(stringProcedure.getFixedValue());
		}
	}

}
