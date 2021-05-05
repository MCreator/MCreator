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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.AbstractMCreatorTheme;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UpdatePluginDialog {

	public static void showPluginUpdateDialogIfUpdatesExist(Window parent) {
		if (!PluginLoader.INSTANCE.getPluginUpdates().isEmpty()) {
			JPanel pan = new JPanel(new BorderLayout());
			JPanel plugins = new JPanel();
			plugins.setFont(AbstractMCreatorTheme.console_font);
			plugins.setEnabled(false);
			JScrollPane pane = new JScrollPane(plugins);
			pan.add("Center", PanelUtils.maxMargin(pane, 15, true, false, false, false));
			pan.setPreferredSize(new Dimension(585, 290));
			plugins.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

			Object[] options = { "Open all plugin pages", "Remind me later" };
			for (int i = 0; i < PluginLoader.INSTANCE.getPluginUpdates().size(); i++) {
				PluginUpdateInfo plugin = PluginLoader.INSTANCE.getPluginUpdates().get(i);
				JLabel label = L10N
						.label("dialog.plugin_update_notify.version_message", plugin.getPlugin().getInfo().getName(),
								plugin.getPlugin().getInfo().getVersion(), plugin.getNewVersion());
				label.addMouseListener(new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent e) {
						Object[] opts = { "Open", "Close" };
						int option = JOptionPane.showOptionDialog(parent,
								L10N.t("dialog.plugin_update_notify.open_link",
										MCreatorApplication.SERVER_DOMAIN + "/node/" + plugin.getPageId()),
								L10N.t("dialog.plugin_update_notify.open_link.title"), JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
						if (option == 0)
							DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/node/" + plugin.getPageId());
					}
				});
				plugins.add(label);
			}

			pan.add("North", PanelUtils
					.northAndCenterElement(L10N.label("dialog.plugin_update_notify.message"), new JLabel("      ")));
			pan.add("Center", plugins);

			int option = JOptionPane.showOptionDialog(parent, pan, L10N.t("dialog.plugin_update_notify.update_title"),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (option == 0) {
				for (PluginUpdateInfo plug : PluginLoader.INSTANCE.getPluginUpdates()) {
					DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/node/" + plug.getPageId());
				}
			}
		}
	}
}
