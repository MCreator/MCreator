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

package net.mcreator.ui.workspace.selector;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.net.WebIO;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.WorkspaceSelectorLoadedEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.SplashScreen;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SocialButtons;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.workspace.NewWorkspaceDialog;
import net.mcreator.ui.init.AppIcon;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.notifications.INotificationConsumer;
import net.mcreator.ui.notifications.NotificationsRenderer;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.ShareableZIPManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class WorkspaceSelector extends JFrame implements DropTargetListener, INotificationConsumer {

	private static final Logger LOG = LogManager.getLogger("Workspace Selector");

	private final CardLayout recentPanes = new CardLayout();
	private final JPanel recentPanel = new JPanel(recentPanes);
	private final WorkspaceOpenListener workspaceOpenListener;
	private RecentWorkspaces recentWorkspaces = new RecentWorkspaces();

	@Nullable private final MCreatorApplication application;

	private final JButton newWorkspace;

	private final JPanel subactions = new JPanel(new GridLayout(-1, 1, 0, 2));

	private final NotificationsRenderer notificationsRenderer;

	private final DefaultListModel<RecentWorkspaceEntry> defaultListModel = new DefaultListModel<>();
	private final JList<RecentWorkspaceEntry> recentsList = new JList<>(defaultListModel);

	public WorkspaceSelector(@Nullable MCreatorApplication application, WorkspaceOpenListener workspaceOpenListener) {
		this.workspaceOpenListener = workspaceOpenListener;
		this.application = application;

		setTitle("MCreator " + Launcher.version.getMajorString());
		setIconImages(AppIcon.getAppIcons());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (application != null)
			addWindowListener(new WindowAdapter() {
				@Override public void windowClosing(WindowEvent arg0) {
					application.closeApplication();
				}
			});

		JPanel actions = new JPanel(new BorderLayout(0, 6));

		newWorkspace = mainWorkspaceButton(L10N.t("dialog.workspace_selector.new_workspace"), UIRES.get("wrk_add"),
				e -> {
					NewWorkspaceDialog newWorkspaceDialog = new NewWorkspaceDialog(this);
					if (newWorkspaceDialog.getWorkspaceFile() != null)
						workspaceOpenListener.workspaceOpened(newWorkspaceDialog.getWorkspaceFile());
				});

		actions.add("North", newWorkspace);
		actions.add("Center", subactions);

		addWorkspaceButton(L10N.t("dialog.workspace_selector.open_workspace"), UIRES.get("opnwrk"), e -> {
			File workspaceFile = FileDialogs.getOpenDialog(this, new String[] { ".mcreator" });
			if (workspaceFile != null && workspaceFile.getParentFile().isDirectory())
				workspaceOpenListener.workspaceOpened(workspaceFile);
		});

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
		});

		JPanel logoPanel = new JPanel(new BorderLayout(5, 5));
		JLabel logo = new JLabel(UIRES.SVG.getBuiltIn("logo", 250, (int) (250 * (63 / 350.0))));
		logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logo.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN);
			}
		});
		logoPanel.add("North", logo);

		SocialButtons socialButtons = new SocialButtons();
		logoPanel.add("Center", PanelUtils.centerInPanel(socialButtons));

		logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		JPanel southcenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JLabel donate = L10N.label("dialog.workspace_selector.donate");
		donate.setIcon(UIRES.get("donate"));
		donate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.deriveFont(donate, 13);
		donate.setForeground(Theme.current().getForegroundColor());
		donate.setBorder(BorderFactory.createEmptyBorder());
		donate.setHorizontalTextPosition(JLabel.LEFT);
		donate.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/donate");
			}
		});
		southcenter.add(donate);

		southcenter.add(new JEmptyBox(7, 5));

		JLabel prefs = new JLabel(L10N.t("dialog.workspace_selector.preferences")) {
			@Override protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				try {
					String flagpath =
							"/flags/" + L10N.getLocale().toString().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png";
					BufferedImage image = ImageIO.read(
							Objects.requireNonNull(getClass().getResourceAsStream(flagpath)));
					g.drawImage(ImageUtils.crop(image, new Rectangle(1, 2, 14, 11)), getWidth() - 15, 4, this);
				} catch (Exception ignored) { // flag not found, ignore
				}
			}

			@Override public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width + 21, super.getPreferredSize().height);
			}
		};
		prefs.setIcon(UIRES.get("settings"));
		prefs.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.deriveFont(prefs, 13);
		prefs.setForeground(Theme.current().getForegroundColor());
		prefs.setBorder(BorderFactory.createEmptyBorder());
		prefs.setHorizontalTextPosition(JLabel.LEFT);
		prefs.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				new PreferencesDialog(WorkspaceSelector.this, null);
			}
		});
		southcenter.add(prefs);

		JPanel southcenterleft = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel version = L10N.label("dialog.workspace_selector.version",
				Launcher.version.isSnapshot() ? Launcher.version.getMajorString() : Launcher.version.getFullString());
		version.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				AboutAction.showDialog(WorkspaceSelector.this);
			}
		});
		ComponentUtils.deriveFont(version, 13);
		version.setForeground(Theme.current().getForegroundColor());
		version.setHorizontalTextPosition(SwingConstants.LEFT);
		version.setIcon(UIRES.get("info"));
		version.setCursor(new Cursor(Cursor.HAND_CURSOR));
		southcenterleft.add(version);

		JComponent southSubComponent = PanelUtils.westAndEastElement(southcenterleft, southcenter);

		southSubComponent.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));

		JComponent centerComponent = PanelUtils.centerAndSouthElement(
				PanelUtils.northAndCenterElement(logoPanel, PanelUtils.totalCenterInPanel(actions)), southSubComponent);

		notificationsRenderer = new NotificationsRenderer(centerComponent);

		add("Center", centerComponent);

		recentPanel.setBackground(Theme.current().getSecondAltBackgroundColor());
		recentPanel.setPreferredSize(new Dimension(225, 10));

		JLabel norecentsloaded = L10N.label("dialog.workspace_selector.no_workspaces_loaded");
		norecentsloaded.setForeground(Theme.current().getAltForegroundColor());
		JLabel norecents = L10N.label("dialog.workspace_selector.no_workspaces");
		norecents.setForeground(Theme.current().getAltForegroundColor());

		recentsList.setBackground(Theme.current().getSecondAltBackgroundColor());
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
		recentsList.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					Object[] options = { L10N.t("dialog.workspace_selector.delete_workspace.recent_list"),
							L10N.t("dialog.workspace_selector.delete_workspace.workspace"), L10N.t("common.cancel") };
					int n = JOptionPane.showOptionDialog(WorkspaceSelector.this,
							L10N.t("dialog.workspace_selector.delete_workspace.message",
									recentsList.getSelectedValue().getName()),
							L10N.t("dialog.workspace_selector.delete_workspace.title"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (n == 0) {
						removeRecentWorkspace(recentsList.getSelectedValue());
						reloadRecents();
					} else if (n == 1) {
						int m = JOptionPane.showConfirmDialog(WorkspaceSelector.this,
								L10N.t("dialog.workspace_selector.delete_workspace.confirmation",
										recentsList.getSelectedValue().getName()), L10N.t("common.confirmation"),
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (m == JOptionPane.YES_OPTION) {
							FileIO.deleteDir(recentsList.getSelectedValue().getPath().getParentFile());
							reloadRecents();
						}
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					workspaceOpenListener.workspaceOpened(recentsList.getSelectedValue().getPath());
				}
			}
		});
		recentsList.setCellRenderer(new RecentWorkspacesRenderer());
		JScrollPane recentsScrollPane = new JScrollPane(recentsList);
		recentsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		recentPanel.add("recents", recentsScrollPane);
		recentPanel.add("norecentsloaded", PanelUtils.totalCenterInPanel(norecentsloaded));
		recentPanel.add("norecents", PanelUtils.totalCenterInPanel(norecents));

		initWebsitePanel();

		add("West", recentPanel);

		new DropTarget(this, DnDConstants.ACTION_MOVE, this, true, null);

		MCREvent.event(new WorkspaceSelectorLoadedEvent(this));

		setSize(795, 460);
		setResizable(false);
		setLocationRelativeTo(null);

		if (OS.getOS() == OS.WINDOWS) {
			getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
			centerComponent.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
		} else if (OS.getOS() == OS.MAC && SystemInfo.isMacFullWindowContentSupported) {
			getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
			getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
			getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
			recentPanel.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
			centerComponent.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
		}
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
				if (!transferData.isEmpty()) {
					Object transfObj = transferData.get(0);
					if (transfObj instanceof File workspaceFile) {
						if (workspaceFile.getName().endsWith(".mcreator")) {
							workspaceOpenListener.workspaceOpened(workspaceFile);
						} else {
							Toolkit.getDefaultToolkit().beep();
						}
					}
				}
			} catch (Exception ex) {
				LOG.error("Drag and drop failed", ex);
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

	public void addOrUpdateRecentWorkspace(RecentWorkspaceEntry recentWorkspaceEntry) {
		if (!recentWorkspaces.getList().contains(recentWorkspaceEntry))
			recentWorkspaces.getList().add(recentWorkspaceEntry);
		else
			recentWorkspaces.getList().get(recentWorkspaces.getList().indexOf(recentWorkspaceEntry))
					.update(recentWorkspaceEntry);

		ListUtils.rearrange(recentWorkspaces.getList(), recentWorkspaceEntry);
		saveRecentWorkspaces();
	}

	private void removeRecentWorkspace(RecentWorkspaceEntry recentWorkspace) {
		recentWorkspaces.getList().remove(recentWorkspace);
		saveRecentWorkspaces();
	}

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

	private void saveRecentWorkspaces() {
		String serialized = gson.toJson(recentWorkspaces);
		if (serialized != null && !serialized.isEmpty()) {
			FileIO.writeStringToFile(serialized, UserFolderManager.getFileFromUserFolder("recentworkspaces"));
		}
	}

	private synchronized void reloadRecents() {
		if (UserFolderManager.getFileFromUserFolder("recentworkspaces").isFile()) {
			try {
				recentWorkspaces = gson.fromJson(
						FileIO.readFileToString(UserFolderManager.getFileFromUserFolder("recentworkspaces")),
						RecentWorkspaces.class);
				if (recentWorkspaces != null) {
					List<RecentWorkspaceEntry> recentWorkspacesFiltered = new ArrayList<>();
					for (RecentWorkspaceEntry recentWorkspaceEntry : recentWorkspaces.getList())
						if (recentWorkspaceEntry.getPath().isFile())
							recentWorkspacesFiltered.add(recentWorkspaceEntry);
					recentWorkspaces = new RecentWorkspaces(recentWorkspacesFiltered);
				}
			} catch (Exception e) {
				recentWorkspaces = null;
				LOG.warn("Failed to load recent workspaces", e);
			}
		}

		if (recentWorkspaces != null && !recentWorkspaces.getList().isEmpty()) {
			defaultListModel.removeAllElements();
			recentWorkspaces.getList().forEach(defaultListModel::addElement);
			recentPanes.show(recentPanel, "recents");
		} else if (recentWorkspaces == null) {
			recentPanes.show(recentPanel, "norecentsloaded");
		} else {
			recentPanes.show(recentPanel, "norecents");
		}

		recentPanel.revalidate();
	}

	@Override public void setVisible(boolean b) {
		if (b)
			reloadRecents();

		super.setVisible(b);

		if (b)
			newWorkspace.requestFocusInWindow();
	}

	private JButton mainWorkspaceButton(String text, ImageIcon icon, ActionListener event) {
		JButton newWorkspace = new JButton(text);
		ComponentUtils.deriveFont(newWorkspace, 13);
		newWorkspace.setBackground(Theme.current().getBackgroundColor());
		newWorkspace.setPreferredSize(new Dimension(240, 48));
		newWorkspace.setIcon(icon);
		newWorkspace.addActionListener(event);
		newWorkspace.setVerticalTextPosition(SwingConstants.CENTER);
		newWorkspace.setHorizontalTextPosition(SwingConstants.RIGHT);
		newWorkspace.setHorizontalAlignment(SwingConstants.LEFT);
		newWorkspace.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return newWorkspace;
	}

	/**
	 * Adds a new "quick start" button to the main panel of the workspace selector.
	 *
	 * @param text  The text displayed by the button being added.
	 * @param icon  The icon to be shown by the button being added.
	 * @param event The action performed when the button is clicked.
	 */
	public void addWorkspaceButton(String text, ImageIcon icon, ActionListener event) {
		JButton workspaceButton = new JButton(text);
		ComponentUtils.deriveFont(workspaceButton, 11);
		workspaceButton.setBackground(Theme.current().getBackgroundColor());
		workspaceButton.setPreferredSize(new Dimension(240, 22));
		workspaceButton.setIcon(
				ImageUtils.drawOver(new EmptyIcon.ImageIcon(45, 16), icon, 45 / 2 - 16 / 2 + 2, 0, 16, 16));
		workspaceButton.addActionListener(event);
		workspaceButton.setVerticalTextPosition(SwingConstants.CENTER);
		workspaceButton.setHorizontalTextPosition(SwingConstants.RIGHT);
		workspaceButton.setHorizontalAlignment(SwingConstants.LEFT);
		workspaceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		workspaceButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		subactions.add(workspaceButton);
	}

	private void initWebsitePanel() {
		CompletableFuture<String[]> newsFuture = new CompletableFuture<>();
		MCreatorApplication.WEB_API.getWebsiteNews(newsFuture);
		JLabel nov = new JLabel("<html><font style=\"font-size: 9px;\">" + L10N.t("dialog.workspace_selector.news")
				+ "<br></font><font style=\"font-size: 15px; color: #f5f5f5;\">" + L10N.t(
				"dialog.workspace_selector.webdata.loading"));
		nov.setCursor(new Cursor(Cursor.HAND_CURSOR));
		nov.setForeground(new Color(0xf5f5f5));
		newsFuture.whenComplete((news, throwable) -> SwingUtilities.invokeLater(() -> {
			if (news != null) {
				nov.setText("<html><font style=\"font-size: 9px;\">" + L10N.t("dialog.workspace_selector.news")
						+ "<br></font><font style=\"font-size: 15px; color: #f5f5f5;\">" + StringUtils.abbreviateString(
						news[0], 43));

				if (PreferencesManager.PREFERENCES.notifications.showWebsiteNewsNotifications.get()) {
					String id = news[4];

					// Do not show notification the first time
					if (PreferencesManager.PREFERENCES.hidden.lastWebsiteNewsRead.get().isBlank())
						PreferencesManager.PREFERENCES.hidden.lastWebsiteNewsRead.set(id);

					if (!PreferencesManager.PREFERENCES.hidden.lastWebsiteNewsRead.get().equals(id)) {
						String title = L10N.t("notification.news.title", news[0]);
						String link = news[1];
						String description = StringUtils.justifyText(StringUtils.abbreviateString(news[2], 300), 50,
								"<br>");
						ImageIcon newsIcon = null;
						if (news[3] != null && !news[3].isBlank()) {
							newsIcon = WebIO.getIconFromURL(MCreatorApplication.SERVER_DOMAIN + news[3], 3 * 60, 60,
									null);
						}
						addNotification(title, newsIcon, description,
								new NotificationsRenderer.ActionButton(L10N.t("notification.news.read_more"), e -> {
									DesktopUtils.browseSafe(link);
									PreferencesManager.PREFERENCES.hidden.lastWebsiteNewsRead.set(id);
								}), new NotificationsRenderer.ActionButton(L10N.t("notification.news.hide"),
										e -> PreferencesManager.PREFERENCES.hidden.lastWebsiteNewsRead.set(id)));
					}
				}
			} else {
				nov.setText("");
			}
			nov.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent en) {
					if (news != null)
						DesktopUtils.browseSafe(news[1]);
				}
			});
		}));

		CompletableFuture<String[]> motwFuture = new CompletableFuture<>();
		MCreatorApplication.WEB_API.getModOfTheWeekData(motwFuture);
		JLabel lab3 = new JLabel("<html><font style=\"font-size: 9px;\">" + L10N.t("dialog.workspace_selector.motw")
				+ "<br></font><font style=\"font-size: 15px; color: #f5f5f5;\">" + L10N.t(
				"dialog.workspace_selector.webdata.loading"));
		lab3.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		lab3.setForeground(new Color(0xf5f5f5));
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
			if (motw != null) {
				lab3.setText("<html><font style=\"font-size: 9px;\">" + L10N.t("dialog.workspace_selector.motw")
						+ "<br></font><font style=\"font-size: 15px; color: #f5f5f5;\">" + StringUtils.abbreviateString(
						motw[0], 33) + "&nbsp;&nbsp;&nbsp;&nbsp;");
			} else {
				lab3.setText("");
			}
			ImageIcon defaultIcon;
			if (motw != null && (defaultIcon = WebIO.getIconFromURL(motw[4], 48, 48, null, true)) != null)
				lab2.setIcon(defaultIcon);
		}));

		JComponent south = PanelUtils.westAndEastElement(nov, motwpan, 20, 20);
		south.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		JPanel soim;
		if (!Launcher.version.isSnapshot()) {
			soim = new ImagePanel(SplashScreen.getSplashImage(true));
			((ImagePanel) soim).setFitToWidth(true);
			((ImagePanel) soim).setOffsetY(-400);
		} else {
			soim = new JPanel();
			soim.setBackground(Theme.current().getSecondAltBackgroundColor());
		}

		soim.setLayout(new BorderLayout());
		soim.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.current().getAltBackgroundColor()));
		soim.add(south);

		add("South", soim);
	}

	@Nonnull public RecentWorkspaces getRecentWorkspaces() {
		if (recentWorkspaces == null)
			this.recentWorkspaces = new RecentWorkspaces();

		return recentWorkspaces;
	}

	@Nullable public MCreatorApplication getApplication() {
		return application;
	}

	@Override public NotificationsRenderer getNotificationsRenderer() {
		return notificationsRenderer;
	}

}
