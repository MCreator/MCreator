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

	AddFileDropdown(ProjectBrowser projectBrowser) {
		setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		try {
			ProjectBrowserFilterTreeNode selected = (ProjectBrowserFilterTreeNode) projectBrowser.tree
					.getLastSelectedPathComponent();
			if (selected != null) {
				if (selected.getUserObject() instanceof File) {
					File file = (File) selected.getUserObject();
					if (file.isFile())
						file = file.getParentFile();

					if (projectBrowser.mcreator.getWorkspace().getGenerator().getGeneratorConfiguration()
							.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA) {
						if (file.isDirectory() && file.getCanonicalPath().startsWith(
								projectBrowser.mcreator.getWorkspace().getGenerator().getSourceRoot()
										.getCanonicalPath())) {
							add(projectBrowser.mcreator.actionRegistry.newClass);
							addSeparator();
							add(projectBrowser.mcreator.actionRegistry.newPackage);
						} else if (file.isDirectory() && (file.getCanonicalPath().startsWith(
								projectBrowser.mcreator.getWorkspace().getGenerator().getResourceRoot()
										.getCanonicalPath()))) {
							add(projectBrowser.mcreator.actionRegistry.newJson);
							add(projectBrowser.mcreator.actionRegistry.newImage);
							addSeparator();
							add(projectBrowser.mcreator.actionRegistry.newFolder);
						}
					} else {
						if (file.isDirectory() && file.getCanonicalPath().startsWith(
								projectBrowser.mcreator.getWorkspace().getGenerator().getSourceRoot()
										.getCanonicalPath()) || file.isDirectory() && (file.getCanonicalPath()
								.startsWith(projectBrowser.mcreator.getWorkspace().getGenerator().getResourceRoot()
										.getCanonicalPath()))) {
							add(projectBrowser.mcreator.actionRegistry.newJson);
							add(projectBrowser.mcreator.actionRegistry.newImage);
							addSeparator();
							add(projectBrowser.mcreator.actionRegistry.newFolder);
						}
					}
				} else if (selected == projectBrowser.sourceCode) {
					add(projectBrowser.mcreator.actionRegistry.newClass);
					addSeparator();
					add(projectBrowser.mcreator.actionRegistry.newPackage);
				}
			}
		} catch (Exception ignored) {
		}

		if (getComponents().length == 0) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
