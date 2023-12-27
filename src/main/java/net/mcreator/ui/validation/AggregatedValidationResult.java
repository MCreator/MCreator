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

import java.util.*;

public class AggregatedValidationResult extends ValidationGroup {

	private final List<ValidationGroup> validationGroups = new ArrayList<>();

	public AggregatedValidationResult() {
	}

	public AggregatedValidationResult(IValidable... validationElements) {
		this.validationElements.addAll(Arrays.asList(validationElements));
	}

	public AggregatedValidationResult(ValidationGroup... validationGroups) {
		this.validationGroups.addAll(Arrays.asList(validationGroups));
	}

	public AggregatedValidationResult(Collection<ValidationGroup> validationGroups) {
		this.validationGroups.addAll(validationGroups);
	}

	public AggregatedValidationResult addValidationGroup(ValidationGroup validable) {
		validationGroups.add(validable);
		return this;
	}

	@Override public List<Validator.ValidationResult> getGroupedValidationResults() {
		List<Validator.ValidationResult> retval = new ArrayList<>();

		validationElements.stream().map(IValidable::getValidationStatus)
				.filter(e -> e.getValidationResultType() != Validator.ValidationResultType.PASSED).forEach(retval::add);

		validationGroups.stream().filter((e) -> !e.validateIsErrorFree())
				.forEach((e) -> retval.addAll(e.getGroupedValidationResults()));

		return retval;
	}

	public static class PASS extends AggregatedValidationResult {

		@Override public List<Validator.ValidationResult> getGroupedValidationResults() {
			return Collections.emptyList();
		}

	}

	public static class FAIL extends AggregatedValidationResult {

		private final String message;

		public FAIL(String message) {
			this.message = message;
		}

		@Override public List<Validator.ValidationResult> getGroupedValidationResults() {
			return List.of(new Validator.ValidationResult(Validator.ValidationResultType.ERROR, message));
		}

	}

}
