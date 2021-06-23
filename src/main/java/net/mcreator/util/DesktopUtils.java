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

package net.mcreator.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DesktopUtils {

	private static final Logger LOG = LogManager.getLogger(DesktopUtils.class);

	public static void browseSafe(String uri) {
		try {
			new Thread(() -> {
				try {
					browse(new URI(uri));
				} catch (Exception ignored) {
				}
			}).start();
		} catch (Exception ignored) {
		}
	}

	public static void openSafe(File file) {
		try {
			new Thread(() -> {
				try {
					open(file);
				} catch (Exception ignored) {
				}
			}).start();
		} catch (Exception ignored) {
		}
	}

	public static boolean browse(URI uri) {
		if (browseDESKTOP(uri)) {
			return true;
		}

		if (openSystemSpecific(uri.toString())) {
			return true;
		}

		LOG.warn(String.format("Failed to browse %s", uri));

		return false;
	}

	public static boolean open(File file) {
		if (openDESKTOP(file)) {
			return true;
		}

		if (openSystemSpecific(file.getPath())) {
			return true;
		}

		LOG.warn(String.format("Failed to open %s", file.getAbsolutePath()));

		return false;
	}

	public static boolean edit(File file) {
		if (editDESKTOP(file)) {
			return true;
		}

		if (openSystemSpecific(file.getPath())) {
			return true;
		}

		LOG.warn(String.format("Failed to edit %s", file.getAbsolutePath()));

		return false;
	}

	private static boolean openSystemSpecific(String what) {
		if (SystemUtils.IS_OS_LINUX) {
			if (isXDG()) {
				if (runCommand("xdg-open", "%s", what)) {
					return true;
				}
			}
			if (isKDE()) {
				if (runCommand("kde-open", "%s", what)) {
					return true;
				}
			}
			if (isGNOME()) {
				if (runCommand("gnome-open", "%s", what)) {
					return true;
				}
			}
			if (runCommand("kde-open", "%s", what)) {
				return true;
			}
			if (runCommand("gnome-open", "%s", what)) {
				return true;
			}
		}

		if (SystemUtils.IS_OS_MAC) {
			if (runCommand("open", "%s", what)) {
				return true;
			}
		}

		if (SystemUtils.IS_OS_WINDOWS) {
			if (runCommand("explorer", "%s", what)) {
				return true;
			}
		}

		return false;
	}

	private static boolean browseDESKTOP(URI uri) {
		try {
			if (!java.awt.Desktop.isDesktopSupported()) {
				LOG.debug("Platform is not supported.");
				return false;
			}

			if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
				LOG.debug("BROWSE is not supported.");
				return false;
			}

			LOG.info("Trying to use Desktop.getDesktop().browse() with " + uri.toString());
			java.awt.Desktop.getDesktop().browse(uri);

			return true;
		} catch (Throwable t) {
			LOG.error("Error using desktop browse.", t);
			return false;
		}
	}

	private static boolean openDESKTOP(File file) {
		try {
			if (!java.awt.Desktop.isDesktopSupported()) {
				LOG.debug("Platform is not supported.");
				return false;
			}

			if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
				LOG.debug("OPEN is not supported.");
				return false;
			}

			LOG.info("Trying to use Desktop.getDesktop().open() with " + file.toString());
			java.awt.Desktop.getDesktop().open(file);

			return true;
		} catch (Throwable t) {
			LOG.error("Error using desktop open.", t);
			return false;
		}
	}

	private static boolean editDESKTOP(File file) {
		try {
			if (!java.awt.Desktop.isDesktopSupported()) {
				LOG.debug("Platform is not supported.");
				return false;
			}

			if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.EDIT)) {
				LOG.debug("EDIT is not supported.");
				return false;
			}

			LOG.info("Trying to use Desktop.getDesktop().edit() with " + file);
			java.awt.Desktop.getDesktop().edit(file);

			return true;
		} catch (Throwable t) {
			LOG.error("Error using desktop edit.", t);
			return false;
		}
	}

	private static boolean runCommand(String command, String args, String file) {
		LOG.info("Trying to exec:\n   cmd = " + command + "\n   args = " + args + "\n   %s = " + file);

		String[] parts = prepareCommand(command, args, file);

		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null) {
				return false;
			}

			try {
				int retval = p.exitValue();
				if (retval == 0) {
					LOG.error("Process ended immediately.");
					return false;
				} else {
					LOG.error("Process crashed.");
					return false;
				}
			} catch (IllegalThreadStateException itse) {
				LOG.error("Process is running.");
				return true;
			}
		} catch (IOException e) {
			LOG.error("Error running command.", e);
			return false;
		}
	}

	private static String[] prepareCommand(String command, String args, String file) {
		List<String> parts = new ArrayList<>();
		parts.add(command);

		if (args != null) {
			for (String s : args.split(" ")) {
				s = String.format(s, file); // put in the filename thing

				parts.add(s.trim());
			}
		}

		return parts.toArray(new String[0]);
	}

	private static boolean isXDG() {
		String xdgSessionId = System.getenv("XDG_SESSION_ID");
		return xdgSessionId != null && !xdgSessionId.isEmpty();
	}

	private static boolean isGNOME() {
		String gdmSession = System.getenv("GDMSESSION");
		return gdmSession != null && gdmSession.toLowerCase().contains("gnome");
	}

	private static boolean isKDE() {
		String gdmSession = System.getenv("GDMSESSION");
		return gdmSession != null && gdmSession.toLowerCase().contains("kde");
	}

}
