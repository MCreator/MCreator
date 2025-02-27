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
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.minecraft.resourcepack.ResourcePackInfo;
import net.mcreator.minecraft.resourcepack.ResourcePackStructure;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.browser.action.NewFolderAction;
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
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FileUtils;
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
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ResourcePackEditor extends JPanel implements IReloadableFilterable {

	private static final List<String> textExtensions = List.of("json", "mcmeta", "fsh", "vsh");

	private final MCreator mcreator;

	private final Workspace workspace;

	@Nullable private final Supplier<String> filterProvider;

	private final JFileTree tree;
	@Nullable ResourcePackStructure.Entry selectedEntry = null;

	private final JFileBreadCrumb breadCrumb;

	private final FilteredTreeModel model = new FilteredTreeModel(new FilterTreeNode(""));

	private final JPanel previewPanel = new JPanel(new GridLayout());

	private final ResourcePackInfo resourcePack;

	private final JLabel originalLabel = L10N.label("mcreator.resourcepack.original");
	private final JLabel overrideLabel = L10N.label("mcreator.resourcepack.override");

	private final JButton editFile;
	private final JButton importFile;
	private final JButton deleteOverrideOrFile;

	public ResourcePackEditor(MCreator mcreator, ResourcePackInfo resourcePack,
			@Nullable Supplier<String> filterProvider) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;
		this.workspace = mcreator.getWorkspace();
		this.filterProvider = filterProvider;
		this.resourcePack = resourcePack;

		originalLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
		ComponentUtils.deriveFont(originalLabel, 13);

		overrideLabel.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
		ComponentUtils.deriveFont(overrideLabel, 13);

		this.tree = new JFileTree(model);
		tree.setCellRenderer(new ResourcePackTreeCellRenderer());

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());

		TransparentToolBar folderBar = new TransparentToolBar();

		JPopupMenu createMenu = new JPopupMenu();
		JMenuItem createJSON = new JMenuItem(L10N.t("action.browser.new_json_file"));
		createJSON.addActionListener(e -> {
			File currentFolder = getCurrentFolder();
			if (currentFolder != null) {
				String fileName = JOptionPane.showInputDialog(mcreator, L10N.t("workspace_file_browser.new_json"));
				if (fileName != null) {
					fileName = RegistryNameFixer.fix(fileName);
					FileIO.writeStringToFile("",
							new File(currentFolder, fileName + (fileName.contains(".") ? "" : ".json")));
					reloadElements();
				}
			}
		});
		createMenu.add(createJSON);
		JMenuItem createPNG = new JMenuItem(L10N.t("action.browser.new_image_file"));
		createPNG.addActionListener(e -> {
			File currentFolder = getCurrentFolder();
			if (currentFolder != null) {
				String fileName = JOptionPane.showInputDialog(mcreator, L10N.t("workspace_file_browser.new_image"));
				if (fileName != null) {
					fileName = RegistryNameFixer.fix(fileName);
					ImageMakerView imageMakerView = new ImageMakerView(mcreator);
					new NewImageDialog(mcreator, imageMakerView).setVisible(true);
					imageMakerView.setSaveLocation(
							new File(currentFolder, fileName + (fileName.contains(".") ? "" : ".png")));
					reloadElements();
				}
			}
		});
		createMenu.add(createPNG);
		JButton addFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.add_file",
				UIRES.get("16px.add"));
		addFile.addActionListener(e -> {
			if (selectedEntry != null) {
				createMenu.show(addFile, 5, addFile.getHeight() + 5);
			}
		});
		folderBar.add(addFile);

		JButton addFolder = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.add_folder",
				UIRES.get("16px.directory"), e -> {
					File currentFolder = getCurrentFolder();
					if (currentFolder != null) {
						File folderToMake = NewFolderAction.openCreateFolderDialog(mcreator, currentFolder);
						if (folderToMake != null) {
							folderToMake.mkdirs();
							reloadElements();
						}
					}
				});
		folderBar.add(addFolder);

		File root = ResourcePackStructure.getResourcePackRoot(workspace, resourcePack.namespace());
		this.breadCrumb = new JFileBreadCrumb(mcreator, root, root);

		TransparentToolBar fileBar = new TransparentToolBar();

		editFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.edit_override",
				UIRES.get("16px.edit"), e -> editOrOverrideCurrentEntry());
		fileBar.add(editFile);

		importFile = AbstractWorkspacePanel.createToolBarButton("mcreator.resourcepack.import_override",
				UIRES.get("16px.open"), e -> {
					if (selectedEntry != null) {
						if (selectedEntry.isFolder()) { // Importing files into a folder
							File importTargetFolder = selectedEntry.override();
							File[] fileOrigin = FileDialogs.getMultiOpenDialog(mcreator, new String[] { "*" });
							if (fileOrigin != null) {
								for (File file : fileOrigin) {
									FileIO.copyFile(file, new File(importTargetFolder, file.getName()));
								}
								reloadElements();
							}
						} else { // Importing a file to override existing file
							File importTarget = selectedEntry.override();
							File fileOrigin = FileDialogs.getOpenDialog(mcreator,
									new String[] { selectedEntry.extension() });
							if (fileOrigin != null) {
								FileIO.copyFile(fileOrigin, importTarget);
								reloadElements();
							}
						}
					}
				});
		fileBar.add(importFile);

		deleteOverrideOrFile = AbstractWorkspacePanel.createToolBarButton("common.delete_selected",
				UIRES.get("16px.delete"), e -> {
					if (selectedEntry != null && selectedEntry.type() != ResourcePackStructure.EntryType.VANILLA) {
						File toDelete = selectedEntry.override();
						int n = JOptionPane.showConfirmDialog(mcreator,
								L10N.t("mcreator.resourcepack.delete_override_confirm"), L10N.t("common.confirmation"),
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (n == JOptionPane.YES_OPTION) {
							if (toDelete.isDirectory()) {
								FileIO.deleteDir(toDelete);
							} else {
								toDelete.delete();
							}
							reloadElements();
						}
					}
				});
		fileBar.add(deleteOverrideOrFile);

		previewPanel.setOpaque(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				PanelUtils.northAndCenterElement(folderBar, jsp),
				PanelUtils.northAndCenterElement(fileBar, PanelUtils.centerAndSouthElement(previewPanel, breadCrumb)));
		splitPane.setDividerLocation(320);
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

		tree.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editOrOverrideCurrentEntry();
				}
			}
		});

		editFile.setEnabled(false);
		importFile.setEnabled(false);
		deleteOverrideOrFile.setEnabled(false);
	}

	private void editOrOverrideCurrentEntry() {
		if (selectedEntry != null) {
			if (!selectedEntry.isFolder()) { // Make sure not a folder
				if (selectedEntry.type() != ResourcePackStructure.EntryType.VANILLA) {
					File override = selectedEntry.override();
					if (override.isFile()) {
						FileOpener.openFile(mcreator, override);
					}
				} else {
					int n = JOptionPane.showConfirmDialog(mcreator,
							L10N.t("mcreator.resourcepack.edit_override_confirm"), L10N.t("common.confirmation"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						File result = ZipIO.readFileInZip(resourcePack.packFile(), selectedEntry.fullPath(),
								(file, zipEntry) -> {
									try {
										FileUtils.copyInputStreamToFile(file.getInputStream(zipEntry),
												selectedEntry.override());
										return selectedEntry.override();
									} catch (IOException e1) {
										return null;
									}
								});
						if (result != null) {
							FileOpener.openFile(mcreator, result);
							reloadElements();
						}
					} else if (n == JOptionPane.NO_OPTION) {
						if (selectedEntry.extension().equals("png")) {
							ImageMakerView imageMakerView = new ImageMakerView(mcreator);
							new NewImageDialog(mcreator, imageMakerView).setVisible(true);
							imageMakerView.setSaveLocation(selectedEntry.override());
						} else if (textExtensions.contains(selectedEntry.extension())) {
							FileIO.writeStringToFile("", selectedEntry.override());
							FileOpener.openFile(mcreator, selectedEntry.override());
							reloadElements();
						} else {
							// Can't create new file of this type
							Toolkit.getDefaultToolkit().beep();
						}
					}
				}
			}
		}
	}

	@Nullable private File getCurrentFolder() {
		File currentFolder = null;
		if (selectedEntry != null) {
			if (selectedEntry.isFolder()) {
				currentFolder = selectedEntry.override(); // Already a folder
			} else {
				currentFolder = selectedEntry.override().getParentFile();
			}
		}
		return currentFolder;
	}

	private void setSelectedEntry(final @Nullable ResourcePackStructure.Entry entry) {
		this.selectedEntry = entry;

		previewPanel.removeAll();

		editFile.setEnabled(false);
		importFile.setEnabled(false);
		deleteOverrideOrFile.setEnabled(false);

		if (entry != null) {
			breadCrumb.reloadPath(entry.override());

			String extension = entry.extension();

			if (extension.isBlank() || entry.type() == ResourcePackStructure.EntryType.VANILLA) {
				importFile.setEnabled(true);
			}

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

			if (extension.equals("png")) {
				Image image = ZipIO.readFileInZip(resourcePack.packFile(), entry.fullPath(), (file, zipEntry) -> {
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
				String original = ZipIO.readCodeInZip(resourcePack.packFile(), entry.fullPath());
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
		ResourcePackStructure.Entry selectedEntry = this.selectedEntry;

		FilterTreeNode root = new FilterTreeNode("");

		FileTree<ResourcePackStructure.Entry> fileTree = new FileTree<>(new FileNode<>("", ""));
		ResourcePackStructure.getResourcePackStructure(workspace, resourcePack.namespace(), resourcePack.packFile())
				.forEach(entry -> fileTree.addElement(entry.path(), entry));
		JFileTree.addFileNodeToRoot(root, fileTree.root());

		model.setRoot(root);
		model.refilter();

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

			if (selectedEntry != null) {
				AtomicReference<ResourcePackStructure.Entry> newEntry = new AtomicReference<>(null);
				TreeUtils.selectNodeByUserObject(tree, entry -> {
					if (entry.getObject() instanceof ResourcePackStructure.Entry selectedEntry2) {
						boolean match = selectedEntry2.path().equals(selectedEntry.path());
						if (match) {
							newEntry.set(selectedEntry2);
						}
						return match;
					}
					return false;
				}, FileNode.class);
				setSelectedEntry(newEntry.get());
			}
		}
	}

	@Override public void refilterElements() {
		if (filterProvider != null) {
			String filter = filterProvider.get();
			if (filter.length() >= 3) {
				model.setFilter(filter);
				SwingUtilities.invokeLater(() -> TreeUtils.expandAllNodes(tree, 0, tree.getRowCount()));
			} else {
				model.setFilter("");
			}
		}
	}

	public void importExternalFile(File file) {
		if (selectedEntry != null) {
			if (selectedEntry.isFolder()) { // Importing files into a folder
				File importTargetFolder = selectedEntry.override();
				FileIO.copyFile(file, new File(importTargetFolder, file.getName()));
				reloadElements();
			} else { // Importing a file to override existing file
				// Make sure extensions match, otherwise reject import
				if (!selectedEntry.extension().equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}

				File importTarget = selectedEntry.override();
				FileIO.copyFile(file, importTarget);
				reloadElements();
			}
		}
	}

}
