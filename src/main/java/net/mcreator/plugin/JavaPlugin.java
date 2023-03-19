/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Extend this object to define custom plugin.
 * <p>
 * A fully qualified name to this class name needs to be specified as "javaplugin" field in plugin.json file
 */
public abstract class JavaPlugin {

	protected final Plugin plugin;

	private final EventMap listeners = new EventMap();

	private final ExecutorService eventQueue = Executors.newSingleThreadExecutor(new ThreadFactory() {
		@Override public Thread newThread(@Nonnull Runnable runnable) {
			final Logger LOG = LogManager.getLogger("JavaPlugin-LOG-" + plugin.getID());

			Thread thread = new Thread(runnable);
			thread.setName("JavaPlugin-EventQueue-" + plugin.getID());
			thread.setUncaughtExceptionHandler((t, e) -> LOG.error(e));
			return thread;
		}
	});

	// Called by reflection
	public JavaPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return Definition of the plugin this Java plugin belongs to
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Call this method to add new event listener to the plugin
	 *
	 * @param eventType Class of the event type
	 * @param listener  Listener for the given event
	 */
	public <T extends MCREvent> void addListener(Class<T> eventType, MCREventListener<T> listener) {
		listeners.addEvent(eventType, listener);
	}

	EventMap getListeners() {
		return listeners;
	}

	ExecutorService getEventQueue() {
		return eventQueue;
	}

}
