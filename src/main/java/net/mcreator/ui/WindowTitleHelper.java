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

public class WindowTitleHelper {

	public static String getWindowTitle(MCreator mcreator) {
		StringBuilder title = new StringBuilder(mcreator.getWorkspaceSettings().getModName());

		if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof ModElementGUI<?> modElementGUI) {
			title.append(" - ").append(modElementGUI.getModElement().getName()).append(" (")
					.append(modElementGUI.getModElement().getType().getReadableName()).append(")");
		} else if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof CodeEditorView codeEditorView) {
			try {
				title.append(" - ")
						.append(mcreator.getFolderManager().getPathInWorkspace(codeEditorView.fileWorkingOn));
			} catch (Exception e) {
				title.append(" - ").append(codeEditorView.fileWorkingOn.toPath());
			}
		} else if (mcreator.mcreatorTabs.getCurrentTab() != null && mcreator.mcreatorTabs.getCurrentTab()
				.getContent() instanceof ImageMakerView imageMakerView && imageMakerView.getImageFile() != null) {
			try {
				title.append(" - ")
						.append(mcreator.getFolderManager().getPathInWorkspace(imageMakerView.getImageFile()));
			} catch (Exception e) {
				title.append(" - ").append(imageMakerView.getImageFile().toPath());
			}
		}

		return title.append(" - MCreator ").append(Launcher.version.getMajorString()).toString();
	}

}
