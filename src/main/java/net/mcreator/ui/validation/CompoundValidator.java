/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import java.util.List;

public class CompoundValidator implements Validator {

	private final List<Validator> validators;

	public CompoundValidator(Validator... validators) {
		this.validators = List.of(validators);
	}

	@Override public ValidationResult validate() {
		ValidationResult result = new ValidationResult(ValidationResultType.PASSED, "");

		for (Validator validator : validators) {
			ValidationResult tmpResult = validator.validate();
			if (result.getValidationResultType() == ValidationResultType.PASSED && (
					tmpResult.getValidationResultType() == ValidationResultType.ERROR
							|| tmpResult.getValidationResultType() == ValidationResultType.WARNING))
				result = tmpResult;
			else if (result.getValidationResultType() == ValidationResultType.WARNING
					&& tmpResult.getValidationResultType() == ValidationResultType.ERROR)
				result = tmpResult;
		}

		return result;
	}

}
