/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.mcreator.util.DefaultExceptionHandler;
import net.mcreator.util.LoggingOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;

public class LoggingSystem {

	public static void init() {
		System.setProperty("log_directory", UserFolderManager.getFileFromUserFolder("").getAbsolutePath());

		if (OS.getOS() == OS.WINDOWS && ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
				.noneMatch(arg -> arg.contains("idea_rt.jar"))) {
			System.setProperty("log_disable_ansi", "true");
		} else {
			System.setProperty("log_disable_ansi", "false");
		}

		System.setErr(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDERR"), Level.ERROR), true));
		System.setOut(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDOUT"), Level.INFO), true));
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
	}

}
