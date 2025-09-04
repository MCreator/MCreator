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

import java.awt.*;

public class AddonWorkspacePanel extends AbstractWorkspacePanel {

	public AddonWorkspacePanel(Window parent) {
		super(parent, GeneratorFlavor.ADDON);

		addFormElement(new JEmptyBox(20, 20));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.addon.display_name"),
				workspaceDialogPanel.modName));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.addon.addon_id"),
				workspaceDialogPanel.modID));

		addFormElement(new JEmptyBox(10, 10));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.addon.generator"),
				workspaceDialogPanel.generatorSelector));

		addFormElement(new JEmptyBox(30, 30));

		addFormElement(PanelUtils.westAndEastElement(L10N.label("dialog.new_workspace.addon.folder"),
				PanelUtils.centerAndEastElement(workspaceFolder, selectWorkspaceFolder, 0, 0)));

		addNotice(UIRES.get("18px.warning"), "dialog.new_workspace.addon.notice1");
		addNotice(UIRES.get("18px.info"), "dialog.new_workspace.addon.notice2");

		validationGroup.addValidationElement(workspaceDialogPanel.modName);
		validationGroup.addValidationElement(workspaceDialogPanel.modID);
		validationGroup.addValidationElement(workspaceFolder);
	}

}
