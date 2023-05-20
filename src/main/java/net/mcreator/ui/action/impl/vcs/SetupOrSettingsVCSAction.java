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

package net.mcreator.ui.action.impl.vcs;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.vcs.WorkspaceVCS;

public class SetupOrSettingsVCSAction extends BasicAction implements VCSStateChangeListener {

	public SetupOrSettingsVCSAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.setup_settings"), e -> {
			if (WorkspaceVCS.getVCSWorkspace(actionRegistry.getMCreator().getWorkspace()) == null) {
				actionRegistry.setupVCS.doAction();
			} else {
				actionRegistry.remoteWorkspaceSettings.doAction();
			}
		});

		// Force initial update of the icon.
		vcsStateChanged();
	}

	@Override public void vcsStateChanged() {
		if (WorkspaceVCS.getVCSWorkspace(actionRegistry.getMCreator().getWorkspace()) == null) {
			setIcon(UIRES.get("16px.setupvcs"));
		} else {
			setIcon(UIRES.get("16px.vcs"));
		}
	}
}
