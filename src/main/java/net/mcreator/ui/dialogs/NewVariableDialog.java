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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class NewVariableDialog {

	public static VariableElement showNewVariableDialog(MCreator mcreator, boolean showScope,
			OptionPaneValidatior variableNameValidator, Collection<VariableType> supportedTypes) {
		JPanel inp = new JPanel(new BorderLayout(10, 10));

		VTextField textField = new VTextField(25);
		textField.setPreferredSize(new Dimension(200, 28));
		textField.enableRealtimeValidation();
		variableNameValidator.setValidatedComponent(textField);
		textField.setValidator(variableNameValidator);

		JComboBox<VariableType> type = new JComboBox<>(supportedTypes.toArray(new VariableType[0]));

		JComboBox<VariableType.Scope> scope = new JComboBox<>();

		inp.add("North", L10N.label("dialog.variables.enter_name_select_type"));

		JPanel data = new JPanel(new GridLayout(showScope ? 3 : 2, 2, 5, 5));
		data.add(L10N.label("dialog.variables.variable_name"));
		data.add(textField);
		data.add(L10N.label("dialog.variables.variable_type"));
		data.add(type);

		if (showScope) {
			data.add(L10N.label("dialog.variables.variable_scope"));
			data.add(scope);

			type.addActionListener(e -> {
				scope.removeAllItems();
				VariableType typeSelectedItem = (VariableType) type.getSelectedItem();
				if (typeSelectedItem != null)
					Arrays.stream(typeSelectedItem.getSupportedScopesWithoutLocal(mcreator.getGeneratorConfiguration()))
							.forEach(scope::addItem);
			});

			// intial
			VariableType typeSelectedItem = (VariableType) type.getSelectedItem();
			if (typeSelectedItem != null)
				Arrays.stream(typeSelectedItem.getSupportedScopesWithoutLocal(mcreator.getGeneratorConfiguration()))
						.forEach(scope::addItem);
		}

		inp.add("Center", data);

		int option = JOptionPane.showConfirmDialog(mcreator, inp, L10N.t("dialog.variables.new_title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION
				&& textField.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR
				&& type.getSelectedItem() != null) {
			VariableElement element = new VariableElement();
			VariableType variable = VariableTypeLoader.INSTANCE.fromName(
					((VariableType) type.getSelectedItem()).getName());
			if (variable != null) {
				element.setName(Transliteration.transliterateString(textField.getText()));
				element.setType((VariableType) type.getSelectedItem());
				element.setValue(variable.getDefaultValue(mcreator.getWorkspace()));
				if (showScope)
					element.setScope((VariableType.Scope) scope.getSelectedItem());
				else
					element.setScope(VariableType.Scope.LOCAL);
				return element;
			}
		}
		return null;
	}

}
