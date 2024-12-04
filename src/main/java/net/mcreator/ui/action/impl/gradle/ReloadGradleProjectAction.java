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

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;

public class ReloadGradleProjectAction extends GradleAction {

	public ReloadGradleProjectAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.gradle.reload_project"), evt -> {
			ProgressDialog progressDialog = new ProgressDialog(actionRegistry.getMCreator(),
					L10N.t("dialog.reload_gradle_project.title"));
			new Thread(() -> {
				ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.setup_workspace.progress.reloading_gradle_dependencies"));
				progressDialog.addProgressUnit(p1);

				actionRegistry.getMCreator().getTabs().showTab(actionRegistry.getMCreator().consoleTab);

				actionRegistry.getMCreator().getGradleConsole()
						.exec(GradleConsole.GRADLE_SYNC_TASK + " --refresh-dependencies", finished -> {
							p1.markStateOk();

							ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
									L10N.t("dialog.setup_workspace.progress.reloading_gradle_project"));
							progressDialog.addProgressUnit(p2);

							new Thread(() -> {
								try {
									actionRegistry.getMCreator().getGenerator().reloadGradleCaches();
									p2.markStateOk();
									progressDialog.hideDialog();
								} catch (Exception e) {
									p2.markStateError();
									progressDialog.hideDialog();
								}
							}, "GradleProjectCacheReload").start();
						});
			}, "ReloadGradleProject").start();
			progressDialog.setVisible(true);
		});
	}

}
