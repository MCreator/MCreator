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

package net.mcreator.ui.mcp.tools.utils;

import net.mcreator.generator.GeneratorUtils;
import net.mcreator.ui.MCreator;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class ProjectFileUtils {

	public static Path getWorkspaceRoot(MCreator mcreator) throws IOException {
		return mcreator.getWorkspace().getWorkspaceFolder().getCanonicalFile().toPath();
	}

	/**
	 * @return canonical common root of the source and resource roots, or null if it does not exist
	 */
	@Nullable public static Path getCommonRoot(MCreator mcreator) throws IOException {
		File commonRoot = GeneratorUtils.getCommonRoot(mcreator.getWorkspace(),
				mcreator.getGenerator().getGeneratorConfiguration());
		if (commonRoot == null || !commonRoot.isDirectory()) {
			return null;
		}
		return commonRoot.getCanonicalFile().toPath();
	}

	/**
	 * @return canonical file for the workspace-relative path, or null if it is outside the given root
	 */
	@Nullable public static File resolveFile(Path workspaceRoot, Path root, String path) throws IOException {
		File file = workspaceRoot.resolve(path.trim()).toFile().getCanonicalFile();
		return file.toPath().startsWith(root) ? file : null;
	}

	/**
	 * @return workspace-relative path of the file with forward slashes
	 */
	public static String getPathInWorkspace(Path workspaceRoot, File file) {
		return workspaceRoot.relativize(file.toPath()).toString().replace(File.separator, "/");
	}

}
