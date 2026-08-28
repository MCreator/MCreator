/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import net.mcreator.plugin.Plugin;
import net.mcreator.ui.component.util.ThreadUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PluginApprovalDialog {

	/**
	 * <p>Asks the user whether to load the given plugin or not. This dialog is shown while plugins are still
	 * being loaded, before UI themes and translations are initialized, so it uses the default UI theme and
	 * English text only.</p>
	 *
	 * @param plugin The plugin to be approved or denied by the user
	 * @return True if the user approved loading of the plugin, false otherwise
	 */
	public static boolean promptPluginApproval(Plugin plugin) {
		String name = plugin.getInfo() != null && plugin.getInfo().getName() != null ?
				plugin.getInfo().getName() :
				plugin.getID();
		String version = plugin.getInfo() != null ? plugin.getPluginVersion() : "-";

		String message = "<html><body style=\"width: 460px\">"
				+ "MCreator detected a new or changed plugin that is not approved to load yet:<br><br>" + "Plugin: "
				+ name + " (version " + version + ")<br>" + "File: " + plugin.getFile().getAbsolutePath() + "<br>"
				+ "SHA-256: " + plugin.getSHA256() + "<br><br>"
				+ "Plugins have full access to your workspaces and your computer. "
				+ "Only load plugins that come from sources you trust.<br><br>"
				+ "Do you want to load this plugin? You can change this decision later in preferences.";

		AtomicBoolean retval = new AtomicBoolean(false);
		ThreadUtil.runOnSwingThreadAndWait(() -> {
			int result = JOptionPane.showConfirmDialog(null, message, "MCreator plugin approval",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			retval.set(result == JOptionPane.YES_OPTION);
		});

		return retval.get();
	}

	private PluginApprovalDialog() {
	}

}
