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

import com.google.gson.Gson;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class HistoryManager implements AutoCloseable {

	private static final Gson gson = new Gson();

	@Nullable private final GitHistoryBackend backend;
	private final List<String> pendingEvents = new ArrayList<>();
	private long lastCheckpointMillis = System.currentTimeMillis();

	private final File workspaceFolder;

	public HistoryManager(Workspace workspace) {
		this(workspace.getWorkspaceFolder());
	}

	public HistoryManager(File workspaceFolder) {
		this.workspaceFolder = workspaceFolder;

		if (PreferencesManager.PREFERENCES.backups.enableLocalHistory.get()) {
			backend = GitHistoryBackend.tryCreate(this);
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

	private synchronized void checkpoint(boolean important, String checkpointName, Object... parameters) {
		if (backend == null) {
			return;
		}

		pendingEvents.add(L10N.t("local_history.checkpoint." + checkpointName, parameters));

		if (important || isSaveIntervalElapsed()) {
			flushPendingCheckpoint();
		}
	}

	private boolean isSaveIntervalElapsed() {
		long intervalMillis = PreferencesManager.PREFERENCES.backups.localHistorySaveInterval.get() * 60_000L;
		return System.currentTimeMillis() - lastCheckpointMillis >= intervalMillis;
	}

	private void flushPendingCheckpoint() {
		if (pendingEvents.isEmpty()) {
			return;
		}

		// TODO: we may want to do pagination of multiple events here
		if (saveCheckpoint(commitMessageFromEvents(pendingEvents))) {
			lastCheckpointMillis = System.currentTimeMillis();
		}

		// Clear events in every case, as even if saveCheckpoint returned false,
		// this means no changes were needed to be committed, meaning events did not change any files
		pendingEvents.clear();
	}

	private boolean saveCheckpoint(String eventName) {
		if (backend == null) {
			return false;
		}

		return backend.saveCheckpoint(eventName);
	}

	public List<HistoryCheckpoint> getCheckpoints() {
		if (backend == null) {
			return List.of();
		}

		return backend.getCheckpoints();
	}

	public File getWorkspaceFolder() {
		return workspaceFolder;
	}

	@Override public synchronized void close() {
		flushPendingCheckpoint();
		if (backend != null) {
			backend.close();
		}
	}

	// TODO: add a method to clear local history (delete repo), so user can clear history for space saving, should close manager, delete files and open new one

	public static void revertToCommit(String hash, File workspaceFolder) throws LocalHistoryException {
		try (GitHistoryBackend backend = GitHistoryBackend.tryCreate(new HistoryManager(workspaceFolder))) {
			if (backend != null) {
				backend.revertToCheckpoint(hash);
			}
		}
	}

	private static String commitMessageFromEvents(List<String> eventNames) {
		return gson.toJson(eventNames);
	}

	static String[] eventNamesFromCommitMessage(String commitMessage) {
		return gson.fromJson(commitMessage, String[].class);
	}

}
