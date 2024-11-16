/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class MCREvent {

	private static final Logger LOG = LogManager.getLogger(MCREvent.class);

	public static <T extends MCREvent> void event(T event) {
		PluginLoader.INSTANCE.getJavaPlugins()
				.forEach(javaPlugin -> javaPlugin.getListeners().get(event.getClass()).forEach(listener -> {
					Future<?> result = javaPlugin.getEventQueue().submit(() -> listener.eventTriggered(event));
					if (event.isSynchronous()) { // Wait for synchronous events to finish
						try {
							result.get(event.getTimeout(), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							LOG.warn("Failed to wait for synchronous event", e);
						}
					}
				}));
	}

	/**
	 * Returns if the event is synchronous. MCreator waits for all synchronous events to finish before continuing with execution.
	 * If the event is meant to change value that is needed after the MCREvent.event() call, it should be synchronous.
	 *
	 * @return if the event is synchronous
	 */
	protected boolean isSynchronous() {
		return true;
	}

	/**
	 * Returns the timeout for the event. Ignored if the event is not synchronous.
	 * If the event is synchronous and the timeout is set to a value greater than 0, MCreator will wait for the event to finish for the given amount of time.
	 * If the event does not finish in the given time, MCreator will continue with execution and the event execution will be cancelled. A warning will be logged.
	 * <p>
	 * This is used to prevent a plugin from blocking MCreator execution indefinitely.
	 *
	 * @return the timeout for the event in milliseconds
	 */
	protected int getTimeout() {
		return 10000;
	}

}
