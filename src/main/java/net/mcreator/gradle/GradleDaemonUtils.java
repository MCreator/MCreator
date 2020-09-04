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

import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.workspace.Workspace;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GradleDaemonUtils {

	private static Process getGradleCompatibleBashProcess(Workspace workspace) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(OS.getRuntimeProvider());
		processBuilder.directory(workspace.getFolderManager().getWorkspaceFolder());
		Map<String, String> env = processBuilder.environment();
		env.remove("_JAVA_OPTIONS"); // to avoid global overrides
		env.put("GRADLE_USER_HOME", UserFolderManager.getGradleHome().getAbsolutePath());
		String java_home = GradleUtils.getJavaHome();
		if (java_home != null) // make sure detected JAVA_HOME is not null
			env.put("JAVA_HOME", java_home);
		return processBuilder.start();
	}

	public static void stopAllDaemons(Workspace workspace) throws IOException, InterruptedException, TimeoutException {
		Process process = getGradleCompatibleBashProcess(workspace);
		PrintWriter stdin = new PrintWriter(process.getOutputStream());

		Map<String, String> gradleParamteres = new HashMap<>();
		String java_home = GradleUtils.getJavaHome();
		if (java_home != null)
			gradleParamteres.put("org.gradle.java.home", "\"" + java_home.replace('\\', '/') + "\"");

		StringBuilder paramsBuilder = new StringBuilder();
		for (Map.Entry<String, String> entry : gradleParamteres.entrySet()) {
			paramsBuilder.append("-D").append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
		}

		if (OS.getOS() == OS.WINDOWS) {
			stdin.println("gradlew " + paramsBuilder.toString() + " --stop");
		} else {
			stdin.println("chmod 777 gradlew");
			stdin.println("./gradlew " + paramsBuilder.toString() + " --stop");
		}

		stdin.close();

		if (!process.waitFor(30, TimeUnit.SECONDS))
			throw new TimeoutException("Timeout waiting for the gradle daemons to stop");
	}

}
