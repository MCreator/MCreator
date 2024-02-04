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
import net.mcreator.io.FileIO;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.io.File;

class WorkspaceFileBrowserContextMenu extends JPopupMenu {

	WorkspaceFileBrowserContextMenu(WorkspaceFileBrowser browser) {
		boolean fileActionsAllowed = false;
		JMenu createMenu = L10N.menu("common.add");
		createMenu.setIcon(UIRES.get("16px.add"));

		FilterTreeNode selected = (FilterTreeNode) browser.tree.getLastSelectedPathComponent();
		if (selected.getUserObject() instanceof File file) {
			if (file.isFile())
				file = file.getParentFile();

			if (browser.mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA) {
				if (file.isDirectory() && FileIO.isFileSomewhereInDirectory(file,
						browser.mcreator.getGenerator().getSourceRoot())) {
					fileActionsAllowed = true;
					createMenu.add(browser.mcreator.actionRegistry.newClass);
					createMenu.add(browser.mcreator.actionRegistry.newPackage);
				} else if (file.isDirectory() && FileIO.isFileSomewhereInDirectory(file,
						browser.mcreator.getGenerator().getResourceRoot())) {
					fileActionsAllowed = true;
					createMenu.add(browser.mcreator.actionRegistry.newJson);
					createMenu.add(browser.mcreator.actionRegistry.newImage);
					createMenu.add(browser.mcreator.actionRegistry.newFolder);
				}
			} else {
				if (file.isDirectory() && (
						FileIO.isFileSomewhereInDirectory(file, browser.mcreator.getGenerator().getSourceRoot())
								|| FileIO.isFileSomewhereInDirectory(file,
								browser.mcreator.getGenerator().getResourceRoot()))) {
					fileActionsAllowed = true;
					createMenu.add(browser.mcreator.actionRegistry.newJson);
					createMenu.add(browser.mcreator.actionRegistry.newImage);
					createMenu.add(browser.mcreator.actionRegistry.newFolder);
				}
			}
		} else if (selected == browser.sourceCode) {
			fileActionsAllowed = true;
			createMenu.add(browser.mcreator.actionRegistry.newClass);
			createMenu.add(browser.mcreator.actionRegistry.newPackage);
		} else if (selected == browser.currRes) {
			fileActionsAllowed = true;
			createMenu.add(browser.mcreator.actionRegistry.newJson);
			createMenu.add(browser.mcreator.actionRegistry.newImage);
			createMenu.add(browser.mcreator.actionRegistry.newFolder);
		}

		boolean fileInWorkspace = selected.getUserObject() instanceof File file && browser.mcreator.getFolderManager()
				.isFileInWorkspace(file, true);
		browser.mcreator.actionRegistry.openFile.setEnabled(true);
		browser.mcreator.actionRegistry.openFileInDesktop.setEnabled(
				fileInWorkspace || selected == browser.sourceCode || selected == browser.currRes);
		browser.mcreator.actionRegistry.showFileInExplorer.setEnabled(
				fileInWorkspace && selected != browser.sourceCode && selected != browser.currRes);
		createMenu.setEnabled(fileActionsAllowed);
		browser.mcreator.actionRegistry.deleteFile.setEnabled(
				fileActionsAllowed && selected != browser.sourceCode && selected != browser.currRes);

		add(browser.mcreator.actionRegistry.openFile);
		add(browser.mcreator.actionRegistry.openFileInDesktop);
		add(browser.mcreator.actionRegistry.showFileInExplorer);
		addSeparator();
		add(createMenu);
		add(browser.mcreator.actionRegistry.deleteFile);
	}
}
