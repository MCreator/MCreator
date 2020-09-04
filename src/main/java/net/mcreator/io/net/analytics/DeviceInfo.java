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

package net.mcreator.io.net.analytics;

import net.mcreator.io.OS;

import java.awt.*;
import java.lang.management.ManagementFactory;

public class DeviceInfo {
	private int screenWidth = 0, screenHeight = 0;
	private final int systemBits;
	private final int ramAmountMB;

	private final String osName;
	private final String jvmVersion;

	public DeviceInfo() {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.screenWidth = (int) screenSize.getWidth();
			this.screenHeight = (int) screenSize.getHeight();
		} catch (Exception ignored) {
		}
		this.systemBits = OS.getSystemBits();
		this.ramAmountMB = (int) (
				((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
						.getTotalPhysicalMemorySize() / 1048576);
		this.osName = System.getProperty("os.name");
		this.jvmVersion = System.getProperty("java.version");
	}

	int getScreenWidth() {
		return screenWidth;
	}

	int getScreenHeight() {
		return screenHeight;
	}

	public int getSystemBits() {
		return systemBits;
	}

	public int getRamAmountMB() {
		return ramAmountMB;
	}

	public String getOsName() {
		return osName;
	}

	public String getJvmVersion() {
		return jvmVersion;
	}

}
