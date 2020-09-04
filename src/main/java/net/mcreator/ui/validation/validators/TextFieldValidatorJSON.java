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

import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

public class TextFieldValidatorJSON implements Validator {

	private final VTextField holder;
	private final String warning;
	private final ValidationResultType answer;
	private final boolean canBeEmpty;

	public TextFieldValidatorJSON(VTextField holder, String warning, boolean canBeEmpty) {
		this(holder, warning, ValidationResultType.ERROR, canBeEmpty);
	}

	public TextFieldValidatorJSON(VTextField holder, String warning, ValidationResultType answer, boolean canBeEmpty) {
		this.holder = holder;
		this.warning = warning;
		this.answer = answer;
		this.canBeEmpty = canBeEmpty;
	}

	@Override public ValidationResult validate() {
		if ((!holder.getText().trim().equals("") || canBeEmpty) && !holder.getText().contains("\""))
			return new ValidationResult(ValidationResultType.PASSED, "");
		else
			return new ValidationResult(answer, warning);
	}
}
