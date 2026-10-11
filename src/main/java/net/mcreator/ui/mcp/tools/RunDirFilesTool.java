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
import net.mcreator.workspace.WorkspaceFolderManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RunDirFilesTool extends MCreatorMcpTool<RunDirFilesTool.Args> {

	public static class Args {
		public Action actionType;
		@Nullable public String query;
		@Nullable public String path;
		@Nullable public Integer lastNCharsToRead;

		public enum Action {
			SEARCH, READ
		}
	}

	public RunDirFilesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "run_dir_files";
	}

	@Override public String getDescription() {
		return """
				Searches or reads files inside the Minecraft run directory of the workspace (logs, crash reports, configs, ...).\
				 SEARCH recursively lists workspace-relative file paths, optionally filtered by the case-insensitive query substring.\
				 READ returns the contents of a text file given its workspace-relative path as returned by SEARCH.\
				 Use lastNCharsToRead to only read the end of large files such as logs.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) throws IOException {
		if (input.actionType == null) {
			return completedError("actionType is required");
		}

		Path workspaceRoot = ProjectFileUtils.getWorkspaceRoot(mcreator);
		Set<Path> roots = getRunDirs(mcreator.getFolderManager());
		if (roots.isEmpty()) {
			return completedError("Run directory does not exist yet, run the client or server first");
		}

		return switch (input.actionType) {
			case SEARCH -> search(workspaceRoot, roots, input.query);
			case READ -> read(workspaceRoot, roots, input.path, input.lastNCharsToRead);
		};
	}

	private static Set<Path> getRunDirs(WorkspaceFolderManager folderManager) throws IOException {
		Set<Path> roots = new LinkedHashSet<>();
		for (File runDir : List.of(folderManager.getClientRunDir(), folderManager.getServerRunDir())) {
			if (runDir.isDirectory())
				roots.add(runDir.getCanonicalFile().toPath());
		}
		return roots;
	}

	private static CompletableFuture<ToolResult> search(Path workspaceRoot, Set<Path> roots, @Nullable String query) {
		String filter = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
		Set<String> paths = new TreeSet<>();
		for (Path root : roots) {
			for (File file : FileIO.listFilesRecursively(root.toFile())) {
				String relativePath = ProjectFileUtils.getPathInWorkspace(workspaceRoot, file);
				if (relativePath.toLowerCase(Locale.ROOT).contains(filter)) {
					paths.add(relativePath);
				}
			}
		}
		return completed(ToolResult.collection(paths));
	}

	private static CompletableFuture<ToolResult> read(Path workspaceRoot, Set<Path> roots, @Nullable String path,
			@Nullable Integer lastNCharsToRead) throws IOException {
		if (path == null || path.isBlank()) {
			return completedError("path is required for READ");
		}

		File file = null;
		for (Path root : roots) {
			file = ProjectFileUtils.resolveFile(workspaceRoot, root, path);
			if (file != null)
				break;
		}
		if (file == null) {
			return completedError("Path is outside of the run directory: " + path);
		}
		if (!file.isFile()) {
			return completedError("File not found: " + path);
		}

		String content = FileIO.readFileToString(file);
		if (lastNCharsToRead != null) {
			content = content.substring(Math.max(0, content.length() - lastNCharsToRead));
		}
		return completed(ToolResult.text(content));
	}

}
