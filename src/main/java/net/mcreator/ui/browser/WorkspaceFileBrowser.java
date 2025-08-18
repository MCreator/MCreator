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

package net.mcreator.ui.browser;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.io.tree.FileNode;
import net.mcreator.io.tree.FileTree;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.minecraft.MinecraftFolderUtils;
import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.tree.FilterTreeNode;
import net.mcreator.ui.component.tree.FilteredTreeModel;
import net.mcreator.ui.component.tree.JFileTree;
import net.mcreator.ui.component.tree.SerializableTreeExpansionState;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.FilenameUtilsPatched;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Workspace file browser is used in a workspace window by the user to view source files of the project contained in
 * the workspace and also observe source code of external libraries used by that project.
 */
public class WorkspaceFileBrowser extends JPanel {

	private final FilteredTreeModel mods = new FilteredTreeModel(null);

	FilterTreeNode sourceCode = null;
	FilterTreeNode currRes = null;

	public final JFileTree tree = new JFileTree(mods);

	private final JTextField jtf1 = new JTextField() {
		@Override public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.clearRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
			g.setColor(new Color(111, 111, 111));
			g.setFont(getFont().deriveFont(10f));
			if (getText().isBlank())
				g.drawString(L10N.t("workspace_file_browser.search"), 2, 15);
		}
	};

	final MCreator mcreator;

	/**
	 * The sole constructor.
	 *
	 * @param mcreator Workspace window that is the future owner of this browser instance.
	 */
	public WorkspaceFileBrowser(MCreator mcreator) {
		setLayout(new BorderLayout(0, 0));
		this.mcreator = mcreator;

		tree.setCellRenderer(new ProjectBrowserCellRenderer(mcreator));

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBorder(BorderFactory.createLineBorder((Theme.current().getBackgroundColor()).brighter()));
		jtf1.setBackground(Theme.current().getBackgroundColor());
		jtf1.setForeground(new Color(0xCBCBCB));
		jtf1.setOpaque(true);
		ComponentUtils.deriveFont(jtf1, 12);

		jtf1.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void insertUpdate(DocumentEvent e) {
				updateSearch(true);
			}

			@Override public void removeUpdate(DocumentEvent e) {
				updateSearch(true);
			}

			@Override public void changedUpdate(DocumentEvent e) {
				updateSearch(true);
			}
		});

		jtf1.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(Theme.current().getBackgroundColor());
		bar.add(jtf1);
		bar.setBorder(BorderFactory.createMatteBorder(3, 5, 3, 0, Theme.current().getBackgroundColor()));

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topBar.setBackground(Theme.current().getAltBackgroundColor());
		topBar.add(
				ComponentUtils.setForeground(ComponentUtils.deriveFont(L10N.label("workspace_file_browser.title"), 10f),
						Theme.current().getAltForegroundColor()));

		topBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current().getBackgroundColor()),
				BorderFactory.createEmptyBorder(2, 5, 2, 0)));

		JLabel sil = new JLabel(UIRES.get("16px.search"));
		sil.setPreferredSize(new Dimension(sil.getIcon().getIconWidth(), sil.getIcon().getIconHeight()));

		JComponent search = PanelUtils.westAndCenterElement(sil, bar);
		search.setBackground(Theme.current().getBackgroundColor());
		search.setOpaque(true);
		search.setBorder(BorderFactory.createEmptyBorder(3, 4, 0, 3));

		add("North", topBar);

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Theme.current().getBackgroundColor()));
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
		add("Center", PanelUtils.northAndCenterElement(search, jsp));

		tree.addMouseListener(new MouseAdapter() {

			long lastUpdate = 0;

			@Override public void mouseEntered(MouseEvent mouseEvent) {
				super.mouseEntered(mouseEvent);
				if (System.currentTimeMillis() - lastUpdate > 5000) {
					reloadTree();
					lastUpdate = System.currentTimeMillis();
				}
			}

			@Override public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2)
					openSelectedFile(false);
				else if (mouseEvent.getButton() == MouseEvent.BUTTON3 && tree.getLastSelectedPathComponent() != null)
					new WorkspaceFileBrowserContextMenu(WorkspaceFileBrowser.this).show(tree, mouseEvent.getX(),
							mouseEvent.getY());
			}

		});

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			@Override public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
				mcreator.getWorkspaceUserSettings().projectBrowserState = SerializableTreeExpansionState.fromTree(tree);
			}

			@Override public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
				mcreator.getWorkspaceUserSettings().projectBrowserState = SerializableTreeExpansionState.fromTree(tree);
			}
		});
	}

	private boolean initial = true;

	/**
	 * Reloads all the project files.
	 */
	public synchronized void reloadTree() {
		// Only reload the tree if we are visible in the UI
		if (!isShowing())
			return;

		List<DefaultMutableTreeNode> state = TreeUtils.getExpansionState(tree);

		FilterTreeNode root = new FilterTreeNode("");
		FilterTreeNode node = new FilterTreeNode(mcreator.getWorkspaceSettings().getModName());

		sourceCode = new FilterTreeNode("Source (Gradle)");
		JFileTree.addNodes(sourceCode, mcreator.getGenerator().getSourceRoot(), true);
		node.add(sourceCode);

		currRes = new FilterTreeNode("Resources (Gradle)");
		JFileTree.addNodes(currRes, mcreator.getGenerator().getResourceRoot(), true);
		node.add(currRes);

		if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("sounds") != GeneratorStats.CoverageStatus.NONE) {
			FilterTreeNode sounds = new FilterTreeNode("Sounds");
			JFileTree.addNodes(sounds, mcreator.getFolderManager().getSoundsDir(), true);
			node.add(sounds);
		}

		if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("structures")
				!= GeneratorStats.CoverageStatus.NONE) {
			FilterTreeNode structures = new FilterTreeNode("Structures");
			JFileTree.addNodes(structures, mcreator.getFolderManager().getStructuresDir(), true);
			node.add(structures);
		}

		if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_json") != GeneratorStats.CoverageStatus.NONE
				|| mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_java")
				!= GeneratorStats.CoverageStatus.NONE
				|| mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_obj")
				!= GeneratorStats.CoverageStatus.NONE) {
			FilterTreeNode models = new FilterTreeNode("Models");
			JFileTree.addNodes(models, mcreator.getFolderManager().getModelsDir(), true);
			node.add(models);
		}

		if (new File(mcreator.getFolderManager().getClientRunDir(), "debug").isDirectory()) {
			FilterTreeNode debugFolder = new FilterTreeNode("Debug profiler results");
			JFileTree.addNodes(debugFolder, new File(mcreator.getFolderManager().getClientRunDir(), "debug"), true);
			node.add(debugFolder);
		}

		File[] rootFiles = mcreator.getWorkspaceFolder().listFiles();
		for (File file : rootFiles != null ? rootFiles : new File[0]) {
			if (file.isFile() && !file.isHidden() && !file.getName().startsWith("."))
				if (!file.getName().startsWith("gradlew") && !file.getName().endsWith(".mcreator"))
					node.add(new FilterTreeNode(file));
		}

		root.add(node);

		File clientRunDir = mcreator.getFolderManager().getClientRunDir();
		File serverRunDir = mcreator.getFolderManager().getServerRunDir();
		if (clientRunDir.equals(serverRunDir)) {
			if (clientRunDir.isDirectory()) {
				FilterTreeNode minecraft = new FilterTreeNode("Minecraft run folder");
				JFileTree.addNodes(minecraft, clientRunDir, true);
				root.add(minecraft);
			}
		} else {
			if (clientRunDir.isDirectory()) {
				FilterTreeNode minecraft = new FilterTreeNode("MC client run folder");
				JFileTree.addNodes(minecraft, clientRunDir, true);
				root.add(minecraft);
			}
			if (serverRunDir.isDirectory()) {
				FilterTreeNode minecraft = new FilterTreeNode("MC server run folder");
				JFileTree.addNodes(minecraft, serverRunDir, true);
				root.add(minecraft);
			}
		}

		if (mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA)
			loadExtSources(root);

		if (mcreator.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON
				&& MinecraftFolderUtils.getBedrockEditionFolder() != null) {
			FilterTreeNode minecraft = new FilterTreeNode("Bedrock Edition");
			JFileTree.addNodes(minecraft, MinecraftFolderUtils.getBedrockEditionFolder(), true);
			root.add(minecraft);
		}

		mods.setRoot(root);

		if (initial) {
			SerializableTreeExpansionState expansionState = mcreator.getWorkspaceUserSettings().projectBrowserState;
			if (expansionState != null)
				expansionState.applyToTree(tree);
			else
				tree.expandPath(new TreePath(new Object[] { root, node }));
			initial = false;
		} else {
			TreeUtils.setExpansionState(tree, state);
		}

		updateSearch(false);
	}

	private List<DefaultMutableTreeNode> preSearchState = null;

	private synchronized void updateSearch(boolean clearFilter) {
		if (jtf1.getText().trim().length() >= 3) {
			if (preSearchState == null)
				preSearchState = TreeUtils.getExpansionState(tree);

			mods.setFilter(jtf1.getText().trim());
			TreeUtils.expandAllNodes(tree, 0, tree.getRowCount());
		} else if (clearFilter) {
			mods.setFilter("");
			if (preSearchState != null) {
				TreeUtils.setExpansionState(tree, preSearchState);
				preSearchState = null;
			}
		}
	}

	/**
	 * If a file is selected, opens this file in the built-in code editor if its type is supported, otherwise calls the
	 * program assigned to that file type.
	 *
	 * @param forceExpansion If selected node represents a directory and is expanded, value of <i>{@code true}</i>
	 *                       will keep it open and value of <i>{@code false}</i> will let it collapse.
	 */
	public void openSelectedFile(boolean forceExpansion) {
		if (tree.getLastSelectedPathComponent() != null) {
			FilterTreeNode selection = (FilterTreeNode) tree.getLastSelectedPathComponent();
			if (selection.getUserObject() instanceof File selFile) {
				if (selFile.isDirectory() && forceExpansion)
					tree.expandPath(tree.getSelectionPath());
				else
					FileOpener.openFile(mcreator, selFile);
			} else if (!selection.isLeaf() && forceExpansion) {
				tree.expandPath(tree.getSelectionPath());
			} else {
				FileOpener.openFile(mcreator, selection.getUserObject());
			}
		}
	}

	/**
	 * If a file is selected, opens this file using the program assigned to that file type.
	 */
	public void openSelectedFileInDesktop() {
		if (tree.getLastSelectedPathComponent() != null) {
			FilterTreeNode selection = (FilterTreeNode) tree.getLastSelectedPathComponent();
			if (selection.getUserObject() instanceof File selectedFile) {
				if (Files.isRegularFile(selectedFile.toPath()) || Files.isDirectory(selectedFile.toPath()))
					DesktopUtils.openSafe(selectedFile);
				else
					Toolkit.getDefaultToolkit().beep();
			} else if (selection.getUserObject() instanceof String selectedObject) {
				if (selectedObject.equals("Source (Gradle)"))
					DesktopUtils.openSafe(mcreator.getGenerator().getSourceRoot());
				else if (selectedObject.equals("Resources (Gradle)"))
					DesktopUtils.openSafe(mcreator.getGenerator().getResourceRoot());
				else
					Toolkit.getDefaultToolkit().beep();
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	/**
	 * If a file is selected, opens that file's parent folder via OS native file explorer and highlights the file.
	 */
	public void showSelectedFileInDesktop() {
		if (tree.getLastSelectedPathComponent() != null) {
			FilterTreeNode selection = (FilterTreeNode) tree.getLastSelectedPathComponent();
			if (selection.getUserObject() instanceof File sel)
				DesktopUtils.openSafe(sel, true);
			else
				Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 * If a file is selected, attempts to remove it from the file system.
	 */
	public void deleteSelectedFile() {
		FilterTreeNode selected = (FilterTreeNode) tree.getLastSelectedPathComponent();
		if (selected != null && selected != sourceCode && selected != currRes) {
			if (selected.getUserObject() instanceof File file) {
				int n = JOptionPane.showConfirmDialog(mcreator, L10N.t("workspace_file_browser.remove_file.message"),
						L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (n == 0) {
					if (file.isFile())
						file.delete();
					else
						FileIO.deleteDir(file);
					mods.removeNodeFromParent(selected);
				}
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} else if (selected == sourceCode || selected == currRes) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private void loadExtSources(FilterTreeNode node) {
		FilterTreeNode extDeps = new FilterTreeNode("External libraries");

		if (mcreator.getGenerator().getProjectJarManager() != null) {
			List<LibraryInfo> libraryInfos = mcreator.getGenerator().getProjectJarManager().getClassFileSources();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile() && (ZipIO.checkIfZip(libraryFile) || ZipIO.checkIfJMod(libraryFile))) {
					String libName = FilenameUtilsPatched.removeExtension(libraryFile.getName());
					if (libName.equals("rt") || libName.equals("java.base"))
						libName = "Java " + System.getProperty("java.version") + " SDK";
					else
						libName = "Gradle: " + libName;

					if (libraryInfo.getSourceLocation() != null) {
						File sourceFile = new File(libraryInfo.getSourceLocation().getLocationAsString());
						if (sourceFile.isFile() && (ZipIO.checkIfZip(sourceFile) || ZipIO.checkIfJMod(sourceFile))) {
							FileTree<Void> libsrc = new FileTree<>(
									new FileNode<>(libName, sourceFile.getAbsolutePath() + ":%:"));
							ZipIO.iterateZip(sourceFile, entry -> libsrc.addElement(entry.getName()), true);
							JFileTree.addFileNodeToFilterTreeNode(extDeps, libsrc.root());
							continue;
						}
					}

					// If source file is not found, add the library file itself
					FileTree<Void> lib = new FileTree<>(new FileNode<>(libName, libraryFile.getAbsolutePath() + ":%:"));
					ZipIO.iterateZip(libraryFile, entry -> lib.addElement(entry.getName()), true);
					JFileTree.addFileNodeToFilterTreeNode(extDeps, lib.root());
				}
			}
		}

		node.add(extDeps);
	}

}
