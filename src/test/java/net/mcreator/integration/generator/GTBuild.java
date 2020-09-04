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

package net.mcreator.integration.generator;

import net.mcreator.gradle.GradleUtils;
import net.mcreator.io.OutputStreamEventHandler;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;

public class GTBuild {

	public static void runTest(Logger LOG, String generatorName, Workspace workspace)
			throws GradleConnectionException, IllegalStateException {
		BuildLauncher buildLauncher = GradleUtils.getGradleTaskLauncher(workspace, "build");

		StringBuilder sb = new StringBuilder();

		buildLauncher
				.setStandardError(new OutputStreamEventHandler(line -> sb.append(line).append(System.lineSeparator())));
		buildLauncher.setStandardOutput(
				new OutputStreamEventHandler(line -> sb.append(line).append(System.lineSeparator())));

		try {
			buildLauncher.run();
		} catch (GradleConnectionException | IllegalStateException e) {
			LOG.error("[" + generatorName + "] " + sb);
			throw e;
		}

		LOG.info("[" + generatorName + "] Gradle build OK");
	}

}
