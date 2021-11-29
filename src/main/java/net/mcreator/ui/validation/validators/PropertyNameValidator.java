/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import javax.swing.*;
import java.util.List;
import java.util.function.Supplier;

public class PropertyNameValidator implements Validator {

	private final String name;
	private final JTextField holder;
	private final Supplier<List<String>> properties;
	private final Validator extraValidator;

	public PropertyNameValidator(VTextField holder, String name, Supplier<List<String>> properties) {
		this(holder, name, properties, null);
	}

	public PropertyNameValidator(VTextField holder, String name, Supplier<List<String>> properties,
			Validator extraValidator) {
		this.name = name;
		this.holder = holder;
		this.properties = properties;
		this.extraValidator = extraValidator;
	}

	@Override public ValidationResult validate() {
		if (properties.get().stream().filter(holder.getText()::equals).count() > 1)
			return new ValidationResult(ValidationResultType.ERROR, L10N.t("validators.property_name.duplicate", name));
		return extraValidator.validate();
	}

}
