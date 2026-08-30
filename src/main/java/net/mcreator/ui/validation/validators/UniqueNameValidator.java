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
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * While serving as a wrapper for the main component validator (if specified), a unique name validator also checks
 * if that component provides a non-empty identifier that has no duplicates in the given elements list.
 */
public class UniqueNameValidator implements Validator {

	private final String name;
	private final Supplier<String> uniqueNameGetter;

	private final Supplier<Stream<String>> otherNames;
	private Supplier<Boolean> isPresentOnList;
	private boolean ignoreCase;
	private final Collection<String> forbiddenNames;

	/**
	 * @param name             The text used to describe the purpose of the holder.
	 * @param uniqueNameGetter Supplier to get unique name from the holder's text.
	 * @param otherNames       Supplier of names of other elements in the same list. Those must all be unique names.
	 */
	public UniqueNameValidator(String name, Supplier<String> uniqueNameGetter, Supplier<Stream<String>> otherNames) {
		this(name, uniqueNameGetter, otherNames, Collections.emptyList());
	}

	/**
	 * @param name             The text used to describe the purpose of the holder.
	 * @param uniqueNameGetter Supplier to get unique name from the holder's text.
	 * @param otherNames       Supplier of names of other elements in the same list. Those must all be unique names.
	 * @param forbiddenNames   List of strings that must not be used as a name, e.g. names of built-in properties.
	 */
	public UniqueNameValidator(String name, Supplier<String> uniqueNameGetter, Supplier<Stream<String>> otherNames,
			Collection<String> forbiddenNames) {
		this.name = name;
		this.uniqueNameGetter = uniqueNameGetter;
		this.otherNames = otherNames;
		this.isPresentOnList = () -> true;
		this.ignoreCase = false;
		this.forbiddenNames = forbiddenNames;
	}

	/**
	 * Use this method to define if the validated name is present on {@link UniqueNameValidator#otherNames} list.
	 *
	 * @param isPresentOnList Whether the validated name is present on {@code otherNames} list.
	 * @return This validator instance with {@code isPresentOnList} parameter set to passed value.
	 */
	public UniqueNameValidator setIsPresentOnList(boolean isPresentOnList) {
		return setIsPresentOnList(() -> isPresentOnList);
	}

	/**
	 * Use this method to define if the validated name is present on {@link UniqueNameValidator#otherNames} list.
	 *
	 * @param isPresentOnList Supplier that controls whether the validated name is present on {@code otherNames} list.
	 * @return This validator instance with {@code isPresentOnList} parameter set to passed value.
	 */
	public UniqueNameValidator setIsPresentOnList(Supplier<Boolean> isPresentOnList) {
		this.isPresentOnList = isPresentOnList;
		return this;
	}

	/**
	 * Use this method to define if case of validated name doesn't have to match case of
	 * {@link UniqueNameValidator#otherNames}.
	 *
	 * @param ignoreCase Whether case of validated name doesn't have to match case of {@code otherNames}.
	 * @return This validator instance with {@code ignoreCase} parameter set to passed value.
	 */
	public UniqueNameValidator setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	private Predicate<String> textCheck(String name) {
		return ignoreCase ? name::equalsIgnoreCase : name::equals;
	}

	@Override public ValidationResult validate() {
		String uniqueName = uniqueNameGetter.get();
		if (uniqueName == null || uniqueName.isEmpty())
			return new ValidationResult(ValidationResult.Type.ERROR, L10N.t("validators.unique_name.empty", name));
		if (otherNames.get().filter(textCheck(uniqueName)).count() > (isPresentOnList.get() ? 1 : 0)
				|| forbiddenNames.contains(uniqueName))
			return new ValidationResult(ValidationResult.Type.ERROR, L10N.t("validators.unique_name.duplicate", name));

		return ValidationResult.PASSED;
	}

}
