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
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.Validator;

import javax.swing.*;

public class TileHolderValidator implements Validator {

	private JToggleButton requirement;
	private final TextureSelectionButton holder;

	public TileHolderValidator(TextureSelectionButton holder) {
		this.holder = holder;
	}

	public TileHolderValidator(TextureSelectionButton holder, JToggleButton requirement) {
		this.holder = holder;
		this.requirement = requirement;
	}

	@Override public ValidationResult validate() {
		if (holder.hasTexture() || (requirement != null && !requirement.isSelected()))
			return Validator.ValidationResult.PASSED;
		else
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validator.texture_needed"));
	}

}
