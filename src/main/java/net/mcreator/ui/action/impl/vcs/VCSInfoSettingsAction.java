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

package net.mcreator.ui.action.impl.vcs;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.vcs.VCSSetupDialogs;
import net.mcreator.vcs.VCSInfo;

import java.io.File;

public class VCSInfoSettingsAction extends VCSAction {

	public VCSInfoSettingsAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.vcs.settings"), e -> {
			VCSInfo vcsInfo = actionRegistry.getMCreator().getWorkspace().getVCS().getInfo();
			VCSInfo newInfo;
			if (vcsInfo != null) {
				newInfo = VCSSetupDialogs
						.getVCSInfoDialog(actionRegistry.getMCreator(), L10N.t("dialog.vcs.settings.help.message"),
								vcsInfo.getRemote(), vcsInfo.getUsername(), vcsInfo.isPromptForPassword(), false);
			} else {
				newInfo = VCSSetupDialogs
						.getVCSInfoDialog(actionRegistry.getMCreator(), L10N.t("dialog.vcs.settings.help.message"));
			}

			if (newInfo != null) {
				actionRegistry.getMCreator().getWorkspace().getVCS().setInfo(newInfo);
				VCSInfo.saveToFile(newInfo,
						new File(actionRegistry.getMCreator().getWorkspace().getFolderManager().getWorkspaceCacheDir(),
								"vcsInfo"));
			}
		});
		setIcon(UIRES.get("16px.vcs"));
	}

}
