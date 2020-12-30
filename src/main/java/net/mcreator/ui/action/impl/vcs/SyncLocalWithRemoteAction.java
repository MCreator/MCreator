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
import net.mcreator.ui.vcs.VCSCommitDialog;
import net.mcreator.util.GSONClone;
import net.mcreator.vcs.ICustomSyncHandler;
import net.mcreator.vcs.SyncTwoRefsWithMerge;
import net.mcreator.workspace.TooNewWorkspaceVerisonException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import net.mcreator.workspace.settings.WorkspaceSettingsChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.MergeMessageFormatter;
import org.eclipse.jgit.transport.CredentialsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class SyncLocalWithRemoteAction extends VCSAction {

	private static final Logger LOG = LogManager.getLogger("VCS to remote");

	public SyncLocalWithRemoteAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.sync_with_remote_workspace"), e -> {
			actionRegistry.getMCreator().setCursor(new Cursor(Cursor.WAIT_CURSOR));

			boolean needsWorkspaceBuildAfter = false;

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

			Status status;
			try {
				git.rm().addFilepattern(".").call();
				git.add().addFilepattern(".").call();
				status = git.status().call();
			} catch (GitAPIException ex) {
				LOG.error("Failed to update untracked files and get repo status", ex);
				actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());
				return;
			}

			// if we have changes, ask user to commit them before syncing
			if (!status.getUncommittedChanges().isEmpty()) {
				String commitMessage = VCSCommitDialog.getVCSCommitDialog(actionRegistry.getMCreator(), status);
				if (commitMessage != null) {
					try {
						// first commit our changes
						CommitCommand commitCommand = git.commit().setAll(true).setMessage(commitMessage);
						try {
							commitCommand = commitCommand.setAuthor(
									actionRegistry.getMCreator().getWorkspace().getVCS().getInfo().getUsername(),
									new PersonIdent(git.getRepository()).getEmailAddress());
						} catch (Exception ignored) {
						}
						commitCommand.call();

						// next we check if there are any commits on the remote
						git.fetch().setRemote("origin").setCredentialsProvider(credentialsProvider).call();
						if (git.getRepository().findRef(Constants.FETCH_HEAD) != null) {
							ObjectId head = git.getRepository().resolve(Constants.HEAD);
							ObjectId fetchHead = git.getRepository().findRef(Constants.FETCH_HEAD).getObjectId();

							String mergeMessage = new MergeMessageFormatter().format(Collections.singletonList(
									new ObjectIdRef.Unpeeled(Ref.Storage.LOOSE, fetchHead.getName(), fetchHead.copy())),
									new ObjectIdRef.Unpeeled(Ref.Storage.LOOSE, head.getName(), head.copy()));

							try {
								Workspace localWorkspace = actionRegistry.getMCreator().getWorkspace();
								WorkspaceSettings preMergeSettings = GSONClone
										.deepClone(localWorkspace.getWorkspaceSettings(), WorkspaceSettings.class);

								// if custom merge handler was required
								needsWorkspaceBuildAfter = SyncTwoRefsWithMerge
										.sync(git, head, fetchHead, mergeHandler, () -> {
											// fix in case if merge was not commited yet
											if (git.getRepository().getRepositoryState()
													== RepositoryState.MERGING_RESOLVED) {
												git.rm().addFilepattern(".").call();
												git.add().addFilepattern(".").call();
												git.commit().setAll(true).setMessage(mergeMessage).call();
											}

											// we pull changes before custom merge handler tasks
											git.pull().setRemote("origin").setCredentialsProvider(credentialsProvider)
													.call();
										}, false).wasCustomMergeHandlerRequired();

								// possible refactor after sync start
								actionRegistry.getMCreator().getWorkspace().reloadFromFS();
								if (!localWorkspace.getWorkspaceSettings().getCurrentGenerator()
										.equals(preMergeSettings.getCurrentGenerator())) {
									LOG.debug("Switching local workspace generator to " + localWorkspace
											.getWorkspaceSettings().getCurrentGenerator());

									WorkspaceGeneratorSetup.cleanupGeneratorForSwitchTo(localWorkspace,
											Generator.GENERATOR_CACHE
													.get(localWorkspace.getWorkspaceSettings().getCurrentGenerator()));
									localWorkspace.switchGenerator(
											localWorkspace.getWorkspaceSettings().getCurrentGenerator());
									WorkspaceGeneratorSetupDialog.runSetup(actionRegistry.getMCreator(), false);
								}
								WorkspaceSettingsChange workspaceSettingsChange = new WorkspaceSettingsChange(
										preMergeSettings, localWorkspace.getWorkspaceSettings());
								if (workspaceSettingsChange.refactorNeeded())
									WorkspaceSettingsAction
											.refactorWorkspace(actionRegistry.getMCreator(), workspaceSettingsChange);
								// possible refactor after sync end

								// we might need to make another commit to commit the merge changes
								try {
									git.rm().addFilepattern(".").call();
									git.add().addFilepattern(".").call();
									git.commit().setAll(true).setAllowEmpty(false)
											.setMessage("MCreator " + mergeMessage).call();
								} catch (Exception ignored) {
								}
							} catch (TooNewWorkspaceVerisonException ex) {
								JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
										L10N.t("dialog.vcs.sync_with_remote_workspace.too_new_error.message"),
										L10N.t("dialog.vcs.sync_with_remote_workspace.too_new_error.title"),
										JOptionPane.ERROR_MESSAGE);
								actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());
								return;
							}

						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
								L10N.t("dialog.vcs.sync_with_remote_workspace.general_fail.message", ex.getMessage()),
								L10N.t("dialog.vcs.sync_with_remote_workspace.general_fail.title"),
								JOptionPane.ERROR_MESSAGE);
						LOG.error("Failed to pull from remote!", ex);
					}

				}
			}

			// push local changes to remote in all cases
			try {
				BranchTrackingStatus trackingStatus = BranchTrackingStatus
						.of(git.getRepository(), git.getRepository().getFullBranch());

				PushCommand pushCommand = git.push();
				pushCommand.setCredentialsProvider(credentialsProvider);
				pushCommand.setRemote("origin").add(git.getRepository().getBranch());
				pushCommand.call();

				if (trackingStatus != null) {
					actionRegistry.getMCreator().statusBar.setPersistentMessage(
							L10N.t("statusbar.vcs.sync_with_remote_workspace.diff_message",
									trackingStatus.getAheadCount(), trackingStatus.getBehindCount()));
				}
			} catch (Exception ex) {
				LOG.error("Failed to push to remote!", ex);
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.vcs.sync_with_remote_workspace.push_fail.message", ex.getMessage()),
						L10N.t("dialog.vcs.sync_with_remote_workspace.push_fail.title"), JOptionPane.ERROR_MESSAGE);
			}

			actionRegistry.getMCreator().mv.updateMods();
			actionRegistry.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (needsWorkspaceBuildAfter)
				actionRegistry.buildWorkspace.doAction();
		});
		setIcon(UIRES.get("16px.vcspush"));
	}

}
