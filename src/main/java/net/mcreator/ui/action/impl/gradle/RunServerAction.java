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

import net.mcreator.io.FileIO;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MinecraftOptionsUtils;
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class RunServerAction extends GradleAction {

	private static final Logger LOG = LogManager.getLogger("Run Minecraft Server");

	public RunServerAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.run_server_and_client"), evt -> {
			String eula;
			if (!new File(actionRegistry.getMCreator().getWorkspaceFolder(), "run/eula.txt").isFile())
				eula = "eula=false";
			else
				eula = FileIO
						.readFileToString(new File(actionRegistry.getMCreator().getWorkspaceFolder(), "run/eula.txt"));
			if (eula.contains("eula=false") || !new File(actionRegistry.getMCreator().getWorkspaceFolder(),
					"run/eula.txt").isFile()) {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.run_server_and_client.eula_intro"));

				DesktopUtils.browseSafe("https://account.mojang.com/documents/minecraft_eula");

				Object[] options = { L10N.t("dialog.run_server_and_client.agree"),
						L10N.t("dialog.run_server_and_client.disagree") };
				int n = JOptionPane
						.showOptionDialog(null, L10N.t("dialog.run_server_and_client.eula_confirmation.message"),
								L10N.t("dialog.run_server_and_client.eula_confirmation.title"),
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (n == 0) {
					Properties por = new Properties();
					try {
						por.load(new FileInputStream(
								new File(actionRegistry.getMCreator().getWorkspaceFolder(), "run/eula.txt")));
					} catch (IOException e) {
						LOG.warn(e.getMessage());
					}
					por.setProperty("eula", "true");
					try {
						por.store(new FileOutputStream(
										new File(actionRegistry.getMCreator().getWorkspaceFolder(), "run/eula.txt")),
								"#Edited by MCreator - User agreed with EULA inside MCreator");
					} catch (FileNotFoundException e) {
						LOG.error(e.getMessage());
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
					actionRegistry.getMCreator().getGradleConsole()
							.markRunning(); // so console gets locked while we generate code already
					try {
						actionRegistry.getMCreator().getGenerator().runResourceSetupTasks();
						actionRegistry.getMCreator().getGenerator().generateBase();
						actionRegistry.getMCreator().getGradleConsole()
								.exec(actionRegistry.getMCreator().getGeneratorConfiguration()
										.getGradleTaskFor("run_server"));

						if (actionRegistry.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_client")
								!= null)
							actionRegistry.getMCreator().getGradleConsole()
									.exec(actionRegistry.getMCreator().getGeneratorConfiguration()
											.getGradleTaskFor("run_client"));
					} catch (Exception e) { // if something fails, we still need to free the gradle console
						LOG.error(e.getMessage(), e);
						actionRegistry.getMCreator().getGradleConsole().markReady();
					}
				} else {
					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.run_server_and_client.eula_rejected.message"));
				}
			} else {
				actionRegistry.getMCreator().getGradleConsole()
						.markRunning(); // so console gets locked while we generate code already
				try {
					actionRegistry.getMCreator().getGenerator().runResourceSetupTasks();
					actionRegistry.getMCreator().getGenerator().generateBase();

					if (PreferencesManager.PREFERENCES.gradle.passLangToMinecraft)
						MinecraftOptionsUtils
								.setLangTo(actionRegistry.getMCreator().getWorkspace(), L10N.getLocaleString());

					actionRegistry.getMCreator().getGradleConsole()
							.exec(actionRegistry.getMCreator().getGeneratorConfiguration()
									.getGradleTaskFor("run_server"));

					if (actionRegistry.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_client") != null)
						actionRegistry.getMCreator().getGradleConsole()
								.exec(actionRegistry.getMCreator().getGeneratorConfiguration()
										.getGradleTaskFor("run_client"));
				} catch (Exception e) { // if something fails, we still need to free the gradle console
					LOG.error(e.getMessage(), e);
					actionRegistry.getMCreator().getGradleConsole().markReady();
				}
			}
		});
	}

	@Override public boolean isEnabled() {
		return actionRegistry.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_server") != null && super
				.isEnabled();
	}
}
