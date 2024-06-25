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

import net.mcreator.io.FileIO;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.gradle.GradleAction;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.FilenameUtilsPatched;

import javax.swing.*;
import java.io.File;

public class ExportWorkspaceForDistAction extends GradleAction {

	public ExportWorkspaceForDistAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.export_mod"), e -> exportImpl(actionRegistry));
	}

	private static void exportImpl(ActionRegistry actionRegistry) {
		actionRegistry.getMCreator().getGenerator().runResourceSetupTasks();
		actionRegistry.getMCreator().getGenerator().generateBase();

		actionRegistry.getMCreator().mcreatorTabs.showTab(actionRegistry.getMCreator().consoleTab);

		actionRegistry.getMCreator().getGradleConsole().exec("build", taskResult -> {
			String exportFile = actionRegistry.getMCreator().getGeneratorConfiguration()
					.getGradleTaskFor("export_file");
			String exportExtension = FilenameUtilsPatched.getExtension(exportFile);

			if (new File(actionRegistry.getMCreator().getWorkspaceFolder(), exportFile).isFile()) {
				Object[] options2 = { L10N.t("dialog.workspace.export.option.just_export"),
						L10N.t("dialog.workspace.export.option.donate_and_export"),
						UIManager.getString("OptionPane.cancelButtonText") };
				int n = JOptionPane.showOptionDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.export.message"), L10N.t("dialog.workspace.export.title"),
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, UIRES.get("export_donate"),
						options2, options2[1]);
				if (n == 2 || n == JOptionPane.CLOSED_OPTION) {
					return;
				} else if (n == 1) {
					DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/donate");
				}

				String suggestedFileName = actionRegistry.getMCreator().getWorkspaceSettings().getModID() + "-"
						+ actionRegistry.getMCreator().getWorkspaceSettings().getVersion() + "-"
						+ actionRegistry.getMCreator().getWorkspaceSettings().getCurrentGenerator() + "."
						+ exportExtension;

				File loc = FileDialogs.getSaveDialog(actionRegistry.getMCreator(), suggestedFileName,
						new String[] { "." + exportExtension });
				if (loc != null) {
					actionRegistry.getMCreator().getApplication().getAnalytics()
							.trackEvent(AnalyticsConstants.EVENT_EXPORT_FOR_DIST, "build");

					FileIO.copyFile(new File(actionRegistry.getMCreator().getWorkspaceFolder(), exportFile), loc);
				}
			} else {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.export.error.message"), L10N.t("dialog.workspace.export.error.title"),
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

}
