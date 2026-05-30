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

class GitHistoryBackend implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger(GitHistoryBackend.class);

	private final Git git;
	private final ReentrantLock lock = new ReentrantLock();

	private GitHistoryBackend(Git git) {
		this.git = git;
	}

	@Nullable static GitHistoryBackend tryCreate(File workspaceRoot) {
		File historyDatabaseDir = new File(workspaceRoot, ".mcreator/localHistory");

		try {
			boolean isNewRepo = !new File(historyDatabaseDir, "HEAD").exists();

			if (isNewRepo) {
				try (Repository initRepo = new FileRepositoryBuilder().setGitDir(historyDatabaseDir).setBare()
						.build()) {

					initRepo.create(true);

					StoredConfig initConfig = initRepo.getConfig();
					initConfig.setBoolean("core", null, "bare", false);
					initConfig.setString("core", null, "worktree", workspaceRoot.getAbsolutePath());
					initConfig.save();
				}
			}

			Repository repository = new FileRepositoryBuilder().setGitDir(historyDatabaseDir).setWorkTree(workspaceRoot)
					.build();

			Git git = new Git(repository);

			StoredConfig config = git.getRepository().getConfig();
			config.setBoolean("core", null, "autocrlf", false);
			config.save();

			configureIgnores(historyDatabaseDir);

			GitHistoryBackend backend = new GitHistoryBackend(git);
			if (isNewRepo) {
				backend.saveCheckpoint(HistoryManager.commitMessageFromEvents(List.of("Initial checkpoint")));
				LOG.debug("Initialized local history repository");
			} else {
				LOG.debug("Loaded local history repository");
			}
			return backend;

		} catch (IOException e) {
			LOG.warn("Failed to initialize local history: {}", e.getMessage());
			return null;
		}
	}

	private static void configureIgnores(File historyDatabaseDir) {
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

	/**
	 * @param eventName Name of commit
	 * @return true if the commit was successful, false if no changes to commit
	 */
	boolean saveCheckpoint(String eventName) {
		lock.lock();
		try {
			Status status = git.status().call();
			if (status.isClean()) {
				return false;
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

		return true;
	}

	List<HistoryCheckpoint> getCheckpoints() {
		List<HistoryCheckpoint> history = new ArrayList<>();

		lock.lock();
		try {
			for (RevCommit commit : git.log().call()) {
				history.add(new HistoryCheckpoint(commit.getName(), commit.getFullMessage(), commit.getCommitTime()));
			}
		} catch (GitAPIException e) {
			LOG.warn("Failed to retrieve local history checkpoints: {}", e.getMessage());
		} finally {
			lock.unlock();
		}

		return history;
	}

	void revertToCheckpoint(String checkpointHash) throws LocalHistoryException {
		lock.lock();
		try (RevWalk walk = new RevWalk(git.getRepository())) {
			// Resolve the target commit
			ObjectId targetId = git.getRepository().resolve(checkpointHash);
			RevCommit targetCommit = walk.parseCommit(targetId);

			// Safe, exact snapshot restore. Replaces working tree and index exactly.
			DirCache dirc = git.getRepository().lockDirCache();
			try {
				DirCacheCheckout dco = new DirCacheCheckout(git.getRepository(), dirc, targetCommit.getTree());
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
			throw new LocalHistoryException("Failed to revert to checkpoint " + checkpointHash, e);
		} finally {
			lock.unlock();
		}
	}

	@Override public void close() {
		lock.lock();
		try {
			git.close();
		} finally {
			lock.unlock();
		}
	}

}