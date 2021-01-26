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

package net.mcreator.ui.vcs;

import net.mcreator.vcs.diff.MergeHandle;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.settings.WorkspaceSettings;

import java.util.Set;

public class WorkspaceMergeHandles {

	private final MergeHandle<WorkspaceSettings> workspaceSettingsMergeHandle;
	private final Set<MergeHandle<ModElement>> conflictingModElements;
	private final Set<MergeHandle<VariableElement>> conflictingVariableElements;
	private final Set<MergeHandle<SoundElement>> conflictingSoundElements;
	private final Set<MergeHandle<String>> conflictingLangMaps;
	private final MergeHandle<FolderElement> workspaceFoldersMergeHandle;

	public WorkspaceMergeHandles(MergeHandle<WorkspaceSettings> workspaceSettingsMergeHandle,
			Set<MergeHandle<ModElement>> conflictingModElements,
			Set<MergeHandle<VariableElement>> conflictingVariableElements,
			Set<MergeHandle<SoundElement>> conflictingSoundElements, Set<MergeHandle<String>> conflictingLangMaps,
			MergeHandle<FolderElement> workspaceFoldersMergeHandle) {
		this.workspaceSettingsMergeHandle = workspaceSettingsMergeHandle;
		this.conflictingModElements = conflictingModElements;
		this.conflictingVariableElements = conflictingVariableElements;
		this.conflictingSoundElements = conflictingSoundElements;
		this.conflictingLangMaps = conflictingLangMaps;
		this.workspaceFoldersMergeHandle = workspaceFoldersMergeHandle;
	}

	public MergeHandle<WorkspaceSettings> getWorkspaceSettingsMergeHandle() {
		return workspaceSettingsMergeHandle;
	}

	public Set<MergeHandle<ModElement>> getConflictingModElements() {
		return conflictingModElements;
	}

	public Set<MergeHandle<VariableElement>> getConflictingVariableElements() {
		return conflictingVariableElements;
	}

	public Set<MergeHandle<SoundElement>> getConflictingSoundElements() {
		return conflictingSoundElements;
	}

	public Set<MergeHandle<String>> getConflictingLangMaps() {
		return conflictingLangMaps;
	}

	public MergeHandle<FolderElement> getWorkspaceFoldersMergeHandle() {
		return workspaceFoldersMergeHandle;
	}
}
