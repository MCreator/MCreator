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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class PointOfInterestValidator implements Validator {

	private final MCreator mcreator;
	private final MCItemHolder holder;
	private final MCItemHolder exception;
	private final List<MItemBlock> vanillaPointOfInterest;

	public PointOfInterestValidator(MCreator mcreator, MCItemHolder holder, MCItemHolder exception) {
		this.mcreator = mcreator;
		this.holder = holder;
		this.exception = exception;
		this.vanillaPointOfInterest = Arrays.asList(new MItemBlock(mcreator.getWorkspace(), "bee_nest"),
				new MItemBlock(mcreator.getWorkspace(), "beehive"), new MItemBlock(mcreator.getWorkspace(), "bed"),
				new MItemBlock(mcreator.getWorkspace(), "lightning_rod"),
				new MItemBlock(mcreator.getWorkspace(), "lodestone"), new MItemBlock(mcreator.getWorkspace(), "bell"),
				new MItemBlock(mcreator.getWorkspace(), "nether_portal"));
	}

	@Override public ValidationResult validate() {
		if (holder.containsItem()) {
			if (exception.containsItem() && holder.getBlock().equals(exception.getBlock()))
				return ValidationResult.PASSED;
			if (ElementUtil.loadAllPointOfInterest(mcreator.getWorkspace()).stream().toList()
					.contains(holder.getBlock()) || (vanillaPointOfInterest.contains(holder.getBlock())))
				return new Validator.ValidationResult(ValidationResultType.ERROR,
						L10N.t("validator.point_of_interest.unique"));
			return ValidationResult.PASSED;
		}
		return new Validator.ValidationResult(ValidationResultType.ERROR, L10N.t("validators.select_element"));
	}
}
