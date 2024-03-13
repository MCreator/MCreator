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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.FilenameUtilsPatched;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;

/**
 * Workspace file browser is used in a workspace window by the user to view source files of the project contained in
 * the workspace and also observe source code of external libraries used by that project.
 */
public class WorkspaceFileBrowser extends JPanel {

	private final FilteredTreeModel mods = new FilteredTreeModel(null);

	FilterTreeNode sourceCode = null;
	FilterTreeNode currRes = null;

	public final JTree tree = new JTree(mods);
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

		tree.setCellRenderer(new ProjectBrowserCellRenderer());
		tree.setRowHeight(18);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setOpaque(false);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setBackground(Theme.current().getBackgroundColor());

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, Theme.current().getBackgroundColor()));
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBorder(BorderFactory.createLineBorder((Theme.current().getBackgroundColor()).brighter()));
		jtf1.setBackground(Theme.current().getBackgroundColor());
		jtf1.setForeground(new Color(0xCBCBCB));
		jtf1.setOpaque(true);
		ComponentUtils.deriveFont(jtf1, 12);

		jtf1.addKeyListener(new KeyAdapter() {
			boolean searchInAction = false;

			@Override public void keyReleased(KeyEvent keyEvent) {
				super.keyReleased(keyEvent);
				if (jtf1.getText().trim().length() > 3) {
					if (!searchInAction && Character.isLetterOrDigit(keyEvent.getKeyChar())) {
						new Thread(() -> {
							searchInAction = true;
							mods.setFilter(jtf1.getText().trim());
							SwingUtilities.invokeLater(() -> TreeUtils.expandAllNodes(tree, 0, tree.getRowCount()));
							searchInAction = false;
						}, "ReferenceSearch").start();
					}
				} else {
					mods.setFilter("");
				}
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
	}

	private boolean initial = true;

	/**
	 * Reloads all the project files.
	 */
	public synchronized void reloadTree() {
		if (jtf1.getText().isEmpty()) {
			List<DefaultMutableTreeNode> state = TreeUtils.getExpansionState(tree);

			FilterTreeNode root = new FilterTreeNode("");
			FilterTreeNode node = new FilterTreeNode(mcreator.getWorkspaceSettings().getModName());

			sourceCode = new FilterTreeNode("Source (Gradle)");
			addNodes(sourceCode, mcreator.getGenerator().getSourceRoot(), true);
			node.add(sourceCode);

			currRes = new FilterTreeNode("Resources (Gradle)");
			addNodes(currRes, mcreator.getGenerator().getResourceRoot(), true);
			node.add(currRes);

			if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("sounds")
					!= GeneratorStats.CoverageStatus.NONE) {
				FilterTreeNode sounds = new FilterTreeNode("Sounds");
				addNodes(sounds, mcreator.getFolderManager().getSoundsDir(), true);
				node.add(sounds);
			}

			if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("structures")
					!= GeneratorStats.CoverageStatus.NONE) {
				FilterTreeNode structures = new FilterTreeNode("Structures");
				addNodes(structures, mcreator.getFolderManager().getStructuresDir(), true);
				node.add(structures);
			}

			if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_json")
					!= GeneratorStats.CoverageStatus.NONE
					|| mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_java")
					!= GeneratorStats.CoverageStatus.NONE
					|| mcreator.getGeneratorStats().getBaseCoverageInfo().get("model_obj")
					!= GeneratorStats.CoverageStatus.NONE) {
				FilterTreeNode models = new FilterTreeNode("Models");
				addNodes(models, mcreator.getFolderManager().getModelsDir(), true);
				node.add(models);
			}

			if (new File(mcreator.getFolderManager().getClientRunDir(), "debug").isDirectory()) {
				FilterTreeNode debugFolder = new FilterTreeNode("Debug profiler results");
				addNodes(debugFolder, new File(mcreator.getFolderManager().getClientRunDir(), "debug"), true);
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
					addNodes(minecraft, clientRunDir, true);
					root.add(minecraft);
				}
			} else {
				if (clientRunDir.isDirectory()) {
					FilterTreeNode minecraft = new FilterTreeNode("MC client run folder");
					addNodes(minecraft, clientRunDir, true);
					root.add(minecraft);
				}
				if (serverRunDir.isDirectory()) {
					FilterTreeNode minecraft = new FilterTreeNode("MC server run folder");
					addNodes(minecraft, serverRunDir, true);
					root.add(minecraft);
				}
			}

			if (mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA)
				loadExtSources(root);

			if (mcreator.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON
					&& MinecraftFolderUtils.getBedrockEditionFolder() != null) {
				FilterTreeNode minecraft = new FilterTreeNode("Bedrock Edition");
				addNodes(minecraft, MinecraftFolderUtils.getBedrockEditionFolder(), true);
				root.add(minecraft);
			}

			mods.setRoot(root);

			if (initial) {
				tree.expandPath(new TreePath(new Object[] { root, node }));
				initial = false;
			} else {
				TreeUtils.setExpansionState(tree, state);
			}
		}
	}

	/**
	 * If a file is selected, opens this file in built-in code editor if its type is supported, otherwise calls the
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
							FileTree libsrc = new FileTree(new FileNode(libName, sourceFile.getAbsolutePath() + ":%:"));
							ZipIO.iterateZip(sourceFile, entry -> libsrc.addElement(entry.getName()), true);
							addFileNodeToFilterTreeNode(extDeps, libsrc.root);
							continue;
						}
					}

					// If source file is not found, add the library file itself
					FileTree lib = new FileTree(new FileNode(libName, libraryFile.getAbsolutePath() + ":%:"));
					ZipIO.iterateZip(libraryFile, entry -> lib.addElement(entry.getName()), true);
					addFileNodeToFilterTreeNode(extDeps, lib.root);
				}
			}
		}

		node.add(extDeps);
	}

	private void addFileNodeToFilterTreeNode(FilterTreeNode node, FileNode root) {
		FilterTreeNode treeNode = new FilterTreeNode(root);
		node.add(treeNode);
		for (FileNode child : root.childs)
			addFileNodeToFilterTreeNode(treeNode, child);
		for (FileNode child : root.leafs)
			addFileNodeToFilterTreeNode(treeNode, child);

	}

	private void addNodes(FilterTreeNode curTop, File dir, boolean first) {
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

	private class ProjectBrowserCellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			ComponentUtils.deriveFont(a, 11);

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
			} else if (node.getUserObject() instanceof FileNode fileNode) {
				a.setText(fileNode.data);
				if (fileNode.data.endsWith(".java"))
					a.setIcon(UIRES.get("16px.classro"));
				else if (fileNode.data.startsWith("Gradle: "))
					a.setIcon(UIRES.get("16px.ext"));
				else if (fileNode.data.startsWith("Java "))
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

}
