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

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.api.ModAPI;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProjectConnection;

import java.io.File;

public class GradleUtils {

	private static ProjectConnection getGradleProjectConnection(Workspace workspace) {
		updateMCreatorBuildFile(workspace);
		return workspace.getGenerator().getGradleProjectConnection();
	}

	public static BuildLauncher getGradleTaskLauncher(Workspace workspace, String... tasks) {
		BuildLauncher retval = getGradleProjectConnection(workspace).newBuild().forTasks(tasks)
				.setJvmArguments("-Xms" + PreferencesManager.PREFERENCES.gradle.xms + "m",
						"-Xmx" + PreferencesManager.PREFERENCES.gradle.xmx + "m");

		String java_home = getJavaHome();
		if (java_home != null) // make sure detected JAVA_HOME is not null
			retval = retval.setJavaHome(new File(java_home));

		return retval;
	}

	public static String getJavaHome() {
		// check if JAVA_HOME was overwritten in preferences and return this one in such case
		if (PreferencesManager.PREFERENCES.hidden.java_home != null && PreferencesManager.PREFERENCES.hidden.java_home
				.isFile()) {
			String path = PreferencesManager.PREFERENCES.hidden.java_home.toString().replace("\\", "/");
			if (new File(path).exists() && path.contains("/bin/java"))
				return path.split("/bin/java")[0];
			else if (new File(path).exists() && path.contains("/jre/sh/java"))
				return path.split("/jre/sh/java")[0];
		}

		// if we have bundled jdk, we set JAVA_HOME to bundled SDK
		if (new File("./jdk/bin/javac.exe").isFile())
			return FilenameUtils.normalize(new File("./jdk/").getAbsolutePath());

		// otherwise, we try to set JAVA_HOME to the same Java as MCreator is launched with
		String current_java_home = System.getProperty("java.home");
		if (current_java_home != null && current_java_home.contains("jdk")) // only set it if it is jdk, not jre
			return current_java_home;

		// if we can not get a better match, use system default JAVA_HOME variable
		// THIS ONE CAN BE null!!!, so handle this with care where used
		return System.getenv("JAVA_HOME");
	}

	public static void updateMCreatorBuildFile(Workspace workspace) {
		if (workspace != null) {
			StringBuilder mcreatorGradleConfBuilder = new StringBuilder();

			if (workspace.getWorkspaceSettings() != null
					&& workspace.getWorkspaceSettings().getMCreatorDependencies() != null) {
				for (String dep : workspace.getWorkspaceSettings().getMCreatorDependencies()) {
					ModAPI.Implementation implementation = ModAPIManager
							.getModAPIForNameAndGenerator(dep, workspace.getGenerator().getGeneratorName());
					if (implementation != null) {
						mcreatorGradleConfBuilder.append(implementation.gradle).append("\n\n");
					}
				}
			}

			FileIO.writeStringToFile(mcreatorGradleConfBuilder.toString(),
					new File(workspace.getWorkspaceFolder(), "mcreator.gradle"));
		}
	}

}
