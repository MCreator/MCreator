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

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
				Searches or reads project files inside the source and resource roots of the workspace.\
				 SEARCH recursively lists workspace-relative file paths matching the case-insensitive query substring.\
				 READ returns the contents of a text file given its workspace-relative path as returned by SEARCH.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) throws IOException {
		if (input.actionType == null) {
			return completedError("actionType is required");
		}

		Path workspaceRoot = mcreator.getWorkspace().getWorkspaceFolder().getCanonicalFile().toPath();
		List<Path> roots = new ArrayList<>();
		for (File root : new File[] { mcreator.getGenerator().getSourceRoot(),
				mcreator.getGenerator().getResourceRoot() }) {
			if (root != null && root.isDirectory()) {
				roots.add(root.getCanonicalFile().toPath());
			}
		}
		if (roots.isEmpty()) {
			return completedError("Workspace has no source or resource root");
		}

		return switch (input.actionType) {
			case SEARCH -> search(workspaceRoot, roots, input.query);
			case READ -> read(workspaceRoot, roots, input.path);
		};
	}

	private static CompletableFuture<ToolResult> search(Path workspaceRoot, List<Path> roots, @Nullable String query) {
		if (query == null || query.isBlank()) {
			return completedError("query is required for SEARCH");
		}

		String filter = query.trim().toLowerCase(Locale.ROOT);
		Set<String> paths = new TreeSet<>();
		for (Path root : roots) {
			for (File file : FileIO.listFilesRecursively(root.toFile())) {
				String relativePath = workspaceRoot.relativize(file.toPath()).toString().replace(File.separator, "/");
				if (relativePath.toLowerCase(Locale.ROOT).contains(filter)) {
					paths.add(relativePath);
				}
			}
		}
		return completed(ToolResult.collection(paths));
	}

	private static CompletableFuture<ToolResult> read(Path workspaceRoot, List<Path> roots, @Nullable String path)
			throws IOException {
		if (path == null || path.isBlank()) {
			return completedError("path is required for READ");
		}

		File file = workspaceRoot.resolve(path.trim()).toFile().getCanonicalFile();
		if (roots.stream().noneMatch(root -> file.toPath().startsWith(root))) {
			return completedError("Path is outside of the source and resource roots: " + path);
		}
		if (!file.isFile()) {
			return completedError("File not found: " + path);
		}

		return completed(ToolResult.text(FileIO.readFileToString(file)));
	}

}
