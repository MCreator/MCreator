/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.io.OutputStreamEventHandler;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.eclipse.EclipseJavaSourceSettings;
import org.gradle.tooling.model.eclipse.EclipseProject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class GradleToolchainUtil {

	private static final Logger LOG = LogManager.getLogger(GradleToolchainUtil.class);

	/**
	 * Attempts to determine the Java home directory used by the toolchain in a Gradle project.
	 * If the toolchain Java home cannot be determined, it falls back to checking the provided
	 * Eclipse project, if available.
	 *
	 * @param generatorConfiguration the configuration object used to configure the Gradle launcher.
	 * @param projectConnection      the connection to the Gradle project.
	 * @param fallbackProject        an optional Eclipse project to use as a fallback for determining
	 *                               the Java home in case the toolchain Java home cannot be resolved.
	 * @return the file path representing the Java home directory used by the toolchain if found,
	 * or null if no suitable Java home could be determined.
	 */
	@Nullable public static File getToolchainJavaHome(GeneratorConfiguration generatorConfiguration,
			ProjectConnection projectConnection, @Nullable EclipseProject fallbackProject) {
		File initScript = null;
		try {
			initScript = createInitScript();

			StringBuilder sb = new StringBuilder();

			// Use the help task as a dummy task to invoke Gradle
			BuildLauncher launcher = GradleUtils.getGradleTaskLauncher(generatorConfiguration, projectConnection,
							"resolveToolchainPath").addArguments("--init-script", initScript.getAbsolutePath())
					.addArguments("--quiet")
					.setStandardOutput(new OutputStreamEventHandler(line -> sb.append(line).append("\n")));
			launcher.run();

			return parseOutput(sb.toString());
		} catch (Exception e) {
			LOG.warn("Failed to determine toolchain JDK home", e);
			TestUtil.failIfTestingEnvironment();
		} finally {
			if (initScript != null)
				initScript.delete();
		}

		// not accurate in most cases
		if (fallbackProject != null) {
			File altJavaHome = findJavaHome(fallbackProject);
			if (altJavaHome == null)
				LOG.error("Could not determine toolchain JDK home nor Eclipse project JDK home");
			return altJavaHome;
		}

		return null;
	}

	private static File createInitScript() throws IOException {
		File initScriptFile = File.createTempFile("toolchainlookup-", ".gradle");
		initScriptFile.deleteOnExit();
		FileIO.writeStringToFile("""
				allprojects {
				    tasks.register("resolveToolchainPath") {
				        def prov = tasks.named("compileJava").flatMap { it.javaCompiler }
				        doLast {
				            println "TOOLCHAIN_JDK_HOME=${prov.get().metadata.installationPath}"
				        }
				    }
				}
				""", initScriptFile);
		return initScriptFile;
	}

	private static File parseOutput(String output) {
		for (String line : output.split("\n")) {
			line = line.trim();
			if (line.startsWith("TOOLCHAIN_JDK_HOME=")) {
				return new File(line.substring("TOOLCHAIN_JDK_HOME=".length()).trim());
			}
		}
		throw new RuntimeException("Could not determine compileJava toolchain JDK home. Output: " + output);
	}

	@Nullable private static File findJavaHome(EclipseProject project) {
		EclipseJavaSourceSettings javaSourceSettings = project.getJavaSourceSettings();
		if (javaSourceSettings != null) {
			File javaHome = javaSourceSettings.getJdk().getJavaHome();
			if (javaHome != null) {
				return javaHome;
			}
		}

		// If we did not find one, try child projects
		for (EclipseProject childProject : project.getChildren()) {
			File javaHome = findJavaHome(childProject);
			if (javaHome != null) {
				return javaHome;
			}
		}

		return null;
	}

}
