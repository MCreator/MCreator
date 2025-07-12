/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * This class watches a directory for changes and notifies listeners when a file is created, deleted, or modified.
 * <p>
 * It uses the Java NIO WatchService API to monitor file system events.
 * <p>
 * The class is designed to be used in a generator context, where it can watch for changes in the generator's workspace.
 */
public class FileWatcher implements Closeable {

	private static final Logger LOG = LogManager.getLogger(FileWatcher.class);

	@Nullable private final WatchService watchService;

	// List of watch keys and their corresponding directories
	private final Map<WatchKey, Path> watchKeys = new HashMap<>();

	private final List<Listener> listeners = new ArrayList<>();

	private final Set<FileChange> nonReportedChanges = new HashSet<>();

	private boolean closed = false;

	public FileWatcher() {
		WatchService watchServiceTmp;
		try {
			watchServiceTmp = FileSystems.getDefault().newWatchService();
		} catch (Exception e) {
			watchServiceTmp = null;
			LOG.warn("Failed to create file system watch service", e);
		}
		this.watchService = watchServiceTmp;
		if (this.watchService == null) {
			return;
		}

		new Thread(() -> {
			while (!closed) {
				try {
					WatchKey key = this.watchService.poll(3, TimeUnit.SECONDS); // wait for the key to be signaled
					if (key != null) {
						key.pollEvents().stream().filter(e -> (e.kind() != OVERFLOW)).forEach(e -> {
							Path directory = watchKeys.get(key);
							if (directory != null && e.context() instanceof Path p) {
								File file = directory.resolve(p).toFile();
								nonReportedChanges.add(new FileChange(key, e.kind(), file));

								// Check if the directory still exists, if not, remove the watch key
								if (!directory.toFile().isDirectory()) {
									watchKeys.remove(key);
									try {
										key.cancel(); // cancel the key if the directory is no longer valid
									} catch (Exception ignored) {
									}
								}
							}
						});

						// reset the key -- this step is critical if you want to receive further watch events.
						if (!key.reset())
							break;
					} else if (!nonReportedChanges.isEmpty()) { // no change for a given timeout, process changes if any
						LOG.debug("Detected {} file changes, notifying listeners", nonReportedChanges.size());
						listeners.forEach(listener -> listener.filesChanged(new ArrayList<>(nonReportedChanges)));
						nonReportedChanges.clear();
					}
				} catch (Exception e) {
					if (!closed) {
						LOG.warn("Failed to watch file", e);
					}
				}
			}
		}, "File Watcher Thread").start();
	}

	/**
	 * Adds a listener to be notified when a file is changed.
	 *
	 * @param listener the listener to add
	 */
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Watches a folder for changes. If the folder is not a directory, it will not be watched.
	 * <p>
	 * The watch is not recursive, so only the specified folder will be watched, not its subdirectories!
	 *
	 * @param path the path to the folder to watch
	 */
	public void watchFolder(File path) {
		Path dir = path.toPath();
		if (watchService != null) {
			if (watchKeys.containsValue(dir))
				return;

			try {
				if (dir.toFile().isDirectory()) {
					WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
					watchKeys.put(key, dir);
				}
			} catch (IOException e) {
				LOG.warn("Failed to register watcher for {}", path.getAbsolutePath(), e);
			}
		}
	}

	@Override public void close() {
		closed = true;
		if (watchService != null) {
			try {
				watchService.close();
			} catch (IOException e) {
				LOG.error("Failed to close watch service", e);
			}
		}
	}

	public interface Listener {
		void filesChanged(List<FileChange> changedFiles);
	}

	public record FileChange(WatchKey watchKey, WatchEvent.Kind<?> kind, File file) {

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			FileChange that = (FileChange) o;
			return Objects.equals(file, that.file);
		}

		@Override public int hashCode() {
			return Objects.hash(file);
		}

	}

}
