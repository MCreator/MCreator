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

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class HistoryManager implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger(HistoryManager.class);

	@Nullable private final GitHistoryBackend backend;
	private final List<HistoryEvent> pendingEvents = new ArrayList<>();
	private long lastCheckpointMillis = System.currentTimeMillis();

	private final Workspace workspace;

	@Nullable private Runnable checkpointListener = null;
	@Nullable private Consumer<Boolean> busyListener = null;

	public HistoryManager(Workspace workspace) {
		this.workspace = workspace;

		if (PreferencesManager.PREFERENCES.backups.enableLocalHistory.get()) {
			GitHistoryBackend backendToUse = null;
			try {
				backendToUse = new GitHistoryBackend(this);
				backendToUse.setBusyListener(busy -> {
					if (busyListener != null) {
						busyListener.accept(busy);
					}
				});
			} catch (IOException e) {
				LOG.warn("Failed to initialize local history backend, local history will be disabled", e);
			}
			backend = backendToUse;
		} else {
			backend = null;
		}
	}

	/**
	 * Saves checkpoint with a given name.
	 *
	 * @param checkpointName Checkpoint name should reflect the current state of the workspace at the time of the checkpoint.
	 */
	public void checkpoint(String checkpointName, Object... parameters) {
		checkpoint(false, checkpointName, parameters);
	}

	public void importantCheckpoint(String checkpointName, Object... parameters) {
		checkpoint(true, checkpointName, parameters);
	}

	private void checkpoint(boolean important, String checkpointName, Object... parameters) {
		important = true; // TODO: for testing

		pendingEvents.add(
				new HistoryEvent(L10N.t("local_history.checkpoint." + checkpointName, parameters), important));

		if (important || isSaveIntervalElapsed()) {
			flushPendingEventsIntoCheckpoint();
		}
	}

	private boolean isSaveIntervalElapsed() {
		long intervalMillis = PreferencesManager.PREFERENCES.backups.localHistorySaveInterval.get() * 60_000L;
		return System.currentTimeMillis() - lastCheckpointMillis >= intervalMillis;
	}

	private void flushPendingEventsIntoCheckpoint() {
		if (pendingEvents.isEmpty()) {
			return;
		}

		List<HistoryEvent> eventsToCommit = new ArrayList<>(pendingEvents);

		String commitMessage;
		if (eventsToCommit.size() == 1) {
			commitMessage = eventsToCommit.getFirst().eventName();
		} else {
			commitMessage = L10N.t("local_history.checkpoint.and_n_more",
					eventsToCommit.stream().limit(1).map(HistoryEvent::eventName).findFirst().orElse(""),
					eventsToCommit.size() - 1);
		}

		saveCheckpoint(commitMessage, commitResult -> {
			if (commitResult == GitHistoryBackend.CommitResult.SUCCESS) {
				lastCheckpointMillis = System.currentTimeMillis();
				if (checkpointListener != null) {
					checkpointListener.run();
				}
			}

			// Clear events in every case (except if git was busy), as even if saveCheckpoint returned false,
			// this means no changes were needed to be committed, meaning events did not change any files
			if (commitResult != GitHistoryBackend.CommitResult.SKIPPED_GIT_BUSY) {
				pendingEvents.removeAll(eventsToCommit);
			}
		});
	}

	private void saveCheckpoint(String eventName, Consumer<GitHistoryBackend.CommitResult> didCommitCallback) {
		if (backend == null) {
			didCommitCallback.accept(GitHistoryBackend.CommitResult.SKIPPED_EXCEPTION);
			return;
		}

		// Make sure workspace is written to file before commiting so other files are in-sync with workspace
		workspace.getFileManager().saveWorkspaceDirectlyAndWait();

		backend.saveCheckpoint(eventName, didCommitCallback);
	}

	public void setCheckpointListener(@Nullable Runnable listener) {
		checkpointListener = listener;
	}

	public void setBusyListener(@Nullable Consumer<Boolean> listener) {
		busyListener = listener;
	}

	public void getCheckpoints(Consumer<List<HistoryCheckpoint>> callback) {
		if (backend == null) {
			callback.accept(List.of());
			return;
		}

		backend.getCheckpoints(callback);
	}

	public void optimizeStorage() {
		if (backend != null)
			backend.optimizeStorage();
	}

	public void revertToCheckpoint(HistoryCheckpoint checkpoint) throws LocalHistoryException {
		if (backend == null) {
			throw new LocalHistoryException("");
		}

		backend.revertToCheckpoint(checkpoint.hash());
	}

	File getWorkspaceFolder() {
		return workspace.getWorkspaceFolder();
	}

	public boolean isAvailable() {
		return backend != null;
	}

	@Override public synchronized void close() {
		flushPendingEventsIntoCheckpoint();
		if (backend != null) {
			backend.close();
		}
	}

	record HistoryEvent(String eventName, boolean important) {

		@Nonnull @Override public String toString() {
			return eventName;
		}
	}

	public static File getLocalHistoryRoot(File workspaceFolder) {
		return new File(workspaceFolder, ".mcreator/localHistory");
	}

}
