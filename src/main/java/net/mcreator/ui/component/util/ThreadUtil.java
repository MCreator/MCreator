/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.component.util;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ThreadUtil {

	private static final Logger LOG = LogManager.getLogger("Thread Util");

	private static boolean fxRunning = false;

	private static void setupFxThreadIfNotAlready() {
		if (fxRunning)
			return;

		try {
			Platform.runLater(() -> {
			});
		} catch (Exception e) {
			com.sun.javafx.application.PlatformImpl.startup(() -> LOG.info("Starting FX toolkit ..."));
			fxRunning = true;
		}
	}

	public static void runOnFxThread(Runnable runnable) {
		setupFxThreadIfNotAlready();

		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}

	public static void runOnSwingThread(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	public static void runOnSwingThreadAndWait(Runnable runnable)
			throws InvocationTargetException, InterruptedException {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeAndWait(runnable);
		}
	}

}
