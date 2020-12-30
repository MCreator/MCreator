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

package net.mcreator.vcs;

import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class WorkspaceVCS {

	private static final Logger LOG = LogManager.getLogger("Workspace VCS");

	private VCSInfo info;

	private Git git;

	private WorkspaceVCS(Workspace workspace, VCSInfo info) {
		this.info = info;

		try {
			this.git = Git.init().setDirectory(workspace.getWorkspaceFolder()).call();
		} catch (GitAPIException e) {
			LOG.error("Failed to load repository", e);
		}
	}

	public void setInfo(VCSInfo info) {
		this.info = info;
	}

	public VCSInfo getInfo() {
		return info;
	}

	public Git getGit() {
		return git;
	}

	public CredentialsProvider getCredentialsProvider(File workspaceFolder, Window parent) {
		return new UsernamePasswordCredentialsProvider(info.getUsername(), info.getPassword(workspaceFolder, parent));
	}

	public static WorkspaceVCS initNewVCSWorkspace(Workspace workspace, VCSInfo vcsInfo)
			throws WorkspaceNotEmptyException {
		Git git = null;
		try {
			git = Git.init().setDirectory(workspace.getWorkspaceFolder()).call();

			// add remote first
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("origin");
			remoteAddCommand.setUri(new URIish(vcsInfo.getRemote()));
			remoteAddCommand.call();

			// setup branch "master" section
			StoredConfig config = git.getRepository().getConfig();
			config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", ConfigConstants.CONFIG_KEY_REMOTE,
					"origin");
			config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", ConfigConstants.CONFIG_KEY_MERGE,
					Constants.R_HEADS + "master");
			config.save();

			// try to fetch, reject this workspace if there is existing content in it
			git.fetch().setRemote("origin").setCredentialsProvider(
					new UsernamePasswordCredentialsProvider(vcsInfo.getUsername(), vcsInfo.getPasswordDirect())).call();
			if (git.getRepository().findRef(Constants.FETCH_HEAD) != null) {
				if (git.getRepository().findRef(Constants.FETCH_HEAD).getObjectId() != null) {
					git.getRepository().close();
					git.close();
					FileIO.deleteDir(git.getRepository().getDirectory());
					throw new WorkspaceNotEmptyException();
				}
			}
		} catch (GitAPIException | IOException | URISyntaxException e) {
			if (git != null) {
				git.getRepository().close();
				git.close();
				FileIO.deleteDir(git.getRepository().getDirectory());
			}
			LOG.error("Failed to init GIT repository", e);
		}

		// create gitignore
		String gitignore = FileIO.readResourceToString(PluginLoader.INSTANCE,
				workspace.getGenerator().getGeneratorName() + "/workspace.gitignore");
		FileIO.writeStringToFile(gitignore, new File(workspace.getWorkspaceFolder(), ".gitignore"));

		VCSInfo.saveToFile(vcsInfo, new File(workspace.getFolderManager().getWorkspaceCacheDir(), "vcsInfo"));

		return new WorkspaceVCS(workspace, vcsInfo);
	}

	public static WorkspaceVCS loadVCSWorkspace(Workspace workspace) {
		if (!isVCSInitialized(workspace))
			return null;

		VCSInfo vcsInfo = VCSInfo
				.loadFromFile(new File(workspace.getFolderManager().getWorkspaceCacheDir(), "vcsInfo"));
		if (vcsInfo != null)
			return new WorkspaceVCS(workspace, vcsInfo);
		return null;
	}

	private static boolean isVCSInitialized(Workspace workspace) {
		try {
			if (new File(workspace.getFolderManager().getWorkspaceCacheDir(), "vcsInfo").isFile()) {
				Git git = Git.init().setDirectory(workspace.getWorkspaceFolder()).call();
				Repository repository = git.getRepository();
				return !repository.getRemoteNames().isEmpty();
			}
		} catch (GitAPIException e) {
			LOG.warn("Failed to check for Git repo", e);
		}
		return false;
	}

}
