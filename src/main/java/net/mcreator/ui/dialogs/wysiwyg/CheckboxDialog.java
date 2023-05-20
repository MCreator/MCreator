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

package net.mcreator.ui.dialogs.wysiwyg;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.gui.Checkbox;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.io.Transliteration;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class CheckboxDialog extends AbstractWYSIWYGDialog<Checkbox> {

	public CheckboxDialog(WYSIWYGEditor editor, @Nullable Checkbox checkbox) {
		super(editor, checkbox);
		setSize(480, 220);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);
		setTitle(L10N.t("dialog.gui.checkbox_add"));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		VTextField nameField = new VTextField(20);
		nameField.setPreferredSize(new Dimension(200, 28));
		UniqueNameValidator validator = new UniqueNameValidator(L10N.t("dialog.gui.checkbox_name_validator"),
				() -> Transliteration.transliterateString(nameField.getText()),
				() -> editor.getComponentList().stream().map(GUIComponent::getName),
				new JavaMemberNameValidator(nameField, false));
		validator.setIsPresentOnList(checkbox != null);
		nameField.setValidator(validator);
		nameField.enableRealtimeValidation();
		options.add(PanelUtils.join(L10N.label("dialog.gui.checkbox_name"), nameField));

		JTextField checkboxText = new JTextField(20);
		options.add(PanelUtils.join(L10N.label("dialog.gui.checkbox_text"), checkboxText));
		checkboxText.setPreferredSize(new Dimension(200, 28));

		ProcedureSelector isCheckedProcedure = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/checkbox_procedure_value"), editor.mcreator,
				L10N.t("dialog.gui.checkbox_procedure_value"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")).setDefaultName(
				L10N.t("condition.common.false"));
		isCheckedProcedure.refreshList();
		options.add(PanelUtils.join(isCheckedProcedure));

		add("Center", options);

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (checkbox != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			nameField.setText(checkbox.name);
			checkboxText.setText(checkbox.text);
			isCheckedProcedure.setSelectedProcedure(checkbox.isCheckedProcedure);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			if (nameField.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				setVisible(false);
				String checkBoxName = nameField.getText();
				if (!checkBoxName.equals("")) {
					if (checkbox == null) {
						Checkbox component = new Checkbox(checkBoxName, 0, 0, checkboxText.getText(),
								isCheckedProcedure.getSelectedProcedure());

						setEditingComponent(component);
						editor.editor.addComponent(component);
						editor.list.setSelectedValue(component, true);
						editor.editor.moveMode();
					} else {
						int idx = editor.components.indexOf(checkbox);
						editor.components.remove(checkbox);
						Checkbox checkboxNew = new Checkbox(checkBoxName, checkbox.getX(), checkbox.getY(),
								checkboxText.getText(), isCheckedProcedure.getSelectedProcedure());
						editor.components.add(idx, checkboxNew);
						setEditingComponent(checkboxNew);
					}
				}
			}
		});

		setVisible(true);
	}
}
