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

package net.mcreator.ui.action.impl.workspace;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

public class NewWorkspaceAction extends BasicAction {
	public NewWorkspaceAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.new"), e -> {
			NewWorkspaceDialog newWorkspaceDialog = new NewWorkspaceDialog(actionRegistry.getMCreator());
			if (newWorkspaceDialog.getWorkspaceFile() != null)
				actionRegistry.getMCreator().getApplication()
						.openWorkspaceInMCreator(newWorkspaceDialog.getWorkspaceFile(), null, false);
		});
		setIcon(UIRES.get("16px.add.gif"));
	}
}
