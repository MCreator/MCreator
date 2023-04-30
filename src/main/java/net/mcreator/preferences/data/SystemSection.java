/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.preferences.data;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.FileEntry;
import net.mcreator.workspace.WorkspaceFolderManager;

public class SystemSection extends PreferencesSection {


	public FileEntry defaultWorkspacesFolder;
	public BooleanEntry openLastWorkspace;
	public BooleanEntry confirmBeforeClosing;
	public BooleanEntry returnToWorkspaceSelector;

	public SystemSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		defaultWorkspacesFolder = addEntry(new FileEntry("defaultWorkspacesFolder", WorkspaceFolderManager.getSuggestedWorkspaceFoldersRoot().getAbsolutePath(), true));
		openLastWorkspace = addEntry(new BooleanEntry("openLastWorkspace", false));
		confirmBeforeClosing = addEntry(new BooleanEntry("confirmBeforeClosing", false));
		returnToWorkspaceSelector = addEntry(new BooleanEntry("returnToWorkspaceSelector", false));
	}

	@Override public String getSectionKey() {
		return "system";
	}
}
