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
import net.mcreator.plugin.modapis.ModAPIImplementation;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GradleUtils {

	private static final Logger LOG = LogManager.getLogger(GradleUtils.class);

	public static ProjectConnection getGradleProjectConnection(Workspace workspace) {
		updateMCreatorBuildFile(workspace); // update mcreator.gradle file if needed
		workspace.getGenerator().notifyDaemonsAboutChangedPaths(); // notify gradle daemons about changed paths
		return workspace.getGenerator().getGradleProjectConnection();
	}

	public static BuildActionExecuter<Void> getGradleSyncLauncher(ProjectConnection projectConnection) {
		BuildAction<Void> syncBuildAction = GradleSyncBuildAction.loadFromIsolatedClassLoader();
		BuildActionExecuter<Void> retval = projectConnection.action().projectsLoaded(syncBuildAction, unused -> {})
				.build().forTasks();
		return configureLauncher(retval); // make sure we have proper JVM, environment, ...
	}

	public static BuildLauncher getGradleTaskLauncher(ProjectConnection projectConnection, String... tasks) {
		BuildLauncher retval = projectConnection.newBuild().forTasks(tasks);
		return configureLauncher(retval); // make sure we have proper JVM, environment, ...
	}

	private static <T extends ConfigurableLauncher<T>> T configureLauncher(T launcher) {
		launcher.setJvmArguments("-Xms" + PreferencesManager.PREFERENCES.gradle.xms.get() + "m",
				"-Xmx" + PreferencesManager.PREFERENCES.gradle.xmx.get() + "m", "-Duser.language=en");

		String java_home = getJavaHome();
		if (java_home != null) // make sure detected JAVA_HOME is not null
			launcher = launcher.setJavaHome(new File(java_home));

		// use custom set of environment variables to prevent system overrides
		launcher.setEnvironmentVariables(getEnvironment(java_home));

		if (java_home != null)
			launcher.withArguments(Arrays.asList("-Porg.gradle.java.installations.auto-detect=false",
					"-Porg.gradle.java.installations.paths=" + java_home.replace('\\', '/')));

		// some mod API toolchains need to think they are running in IDE, so we make them think we are Eclipse
		launcher.addJvmArguments("-Declipse.application=net.mcreator");

		return launcher;
	}

	public static String getJavaHome() {
		// check if JAVA_HOME was overwritten in preferences and return this one in such case
		if (PreferencesManager.PREFERENCES.hidden.java_home.get() != null
				&& PreferencesManager.PREFERENCES.hidden.java_home.get().isFile()) {
			LOG.warn("Using java home override specified by users!");
			String path = PreferencesManager.PREFERENCES.hidden.java_home.get().toString().replace("\\", "/");
			if (new File(path).exists() && path.contains("/bin/java"))
				return path.split("/bin/java")[0];
			else
				LOG.error("Java home override from preferences is not valid!");
		}

		// if we have bundled JDK, we set JAVA_HOME to bundled JDK
		if (new File("./jdk/bin/javac.exe").isFile() || new File("./jdk/bin/javac").isFile())
			return FilenameUtils.normalize(new File("./jdk/").getAbsolutePath());

		// otherwise, we try to set JAVA_HOME to the same Java as MCreator is launched with
		String current_java_home = System.getProperty("java.home");
		if (current_java_home != null && current_java_home.contains("jdk")) // only set it if it is jdk, not jre
			return current_java_home;

		LOG.error("Failed to find any bundled JDKs, using system's default if it exists");

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
					ModAPIImplementation implementation = ModAPIManager.getModAPIForNameAndGenerator(dep,
							workspace.getGenerator().getGeneratorName());
					if (implementation != null) {
						mcreatorGradleConfBuilder.append(implementation.gradle()).append("\n\n");
					}
				}
			}

			FileIO.writeStringToFile(mcreatorGradleConfBuilder.toString(),
					new File(workspace.getWorkspaceFolder(), "mcreator.gradle"));
		}
	}

	public static Map<String, String> getEnvironment(String java_home) {
		Map<String, String> environment = new HashMap<>(System.getenv());

		// avoid global overrides
		cleanupEnvironment(environment);

		if (java_home != null)
			environment.put("JAVA_HOME", java_home);

		return environment;
	}

	public static void cleanupEnvironment(Map<String, String> environment) {
		// General Java environment variables
		environment.remove("JAVA_HOME");
		environment.remove("JRE_HOME");
		environment.remove("JDK_HOME");
		environment.remove("CLASSPATH");
		environment.remove("_JAVA_OPTIONS");
		environment.remove("JAVA_OPTS");
		environment.remove("JAVA_TOOL_OPTIONS");
		environment.remove("JAVACMD");
		environment.remove("JDK_JAVA_OPTIONS");

		// Gradle-specific environment variables
		environment.remove("GRADLE_HOME");
		environment.remove("GRADLE_OPTS");
		environment.remove("GRADLE_USER_HOME");
	}

}
