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

import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

public class TextFieldValidator implements Validator {

	private final VTextField holder;
	private final String emptyMessage;
	private final ValidationResult.Type answer;

	public TextFieldValidator(VTextField holder, String emptyMessage) {
		this(holder, emptyMessage, ValidationResult.Type.ERROR);
	}

	public TextFieldValidator(VTextField holder, String emptyMessage, ValidationResult.Type answer) {
		this.holder = holder;
		this.emptyMessage = emptyMessage;
		this.answer = answer;
	}

	@Override public ValidationResult validate() {
		if (!holder.getText().isBlank())
			return ValidationResult.PASSED;
		else
			return new ValidationResult(answer, emptyMessage);
	}
}
