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

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CurrentTabTool extends MCreatorMcpTool<Void> {

	public CurrentTabTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Void.class);
	}

	@Override public String getName() {
		return "get_current_tab";
	}

	@Override public String getDescription() {
		return """
				Returns information about the currently open tab in MCreator. \
				Reports mod element, code file, or texture being edited when applicable.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Void input) {
		Map<String, Object> response = new HashMap<>();

		ThreadUtil.runOnSwingThreadAndWait(() -> {
			MCreatorTabs.Tab currentTab = mcreator.getTabs().getCurrentTab();
			if (currentTab == null) {
				response.put("tabType", "none");
				return;
			}

			response.put("tabTitle", currentTab.getText());

			if (currentTab.equals(mcreator.workspaceTab)) {
				response.put("tabType", "workspace");
			} else if (currentTab.getContent() instanceof ModElementGUI<?> modElementGUI) {
				ModElement modElement = modElementGUI.getModElement();
				response.put("tabType", "mod_element");
				response.put("modElement",
						Map.of("name", modElement.getName(), "type", modElement.getType().getRegistryName()));
			} else if (currentTab.getContent() instanceof CodeEditorView codeEditorView) {
				response.put("tabType", "code_editor");
				Map<String, Object> fileInfo = new HashMap<>();
				fileInfo.put("fileName", codeEditorView.fileWorkingOn.getName());
				try {
					fileInfo.put("pathInWorkspace",
							mcreator.getFolderManager().getPathInWorkspace(codeEditorView.fileWorkingOn));
				} catch (Exception e) {
					fileInfo.put("pathInWorkspace", codeEditorView.fileWorkingOn.getPath());
				}
				fileInfo.put("unsavedChanges", codeEditorView.changed);
				response.put("file", fileInfo);
			} else if (currentTab.getContent() instanceof ImageMakerView imageMakerView) {
				response.put("tabType", "texture_editor");
				response.put("texture", buildTextureInfo(mcreator, imageMakerView));
			} else {
				response.put("tabType", "other");
				response.put("contentType", currentTab.getContent().getClass().getSimpleName());
			}
		});

		return CompletableFuture.completedFuture(ToolResult.object(response));
	}

	private static Map<String, Object> buildTextureInfo(MCreator mcreator, ImageMakerView imageMakerView) {
		Map<String, Object> textureInfo = new HashMap<>();
		File imageFile = imageMakerView.getImageFile();
		if (imageFile != null) {
			textureInfo.put("fileName", imageFile.getName());
			try {
				textureInfo.put("pathInWorkspace", mcreator.getFolderManager().getPathInWorkspace(imageFile));
			} catch (Exception e) {
				textureInfo.put("pathInWorkspace", imageFile.getPath());
			}
			String textureType = resolveTextureType(mcreator, imageFile);
			if (textureType != null) {
				textureInfo.put("textureType", textureType);
			}
		} else {
			textureInfo.put("name", imageMakerView.getViewName());
		}
		return textureInfo;
	}

	private static String resolveTextureType(MCreator mcreator, File imageFile) {
		String imagePath = imageFile.getAbsolutePath();
		for (TextureType type : TextureType.values()) {
			File folder = mcreator.getFolderManager().getTexturesFolder(type);
			if (folder != null && imagePath.startsWith(folder.getAbsolutePath())) {
				return type.getID();
			}
		}
		return null;
	}

}
