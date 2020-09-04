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
import java.util.*;

public class AggregatedValidationResult extends ValidationGroup {

	private final List<IValidable> validationElements = new ArrayList<>();
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

	public <T extends JComponent & IValidable> void addValidationElement(T validable) {
		validationElements.add(validable);
	}

	public void addValidationGroup(ValidationGroup validable) {
		validationGroups.add(validable);
	}

	@Override public boolean validateIsErrorFree() {
		boolean isErrorFree = true;

		for (IValidable validable : validationElements)
			if (validable.getValidationStatus().getValidationResultType() == Validator.ValidationResultType.ERROR)
				isErrorFree = false;

		for (ValidationGroup validable : validationGroups)
			if (!validable.validateIsErrorFree())
				isErrorFree = false;

		return isErrorFree;
	}

	/**
	 * Returns list of messages of aggregated check with possible HTML elements
	 *
	 * @return List of messages
	 */
	@Override public List<String> getValidationProblemMessages() {
		List<String> retval = new ArrayList<>();

		validationElements.stream().filter((e) -> e.getValidationStatus().getValidationResultType()
				!= Validator.ValidationResultType.PASSED)
				.forEach((e) -> retval.add(e.getValidator().validate().getMessage()));

		validationGroups.stream().filter((e) -> !e.validateIsErrorFree())
				.forEach((e) -> retval.addAll(e.getValidationProblemMessages()));

		return retval;
	}

	public static class PASS extends AggregatedValidationResult {

		@Override public List<String> getValidationProblemMessages() {
			return new ArrayList<>();
		}

		@Override public boolean validateIsErrorFree() {
			return true;
		}
	}

	public static class FAIL extends AggregatedValidationResult {

		private final String message;

		public FAIL(String message) {
			this.message = message;
		}

		@Override public List<String> getValidationProblemMessages() {
			return new ArrayList<>(Collections.singleton(message));
		}

		@Override public boolean validateIsErrorFree() {
			return false;
		}
	}

	public static class MULTIFAIL extends AggregatedValidationResult {

		private final List<String> messages;

		public MULTIFAIL(List<String> messages) {
			this.messages = messages;
		}

		@Override public List<String> getValidationProblemMessages() {
			return messages;
		}

		@Override public boolean validateIsErrorFree() {
			return false;
		}
	}

}
