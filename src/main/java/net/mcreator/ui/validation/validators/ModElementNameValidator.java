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

import net.mcreator.java.JavaConventions;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ModElementNameValidator extends UniqueNameValidator {

	private final List<String> usedNames;

	public ModElementNameValidator(@Nonnull Workspace workspace, VTextField textField, String name) {
		this(workspace, textField, name, new ArrayList<>());
	}

	// Private constructor so we can construct UniqueNameValidator with a reference to usedNames stream
	private ModElementNameValidator(@Nonnull Workspace workspace, VTextField textField, String name,
			List<String> usedNames) {
		super(name, () -> JavaConventions.convertToValidClassName(textField.getText()), usedNames::stream,
				new JavaMemberNameValidator(textField, true));
		this.usedNames = usedNames;

		setIsPresentOnList(false);
		setIgnoreCase(true);

		reloadUsedNames(workspace);
	}

	public void reloadUsedNames(@Nonnull Workspace workspace) {
		usedNames.clear();
		usedNames.addAll(workspace.getWorkspaceInfo().getUsedElementNames());
	}

}