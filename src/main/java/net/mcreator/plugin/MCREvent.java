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

import java.util.concurrent.ExecutionException;

public abstract class MCREvent {

	public static <T extends MCREvent> void event(T event) {
		boolean canCancel = event instanceof MCRCancelableEvent;
		PluginLoader.INSTANCE.getJavaPlugins().forEach(javaPlugin -> javaPlugin.getListeners().get(event.getClass())
				.forEach(listener -> {
					var future = javaPlugin.getEventQueue().submit(() -> listener.eventTriggered(event));
					if (canCancel) {
						try {
							//wait for all jobs complement
							future.get();
						} catch (InterruptedException | ExecutionException e) {
							throw new RuntimeException(e);
						}
					}
				}));
	}

	public static abstract class MCRCancelableEvent extends MCREvent{
		private boolean canceled = false;

		public boolean isCanceled() {
			return canceled;
		}

		public void setCanceled(boolean canceled) {
			this.canceled = canceled;
		}
	}

}
