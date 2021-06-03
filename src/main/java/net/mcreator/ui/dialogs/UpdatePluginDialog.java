/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.plugin.PluginUpdateInfo;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;

public class UpdatePluginDialog {

	public static void showPluginUpdateDialogIfUpdatesExist(Window parent) {
		if (PreferencesManager.PREFERENCES.notifications.checkAndNotifyForPluginUpdates && !PluginLoader.INSTANCE
				.getPluginUpdates().isEmpty()) {
			JPanel pan = new JPanel(new BorderLayout(10, 15));

			JPanel plugins = new JPanel(new GridLayout(0, 1, 10, 10));

			pan.add("North", L10N.label("dialog.plugin_update_notify.message"));
			pan.add("Center", new JScrollPane(PanelUtils.pullElementUp(plugins)));
			pan.setPreferredSize(new Dimension(560, 250));

			for (PluginUpdateInfo pluginUpdateInfo : PluginLoader.INSTANCE.getPluginUpdates()) {
				JLabel label = L10N.label("dialog.plugin_update_notify.version_message",
						pluginUpdateInfo.getPlugin().getInfo().getName(),
						pluginUpdateInfo.getPlugin().getInfo().getVersion(), pluginUpdateInfo.getNewVersion());

				JButton update = L10N.button("dialog.plugin_update_notify.update");
				update.addActionListener(e -> DesktopUtils.browseSafe(
						MCreatorApplication.SERVER_DOMAIN + "/node/" + pluginUpdateInfo.getPlugin().getInfo()
								.getPluginPageID()));

				plugins.add(PanelUtils.westAndEastElement(label, update));
			}

			JOptionPane.showOptionDialog(parent, pan, L10N.t("dialog.plugin_update_notify.update_title"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					new Object[] { L10N.t("dialog.plugin_update_notify.close") }, "");
		}
	}
}
