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
import net.mcreator.ui.component.util.WrapLayout;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.SlickTreeUI;
import org.apache.commons.io.FilenameUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class WorkspaceFileBrowser extends JPanel {

	private final FilteredTreeModel mods = new FilteredTreeModel(null);

	FilterTreeNode sourceCode = null;

	public JTree tree = new JTree(mods) {
		@Override public void paintComponent(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			if (getSelectionCount() > 0) {
				g.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
				for (int i : Objects.requireNonNull(getSelectionRows())) {
					Rectangle r = getRowBounds(i);
					g.fillRect(0, r.y, getWidth(), r.height);
				}
			}
			super.paintComponent(g);
		}
	};
	private final JTextField jtf1 = new JTextField() {
		@Override public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.clearRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
			g.setColor(new Color(111, 111, 111));
			g.setFont(getFont().deriveFont(10f));
			if (getText().trim().equals(""))
				g.drawString("Search by file name", 2, 17);
		}
	};

	final MCreator mcreator;

	public WorkspaceFileBrowser(MCreator mcreator) {
		setLayout(new BorderLayout(0, 0));
		this.mcreator = mcreator;

		tree.setCellRenderer(new ProjectBrowserCellRenderer());
		tree.setRowHeight(18);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setOpaque(false);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		JScrollPane jsp = new JScrollPane(tree);
		jsp.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		SlickTreeUI treeUI = new SlickTreeUI();
		tree.setUI(treeUI);

		treeUI.setRightChildIndent(11);
		treeUI.setLeftChildIndent(3);

		jsp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"), jsp.getVerticalScrollBar()));
		jsp.getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));
		jsp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));

		JPanel cornerDummy1 = new JPanel();
		cornerDummy1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		jsp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cornerDummy1);

		JPanel cornerDummy2 = new JPanel();
		cornerDummy2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		jsp.setCorner(JScrollPane.LOWER_LEFT_CORNER, cornerDummy2);

		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		jsp.setBorder(BorderFactory.createMatteBorder(5, 5, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		add("Center", jsp);

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBorder(BorderFactory.createLineBorder(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter()));
		jtf1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
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
						}).start();
					}
				} else {
					mods.setFilter("");
				}
			}
		});

		JPanel tools = new JPanel(new WrapLayout(FlowLayout.LEFT, 0, 4));

		JButton create = L10N.button("workspace_file_browser.add");
		create.setIcon(UIRES.get("16px.add.gif"));
		create.setContentAreaFilled(false);
		create.setOpaque(false);
		create.setForeground(Color.white);
		ComponentUtils.deriveFont(create, 11);
		create.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
		tools.add(create);

		JButton delete = new JButton(UIRES.get("16px.delete.gif"));
		delete.setContentAreaFilled(false);
		delete.setOpaque(false);
		delete.setForeground(Color.white);
		delete.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
		tools.add(delete);

		tools.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));

		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		bar.add(jtf1);
		bar.setBorder(BorderFactory.createMatteBorder(0, 5, 6, 5, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topBar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		topBar.add(ComponentUtils
				.setForeground(ComponentUtils.deriveFont(L10N.label("workspace_file_browser.title"), 10f),
						(Color) UIManager.get("MCreatorLAF.GRAY_COLOR")));

		topBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")),
				BorderFactory.createEmptyBorder(2, 5, 2, 0)));

		add("North", PanelUtils.northAndCenterElement(topBar, tools));
		add("South", bar);

		create.addActionListener(e -> new AddFileDropdown(this).show(create, 0, 20));

		delete.addActionListener(e -> {
			FilterTreeNode selected = (FilterTreeNode) tree.getLastSelectedPathComponent();
			if (selected != null) {
				if (selected.getUserObject() instanceof File) {
					int n = JOptionPane.showConfirmDialog(mcreator, L10N.t("workspace_file_browser.remove_file"),
							L10N.t("workspace_file_browser.remove_file.title"), JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (n == 0) {
						File file = (File) selected.getUserObject();
						if (file.isFile())
							file.delete();
						else
							FileIO.deleteDir(file);
						mods.removeNodeFromParent(selected);
					}
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		});

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
				if (mouseEvent.getClickCount() == 2) {
					if (tree.getLastSelectedPathComponent() != null) {
						Object selection = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent())
								.getUserObject();
						FileOpener.openFile(mcreator, selection);
					}
				}
			}

		});
	}

	private boolean initial = true;

	public void reloadTree() {
		if (jtf1.getText().isEmpty()) {
			List<DefaultMutableTreeNode> state = TreeUtils.getExpansionState(tree);

			FilterTreeNode root = new FilterTreeNode("");
			FilterTreeNode node = new FilterTreeNode(mcreator.getWorkspaceSettings().getModName());

			sourceCode = new FilterTreeNode("Source (Gradle)");
			addNodes(sourceCode, mcreator.getGenerator().getSourceRoot(), true);
			node.add(sourceCode);

			FilterTreeNode currRes = new FilterTreeNode("Resources (Gradle)");
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

			if (new File(mcreator.getWorkspaceFolder(), "run/debug").isDirectory()) {
				FilterTreeNode debugFolder = new FilterTreeNode("Debug profiler results");
				addNodes(debugFolder, new File(mcreator.getWorkspaceFolder(), "run/debug"), true);
				node.add(debugFolder);
			}

			File[] rootFiles = mcreator.getWorkspaceFolder().listFiles();
			for (File file : rootFiles != null ? rootFiles : new File[0]) {
				if (file.isFile() && !file.isHidden() && !file.getName().startsWith("."))
					if (!file.getName().startsWith("gradlew") && !file.getName().endsWith(".mcreator"))
						node.add(new FilterTreeNode(file));
			}

			root.add(node);

			if (new File(mcreator.getWorkspaceFolder(), "run/").isDirectory()) {
				FilterTreeNode minecraft = new FilterTreeNode("Minecraft run folder");
				addNodes(minecraft, new File(mcreator.getWorkspaceFolder(), "run/"), true);
				root.add(minecraft);
			}

			if (mcreator.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA)
				loadExtSoruces(root);

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

	private void loadExtSoruces(FilterTreeNode node) {
		FilterTreeNode extDeps = new FilterTreeNode("External libraries");

		if (mcreator.getGenerator().getProjectJarManager() != null) {
			List<LibraryInfo> libraryInfos = mcreator.getGenerator().getProjectJarManager().getClassFileSources();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile() && ZipIO.checkIfZip(libraryFile)) {
					String libName = FilenameUtils.removeExtension(libraryFile.getName());
					if (libName.equals("rt"))
						libName = "Java " + System.getProperty("java.version") + " SDK";
					else
						libName = "Gradle: " + libName;
					if (libraryInfo.getSourceLocation() != null) {
						File sourceFile = new File(libraryInfo.getSourceLocation().getLocationAsString());
						FileTree libsrc = new FileTree(new FileNode(libName, sourceFile.getAbsolutePath() + ":%:"));
						ZipIO.iterateZip(sourceFile, (entry) -> libsrc.addElement(entry.getName()));
						addFileNodeToFilterTreeNode(extDeps, libsrc.root);
					} else {
						FileTree lib = new FileTree(new FileNode(libName, libraryFile.getAbsolutePath() + ":%:"));
						ZipIO.iterateZip(libraryFile, (entry) -> lib.addElement(entry.getName()));
						addFileNodeToFilterTreeNode(extDeps, lib.root);
					}
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

		ProjectBrowserCellRenderer() {
			setBorderSelectionColor(null);
			setBackgroundSelectionColor(null);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			FilterTreeNode node = (FilterTreeNode) value;
			setOpaque(false);
			JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			a.setOpaque(true);
			ComponentUtils.deriveFont(a, 11);

			if (node.getUserObject() instanceof String) {
				String tsi = (String) node.getUserObject();
				a.setText(tsi);
				if (tsi.equals(mcreator.getWorkspaceSettings().getModName()))
					a.setIcon(UIRES.get("16px.package.gif"));
				else if (tsi.equals("Source (Gradle)"))
					a.setIcon(UIRES.get("16px.mod.png"));
				else if (tsi.equals("Textures"))
					a.setIcon(UIRES.get("16px.textures.png"));
				else if (tsi.equals("Resources (Gradle)"))
					a.setIcon(UIRES.get("16px.resources.png"));
				else if (tsi.equals("Models"))
					a.setIcon(UIRES.get("16px.models.png"));
				else if (tsi.equals("Minecraft run folder") || tsi.equals("Bedrock Edition"))
					a.setIcon(UIRES.get("16px.minecraft.png"));
				else if (tsi.equals("Sounds"))
					a.setIcon(UIRES.get("16px.music.png"));
				else if (tsi.equals("External libraries"))
					a.setIcon(UIRES.get("16px.directory.gif"));
				else if (tsi.equals("Structures"))
					a.setIcon(UIRES.get("16px.structures.png"));
			} else if (node.getUserObject() instanceof FileNode) {
				FileNode fileNode = (FileNode) node.getUserObject();
				a.setText(fileNode.data);
				if (fileNode.data.endsWith(".java"))
					a.setIcon(UIRES.get("16px.classro.gif"));
				else if (fileNode.data.startsWith("Gradle: "))
					a.setIcon(UIRES.get("16px.ext.gif"));
				else if (fileNode.data.startsWith("Java "))
					a.setIcon(UIRES.get("16px.directory.gif"));
				else
					a.setIcon(FileIcons.getIconForFile(fileNode.data));
			} else if (node.getUserObject() instanceof File) {
				File fil = (File) node.getUserObject();
				a.setText(fil.getName());
				if (!fil.isDirectory())
					a.setIcon(FileIcons.getIconForFile(fil));
				else
					a.setIcon(UIRES.get("laf.directory.gif"));
			}

			if (node.getFilter() != null && !node.getFilter().equals("")) {
				a.setText("<html>" + getText().replace(node.getFilter(), "<b>" + node.getFilter() + "</b>"));
			}

			if (sel) {
				a.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				a.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			} else {
				a.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				a.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			}
			return a;
		}
	}

}
