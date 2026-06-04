/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.file;

import java.util.List;
import java.util.Objects;

/**
 * Represents a file extension filter used in file dialogs or selectors.
 * <p>
 * Each filter consists of a description (e.g., "Image Files") and one or more
 * allowed file extensions (e.g., "png", "jpg").
 * </p>
 */
public record ExtensionFilter(String description, List<String> extensions) {

	/**
	 * Constructs an {@code ExtensionFilter} with a description and a variable
	 * number of extensions.
	 *
	 * @param description a short text describing the type of files (e.g., "Images")
	 * @param extensions  one or more allowed file extensions (e.g., "png", "jpg")
	 * @throws NullPointerException     if {@code description} is {@code null}
	 * @throws IllegalArgumentException if {@code description} is empty,
	 *                                  or if {@code extensions} is empty or contains empty strings
	 */
	public ExtensionFilter(String description, String... extensions) {
		this(description, List.of(extensions));
	}

	/**
	 * Validates the provided description and extension list.
	 *
	 * @param description the filter description
	 * @param extensions  the list of allowed extensions
	 * @throws NullPointerException     if {@code description} is {@code null}
	 * @throws IllegalArgumentException if arguments are invalid
	 */
	private static void validateArguments(String description, List<String> extensions) {
		Objects.requireNonNull(description, "Description must not be null");

		if (description.isEmpty()) {
			throw new IllegalArgumentException("Description must not be empty");
		}
		if (extensions.isEmpty()) {
			throw new IllegalArgumentException("At least one extension must be defined");
		}
		for (String extension : extensions) {
			if (extension.isEmpty()) {
				throw new IllegalArgumentException("Extension must not be empty");
			}
		}
	}
}
