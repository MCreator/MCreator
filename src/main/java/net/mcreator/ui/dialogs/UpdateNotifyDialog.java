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

package net.mcreator.ui.dialogs;

import net.mcreator.Launcher;
import net.mcreator.io.net.api.update.Release;
import net.mcreator.io.net.api.update.UpdateInfo;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.MCreatorVersionNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class UpdateNotifyDialog {

	private static final Logger LOG = LogManager.getLogger("UpdateNotify");

	public static void showUpdateDialogIfUpdateExists(Window parent, boolean showNoUpdates) {
		if (MCreatorApplication.isInternet) {
			long oldMajor = Launcher.version.majorlong;
			UpdateInfo updateInfo = MCreatorApplication.WEB_API.getUpdateInfo();
			if (updateInfo != null) {
				long newMajor = MCreatorVersionNumber.majorStringToLong(updateInfo.getLatestMajor());
				if (newMajor > oldMajor && (PreferencesManager.PREFERENCES.notifications.checkAndNotifyForUpdates
						|| Launcher.version.isSnapshot())) {
					JPanel pan = new JPanel(new BorderLayout());
					JLabel upde = L10N
							.label("dialog.update_notify.message", Launcher.version.major, updateInfo.getLatestMajor());

					ComponentUtils.deriveFont(upde, 13);
					pan.add("North", upde);
					JTextPane ar = new JTextPane();
					ar.setFont(MCreatorTheme.console_font);
					ar.setEnabled(false);
					ar.setMargin(new Insets(5, 10, 5, 5));
					DefaultCaret caret = (DefaultCaret) ar.getCaret();
					caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
					ar.setContentType("text/html");
					JScrollPane pane = new JScrollPane(ar);
					pan.add(new JLabel("   "));
					pan.add("Center", PanelUtils.maxMargin(pane, 15, true, false, false, false));
					pan.setPreferredSize(new Dimension(585, 290));
					ar.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

					ar.setText(fullChangelog(updateInfo));

					Object[] options = { "Open download page", "Remind me later" };
					int option = JOptionPane.showOptionDialog(parent, pan, L10N.t("dialog.update_notify.update_title"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (option == 0) {
						DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/download#update");
					}
				} else {
					Release thisRelease = updateInfo.getReleases().get(Launcher.version.major);
					if (thisRelease != null) {
						if (Launcher.version.buildlong < Long.parseLong(thisRelease.getLatestBuild()) && (
								PreferencesManager.PREFERENCES.notifications.checkAndNotifyForPatches
										|| Launcher.version.isSnapshot())) {
							JPanel pan = new JPanel(new BorderLayout());
							JLabel upde = L10N.label("dialog.update_notify.more_recent_build", Launcher.version.major,
									Launcher.version.build,
									updateInfo.getReleases().get(Launcher.version.major).getLatestBuild(),
									Launcher.version.major);

							ComponentUtils.deriveFont(upde, 13);
							pan.add("North", upde);
							JTextPane ar = new JTextPane();
							ar.setFont(MCreatorTheme.console_font);
							ar.setEnabled(false);
							ar.setMargin(new Insets(5, 10, 5, 5));
							DefaultCaret caret = (DefaultCaret) ar.getCaret();
							caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
							ar.setContentType("text/html");
							JScrollPane pane = new JScrollPane(ar);
							pan.add(new JLabel("   "));
							pan.add("Center", PanelUtils.maxMargin(pane, 15, true, false, false, false));
							pan.setPreferredSize(new Dimension(585, 290));
							ar.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

							ar.setText(
									releaseChangelog(updateInfo.getReleases().get(Launcher.version.major).getBuilds(),
											Launcher.version.buildlong));

							Object[] options = { "Open download page", "Remind me later" };
							int option = JOptionPane
									.showOptionDialog(parent, pan, L10N.t("dialog.update_notify.update_title"),
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
											options[0]);
							if (option == 0) {
								DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/download#updatebuild");
							}
						} else if (showNoUpdates) {
							showNoUpdates(parent);
						}
					} else if (showNoUpdates) {
						showNoUpdates(parent);
					}
				}
			} else if (showNoUpdates) {
				showNoUpdates(parent);
			}
		} else if (showNoUpdates) {
			JOptionPane.showMessageDialog(parent, L10N.t("dialog.update_notify.error_failed_check_internet_message"),
					L10N.t("dialog.update_notify.error_failed_check_internet_title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	private static void showNoUpdates(Window parent) {
		JOptionPane.showMessageDialog(parent, L10N.t("dialog.update_notify.no_update_message"),
				L10N.t("dialog.update_notify.no_update_title"), JOptionPane.INFORMATION_MESSAGE);
	}

	private static String fullChangelog(UpdateInfo updateInfo) {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Release> release : updateInfo.getReleases().entrySet()) {
			if (MCreatorVersionNumber.majorStringToLong(release.getKey()) <= Launcher.version.majorlong)
				continue;

			sb.append("<b>MCreator ").append(release.getKey()).append("</b><br>");
			if (release.getValue().getBuilds() != null) {
				for (Release.Build build : release.getValue().getBuilds()) {
					List<String> changes = build.getChangelog();
					if (changes != null) {
						for (String change : changes) {
							sb.append("- ").append(change).append("<br>");
						}
					}
				}
			}
			sb.append("<br>");
		}
		return sb.toString();
	}

	private static String releaseChangelog(@Nullable List<Release.Build> changelog, long currentbuildlong) {
		StringBuilder sb = new StringBuilder();

		if (changelog != null) {
			for (Release.Build build : changelog) {
				if (currentbuildlong < Long.parseLong(build.getBuild())) {
					sb.append("<b>MCreator ").append(Launcher.version.major).append(".").append(build.getBuild())
							.append("</b><br>");
					List<String> changes = build.getChangelog();
					if (changes != null) {
						for (String change : changes) {
							sb.append("- ").append(change).append("<br>");
						}
					}
					sb.append("<br>");
				}
			}
		}
		return sb.toString();
	}

}
