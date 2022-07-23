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

import javax.swing.*;

public class PointOfInterestValidator implements Validator {

	private final MCreator mcreator;
	private final MCItemHolder holder;
	private JToggleButton requirement;

	public PointOfInterestValidator(MCreator mcreator, MCItemHolder holder) {
		this.mcreator = mcreator;
		this.holder = holder;
	}

	public PointOfInterestValidator(MCreator mcreator, MCItemHolder holder, JToggleButton requirement) {
		this.mcreator = mcreator;
		this.holder = holder;
		this.requirement = requirement;
	}

	@Override public ValidationResult validate() {
		final boolean[] flag = { false };
		ElementUtil.loadAllPointOfInterest(mcreator.getWorkspace()).forEach(mcItem -> {
			if (mcItem.getName().equals(holder.getName()))
				flag[0] = true;
		});
		if (flag[0])
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validator.point_of_interest.unique"));
		if (holder.containsItem() || (requirement != null && !requirement.isSelected()))
			return Validator.ValidationResult.PASSED;
		else
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validators.select_element"));
	}
}
