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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.vcs.LocalChangesPanel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;

public class ShowLocalChangesAction extends VCSAction {

	public ShowLocalChangesAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.show_unsynced_changes"), e -> {
			Git git = actionRegistry.getMCreator().getWorkspace().getVCS().getGit();
			try {
				git.rm().addFilepattern(".").call();
				git.add().addFilepattern(".").call();
				Status status = git.status().call();
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(), new LocalChangesPanel(status),
						L10N.t("dialog.vcs.show_unsynced_changes.title"), JOptionPane.PLAIN_MESSAGE);
			} catch (GitAPIException ignored) {
			}
		});
	}

}
