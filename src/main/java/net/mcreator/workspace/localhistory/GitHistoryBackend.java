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
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// TODO: make this whole backend async in single thread executor, except for indexdiff that will run in another thread
// TODO: support staging files with file tracking so we are sure files are not missed?
class GitHistoryBackend implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger(GitHistoryBackend.class);

	private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("GitHistoryBackend");
		thread.setUncaughtExceptionHandler((_, e) -> LOG.error(e));
		return thread;
	});

	private final Git git;

	private final HistoryManager historyManager;

	GitHistoryBackend(HistoryManager historyManager) throws IOException {
		this.historyManager = historyManager;

		File workspaceRoot = historyManager.getWorkspaceFolder();
		File historyDatabaseDir = HistoryManager.getLocalHistoryRoot(workspaceRoot);

		boolean isNewRepo = !new File(historyDatabaseDir, "HEAD").isFile();

		if (isNewRepo) {
			// Delete any potential stale files
			if (historyDatabaseDir.isDirectory()) {
				FileIO.deleteDir(historyDatabaseDir);
			}

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

		this.git = new Git(repository);

		StoredConfig config = git.getRepository().getConfig();
		config.setBoolean("core", null, "autocrlf", false);
		config.setBoolean("core", null, "filemode", false);
		config.setString("core", null, "sha1Implementation", "jdkNative");
		config.setInt("index", null, "version", 4);
		config.setInt("pack", null, "threads", 0);
		config.save();

		configureIgnores(historyDatabaseDir);

		if (isNewRepo) {
			saveCheckpoint(L10N.t("local_history.checkpoint.initial"), true);
			LOG.debug("Initialized local history repository");
		} else {
			LOG.debug("Loaded local history repository");
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
		ignores.add(".mcreator/");
		ignores.add("build/");
		ignores.add("run/");
		ignores.add("runs/");
		FileIO.writeStringToFile(String.join("\n", ignores), excludeFile);
	}

	/**
	 * @param commitMessage Commit message to use for the checkpoint
	 * @return true if the commit was successful, false if no changes to commit
	 */
	boolean saveCheckpoint(String commitMessage) {
		return saveCheckpoint(commitMessage, false);
	}

	/**
	 * @param commitMessage Commit message to use for the checkpoint
	 * @param initialCommit if true, skip the change-detection scan and stage the entire workspace in one pass
	 * @return true if the commit was successful, false if no changes to commit
	 */
	boolean saveCheckpoint(String commitMessage, boolean initialCommit) {
		return run(() -> {
			try {
				if (initialCommit) {
					// Empty index: one tree walk to stage everything
					git.add().addFilepattern(".").call();
				} else {
					Repository repo = git.getRepository();

					// TODO: for large workspaces, this takes tens of seconds even if only one file is changed
					IndexDiff diff = new IndexDiff(repo, Constants.HEAD, new FileTreeIterator(repo));
					if (!diff.diff()) {
						return false;
					}

					stageChanges(diff);
				}

				RevCommit commit = git.commit().setMessage(commitMessage).call();
				LOG.debug("Saved local history checkpoint '{}' as {}", commitMessage, commit.getName());
				return true;
			} catch (GitAPIException | IOException e) {
				LOG.warn("Failed to save local history checkpoint: {}", e.getMessage());
				return false;
			}
		});
	}

	private void stageChanges(IndexDiff diff) throws GitAPIException {
		if (!diff.getUntracked().isEmpty() || !diff.getModified().isEmpty()) {
			AddCommand add = git.add();
			for (String path : diff.getUntracked()) {
				add.addFilepattern(path);
			}
			for (String path : diff.getModified()) {
				add.addFilepattern(path);
			}
			add.call();
		}
		if (!diff.getMissing().isEmpty()) {
			AddCommand update = git.add().setUpdate(true);
			for (String path : diff.getMissing()) {
				update.addFilepattern(path);
			}
			update.call();
		}
	}

	List<HistoryCheckpoint> getCheckpoints() {
		return run(() -> {
			List<HistoryCheckpoint> history = new ArrayList<>();
			try {
				for (RevCommit commit : git.log().call()) {
					history.add(new HistoryCheckpoint(commit.getName(), commit.getFullMessage(), commit.getCommitTime(),
							() -> getDiffEntries(commit)));
				}
			} catch (GitAPIException e) {
				LOG.warn("Failed to retrieve local history checkpoints", e);
			}
			return history;
		});
	}

	private List<HistoryCheckpoint.DiffEntry> getDiffEntries(RevCommit commit) {
		List<HistoryCheckpoint.DiffEntry> diffList = new ArrayList<>();
		try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
			df.setRepository(git.getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			try {
				RevCommit parent = commit.getParentCount() > 0 ? commit.getParent(0) : null;
				if (parent != null) {
					for (DiffEntry diff : df.scan(parent.getTree(), commit.getTree())) {
						switch (diff.getChangeType()) {
						case ADD -> diffList.add(
								new HistoryCheckpoint.DiffEntry(HistoryCheckpoint.ChangeType.ADD, diff.getNewPath()));
						case MODIFY -> diffList.add(new HistoryCheckpoint.DiffEntry(HistoryCheckpoint.ChangeType.MODIFY,
								diff.getNewPath()));
						case DELETE -> diffList.add(new HistoryCheckpoint.DiffEntry(HistoryCheckpoint.ChangeType.REMOVE,
								diff.getOldPath()));
						case RENAME -> diffList.add(new HistoryCheckpoint.DiffEntry(HistoryCheckpoint.ChangeType.RENAME,
								diff.getNewPath()));
						case COPY -> diffList.add(
								new HistoryCheckpoint.DiffEntry(HistoryCheckpoint.ChangeType.COPY, diff.getNewPath()));
						default -> throw new IllegalStateException("Unexpected value: " + diff.getChangeType());
						}
					}
				}
			} catch (IOException e) {
				LOG.warn("Failed to calculate diff for commit {}: {}", commit.getName(), e.getMessage());
			}
		}
		return diffList;
	}

	void revertToCheckpoint(String checkpointHash) throws LocalHistoryException {
		try {
			executor.submit((Callable<Void>) () -> {
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
					HistoryCheckpoint checkpoint = new HistoryCheckpoint(targetCommit.getName(),
							targetCommit.getFullMessage(), targetCommit.getCommitTime(), List::of);
					historyManager.importantCheckpoint("revert", checkpoint.getTimestampString());
				} catch (Exception e) {
					throw new LocalHistoryException("Failed to revert to checkpoint " + checkpointHash, e);
				}
				return null;
			}).get();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof LocalHistoryException localHistoryException) {
				throw localHistoryException;
			}
			throw new LocalHistoryException("Failed to revert to checkpoint " + checkpointHash, cause);
		} catch (InterruptedException _) {
		}
	}

	@Override public void close() {
		try {
			executor.submit(git::close).get();
		} catch (InterruptedException _) {
		} catch (ExecutionException e) {
			LOG.warn("Failed to close local history: {}", e.getCause().getMessage());
		} finally {
			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}
	}

	boolean optimizeStorage() {
		return run(() -> {
			try {
				git.gc().call();
				LOG.debug("Optimized local history storage");
				return true;
			} catch (GitAPIException e) {
				LOG.warn("Failed to optimize local history storage: {}", e.getMessage());
				return false;
			}
		});
	}

	private <T> T run(Callable<T> task) {
		try {
			return executor.submit(task).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while waiting for git history operation", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			if (cause instanceof Error error) {
				throw error;
			}
			throw new IllegalStateException(cause);
		}
	}

}