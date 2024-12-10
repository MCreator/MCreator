/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.variants.resourcepackmaker;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MainMenuBar;
import net.mcreator.ui.init.L10N;

import javax.swing.*;

class ResourcePackMakerMenuBar extends MainMenuBar {

	protected ResourcePackMakerMenuBar(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected void assembleMenuBar(MCreator mcreator) {
		JMenu workspace = L10N.menu("menubar.workspace");
		workspace.setMnemonic('S');
		workspace.add(mcreator.getActionRegistry().openWorkspaceFolder);
		workspace.addSeparator();
		workspace.add(mcreator.getActionRegistry().workspaceSettings);
		workspace.addSeparator();
		workspace.add(mcreator.getActionRegistry().exportToJAR);
		add(workspace);

		JMenu build = L10N.menu("menubar.build_and_run");
		build.setMnemonic('B');
		build.add(mcreator.getActionRegistry().buildWorkspace);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().reloadGradleProject);
		build.add(mcreator.getActionRegistry().clearAllGradleCaches);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().cancelGradleTaskAction);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().runGradleTask);
		build.addSeparator();
		build.add(mcreator.getActionRegistry().runClient);
		add(build);
	}

}
