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

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class WindowsProcessUtil {

	/**
	 * Retrieves the name of a running process on the system that contains the specified
	 * partial name in its details. The method searches through the output of the
	 * "tasklist" command.
	 *
	 * @param partialName the partial name of the process to search for (case-insensitive)
	 * @return the full name of a running process that matches the partial name, or null
	 * if no matching process is found
	 * @throws Exception if an error occurs while executing the "tasklist" command or
	 *                   reading its output
	 */
	@Nullable public static String getProcessNameIfExists(String partialName) throws Exception {
		Process p = Runtime.getRuntime().exec(new String[] { "tasklist" });
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			// Each line format: "processName.exe  ... other info ..."
			if (line.toLowerCase().contains(partialName.toLowerCase())) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length > 0) {
					return parts[0]; // Return the process name
				}
			}
		}
		return null;
	}

	/**
	 * Terminates a process with the given name forcefully using the "taskkill" command.
	 * This method is specific to the Windows operating system and may throw exceptions
	 * if the process cannot be terminated or if an error occurs during execution.
	 *
	 * @param serviceName the name of the process to terminate, including the file extension
	 * @throws Exception if an error occurs while executing the "taskkill" command, waiting for
	 *                   the command to complete, or during the subsequent delay
	 */
	public static void killProcess(String serviceName) throws Exception {
		Runtime.getRuntime().exec(new String[] { "taskkill", "/F", "/IM", serviceName }).waitFor(5, TimeUnit.SECONDS);
		Thread.sleep(500);
	}

}
