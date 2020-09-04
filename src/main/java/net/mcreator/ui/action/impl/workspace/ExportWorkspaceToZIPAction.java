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
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.ShareableZIPManager;

import java.io.File;

public class ExportWorkspaceToZIPAction extends BasicAction {

	public ExportWorkspaceToZIPAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.workspace.export_workspace"), e -> {
			File file = FileDialogs.getSaveDialog(actionRegistry.getMCreator(), new String[] { ".zip" });
			if (file != null)
				ShareableZIPManager.exportZIP(L10N.t("dialog.workspace.export_workspace.title"), file,
						actionRegistry.getMCreator(), true);
		});
	}

	public static class WithRunDir extends BasicAction {

		public WithRunDir(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.export_workspace_include_run"), e -> {
				File file = FileDialogs.getSaveDialog(actionRegistry.getMCreator(), new String[] { ".zip" });
				if (file != null)
					ShareableZIPManager.exportZIP(L10N.t("dialog.workspace.export_workspace.title"), file,
							actionRegistry.getMCreator(), false);
			});
		}

	}

}
