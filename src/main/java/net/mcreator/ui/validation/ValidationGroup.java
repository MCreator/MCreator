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

package net.mcreator.ui.validation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ValidationGroup {

	protected final List<IValidable> validationElements = new ArrayList<>();

	public final <T extends JComponent & IValidable> ValidationGroup addValidationElement(T validable) {
		validationElements.add(validable);
		return this;
	}

	public final <T extends JTextField & IValidable> ValidationGroup addValidationElement(T validable) {
		validationElements.add(validable);
		return this;
	}

	public final boolean validateIsErrorFree() {
		return getGroupedValidationResults().stream()
				.noneMatch(e -> e.getValidationResultType() == Validator.ValidationResultType.ERROR);
	}

	public final List<String> getValidationProblemMessages() {
		return getGroupedValidationResults().stream().map(Validator.ValidationResult::getMessage).toList();
	}

	public List<Validator.ValidationResult> getGroupedValidationResults() {
		return validationElements.stream().map(IValidable::getValidationStatus)
				.filter(e -> e.getValidationResultType() != Validator.ValidationResultType.PASSED).toList();
	}

}
