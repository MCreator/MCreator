/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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
import net.mcreator.io.FileIO;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.buildpath.DirLibraryInfo;
import org.fife.rsta.ac.java.buildpath.DirSourceLocation;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link LibraryInfo} over the directory with compiled workspace classes (Gradle build output),
 * making code completion aware of the classes of the workspace itself. Unlike {@link DirLibraryInfo},
 * it tolerates the directory not existing yet (workspace that was not built).
 * <p>
 * The class list of a library is only indexed when the library is registered with a jar manager,
 * so {@link ProjectJarManager#refreshWorkspaceClassInfo()} re-registers a new instance of
 * this class after each Gradle task run, which is the only point at which compiled workspace
 * classes can change.
 */
public class WorkspaceLibraryInfo extends LibraryInfo {

	private final File classesDir;

	private final File sourceRoot;

	// The source location is kept in this holder, with getSourceLocation()/setSourceLocation
	// overridden to use it: LibraryInfo#clone() is shallow, so the holder is shared with the
	// clones made when this library is registered, and refreshSourceLocation() therefore takes
	// effect on the registered clone as well
	private final AtomicReference<SourceLocation> sourceLocation = new AtomicReference<>();

	public WorkspaceLibraryInfo(Generator generator) {
		this.classesDir = new File(generator.getWorkspace().getWorkspaceFolder(), "build/classes/java/main");
		this.sourceRoot = generator.getSourceRoot();
		refreshSourceLocation();
	}

	/**
	 * Attaches a fresh source location instance to this library. Workspace source files presented
	 * by code completion (javadoc, parameter names) are parsed on demand, but cached keyed on the
	 * source location instance identity, so a fresh instance makes code completion re-read them.
	 */
	public void refreshSourceLocation() {
		// Identity equality is what the caching and the entry replacement path rely on, so it is
		// self-enforced here instead of depending on DirSourceLocation never overriding Object#equals
		setSourceLocation(new DirSourceLocation(sourceRoot.getAbsolutePath()) {
			@SuppressWarnings("RedundantMethodOverride") @Override public boolean equals(Object obj) {
				return this == obj;
			}
		});
	}

	@Override public SourceLocation getSourceLocation() {
		return sourceLocation.get();
	}

	@Override public void setSourceLocation(SourceLocation sourceLocation) {
		this.sourceLocation.set(sourceLocation);
	}

	@Override public void bulkClassFileCreationStart() {
	}

	@Override public void bulkClassFileCreationEnd() {
	}

	/**
	 * {@code LibraryInfo#equals(Object)} delegates to this method, and
	 * {@code JarManager#addClassFileSource(LibraryInfo)} uses equality to detect that a library is
	 * already registered. Matching on the classes directory makes any two instances over the same
	 * directory equal, so re-registering a fresh instance replaces (and thus re-indexes) the
	 * already registered entry instead of adding a duplicate one.
	 */
	@Override public int compareTo(@Nonnull LibraryInfo info) {
		if (info == this)
			return 0;
		if (info instanceof WorkspaceLibraryInfo workspaceLibraryInfo)
			return classesDir.compareTo(workspaceLibraryInfo.classesDir);
		return -1;
	}

	@Override public ClassFile createClassFile(String entryName) throws IOException {
		File file = new File(classesDir, entryName);
		if (!file.isFile())
			return null;
		return new ClassFile(file);
	}

	@Override public ClassFile createClassFileBulk(String entryName) throws IOException {
		return createClassFile(entryName);
	}

	@Override public PackageMapNode createPackageMap() {
		PackageMapNode root = new PackageMapNode();
		Path classesPath = classesDir.toPath();
		for (File classFile : FileIO.listFilesRecursively(classesDir)) {
			if (classFile.getName().endsWith(".class"))
				root.add(classesPath.relativize(classFile.toPath()).toString().replace(File.separatorChar, '/'));
		}
		return root;
	}

	@Override public long getLastModified() {
		// 0 disables timestamp-based clearing of cached class file data by the jar reader: it is
		// not needed, as each refresh registers this library anew with no stale cached data
		return 0;
	}

	@Override public String getLocationAsString() {
		return classesDir.getAbsolutePath();
	}

	@Override public int hashCodeImpl() {
		return classesDir.hashCode();
	}

}
