/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.Validator;

public class PointOfInterestValidator implements Validator {

	private final MCreator mcreator;
	private final MCItemHolder holder;
	private final MCItemHolder exception;

	public PointOfInterestValidator(MCreator mcreator, MCItemHolder holder, MCItemHolder exception) {
		this.mcreator = mcreator;
		this.holder = holder;
		this.exception = exception;
	}

	@Override public ValidationResult validate() {
		if (holder.containsItem()) {
			if (exception.containsItem() && holder == exception)
				return ValidationResult.PASSED;
			if (ElementUtil.loadAllPointOfInterest(mcreator.getWorkspace()).stream().toList()
					.contains(holder.getBlock()))
				return new Validator.ValidationResult(ValidationResultType.ERROR,
						L10N.t("validator.point_of_interest.unique"));
			return ValidationResult.PASSED;
		}
		return new Validator.ValidationResult(ValidationResultType.ERROR, L10N.t("validators.select_element"));
	}
}
