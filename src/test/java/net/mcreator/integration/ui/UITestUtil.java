/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.integration.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class UITestUtil {

	public static void waitUntilWindowIsOpen(Window master, Runnable openTask) throws Throwable {
		int frames_start = Window.getWindows().length;

		AtomicReference<Throwable> throwableAtomic = new AtomicReference<>(null);

		SwingUtilities.invokeLater(() -> {
			try {
				openTask.run();
			} catch (Throwable t) {
				throwableAtomic.set(t);
			}
		});

		long start = System.currentTimeMillis();
		while (Window.getWindows().length == frames_start) {
			//noinspection BusyWait
			Thread.sleep(5);

			if (System.currentTimeMillis() - start > 4000)
				throw new TimeoutException();

			if (throwableAtomic.get() != null)
				throw throwableAtomic.get();
		}

		if (throwableAtomic.get() != null)
			throw throwableAtomic.get();

		Arrays.stream(Window.getWindows()).filter(w -> w != master).forEach(Window::dispose);
	}
}
