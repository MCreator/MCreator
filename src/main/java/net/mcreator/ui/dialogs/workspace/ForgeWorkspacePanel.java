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

package net.mcreator.ui.dialogs.workspace;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;

public class ForgeWorkspacePanel extends AbstractWorkspacePanel {

	public ForgeWorkspacePanel(Window parent) {
		super(parent, GeneratorFlavor.FORGE);

		addFormElement(new JEmptyBox(20, 20));

		JLabel requiredModInfosLabel = new JLabel("Required mod infos");
		requiredModInfosLabel.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		addFormElement(PanelUtils.westAndEastElement(requiredModInfosLabel, new JEmptyBox(0, 0)));

		addFormElement(new JEmptyBox(15, 15));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.forge.display_name"),
				workspaceDialogPanel.modName));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.forge.modid"),
				workspaceDialogPanel.modID));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.forge.generator"),
				workspaceDialogPanel.generatorSelector));

		addFormElement(new JEmptyBox(30, 30));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.forge.package"),
				workspaceDialogPanel.packageName));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.forge.folder"),
				PanelUtils.centerAndEastElement(workspaceFolder, selectWorkspaceFolder, 0, 0)));

		addFormElement(new JEmptyBox(10, 10));

		JLabel additionalModInfosLabel = new JLabel("Additional mod infos");
		additionalModInfosLabel.setFont(new Font("Sans-Serif", Font.BOLD, 18));

		addFormElement(PanelUtils.westAndEastElement(additionalModInfosLabel, new JEmptyBox(0, 0)));

		addFormElement(new JEmptyBox(15, 15));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.workspace_settings.author"),
				workspaceDialogPanel.author));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.workspace_settings.description"),
				workspaceDialogPanel.description));

		addFormElement(new JEmptyBox(5, 5));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.workspace_settings.license"),
				workspaceDialogPanel.license));

		addNotice(UIRES.get("18px.info"), "dialog.new_workspace.forge.notice");

		validationGroup.addValidationElement(workspaceDialogPanel.modName);
		validationGroup.addValidationElement(workspaceDialogPanel.modID);
		validationGroup.addValidationElement(workspaceDialogPanel.packageName);
		validationGroup.addValidationElement(workspaceFolder);
	}

}
