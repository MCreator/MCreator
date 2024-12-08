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

package net.mcreator.ui.variants.modmaker;

import net.mcreator.ui.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

public final class ModMaker extends MCreator {

	public ModMaker(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		super(application, workspace, true);

		new ModMakerDropTarget(this);
	}

	@Override public MainMenuBar createMenuBar() {
		return new ModMakerMenuBar(this);
	}

	@Override public MainToolBar createToolBar() {
		return new ModMakerToolBar(this);
	}

	@Override protected JPanel createWorkspaceTabContent() {
		return ComponentUtils.applyPadding(getWorkspacePanel(), 5, true, true, true, true);
	}

}
