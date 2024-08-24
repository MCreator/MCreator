/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GradleFileTracker {

	private static final Logger LOG = LogManager.getLogger("Gradle File Tracker");

	private final ProjectConnection projectConnection;

	private final List<Path> changedFiles = new ArrayList<>();

	public GradleFileTracker(ProjectConnection projectConnection) {
		this.projectConnection = projectConnection;
	}

	public void trackFile(File file) {
		try {
			changedFiles.add(file.getCanonicalFile().toPath());
		} catch (IOException ignored) {
		}
	}

	public void notifyDaemonsAboutChangedPaths() {
		try {
			projectConnection.notifyDaemonsAboutChangedPaths(changedFiles);
			LOG.debug("Notified Gradle about {} changed paths", changedFiles.size());
		} catch (Exception e) {
			LOG.warn("Failed to notify Gradle about changed paths", e);
		} finally {
			changedFiles.clear();
		}
	}

}
