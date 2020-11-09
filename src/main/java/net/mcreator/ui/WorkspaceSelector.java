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

package net.mcreator.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.net.WebIO;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SocialButtons;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.AbstractMCreatorTheme;
import net.mcreator.ui.vcs.VCSSetupDialogs;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.vcs.CloneWorkspace;
import net.mcreator.vcs.VCSInfo;
import net.mcreator.workspace.ShareableZIPManager;
import net.mcreator.workspace.WorkspaceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorkspaceSelector extends JFrame implements DropTargetListener {

	private static final Logger LOG = LogManager.getLogger("Workspace Selector");

	private final JPanel recentPanel = new JPanel(new GridLayout());
	private final WorkspaceOpenListener workspaceOpenListener;
	RecentWorkspaces recentWorkspaces = new RecentWorkspaces();

	WorkspaceSelector(MCreatorApplication application, WorkspaceOpenListener workspaceOpenListener) {
		this.workspaceOpenListener = workspaceOpenListener;

		reloadTitle();
		setIconImage(UIRES.get("icon").getImage());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent arg0) {
				application.closeApplication();
			}
		});

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		addWorkspaceButton(L10N.t("dialog.workspace_selector.new_workspace"), UIRES.get("addwrk"), e -> {
			NewWorkspaceDialog newWorkspaceDialog = new NewWorkspaceDialog(this);
			if (newWorkspaceDialog.getWorkspaceFile() != null)
				workspaceOpenListener.workspaceOpened(newWorkspaceDialog.getWorkspaceFile());
		}, actions);

		addWorkspaceButton(L10N.t("dialog.workspace_selector.open_workspace"), UIRES.get("opnwrk"), e -> {
			File workspaceFile = FileDialogs.getOpenDialog(this, new String[] { ".mcreator" });
			if (workspaceFile != null && workspaceFile.getParentFile().isDirectory())
				workspaceOpenListener.workspaceOpened(workspaceFile);
		}, actions);

		addWorkspaceButton(L10N.t("dialog.workspace_selector.import"), UIRES.get("impfile"), e -> {
			File file = FileDialogs.getOpenDialog(this, new String[] { ".zip" });
			if (file != null) {
				File workspaceDir = FileDialogs.getWorkspaceDirectorySelectDialog(this, null);
				if (workspaceDir != null) {
					File workspaceFile = ShareableZIPManager.importZIP(file, workspaceDir, this);
					if (workspaceFile != null)
						workspaceOpenListener.workspaceOpened(workspaceFile);
				}
			}
		}, actions);

		addWorkspaceButton(L10N.t("dialog.workspace_selector.clone"), UIRES.get("vcsclone"), e -> {
			VCSInfo vcsInfo = VCSSetupDialogs.getVCSInfoDialog(this,
					"<html>Please enter the URL of the repository/remote workspace you would like to clone below. In order to be able<br>"
							+ "to sync back to the remote workspace, you need to be have proper permissions on the remote repository for<br>"
							+ "the Git account you will enter here. Note that Git account is not your MCreator account.<br>");
			if (vcsInfo != null) {
				File workspaceFolder = FileDialogs.getWorkspaceDirectorySelectDialog(this, null);
				if (workspaceFolder != null) {
					try {
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						CloneWorkspace.cloneWorkspace(this, vcsInfo, workspaceFolder);
						try {
							File workspaceFile = WorkspaceUtils.getWorkspaceFileForWorkspaceFolder(workspaceFolder);
							workspaceOpenListener.workspaceOpened(workspaceFile);
						} catch (Exception ex) {
							throw new Exception("The remote repository is not a MCreator workspace or is corrupted");
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this,
								"<html>Failed to setup remote workspace. Reason:<br><b>" + ex.getMessage(),
								"Remote workspace setup failed", JOptionPane.ERROR_MESSAGE);
					}
					setCursor(Cursor.getDefaultCursor());
				}
			}
		}, actions);

		JPanel logoPanel = new JPanel(new BorderLayout());
		JLabel logo = new JLabel(new ImageIcon(ImageUtils.resizeAA(UIRES.get("logo").getImage(), 250, 45)));
		logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logo.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN);
			}
		});
		logoPanel.add("North", PanelUtils.join(FlowLayout.LEFT, logo));
		JLabel version = new JLabel("  Version " + Launcher.version.getMajorString());
		version.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				AboutAction.showDialog(WorkspaceSelector.this);
			}
		});
		version.setHorizontalTextPosition(SwingConstants.LEFT);
		version.setIcon(UIRES.get("info"));
		version.setCursor(new Cursor(Cursor.HAND_CURSOR));

		ComponentUtils.deriveFont(version, 18);
		version.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		SocialButtons socialButtons = new SocialButtons();
		socialButtons.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 0));
		logoPanel.add("Center", socialButtons);
		logoPanel.add("South", version);

		logoPanel.setBorder(BorderFactory.createEmptyBorder(45, 26 + 25, 0, 10));
		actions.setBorder(BorderFactory.createEmptyBorder(25, 24 + 25, 2, 10));

		JPanel southcenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southcenter.setBorder(BorderFactory.createEmptyBorder(0, 0, 26, 60 - 1));

		southcenter.add(new JEmptyBox(5, 5));

		JLabel prefs = L10N.label("dialog.workspace_selector.preferences");
		prefs.setIcon(UIRES.get("settings"));
		prefs.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.deriveFont(prefs, 13);
		prefs.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		prefs.setBorder(BorderFactory.createEmptyBorder());
		prefs.setHorizontalTextPosition(JLabel.LEFT);
		prefs.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				new PreferencesDialog(WorkspaceSelector.this, null);
			}
		});
		southcenter.add(prefs);

		add("Center",
				PanelUtils.centerAndSouthElement(PanelUtils.northAndCenterElement(logoPanel, actions), southcenter));

		recentPanel.setBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));
		recentPanel.setPreferredSize(new Dimension(220, 10));

		initWebsitePanel();

		add("West", recentPanel);

		new DropTarget(this, DnDConstants.ACTION_MOVE, this, true, null);

		setSize(790, 460);
		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void reloadTitle() {
		setTitle("MCToolkit " + Launcher.version.getMajorString());
	}

	@Override public void dragEnter(DropTargetDragEvent dtde) {
		processDrag(dtde);
	}

	@Override public void dragOver(DropTargetDragEvent dtde) {
		processDrag(dtde);
	}

	@Override public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	@Override public void dragExit(DropTargetEvent dtde) {

	}

	@Override public void drop(DropTargetDropEvent dtde) {
		Transferable transferable = dtde.getTransferable();
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrop(dtde.getDropAction());
			try {
				List<?> transferData = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (transferData.size() > 0) {
					Object transfObj = transferData.get(0);
					if (transfObj instanceof File) {
						File workspaceFile = (File) transfObj;
						if (workspaceFile.getName().endsWith(".mcreator")) {
							workspaceOpenListener.workspaceOpened(workspaceFile);
						} else {
							Toolkit.getDefaultToolkit().beep();
						}
					}
				}
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		} else {
			dtde.rejectDrop();
		}
	}

	private void processDrag(DropTargetDragEvent dtde) {
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		} else {
			dtde.rejectDrag();
		}
	}

	public void addRecentWorkspace(RecentWorkspaceEntry recentWorkspaceEntry) {
		if (!recentWorkspaces.list.contains(recentWorkspaceEntry))
			recentWorkspaces.list.add(recentWorkspaceEntry);
		else
			recentWorkspaces.list
					.get(recentWorkspaces.list.indexOf(recentWorkspaceEntry)).name = recentWorkspaceEntry.name;
		ListUtils.rearrange(recentWorkspaces.list, recentWorkspaceEntry);
		saveRecentWorkspaces();
	}

	private void removeRecentWorkspace(RecentWorkspaceEntry recentWorkspace) {
		recentWorkspaces.list.remove(recentWorkspace);
		saveRecentWorkspaces();
	}

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	private void saveRecentWorkspaces() {
		String serialized = gson.toJson(recentWorkspaces);
		if (serialized != null && !serialized.isEmpty()) {
			FileIO.writeStringToFile(serialized, UserFolderManager.getFileFromUserFolder("recentworkspaces"));
		}
	}

	private void reloadRecents() {
		if (UserFolderManager.getFileFromUserFolder("recentworkspaces").isFile()) {
			try {
				recentWorkspaces = gson
						.fromJson(FileIO.readFileToString(UserFolderManager.getFileFromUserFolder("recentworkspaces")),
								RecentWorkspaces.class);
				if (recentWorkspaces != null) {
					List<RecentWorkspaceEntry> recentWorkspacesFiltered = new ArrayList<>();
					for (RecentWorkspaceEntry recentWorkspaceEntry : recentWorkspaces.list)
						if (recentWorkspaceEntry.getPath().isFile())
							recentWorkspacesFiltered.add(recentWorkspaceEntry);
					recentWorkspaces = new RecentWorkspaces(recentWorkspacesFiltered);
				}
			} catch (Exception e) {
				recentWorkspaces = null;
				LOG.warn("Failed to load recent workspaces", e);
			}
		}

		recentPanel.removeAll();

		if (recentWorkspaces != null && recentWorkspaces.list.size() > 0) {
			DefaultListModel<RecentWorkspaceEntry> defaultListModel = new DefaultListModel<>();
			recentWorkspaces.list.forEach(defaultListModel::addElement);
			JList<RecentWorkspaceEntry> recentsList = new JList<>(defaultListModel);
			recentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			recentsList.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent mouseEvent) {
					if (mouseEvent.getButton() == MouseEvent.BUTTON2) {
						int idx = recentsList.locationToIndex(mouseEvent.getPoint());
						removeRecentWorkspace(defaultListModel.elementAt(idx));
						reloadRecents();
					} else if (mouseEvent.getClickCount() == 2) {
						workspaceOpenListener.workspaceOpened(recentsList.getSelectedValue().getPath());
					}
				}
			});
			recentsList.setCellRenderer(new RecentWorkspacesRenderer());
			JScrollPane scrollPane = new JScrollPane(recentsList);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			recentPanel.add(scrollPane);
		} else if (recentWorkspaces == null) {
			JLabel norecents = new JLabel(
					"<html><center>Failed to load recent workspaces<br>shortcuts. Re-open them to add<br>them to the list again.");
			norecents.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			recentPanel.add(PanelUtils.totalCenterInPanel(norecents));
		} else {
			JLabel norecents = new JLabel("No recent workspaces");
			norecents.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			recentPanel.add(PanelUtils.totalCenterInPanel(norecents));
		}

		recentPanel.revalidate();
	}

	@Override public void setVisible(boolean b) {
		super.setVisible(b);
		if (b)
			reloadRecents();
	}

	private void addWorkspaceButton(String text, ImageIcon icon, ActionListener event, JPanel container) {
		JButton newWorkspace = new JButton(text);
		ComponentUtils.deriveFont(newWorkspace, 10);
		newWorkspace.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		newWorkspace.setPreferredSize(new Dimension(100, 100));
		newWorkspace.setMargin(new Insets(0, 0, 0, 0));
		newWorkspace.setIcon(icon);
		newWorkspace.addActionListener(event);
		newWorkspace.setVerticalTextPosition(SwingConstants.BOTTOM);
		newWorkspace.setHorizontalTextPosition(SwingConstants.CENTER);
		newWorkspace.setBorder(
				BorderFactory.createLineBorder(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).brighter(), 1));
		newWorkspace.setCursor(new Cursor(Cursor.HAND_CURSOR));
		container.add(newWorkspace);
	}

	private void initWebsitePanel() {
		CompletableFuture<String[]> newsFuture = new CompletableFuture<>();
		MCreatorApplication.WEB_API.getWebsiteNews(newsFuture);
		JLabel nov = new JLabel(
				"<html>Latest news from MCreator website:<br><div style=\"font-size: 14px;\">Loading news ...</div>");
		nov.setCursor(new Cursor(Cursor.HAND_CURSOR));
		newsFuture.whenComplete((news, throwable) -> SwingUtilities.invokeLater(() -> {
			if (news != null)
				nov.setText("<html>Latest news from MCreator website:<br><div style=\"font-size: 14px;\">" + StringUtils
						.abbreviateString(news[0], 43) + "</div>");
			else
				nov.setText("");
			nov.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent en) {
					if (news != null)
						DesktopUtils.browseSafe(news[1]);
				}
			});
		}));

		CompletableFuture<String[]> motwFuture = new CompletableFuture<>();
		MCreatorApplication.WEB_API.getModOfTheWeekData(motwFuture);
		JLabel lab3 = new JLabel("<html>Mod of the week:<br><font style=\"font-size: 14px;\">Loading data ...");
		lab3.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		JLabel lab2 = new JLabel();
		lab2.setIcon(new EmptyIcon(48, 48));
		JComponent motwpan = PanelUtils.westAndEastElement(lab3, lab2);
		motwpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		motwFuture.whenComplete((motw, throwable) -> SwingUtilities.invokeLater(() -> {
			motwpan.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent arg0) {
					if (motw != null)
						DesktopUtils.browseSafe(motw[1]);
				}
			});
			if (motw != null)
				lab3.setText("<html>Mod of the week:<br><font style=\"font-size: 14px;\">" + StringUtils
						.abbreviateString(motw[0], 33) + "&nbsp;&nbsp;&nbsp;&nbsp;");
			else
				lab3.setText("");
			ImageIcon defaultIcon;
			if (motw != null && (defaultIcon = WebIO.getIconFromURL(motw[4], 48, 48, null, true)) != null)
				lab2.setIcon(defaultIcon);
		}));

		JComponent south = PanelUtils.westAndEastElement(nov, motwpan, 20, 20);
		south.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		JComponent hol = PanelUtils.gridElements(1, 1, south);
		hol.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		JPanel soim;
		if (!Launcher.version.isSnapshot()) {
			soim = new ImagePanel(UIRES.get("splash").getImage());
			((ImagePanel) soim).setFitToWidth(true);
			((ImagePanel) soim).setOffsetY(-320);
		} else {
			soim = new JPanel();
			soim.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		}

		soim.setLayout(new BorderLayout());
		soim.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		soim.add(south);

		add("South", soim);
	}

	static class RecentWorkspacesRenderer extends JLabel implements ListCellRenderer<RecentWorkspaceEntry> {
		@Override
		public Component getListCellRendererComponent(JList<? extends RecentWorkspaceEntry> list,
				RecentWorkspaceEntry value, int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
					(Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			setBorder(BorderFactory.createEmptyBorder(2, 5, 3, 0));

			setFont(AbstractMCreatorTheme.light_font.deriveFont(16.0f));

			String path = value.getPath().getParentFile().getAbsolutePath().replace("\\", "/");
			setText("<html><font style=\"font-size: 15px;\">" + StringUtils.abbreviateString(value.name, 20)
					+ "</font><small><br>" + StringUtils.abbreviateStringInverse(path, 37));

			return this;
		}
	}

	interface WorkspaceOpenListener {
		void workspaceOpened(File workspaceFolder);
	}

	static class RecentWorkspaces {
		List<RecentWorkspaceEntry> list;

		RecentWorkspaces() {
			list = new ArrayList<>();
		}

		RecentWorkspaces(Collection<RecentWorkspaceEntry> recentWorkspacesFiltered) {
			list = new ArrayList<>(recentWorkspacesFiltered);
		}
	}

	public static class RecentWorkspaceEntry {

		private String name;
		private String path;

		public RecentWorkspaceEntry(String name, File path) {
			this.name = name;
			this.path = path.toString();
		}

		public File getPath() {
			return new File(path);
		}

		public String getName() {
			return name;
		}

		@Override public int hashCode() {
			return path.hashCode();
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof RecentWorkspaceEntry) {
				RecentWorkspaceEntry cmpObj = (RecentWorkspaceEntry) obj;
				return cmpObj.path.equals(path);
			}
			return false;
		}
	}

}
