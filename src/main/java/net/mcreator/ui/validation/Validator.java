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

public interface Validator {

	enum ValidationResultType {
		PASSED, WARNING, ERROR
	}

	class ValidationResult {

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

	ValidationResult validate();

}
