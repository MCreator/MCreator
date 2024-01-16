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

package net.mcreator.generator;

import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.io.UserFolderManager;
import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class GeneratorGradleCache {

	@Nullable transient ProjectJarManager projectJarManager;

	private final List<ClasspathEntry> classpath;
	private final Map<String, List<String>> importTree;

	GeneratorGradleCache(Generator generator) {
		projectJarManager = new ProjectJarManager(generator);
		this.classpath = projectJarManager.getClasspath();
		this.importTree = ImportTreeBuilder.generateImportTree(this.projectJarManager);
	}

	void reinitAfterGSON(Generator generator) throws GradleCacheImportFailedException {
		projectJarManager = new ProjectJarManager(generator, classpath);
	}

	public Map<String, List<String>> getImportTree() {
		return importTree;
	}

	public static class ClasspathEntry {

		private final String lib;
		@Nullable private String src;

		public ClasspathEntry(Workspace workspace, String lib, @Nullable String src) {
			this.lib = encodePlaceholders(workspace, lib);
			if (src != null)
				this.src = encodePlaceholders(workspace, src);
		}

		public String getLib(Workspace workspace) {
			return decodePlaceholders(workspace, lib);
		}

		@Nullable public String getSrc(Workspace workspace) {
			if (src != null)
				return decodePlaceholders(workspace, src);
			else
				return null;
		}

		private String decodePlaceholders(Workspace workspace, String string) {
			return string.replace("<gradle_home>", UserFolderManager.getGradleHome().getAbsolutePath())
					.replace("<workspace_home>", workspace.getWorkspaceFolder().getAbsolutePath());
		}

		private String encodePlaceholders(Workspace workspace, String string) {
			return string.replace(UserFolderManager.getGradleHome().getAbsolutePath(), "<gradle_home>")
					.replace(workspace.getWorkspaceFolder().getAbsolutePath(), "<workspace_home>");
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			return lib.equals(((ClasspathEntry) o).lib);
		}

		@Override public int hashCode() {
			return lib.hashCode();
		}

	}

}
