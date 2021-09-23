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
import net.mcreator.ui.component.tree.FilterTreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class AddFileDropdown extends JPopupMenu {

	private final Logger LOG = LogManager.getLogger("Add File Dropdown");

	AddFileDropdown(WorkspaceFileBrowser workspaceFileBrowser) {
		setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		try {
			FilterTreeNode selected = (FilterTreeNode) workspaceFileBrowser.tree.getLastSelectedPathComponent();
			if (selected != null) {
				LOG.debug("Something is selected");
				if (selected.getUserObject() instanceof File) {
					LOG.debug("A file is selected");
					File file = (File) selected.getUserObject();
					if (file.isFile())
						file = file.getParentFile();

					if (workspaceFileBrowser.mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
							== GeneratorFlavor.BaseLanguage.JAVA) {
						LOG.debug("Java file is selected");
						if (file.isDirectory() && file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getSourceRoot().getCanonicalPath())) {
							LOG.debug("Source is selected");
							add(workspaceFileBrowser.mcreator.actionRegistry.newClass);
							addSeparator();
							add(workspaceFileBrowser.mcreator.actionRegistry.newPackage);
						} else if (file.isDirectory() && (file.getCanonicalPath().startsWith(
								workspaceFileBrowser.mcreator.getGenerator().getResourceRoot().getCanonicalPath()))) {
							LOG.debug("Resource is selected");
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
							LOG.debug("Resource file is selected");
							add(workspaceFileBrowser.mcreator.actionRegistry.newJson);
							add(workspaceFileBrowser.mcreator.actionRegistry.newImage);
							addSeparator();
							add(workspaceFileBrowser.mcreator.actionRegistry.newFolder);
						}
					}
				} else if (selected == workspaceFileBrowser.sourceCode) {
					LOG.debug("Source code is selected");
					add(workspaceFileBrowser.mcreator.actionRegistry.newClass);
					addSeparator();
					add(workspaceFileBrowser.mcreator.actionRegistry.newPackage);
				}
			}
		} catch (Exception e) {
			LOG.debug("Nothing is selected :/");
		}

		if (getComponents().length == 0) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
