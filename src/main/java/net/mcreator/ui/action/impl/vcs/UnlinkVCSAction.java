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

import net.mcreator.io.FileIO;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import org.eclipse.jgit.api.Git;

import javax.swing.*;
import java.io.File;

public class UnlinkVCSAction extends VCSAction {

	public UnlinkVCSAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.unlink"), e -> {
			int n = JOptionPane.showConfirmDialog(actionRegistry.getMCreator(), L10N.t("dialog.vcs.unlink.message"),
					L10N.t("dialog.vcs.unlink.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == JOptionPane.YES_OPTION) {
				Git git = actionRegistry.getMCreator().getWorkspace().getVCS().getGit();
				FileIO.deleteDir(git.getRepository().getDirectory());
				new File(actionRegistry.getMCreator().getFolderManager().getWorkspaceCacheDir(),
						"vcsInfo").delete();
				actionRegistry.getMCreator().getWorkspace().setVCS(null);
				actionRegistry.getActions().stream().filter(action -> action instanceof VCSAction)
						.forEach(action -> ((VCSAction) action).vcsStateChanged());
				actionRegistry.getMCreator().statusBar.reloadVCSStatus();
			}
		});
	}

}
