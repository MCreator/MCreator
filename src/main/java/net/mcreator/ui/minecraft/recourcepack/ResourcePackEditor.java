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

import net.mcreator.io.FileIO;
import net.mcreator.io.tree.FileNode;
import net.mcreator.io.tree.FileTree;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.minecraft.ResourcePackStructure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResourcePackEditor extends JPanel implements IReloadableFilterable {

	private final Workspace workspace;

	@Nullable private final WorkspacePanel workspacePanel;

	private final JFileTree tree;

	private final JFileBreadCrumb breadCrumb;

	private final FilteredTreeModel model = new FilteredTreeModel(new FilterTreeNode(""));

	private final JPanel previewPanel = new JPanel();

	@Nullable private File resourcePackArchive = null;

	public ResourcePackEditor(MCreator mcreator, @Nullable WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspace = mcreator.getWorkspace();
		this.workspacePanel = workspacePanel;

		this.tree = new JFileTree(model);
		tree.setCellRenderer(new ResourcePackTreeCellRenderer());

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setOpaque(false);
		jsp.getViewport().setOpaque(false);
		jsp.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Theme.current().getBackgroundColor()));
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());

		jsp.setPreferredSize(new Dimension(320, 0));

		add("West", jsp);

		File root = ResourcePackStructure.getResourcePackRoot(workspace);
		this.breadCrumb = new JFileBreadCrumb(mcreator, root, root);

		previewPanel.setOpaque(false);
		add("Center", PanelUtils.northAndCenterElement(breadCrumb, previewPanel));

		tree.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				if (tree.getLastSelectedPathComponent() instanceof FilterTreeNode node
						&& node.getUserObject() instanceof FileNode<?> fileNode) {
					if (fileNode.getObject() instanceof ResourcePackStructure.Entry entry) {
						setSelectedEntry(entry);
					}
				}
			}
		});
	}

	private void setSelectedEntry(ResourcePackStructure.Entry entry) {
		previewPanel.removeAll();

		breadCrumb.reloadPath(entry.override());

		String extension = FilenameUtils.getExtension(entry.path());
		if (extension.equalsIgnoreCase("png")) {
			Image image = ZipIO.readFileInZip(resourcePackArchive, entry.fullPath(), (file, zipEntry) -> {
				try {
					return ImageIO.read(file.getInputStream(zipEntry));
				} catch (IOException e) {
					return null;
				}
			});
			ImageIcon originalIcon = null;
			if (image != null) {
				originalIcon = new ImageIcon(image);
			}
			ImageIcon overrideIcon = null;
			if (entry.type() != ResourcePackStructure.EntryType.VANILLA) {
				overrideIcon = new ImageIcon(entry.override().getAbsolutePath());
			}
			showImageEntry(originalIcon, overrideIcon);
		} else {
			String original = ZipIO.readCodeInZip(resourcePackArchive, entry.fullPath());
			String override = null;
			if (entry.type() != ResourcePackStructure.EntryType.VANILLA) {
				override = FileIO.readFileToString(entry.override());
			}
			showTextEntry(original, override);
		}
	}

	private void showImageEntry(@Nullable ImageIcon original, @Nullable ImageIcon override) {
		if (original != null) {

		}
	}

	private void showTextEntry(@Nullable String original, @Nullable String override) {
		if (original != null) {
			System.err.println(original);
		}
	}

	private boolean initial = true;

	@Override public void reloadElements() {
		List<DefaultMutableTreeNode> state = TreeUtils.getExpansionState(tree);

		FilterTreeNode root = new FilterTreeNode("");

		FileTree<ResourcePackStructure.Entry> fileTree = new FileTree<>(new FileNode<>("", ""));
		resourcePackArchive = ResourcePackStructure.getResourcePackArchive(workspace);
		ResourcePackStructure.getResourcePackStructure(workspace, resourcePackArchive)
				.forEach(entry -> fileTree.addElement(entry.path(), entry));
		JFileTree.addFileNodeToRoot(root, fileTree.root());

		model.setRoot(root);

		if (initial) {
			TreeUtils.expandMatchingNodesRecursively(tree, root, node -> {
				if (node instanceof FilterTreeNode filterTreeNode) {
					if (filterTreeNode.getUserObject() instanceof FileNode<?> fileNode) {
						if (fileNode.getObject() instanceof ResourcePackStructure.Entry entry) {
							return entry.type() != ResourcePackStructure.EntryType.VANILLA;
						}
					}
				}
				return false;
			});
			initial = false;
		} else {
			TreeUtils.setExpansionState(tree, state);
		}
	}

	@Override public void refilterElements() {
		if (workspacePanel != null) {
			if (workspacePanel.search.getText().trim().length() >= 3) {
				model.setFilter(workspacePanel.search.getText().trim());
				SwingUtilities.invokeLater(() -> TreeUtils.expandAllNodes(tree, 0, tree.getRowCount()));
			} else {
				model.setFilter("");
			}
		}
	}

}
