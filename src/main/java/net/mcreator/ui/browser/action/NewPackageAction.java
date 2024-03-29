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

import javax.swing.*;
import java.io.File;

public class NewPackageAction extends BasicAction {

	public NewPackageAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.browser.new_package"), actionEvent -> {
			String packagein = JOptionPane.showInputDialog(actionRegistry.getMCreator(),
					L10N.t("workspace_file_browser.new_package.package_name"),
					L10N.t("workspace_file_browser.new_package.package_name.title"), JOptionPane.QUESTION_MESSAGE);
			if (packagein != null) {
				new File(actionRegistry.getMCreator().getGenerator().getSourceRoot(),
						packagein.replace(".", "/")).mkdirs();
				actionRegistry.getMCreator().getProjectBrowser().reloadTree();
			}
		});
		setIcon(UIRES.get("16px.directory"));
	}

}
