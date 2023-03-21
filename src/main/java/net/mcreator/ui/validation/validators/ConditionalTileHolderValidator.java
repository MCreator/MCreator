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

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.Validator;

import javax.swing.*;

public class ConditionalTileHolderValidator implements Validator {

	private final TextureHolder holder;
	private final JComboBox<String> requirement;
	private final String requirementValue;
	private final boolean validateConditionWhenBooleanIs;

	public ConditionalTileHolderValidator(TextureHolder holder, JComboBox<String> requirement, String requirementValue) {
		this(holder, requirement, requirementValue, true);
	}

	public ConditionalTileHolderValidator(TextureHolder holder, JComboBox<String> requirement, String requirementValue, boolean validateConditionWhenBooleanIs) {
		this.holder = holder;
		this.requirement = requirement;
		this.requirementValue = requirementValue;
		this.validateConditionWhenBooleanIs = validateConditionWhenBooleanIs;
	}

	@Override public ValidationResult validate() {
		if (holder.has() || (requirement.getSelectedItem().equals(requirementValue) != validateConditionWhenBooleanIs))
			return Validator.ValidationResult.PASSED;
		else
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validator.texture_needed"));
	}
}
