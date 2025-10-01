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

package net.mcreator.ui.action.impl.workspace;

import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.WorkspaceFileManager;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

public class MergeExternalWorkspaceAction extends BasicAction {
	private static final Logger LOG = LogManager.getLogger("Merge External Workspace");

	public MergeExternalWorkspaceAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.merge_workspace"), event -> {
			File file = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".zip" });
			if (file != null) {
				MCreator mcreator = actionRegistry.getMCreator();
				ProgressDialog dial = new ProgressDialog(mcreator, L10N.t("dialog.workspace.merge_workspace.title"));
				Thread thread = new Thread(() -> {
					Workspace workspace = mcreator.getWorkspace();

					LOG.debug("Starting to import external workspace {} to merge into current workspace...",
							file.getName());
					ProgressDialog.ProgressUnit p0 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.workspace.merge_workspace.progress.initial_import"));
					dial.addProgressUnit(p0);

					String tempFolder = System.getProperty("java.io.tmpdir") + file.getName().replace(".zip", "");
					ZipIO.unzip(file.getAbsolutePath(), tempFolder);
					LOG.info("Workspace successfully unzipped");

					Workspace imported;
					try {
						imported = getWorkspaceFile(tempFolder);
					} catch (IOException e) {
						LOG.error("Impossible to retrieve workspace's main file. Ending the process now", e);
						p0.markStateError();
						dial.hideDialog();
						return;
					}
					LOG.info("Workspace successfully retrieved");
					p0.markStateOk();

					if (imported != null) {
						File workspaceFolder = workspace.getWorkspaceFolder();
						String oldModID = imported.getWorkspaceSettings().getModID();
						String currentModID = workspace.getWorkspaceSettings().getModID();
						String assetsFolder = "src/main/resources/assets/" + oldModID;

						// We add and copy all elements and related files to the current workspace
						LOG.debug("Adding mod elements to workspace...");
						ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_mod_elements"));
						dial.addProgressUnit(p1);
						imported.getModElements().forEach(workspace::addModElement);
						copyFilesFromFolder(workspaceFolder, tempFolder, "/elements/", oldModID, currentModID, p1);

						LOG.debug("Adding texture files to workspace...");
						ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_textures"));
						dial.addProgressUnit(p2);
						copyFilesFromFolder(workspaceFolder, tempFolder, assetsFolder + "/textures/", oldModID,
								currentModID, p2);

						LOG.debug("Adding model files to workspace...");
						ProgressDialog.ProgressUnit p3 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_models"));
						dial.addProgressUnit(p3);
						copyFilesFromFolder(workspaceFolder, tempFolder, assetsFolder + "/models/", oldModID,
								currentModID, p3);

						LOG.debug("Adding structure files to workspace...");
						ProgressDialog.ProgressUnit p4 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_structures"));
						dial.addProgressUnit(p4);
						copyFilesFromFolder(workspaceFolder, tempFolder,
								"src/main/resources/data/" + oldModID + "/structure/", oldModID, currentModID, p4);

						LOG.debug("Adding sounds to workspace...");
						ProgressDialog.ProgressUnit p5 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_sounds"));
						dial.addProgressUnit(p5);
						imported.getSoundElements().forEach(workspace::addSoundElement);
						copyFilesFromFolder(workspaceFolder, tempFolder, assetsFolder + "/sounds/", oldModID,
								currentModID, p5);

						LOG.debug("Adding variable objects to workspace...");
						ProgressDialog.ProgressUnit p6 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_variables"));
						dial.addProgressUnit(p6);
						imported.getVariableElements().forEach(workspace::addVariableElement);
						p6.markStateOk();

						LOG.debug("Adding languages to workspace...");
						ProgressDialog.ProgressUnit p7 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_languages"));
						dial.addProgressUnit(p7);
						imported.getLanguageMap().forEach(workspace::addLanguage);
						p7.markStateOk();

						LOG.debug("Adding tags to workspace...");
						ProgressDialog.ProgressUnit p8 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.add_tags"));
						dial.addProgressUnit(p8);
						imported.getTagElements().forEach((tagElement, elements) -> {
							workspace.addTagElement(tagElement);
							workspace.getTagElements().get(tagElement).addAll(elements);
						});
						p8.markStateOk();

						// We regenerate all the code, so all the new imported mode elements can be generated
						LOG.debug("All elements were successfully imported. Now regenerating the code...");
						ProgressDialog.ProgressUnit p9 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.regenerate_code"));
						dial.addProgressUnit(p9);
						RegenerateCodeAction.regenerateCode(mcreator, false, true);
						p9.markStateOk();

						LOG.debug("The code was successfully regenerated. Now generating the mod element icons...");
						ProgressDialog.ProgressUnit p10 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.regenerating_icons"));
						dial.addProgressUnit(p10);
						Collection<ModElement> modElements = workspace.getModElements();
						int iconsToMake = modElements.size();
						int i = 0;
						for (ModElement mod : modElements) {
							workspace.getModElementManager().storeModElementPicture(mod.getGeneratableElement());
							p10.setPercent((int) (i / (float) iconsToMake * 100));
							i++;
						}
						p10.markStateOk();

						LOG.debug("Icons successfully regenerated. Now saving the workspace...");
						ProgressDialog.ProgressUnit p11 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.workspace.merge_workspace.progress.save_workspace"));
						dial.addProgressUnit(p11);
						workspace.getFileManager().saveWorkspaceDirectlyAndWait();
						p11.markStateOk();

						dial.hideDialog();
					}
				}, "MergeWorkspace");
				thread.start();
				dial.setVisible(true);
			}
		});
	}

	private static @Nullable Workspace getWorkspaceFile(String folder) throws IOException {
		File workspaceFile;

		try (Stream<Path> files = Files.list(Path.of(folder))) {
			workspaceFile = files.filter(f -> f.getFileName().toString().endsWith(".mcreator")).map(Path::toFile)
					.findFirst().orElse(null);
		}

		return WorkspaceFileManager.gson.fromJson(FileIO.readFileToString(workspaceFile), Workspace.class);
	}

	private static void copyFilesFromFolder(File workspaceFolder, String tempFolder, String folder, String oldModID,
			String currentModID, ProgressDialog.ProgressUnit unit) {
		try {
			FileUtils.copyDirectory(new File(tempFolder, folder),
					new File(workspaceFolder, folder.replace(oldModID, currentModID)));
			System.out.println();
			unit.markStateOk();
		} catch (IOException e) {
			LOG.warn("Could not copy files from {} as it does not exist.", folder,
					new FileNotFoundException("Folder " + folder + " does not exist"));
			unit.markStateWarning();
		}
	}
}