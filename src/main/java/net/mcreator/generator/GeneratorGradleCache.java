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
import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class GeneratorGradleCache {

	@Nullable transient ProjectJarManager projectJarManager;

	private List<ClasspathEntry> classpath;
	private Map<String, List<String>> importTree;

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

		public ClasspathEntry(String lib, @Nullable String src) {
			this.lib = lib.replace(UserFolderManager.getGradleHome().getAbsolutePath(), "<user.home.mcreator.gradle>");
			if (src != null)
				this.src = src
						.replace(UserFolderManager.getGradleHome().getAbsolutePath(), "<user.home.mcreator.gradle>");
		}

		public String getLib() {
			return lib.replace("<user.home.mcreator.gradle>", UserFolderManager.getGradleHome().getAbsolutePath());
		}

		@Nullable public String getSrc() {
			if (src != null)
				return src.replace("<user.home.mcreator.gradle>", UserFolderManager.getGradleHome().getAbsolutePath());
			else
				return null;
		}
	}

}
