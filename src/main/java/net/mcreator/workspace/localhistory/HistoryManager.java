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

package net.mcreator.workspace.localhistory;

import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class HistoryManager implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger(HistoryManager.class);

	@Nullable private final Git git;
	private final ReentrantLock lock = new ReentrantLock();

	public HistoryManager(Workspace workspace) {
		// TODO: make it configurable in workspace settings, on by default

		File historyDatabaseDir = new File(workspace.getFolderManager().getWorkspaceCacheDir(), "localHistory");
		File workspaceRoot = workspace.getWorkspaceFolder();

		Git localGit = null;
		try {
			boolean isNewRepo = !new File(historyDatabaseDir, "HEAD").exists();
			if (isNewRepo) {
				localGit = Git.init().setDirectory(workspaceRoot).setGitDir(historyDatabaseDir).setBare(false).call();
			} else {
				Repository repository = new FileRepositoryBuilder().setGitDir(historyDatabaseDir)
						.setWorkTree(workspaceRoot).setup().build();
				localGit = new Git(repository);
			}

			// Enforce safe configurations
			StoredConfig config = localGit.getRepository().getConfig();
			config.setBoolean("core", null, "autocrlf", false);
			config.setString("core", null, "worktree", workspaceRoot.getAbsolutePath());
			config.save();

			configureIgnores(historyDatabaseDir);

			if (isNewRepo) {
				saveCheckpoint("Initial workspace state");
				LOG.debug("Initialized local history repository");
			}
		} catch (IOException | GitAPIException e) {
			LOG.warn("Failed to initialize local history: {}", e.getMessage());
		}

		git = localGit;
	}

	private void configureIgnores(File historyDatabaseDir) {
		File excludeFile = new File(historyDatabaseDir, "info/exclude");
		if (excludeFile.isFile())
			return;

		excludeFile.getParentFile().mkdirs();

		List<String> ignores = new ArrayList<>();
		ignores.add(".git/");
		ignores.add(".gradle/");
		ignores.add("build/");
		ignores.add(".mcreator/");
		ignores.add("run/");
		ignores.add("runs/");
		FileIO.writeStringToFile(String.join("\n", ignores), excludeFile);
	}

	public void saveCheckpoint(String eventName) {
		if (git == null) {
			return;
		}

		lock.lock();
		try {
			Status status = git.status().call();
			if (status.isClean()) {
				return;
			}

			if (!status.getUntracked().isEmpty() || !status.getModified().isEmpty()) {
				git.add().addFilepattern(".").call();
			}

			// Bulk stage all deleted (missing) files
			if (!status.getMissing().isEmpty()) {
				git.add().setUpdate(true).addFilepattern(".").call();
			}

			git.commit().setMessage(eventName).setAuthor("MCreator Local History", "system@localhost").call();
		} catch (GitAPIException e) {
			LOG.warn("Failed to save local history checkpoint: {}", e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	public List<Checkpoint> getCheckpoints() {
		if (git == null) {
			return List.of();
		}

		List<Checkpoint> history = new ArrayList<>();

		lock.lock();
		try {
			for (RevCommit commit : git.log().call()) {
				history.add(new Checkpoint(commit.getName(), commit.getFullMessage(), commit.getCommitTime()));
			}
		} catch (GitAPIException e) {
			LOG.warn("Failed to retrieve local history checkpoints: {}", e.getMessage());
		} finally {
			lock.unlock();
		}

		return history;
	}

	public void revertToCheckpoint(String checkpointHash) {
		if (git == null) {
			return;
		}

		lock.lock();
		try (RevWalk walk = new RevWalk(git.getRepository())) {
			// Resolve the target commit
			ObjectId targetId = git.getRepository().resolve(checkpointHash);
			RevCommit targetCommit = walk.parseCommit(targetId);

			// Safe, exact snapshot restore. Replaces working tree and index exactly.
			DirCache dirc = git.getRepository().lockDirCache();
			try {
				DirCacheCheckout dco = new DirCacheCheckout(git.getRepository(), git.getRepository().readDirCache(),
						targetCommit.getTree());
				dco.setFailOnConflict(false);
				dco.checkout();
			} finally {
				dirc.unlock();
			}

			// Wipe out any untracked files or directories that did not exist in this checkpoint
			// to guarantee a strict 1:1 workspace reset.
			git.clean().setCleanDirectories(true).call();

			// Immediately save this reverted state as a new event on the timeline
			saveCheckpoint("Reverted to checkpoint: " + checkpointHash.substring(0, 7));
		} catch (Exception e) {
			LOG.warn("Failed to revert to checkpoint {}: {}", checkpointHash, e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	@Override public void close() {
		if (git != null) {
			git.close();
		}
	}

	public record Checkpoint(String hash, String message, int timestamp) {}

}
