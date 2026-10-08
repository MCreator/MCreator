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

package net.mcreator.ui.mcp.tools;

import net.mcreator.io.FileIO;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.ProjectFileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ProjectFilesTool extends MCreatorMcpTool<ProjectFilesTool.Args> {

	public static class Args {
		public Action actionType;
		@Nullable public String query;
		@Nullable public String path;

		public enum Action {
			SEARCH, READ
		}
	}

	public ProjectFilesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "project_files";
	}

	@Override public String getDescription() {
		return """
				Searches or reads project files inside the source root of the workspace.\
				 SEARCH recursively lists workspace-relative file paths matching the case-insensitive query substring.\
				 READ returns the contents of a text file given its workspace-relative path as returned by SEARCH.\
				 Use project_file_edit tool to create, edit, or delete project files.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) throws IOException {
		if (input.actionType == null) {
			return completedError("actionType is required");
		}

		Path workspaceRoot = ProjectFileUtils.getWorkspaceRoot(mcreator);
		Path root = ProjectFileUtils.getCommonRoot(mcreator);
		if (root == null) {
			return completedError("Workspace has no source root");
		}

		return switch (input.actionType) {
			case SEARCH -> search(workspaceRoot, root, input.query);
			case READ -> read(workspaceRoot, root, input.path);
		};
	}

	private static CompletableFuture<ToolResult> search(Path workspaceRoot, Path root, @Nullable String query) {
		if (query == null || query.isBlank()) {
			return completedError("query is required for SEARCH");
		}

		String filter = query.trim().toLowerCase(Locale.ROOT);
		Set<String> paths = new TreeSet<>();
		for (File file : FileIO.listFilesRecursively(root.toFile())) {
			String relativePath = ProjectFileUtils.getPathInWorkspace(workspaceRoot, file);
			if (relativePath.toLowerCase(Locale.ROOT).contains(filter)) {
				paths.add(relativePath);
			}
		}
		return completed(ToolResult.collection(paths));
	}

	private static CompletableFuture<ToolResult> read(Path workspaceRoot, Path root, @Nullable String path)
			throws IOException {
		if (path == null || path.isBlank()) {
			return completedError("path is required for READ");
		}

		File file = ProjectFileUtils.resolveFile(workspaceRoot, root, path);
		if (file == null) {
			return completedError("Path is outside of the source root: " + path);
		}
		if (!file.isFile()) {
			return completedError("File not found: " + path);
		}

		return completed(ToolResult.text(FileIO.readFileToString(file)));
	}

}
