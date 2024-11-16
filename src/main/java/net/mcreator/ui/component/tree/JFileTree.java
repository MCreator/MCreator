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

package net.mcreator.ui.component.tree;

import net.mcreator.io.tree.FileNode;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.io.File;
import java.util.Vector;

public class JFileTree extends JTree {

	public JFileTree(FilteredTreeModel model) {
		super(model);

		setRowHeight(18);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setOpaque(false);
		setRootVisible(false);
		setShowsRootHandles(true);
		setBackground(Theme.current().getBackgroundColor());
	}

	public static void addFileNodeToFilterTreeNode(FilterTreeNode node, FileNode<?> root) {
		FilterTreeNode treeNode = new FilterTreeNode(root);
		node.add(treeNode);
		addFileNodeToRoot(treeNode, root);

	}

	public static void addFileNodeToRoot(FilterTreeNode node, FileNode<?> root) {
		for (FileNode<?> child : root.childs)
			addFileNodeToFilterTreeNode(node, child);
		for (FileNode<?> child : root.leafs)
			addFileNodeToFilterTreeNode(node, child);

	}

	public static void addNodes(FilterTreeNode curTop, File dir, boolean first) {
		if (dir == null)
			return;

		String curPath = dir.getPath();
		FilterTreeNode curDir = new FilterTreeNode(dir);
		if (curTop != null && !first) {
			curTop.add(curDir);
		} else {
			curDir = curTop;
		}
		Vector<String> ol = new Vector<>();
		String[] tmp = dir.list();
		for (String aTmp : tmp != null ? tmp : new String[0])
			ol.addElement(aTmp);
		ol.sort(String.CASE_INSENSITIVE_ORDER);
		File f;
		Vector<File> files = new Vector<>();
		for (int i = 0; i < ol.size(); i++) {
			String thisObject = ol.elementAt(i);
			String newPath;
			if (curPath.equals("."))
				newPath = thisObject;
			else
				newPath = curPath + File.separator + thisObject;
			if ((f = new File(newPath)).isDirectory())
				addNodes(curDir, f, false);
			else
				files.addElement(new File(newPath));
		}
		for (int fnum = 0; fnum < files.size(); fnum++)
			if (curDir != null)
				curDir.add(new FilterTreeNode(files.elementAt(fnum)));
	}

}
