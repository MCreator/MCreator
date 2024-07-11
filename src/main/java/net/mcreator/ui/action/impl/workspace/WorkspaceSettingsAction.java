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

import net.mcreator.Launcher;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.generator.GeneratorTokens;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.StructureUtils;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.workspace.WorkspaceRefactoringEvent;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.gradle.GradleAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.workspace.WorkspaceDialogs;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.selector.RecentWorkspaceEntry;
import net.mcreator.util.diff.DiffResult;
import net.mcreator.util.diff.ListDiff;
import net.mcreator.workspace.ShareableZIPManager;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceSettingsAction extends GradleAction {

	public WorkspaceSettingsAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.settings"), e -> {
			WorkspaceSettingsChange change = WorkspaceDialogs.workspaceSettings(actionRegistry.getMCreator(),
					actionRegistry.getMCreator().getWorkspace());
			if (change != null) {
				actionRegistry.getMCreator().getWorkspace().setWorkspaceSettings(change.workspaceSettings);

				refactorWorkspace(actionRegistry.getMCreator(), change);

				actionRegistry.getMCreator().mv.reloadElementsInCurrentTab();
			}
		});
	}

	public static void refactorWorkspace(MCreator mcreator, WorkspaceSettingsChange change) {
		if (change.refactorNeeded() && change.oldSettings != null) {

			MCREvent.event(new WorkspaceRefactoringEvent(mcreator, change));

			if (change.generatorFlavorChanged) {
				ShareableZIPManager.exportZIP(L10N.t("dialog.workspace.export_backup"),
						new File(mcreator.getWorkspace().getFolderManager().getWorkspaceCacheDir(),
								"FullBackup" + mcreator.getWorkspace().getMCreatorVersion() + ".zip"), mcreator, true);
			}

			if (change.packagechanged) { // we need to copy all source files to new package and remove the old one
				File originalPackage = new File(mcreator.getGenerator().getSourceRoot(),
						change.oldSettings.getModElementsPackage().replace(".", "/"));
				File newPackage = new File(mcreator.getGenerator().getSourceRoot(),
						change.workspaceSettings.getModElementsPackage().replace(".", "/"));
				FileIO.copyDirectory(originalPackage, newPackage);
				FileIO.deleteDir(originalPackage);
			}

			if (change.modidchanged) { // we need to refactor structures (modid in nbt files), change workspace file modid and reference in file manager
				File originalWorkspaceFile = new File(mcreator.getFileManager().getWorkspaceFile().getPath());
				File newWorkspaceFile = new File(mcreator.getFileManager().getWorkspaceFile().getParentFile().getPath(),
						change.workspaceSettings.getModID() + ".mcreator");

				// first save current workspace state
				mcreator.getFileManager().saveWorkspaceDirectlyAndWait();

				// move workspace file to a new file
				FileIO.copyFile(originalWorkspaceFile, newWorkspaceFile);
				mcreator.getWorkspace().bindToNewWorkspaceFile(newWorkspaceFile);

				// delete the original workspace file
				originalWorkspaceFile.delete();

				// refactor assets folder if it contains modid
				if (mcreator.getGeneratorConfiguration().getModAssetsRoot() != null
						&& mcreator.getGeneratorConfiguration().getModAssetsRoot().contains("@modid")) {
					File originalAssetsFolder = new File(
							GeneratorTokens.replaceTokens(mcreator.getWorkspace(), change.oldSettings,
									mcreator.getGeneratorConfiguration().getModAssetsRoot()));
					File newAssetsFolder = new File(
							GeneratorTokens.replaceTokens(mcreator.getWorkspace(), change.workspaceSettings,
									mcreator.getGeneratorConfiguration().getModAssetsRoot()));

					FileIO.copyDirectory(originalAssetsFolder, newAssetsFolder);
					FileIO.deleteDir(originalAssetsFolder);
				}

				// refactor data folder if it contains modid
				if (mcreator.getGeneratorConfiguration().getModDataRoot() != null
						&& mcreator.getGeneratorConfiguration().getModDataRoot().contains("@modid")) {
					File originalDataFolder = new File(
							GeneratorTokens.replaceTokens(mcreator.getWorkspace(), change.oldSettings,
									mcreator.getGeneratorConfiguration().getModDataRoot()));
					File newDataFolder = new File(
							GeneratorTokens.replaceTokens(mcreator.getWorkspace(), change.workspaceSettings,
									mcreator.getGeneratorConfiguration().getModDataRoot()));

					FileIO.copyDirectory(originalDataFolder, newDataFolder);
					FileIO.deleteDir(originalDataFolder);
				}

				// refactor structures
				File structuresDir = mcreator.getFolderManager().getStructuresDir();
				if (structuresDir != null) {
					File[] structures = structuresDir.listFiles();
					for (File file : structures != null ? structures : new File[0])
						if (file.getName().endsWith(".nbt"))
							StructureUtils.renamePrefixInStructures(file, change.oldSettings.getModID(),
									change.workspaceSettings.getModID());
				}

				// add new modid workspace to the recent workspaces so it does not get removed from the list
				mcreator.getApplication().getWorkspaceSelector().addOrUpdateRecentWorkspace(
						new RecentWorkspaceEntry(mcreator.getWorkspace(), newWorkspaceFile,
								Launcher.version.getFullString()));
			}

			// handle change of generator in a different manner
			if (change.generatorchanged) {
				// Model.getJavaModelKeys
				ProgressDialog dial = new ProgressDialog(mcreator,
						L10N.t("dialog.workspace.settings.workspace_switch.title"));
				Thread t = new Thread(() -> {
					ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.workspace.settings.workspace_switch.progress.preparing"));
					dial.addProgressUnit(p1);

					WorkspaceGeneratorSetup.cleanupGeneratorForSwitchTo(mcreator.getWorkspace(),
							Generator.GENERATOR_CACHE.get(change.workspaceSettings.getCurrentGenerator()));

					p1.markStateOk();

					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.workspace.settings.workspace_switch.progress.switching_version"));
					dial.addProgressUnit(p2);

					mcreator.getWorkspace().switchGenerator(change.workspaceSettings.getCurrentGenerator());

					p2.markStateOk();
					dial.hideDialog();

					WorkspaceGeneratorSetupDialog.runSetup(mcreator, false);

					// check the Java models situation and warn the user if needed
					try {
						if (Generator.GENERATOR_CACHE.get(change.workspaceSettings.getCurrentGenerator())
								.getGeneratorStats().getBaseCoverageInfo().get("model_java")
								!= GeneratorStats.CoverageStatus.NONE) {
							List<Model> javaModelsOld = Model.getJavaModels(mcreator.getWorkspace(),
									Generator.GENERATOR_CACHE.get(change.oldSettings.getCurrentGenerator())
											.getCompatibleJavaModelKeys());
							List<Model> javaModelsNew = Model.getJavaModels(mcreator.getWorkspace());

							DiffResult<Model> diffResult = ListDiff.getListDiff(javaModelsOld, javaModelsNew);

							if (!diffResult.removed().isEmpty()) {
								JOptionPane.showMessageDialog(mcreator,
										L10N.t("dialog.workspace.version_switch.java_model_warning",
												diffResult.removed().stream().map(Model::getReadableName)
														.collect(Collectors.joining(", ")).trim()),
										L10N.t("dialog.workspace.version_switch.java_model_warning.title"),
										JOptionPane.WARNING_MESSAGE);
							}
						}
					} catch (Exception ignored) {
					}

					// we need to regenerate the whole code after new generator is selected, no need to reload gradle caches as runSetup already did this
					RegenerateCodeAction.regenerateCode(mcreator, true, true);
				}, "WorkspaceRefactor");
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
