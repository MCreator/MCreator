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

public class OS {

	public static final int WINDOWS = 0;
	public static final int MAC = 1;
	public static final int LINUX = 2;

	public static final int BIT32 = 32;
	public static final int BIT64 = 64;

	public static int getOS() {
		String os = System.getProperty("os.name");
		if (os.contains("Mac") || os.contains("OS X"))
			return MAC;
		if (os.contains("Linux"))
			return LINUX;
		return WINDOWS;
	}

	public static int getSystemBits() {
		boolean is64bit;
		if (getOS() == WINDOWS) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").contains("64"));
		}
		if (is64bit)
			return BIT64;
		return BIT32;
	}

	public static int getBundledJVMBits() {
		if (System.getProperty("sun.arch.data.model").contains("64"))
			return BIT64;
		return BIT32;
	}

	public static String getRuntimeProvider() {
		if (getOS() == WINDOWS) {
			return "cmd";
		} else {
			return "bash";
		}
	}

}
