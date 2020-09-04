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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.vcs.VCSSetupDialogs;
import net.mcreator.vcs.VCSInfo;
import net.mcreator.vcs.WorkspaceNotEmptyException;
import net.mcreator.vcs.WorkspaceVCS;

import javax.swing.*;

public class SetupVCSAction extends VCSAction {

	public SetupVCSAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.setup"),
				e -> setupVCSForWorkspaceIfNotYet(actionRegistry.getMCreator()));
	}

	@Override public void setEnabled(boolean b) {
		super.setEnabled(b);
		setTooltip(prevTooltip);
	}

	@Override public boolean isEnabled() {
		return actionRegistry.getMCreator().getWorkspace().getVCS() == null;
	}

	public static boolean setupVCSForWorkspaceIfNotYet(MCreator mcreator) {
		WorkspaceVCS vcs = mcreator.getWorkspace().getVCS();
		if (vcs == null) {
			VCSInfo vcsInfo = VCSSetupDialogs.getVCSInfoDialog(mcreator, L10N.t("dialog.vcs.setup.message"));
			if (vcsInfo != null) {
				try {
					mcreator.getWorkspace().setVCS(WorkspaceVCS.initNewVCSWorkspace(mcreator.getWorkspace(), vcsInfo));
					mcreator.actionRegistry.getActions().stream().filter(action -> action instanceof VCSAction)
							.forEach(action -> ((VCSAction) action).vcsStateChanged());
					mcreator.statusBar.reloadVCSStatus();
				} catch (WorkspaceNotEmptyException e) {
					JOptionPane
							.showMessageDialog(mcreator, L10N.t("dialog.vcs.setup.workspace_folder_not_empty.message"),
									L10N.t("dialog.vcs.setup.workspace_folder_not_empty.title"),
									JOptionPane.ERROR_MESSAGE);
					return false;
				}
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

}
