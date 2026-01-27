// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Modifications by Pylo and opensource contributors

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