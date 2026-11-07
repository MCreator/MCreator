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

import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.ProjectFileUtils;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class OpenTabTool extends MCreatorMcpTool<OpenTabTool.Args> {

	public static class Args {
		@SchemaDescription("Name of the mod element to open in its editor. Either elementName or path is required")
		@Nullable public String elementName;
		@SchemaDescription("Workspace-relative file path as returned by project_files SEARCH action") @Nullable
		public String path;
		@SchemaDescription("Optional 1-based line number to jump to when opening a code file") @Nullable
		public Integer line;
	}

	public OpenTabTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "open_tab";
	}

	@Override public String getDescription() {
		return """
				Opens a mod element editor or a workspace file in a new tab in MCreator and makes it the current tab. \
				Pass elementName to open the mod element editor, or path to open a code file, texture, or NBT \
				structure file. Mod elements with locked code have no editor, open their source files with path instead. \
				Use get_current_tab to check what is currently open.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected Boolean getDestructiveHint() {
		return false;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) throws IOException {
		boolean hasElement = input.elementName != null && !input.elementName.isBlank();
		boolean hasPath = input.path != null && !input.path.isBlank();
		if (hasElement == hasPath) {
			return completedError("Exactly one of elementName or path is required");
		}
		return hasElement ? openModElement(mcreator, input.elementName) : openFile(mcreator, input.path, input.line);
	}

	private static CompletableFuture<ToolResult> openModElement(MCreator mcreator, String elementName) {
		ModElement modElement = mcreator.getWorkspace().getModElementByName(elementName);
		if (modElement == null) {
			return completedError("Element not found. Names usually CamelCase");
		}
		if (modElement.isCodeLocked()) {
			List<String> files = mcreator.getGenerator()
					.getModElementGeneratorTemplatesList(modElement.getGeneratableElement()).stream()
					.map(GeneratorTemplate::getFile).filter(File::isFile)
					.map(file -> getPathInWorkspace(mcreator, file)).toList();
			return completedError("Element code is locked, so it has no editor. Open its source files with path instead: "
					+ files);
		}
		if (modElement.getGeneratableElement() == null) {
			return completedError("Element has no saved definition that could be opened in the editor");
		}

		Map<String, Object> response = new LinkedHashMap<>();
		ThreadUtil.runOnSwingThreadAndWait(() -> {
			ModElementGUI<?> modElementGUI = modElement.getType().getModElementGUI(mcreator, modElement, true);
			if (modElementGUI == null) {
				response.put("error", "Element type " + modElement.getType().getRegistryName() + " has no editor");
				return;
			}
			modElementGUI.showView();
			response.put("result", "Opened mod element editor: " + modElement.getName());
		});
		if (response.containsKey("error")) {
			return completedError(response.get("error").toString());
		}
		return completedObject(response);
	}

	private static CompletableFuture<ToolResult> openFile(MCreator mcreator, String path, @Nullable Integer line)
			throws IOException {
		Path workspaceRoot = ProjectFileUtils.getWorkspaceRoot(mcreator);
		File file = ProjectFileUtils.resolveFile(workspaceRoot, workspaceRoot, path);
		if (file == null) {
			return completedError("Path is outside of the workspace: " + path);
		}
		if (!file.isFile()) {
			return completedError("File not found: " + path);
		}
		String relativePath = ProjectFileUtils.getPathInWorkspace(workspaceRoot, file);

		boolean codeFile = CodeEditorView.isFileSupported(file.getName());
		if (!codeFile && !ImageMakerView.isFileSupported(file.getName()) && !file.getName().toLowerCase()
				.endsWith(".nbt")) {
			return completedError("File type can not be opened in a MCreator tab: " + relativePath);
		}

		ThreadUtil.runOnSwingThreadAndWait(() -> {
			if (codeFile && line != null) {
				ProjectFileOpener.openFileAtLine(mcreator, file, line);
			} else {
				FileOpener.openFile(mcreator, file);
			}
		});

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("result", "Opened file: " + relativePath);
		return completedObject(response);
	}

	private static String getPathInWorkspace(MCreator mcreator, File file) {
		try {
			return ProjectFileUtils.getPathInWorkspace(ProjectFileUtils.getWorkspaceRoot(mcreator),
					file.getCanonicalFile());
		} catch (IOException e) {
			return file.getPath().replace(File.separator, "/");
		}
	}

}
