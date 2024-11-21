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

import net.mcreator.io.tree.FileNode;
import net.mcreator.minecraft.ResourcePackStructure;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ResourcePackTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		FilterTreeNode node = (FilterTreeNode) value;
		if (node.getUserObject() instanceof FileNode<?> fileNode) {
			a.setText(fileNode.data);
			a.setIcon(FileIcons.getIconForFile(fileNode.data, !fileNode.isLeaf()));
			if (!sel && fileNode.getObject() instanceof ResourcePackStructure.Entry entry) {
				if (entry.type() == ResourcePackStructure.EntryType.VANILLA) {
					a.setForeground(Theme.current().getAltForegroundColor());
				} else {
					a.setForeground(Theme.current().getForegroundColor());
				}
			}
		}

		if (node.getFilter() != null && !node.getFilter().isEmpty()) {
			a.setText("<html>" + getText().replace(node.getFilter(), "<b>" + node.getFilter() + "</b>"));
		}

		return a;
	}
}
