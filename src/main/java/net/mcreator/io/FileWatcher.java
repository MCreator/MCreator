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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private final Map<WatchKey, Path> watchKeys = new HashMap<>();

	private final List<Listener> listeners = new ArrayList<>();

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
					WatchKey key = this.watchService.take(); // wait for key to be signaled

					key.pollEvents().stream().filter(e -> (e.kind() != OVERFLOW)).forEach(e -> {
						Path directory = watchKeys.get(key);
						if (directory != null) {
							//noinspection unchecked
							Path p = ((WatchEvent<Path>) e).context();
							File file = directory.resolve(p).toFile();
							listeners.forEach(listener -> listener.onFileChanged(key, e.kind(), file));
						}
					});

					// reset the key -- this step is critical if you want to receive further watch events.
					if (!key.reset())
						break;
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
	 * @return the WatchKey for the folder, or null if the folder is not a directory or if the watch service is not available
	 */
	@Nullable public WatchKey watchFolder(File path) {
		if (watchService != null) {
			try {
				// Check if we are already watching this folder
				for (Map.Entry<WatchKey, Path> entry : watchKeys.entrySet()) {
					if (entry.getValue().equals(path.toPath())) {
						return entry.getKey();
					}
				}

				Path dir = path.toPath();
				if (dir.toFile().isDirectory()) {
					WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
					watchKeys.put(key, dir);
					return key;
				}
			} catch (IOException e) {
				LOG.warn("Failed to register watcher for {}", path.getAbsolutePath(), e);
			}
		}
		return null;
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
		void onFileChanged(WatchKey watchKey, WatchEvent.Kind<?> kind, File file);
	}

}
