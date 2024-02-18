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

package net.mcreator.ui.validation.validators;

import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;

import java.util.List;

public class ItemListFieldSingleTagValidator implements Validator {

	private final JItemListField<?> holder;

	public ItemListFieldSingleTagValidator(JItemListField<?> holder) {
		this.holder = holder;
	}

	@Override public ValidationResult validate() {
		List<?> listElements = holder.getListElements();

		if (listElements.size() > 1) {
			for (Object object : listElements) {
				if (object.toString().startsWith("#"))
					return new ValidationResult(ValidationResultType.ERROR, L10N.t("validator.singletag.multiple"));
			}
		}

		return new ValidationResult(ValidationResultType.PASSED, "");
	}
}
