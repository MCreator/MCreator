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
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * While serving as a wrapper for the main validator of the text field instance, a unique name validator also checks
 * that the provided text field defines a non-empty name that has no duplicates in the given elements list.
 */
public class UniqueNameValidator implements Validator {

	private final String name;
	private final JTextField holder;

	private final Function<String, String> uniqueNameGetter;
	private final Supplier<Stream<String>> otherNames;
	private boolean isPresentOnList;
	private final List<String> forbiddenNames;

	private final Validator extraValidator;

	/**
	 * @param holder         The element to add this validator to.
	 * @param name           The text used to describe the purpose of the {@code holder}.
	 * @param otherNames     Supplier of names of other elements in the same list. Those must all be unique names.
	 * @param extraValidator The main validator for the {@code holder}.
	 */
	public UniqueNameValidator(VTextField holder, String name, Supplier<Stream<String>> otherNames,
			Validator extraValidator) {
		this(holder, name, e -> e, otherNames, Collections.emptyList(), extraValidator);
	}

	/**
	 * @param holder         The element to add this validator to.
	 * @param name           The text used to describe the purpose of the {@code holder}.
	 * @param otherNames     Supplier of names of other elements in the same list. Those must all be unique names.
	 * @param forbiddenNames List of strings that must not be used as a name, e.g. names of built-in properties.
	 * @param extraValidator The main validator for the {@code holder}.
	 */
	public UniqueNameValidator(VTextField holder, String name, Supplier<Stream<String>> otherNames,
			List<String> forbiddenNames, Validator extraValidator) {
		this(holder, name, e -> e, otherNames, forbiddenNames, extraValidator);
	}

	/**
	 * @param holder           The element to add this validator to.
	 * @param name             The text used to describe the purpose of the {@code holder}.
	 * @param uniqueNameGetter The function to get unique name from the {@code holder}'s text.
	 * @param otherNames       Supplier of names of other elements in the same list. Those must all be unique names.
	 * @param extraValidator   The main validator for the {@code holder}.
	 */
	public UniqueNameValidator(VTextField holder, String name, Function<String, String> uniqueNameGetter,
			Supplier<Stream<String>> otherNames, Validator extraValidator) {
		this(holder, name, uniqueNameGetter, otherNames, Collections.emptyList(), extraValidator);
	}

	/**
	 * @param holder           The element to add this validator to.
	 * @param name             The text used to describe the purpose of the {@code holder}.
	 * @param uniqueNameGetter The function to get unique name from the {@code holder}'s text.
	 * @param otherNames       Supplier of names of other elements in the same list. Those must all be unique names.
	 * @param forbiddenNames   List of strings that must not be used as a name, e.g. names of built-in properties.
	 * @param extraValidator   The main validator for the {@code holder}.
	 */
	public UniqueNameValidator(VTextField holder, String name, Function<String, String> uniqueNameGetter,
			Supplier<Stream<String>> otherNames, List<String> forbiddenNames, Validator extraValidator) {
		this.name = name;
		this.holder = holder;
		this.uniqueNameGetter = uniqueNameGetter;
		this.otherNames = otherNames;
		this.isPresentOnList = true;
		this.forbiddenNames = forbiddenNames;
		this.extraValidator = extraValidator;
	}

	/**
	 * Use this method to define if the validated name is present on {@link UniqueNameValidator#otherNames} list.
	 *
	 * @param isPresentOnList Whether the validated name is present on {@code otherNames} list.
	 * @return This validator instance with {@code isPresentOnList} parameter set to passed value.
	 */
	public UniqueNameValidator setIsPresentOnList(boolean isPresentOnList) {
		this.isPresentOnList = isPresentOnList;
		return this;
	}

	/**
	 * Returns a copy of this UniqueNameValidator with main validator changed to the passed one.
	 *
	 * @param extraValidator The new main validator for the validated element.
	 */
	public UniqueNameValidator wrapValidator(Validator extraValidator) {
		return new UniqueNameValidator((VTextField) holder, name, uniqueNameGetter, otherNames, forbiddenNames,
				extraValidator).setIsPresentOnList(isPresentOnList);
	}

	/**
	 * Returns the main validator for the validated element.
	 *
	 * @return The main validator for the validated element.
	 */
	public Validator getExtraValidator() {
		return extraValidator;
	}

	@Override public ValidationResult validate() {
		String uniqueName = uniqueNameGetter.apply(holder.getText());
		if (uniqueName == null || uniqueName.equals(""))
			return new ValidationResult(ValidationResultType.ERROR, L10N.t("validators.unique_name.empty", name));
		if (otherNames.get().filter(uniqueName::equals).count() > (isPresentOnList ? 1 : 0) || forbiddenNames.contains(
				uniqueName))
			return new ValidationResult(ValidationResultType.ERROR, L10N.t("validators.unique_name.duplicate"));

		return extraValidator.validate();
	}

}
