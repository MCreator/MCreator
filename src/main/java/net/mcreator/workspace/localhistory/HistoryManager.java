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
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class HistoryManager implements AutoCloseable {

	private static final Gson gson = new Gson();

	@Nullable private final GitHistoryBackend backend;
	private final List<String> pendingEventNames = new ArrayList<>();
	private long lastCheckpointMillis = System.currentTimeMillis();

	public HistoryManager(Workspace workspace) {
		if (PreferencesManager.PREFERENCES.backups.enableLocalHistory.get()) {
			backend = GitHistoryBackend.tryCreate(workspace.getWorkspaceFolder());
		} else {
			backend = null;
		}
	}

	public synchronized void checkpoint(String eventName, boolean important) {
		if (backend == null) {
			return;
		}

		pendingEventNames.add(eventName);

		if (important || isSaveIntervalElapsed()) {
			flushPendingCheckpoint();
		}
	}

	private boolean isSaveIntervalElapsed() {
		long intervalMillis = PreferencesManager.PREFERENCES.backups.localHistorySaveInterval.get() * 60_000L;
		return System.currentTimeMillis() - lastCheckpointMillis >= intervalMillis;
	}

	private void flushPendingCheckpoint() {
		if (pendingEventNames.isEmpty()) {
			return;
		}

		String chainedEventNames = gson.toJson(pendingEventNames);
		if (saveCheckpoint(chainedEventNames)) {
			lastCheckpointMillis = System.currentTimeMillis();
		}

		// Clear events in every case, as even if saveCheckpoint returned false,
		// this means no changes were needed to be committed, meaning events did not change any files
		pendingEventNames.clear();
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

	@Override public synchronized void close() {
		flushPendingCheckpoint();
		if (backend != null) {
			backend.close();
		}
	}

	public static void revertToCommit(String hash, File workspaceFolder) throws LocalHistoryException {
		try (GitHistoryBackend backend = GitHistoryBackend.tryCreate(workspaceFolder)) {
			if (backend != null) {
				backend.revertToCheckpoint(hash);
			}
		}
	}

	static String commitMessageFromEvents(List<String> eventNames) {
		return gson.toJson(eventNames);
	}

	static String[] eventNamesFromCommitMessage(String commitMessage) {
		return gson.fromJson(commitMessage, String[].class);
	}

}
