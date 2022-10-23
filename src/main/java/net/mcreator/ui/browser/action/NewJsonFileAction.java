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

package net.mcreator.ui.browser.action;

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class NewJsonFileAction extends BasicAction {

	public NewJsonFileAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.browser.new_json_file"), actionEvent -> {
			String fileName = JOptionPane.showInputDialog(actionRegistry.getMCreator(),
					L10N.t("workspace_file_browser.new_json"));

			if (fileName != null) {
				fileName = RegistryNameFixer.fix(fileName);
				if (actionRegistry.getMCreator().getProjectBrowser().tree.getLastSelectedPathComponent() != null) {
					Object selection = ((DefaultMutableTreeNode) actionRegistry.getMCreator()
							.getProjectBrowser().tree.getLastSelectedPathComponent()).getUserObject();
					if (selection instanceof File filesel) {
						if (filesel.isDirectory()) {
							String path = filesel.getPath() + "/" + fileName + (fileName.contains(".") ? "" : ".json");
							FileIO.writeStringToFile("", new File(path));
							actionRegistry.getMCreator().getProjectBrowser().reloadTree();
						}
					}
				}
			}
		});
		setIcon(UIRES.get("16px.json.gif"));
	}

}
