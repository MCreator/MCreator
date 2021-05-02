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

package net.mcreator.ui.dialogs;

import net.mcreator.io.Transliteration;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NewVariableDialog {

	public static VariableElement showNewVariableDialog(JFrame frame, boolean showScope,
			OptionPaneValidatior variableNameValidator, VariableElementType... supportedTypes) {
		JPanel inp = new JPanel(new BorderLayout(10, 10));

		VTextField textField = new VTextField(25);
		textField.setPreferredSize(new Dimension(200, 28));
		textField.enableRealtimeValidation();
		variableNameValidator.setValidatedComponent(textField);
		textField.setValidator(variableNameValidator);

		JComboBox<VariableElementType> type = new JComboBox<>(supportedTypes);

		JComboBox<VariableElementType.Scope> scope = new JComboBox<>(VariableElementType.Scope.values());

		inp.add("North", L10N.label("dialog.variables.enter_name_select_type"));

		JPanel data = new JPanel(new GridLayout(showScope ? 3 : 2, 2, 5, 5));
		data.add(L10N.label("dialog.variables.variable_name"));
		data.add(textField);
		data.add(L10N.label("dialog.variables.variable_type"));
		data.add(type);
		if (showScope) {
			data.add(L10N.label("dialog.variables.variable_scope"));
			data.add(scope);
		}

		inp.add("Center", data);

		int option = JOptionPane
				.showConfirmDialog(frame, inp, L10N.t("dialog.variables.new_title"), JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION
				&& textField.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR
				&& type.getSelectedItem() != null) {
			VariableElement element = new VariableElement();
			VariableElementType variable = VariableElement.getVariableFromType((String) type.getSelectedItem());
			element.setName(Transliteration.transliterateString(textField.getText()));
			element.setType((String) type.getSelectedItem());
			element.setValue(Objects.requireNonNull(variable).getDefaultValue());
			element.setScope((VariableElementType.Scope) scope.getSelectedItem());
			return element;
		}
		return null;
	}

}
