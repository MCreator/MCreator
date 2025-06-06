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

package net.mcreator.ui.validation.optionpane;

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public class VOptionPane {

	public static String showInputDialog(Window frame, String text, String title, ImageIcon icon,
			OptionPaneValidator validator) {
		return showInputDialog(frame, text, title, icon, validator, UIManager.getString("OptionPane.okButtonText"),
				UIManager.getString("OptionPane.cancelButtonText"));
	}

	public static String showInputDialog(Window frame, String text, String title, ImageIcon icon,
			OptionPaneValidator validator, String ok, String cancel) {
		return showInputDialog(frame, text, title, icon, validator, ok, cancel, null);
	}

	public static String showInputDialog(Window frame, String text, String title, ImageIcon icon,
			OptionPaneValidator validator, String ok, String cancel, String defaultValue) {
		return showInputDialog(frame, text, title, icon, validator, ok, cancel, defaultValue, null, null);
	}

	public static String showInputDialog(Window frame, String text, String title, ImageIcon icon,
			OptionPaneValidator validator, String ok, String cancel, String defaultValue,
			@Nullable JComponent optionalNorthComponent, @Nullable JComponent optionalSouthComponent) {
		JPanel inp = new JPanel(new BorderLayout(10, 10));

		VTextField textField = new VTextField(20);
		ComponentUtils.deriveFont(textField, 17);
		textField.setPreferredSize(new Dimension(200, 28));
		textField.enableRealtimeValidation();
		validator.setValidatedComponent(textField);
		textField.setValidator(validator);

		if (defaultValue != null) {
			textField.setText(defaultValue);
			textField.getValidationStatus();
		}

		JPanel textFieldPanel = new JPanel(new BorderLayout(0, 0));
		textFieldPanel.add("Center", textField);

		if (optionalNorthComponent != null) {
			textFieldPanel.add("North", optionalNorthComponent);
		}

		if (optionalSouthComponent != null) {
			textFieldPanel.add("South", optionalSouthComponent);
		}

		inp.add("North", new JLabel(text));
		inp.add("Center", textFieldPanel);

		textField.addAncestorListener(new AncestorListener() {

			@Override public void ancestorRemoved(AncestorEvent event) {
			}

			@Override public void ancestorMoved(AncestorEvent event) {
			}

			@Override public void ancestorAdded(AncestorEvent event) {
				event.getComponent().requestFocusInWindow();
			}

		});

		return showSwingOptionDialog(frame, title, icon, ok, cancel, inp, textField);
	}

	private static @Nullable String showSwingOptionDialog(Window frame, String title, ImageIcon icon, String ok,
			String cancel, JPanel inp, VTextField textField) {
		int option = JOptionPane.showOptionDialog(frame, inp, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, icon, new String[] { ok, cancel }, ok);
		if (option == JOptionPane.OK_OPTION) {
			if (textField.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				return textField.getText();
			} else { // user confirmed, but the validation returned error
				JOptionPane.showMessageDialog(frame,
						L10N.t("dialog.option_pane.invalid_text") + textField.getValidationStatus().getMessage(),
						L10N.t("dialog.option_pane.invalid_input"), JOptionPane.ERROR_MESSAGE);
				return showSwingOptionDialog(frame, title, icon, ok, cancel, inp, textField);
			}
		}

		return null;
	}

}
