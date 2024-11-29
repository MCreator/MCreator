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

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class SerializableTreeExpansionState {

	private List<String> expandedPaths;

	public SerializableTreeExpansionState() {
		this.expandedPaths = new ArrayList<>();
	}

	public SerializableTreeExpansionState(List<String> expandedPaths) {
		this.expandedPaths = expandedPaths;
	}

	public List<String> getExpandedPaths() {
		return expandedPaths;
	}

	public void setExpandedPaths(List<String> expandedPaths) {
		this.expandedPaths = expandedPaths;
	}

	/**
	 * Captures the expansion state of a JTree.
	 *
	 * @param tree the JTree to capture state from
	 * @return a SerializableTreeExpansionState representing the JTree's expansion state
	 */
	public static SerializableTreeExpansionState fromTree(JTree tree) {
		List<String> expandedPaths = new ArrayList<>();
		TreePath rootPath = tree.getPathForRow(0); // Assume the root is at row 0

		if (rootPath != null) {
			captureExpandedPaths(tree, rootPath, expandedPaths);
		}

		return new SerializableTreeExpansionState(expandedPaths);
	}

	private static void captureExpandedPaths(JTree tree, TreePath path, List<String> expandedPaths) {
		if (tree.isExpanded(path)) {
			expandedPaths.add(path.toString());
			for (int i = 0; i < tree.getModel().getChildCount(path.getLastPathComponent()); i++) {
				Object child = tree.getModel().getChild(path.getLastPathComponent(), i);
				captureExpandedPaths(tree, path.pathByAddingChild(child), expandedPaths);
			}
		}
	}

	/**
	 * Restores the expansion state to a JTree.
	 *
	 * @param tree the JTree to apply the expansion state to
	 */
	public void applyToTree(JTree tree) {
		for (String pathString : expandedPaths) {
			TreePath path = stringToTreePath(tree, pathString);
			if (path != null) {
				tree.expandPath(path);
			}
		}
	}

	private TreePath stringToTreePath(JTree tree, String pathString) {
		String[] elements = pathString.replace("[", "").replace("]", "").split(", ");
		Object root = tree.getModel().getRoot();
		TreePath path = new TreePath(root);

		for (int i = 1; i < elements.length; i++) {
			Object node = findChildNode(tree, path.getLastPathComponent(), elements[i]);
			if (node == null) {
				return null; // If any node in the path is missing, return null
			}
			path = path.pathByAddingChild(node);
		}

		return path;
	}

	private Object findChildNode(JTree tree, Object parent, String nodeString) {
		for (int i = 0; i < tree.getModel().getChildCount(parent); i++) {
			Object child = tree.getModel().getChild(parent, i);
			if (child.toString().equals(nodeString)) {
				return child;
			}
		}
		return null;
	}

}
