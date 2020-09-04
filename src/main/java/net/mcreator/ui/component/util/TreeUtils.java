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

package net.mcreator.ui.component.util;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeUtils {

	public static List<DefaultMutableTreeNode> getExpansionState(JTree tree) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		List<DefaultMutableTreeNode> nodes = new ArrayList<>();
		addToNodeList(root, nodes);

		List<DefaultMutableTreeNode> expandedNodes = new ArrayList<>();

		for (DefaultMutableTreeNode node : nodes)
			if (tree.isExpanded(getPath(node)))
				expandedNodes.add(node);

		return expandedNodes;
	}

	private static void addToNodeList(DefaultMutableTreeNode node, List<DefaultMutableTreeNode> nodes) {
		if (node == null)
			return;

		nodes.add(node);
		int childCount = node.getChildCount();
		for (int i = 0; i < childCount; i++) {
			addToNodeList((DefaultMutableTreeNode) node.getChildAt(i), nodes);
		}
	}

	private static TreePath getPath(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}

	public static void setExpansionState(JTree tree, List<DefaultMutableTreeNode> expandedNodes) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		expandNodes(tree, root, expandedNodes);
	}

	private static void expandNodes(JTree tree, DefaultMutableTreeNode node,
			List<DefaultMutableTreeNode> expandedNodes) {
		tryExpand(tree, node, expandedNodes);
		int childCount = node.getChildCount();
		for (int i = 0; i < childCount; i++) {
			expandNodes(tree, (DefaultMutableTreeNode) node.getChildAt(i), expandedNodes);
		}
	}

	private static void tryExpand(JTree tree, DefaultMutableTreeNode node, List<DefaultMutableTreeNode> expandedNodes) {
		boolean isExpanded = false;
		for (DefaultMutableTreeNode nodeCanidate : expandedNodes)
			if (nodeCanidate.toString().equals(node.toString()))
				if (Objects.requireNonNull(getPath(nodeCanidate)).toString()
						.equals(Objects.requireNonNull(getPath(node)).toString()))
					isExpanded = true;
		if (isExpanded)
			tree.expandPath(getPath(node));
	}

	public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}
		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}

	private static void collapseAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.collapseRow(i);
		}
		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}

	public static void expandDescendants(JTree tree, TreePath path) {
		TreeModel model = tree.getModel();
		Object node = path.getLastPathComponent();

		// mostly redundant precondition checking!
		if (model.isLeaf(node))
			return;

		for (int count = model.getChildCount(node), i = 0; i < count; i++) {
			Object child = model.getChild(node, i);

			if (!model.isLeaf(child)) {
				TreePath childPath = path.pathByAddingChild(child);
				tree.expandPath(childPath);
				expandDescendants(tree, childPath);
			}
		}
	}
}
