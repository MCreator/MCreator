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

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.gradle.GradleAction;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.DesktopUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;

public class ExportWorkspaceForDistAction extends GradleAction {

	public ExportWorkspaceForDistAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.export_mod"), e -> exportImpl(actionRegistry, "build"));
	}

	public static class Deobf extends GradleAction {

		public Deobf(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.export_mod_deobf"), e -> {
				int sel = JOptionPane.showConfirmDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.export_deobf.message"), L10N.t("dialog.workspace.export_deobf.title"),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.YES_OPTION) {
					exportImpl(actionRegistry, "jar");
				}
			});
		}

		@Override public boolean isEnabled() {
			return super.isEnabled()
					&& actionRegistry.getMCreator().getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA;
		}

		@Override public void setEnabled(boolean b) {
			if (actionRegistry.getMCreator().getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA)
				super.setEnabled(b);
			else
				super.setEnabled(false);

		}
	}

	private static void exportImpl(ActionRegistry actionRegistry, String task) {
		actionRegistry.getMCreator().getGenerator().runResourceSetupTasks();
		actionRegistry.getMCreator().getGenerator().generateBase();

		actionRegistry.getMCreator().mcreatorTabs.showTab(actionRegistry.getMCreator().consoleTab);

		actionRegistry.getMCreator().getGradleConsole().exec(task, taskResult -> {
			String exportFile = actionRegistry.getMCreator().getGeneratorConfiguration()
					.getGradleTaskFor("export_file");

			if (new File(actionRegistry.getMCreator().getWorkspaceFolder(), exportFile).isFile()) {
				Object[] options2 = { L10N.t("dialog.workspace.export.option.donate_and_export"),
						L10N.t("dialog.workspace.export.option.just_export"),
						UIManager.getString("OptionPane.cancelButtonText") };
				int n = JOptionPane
						.showOptionDialog(actionRegistry.getMCreator(), L10N.t("dialog.workspace.export.message"),
								L10N.t("dialog.workspace.export.title"), JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, UIRES.get("icon"), options2, options2[0]);
				if (n == 2 || n == JOptionPane.CLOSED_OPTION) {
					return;
				} else if (n == 0) {
					DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/donate");
				}

				File loc = FileDialogs.getSaveDialog(actionRegistry.getMCreator(),
						new String[] { "." + FilenameUtils.getExtension(exportFile) });
				if (loc != null)
					FileIO.copyFile(new File(actionRegistry.getMCreator().getWorkspaceFolder(), exportFile), loc);
			} else {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.export.error.message"), L10N.t("dialog.workspace.export.error.title"),
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

}
