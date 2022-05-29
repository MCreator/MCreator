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

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.gradle.GradleTaskFinishedListener;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.gradle.GradleAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RegenerateCodeAction extends GradleAction {

	private static final Logger LOG = LogManager.getLogger("Code Regenerate");

	public RegenerateCodeAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.regenerate_and_build"),
				e -> regenerateCode(actionRegistry.getMCreator(), false, true));
		setIcon(UIRES.get("16px.regencode"));
	}

	public static void regenerateCode(MCreator mcreator, boolean warnLockedCode, boolean warnMissingDefinitions) {
		regenerateCode(mcreator, warnLockedCode, warnMissingDefinitions, null);
	}

	public static void regenerateCode(MCreator mcreator, boolean warnLockedCode, boolean warnMissingDefinitions,
			@Nullable GradleTaskFinishedListener taskSpecificListener) {
		ProgressDialog dial = new ProgressDialog(mcreator, L10N.t("dialog.workspace.regenerate_and_build.title"));
		Thread thread = new Thread(() -> {
			ProgressDialog.ProgressUnit p0 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.removing_autogenerated_code"));
			dial.addProgress(p0);

			List<File> toBePreserved = new ArrayList<>();

			// remove all sources of mod elements that are not locked
			for (ModElement mod : mcreator.getWorkspace().getModElements()) {
				List<GeneratorTemplate> templates = mcreator.getGenerator().getModElementGeneratorTemplatesList(mod);
				if (templates == null)
					continue;

				mcreator.getGenerator().getModElementListTemplates(mod).forEach(list -> {
					for (int i = 0; i < list.listData().size(); i++) {
						for (GeneratorTemplate generatorTemplate : list.templates().keySet()) {
							if (list.templates().get(generatorTemplate).get(i)
									&& list.forIndex(generatorTemplate, i) != null)
								templates.add(list.forIndex(generatorTemplate, i));
						}
					}
				});

				List<File> modElementFiles = templates.stream().map(GeneratorTemplate::getFile).toList();
				toBePreserved.addAll(modElementFiles); // we don't delete mod element files in next step
				if (!mod.isCodeLocked()) // but we do in this step, if the code is not locked
					modElementFiles.forEach(File::delete);
			}

			// keep base mod files that can be locked if selected so in the workspace settings
			if (mcreator.getWorkspaceSettings().isLockBaseModFiles()) {
				mcreator.getGenerator().getModBaseGeneratorTemplatesList(false).forEach(generatorTemplate -> {
					if (((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock") != null
							&& ((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock")
							.equals("true")) // can this file be locked
						// are mod base file locked
						toBePreserved.add(
								generatorTemplate.getFile()); // we add locked base mod files on the to be preserved list
				});
			}

			// delete all non mod element related files from code base package
			File[] files = FileIO.listFilesRecursively(mcreator.getGenerator().getGeneratorPackageRoot());
			for (File a : files) {
				if (!FileIO.isFileOnFileList(toBePreserved,
						a)) // if file is not part of one of the mod elements, it can be removed
					a.delete();
			}

			p0.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p10 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.loading_mod_elements"));
			dial.addProgress(p10);
			List<ModElement> modElementsOld = new ArrayList<>(mcreator.getWorkspace().getModElements());
			int modstoload = modElementsOld.size();
			int i = 0;
			for (ModElement mod : modElementsOld) {
				mod.getGeneratableElement();
				p10.setPercent((int) (((float) i / (float) modstoload) * 100.0f));
				dial.refreshDisplay();
				i++;
			}
			p10.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.regenerating_code"));
			dial.addProgress(p1);

			Set<ModElement> skippedElements = new HashSet<>(0);
			boolean skipAll = !warnMissingDefinitions; // if warnMissingDefinitions is false, we skip all by default without warnings
			boolean hasLockedElements = false;

			// list of generatablemodelements to save after rebuild
			List<GeneratableElement> generatableElementsToSave = new ArrayList<>();
			Set<File> filesToReformat = new HashSet<>();

			modstoload = mcreator.getWorkspace().getModElements().size();
			i = 0;
			for (ModElement mod : mcreator.getWorkspace().getModElements()) {
				if (mod.isCodeLocked()) {
					hasLockedElements = true;
				}

				if (!mcreator.getModElementManager().hasModElementGeneratableElement(mod)) {
					if (!skipAll) {
						int opt = JOptionPane.showOptionDialog(mcreator,
								L10N.t("dialog.workspace.regenerate_and_build.error.failed_to_import.message",
										mod.getName(), skippedElements.size()),
								L10N.t("dialog.workspace.regenerate_and_build.error.failed_to_import.title"),
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] {
										L10N.t("dialog.workspace.regenerate_and_build.error.failed_to_import.option.skip_one"),
										L10N.t("dialog.workspace.regenerate_and_build.error.failed_to_import.option.skip_all") },
								0);
						if (opt == 1)
							skipAll = true;
					}
					skippedElements.add(mod);
					continue;
				}

				try {
					GeneratableElement generatableElement = mod.getGeneratableElement();

					LOG.debug("Regenerating " + mod.getType().getReadableName() + " mod element: " + mod.getName());

					// generate mod element code
					List<GeneratorFile> generatedFiles = mcreator.getGenerator()
							.generateElement(generatableElement, false);

					if (!mod.isCodeLocked()) {
						filesToReformat.addAll(
								generatedFiles.stream().map(GeneratorFile::file).collect(Collectors.toSet()));
					}

					// save custom mod element picture if it has one
					mcreator.getModElementManager().storeModElementPicture(generatableElement);

					// add mod element to workspace again, so the icons get reloaded
					mcreator.getWorkspace().addModElement(mod);

					// we reinit the mod to load new icons etc.
					mod.reinit();

					generatableElementsToSave.add(generatableElement);
				} catch (Exception e) {
					LOG.error("Failed to regenerate: " + mod.getName(), e);
				}

				p1.setPercent((int) (((float) i / (float) modstoload) * 100.0f));
				dial.refreshDisplay();
				i++;
			}

			// save all updated generatable mod elements
			generatableElementsToSave.parallelStream().forEach(mcreator.getModElementManager()::storeModElement);

			if (warnMissingDefinitions && skippedElements.size() > 0) {
				skippedElements.forEach(el -> {
					try {
						mcreator.getWorkspace().removeModElement(el);
					} catch (Exception ignored) {
					}
				});
				JOptionPane.showMessageDialog(dial,
						L10N.t("dialog.workspace.regenerate_and_build.warning.skipped_import_of.message",
								skippedElements.size()),
						L10N.t("dialog.workspace.regenerate_and_build.warning.skipped_import_of.title"),
						JOptionPane.WARNING_MESSAGE);
			}

			if (warnLockedCode && hasLockedElements)
				JOptionPane.showMessageDialog(dial,
						L10N.t("dialog.workspace.regenerate_and_build.warning.elements_with_locked_code.message"),
						L10N.t("dialog.workspace.regenerate_and_build.warning.elements_with_locked_code.title"),
						JOptionPane.WARNING_MESSAGE);

			p1.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.regenerating_workspace_and_resources"));
			dial.addProgress(p2);

			mcreator.getGenerator().runResourceSetupTasks();
			mcreator.getGenerator().generateBase(false);
			mcreator.mv.updateMods();

			// remove custom API libraries so they get re-downloaded
			ModAPIManager.deleteAPIs(mcreator.getWorkspace(), mcreator.getWorkspaceSettings());

			p2.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p22 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.reformating_code"));
			dial.addProgress(p22);

			int ftfCount = filesToReformat.size();
			ClassWriter.formatAndOrganiseImportsForFiles(mcreator.getWorkspace(), filesToReformat,
					idx -> p22.setPercent((int) (((float) idx / (float) ftfCount) * 100.0f)));

			p22.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p23 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.clean_up_workspace"));
			dial.addProgress(p23);

			FileIO.removeEmptyDirs(mcreator.getGenerator().getSourceRoot());
			FileIO.removeEmptyDirs(mcreator.getGenerator().getResourceRoot());

			// delete old license file if present
			new File(mcreator.getGenerator().getResourceRoot(), "MCreator-README.txt").delete();

			p23.ok();
			dial.refreshDisplay();

			ProgressDialog.ProgressUnit p3 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.rebuilding_workspace"));
			dial.addProgress(p3);

			mcreator.getGradleConsole().markRunning(); // so console gets locked while we generate code already
			try {
				mcreator.getGenerator().generateBase();
				mcreator.getGradleConsole().exec("build", taskSpecificListener);
			} catch (Exception e) { // if something fails, we still need to free the gradle console
				LOG.error(e.getMessage(), e);
				mcreator.getGradleConsole().markReady();
			}

			p3.ok();
			dial.refreshDisplay();

			dial.hideAll();
		});
		thread.start();
		dial.setVisible(true);
	}

}
