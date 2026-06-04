/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.browser;

import net.mcreator.io.tree.FileNode;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

class ProjectBrowserCellRenderer extends DefaultTreeCellRenderer {

	private final MCreator mcreator;

	public ProjectBrowserCellRenderer(MCreator mcreator) {
		this.mcreator = mcreator;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		FilterTreeNode node = (FilterTreeNode) value;
		if (node.getUserObject() instanceof String tsi) {
			a.setText(tsi);
			if (tsi.equals(mcreator.getWorkspaceSettings().getModName()))
				a.setIcon(UIRES.get("16px.package"));
			else if (tsi.equals("Source (Gradle)"))
				a.setIcon(UIRES.get("16px.mod"));
			else if (tsi.equals("Textures"))
				a.setIcon(UIRES.get("16px.textures"));
			else if (tsi.equals("Resources (Gradle)"))
				a.setIcon(UIRES.get("16px.resources"));
			else if (tsi.equals("Models"))
				a.setIcon(UIRES.get("16px.models"));
			else if (tsi.equals("Minecraft run folder") || tsi.equals("Bedrock Edition") || tsi.equals(
					"MC client run folder"))
				a.setIcon(UIRES.get("16px.minecraft"));
			else if (tsi.equals("MC server run folder"))
				a.setIcon(UIRES.get("16px.runserver"));
			else if (tsi.equals("Sounds"))
				a.setIcon(UIRES.get("16px.music"));
			else if (tsi.equals("External libraries"))
				a.setIcon(UIRES.get("16px.directory"));
			else if (tsi.equals("Structures"))
				a.setIcon(UIRES.get("16px.structures"));
		} else if (node.getUserObject() instanceof FileNode<?> fileNode) {
			a.setText(fileNode.data);
			if (fileNode.data.endsWith(".java"))
				a.setIcon(UIRES.get("16px.classro"));
			else if (fileNode.data.startsWith("Gradle: "))
				a.setIcon(UIRES.get("16px.ext"));
			else if (fileNode.data.startsWith("<") && fileNode.data.endsWith(">"))
				a.setIcon(UIRES.get("16px.directory"));
			else
				a.setIcon(FileIcons.getIconForFile(fileNode.data, !fileNode.isLeaf()));
		} else if (node.getUserObject() instanceof File fil) {
			a.setText(fil.getName());
			a.setIcon(FileIcons.getIconForFile(fil));
		}

		if (node.getFilter() != null && !node.getFilter().isEmpty()) {
			a.setText("<html>" + getText().replace(node.getFilter(), "<b>" + node.getFilter() + "</b>"));
		}

		return a;
	}
}
