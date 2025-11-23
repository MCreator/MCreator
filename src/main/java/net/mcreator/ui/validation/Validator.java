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

		PASSED(new Color(0x93c54b)), WARNING(new Color(0xf0c948)), ERROR(new Color(0xc43b39));

		private final Color color;

		ValidationResultType(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
	}

	class ValidationResult {

		public static final ValidationResult PASSED = new ValidationResult(ValidationResultType.PASSED, "");

		private final ValidationResultType validationResultType;
		private final String message;
		private final boolean isBlocklyResult;

		public ValidationResult(ValidationResultType validationResultType, String message) {
			this(validationResultType, message, false);
		}

		public ValidationResult(ValidationResultType validationResultType, String message, boolean isBlocklyResult) {
			this.validationResultType = validationResultType;
			this.message = message;
			this.isBlocklyResult = isBlocklyResult;
		}

		public ValidationResultType getValidationResultType() {
			return validationResultType;
		}

		public String getMessage() {
			return message;
		}

		public boolean isBlocklyResult() {
			return isBlocklyResult;
		}
	}

	default ValidationResult validateIfEnabled(IValidable validable) {
		if (!validable.isEnabled())
			return ValidationResult.PASSED;

		return this.validate();
	}

	ValidationResult validate();

}
