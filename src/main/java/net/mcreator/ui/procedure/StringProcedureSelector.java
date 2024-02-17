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
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class StringProcedureSelector extends RetvalProcedureSelector<String, StringProcedure> {

	@Nullable private final JComponent fixedValue;

	// JTextComponent variants

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JTextComponent fixedValue, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, 200,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JTextComponent fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, width,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JTextComponent fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, fixedValue, width, providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable JTextComponent fixedValue, int width,
			Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.STRING, helpContext, mcreator, eventName, side, allowInlineEditor,
				fixedValue, width, providedDependencies);

		this.fixedValue = fixedValue;

		if (fixedValue != null) {
			fixedValue.setBackground(Theme.current().getBackgroundColor());
			fixedValue.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 5, 1, allowInlineEditor ? 5 : 0, this.getBackground()),
					BorderFactory.createMatteBorder(0, 5, 0, 5, fixedValue.getBackground())));
		}
	}

	// JComboBox<String> variants

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JComboBox<String> fixedValue, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, 200,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator,
			@Nullable JComboBox<String> fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, true, fixedValue, width,
				providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JComboBox<String> fixedValue, int width, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, fixedValue, width, providedDependencies);
	}

	public StringProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable JComboBox<String> fixedValue, int width,
			Dependency... providedDependencies) {
		super(VariableTypeLoader.BuiltInTypes.STRING, helpContext, mcreator, eventName, side, allowInlineEditor,
				fixedValue, width, providedDependencies);

		this.fixedValue = fixedValue;

		if (fixedValue != null) {
			fixedValue.setEditable(true);
		}
	}

	@Override public StringProcedure getSelectedProcedure() {
		ProcedureEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new StringProcedure(null, getFixedValue());
		return new StringProcedure(selected.string, getFixedValue());
	}

	@Override public void setSelectedProcedure(Procedure procedure) {
		if (procedure instanceof StringProcedure stringProcedure) {
			if (stringProcedure.getName() != null)
				procedures.setSelectedItem(new ProcedureEntry(stringProcedure.getName(), null));

			setFixedValue(stringProcedure.getFixedValue());
		}
	}

	@Override public String getFixedValue() {
		String value = "";

		if (fixedValue instanceof JTextComponent textComponent) {
			value = textComponent.getText();
		} else if (fixedValue instanceof JComboBox<?> comboBox) {
			value = comboBox.getEditor().getItem().toString();
		}

		return value;
	}

	@Override public void setFixedValue(String value) {
		if (fixedValue instanceof JTextComponent textComponent) {
			textComponent.setText(value);
		} else if (fixedValue instanceof JComboBox<?> comboBox) {
			comboBox.getEditor().setItem(value);
		}
	}

}
