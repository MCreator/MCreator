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

import net.mcreator.minecraft.resourcepack.ResourcePackInfo;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.recourcepack.ResourcePackEditor;
import net.mcreator.ui.workspace.AbstractMainWorkspacePanel;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;

import javax.swing.*;
import java.awt.*;

public class ResourcePackMakerWorkspacePanel extends AbstractMainWorkspacePanel {

	public final ResourcePackEditor resourcePackEditor;

	ResourcePackMakerWorkspacePanel(MCreator mcreator) {
		super(mcreator, new BorderLayout(3, 3));

		JPanel topPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topPan.setOpaque(false);
		topPan.add(search);

		add("North", topPan);

		resourcePackEditor = new ResourcePackEditor(mcreator, new ResourcePackInfo.Vanilla(mcreator.getWorkspace()),
				() -> search.getText().trim());

		addVerticalTab("mods", L10N.t("workspace.category.resources"),
				new WorkspacePanelResourcePack(resourcePackEditor));
	}

	public ResourcePackEditor getResourcePackEditor() {
		return resourcePackEditor;
	}

	private class WorkspacePanelResourcePack extends AbstractWorkspacePanel {

		private WorkspacePanelResourcePack(JComponent contents) {
			super(ResourcePackMakerWorkspacePanel.this);
			add(contents);
		}

		@Override public void reloadElements() {
			resourcePackEditor.reloadElements();
		}

		@Override public void refilterElements() {
			resourcePackEditor.refilterElements();
		}
	}

}

