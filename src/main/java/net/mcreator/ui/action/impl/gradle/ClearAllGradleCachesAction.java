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

package net.mcreator.ui.action.impl.gradle;

import net.mcreator.gradle.GradleDaemonUtils;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ClearAllGradleCachesAction extends GradleAction {

	public ClearAllGradleCachesAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.gradle.clear_caches"), evt -> {
			Object[] options = { L10N.t("action.gradle.clear_caches.option.gradle_caches"),
					L10N.t("action.gradle.clear_caches.option.gradle_folder"),
					UIManager.getString("OptionPane.cancelButtonText") };
			int reply = JOptionPane
					.showOptionDialog(actionRegistry.getMCreator(), L10N.t("action.gradle.clear_caches.option.message"),
							L10N.t("action.gradle.clear_caches.option.title"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (reply == 0 || reply == 1) {
				clearAllGradleCaches(actionRegistry.getMCreator(), reply == 1, false);
			}
		});
	}

	public static void clearAllGradleCaches(MCreator mcreator, boolean entireGradleFolder, boolean regenerateCodeFlag) {
		ProgressDialog progressDialog = new ProgressDialog(mcreator, L10N.t("dialog.cache_cleanup.title"));
		new Thread(() -> {
			ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.cache_cleanup.progress.stopping_daemons"));
			progressDialog.addProgress(p1);

			try {
				GradleDaemonUtils.stopAllDaemons(mcreator.getWorkspace());
				p1.ok();
			} catch (IOException | TimeoutException | InterruptedException e) {
				p1.err();
			}
			progressDialog.refreshDisplay();

			ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.cache_cleanup.progress.clearing_gradle_caches_folder"));
			progressDialog.addProgress(p2);

			try {
				Thread.sleep(2000); // make sure all files are released
			} catch (InterruptedException ignored) {
			}

			if (entireGradleFolder)
				FileIO.deleteDir(UserFolderManager.getGradleHome());
			else
				FileIO.deleteDir(new File(UserFolderManager.getGradleHome(), "caches"));

			p2.ok();
			progressDialog.refreshDisplay();

			ProgressDialog.ProgressUnit p3 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.cache_cleanup.progress.build_task"));
			progressDialog.addProgress(p3);

			mcreator.getGradleConsole().markRunning(); // so console gets locked while we generate code already
			try {
				mcreator.getGenerator().generateBase();

				String tasks = "build";

				if (mcreator.getGeneratorConfiguration().getGradleTaskFor("setup_task") != null) {
					tasks = mcreator.getGeneratorConfiguration().getGradleTaskFor("setup_task") + (regenerateCodeFlag ?
							"" :
							" build");
				}

				mcreator.getGradleConsole().exec(tasks, result -> {
					ProgressDialog progressDialogSecondStage = new ProgressDialog(mcreator,
							L10N.t("dialog.reload_gradle_project.title"));

					new Thread(() -> {
						ProgressDialog.ProgressUnit p33a = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.setup_workspace.progress.reloading_gradle_project"));
						progressDialogSecondStage.addProgress(p33a);

						try {
							mcreator.getGenerator().reloadGradleCaches();
							p33a.ok();
							progressDialogSecondStage.hideAll();
						} catch (Exception e) {
							p33a.err();
							progressDialogSecondStage.hideAll();
						}

						if (regenerateCodeFlag) {
							RegenerateCodeAction.regenerateCode(mcreator, false, true);
						}
					}).start();

					progressDialogSecondStage.setVisible(true);
				});
			} catch (Exception e) { // if something fails, we still need to free the gradle console
				mcreator.getGradleConsole().markReady();
			}

			p3.ok();
			progressDialog.refreshDisplay();

			progressDialog.hideAll();
		}).start();
		progressDialog.setVisible(true);
	}

}
