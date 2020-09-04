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

import net.mcreator.java.JavaConventions;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.StringUtils;

import java.util.Locale;

public class JavaMemeberNameValidator implements Validator {

	private final VTextField textField;
	private final boolean firstLetterUppercase;

	public JavaMemeberNameValidator(VTextField textField, boolean requireFirstLetterUppercase) {
		this.textField = textField;
		this.firstLetterUppercase = requireFirstLetterUppercase;
	}

	@Override public ValidationResult validate() {
		String text = textField.getText();

		if (text == null || text.length() == 0)
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR, "Name can't be empty");

		if (firstLetterUppercase && Character.isLowerCase(text.charAt(0))) {
			if (text.length() > 1)
				text = text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
			else
				text = text.substring(0, 1).toUpperCase(Locale.ENGLISH);
			textField.setText(text);
		}

		if (!JavaConventions.isValidJavaIdentifier(textField.getText())) {
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR, "Invalid name");
		} else if (JavaConventions.isStringReservedJavaWord(textField.getText())) {
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					"Name contains reserved Java keywords");
		} else if (JavaConventions.containsInvalidJavaNameCharacters(textField.getText())) {
			return new Validator.ValidationResult(Validator.ValidationResultType.WARNING,
					"Name contains characters that will be converted");
		} else if (firstLetterUppercase && textField.getText() != null && textField.getText().length() > 0
				&& !StringUtils.isUppercaseLetter(textField.getText().charAt(0))) {
			return new Validator.ValidationResult(Validator.ValidationResultType.WARNING,
					"Java standards recommend uppercase letter for the first character");
		} else {
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		}
	}

}
