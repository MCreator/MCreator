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

import net.mcreator.generator.Generator;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.workspace.WorkspaceSettingsAction;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.vcs.MCreatorWorkspaceSyncHandler;
import net.mcreator.util.GSONClone;
import net.mcreator.vcs.ICustomSyncHandler;
import net.mcreator.vcs.SyncTwoRefsWithMerge;
import net.mcreator.workspace.TooNewWorkspaceVerisonException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;

import javax.swing.*;
import java.awt.*;

public class SyncRemoteToLocalAction extends VCSAction {

	private static final Logger LOG = LogManager.getLogger("VCS from remote");

	public SyncRemoteToLocalAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.pull"), e -> {
			actionRegistry.getMCreator().setCursor(new Cursor(Cursor.WAIT_CURSOR));

			// save workspace to FS first here, so the changes get detected by git
			actionRegistry.getMCreator().getFileManager().saveWorkspaceDirectlyAndWait();
			// generate base at this point too
			actionRegistry.getMCreator().getGenerator().generateBase();

			Git git = actionRegistry.getMCreator().getWorkspace().getVCS().getGit();

			CredentialsProvider credentialsProvider = actionRegistry.getMCreator().getWorkspace().getVCS()
					.getCredentialsProvider(
							actionRegistry.getMCreator().getWorkspaceFolder(),
							actionRegistry.getMCreator());

			ICustomSyncHandler mergeHandler = new MCreatorWorkspaceSyncHandler(actionRegistry.getMCreator());

			try {
				// first we fetch remote changes
				git.fetch().setRemote("origin").setCredentialsProvider(credentialsProvider).call();

				// check if we fetched anything
				if (git.getRepository().findRef(Constants.FETCH_HEAD) == null
						|| git.getRepository().findRef(Constants.FETCH_HEAD).getObjectId() == null) {
					actionRegistry.getMCreator().statusBar
							.setPersistentMessage(L10N.t("statusbar.vcs.pull.no_commits"));
					actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());
					return;
				}

				// stash local changes
				git.rm().addFilepattern(".").call();
				git.add().addFilepattern(".").call();
				RevCommit stash = git.stashCreate().setIncludeUntracked(true).call();

				ObjectId presyncPointer = stash;
				if (presyncPointer == null) // if there are no changes, stash is null
					presyncPointer = git.getRepository().resolve(Constants.HEAD);

				ObjectId fetchHead = git.getRepository().findRef(Constants.FETCH_HEAD).getObjectId();

				// next we do a dry run of the merge to see if we can silently merge workspaces
				SyncTwoRefsWithMerge.SyncResult syncResult;
				try {
					syncResult = SyncTwoRefsWithMerge.sync(git, presyncPointer, fetchHead, mergeHandler, null, true);
				} catch (TooNewWorkspaceVerisonException ex) {
					// unstash the stash as we will not be using it
					if (stash != null)
						git.stashApply().setStashRef(stash.getName()).call();

					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.vcs.error.remote_is_newer.message"),
							L10N.t("dialog.vcs.error.remote_is_newer.title"), JOptionPane.ERROR_MESSAGE);
					actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());
					return;
				}

				// we can pull from remote only if custom merge handler was not required and no user interaction was required
				if (!syncResult.wasCustomMergeHandlerRequired() && !syncResult.wasUserActionRequired()) {
					Workspace localWorkspace = actionRegistry.getMCreator().getWorkspace();
					WorkspaceSettings preMergeSettings = GSONClone
							.deepClone(localWorkspace.getWorkspaceSettings(), WorkspaceSettings.class);

					// pull changes from remote before unstashing
					git.pull().setRemote("origin").setCredentialsProvider(credentialsProvider).call();

					// unstash local changes
					if (stash != null)
						git.stashApply().setStashRef(stash.getName()).call();

					// possible refactor after sync start
					actionRegistry.getMCreator().getWorkspace().reloadFromFS();

					localWorkspace = actionRegistry.getMCreator().getWorkspace();
					if (!localWorkspace.getWorkspaceSettings().getCurrentGenerator()
							.equals(preMergeSettings.getCurrentGenerator())) {
						LOG.debug("Switching local workspace generator to " + localWorkspace.getWorkspaceSettings()
								.getCurrentGenerator());
						WorkspaceGeneratorSetup.cleanupGeneratorForSwitchTo(localWorkspace, Generator.GENERATOR_CACHE
								.get(localWorkspace.getWorkspaceSettings().getCurrentGenerator()));
						localWorkspace.switchGenerator(localWorkspace.getWorkspaceSettings().getCurrentGenerator());
						WorkspaceGeneratorSetupDialog.runSetup(actionRegistry.getMCreator(), false);
					}
					WorkspaceSettingsChange workspaceSettingsChange = new WorkspaceSettingsChange(preMergeSettings,
							localWorkspace.getWorkspaceSettings());
					if (workspaceSettingsChange.refactorNeeded())
						WorkspaceSettingsAction
								.refactorWorkspace(actionRegistry.getMCreator(), workspaceSettingsChange);
					// possible refactor after sync end

					actionRegistry.getMCreator().statusBar
							.setPersistentMessage(L10N.t("statusbar.vcs.pull.changes_synced"));
					actionRegistry.getMCreator().mv.updateMods();
				} else {
					// unstash the stash as we will not be using it
					if (stash != null)
						git.stashApply().setStashRef(stash.getName()).call();

					JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
							L10N.t("dialog.vcs.error.local_changes_not_synced.message"),
							L10N.t("dialog.vcs.error.local_changes_not_synced.title"), JOptionPane.WARNING_MESSAGE);
					actionRegistry.getMCreator().statusBar
							.setPersistentMessage(L10N.t("statusbar.vcs.pull.local_changes_not_synced"));
				}
			} catch (Exception ex) {
				LOG.error("Sync from remote failed", ex);
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.vcs.error.sync_failed.message", ex.getMessage()),
						L10N.t("dialog.vcs.error.sync_failed.title"), JOptionPane.ERROR_MESSAGE);
			}

			actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());
		});
		setIcon(UIRES.get("16px.vcspull"));
	}

}
