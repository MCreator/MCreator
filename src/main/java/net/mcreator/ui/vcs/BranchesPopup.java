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

package net.mcreator.ui.vcs;

import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.vcs.BranchSwitchAction;
import net.mcreator.vcs.WorkspaceVCS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class BranchesPopup extends JPopupMenu {

	private static final Logger LOG = LogManager.getLogger(BranchesPopup.class);

	public BranchesPopup(WorkspaceVCS workspaceVCS, MCreator mcreator) {
		try {
			Git git = workspaceVCS.getGit();

			JMenuItem newBranch = new JMenuItem("New branch from current");
			add(newBranch);
			newBranch.addActionListener(e -> {
				String newBranchName = JOptionPane.showInputDialog("Enter the name of a new branch:");
				if (newBranchName != null) {
					newBranchName = RegistryNameFixer.fix(newBranchName);
					if (!newBranchName.isEmpty()) {
						try {
							git.branchCreate().setName(newBranchName)
									.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
									.setStartPoint(git.getRepository().getFullBranch()).call();
							git.checkout().setName(newBranchName).call();

							mcreator.mv.updateMods();

							try { // try to push if remote exists
								git.push().setRemote("origin")
										.setRefSpecs(new RefSpec(newBranchName + ":" + newBranchName)).call();
							} catch (Exception ignored) {
							}
						} catch (GitAPIException | IOException er) {
							LOG.error("Failed to create branch", er);
						}
					}
				}
			});

			addSeparator();

			List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

			for (Ref ref : refs) {
				JMenuItem menuItem = new JRadioButtonMenuItem(ref.getName());
				add(menuItem);
				if (git.getRepository().getFullBranch().equals(ref.getName())) {
					menuItem.setSelected(true);
				} else {
					menuItem.addActionListener(
							e -> BranchSwitchAction.switchBranch(mcreator, workspaceVCS, ref.getName()));
				}
			}
		} catch (Exception ignored) {
		}
	}

}
