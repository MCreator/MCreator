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
import net.mcreator.element.parts.gui.IMachineNamedComponent;
import net.mcreator.io.Transliteration;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class CheckboxDialog extends AbstractWYSIWYGDialog {

	public CheckboxDialog(WYSIWYGEditor editor, @Nullable Checkbox checkbox) {
		super(editor.mcreator, checkbox);
		setSize(480, 220);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);
		setTitle(L10N.t("dialog.gui.checkbox_add"));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		VTextField nameField = new VTextField(20);
		nameField.setPreferredSize(new Dimension(200, 28));
		nameField.enableRealtimeValidation();
		Validator validator = new JavaMemeberNameValidator(nameField, false);
		nameField.setValidator(() -> {
			String textname = Transliteration.transliterateString(nameField.getText());
			for (int i = 0; i < editor.list.getModel().getSize(); i++) {
				GUIComponent component = editor.list.getModel().getElementAt(i);
				if (checkbox != null && component.name.equals(checkbox.name)) // skip current element if edit mode
					continue;
				if (component instanceof IMachineNamedComponent && component.name.equals(textname))
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("common.name_already_exists"));
			}
			return validator.validate();
		});
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
				String text = Transliteration.transliterateString(nameField.getText());
				if (!text.equals("")) {
					if (checkbox == null) {
						editor.editor.setPositioningMode(20, 20);
						editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(setEditingComponent(
								new Checkbox(text, editor.editor.newlyAddedComponentPosX,
										editor.editor.newlyAddedComponentPosY, checkboxText.getText(),
										isCheckedProcedure.getSelectedProcedure()))));
					} else {
						int idx = editor.components.indexOf(checkbox);
						editor.components.remove(checkbox);
						Checkbox checkboxNew = new Checkbox(text, checkbox.getX(), checkbox.getY(),
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
