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

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.FileIO;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.MinecraftFolderUtils;
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
				 For add-on workspaces, Bedrock Edition game and logs folders are searched instead.\
				 SEARCH recursively lists file paths, optionally filtered by the case-insensitive query substring.\
				 READ returns the contents of a text file given its path as returned by SEARCH.\
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
		Set<Path> roots = getRoots(mcreator);
		if (roots.isEmpty()) {
			return completedError("Run directory does not exist yet, run the client or server first");
		}

		return switch (input.actionType) {
			case SEARCH -> search(workspaceRoot, roots, input.query);
			case READ -> read(workspaceRoot, roots, input.path, input.lastNCharsToRead);
		};
	}

	private static Set<Path> getRoots(MCreator mcreator) throws IOException {
		WorkspaceFolderManager folderManager = mcreator.getFolderManager();
		List<File> candidates = new ArrayList<>(
				List.of(folderManager.getClientRunDir(), folderManager.getServerRunDir()));
		if (mcreator.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			candidates.add(MinecraftFolderUtils.getBedrockEditionFolder());
			candidates.add(MinecraftFolderUtils.getBedrockEditionLogsFolder());
		}

		Set<Path> roots = new LinkedHashSet<>();
		for (File candidate : candidates) {
			if (candidate != null && candidate.isDirectory())
				roots.add(candidate.getCanonicalFile().toPath());
		}
		return roots;
	}

	private static CompletableFuture<ToolResult> search(Path workspaceRoot, Set<Path> roots, @Nullable String query) {
		String filter = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
		Set<String> paths = new TreeSet<>();
		for (Path root : roots) {
			for (File file : FileIO.listFilesRecursively(root.toFile())) {
				String path = getPath(workspaceRoot, file);
				if (path.toLowerCase(Locale.ROOT).contains(filter)) {
					paths.add(path);
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

	/**
	 * @return workspace-relative path for files in the workspace, absolute path for Bedrock Edition folders
	 */
	private static String getPath(Path workspaceRoot, File file) {
		if (file.toPath().startsWith(workspaceRoot))
			return ProjectFileUtils.getPathInWorkspace(workspaceRoot, file);
		return file.getAbsolutePath();
	}

}
