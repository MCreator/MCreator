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

package net.mcreator.ui.chromium.osr;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TimedTaskQueue {

	public enum ThreadToUse {
		SWING_THREAD, POOLED_THREAD
	}

	private final ThreadToUse threadToUse;
	private final ScheduledExecutorService executor;
	private final List<ScheduledFuture<?>> tasks = new ArrayList<>();
	private final Object lock = new Object();
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	public TimedTaskQueue(ThreadToUse threadToUse) {
		this.threadToUse = threadToUse;
		this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "Alarm");
			t.setDaemon(true);
			return t;
		});
	}

	public void addRequest(Runnable runnable, int delayMs) {
		if (disposed.get())
			return;

		Runnable task = () -> {
			if (disposed.get())
				return;

			if (threadToUse == ThreadToUse.SWING_THREAD) {
				SwingUtilities.invokeLater(runnable);
			} else {
				runnable.run();
			}
		};

		ScheduledFuture<?> future = executor.schedule(task, delayMs, TimeUnit.MILLISECONDS);

		synchronized (lock) {
			tasks.add(future);
		}
	}

	public void cancelAllRequests() {
		synchronized (lock) {
			for (ScheduledFuture<?> f : tasks) {
				f.cancel(false);
			}
			tasks.clear();
		}
	}

	public boolean isEmpty() {
		synchronized (lock) {
			return tasks.isEmpty();
		}
	}

	public void dispose() {
		if (!disposed.compareAndSet(false, true))
			return;

		cancelAllRequests();
		executor.shutdownNow();
	}

}