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

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class BuildWorkspaceAction extends GradleAction {

	private static final Logger LOG = LogManager.getLogger(BuildWorkspaceAction.class);

	public BuildWorkspaceAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.gradle.build_workspace"), evt -> new Thread(() -> {
			actionRegistry.getMCreator().getGradleConsole()
					.markRunning(); // so console gets locked while we generate code already
			try {
				actionRegistry.getMCreator().getGenerator().generateBase();
				SwingUtilities.invokeLater(() -> actionRegistry.getMCreator().getGradleConsole().exec("build"));
			} catch (Exception e) { // if something fails, we still need to free the gradle console
				LOG.error(e.getMessage(), e);
				actionRegistry.getMCreator().getGradleConsole().markReady();
			}
		}).start());
		setTooltip(L10N.t("action.gradle.build_workspace.tooltip"));
	}

}
