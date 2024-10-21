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

package net.mcreator.ui.validation;

import java.awt.*;

public interface Validator {


	enum ValidationResultType {

		PASSED(new Color(0x93c54b)),
		WARNING(new Color(0xf0c948)),
		ERROR(new Color(0xc43b39));

		private final Color color;

		ValidationResultType(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
	}

	class ValidationResult {

		public static final ValidationResult PASSED = new Validator.ValidationResult(
				Validator.ValidationResultType.PASSED, "");

		private ValidationResultType validationResultType;
		private String message = "";

		public ValidationResult(ValidationResultType validationResultType, String message) {
			this.setValidationResultType(validationResultType);
			this.setMessage(message);
		}

		public ValidationResultType getValidationResultType() {
			return validationResultType;
		}

		void setValidationResultType(ValidationResultType validationResultType) {
			this.validationResultType = validationResultType;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	default ValidationResult validateIfEnabled(IValidable validable) {
		if (!validable.isEnabled())
			return ValidationResult.PASSED;

		return this.validate();
	}

	ValidationResult validate();

}
