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

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.io.Transliteration;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class TextFieldDialog extends AbstractWYSIWYGDialog {

	public TextFieldDialog(WYSIWYGEditor editor, @Nullable TextField textField) {
		super(editor.mcreator, textField);
		setModal(true);
		setSize(480, 150);
		setLocationRelativeTo(editor.mcreator);

		VTextField nameField = new VTextField(20);
		nameField.setPreferredSize(new Dimension(200, 28));
		nameField.enableRealtimeValidation();
		Validator validator = new JavaMemeberNameValidator(nameField, false);
		nameField.setValidator(() -> {
			String textname = Transliteration.transliterateString(nameField.getText());
			for (int i = 0; i < editor.list.getModel().getSize(); i++) {
				GUIComponent component = editor.list.getModel().getElementAt(i);
				if (textField != null && component.name.equals(textField.name)) // skip current element if edit mode
					continue;
				if (component instanceof TextField && component.name.equals(textname))
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("dialog.gui.textfield_name_already_exists"));
			}
			return validator.validate();
		});
		JTextField deft = new JTextField(20);
		JPanel options = new JPanel();

		if (textField == null)
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.textfield_change_width")));
		else
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.textfield_resize")));

		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
		options.add(PanelUtils.join(L10N.label("dialog.gui.textfield_input_name"), nameField));
		add("Center", options);
		setTitle(L10N.t("dialog.gui.textfield_add"));
		options.add(PanelUtils.join(L10N.label("dialog.gui.textfield_initial_text"), deft));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (textField != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			nameField.setText(textField.name);
			deft.setText(textField.placeholder);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			if (nameField.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				setVisible(false);
				String text = Transliteration.transliterateString(nameField.getText());
				if (!text.equals("")) {
					if (textField == null) {
						editor.editor.setPositioningMode(120, 20);
						editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(setEditingComponent(
								new TextField(text, editor.editor.newlyAddedComponentPosX,
										editor.editor.newlyAddedComponentPosY, editor.editor.ow, editor.editor.oh,
										deft.getText()))));
					} else {
						int idx = editor.components.indexOf(textField);
						editor.components.remove(textField);
						TextField textfieldNew = new TextField(text, textField.getX(), textField.getY(),
								textField.width, textField.height, deft.getText());
						editor.components.add(idx, textfieldNew);
						setEditingComponent(textfieldNew);
					}
				}
			}
		});

		setVisible(true);
	}

}
