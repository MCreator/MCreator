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

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;

import java.util.Arrays;

public class NamespaceValidator<T> extends RegistryNameValidator {

	public NamespaceValidator(VTextField holder) {
		super(holder, L10N.t("common.namespace"));
		setValidChars(Arrays.asList('_', '-'));
	}

	public NamespaceValidator(VComboBox<T> holder) {
		super(holder, L10N.t("common.namespace"));
		setValidChars(Arrays.asList('_', '-'));
	}

}
