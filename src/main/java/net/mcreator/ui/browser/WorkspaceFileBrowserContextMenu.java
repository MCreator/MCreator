/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.io.File;

class WorkspaceFileBrowserContextMenu extends JPopupMenu {

	WorkspaceFileBrowserContextMenu(WorkspaceFileBrowser browser) {
		boolean contextActionsAvailable = false;
		JMenu createMenu = L10N.menu("workspace_file_browser.add");
		createMenu.setIcon(UIRES.get("16px.add.gif"));

		FilterTreeNode selected = (FilterTreeNode) browser.tree.getLastSelectedPathComponent();
		try {
			if (selected != null) {
				if (selected.getUserObject() instanceof File file) {
					if (file.isFile())
						file = file.getParentFile();

					if (browser.mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
							== GeneratorFlavor.BaseLanguage.JAVA) {
						if (file.isDirectory() && file.getCanonicalPath()
								.startsWith(browser.mcreator.getGenerator().getSourceRoot().getCanonicalPath())) {
							contextActionsAvailable = true;
							createMenu.add(browser.mcreator.actionRegistry.newClass);
							createMenu.add(browser.mcreator.actionRegistry.newPackage);
						} else if (file.isDirectory() && (file.getCanonicalPath()
								.startsWith(browser.mcreator.getGenerator().getResourceRoot().getCanonicalPath()))) {
							contextActionsAvailable = true;
							createMenu.add(browser.mcreator.actionRegistry.newJson);
							createMenu.add(browser.mcreator.actionRegistry.newImage);
							createMenu.add(browser.mcreator.actionRegistry.newFolder);
						}
					} else {
						if (file.isDirectory() && file.getCanonicalPath()
								.startsWith(browser.mcreator.getGenerator().getSourceRoot().getCanonicalPath())
								|| file.isDirectory() && (file.getCanonicalPath()
								.startsWith(browser.mcreator.getGenerator().getResourceRoot().getCanonicalPath()))) {
							contextActionsAvailable = true;
							createMenu.add(browser.mcreator.actionRegistry.newJson);
							createMenu.add(browser.mcreator.actionRegistry.newImage);
							createMenu.add(browser.mcreator.actionRegistry.newFolder);
						}
					}
				} else if (selected == browser.sourceCode) {
					contextActionsAvailable = true;
					createMenu.add(browser.mcreator.actionRegistry.newClass);
					createMenu.add(browser.mcreator.actionRegistry.newPackage);
				} else if (selected == browser.currRes) {
					contextActionsAvailable = true;
					createMenu.add(browser.mcreator.actionRegistry.newJson);
					createMenu.add(browser.mcreator.actionRegistry.newImage);
					createMenu.add(browser.mcreator.actionRegistry.newFolder);
				}
			}
		} catch (Exception ignored) {
		}

		boolean fileInWorkspace = selected.getUserObject() instanceof File file && browser.mcreator.getFolderManager()
				.isFileInWorkspace(file);
		browser.mcreator.actionRegistry.openAsCode.setEnabled(selected != null);
		browser.mcreator.actionRegistry.openFile.setEnabled(fileInWorkspace);
		browser.mcreator.actionRegistry.showFileInExplorer.setEnabled(
				fileInWorkspace && selected != browser.sourceCode && selected != browser.currRes);
		createMenu.setEnabled(contextActionsAvailable);
		browser.mcreator.actionRegistry.deleteFile.setEnabled(
				contextActionsAvailable && selected != browser.sourceCode && selected != browser.currRes);

		add(browser.mcreator.actionRegistry.openAsCode);
		add(browser.mcreator.actionRegistry.openFile);
		add(browser.mcreator.actionRegistry.showFileInExplorer);
		addSeparator();
		add(createMenu);
		add(browser.mcreator.actionRegistry.deleteFile);
	}
}
