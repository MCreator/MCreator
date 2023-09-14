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

package net.mcreator.ui.validation.validators;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class RegistryNameValidator implements Validator {

	protected final String name;
	private final JTextField holder;

	private boolean allowEmpty = false;

	private List<Character> validChars = Collections.singletonList('_');

	private int maxLength = 64;

	public RegistryNameValidator(VTextField holder, String name) {
		this.name = name;
		this.holder = holder;
	}

	public RegistryNameValidator(VComboBox<?> holder, String name) {
		this.name = name;
		this.holder = (JTextField) holder.getEditor().getEditorComponent();
	}

	public RegistryNameValidator setValidChars(List<Character> validChars) {
		this.validChars = validChars;
		return this;
	}

	public RegistryNameValidator setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public RegistryNameValidator setAllowEmpty(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
		return this;
	}

	protected String getText() {
		return holder.getText();
	}

	@Override public ValidationResult validate() {
		String text = holder.getText();
		if (text.isEmpty() && !allowEmpty)
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validators.registry_name.empty", name));
		if (text.length() > maxLength)
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validators.registry_name.length", name, maxLength));
		char[] chars = text.toCharArray();
		boolean valid = true;
		int id = 0;
		for (char c : chars) {
			if (id == 0 && (c >= '0' && c <= '9' || validChars.contains(c))) {
				valid = false;
				break;
			}

			if (!isLCLetterOrDigit(c) && !validChars.contains(c)) {
				valid = false;
				break;
			}

			id++;
		}
		if (!valid)
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validators.registry_name.invalid", name, validChars.toString()));

		return Validator.ValidationResult.PASSED;
	}

	public static boolean isLCLetterOrDigit(char c) {
		return StringUtils.isLowercaseLetter(c) || (c >= '0' && c <= '9');
	}

}
