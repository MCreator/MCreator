/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.mcp.tools.utils;

import net.mcreator.java.JavaConventions;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;

public final class ModElementNameValidation {

	private ModElementNameValidation() {}

	public static String normalizeAndValidateName(Workspace workspace, @Nullable String name) {
		String canonical = normalize(name);
		for (String usedName : workspace.getWorkspaceInfo().getUsedElementNames()) {
			if (usedName.equalsIgnoreCase(canonical)) {
				throw new IllegalArgumentException("Mod element with this name already exists");
			}
		}
		return canonical;
	}

	private static String normalize(@Nullable String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name must be provided");
		}

		String canonical = JavaConventions.convertToValidClassName(name.trim());
		if (canonical == null || canonical.isEmpty()) {
			throw new IllegalArgumentException("Invalid mod element name");
		}
		if (!JavaConventions.isValidJavaIdentifier(canonical)) {
			throw new IllegalArgumentException("Invalid mod element name: not a valid Java identifier");
		}
		if (JavaConventions.isStringReservedJavaWord(canonical)) {
			throw new IllegalArgumentException("Invalid mod element name: contains reserved Java keyword");
		}
		if (JavaMemberNameValidator.VANILLA_NAMES.contains(canonical)) {
			throw new IllegalArgumentException(
					"Invalid mod element name: do not use vanilla names, this can cause build problems");
		}
		if (JavaConventions.containsInvalidJavaNameCharacters(canonical)) {
			throw new IllegalArgumentException("Invalid mod element name: contains characters that are not allowed");
		}
		if (!StringUtils.isUppercaseLetter(canonical.charAt(0))) {
			throw new IllegalArgumentException(
					"Invalid mod element name: must start with an uppercase letter (CamelCase)");
		}

		return canonical;
	}

}
