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

package net.mcreator.gradle;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.gradle.GradleErrorDialogs;

public class GradleErrorDecoder {

	/**
	 * This method tries to decode task result based on given parameters, returns status code and shows error message if error is detected
	 *
	 * @param out         String containing data from out stream
	 * @param err         String containing data from err stream
	 * @param whereToShow Parent window on which to show the error dialog
	 * @return One of GradleTaskResult status codes, STATUS_UNKNOWN if GradleErrorDecoder can't decide the type of error
	 */
	public static int processErrorAndShowMessage(String out, String err, MCreator whereToShow) {
		// normalize spaces
		out = out.replaceAll("\u00a0", " ");
		err = err.replaceAll("\u00a0", " ");

		if (err.contains("\nExecution failed for task ':reobfJar'")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_REOBF_FAILED, whereToShow);
		}

		//check if there is no internet or the connection is blocked by a firewall
		if (err.contains(" Software caused connection abort: ") && out.contains("\nBUILD FAILED\n")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_INTERNET_INTERRUPTED, whereToShow);
		}

		//check if there is no internet or the connection is blocked by a firewall
		if ((err.contains("Could not GET ") && err.contains("Could not resolve ")) || (
				err.contains(" Network is unreachable: ") && err.contains("Could not resolve ")) || (
				err.contains("Could not HEAD ") && err.contains("Could not resolve "))) {

			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_NO_INTERNET, whereToShow);
		}

		//Check if cache files are corrupt
		if ((err.contains("java.io.FileNotFoundException: ") && err.contains("McpMappings.json (")) || (
				err.contains("Could not open proj remapped class cache for ") && err
						.contains("java.io.FileNotFoundException: "))) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_CACHEDATA_ERROR, whereToShow);
		}

		//Check if cache files are outdated
		if ((err.contains("No cached version of ") && err.contains(" available for offline mode.")) || (
				err.contains(" not found! Maybe you are running in offline mode?") && err
						.contains("java.io.FileNotFoundException"))) {
			if (PreferencesManager.PREFERENCES.gradle.offline)
				return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_CACHEDATA_OUTDATED, whereToShow);
			else
				return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_CACHEDATA_ERROR, whereToShow);
		}

		//Check if JVM ran out of RAM
		if (err.contains("java.lang.OutOfMemoryError: Java heap space") || out
				.contains("java.lang.OutOfMemoryError: Java heap space") || err
				.contains("Could not reserve enough space for") || out.contains("Could not reserve enough space for")
				|| err.contains("GC overhead limit exceeded") || (err.contains("Execution failed for task") && out
				.contains("Daemon stopping because JVM tenured space is exhausted"))) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.JAVA_JVM_HEAP_SPACE, whereToShow);
		}

		//Check if XMX parameter was set to a wrong value
		if (err.contains("Invalid maximum heap size:")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.JAVA_XMX_INVALID_VALUE, whereToShow);
		}

		//Check if XMS parameter was set to a wrong value
		if (err.contains("Invalid initial heap size:") || err
				.contains("Initial heap size set to a larger value than the maximum heap size")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.JAVA_XMS_INVALID_VALUE, whereToShow);
		}

		//Check if invalid Java version is used
		if (err.contains("Could not determine java version from") || out
				.contains("Could not determine java version from")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.JAVA_INVALID_VERSION, whereToShow);
		}

		//check if the error was caused by JVM crash and no other errors are present
		if ((out.contains("The crash happened outside the Java Virtual Machine in native code") || err
				.contains("The crash happened outside the Java Virtual Machine in native code")) && (
				out.contains("A fatal error has been detected by the Java Runtime Environment") || err
						.contains("A fatal error has been detected by the Java Runtime Environment"))) {

			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.JAVA_JVM_CRASH_ERROR, whereToShow);

		}

		// check if the gameplay crashed, we do not do anything in such cases
		if (out.contains("Task :runClient FAILED") || out.contains("Execution failed for task ':runClient'")) {
			return GradleErrorCodes.JAVA_RUN_CRASHED;
		}

		//if we don't know why, but the build fails, we report GRADLE_BUILD_FAILED
		if (out.contains("\nBUILD FAILED")) {
			return GradleErrorDialogs.showErrorDialog(GradleErrorCodes.GRADLE_BUILD_FAILED, whereToShow);
		}

		//if no error is detected, we return STATUS_OK
		return GradleErrorCodes.STATUS_OK;
	}

	public static boolean isErrorCausedByCorruptedCaches(String errortext) {
		// normalize spaces
		errortext = errortext.replaceAll("\u00a0", " ");
		if (!errortext.contains("Could not GET ") && !errortext.contains("Could not HEAD ") && !errortext
				.contains("Network is unreachable:")) { // eliminate networking problems first
			if (errortext.contains("java.util.zip.ZipException: error in opening zip file")) {
				return true;
			}
			if (errortext.contains("UncheckedIOException: Could not load properties for module")) {
				return true;
			}
			return errortext.contains("Could not resolve all files for configuration");
		}
		return false;
	}

}
