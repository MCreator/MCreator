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

package net.mcreator.ui.browser;

import net.mcreator.generator.GeneratorFlavor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class AddFileDropdown extends JPopupMenu {

	AddFileDropdown(WorkspaceFileBrowser workspaceFileBrowser) {
		setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		try {
			ProjectBrowserFilterTreeNode selected = (ProjectBrowserFilterTreeNode) workspaceFileBrowser.tree
					.getLastSelectedPathComponent();
			if (selected != null) {
				if (selected.getUserObject() instanceof File) {
					File file = (File) selected.getUserObject();
					if (file.isFile())
						file = file.getParentFile();

					if (workspaceFileBrowser.mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
							== GeneratorFlavor.BaseLanguage.JAVA) {
						if (file.isDirectory() && file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getSourceRoot().getCanonicalPath())) {
							add(workspaceFileBrowser.mcreator.actionRegistry.newClass);
							addSeparator();
							add(workspaceFileBrowser.mcreator.actionRegistry.newPackage);
						} else if (file.isDirectory() && (file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getResourceRoot().getCanonicalPath()))) {
							add(workspaceFileBrowser.mcreator.actionRegistry.newJson);
							add(workspaceFileBrowser.mcreator.actionRegistry.newImage);
							addSeparator();
							add(workspaceFileBrowser.mcreator.actionRegistry.newFolder);
						}
					} else {
						if (file.isDirectory() && file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getSourceRoot().getCanonicalPath())
								|| file.isDirectory() && (file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getResourceRoot().getCanonicalPath()))) {
							add(workspaceFileBrowser.mcreator.actionRegistry.newJson);
							add(workspaceFileBrowser.mcreator.actionRegistry.newImage);
							addSeparator();
							add(workspaceFileBrowser.mcreator.actionRegistry.newFolder);
						}
					}
				} else if (selected == workspaceFileBrowser.sourceCode) {
					add(workspaceFileBrowser.mcreator.actionRegistry.newClass);
					addSeparator();
					add(workspaceFileBrowser.mcreator.actionRegistry.newPackage);
				}
			}
		} catch (Exception ignored) {
		}

		if (getComponents().length == 0) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
