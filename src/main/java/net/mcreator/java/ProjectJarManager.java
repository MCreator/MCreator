/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.java;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorGradleCache;
import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.gradle.GradleToolchainUtil;
import net.mcreator.gradle.GradleUtils;
import net.mcreator.io.FileIO;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.DirSourceLocation;
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.ExternalDependency;
import org.gradle.tooling.model.eclipse.EclipseProject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProjectJarManager extends JarManager {

	private static final Logger LOG = LogManager.getLogger(ProjectJarManager.class);

	private final Generator generator;

	private final List<GeneratorGradleCache.ClasspathEntry> classpath;
	@Nullable private final File javaHome;

	@Nullable private WorkspaceLibraryInfo workspaceLibraryInfo;

	private JavaReleaseInfo javaReleaseInfo = JavaReleaseInfo.DEFAULT;

	public ProjectJarManager(Generator generator) {
		this.generator = generator;

		List<GeneratorGradleCache.ClasspathEntry> classPathEntries = new ArrayList<>();
		File assumedJavaHome = null;

		ProjectConnection projectConnection = GradleUtils.getGradleProjectConnection(generator.getWorkspace());
		if (projectConnection != null) {
			try {
				ModelBuilder<EclipseProject> modelBuilder = GradleUtils.getGradleModelBuilder(
						generator.getGeneratorConfiguration(), projectConnection, EclipseProject.class);

				EclipseProject project = modelBuilder.get();

				processProjectClassPath(project, classPathEntries);

				// Only look up JDK toolchain JAVA_HOME for Java-based projects
				if (generator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
						== GeneratorFlavor.BaseLanguage.JAVA) {
					assumedJavaHome = GradleToolchainUtil.getToolchainJavaHome(generator.getGeneratorConfiguration(),
							projectConnection, project);
				}
			} catch (BuildException ignored) {
			}
		}

		this.classpath = classPathEntries;
		this.javaHome = assumedJavaHome;

		// First, try to load JVM library info
		try {
			tryLoadJVMLibraryInfo();
		} catch (GradleCacheImportFailedException e) {
			LOG.error("Failed to load JVM library info", e);
		}

		// After we have collected all classpath entries, load them in the JAR manager
		for (GeneratorGradleCache.ClasspathEntry classpathEntry : this.classpath) {
			try {
				loadExternalDependency(classpathEntry);
			} catch (GradleCacheImportFailedException ignored) {
			}
		}

		// Finally, load compiled classes of the workspace itself
		refreshWorkspaceClassInfo();
	}

	public ProjectJarManager(Generator generator, List<GeneratorGradleCache.ClasspathEntry> classPathEntries,
			@Nullable File javaHome) throws GradleCacheImportFailedException {
		this.generator = generator;

		this.classpath = classPathEntries;
		this.javaHome = javaHome;

		// First, try to load JVM library info
		tryLoadJVMLibraryInfo();

		// Then, load all the classpath entries
		for (GeneratorGradleCache.ClasspathEntry classpathEntry : classPathEntries) {
			loadExternalDependency(classpathEntry);
		}

		// Finally, load compiled classes of the workspace itself
		refreshWorkspaceClassInfo();
	}

	public List<GeneratorGradleCache.ClasspathEntry> getClasspath() {
		return classpath;
	}

	@Nullable public File getJavaHome() {
		return javaHome;
	}

	@Nullable public JavaReleaseInfo getJavaReleaseInfo() {
		return javaHome == null ? null : javaReleaseInfo;
	}

	public void refreshWorkspaceClassInfo() {
		if (generator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				!= GeneratorFlavor.BaseLanguage.JAVA)
			return;

		try {
			WorkspaceLibraryInfo libraryInfo = new WorkspaceLibraryInfo(generator);
			addClassFileSource(libraryInfo);
			this.workspaceLibraryInfo = libraryInfo;
		} catch (IOException e) {
			LOG.warn("Failed to load workspace classes", e);
		}
	}

	/**
	 * Makes code completion re-read workspace source files it presents (javadoc, parameter names).
	 * Unlike {@link #refreshWorkspaceClassInfo()}, the compiled class list is not re-indexed, so
	 * this is the right call for the case when workspace source files change without a Gradle task
	 * run, e.g., on code editor save. To be called on the EDT.
	 */
	public void refreshWorkspaceSourceInfo() {
		if (workspaceLibraryInfo != null)
			workspaceLibraryInfo.refreshSourceLocation();
	}

	/**
	 * @return Same as {@link #getClassFileSources()}, but without the {@link WorkspaceLibraryInfo}
	 * entry: compiled classes of the workspace itself are registered with this jar manager for
	 * code completion purposes, but they are not an external library of the workspace.
	 */
	public List<LibraryInfo> getExternalClassFileSources() {
		return getClassFileSources().stream().filter(libraryInfo -> !(libraryInfo instanceof WorkspaceLibraryInfo))
				.toList();
	}

	/**
	 * Reads the source code of the given class from the source location registered for it with
	 * this jar manager. Both ZIP/JAR and directory based source locations are supported.
	 *
	 * @param classFqdn Fully qualified name of the class to read the source code of.
	 * @return Source code of the given class, or null if there is no source location for it or
	 * the source location does not contain it.
	 */
	@Nullable public String getSourceCodeForClass(String classFqdn) {
		SourceLocation sourceLocation = getSourceLocForClass(classFqdn);
		if (sourceLocation == null)
			return null;

		String sourcePath = classFqdn.replace('.', '/') + ".java";
		File sourceLocationFile = new File(sourceLocation.getLocationAsString());

		if (sourceLocation instanceof ZipSourceLocation) {
			try (ZipFile zipFile = ZipIO.openZipFile(sourceLocationFile)) {
				ZipEntry entry = zipFile.getEntry(sourcePath);
				if (entry == null) { // Sources may be located under a prefix directory inside the archive
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry candidate = entries.nextElement();
						if (candidate.getName().endsWith(sourcePath)) {
							entry = candidate;
							break;
						}
					}
				}
				if (entry != null)
					return ZipIO.entryToString(zipFile, entry);
			} catch (IOException e) {
				LOG.error("Failed to read source code for {} from {}", classFqdn, sourceLocationFile, e);
			}
		} else if (sourceLocation instanceof DirSourceLocation) {
			File sourceFile = new File(sourceLocationFile, sourcePath);
			if (sourceFile.isFile())
				return FileIO.readFileToString(sourceFile);
		}

		return null;
	}

	private void processProjectClassPath(EclipseProject project,
			List<GeneratorGradleCache.ClasspathEntry> classPathEntries) {
		LOG.debug("Processing classpath for project {}", project.getName());

		for (ExternalDependency externalDependency : project.getClasspath()) {
			File libFile = externalDependency.getFile();
			if (libFile != null && libFile.isFile()) {
				if (libFile.getName().contains("-natives-") || libFile.getName().startsWith("scala-"))
					continue; // skip scala and native libraries as we do not need them in MCreator

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

		for (EclipseProject childProject : project.getChildren()) {
			processProjectClassPath(childProject, classPathEntries);
		}
	}

	private void loadExternalDependency(GeneratorGradleCache.ClasspathEntry classpathEntry)
			throws GradleCacheImportFailedException {
		Workspace workspace = generator.getWorkspace();
		String libString = classpathEntry.getLib(workspace);
		File libFile = new File(libString);
		if (!libFile.exists()) {
			LOG.warn("Failed to load cached library {}", libString);
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
			LOG.warn("Failed to load classpath file {}", libString, e);
			throw new GradleCacheImportFailedException(new IOException("Failed to load classpath file " + libString));
		}
	}

	private void tryLoadJVMLibraryInfo() throws GradleCacheImportFailedException {
		if (javaHome == null) {
			if (generator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA) {
				throw new GradleCacheImportFailedException(new IOException("JVM library info is null"));
			}
			return; // we only require JVM info for Java-based projects
		}

		javaReleaseInfo = JavaReleaseInfo.fromJavaHome(javaHome);

		LOG.debug("Loading JVM {} info from {}", javaReleaseInfo, javaHome);

		final File classesArchive = findExistingPath(javaHome, "lib/rt.jar", "../Classes/classes.jar", "lib/jrt-fs.jar",
				"jmods/java.base.jmod", "jre/lib/rt.jar");
		if (classesArchive == null) {
			throw new GradleCacheImportFailedException(new FileNotFoundException("Failed to find SDK base library"));
		}

		final LibraryInfo info = getLibraryInfo(classesArchive);

		LOG.debug("Loaded JVM info of type {}", info.getClass().getSimpleName());

		final File sourcesArchive = findExistingPath(javaHome, "lib/src.zip", "lib/src.jar", "src.zip", "../src.zip",
				"src.jar", "../src.jar");
		if (sourcesArchive != null) {
			info.setSourceLocation(new ZipSourceLocation(sourcesArchive));
		} else {
			LOG.warn("Failed to load sources for {}", classesArchive);
		}

		try {
			addClassFileSource(info);
		} catch (IOException e) {
			throw new GradleCacheImportFailedException(e);
		}
	}

	@Nonnull private LibraryInfo getLibraryInfo(File classesArchive) {
		if (classesArchive.getName().equals("jrt-fs.jar")) {
			return new ModulesFileLibraryInfo(javaHome);
		} else if (classesArchive.getName().endsWith(".jmod")) {
			return new JModLibraryInfo(classesArchive);
		} else {
			return new JarLibraryInfo(classesArchive);
		}
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
