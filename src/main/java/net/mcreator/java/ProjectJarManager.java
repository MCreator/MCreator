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

package net.mcreator.java;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorGradleCache;
import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.DirSourceLocation;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.ExternalDependency;
import org.gradle.tooling.model.eclipse.EclipseProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectJarManager extends JarManager {

	private static final Logger LOG = LogManager.getLogger("Jar Manager");

	private final List<GeneratorGradleCache.ClasspathEntry> classpath;

	public ProjectJarManager(Generator generator) {
		if (generator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA)
			try {
				addClassFileSource(getJVMLibraryInfo());
			} catch (IOException e) {
				LOG.warn("Failed to load JVM to JAR manager", e);
			}

		this.classpath = loadClassPathJARs(generator);
	}

	public ProjectJarManager(Generator generator, List<GeneratorGradleCache.ClasspathEntry> classPathEntries)
			throws GradleCacheImportFailedException {
		if (generator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA)
			try {
				addClassFileSource(getJVMLibraryInfo());
			} catch (IOException e) {
				LOG.warn("Failed to load JVM to JAR manager", e);
			}

		for (GeneratorGradleCache.ClasspathEntry classpathEntry : classPathEntries) {
			loadExternalDependency(generator.getWorkspace(), classpathEntry);
		}

		this.classpath = classPathEntries;
	}

	public List<GeneratorGradleCache.ClasspathEntry> getClasspath() {
		return classpath;
	}

	private List<GeneratorGradleCache.ClasspathEntry> loadClassPathJARs(Generator generator) {
		List<GeneratorGradleCache.ClasspathEntry> classPathEntries = new ArrayList<>();

		ProjectConnection projectConnection = generator.getGradleProjectConnection();
		if (projectConnection != null) {
			try {
				EclipseProject project = projectConnection.getModel(EclipseProject.class);

				for (ExternalDependency externalDependency : project.getClasspath()) {
					File libFile = externalDependency.getFile();
					if (libFile != null && libFile.isFile()) {
						if (libFile.getName().startsWith("scala-"))
							continue; // skip scala libraries as we do not need them in MCreator

						if (libFile.getName().contains("-natives-"))
							continue; // skip native libraries as we do not need them in MCreator

						File srcFile = externalDependency.getSource();
						GeneratorGradleCache.ClasspathEntry classpathEntry = new GeneratorGradleCache.ClasspathEntry(
								generator.getWorkspace(), libFile.getAbsolutePath(),
								srcFile != null ? srcFile.getAbsolutePath() : null);

						int idx = classPathEntries.indexOf(classpathEntry);
						if (idx >= 0) { // If we already have this library in the list,
							GeneratorGradleCache.ClasspathEntry altClasspathEntry = classPathEntries.get(idx);
							//  replace it in case we don't have src yet but the alt entry has it
							if (altClasspathEntry.getSrc(generator.getWorkspace()) == null && srcFile != null) {
								classPathEntries.set(idx, classpathEntry);
							}
						} else {
							classPathEntries.add(classpathEntry);
						}
					}
				}
			} catch (BuildException ignored) {
			}

			// After we have collected all classpath entries, load them in the JAR manager
			for (GeneratorGradleCache.ClasspathEntry classpathEntry : classPathEntries) {
				try {
					loadExternalDependency(generator.getWorkspace(), classpathEntry);
				} catch (GradleCacheImportFailedException ignored) {
				}
			}
		}

		return classPathEntries;
	}

	private void loadExternalDependency(Workspace workspace, GeneratorGradleCache.ClasspathEntry classpathEntry)
			throws GradleCacheImportFailedException {
		String libString = classpathEntry.getLib(workspace);
		File libFile = new File(libString);
		if (!libFile.exists()) {
			LOG.warn("Failed to load cached library " + libString);
			throw new GradleCacheImportFailedException(new IOException("Failed to load cached library " + libString));
		}

		JarLibraryInfo libraryInfo = new JarLibraryInfo(libString);
		String srcString = classpathEntry.getSrc(workspace);
		if (srcString != null) {
			File srcFile = new File(srcString);
			if (srcFile.isFile()) {
				libraryInfo.setSourceLocation(new ZipSourceLocation(srcString));
			} else if (srcFile.isDirectory()) {
				libraryInfo.setSourceLocation(new DirSourceLocation(srcString));
			}
		}
		try {
			addClassFileSource(libraryInfo);
		} catch (IOException e) {
			LOG.warn("Failed to load classpath file " + libString, e);
			throw new GradleCacheImportFailedException(new IOException("Failed to load classpath file " + libString));
		}
	}

	private static LibraryInfo getJVMLibraryInfo() {
		File jreHome = new File(System.getProperty("java.home"));

		final File classesArchive = findExistingPath(jreHome, "lib/rt.jar", "../Classes/classes.jar",
				"jmods/java.base.jmod");
		if (classesArchive == null) {
			LOG.warn("Failed to load default JRE JAR info");
			return null;
		}

		final LibraryInfo info;

		if (classesArchive.getName().endsWith(".jmod")) {
			info = new JModLibraryInfo(classesArchive);
		} else {
			info = new JarLibraryInfo(classesArchive);
		}

		final File sourcesArchive = findExistingPath(jreHome, "lib/src.zip", "lib/src.jar", "src.zip", "../src.zip",
				"src.jar", "../src.jar");
		if (sourcesArchive != null) {
			info.setSourceLocation(new ZipSourceLocation(sourcesArchive));
		}

		return info;
	}

	private static File findExistingPath(final File baseDir, String... paths) {
		for (final String path : paths) {
			File file = new File(baseDir, path);
			if (file.exists())
				return file;
		}
		return null;
	}

}
