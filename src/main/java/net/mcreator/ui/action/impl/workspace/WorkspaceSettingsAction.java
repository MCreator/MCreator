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

package net.mcreator.ui.action.impl.workspace;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorTokens;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.StructureUtils;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.WorkspaceSelector;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.gradle.GradleAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.workspace.WorkspaceDialogs;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;

import java.io.File;

public class WorkspaceSettingsAction extends GradleAction {

	public WorkspaceSettingsAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.settings"), e -> {
			WorkspaceSettingsChange change = WorkspaceDialogs
					.workspaceSettings(actionRegistry.getMCreator(), actionRegistry.getMCreator().getWorkspace());
			actionRegistry.getMCreator().getWorkspace().setWorkspaceSettings(change.workspaceSettings);

			refactorWorkspace(actionRegistry.getMCreator(), change);

			actionRegistry.getMCreator().mv.updateMods();
		});
	}

	public static void refactorWorkspace(MCreator mcreator, WorkspaceSettingsChange change) {
		if (change.refactorNeeded() && change.oldSettings != null) {
			if (change.packagechanged) { // we need to copy all source files to new package and remove the old one
				File originalPackage = new File(mcreator.getWorkspace().getGenerator().getSourceRoot(),
						change.oldSettings.getModElementsPackage().replace(".", "/"));
				File newPackage = new File(mcreator.getWorkspace().getGenerator().getSourceRoot(),
						change.workspaceSettings.getModElementsPackage().replace(".", "/"));
				FileIO.copyDirectory(originalPackage, newPackage);
				FileIO.deleteDir(originalPackage);
			}

			if (change.modidchanged) { // we need to refactor structures (modid in nbt files), change workspace file modid and reference in file manager
				File originalWorkspaceFile = new File(
						mcreator.getWorkspace().getFileManager().getWorkspaceFile().getPath());
				File newWorkspaceFile = new File(
						mcreator.getWorkspace().getFileManager().getWorkspaceFile().getParentFile().getPath(),
						change.workspaceSettings.getModID() + ".mcreator");

				// first save current workspace state
				mcreator.getWorkspace().getFileManager().saveWorkspaceDirectlyAndWait();

				// move workspace file to a new file
				FileIO.copyFile(originalWorkspaceFile, newWorkspaceFile);
				mcreator.getWorkspace().bindToNewWorkspaceFile(newWorkspaceFile);

				// delete the original workspace file
				originalWorkspaceFile.delete();

				// refactor assets folder if it contains modid
				if (mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getModAssetsRoot() != null
						&& mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getModAssetsRoot()
						.contains("@modid")) {
					File originalAssetsFolder = new File(GeneratorTokens
							.replaceTokens(mcreator.getWorkspace(), change.oldSettings,
									mcreator.getWorkspace().getGenerator().getGeneratorConfiguration()
											.getModAssetsRoot()));
					File newAssetsFolder = new File(GeneratorTokens
							.replaceTokens(mcreator.getWorkspace(), change.workspaceSettings,
									mcreator.getWorkspace().getGenerator().getGeneratorConfiguration()
											.getModAssetsRoot()));

					FileIO.copyDirectory(originalAssetsFolder, newAssetsFolder);
					FileIO.deleteDir(originalAssetsFolder);
				}

				// refactor data folder if it contains modid
				if (mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getModDataRoot() != null
						&& mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getModDataRoot()
						.contains("@modid")) {
					File originalDataFolder = new File(GeneratorTokens
							.replaceTokens(mcreator.getWorkspace(), change.oldSettings,
									mcreator.getWorkspace().getGenerator().getGeneratorConfiguration()
											.getModDataRoot()));
					File newDataFolder = new File(GeneratorTokens
							.replaceTokens(mcreator.getWorkspace(), change.workspaceSettings,
									mcreator.getWorkspace().getGenerator().getGeneratorConfiguration()
											.getModDataRoot()));

					FileIO.copyDirectory(originalDataFolder, newDataFolder);
					FileIO.deleteDir(originalDataFolder);
				}

				// refactor structures
				File structuresDir = mcreator.getWorkspace().getFolderManager().getStructuresDir();
				if (structuresDir != null) {
					File[] structures = structuresDir.listFiles();
					for (File file : structures != null ? structures : new File[0])
						if (file.getName().endsWith(".nbt"))
							StructureUtils.renamePrefixInStructures(file, change.oldSettings.getModID(),
									change.workspaceSettings.getModID());
				}

				String flavor = mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getGeneratorFlavor().toString();
				// add new modid workspace to the recent workspaces so it does not get removed from the list
				mcreator.getApplication().getWorkspaceSelector().addRecentWorkspace(
						new WorkspaceSelector.RecentWorkspaceEntry(
								mcreator.getWorkspace().getWorkspaceSettings().getModName(), newWorkspaceFile,
								StringUtils.uppercaseFirstLetter(flavor.toLowerCase()) + " "
										+ mcreator.getWorkspace().getGenerator().getGeneratorMinecraftVersion()));
			}

			// handle change of generator in a different manner
			if (change.generatorchanged) {
				ProgressDialog dial = new ProgressDialog(mcreator,
						L10N.t("dialog.workspace.settings.workspace_switch.title"));
				Thread t = new Thread(() -> {
					ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.workspace.settings.workspace_switch.progress.preparing"));
					dial.addProgress(p1);

					WorkspaceGeneratorSetup.cleanupGeneratorForSwitchTo(mcreator.getWorkspace(),
							Generator.GENERATOR_CACHE.get(change.workspaceSettings.getCurrentGenerator()));

					p1.ok();
					dial.refreshDisplay();

					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.workspace.settings.workspace_switch.progress.switching_version"));
					dial.addProgress(p2);

					mcreator.getWorkspace().switchGenerator(change.workspaceSettings.getCurrentGenerator());
					//Create a new RecentWorkspaceEntry
					WorkspaceSelector.RecentWorkspaceEntry recentWorkspace = new WorkspaceSelector.RecentWorkspaceEntry(
							mcreator.getWorkspace().getWorkspaceSettings().getModName(), mcreator.getWorkspace().getFileManager().getWorkspaceFile(),
							StringUtils.uppercaseFirstLetter(
							mcreator.getWorkspace().getGenerator().getGeneratorConfiguration().getGeneratorFlavor().toString()
									.toLowerCase()) + " " + mcreator.getWorkspace().getGenerator()
							.getGeneratorMinecraftVersion());
					//Remove the old RecentWorkspaceEntry and add the new one
					WorkspaceSelector.RecentWorkspaceEntry old = mcreator.getApplication().getRecentWorkspaceEntry();
					mcreator.getApplication().getWorkspaceSelector().removeRecentWorkspace(old);
					mcreator.getApplication().getWorkspaceSelector().addRecentWorkspace(recentWorkspace);
					mcreator.getApplication().getWorkspaceSelector().saveRecentWorkspaces();
					//Save the new recentWorkspaceEntry
					mcreator.getApplication().setRecentWorkspaceEntry(recentWorkspace);

					p2.ok();
					dial.hideAll();

					WorkspaceGeneratorSetupDialog.runSetup(mcreator, false);

					// we need to regenerate the whole code after new generator is selected, no need to reload gradle caches as runSetup already did this
					RegenerateCodeAction.regenerateCode(mcreator, true, true);
				});
				t.start();
			} else { // in any other case, we need to regenerate the whole code
				if (change.gradleCachesRebuildNeeded()) { // and rebuild caches when needed
					ModAPIManager.deleteAPIs(mcreator.getWorkspace(), change.oldSettings);
					mcreator.actionRegistry.reloadGradleProject.doAction();
				}
				RegenerateCodeAction.regenerateCode(mcreator, true, true);
			}
		}
	}

}
