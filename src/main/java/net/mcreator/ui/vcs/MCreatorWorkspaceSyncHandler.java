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

import net.mcreator.Launcher;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.util.GSONCompare;
import net.mcreator.util.MCreatorVersionNumber;
import net.mcreator.vcs.FileSyncHandle;
import net.mcreator.vcs.ICustomSyncHandler;
import net.mcreator.vcs.diff.*;
import net.mcreator.workspace.TooNewWorkspaceVerisonException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MCreatorWorkspaceSyncHandler implements ICustomSyncHandler {

	private final MCreator mcreator;

	public MCreatorWorkspaceSyncHandler(MCreator mcreator) {
		this.mcreator = mcreator;
	}

	@Override
	public boolean handleSync(Git git, boolean hasMergeConflists, List<FileSyncHandle> handles, boolean dryRun)
			throws GitAPIException, IOException, TooNewWorkspaceVerisonException {
		boolean required_user_action;

		List<FileSyncHandle> unprocessedHandles = new ArrayList<>(handles);

		Workspace localWorkspace = mcreator.getWorkspace();
		Workspace remoteWorkspace = null;
		Workspace baseWorkspace = null;

		boolean conflictsInWorkspaceFile = false;

		// First we check if the remote has any changes on the workspace file
		for (FileSyncHandle handle : handles) {
			if (handle.getBasePath().equals(localWorkspace.getFileManager().getWorkspaceFile().getName())) {
				remoteWorkspace = new Workspace.VirtualWorkspace(localWorkspace, new String(handle.getRemoteBytes()));
				conflictsInWorkspaceFile = handle.isUnmerged();
				if (conflictsInWorkspaceFile)
					baseWorkspace = new Workspace.VirtualWorkspace(localWorkspace, new String(handle.getBaseBytes()));
				unprocessedHandles.remove(handle);
				break;
			}
		}

		// remote workspace could be newer than the latest workspace version supported by this MCreator
		if (remoteWorkspace != null)
			if (remoteWorkspace.getMCreatorVersion() > Launcher.version.versionlong && !MCreatorVersionNumber
					.isBuildNumberDevelopment(remoteWorkspace.getMCreatorVersion()))
				throw new TooNewWorkspaceVerisonException(Long.toString(remoteWorkspace.getMCreatorVersion()));

		Set<MergeHandle<ModElement>> conflictingModElements = new HashSet<>();
		Map<ModElement, List<FileSyncHandle>> conflictingFilesOfModElementMap = new HashMap<>();

		// check for mod element changes
		for (FileSyncHandle handle : handles) {
			if (!handle.isUnmerged()) // if this file is not conflicting/unmerged, we skip it
				continue;

			File file = handle.toFileInWorkspace(localWorkspace, ResultSide.BASE);

			// check if this file is one of the lang files, we skip it as lang files are auto-regenerated
			if (file.getCanonicalPath()
					.startsWith(localWorkspace.getGenerator().getLangFilesRoot().getCanonicalPath())) {
				unprocessedHandles.remove(handle);
				continue;
			}

			// test if this file belongs to mod element definitions folder
			if (file.getCanonicalPath()
					.startsWith(localWorkspace.getFolderManager().getModElementsDir().getCanonicalPath())) {
				String modElement = file.getName().replace(".mod.json", "");
				ModElement testModElementDefinition = localWorkspace.getModElementByName(modElement);
				if (testModElementDefinition != null) {
					conflictingModElements.add(new MergeHandle<>(testModElementDefinition, testModElementDefinition,
							handle.getChangeTypeRelativeToLocal(), handle.getChangeTypeRelativeToRemote()));
					// add conflicting file of mod element to the list
					conflictingFilesOfModElementMap
							.putIfAbsent(testModElementDefinition, new ArrayList<>()); // init list if not already
					conflictingFilesOfModElementMap.get(testModElementDefinition).add(handle);
					unprocessedHandles.remove(handle);
					continue;
				}
			}

			// test if this file belongs to generated code of mod element
			ModElement testGeneratedElements = localWorkspace.getGenerator().getModElementThisFileBelongsTo(file);
			if (testGeneratedElements != null) {
				conflictingModElements.add(new MergeHandle<>(testGeneratedElements, testGeneratedElements,
						handle.getChangeTypeRelativeToLocal(), handle.getChangeTypeRelativeToRemote()));
				// add conflicting file of mod element to the list
				conflictingFilesOfModElementMap
						.putIfAbsent(testGeneratedElements, new ArrayList<>()); // init list if not already
				conflictingFilesOfModElementMap.get(testGeneratedElements).add(handle);
				unprocessedHandles.remove(handle);
			}
		}

		MergeHandle<WorkspaceSettings> workspaceSettingsMergeHandle = null;
		Set<MergeHandle<VariableElement>> conflictingVariableElements = new HashSet<>();
		Set<MergeHandle<SoundElement>> conflictingSoundElements = new HashSet<>();
		Set<MergeHandle<String>> conflictingLangMaps = new HashSet<>();

		if (conflictsInWorkspaceFile) {
			// WORKSPACE SETTINGS
			boolean settingsChangedRemoteToBase = !GSONCompare
					.deepEquals(baseWorkspace.getWorkspaceSettings(), remoteWorkspace.getWorkspaceSettings());
			boolean settingsChangedLocalToBase = !GSONCompare
					.deepEquals(baseWorkspace.getWorkspaceSettings(), localWorkspace.getWorkspaceSettings());

			// settings changed local to base and remote to base, we have conflict
			if (settingsChangedRemoteToBase && settingsChangedLocalToBase) {
				workspaceSettingsMergeHandle = new MergeHandle<>(localWorkspace.getWorkspaceSettings(),
						remoteWorkspace.getWorkspaceSettings(), DiffEntry.ChangeType.MODIFY,
						DiffEntry.ChangeType.MODIFY);
			}

			// MOD ELEMENTS
			DiffResult<ModElement> modElementListDiffLocalToBase = ListDiff
					.getListDiff(baseWorkspace.getModElements(), localWorkspace.getModElements());
			DiffResult<ModElement> modElementListDiffRemoteToBase = ListDiff
					.getListDiff(baseWorkspace.getModElements(), remoteWorkspace.getModElements());

			conflictingModElements.addAll(DiffResultToBaseConflictFinder.findConflicts(modElementListDiffLocalToBase,
					modElementListDiffRemoteToBase)); // add all that were affected on both diffs to conflicting list

			if (!dryRun) {
				localWorkspace.getModElementManager().invalidateCache();

				// first we remove local to base, skipping conflicted elements
				for (ModElement removedElement : modElementListDiffLocalToBase.getRemoved()) {
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingModElements, removedElement))
						baseWorkspace.removeModElement(removedElement);
				}

				// then we remove remote to base, skipping conflicted elements
				for (ModElement removedElement : modElementListDiffRemoteToBase.getRemoved()) {
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingModElements, removedElement))
						baseWorkspace.removeModElement(removedElement);
				}

				// then we add local to base, skipping conflicted elements
				for (ModElement addedElement : modElementListDiffLocalToBase.getAdded()) {
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingModElements, addedElement)) {
						baseWorkspace.addModElement(addedElement);
						GeneratableElement generatableElement = addedElement.getGeneratableElement();
						if (generatableElement != null) {
							baseWorkspace.getGenerator().generateElement(
									generatableElement); // regenerate this mod element to reduce conflicts number
						}
					}
				}

				// then we add remote to base, skipping conflicted elements
				for (ModElement addedElement : modElementListDiffRemoteToBase.getAdded()) {
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingModElements, addedElement)) {
						baseWorkspace.addModElement(addedElement);
						GeneratableElement generatableElement = addedElement.getGeneratableElement();
						if (generatableElement != null) {
							baseWorkspace.getGenerator().generateElement(
									generatableElement); // regenerate this mod element to reduce conflicts number
							localWorkspace.getModElementManager().storeModElementPicture(
									generatableElement); // we regenerate mod element images as we do not have remote images yet
						}
					}
				}
			}

			// VARIABLE ELEMENTS (same concept as for mod elements)
			DiffResult<VariableElement> variableElementListDiffLocalToBase = ListDiff
					.getListDiff(baseWorkspace.getVariableElements(), localWorkspace.getVariableElements());
			DiffResult<VariableElement> variableElementListDiffRemoteToBase = ListDiff
					.getListDiff(baseWorkspace.getVariableElements(), remoteWorkspace.getVariableElements());

			conflictingVariableElements.addAll(DiffResultToBaseConflictFinder
					.findConflicts(variableElementListDiffLocalToBase, variableElementListDiffRemoteToBase));

			if (!dryRun) {
				for (VariableElement removedVariableElement : variableElementListDiffLocalToBase.getRemoved())
					if (MergeHandle
							.isElementNotInMergeHandleCollection(conflictingVariableElements, removedVariableElement))
						baseWorkspace.removeVariableElement(removedVariableElement);

				for (VariableElement removedVariableElement : variableElementListDiffRemoteToBase.getRemoved())
					if (MergeHandle
							.isElementNotInMergeHandleCollection(conflictingVariableElements, removedVariableElement))
						baseWorkspace.removeVariableElement(removedVariableElement);

				for (VariableElement addedVariableElement : variableElementListDiffLocalToBase.getAdded())
					if (MergeHandle
							.isElementNotInMergeHandleCollection(conflictingVariableElements, addedVariableElement))
						baseWorkspace.addVariableElement(addedVariableElement);

				for (VariableElement addedVariableElement : variableElementListDiffRemoteToBase.getAdded())
					if (MergeHandle
							.isElementNotInMergeHandleCollection(conflictingVariableElements, addedVariableElement))
						baseWorkspace.addVariableElement(addedVariableElement);
			}

			// SOUND ELEMENTS (same concept as for mod elements)
			DiffResult<SoundElement> soundElementListDiffLocalToBase = ListDiff
					.getListDiff(baseWorkspace.getSoundElements(), localWorkspace.getSoundElements());
			DiffResult<SoundElement> soundElementListDiffRemoteToBase = ListDiff
					.getListDiff(baseWorkspace.getSoundElements(), remoteWorkspace.getSoundElements());

			conflictingSoundElements.addAll(DiffResultToBaseConflictFinder
					.findConflicts(soundElementListDiffLocalToBase, soundElementListDiffRemoteToBase));

			if (!dryRun) {
				for (SoundElement removedSoundElement : soundElementListDiffLocalToBase.getRemoved())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingSoundElements, removedSoundElement))
						baseWorkspace.removeSoundElement(removedSoundElement);

				for (SoundElement removedSoundElement : soundElementListDiffRemoteToBase.getRemoved())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingSoundElements, removedSoundElement))
						baseWorkspace.removeSoundElement(removedSoundElement);

				for (SoundElement addedSoundElement : soundElementListDiffLocalToBase.getAdded())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingSoundElements, addedSoundElement))
						baseWorkspace.addSoundElement(addedSoundElement);

				for (SoundElement addedSoundElement : soundElementListDiffRemoteToBase.getAdded())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingSoundElements, addedSoundElement))
						baseWorkspace.addSoundElement(addedSoundElement);
			}

			// ID MAP (always silent, just increment to from common base)
			if (!dryRun) {
				Map<ModElementType.BaseType, Integer> base_id_map = baseWorkspace.getIDMap();
				Map<ModElementType.BaseType, Integer> local_id_map = localWorkspace.getIDMap();
				Map<ModElementType.BaseType, Integer> remote_id_map = remoteWorkspace.getIDMap();
				for (Map.Entry<ModElementType.BaseType, Integer> base_mapping : base_id_map.entrySet()) {
					int baseid = base_mapping.getValue();
					int localid = local_id_map.get(base_mapping.getKey());
					int remoteid = remote_id_map.get(base_mapping.getKey());
					int newid = baseid + Math.max(0, remoteid - baseid) + Math.max(0, localid - baseid);
					baseWorkspace.getIDMap().put(base_mapping.getKey(), newid);
				}

				// after we merge exising IDs, we add any possibly new IDs from remote
				for (Map.Entry<ModElementType.BaseType, Integer> remote_mapping : remote_id_map.entrySet()) {
					baseWorkspace.getIDMap().putIfAbsent(remote_mapping.getKey(),
							remote_mapping.getValue()); // we only put directly from remote
					// if there is no local mapping for this value yet
				}
			}

			// LANGUAGE MAP
			Map<String, ConcurrentHashMap<String, String>> base_language_map = baseWorkspace.getLanguageMap();
			Map<String, ConcurrentHashMap<String, String>> local_language_map = localWorkspace.getLanguageMap();
			Map<String, ConcurrentHashMap<String, String>> remote_language_map = remoteWorkspace.getLanguageMap();

			DiffResult<String> langMapDiffLocalToBase = MapDiff.getMapDiff(base_language_map, local_language_map);
			DiffResult<String> langMapDiffRemoteToBase = MapDiff.getMapDiff(base_language_map, remote_language_map);

			conflictingLangMaps.addAll(DiffResultToBaseConflictFinder
					.findConflicts(langMapDiffLocalToBase, langMapDiffRemoteToBase));

			if (!dryRun) {
				for (String removedLangMap : langMapDiffLocalToBase.getRemoved())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangMaps, removedLangMap))
						baseWorkspace.getLanguageMap().remove(removedLangMap);

				for (String removedLangMap : langMapDiffRemoteToBase.getRemoved())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangMaps, removedLangMap))
						baseWorkspace.getLanguageMap().remove(removedLangMap);

				for (String addedLangMap : langMapDiffLocalToBase.getAdded())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangMaps, addedLangMap))
						if (localWorkspace.getLanguageMap().get(addedLangMap) != null)
							baseWorkspace.getLanguageMap()
									.put(addedLangMap, localWorkspace.getLanguageMap().get(addedLangMap));

				for (String addedLangMap : langMapDiffRemoteToBase.getAdded())
					if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangMaps, addedLangMap))
						if (remoteWorkspace.getLanguageMap().get(addedLangMap) != null)
							baseWorkspace.getLanguageMap()
									.put(addedLangMap, remoteWorkspace.getLanguageMap().get(addedLangMap));
			}

			Set<MergeHandle<String>> conflictingLangMapsTmp = new HashSet<>(conflictingLangMaps);
			for (MergeHandle<String> langMergeHandle : conflictingLangMapsTmp) {
				// we can only merge automatically modify type changes, we can't merge
				if (langMergeHandle.getLocalChange() == DiffEntry.ChangeType.MODIFY
						&& langMergeHandle.getRemoteChange() == DiffEntry.ChangeType.MODIFY) {
					String language = langMergeHandle.getLocal();

					ConcurrentHashMap<String, String> base_translation = base_language_map.get(language);
					ConcurrentHashMap<String, String> local_translation = local_language_map.get(language);
					ConcurrentHashMap<String, String> remote_translation = remote_language_map.get(language);

					DiffResult<String> langMapContentsDiffLocalToBase = MapDiff
							.getMapDiff(base_translation, local_translation);
					DiffResult<String> langMapContentsDiffRemoteToBase = MapDiff
							.getMapDiff(base_translation, remote_translation);

					Set<MergeHandle<String>> conflictingLangEntries = DiffResultToBaseConflictFinder
							.findConflicts(langMapContentsDiffLocalToBase, langMapContentsDiffRemoteToBase);

					for (String removedLangEntry : langMapContentsDiffLocalToBase.getRemoved())
						if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangEntries, removedLangEntry))
							baseWorkspace.removeLocalizationEntryByKey(removedLangEntry);

					for (String removedLangEntry : langMapContentsDiffRemoteToBase.getRemoved())
						if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangEntries, removedLangEntry))
							baseWorkspace.removeLocalizationEntryByKey(removedLangEntry);

					for (String addedLangEntry : langMapContentsDiffLocalToBase.getAdded())
						if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangEntries, addedLangEntry))
							baseWorkspace.getLanguageMap().get(language).put(addedLangEntry,
									localWorkspace.getLanguageMap().get(language).get(addedLangEntry));

					for (String addedLangEntry : langMapContentsDiffRemoteToBase.getAdded())
						if (MergeHandle.isElementNotInMergeHandleCollection(conflictingLangEntries, addedLangEntry))
							baseWorkspace.getLanguageMap().get(language).put(addedLangEntry,
									remoteWorkspace.getLanguageMap().get(language).get(addedLangEntry));

					// if it can merge silently (no conflicts), we remove this merge handle from conflicting language maps
					// as we will do auto merge
					if (conflictingLangEntries.isEmpty())
						conflictingLangMaps.remove(langMergeHandle);
				}
			}
		}

		// next we can decide if required_user_action will be needed
		boolean workspace_manual_merge_required =
				workspaceSettingsMergeHandle != null || !conflictingModElements.isEmpty() || !conflictingSoundElements
						.isEmpty() || !conflictingVariableElements.isEmpty() || !conflictingLangMaps.isEmpty();

		required_user_action = workspace_manual_merge_required;

		if (!dryRun && workspace_manual_merge_required) {
			// Show workspace merge dialog
			VCSWorkspaceMergeDialog.show(mcreator,
					new WorkspaceMergeHandles(workspaceSettingsMergeHandle, conflictingModElements,
							conflictingVariableElements, conflictingSoundElements, conflictingLangMaps));

			// after UI merge is complete, we apply the merge to the workspace

			if (conflictsInWorkspaceFile && workspaceSettingsMergeHandle != null)
				baseWorkspace.setWorkspaceSettings(workspaceSettingsMergeHandle.getSelectedResult());

			for (MergeHandle<ModElement> modElementMergeHandle : conflictingModElements) {
				if (conflictsInWorkspaceFile) {
					if (modElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.ADD) {
						baseWorkspace.addModElement(modElementMergeHandle.getSelectedResult());
					} else if (modElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.DELETE) {
						baseWorkspace.removeModElement(modElementMergeHandle.getSelectedResult());
					} else if (modElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.MODIFY) {
						baseWorkspace.updateModElement(modElementMergeHandle.getSelectedResult());
					}
				}

				List<FileSyncHandle> modElementFiles = conflictingFilesOfModElementMap
						.get(modElementMergeHandle.getSelectedResult());

				if (modElementFiles != null) {
					for (FileSyncHandle fileSyncHandle : modElementFiles) {
						mergeNormalFile(localWorkspace, fileSyncHandle, modElementMergeHandle);
					}
				}

				// at last, we regenerate these mod elements too
				GeneratableElement generatableElement = modElementMergeHandle.getSelectedResult()
						.getGeneratableElement();
				if (generatableElement != null) {
					// regenerate this mod element to reduce conflicts number, we prefer to use baseWorkspace for this
					if (baseWorkspace != null)
						baseWorkspace.getGenerator().generateElement(generatableElement);
					else
						localWorkspace.getGenerator().generateElement(generatableElement);
					localWorkspace.getModElementManager().storeModElementPicture(
							generatableElement); // we regenerate mod element images as we do not have remote images yet
				}
			}

			if (conflictsInWorkspaceFile) {
				for (MergeHandle<VariableElement> variableElementMergeHandle : conflictingVariableElements) {
					if (variableElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.ADD) {
						baseWorkspace.addVariableElement(variableElementMergeHandle.getSelectedResult());
					} else if (variableElementMergeHandle.getSelectedResultChangeType()
							== DiffEntry.ChangeType.DELETE) {
						baseWorkspace.removeVariableElement(variableElementMergeHandle.getSelectedResult());
					} else if (variableElementMergeHandle.getSelectedResultChangeType()
							== DiffEntry.ChangeType.MODIFY) {
						baseWorkspace.updateVariableElement(variableElementMergeHandle.getSelectedResult(),
								variableElementMergeHandle.getSelectedResult());
					}
				}

				for (MergeHandle<SoundElement> soundElementMergeHandle : conflictingSoundElements) {
					if (soundElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.ADD) {
						baseWorkspace.addSoundElement(soundElementMergeHandle.getSelectedResult());
					} else if (soundElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.DELETE) {
						baseWorkspace.removeSoundElement(soundElementMergeHandle.getSelectedResult());
					} else if (soundElementMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.MODIFY) {
						baseWorkspace.updateSoundElement(soundElementMergeHandle.getSelectedResult(),
								soundElementMergeHandle.getSelectedResult());
					}
				}

				for (MergeHandle<String> langMapMergeHandle : conflictingLangMaps) {
					if (langMapMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.ADD) {
						if (langMapMergeHandle.getResultSide() == ResultSide.LOCAL) {
							baseWorkspace.addLanguage(langMapMergeHandle.getSelectedResult(),
									localWorkspace.getLanguageMap().get(langMapMergeHandle.getSelectedResult()));
						} else if (langMapMergeHandle.getResultSide() == ResultSide.REMOTE) {
							baseWorkspace.addLanguage(langMapMergeHandle.getSelectedResult(),
									remoteWorkspace.getLanguageMap().get(langMapMergeHandle.getSelectedResult()));
						}
					} else if (langMapMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.DELETE) {
						baseWorkspace.removeLocalizationLanguage(langMapMergeHandle.getSelectedResult());
					} else if (langMapMergeHandle.getSelectedResultChangeType() == DiffEntry.ChangeType.MODIFY) {
						if (langMapMergeHandle.getResultSide() == ResultSide.LOCAL) {
							baseWorkspace.updateLanguage(langMapMergeHandle.getSelectedResult(),
									localWorkspace.getLanguageMap().get(langMapMergeHandle.getSelectedResult()));
						} else if (langMapMergeHandle.getResultSide() == ResultSide.REMOTE) {
							baseWorkspace.updateLanguage(langMapMergeHandle.getSelectedResult(),
									remoteWorkspace.getLanguageMap().get(langMapMergeHandle.getSelectedResult()));
						}
					}
				}
			}
		}

		// if remote workspace was not null, we might have a merge so we set local workspace to after merge state
		if (conflictsInWorkspaceFile && !dryRun) {
			// local workspace is not at the same state as merged base workspace
			localWorkspace.loadStoredDataFrom(baseWorkspace);

			// to be sure, we save workspace and load it back from file
			localWorkspace.getFileManager().saveWorkspaceDirectlyAndWait();
			localWorkspace.reloadFromFS();
		}

		// process workspace base files
		List<GeneratorTemplate> modBaseTemplates = localWorkspace.getGenerator().getModBaseGeneratorTemplatesList(true);
		for (GeneratorTemplate generatorTemplate : modBaseTemplates) {
			if (((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock") != null
					&& ((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock")
					.equals("true")) // can this file be locked
				if (localWorkspace.getWorkspaceSettings()
						.isLockBaseModFiles()) // are mod base file locked in local workspace
					continue; // if they are, we skip this file

			for (FileSyncHandle handle : handles) {
				if (isVCSPathThisFile(localWorkspace, handle.getBasePath(), generatorTemplate.getFile())) {
					unprocessedHandles.remove(handle);
					if (!dryRun)
						generatorTemplate.getFile().delete();
				}
			}
		}
		if (!dryRun)
			localWorkspace.getGenerator().generateBase(); // regenerate mod base for state after merge

		// mark all handles that do not have conflicts as merged at this point
		// as now we only need to process remaining unmerged paths
		for (FileSyncHandle handle : handles)
			if (!handle.isUnmerged())
				unprocessedHandles.remove(handle);

		if (!required_user_action) // if not marked as required_user_action yet, we might do this now
			// if we have unmerged files at this point, we will need user action to merge them
			required_user_action = !unprocessedHandles.isEmpty();

		if (!dryRun && !unprocessedHandles.isEmpty()) {
			List<MergeHandle<FileSyncHandle>> unmergedPaths = unprocessedHandles.stream()
					.map(FileSyncHandle::toPathMergeHandle).collect(Collectors.toList());

			VCSFileMergeDialog.show(mcreator, unmergedPaths);

			for (MergeHandle<FileSyncHandle> unmergedPath : unmergedPaths) {
				FileSyncHandle fileSyncHandle = unmergedPath.getLocal();
				mergeNormalFile(localWorkspace, fileSyncHandle, unmergedPath);
			}
		}

		// At the end of sync/merge, we mark all handles resolved, if it is not a dry run
		if (!dryRun) {
			git.rm().addFilepattern(".").call();
			git.add().addFilepattern(".").call();
		}

		return required_user_action;
	}

	private boolean isVCSPathThisFile(Workspace workspace, String vcsPath, File file) throws IOException {
		return file.getCanonicalPath().equals(new File(workspace.getWorkspaceFolder(), vcsPath).getCanonicalPath());
	}

	private void mergeNormalFile(Workspace workspace, FileSyncHandle fileSyncHandle, MergeHandle<?> mergeHandle) {
		if (fileSyncHandle.getChangeTypeRelativeTo(mergeHandle.getResultSide()) == DiffEntry.ChangeType.ADD
				|| fileSyncHandle.getChangeTypeRelativeTo(mergeHandle.getResultSide()) == DiffEntry.ChangeType.MODIFY) {
			FileIO.writeBytesToFile(fileSyncHandle.getBytes(mergeHandle.getResultSide()),
					fileSyncHandle.toFileInWorkspace(workspace, mergeHandle.getResultSide()));
		} else if (fileSyncHandle.getChangeTypeRelativeTo(mergeHandle.getResultSide()) == DiffEntry.ChangeType.DELETE) {
			File file = fileSyncHandle.toFileInWorkspace(workspace, mergeHandle.getResultSide());
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				FileIO.deleteDir(file);
		}
	}

}
