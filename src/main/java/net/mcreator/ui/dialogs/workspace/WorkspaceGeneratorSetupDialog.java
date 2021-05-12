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

package net.mcreator.ui.dialogs.workspace;

import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleDaemonUtils;
import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkspaceGeneratorSetupDialog {

	private static final Logger LOG = LogManager.getLogger("Workspace Generator Setup UI");

	public static void runSetup(final MCreator m, boolean showWebsite) {
		ProgressDialog dial = new ProgressDialog(m, L10N.t("dialog.setup_workspace.title"));
		AtomicBoolean setupOk = new AtomicBoolean(true);

		Thread t = new Thread(() -> {
			ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.setup_workspace.step.gradle_files"));
			dial.addProgress(p1);

			// setup workspacebase files
			WorkspaceGeneratorSetup.setupWorkspaceBase(m.getWorkspace());

			p1.ok();
			dial.refreshDisplay();

			if (m.getGeneratorConfiguration().getGradleTaskFor("setup_task") != null) {
				m.getGradleConsole().setGradleSetupTaskRunningFlag(true);

				ProgressDialog.ProgressUnit p20 = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.setup_workspace.step.gradle_daemons"));
				dial.addProgress(p20);

				try {
					GradleDaemonUtils.stopAllDaemons(m.getWorkspace());
					p20.ok();
				} catch (IOException | TimeoutException | InterruptedException e) {
					LOG.warn("Failed to stop Gradle daemons", e);
					p20.warn();
				}
				dial.refreshDisplay();

				ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.setup_workspace.step.gradle_project"));
				dial.addProgress(p2);

				m.mcreatorTabs.showTab(m.consoleTab);

				m.getGradleConsole().exec(m.getGeneratorConfiguration().getGradleTaskFor("setup_task"), taskResult -> {
					m.getGradleConsole().setGradleSetupTaskRunningFlag(false);
					if (taskResult.getStatusByMCreator() == GradleErrorCodes.STATUS_OK) {
						p2.ok();
						dial.refreshDisplay();

						finalizeTheSetup(m, dial);
					} else {
						setupOk.set(false);
						p2.err();
						showSetupFailedMessage(dial, m, null);
					}

				});
			} else {
				finalizeTheSetup(m, dial);
			}
		});
		t.start();

		if (showWebsite)
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/page/what-next");

		dial.setVisible(true);
	}

	private static void finalizeTheSetup(MCreator m, ProgressDialog dial) {
		ProgressDialog.ProgressUnit p3 = new ProgressDialog.ProgressUnit(
				L10N.t("dialog.setup_workspace.step.importing_gradle"));
		dial.addProgress(p3);
		new Thread(() -> {
			try {
				m.getGenerator().reloadGradleCaches();
				p3.ok();

				ProgressDialog.ProgressUnit p4 = new ProgressDialog.ProgressUnit(
						L10N.t("dialog.setup_workspace.step.generating_base"));
				dial.addProgress(p4);
				m.getGenerator().generateBase();
				p4.ok();

				WorkspaceGeneratorSetup.completeSetup(m.getGenerator());

				dial.hideAll();
			} catch (Exception e) {
				LOG.error("Failed to reload Gradle caches", e);
				p3.err();
				showSetupFailedMessage(dial, m, "Failed to build cache. Reason: " + e.getMessage());
			}
		}).start();
	}

	private static void showSetupFailedMessage(ProgressDialog dial, MCreator m, String s) {
		dial.hideAll();

		Object[] options = { "<html>Re-run the setup<br><small>I've fixed the problem",
				"<html>Open preferences<br><small>And re-run afterwards",
				"<html>Copy error to clipboard<br><small>And close this workspace", "Close this workspace" };
		int action = JOptionPane.showOptionDialog(m, "<html><big>Workspace setup failed</big><br>"
						+ "<br>The setup of MCreator failed. Based on the suggestions you got, try to fix any errors you have and run it again. "
						+ "<br><br><b>Check https://mcreator.net/support/knowledgebase for tips on fixing the setup errors.</b>"
						+ "<br><br>If this is the first time you are seeing this message, try re-running the setup first."
						+ "<br><br>If the error is fixable by you (internet connection problems, ...), you can fix the problem and click the first button"
						+ "<br><b>after</b> you fix the problem. MCreator will run the setup again automatically. You choose to copy the error log to"
						+ "<br>the clipboard for further investigation. You can close the MCreator and run it again when you think you fixed the problem."
						+ (s != null ? "<br><br>Additional info: " + s : "") + "<br><br>", "MCreator setup failed!",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (action == 0) {
			runSetup(m, false);
		} else if (action == 1) {
			new PreferencesDialog(m, null);
			runSetup(m, false);
		} else if (action == 2) {
			StringSelection stringSelection = new StringSelection(m.getGradleConsole().getConsoleText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
			m.closeThisMCreator(true);
		} else {
			m.closeThisMCreator(true);
		}
	}

}
