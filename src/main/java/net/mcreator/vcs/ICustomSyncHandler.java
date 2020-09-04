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

package net.mcreator.vcs;

import net.mcreator.workspace.TooNewWorkspaceVerisonException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

public interface ICustomSyncHandler {

	/**
	 * Handles merge conflicts that were not solved by Git using custom merger
	 *
	 * @param git     Git references
	 * @param handles Handles of files with sync/merge info
	 * @param dryRun  True, if merge should only be tested, but not applied
	 * @return true, if the merge required user interaction
	 */
	boolean handleSync(Git git, boolean hasMergeConflists, List<FileSyncHandle> handles, boolean dryRun)
			throws GitAPIException, IOException, TooNewWorkspaceVerisonException;

}
