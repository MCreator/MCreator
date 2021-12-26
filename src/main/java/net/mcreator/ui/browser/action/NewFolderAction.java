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

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.RegistryNameValidator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class NewFolderAction extends BasicAction {

	public NewFolderAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.browser.new_folder"), actionEvent -> {
			String foldername = VOptionPane.showInputDialog(actionRegistry.getMCreator(),
					L10N.t("workspace_file_browser.new_folder_name.folder_name"),
					L10N.t("workspace_file_browser.new_folder_name.folder_name.title"), null,
					new OptionPaneValidatior() {
						@Override public Validator.ValidationResult validate(JComponent component) {
							return new RegistryNameValidator((VTextField) component,
									L10N.t("workspace_file_browser.new_folder_name.folder")).validate();
						}
					});
			if (foldername != null) {
				if (actionRegistry.getMCreator().getProjectBrowser().tree.getLastSelectedPathComponent() != null) {
					Object selection = ((DefaultMutableTreeNode) actionRegistry.getMCreator()
							.getProjectBrowser().tree.getLastSelectedPathComponent()).getUserObject();
					if (selection instanceof File filesel) {
						if (filesel.isFile())
							filesel = filesel.getParentFile();

						if (filesel.isDirectory()) {
							new File(filesel, foldername).mkdirs();
							actionRegistry.getMCreator().getProjectBrowser().reloadTree();
						}
					}
				}
			}
		});
		setIcon(UIRES.get("16px.directory.gif"));
	}

}
