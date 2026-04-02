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

package net.mcreator.util;

import in.pratanumandal.unique4j.*;
import in.pratanumandal.unique4j.unixsocketchannel.UnixSocketChannelIpcFactory;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SingleAppHandler implements Closeable {

	private static final String APP_ID = "net.mcreator.MCreator";

	private static final Logger LOG = LogManager.getLogger(SingleAppHandler.class);

	private final Unique4jLock lock;

	private final List<String> currentArgs;

	/**
	 * @param args                        Command line args of this instance
	 * @param secondaryAppLaunchedHandler Called in the first instance when a secondary instance sends its args
	 */
	public SingleAppHandler(List<String> args, Consumer<List<String>> secondaryAppLaunchedHandler) {
		this.currentArgs = args;

		IpcFactory ipcFactory;
		if (isUnixSocketSupported())
			ipcFactory = new UnixSocketChannelIpcFactory();
		else
			ipcFactory = new DynamicPortSocketIpcFactory(InetAddress.getLoopbackAddress(), 3000);

		this.lock = Unique4j.withConfig(
				Unique4jConfig.createDefault(APP_ID).lockFolder(UserFolderManager.getFileFromUserFolder("/"))
						.ipcFactory(ipcFactory)).newLock(
				// first instance, here we receive the args
				firstInstanceClient -> {
					try (InputStream is = firstInstanceClient.getInputStream();
							ObjectInputStream ois = new ObjectInputStream(is)) {
						Object obj = ois.readObject();
						if (obj instanceof String[] secondaryArgs) {
							secondaryAppLaunchedHandler.accept(Arrays.asList(secondaryArgs));
						}
					} catch (IOException | ClassNotFoundException e) {
						LOG.warn("Failed to read args from secondary instance", e);
					}
				},
				// secondary instance, here we send the args
				otherInstanceClient -> {
					try (OutputStream os = otherInstanceClient.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(os)) {
						// Send the args from this instance to the first instance
						oos.writeObject(currentArgs.toArray(String[]::new));
						oos.flush();
					} catch (IOException e) {
						LOG.warn("Failed to send args to first instance", e);
					}
				});
	}

	/**
	 * Tries to acquire the single-instance lock. If another instance is running,
	 * sends args to the first instance.
	 *
	 * @return true if this is the first instance (or if locking completely fails), false if another instance is already running
	 */
	public boolean tryAcquireLock() {
		try {
			return lock.tryLock();
		} catch (IOException e) {
			LOG.error("Failed to handle app lock", e);
			// if we fail to lock, the best solution is to launch a new instance
			return true;
		}
	}

	@Override public void close() {
		try {
			lock.unlock();
		} catch (IOException e) {
			LOG.warn("Failed to release app lock", e);
		}
	}

	private static boolean isUnixSocketSupported() {
		if (OS.getOS() == OS.MAC || OS.getOS() == OS.LINUX)
			return true;

		try {
			SocketChannel.open(StandardProtocolFamily.UNIX).close();
		} catch (UnsupportedOperationException e) {
			return false;
		} catch (IOException ignored) {
		}

		return true;
	}

	public static void bringToFront(JFrame frame) {
		if (frame.getExtendedState() == JFrame.ICONIFIED) {
			frame.setExtendedState(JFrame.NORMAL);
		} else {
			// Use a hacky approach to bring the window to front reliably
			frame.setAlwaysOnTop(true);
			frame.toFront();
			frame.requestFocus();
			frame.setAlwaysOnTop(false);
		}
	}

}