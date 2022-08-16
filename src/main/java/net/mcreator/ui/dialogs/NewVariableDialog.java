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
import net.mcreator.ui.traslatable.AdvancedTranslatableComboBox;
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
import java.util.Map;
import java.util.Objects;

public class NewVariableDialog {

	public static VariableElement showNewVariableDialog(MCreator mcreator, boolean showScope,
			OptionPaneValidatior variableNameValidator, Collection<VariableType> supportedTypes) {

		JPanel inp = new JPanel(new BorderLayout(10, 10));

		JLabel label = new JLabel("Java命名规则:1.不要使用数字开头,2.尽量使用英文(否则可读性会很低),3.不要加入空格,4.不要使用关键字");
		inp.add("South",label);

		VTextField textField = new VTextField(25);
		textField.setPreferredSize(new Dimension(200, 28));
		textField.enableRealtimeValidation();
		variableNameValidator.setValidatedComponent(textField);
		textField.setValidator(variableNameValidator);

		AdvancedTranslatableComboBox<VariableType> type = new AdvancedTranslatableComboBox<>(Map.of("blockstate","方块状态","direction","方向","itemstack"
				,"物品组","logic","逻辑","number","数字","string","文本串"), Objects::toString);
		type.setDisplayEnglish(true);
		supportedTypes.forEach(type::addItem);

		AdvancedTranslatableComboBox<VariableType.Scope> scope = new AdvancedTranslatableComboBox<>(Map.of("GLOBAL_MAP","全地图","GLOBAL_WORLD","全世界","GLOBAL_SESSION","全局"
				,"PLAYER_LIFETIME","玩家一条命","PLAYER_PERSISTENT","玩家一直有","LOCAL","本地"),Object::toString);
		scope.setDisplayEnglish(true);
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
			VariableType variable = VariableTypeLoader.INSTANCE.fromName(type.getSelectedItem().toString());
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
