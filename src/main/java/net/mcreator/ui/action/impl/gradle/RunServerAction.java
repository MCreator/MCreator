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

import net.mcreator.gradle.GradleTaskFinishedListener;
import net.mcreator.io.FileIO;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MinecraftOptionsUtils;
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunServerAction extends GradleAction {

	private static final Logger LOG = LogManager.getLogger("Run Minecraft Server");

	public RunServerAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.run_server_and_client"), null);
		setActionListener(evt -> {
			File eulaFile = new File(actionRegistry.getMCreator().getFolderManager().getServerRunDir(), "eula.txt");

			String eula;
			if (!eulaFile.isFile())
				eula = "eula=false";
			else
				eula = FileIO.readFileToString(eulaFile);
			if (eula.contains("eula=false") || !eulaFile.isFile()) {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.run_server_and_client.eula_intro"));

				DesktopUtils.browseSafe("https://www.minecraft.net/en-us/eula");

				Object[] options = { L10N.t("dialog.run_server_and_client.agree"),
						L10N.t("dialog.run_server_and_client.disagree") };
				int n = JOptionPane.showOptionDialog(null,
						L10N.t("dialog.run_server_and_client.eula_confirmation.message"),
						L10N.t("dialog.run_server_and_client.eula_confirmation.title"),
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 0) {
					try {
						Properties por = new Properties();
						por.load(new FileInputStream(eulaFile));
						por.setProperty("eula", "true");
						por.store(new FileOutputStream(eulaFile),
								"#Edited by MCreator - user agreed to EULA inside MCreator");
					} catch (IOException e) {
						LOG.warn("Failed to write EULA file", e);
					}
					runServer();
				} else {
					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.run_server_and_client.eula_rejected.message"));
				}
			} else {
				runServer();
			}
		});
	}

	private void runServer() {
		actionRegistry.getMCreator().getGradleConsole()
				.markRunning(); // so console gets locked while we generate code already
		try {
			actionRegistry.getMCreator().getGenerator().runResourceSetupTasks();
			actionRegistry.getMCreator().getGenerator().generateBase();

			if (PreferencesManager.PREFERENCES.gradle.passLangToMinecraft.get())
				MinecraftOptionsUtils.setLangTo(actionRegistry.getMCreator().getWorkspace(), L10N.getLocaleString());

			AtomicBoolean clientStarted = new AtomicBoolean(false);
			actionRegistry.getMCreator().getGradleConsole()
					.exec(actionRegistry.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_server"),
							progressEvent -> {
								if (!clientStarted.get() && progressEvent.getDescription().contains(
										":" + actionRegistry.getMCreator().getGeneratorConfiguration()
												.getGradleTaskFor("run_server"))) {
									clientStarted.set(true);
									if (actionRegistry.getMCreator().getGeneratorConfiguration()
											.getGradleTaskFor("run_client") != null) {
										actionRegistry.getMCreator().getGradleConsole()
												.exec(actionRegistry.getMCreator().getGeneratorConfiguration()
														.getGradleTaskFor("run_client"));
									}
								}
							}, (GradleTaskFinishedListener) null);
		} catch (Exception e) { // if something fails, we still need to free the gradle console
			LOG.error("Failed to run server", e);
			actionRegistry.getMCreator().getGradleConsole().markReady();
		}
	}

	@Override public boolean isEnabled() {
		return actionRegistry.getMCreator().getGeneratorConfiguration().getGradleTaskFor("run_server") != null
				&& super.isEnabled();
	}
}
