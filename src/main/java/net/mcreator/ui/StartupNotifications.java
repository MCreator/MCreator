/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui;

import net.mcreator.plugin.PluginLoadFailure;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.dialogs.UpdateNotifyDialog;
import net.mcreator.ui.dialogs.UpdatePluginDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class StartupNotifications {

	private static boolean notificationsHandled = false;

	public static void handleStartupNotifications(Window parent) {
		if (!notificationsHandled) {
			ThreadUtil.runOnSwingThreadAndWait(() -> {
				UpdateNotifyDialog.showUpdateDialogIfUpdateExists(parent, false);

				showPluginLoadingFailures(parent);

				UpdatePluginDialog.showPluginUpdateDialogIfUpdatesExist(parent);
			});

			notificationsHandled = true;
		}
	}

	private static void showPluginLoadingFailures(Window parent) {
		Collection<PluginLoadFailure> failedPlugins = PluginLoader.INSTANCE.getFailedPlugins();
		if (!failedPlugins.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html>");
			sb.append(L10N.t("dialog.plugin_load_failed.msg1"));
			sb.append("<ul>");
			for (PluginLoadFailure plugin : failedPlugins) {
				sb.append("<li><b>").append(plugin.pluginID()).append("</b> - reason: ")
						.append(StringUtils.abbreviateString(plugin.message(), 100, true))
						.append("<br><small>Location: ").append(plugin.pluginFile()).append("</small></li>");
			}
			sb.append("</ul><br>");
			sb.append(L10N.t("dialog.plugin_load_failed.msg2"));

			JOptionPane.showMessageDialog(parent, sb.toString(), L10N.t("dialog.plugin_load_failed.title"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

}
