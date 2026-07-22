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

package net.mcreator.io;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class WindowsPackage {

	private static final Logger LOG = LogManager.getLogger(WindowsPackage.class);

	// PACKAGE_FULL_NAME_MAX_LENGTH (127) + null terminator
	private static final int PACKAGE_FULL_NAME_BUFFER_LENGTH = 128;

	private interface Kernel32 extends StdCallLibrary {
		Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

		int GetCurrentPackageFullName(IntByReference packageFullNameLength, char[] packageFullName);
	}

	private static boolean initialized = false;
	@Nullable private static String PACKAGE_FULL_NAME = null;

	public static void initIfWindows() {
		if (!initialized && OS.getOS() == OS.WINDOWS) {
			PACKAGE_FULL_NAME = detectPackageFullName();
			if (PACKAGE_FULL_NAME != null) {
				LOG.info("Running as MSIX package: {}", PACKAGE_FULL_NAME);
			}
		}
		initialized = true;
	}

	public static boolean isRunningAsMSIX() {
		initIfWindows(); // in case someone calls this without calling initIfWindows() first

		return PACKAGE_FULL_NAME != null;
	}

	@Nullable private static String detectPackageFullName() {
		try {
			char[] name = new char[PACKAGE_FULL_NAME_BUFFER_LENGTH];
			IntByReference length = new IntByReference(name.length);
			int result = Kernel32.INSTANCE.GetCurrentPackageFullName(length, name);
			if (result == 0) // ERROR_SUCCESS — process has package identity (MSIX)
				return Native.toString(name);
		} catch (Throwable ignored) {
		}

		return null; // includes APPMODEL_ERROR_NO_PACKAGE (15700) for classic installs
	}

}
