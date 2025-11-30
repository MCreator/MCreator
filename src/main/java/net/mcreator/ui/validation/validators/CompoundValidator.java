/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;

import java.util.List;

public class CompoundValidator implements Validator {

	private final List<Validator> validators;

	public CompoundValidator(Validator... validators) {
		this.validators = List.of(validators);
	}

	@Override public ValidationResult validate() {
		ValidationResult result = ValidationResult.PASSED;

		for (Validator validator : validators) {
			ValidationResult tmpResult = validator.validate();
			if (tmpResult.type() == ValidationResult.Type.ERROR)
				return tmpResult; // Return as soon as we find an error
			else if (tmpResult.type() == ValidationResult.Type.WARNING)
				result = tmpResult; // Do not return yet, there might still be errors
		}

		return result;
	}

}
