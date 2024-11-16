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

package net.mcreator.ui.minecraft.recourcepack;

import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResourcePackEditor extends JPanel implements IReloadableFilterable {

	private final Workspace workspace;

	public ResourcePackEditor(Workspace workspace, @Nullable WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspace = workspace;
	}

	@Override public void reloadElements() {
		List<ResourcePackStructure.Entry> entries = ResourcePackStructure.getResourcePackStructure(workspace);
	}

	@Override public void refilterElements() {

	}

}
