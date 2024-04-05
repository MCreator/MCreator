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

import net.mcreator.Launcher;
import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleStateListener;
import net.mcreator.gradle.GradleTaskResult;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.browser.WorkspaceFileBrowser;
import net.mcreator.ui.component.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.debug.DebugPanel;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.AppIcon;
import net.mcreator.ui.init.BackgroundLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.notifications.INotificationConsumer;
import net.mcreator.ui.notifications.NotificationsRenderer;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.ListUtils;
import net.mcreator.util.MCreatorVersionNumber;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.ShareableZIPManager;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class MCreator extends JFrame implements IWorkspaceProvider, IGeneratorProvider, INotificationConsumer {

	private static final Logger LOG = LogManager.getLogger("MCreator");

	public WorkspacePanel mv;
	private final GradleConsole gradleConsole;

	private final WorkspaceFileBrowser workspaceFileBrowser;

	public final ActionRegistry actionRegistry;

	public final MCreatorTabs mcreatorTabs;
	public final StatusBar statusBar;

	public MCreatorTabs.Tab workspaceTab;
	public MCreatorTabs.Tab consoleTab;

	private final MainMenuBar menuBar;
	private final MainToolBar toolBar;

	private final MCreatorApplication application;

	public final JSplitPane splitPane;

	private final Workspace workspace;

	private final long windowUID;

	private final NotificationsRenderer notificationsRenderer;

	private final DebugPanel debugPanel;

	public MCreator(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		LOG.info("Opening MCreator workspace: " + workspace.getWorkspaceSettings().getModID());

		this.windowUID = System.currentTimeMillis();
		this.workspace = workspace;
		this.application = application;

		this.gradleConsole = new GradleConsole(this);
		this.gradleConsole.addGradleStateListener(new GradleStateListener() {
			@Override public void taskStarted(String taskName) {
				mv.disableRemoving();
			}

			@Override public void taskFinished(GradleTaskResult result) {
				mv.enableRemoving();
			}
		});

		this.debugPanel = new DebugPanel(this);

		this.mcreatorTabs = new MCreatorTabs();

		this.actionRegistry = new ActionRegistry(this);
		this.statusBar = new StatusBar(this);

		this.workspaceFileBrowser = new WorkspaceFileBrowser(this);

		new MCreatorDropTarget(this);

		this.menuBar = new MainMenuBar(this);
		this.toolBar = new MainToolBar(this);

		setTitle(WindowTitleHelper.getWindowTitle(this));

		setLayout(new BorderLayout(0, 0));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() > 2140 && screenSize.getHeight() > 1250)
			setSize(2140, 1250);
		else if (screenSize.getWidth() > 1574 && screenSize.getHeight() > 970)
			setSize(1574, 967);
		else if (screenSize.getWidth() > 1290 && screenSize.getHeight() > 795)
			setSize(1290, 791);
		else
			setSize(1002, 640);

		if (OS.getOS() == OS.MAC)
			getRootPane().putClientProperty("apple.awt.fullscreenable", true);

		if (PreferencesManager.PREFERENCES.hidden.fullScreen.get())
			setExtendedState(JFrame.MAXIMIZED_BOTH);

		setIconImages(AppIcon.getAppIcons());
		setLocationRelativeTo(null);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent arg0) {
				closeThisMCreator(false);
			}
		});

		mcreatorTabs.addTabShownListener(tab -> {
			if (tab.equals(workspaceTab))
				mv.reloadElementsInCurrentTab();

			menuBar.refreshMenuBar();

			setTitle(WindowTitleHelper.getWindowTitle(this));
		});

		UserFolderManager.getFileFromUserFolder("backgrounds").mkdirs();

		JPanel mpan;

		// Load backgrounds depending on the background source
		List<Image> bgimages = new ArrayList<>();
		switch (PreferencesManager.PREFERENCES.ui.backgroundSource.get()) {
		case "All":
			bgimages.addAll(BackgroundLoader.loadThemeBackgrounds());
			bgimages.addAll(BackgroundLoader.loadUserBackgrounds());
			break;
		case "Current theme":
			bgimages = BackgroundLoader.loadThemeBackgrounds();
			break;
		case "Custom":
			bgimages = BackgroundLoader.loadUserBackgrounds();
			break;
		}

		Image bgimage = null;
		if (!bgimages.isEmpty()) {
			bgimage = ListUtils.getRandomItem(bgimages);
			float avg = ImageUtils.getAverageLuminance(ImageUtils.toBufferedImage(bgimage));
			if (avg > 0.15) {
				avg = (float) Math.min(avg * 1.7, 0.85);
				bgimage = ImageUtils.drawOver(new ImageIcon(bgimage), new ImageIcon(
						ImageUtils.emptyImageWithSize(bgimage.getWidth(this), bgimage.getHeight(this),
								new Color(0.12f, 0.12f, 0.12f, avg)))).getImage();
			}
		}

		if (bgimage != null) {
			mpan = new ImagePanel(bgimage);
			((ImagePanel) mpan).setKeepRatio(true);
		} else {
			mpan = new JPanel();
			mpan.setBackground(Theme.current().getSecondAltBackgroundColor());
		}

		mpan.setLayout(new BorderLayout());
		mpan.add("Center", mcreatorTabs.getContainer());

		mv = new WorkspacePanel(this);

		JPanel pon = new JPanel(new BorderLayout(0, 0));
		pon.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.current().getSecondAltBackgroundColor()));

		workspaceTab = new MCreatorTabs.Tab(L10N.t("tab.workspace"),
				ComponentUtils.applyPadding(mv, 5, true, true, true, true), "Workspace", true, false);
		mcreatorTabs.addTab(workspaceTab);
		pon.add("West", workspaceTab);

		consoleTab = new MCreatorTabs.Tab(L10N.t("tab.console") + " ", gradleConsole, "Console", true, false) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				switch (gradleConsole.getStatus()) {
				case GradleConsole.READY:
					g.setColor(Theme.current().getForegroundColor());
					break;
				case GradleConsole.RUNNING:
					g.setColor(new Color(158, 247, 89));
					break;
				case GradleConsole.ERROR:
					g.setColor(new Color(0xFF5956));
					break;
				}
				if (gradleConsole.isGradleSetupTaskRunning())
					g.setColor(new Color(106, 247, 244));
				g.fillRect(getWidth() - 15, getHeight() - 18, 3, 3);
			}
		};
		consoleTab.setHasRightBorder(false);
		consoleTab.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)
					actionRegistry.buildWorkspace.doAction();
			}
		});
		mcreatorTabs.addTab(consoleTab);
		pon.add("East", consoleTab);

		mcreatorTabs.showTabNoNotify(workspaceTab);

		pon.add("Center", mcreatorTabs.getTabsStrip());

		workspace.getFileManager().setDataSavedListener(() -> statusBar.setPersistentMessage(
				L10N.t("workspace.statusbar.autosave_message", new SimpleDateFormat("HH:mm").format(new Date()))));

		JComponent rightPanel = PanelUtils.northAndCenterElement(pon, mpan);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, workspaceFileBrowser, rightPanel);
		splitPane.setBackground(Theme.current().getAltBackgroundColor());
		splitPane.setOneTouchExpandable(true);

		splitPane.setDividerLocation(280);
		splitPane.setDividerLocation(PreferencesManager.PREFERENCES.hidden.projectTreeSplitPos.get());

		rightPanel.setMinimumSize(new Dimension(0, 0));
		workspaceFileBrowser.setMinimumSize(new Dimension(0, 0));

		JAdaptiveSplitPane mainContent = new JAdaptiveSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, debugPanel, 0.65);

		this.notificationsRenderer = new NotificationsRenderer(mainContent);

		add("South", statusBar);
		add("North", toolBar);
		add("Center", mainContent);

		MCREvent.event(new MCreatorLoadedEvent(this));
	}

	@Override public void setVisible(boolean makeVisible) {
		super.setVisible(makeVisible);
		if (makeVisible) {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));

			if (MCreatorVersionNumber.isBuildNumberDevelopment(workspace.getMCreatorVersion())) {
				workspace.setMCreatorVersion(
						Launcher.version.versionlong); // if we open dev version, store new version number in it
			}

			// backup if new version and backups are enabled
			if (workspace.getMCreatorVersion() < Launcher.version.versionlong
					&& PreferencesManager.PREFERENCES.backups.backupOnVersionSwitch.get()) {
				ShareableZIPManager.exportZIP(L10N.t("dialog.workspace.export_backup"),
						new File(workspace.getFolderManager().getWorkspaceCacheDir(),
								"FullBackup" + workspace.getMCreatorVersion() + ".zip"), this, true);
			}

			// if we need to set up the workspace, we do so
			if (WorkspaceGeneratorSetup.shouldSetupBeRan(workspace.getGenerator())) {
				WorkspaceGeneratorSetupDialog.runSetup(this,
						PreferencesManager.PREFERENCES.notifications.openWhatsNextPage.get()
								&& !Launcher.version.isDevelopment());
			}

			if (workspace.getMCreatorVersion()
					< Launcher.version.versionlong) { // if this is the case, update the workspace files
				RegenerateCodeAction.regenerateCode(this, true, true);
				workspace.setMCreatorVersion(Launcher.version.versionlong);
				workspace.getFileManager().saveWorkspaceDirectlyAndWait();
			} else if (workspace.isRegenerateRequired()) { // if workspace is marked for regeneration, we do so
				RegenerateCodeAction.regenerateCode(this, true, true);
			}

			// it is not safe to do user operations on workspace while it is being preloaded, so we lock the UI
			setGlassPane(getPreloaderPane());
			getGlassPane().setVisible(true);

			// Preload workspace file browser
			new Thread(this.workspaceFileBrowser::reloadTree, "File browser preloader").start();

			// reinit (preload) MCItems (also loads GEs and performs conversions if needed)
			new Thread(() -> {
				workspace.getModElements().forEach(ModElement::getMCItems);

				SwingUtilities.invokeLater(() -> {
					getGlassPane().setVisible(false);
					setGlassPane(new JEmptyBox());
					setJMenuBar(menuBar);
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				});
			}, "ME preloader").start();
		}
	}

	public MCreatorApplication getApplication() {
		return application;
	}

	public GradleConsole getGradleConsole() {
		return gradleConsole;
	}

	public WorkspaceFileBrowser getProjectBrowser() {
		return workspaceFileBrowser;
	}

	@Override public @Nonnull Workspace getWorkspace() {
		return workspace;
	}

	@Override public NotificationsRenderer getNotificationsRenderer() {
		return notificationsRenderer;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public boolean closeThisMCreator(boolean returnToProjectSelector) {
		boolean safetoexit = gradleConsole.getStatus() != GradleConsole.RUNNING;
		if (!safetoexit) {
			if (gradleConsole.isGradleSetupTaskRunning()) {
				JOptionPane.showMessageDialog(this, L10N.t("action.gradle.close_mcreator_while_installation_message"),
						L10N.t("action.gradle.close_mcreator_while_installation_title"), JOptionPane.WARNING_MESSAGE);
				return false;
			}

			int reply = JOptionPane.showConfirmDialog(this,
					L10N.t("action.gradle.close_mcreator_while_running_message"),
					L10N.t("action.gradle.close_mcreator_while_running_title"), JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);
			if (reply == JOptionPane.YES_OPTION) {
				safetoexit = true;
				gradleConsole.cancelTask();
			}
		}

		if (safetoexit) {
			LOG.info("Closing MCreator window ...");
			PreferencesManager.PREFERENCES.hidden.fullScreen.set(getExtendedState() == MAXIMIZED_BOTH);
			if (splitPane != null)
				PreferencesManager.PREFERENCES.hidden.projectTreeSplitPos.set(
						splitPane.getDividerLocation()); // this one could be stored per workspace in the future

			mcreatorTabs.getTabs().forEach(tab -> {
				if (tab.getTabClosedListener() != null)
					tab.getTabClosedListener().tabClosed(tab);
			});

			workspace.close();

			setVisible(false); // close the window

			application.getOpenMCreators().remove(this);

			if (application.getOpenMCreators()
					.isEmpty()) { // no MCreator windows left, close the app, or return to project selector if selected
				if (returnToProjectSelector)
					application.showWorkspaceSelector();
				else
					application.closeApplication();
			}

			return true;
		}
		return false;
	}

	@Override public void setTitle(String title) {
		super.setTitle(title);

		if (application != null) {
			String tabAddition = "";

			if (mcreatorTabs.getCurrentTab() != null) {
				tabAddition = " - " + mcreatorTabs.getCurrentTab().getText();
			}

			// Do not externalize this text
			application.getDiscordClient()
					.updatePresence("Working on " + workspace.getWorkspaceSettings().getModName() + tabAddition,
							Launcher.version.getMajorString() + " for " + workspace.getGenerator()
									.getGeneratorMinecraftVersion(),
							"type-" + workspace.getGeneratorConfiguration().getGeneratorFlavor().name()
									.toLowerCase(Locale.ENGLISH));
		}
	}

	@Override public boolean equals(Object mcreator) {
		if (mcreator instanceof MCreator theothermcreator) {
			if (theothermcreator.workspace != null && workspace != null)
				return theothermcreator.workspace.getFileManager().getWorkspaceFile()
						.equals(workspace.getFileManager().getWorkspaceFile());
			else
				return theothermcreator.windowUID == windowUID;
		}
		return false;
	}

	@Override public int hashCode() {
		if (workspace != null)
			return workspace.getFileManager().getWorkspaceFile().hashCode();
		return Long.valueOf(windowUID).hashCode();
	}

	private JComponent getPreloaderPane() {
		JPanel wrap = new BlockingGlassPane();
		JLabel loading = L10N.label("workspace.loading");
		loading.setIconTextGap(5);
		loading.setFont(loading.getFont().deriveFont(16f));
		loading.setForeground(Theme.current().getAltForegroundColor());
		loading.setIcon(new SquareLoaderIcon(5, 1, Theme.current().getForegroundColor()));
		wrap.add(PanelUtils.totalCenterInPanel(loading));
		return wrap;
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public MCreatorTabs getMCreatorTabs() {
		return mcreatorTabs;
	}

	public MainMenuBar getMainMenuBar() {
		return menuBar;
	}

	public MainToolBar getToolBar() {
		return toolBar;
	}

	public DebugPanel getDebugPanel() {
		return debugPanel;
	}

}
