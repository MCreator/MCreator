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

import net.mcreator.vcs.diff.DiffResult;
import net.mcreator.vcs.diff.DiffResultToBaseConflictFinder;
import net.mcreator.vcs.diff.ListDiff;
import net.mcreator.vcs.diff.MergeHandle;
import net.mcreator.workspace.elements.FolderElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FolderSyncHandler {

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean mergeFoldersRecursively(FolderElement local, FolderElement remote, FolderElement base,
			boolean dryRun) {
		DiffResult<FolderElement> folderElementListDiffLocalToBase = ListDiff
				.getListDiff(base.getDirectFolderChildren(), local.getDirectFolderChildren());
		DiffResult<FolderElement> folderElementListDiffRemoteToBase = ListDiff
				.getListDiff(base.getDirectFolderChildren(), remote.getDirectFolderChildren());

		Set<MergeHandle<FolderElement>> conflictingFolderElements = DiffResultToBaseConflictFinder
				.findConflicts(folderElementListDiffLocalToBase, folderElementListDiffRemoteToBase);

		if (!conflictingFolderElements.isEmpty())
			return false;

		// prevent duplicate folders (compared by full path), use Set for this
		Set<FolderElement> mergedChildren = new HashSet<>(base.getDirectFolderChildren());

		// process "mergable changes" for the current tree depth
		for (FolderElement removedFolderElement : folderElementListDiffLocalToBase.getRemoved())
			if (MergeHandle.isElementNotInMergeHandleCollection(conflictingFolderElements, removedFolderElement))
				mergedChildren.remove(removedFolderElement);

		for (FolderElement removedFolderElement : folderElementListDiffRemoteToBase.getRemoved())
			if (MergeHandle.isElementNotInMergeHandleCollection(conflictingFolderElements, removedFolderElement))
				mergedChildren.remove(removedFolderElement);

		for (FolderElement addedFolderElement : folderElementListDiffLocalToBase.getAdded())
			if (MergeHandle.isElementNotInMergeHandleCollection(conflictingFolderElements, addedFolderElement))
				mergedChildren.add(addedFolderElement);

		for (FolderElement addedFolderElement : folderElementListDiffRemoteToBase.getAdded())
			if (MergeHandle.isElementNotInMergeHandleCollection(conflictingFolderElements, addedFolderElement))
				mergedChildren.add(addedFolderElement);

		// if not in dry run, update the base workspace with new children
		if (!dryRun)
			base.setChildren(new ArrayList<>(mergedChildren));

		for (FolderElement baseChild : mergedChildren) {
			int localIdx = local.getDirectFolderChildren().indexOf(baseChild);
			int remoteIdx = remote.getDirectFolderChildren().indexOf(baseChild);
			if (localIdx != -1 && remoteIdx != -1) { // folder has common root in all branches
				if (!mergeFoldersRecursively(local.getDirectFolderChildren().get(localIdx),
						remote.getDirectFolderChildren().get(remoteIdx), baseChild, dryRun))
					return false; // return false, if we failed to merge one of the children
			}
		}

		return true;
	}

}
