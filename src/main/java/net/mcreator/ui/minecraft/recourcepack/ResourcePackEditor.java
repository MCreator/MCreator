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
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.CodePreviewPanel;
import net.mcreator.ui.component.ImagePreviewPanel;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ResourcePackEditor extends JPanel implements IReloadableFilterable {

	private static final List<String> textExtensions = List.of("json", "mcmeta", "fsh", "vsh");

	private final MCreator mcreator;
	private final Workspace workspace;

	@Nullable private final WorkspacePanel workspacePanel;

	private final JFileTree tree;
	@Nullable ResourcePackStructure.Entry selectedEntry = null;

	private final JFileBreadCrumb breadCrumb;

	private final FilteredTreeModel model = new FilteredTreeModel(new FilterTreeNode(""));

	private final JPanel previewPanel = new JPanel(new GridLayout());

	@Nullable private File resourcePackArchive = null;

	private final JLabel originalLabel = L10N.label("mcreator.resourcepack.original");
	private final JLabel overrideLabel = L10N.label("mcreator.resourcepack.override");

	private final JButton editFile;
	private final JButton importFile;
	private final JButton deleteOverrideOrFile;

	public ResourcePackEditor(MCreator mcreator, @Nullable WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;

		this.workspace = mcreator.getWorkspace();
		this.workspacePanel = workspacePanel;

		originalLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
		ComponentUtils.deriveFont(originalLabel, 13);
		originalLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		originalLabel.setVerticalTextPosition(SwingConstants.CENTER);

		overrideLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
		ComponentUtils.deriveFont(overrideLabel, 13);
		overrideLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		overrideLabel.setVerticalTextPosition(SwingConstants.CENTER);

		this.tree = new JFileTree(model);
		tree.setCellRenderer(new ResourcePackTreeCellRenderer());

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());

		TransparentToolBar folderBar = new TransparentToolBar();
		add("North", folderBar);

		JButton addFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.add_file",
				UIRES.get("16px.add"), e -> {
					// TODO: implement
				});
		folderBar.add(addFile);

		JButton addFolder = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.add_folder",
				UIRES.get("16px.directory"), e -> {
					// TODO: implement
				});
		folderBar.add(addFolder);

		File root = ResourcePackStructure.getResourcePackRoot(workspace);
		this.breadCrumb = new JFileBreadCrumb(mcreator, root, root);

		previewPanel.setOpaque(false);

		TransparentToolBar fileBar = new TransparentToolBar();
		add("North", fileBar);

		editFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.edit_override",
				UIRES.get("16px.edit"), e -> {
					if (selectedEntry != null && selectedEntry.type() != ResourcePackStructure.EntryType.VANILLA) {
						File override = selectedEntry.override();
						if (override.isFile()) {
							FileOpener.openFile(mcreator, override);
						}
					} else {
						// TODO: ask if make new file or duplicate vanilla and edit
					}
				});
		fileBar.add(editFile);

		importFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.import_override",
				UIRES.get("16px.open"), e -> {
					// TODO: implement
				});
		fileBar.add(importFile);

		deleteOverrideOrFile = AbstractWorkspacePanel.createToolBarButton("common.delete_selected",
				UIRES.get("16px.delete"), e -> {
					if (selectedEntry != null && selectedEntry.type() != ResourcePackStructure.EntryType.VANILLA) {
						File toDelete = selectedEntry.override();
						int n = JOptionPane.showConfirmDialog(mcreator,
								L10N.t("mcreator.resourcepack.delete_override_confirm", toDelete.getName()),
								L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (n == JOptionPane.YES_OPTION) {
							if (toDelete.isDirectory()) {
								FileIO.deleteDir(toDelete);
							} else {
								toDelete.delete();
							}
							setSelectedEntry(null);
							reloadElements();
						}
					}
				});
		fileBar.add(deleteOverrideOrFile);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				PanelUtils.northAndCenterElement(folderBar, jsp),
				PanelUtils.northAndCenterElement(fileBar, PanelUtils.centerAndSouthElement(previewPanel, breadCrumb)));
		splitPane.setDividerLocation(300);
		splitPane.setOpaque(false);
		splitPane.setBackground(Theme.current().getBackgroundColor());

		add("Center", splitPane);

		tree.addTreeSelectionListener(e -> {
			ResourcePackStructure.Entry toSelect = null;
			if (tree.getLastSelectedPathComponent() instanceof FilterTreeNode node
					&& node.getUserObject() instanceof FileNode<?> fileNode) {
				if (fileNode.getObject() instanceof ResourcePackStructure.Entry entry) {
					if (entry.path().equals(fileNode.incrementalPath)) {
						if (entry.type() == ResourcePackStructure.EntryType.VANILLA || entry.override().exists())
							toSelect = entry; // prevents edge cases where deleted entry is attempted to be selected
					} else {
						toSelect = entry.parent();
					}
				}
			}
			setSelectedEntry(toSelect);
		});

		editFile.setEnabled(false);
		importFile.setEnabled(false);
		deleteOverrideOrFile.setEnabled(false);
	}

	private void setSelectedEntry(final @Nullable ResourcePackStructure.Entry entry) {
		this.selectedEntry = entry;

		previewPanel.removeAll();

		editFile.setEnabled(false);
		importFile.setEnabled(false);
		deleteOverrideOrFile.setEnabled(false);

		if (entry != null) {
			breadCrumb.reloadPath(entry.override());

			String extension = FilenameUtils.getExtension(entry.path());

			importFile.setEnabled(true);
			if (!extension.isBlank()) {
				editFile.setEnabled(true);
				if (entry.type() == ResourcePackStructure.EntryType.VANILLA) {
					editFile.setText(L10N.t("mcreator.resourcepack.edit_vanilla"));
				} else if (entry.type() == ResourcePackStructure.EntryType.CUSTOM) {
					editFile.setText(L10N.t("mcreator.resourcepack.edit_file"));
				} else {
					editFile.setText(L10N.t("mcreator.resourcepack.edit_override"));
				}
			}
			if (entry.type() != ResourcePackStructure.EntryType.VANILLA) {
				deleteOverrideOrFile.setEnabled(true);
			}

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
			} else if (textExtensions.contains(extension.toLowerCase(Locale.ROOT))) {
				String original = ZipIO.readCodeInZip(resourcePackArchive, entry.fullPath());
				String override = null;
				if (entry.type() != ResourcePackStructure.EntryType.VANILLA) {
					override = FileIO.readFileToString(entry.override());
				}
				showTextEntry(entry.override(), original, override);
			}
		}

		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void showImageEntry(@Nullable ImageIcon original, @Nullable ImageIcon override) {
		if (original != null) {
			ImagePreviewPanel imagePreviewPanel = new ImagePreviewPanel(original);
			if (override != null) {
				ImagePreviewPanel overrideImagePreviewPanel = new ImagePreviewPanel(override);
				previewPanel.add(PanelUtils.gridElements(1, 2,
						PanelUtils.northAndCenterElement(originalLabel, imagePreviewPanel),
						PanelUtils.northAndCenterElement(overrideLabel, overrideImagePreviewPanel)));
			} else {
				previewPanel.add(imagePreviewPanel);
			}
		} else if (override != null) {
			ImagePreviewPanel overrideImagePreviewPanel = new ImagePreviewPanel(override);
			previewPanel.add(overrideImagePreviewPanel);
		}
	}

	private void showTextEntry(File file, @Nullable String original, @Nullable String override) {
		if (original != null) {
			CodePreviewPanel codeEditorView = new CodePreviewPanel(original, file);
			if (override != null) {
				CodePreviewPanel overrideCodeEditorView = new CodePreviewPanel(override, file);
				previewPanel.add(
						PanelUtils.gridElements(1, 2, PanelUtils.northAndCenterElement(originalLabel, codeEditorView),
								PanelUtils.northAndCenterElement(overrideLabel, overrideCodeEditorView)));
			} else {
				previewPanel.add(codeEditorView);
			}
		} else if (override != null) {
			CodePreviewPanel overrideCodeEditorView = new CodePreviewPanel(override, file);
			previewPanel.add(overrideCodeEditorView);
		}
	}

	private boolean initial = true;

	@Override public void reloadElements() {
		List<DefaultMutableTreeNode> state = TreeUtils.getExpansionState(tree);
		TreePath selectionPath = tree.getSelectionPath();

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
			tree.setSelectionPath(selectionPath);
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
