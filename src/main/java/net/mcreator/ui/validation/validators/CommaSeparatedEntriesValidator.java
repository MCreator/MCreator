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
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.Tuple;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommaSeparatedEntriesValidator implements Validator {

	private final VTextField holder;

	public CommaSeparatedEntriesValidator(VTextField holder) {
		this.holder = holder;
	}

	@Override public ValidationResult validate() {
		if (holder.getText().trim().equals(""))
			return ValidationResult.PASSED;
		try {
			Stream.of(holder.getText().split(",")).map(e -> new Tuple<>(e.split("=")[0], e.split("=")[1]))
					.collect(Collectors.toList());
		} catch (Exception e) {
			return new ValidationResult(ValidationResultType.ERROR, L10N.t("validators.only_number_list"));
		}
		return ValidationResult.PASSED;
	}

}
