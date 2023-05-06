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

package net.mcreator.ui.minecraft.states;

import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class JPropertyNameField extends JPanel implements IValidable {
	private final VTextField field = new VTextField(20);
	private String cachedName;

	public JPropertyNameField(String initialPropertyName, Runnable editListener) {
		super(new BorderLayout());

		field.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		field.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				if (field.getValidationStatus() == Validator.ValidationResult.PASSED) {
					editListener.run();
					renameTo(field.getText());
				} else {
					field.setText(cachedName);
				}
				getValidationStatus();
			}
		});
		renameTo(initialPropertyName);
		add(field);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
	}

	public VTextField getTextField() {
		return field;
	}

	public String getPropertyName() {
		return field.getText();
	}

	public String getCachedName() {
		return cachedName;
	}

	public void renameTo(String newName) {
		field.setText(cachedName = newName);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return field.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
		field.setValidator(validator);
	}

	@Override public Validator getValidator() {
		return field.getValidator();
	}
}
