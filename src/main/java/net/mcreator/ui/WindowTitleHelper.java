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

package net.mcreator.ui;

import net.mcreator.Launcher;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.views.editor.image.ImageMakerView;

import java.io.IOException;

public class WindowTitleHelper {

	public static String getWindowTitle(MCreator mcreator) {
		String appendix = "";
		if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof ModElementGUI<?> modElementGUI) {
			appendix = " - " + modElementGUI.getModElement().getName() + " (" + modElementGUI.getModElement().getType()
					.getReadableName() + ")";
		} else if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof CodeEditorView codeEditorView) {
			try {
				appendix = " - " + mcreator.getFolderManager().getPathInWorkspace(codeEditorView.fileWorkingOn);
			} catch (Exception e) {
				appendix = " - " + codeEditorView.fileWorkingOn.toPath();
			}
		} else if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof ImageMakerView imageMakerView && imageMakerView.getImageFile() != null) {
			try {
				appendix = " - " + mcreator.getFolderManager().getPathInWorkspace(imageMakerView.getImageFile());
			} catch (Exception e) {
				appendix = " - " + imageMakerView.getImageFile().toPath();
			}
		}

		String workspaceBaseName = mcreator.getWorkspaceSettings().getModName();
		try {
			return workspaceBaseName + " [" + mcreator.getWorkspaceFolder().getCanonicalPath() + "]" + appendix
					+ " - MCreator " + Launcher.version.getMajorString();
		} catch (IOException e) {
			return workspaceBaseName + " [" + mcreator.getWorkspaceFolder().getAbsolutePath() + "]" + appendix
					+ " - MCreator " + Launcher.version.getMajorString();
		}
	}

}
