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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.browser.WorkspaceFileBrowser;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.util.EDTUtils;
import net.mcreator.ui.component.util.MacOSUIUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.ListUtils;
import net.mcreator.util.MCreatorVersionNumber;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.vcs.WorkspaceVCS;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.ShareableZIPManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public final class MCreator extends JFrame implements IWorkspaceProvider, IGeneratorProvider {

	private static final Logger LOG = LogManager.getLogger("MCreator");

	public WorkspacePanel mv;
	private final GradleConsole gradleConsole;

	private final WorkspaceFileBrowser workspaceFileBrowser;

	public final ActionRegistry actionRegistry;

	public final MCreatorTabs mcreatorTabs;
	public final StatusBar statusBar;

	public MCreatorTabs.Tab workspaceTab;
	public MCreatorTabs.Tab consoleTab;

	private final MCreatorApplication application;

	public final JSplitPane splitPane;

	private final Workspace workspace;

	private final long windowUID;

	public MCreator(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		LOG.info("Opening MCreator workspace: " + workspace.getWorkspaceSettings().getModID());

		this.windowUID = System.currentTimeMillis();
		this.workspace = workspace;
		this.application = application;

		WorkspaceVCS vcs = WorkspaceVCS.loadVCSWorkspace(this.workspace);
		if (vcs != null) {
			this.workspace.setVCS(vcs);
			LOG.info("Loaded VCS for current workspace");
		}

		this.gradleConsole = new GradleConsole(this);
		this.gradleConsole.addGradleStateListener(new GradleStateListener() {
			@Override public void taskStarted(String taskName) {
				mv.disableRemoving();
			}

			@Override public void taskFinished(GradleTaskResult result) {
				mv.enableRemoving();
			}
		});

		this.mcreatorTabs = new MCreatorTabs();

		this.actionRegistry = new ActionRegistry(this);
		this.statusBar = new StatusBar(this);

		this.workspaceFileBrowser = new WorkspaceFileBrowser(this);

		new MCreatorDropTarget(this);

		MainMenuBar menuBar = new MainMenuBar(this);
		MainToolBar toolBar = new MainToolBar(this);

		setTitle(WindowTitleHelper.getWindowTitle(this));

		setLayout(new BorderLayout(0, 0));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() > 1574 && screenSize.getHeight() > 970)
			setSize(1574, 967);
		else if (screenSize.getWidth() > 1290 && screenSize.getHeight() > 795)
			setSize(1290, 791);
		else
			setSize(1002, 640);

		if (OS.getOS() == OS.MAC)
			MacOSUIUtil.enableTrueFullscreen(this);

		if (PreferencesManager.PREFERENCES.hidden.fullScreen)
			setExtendedState(JFrame.MAXIMIZED_BOTH);

		setIconImage(UIRES.get("icon").getImage());
		setLocationRelativeTo(null);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent arg0) {
				closeThisMCreator(false);
			}
		});

		setJMenuBar(menuBar);

		mcreatorTabs.addTabShownListener(tab -> {
			if (tab.equals(workspaceTab))
				mv.updateMods();

			menuBar.refreshMenuBar();

			setTitle(WindowTitleHelper.getWindowTitle(this));
		});

		UserFolderManager.getFileFromUserFolder("backgrounds").mkdirs();

		JPanel mpan;
		File[] bgfiles = UserFolderManager.getFileFromUserFolder("backgrounds").listFiles();
		List<File> bgimages = new ArrayList<>();
		if (bgfiles != null) {
			bgimages = Arrays.stream(bgfiles).filter(e -> e.getName().endsWith(".png")).collect(Collectors.toList());
		}

		Image bgimage = null;
		if (bgimages.size() > 0) {
			try {
				bgimage = ImageIO.read(ListUtils.getRandomItem(bgimages));
				float avg = ImageUtils.getAverageLuminance(ImageUtils.toBufferedImage(bgimage));
				if (avg > 0.15) {
					avg = (float) Math.min(avg * 1.7, 0.85);
					bgimage = ImageUtils.drawOver(new ImageIcon(bgimage), new ImageIcon(ImageUtils
							.emptyImageWithSize(bgimage.getWidth(this), bgimage.getHeight(this),
									new Color(0.12f, 0.12f, 0.12f, avg)))).getImage();
				}
			} catch (IOException e) {
				LOG.warn("Failed to load background image", e);
			}
		}

		if (bgimage != null) {
			mpan = new ImagePanel(bgimage);
			((ImagePanel) mpan).setKeepRatio(true);
		} else {
			mpan = new JPanel();
			mpan.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		}

		mpan.setLayout(new BorderLayout());
		mpan.add("Center", mcreatorTabs.getContainer());

		mv = new WorkspacePanel(this);

		JPanel pon = new JPanel(new BorderLayout(0, 0));
		pon.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		pon.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));

		workspaceTab = new MCreatorTabs.Tab(L10N.t("tab.workspace"),
				PanelUtils.maxMargin(mv, 5, true, true, true, true), "Workspace", true, false);
		mcreatorTabs.addTab(workspaceTab);
		pon.add("West", workspaceTab);

		consoleTab = new MCreatorTabs.Tab(L10N.t("tab.console") + " ", gradleConsole, "Console", true, false) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				switch (gradleConsole.getStatus()) {
				case GradleConsole.READY:
					g.setColor(Color.white);
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

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, workspaceFileBrowser,
				PanelUtils.northAndCenterElement(pon, mpan));
		splitPane.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		splitPane.setOneTouchExpandable(true);

		splitPane.setDividerLocation(280);
		splitPane.setDividerLocation(PreferencesManager.PREFERENCES.hidden.projectTreeSplitPos);

		workspaceFileBrowser.setMinimumSize(new Dimension(0, 0));

		add("South", statusBar);
		add("North", toolBar);
		add("Center", splitPane);
	}

	@Override public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));

			EDTUtils.requestNonBlockingUIRefresh();

			if (MCreatorVersionNumber.isBuildNumberDevelopment(workspace.getMCreatorVersion())) {
				workspace.setMCreatorVersion(
						Launcher.version.versionlong); // if we open dev version, store new version number in it
			}

			new Thread(this.workspaceFileBrowser::reloadTree).start();

			// backup if new version and backups are enabled
			if (workspace.getMCreatorVersion() < Launcher.version.versionlong
					&& PreferencesManager.PREFERENCES.backups.backupOnVersionSwitch) {
				ShareableZIPManager.exportZIP("Workspace backup",
						new File(workspace.getFolderManager().getWorkspaceCacheDir(),
								"FullBackup" + workspace.getMCreatorVersion() + ".zip"), this, true);
			}

			// if we need to setup MCreator, we do so
			if (WorkspaceGeneratorSetup.shouldSetupBeRan(workspace.getGenerator())) {
				WorkspaceGeneratorSetupDialog
						.runSetup(this, PreferencesManager.PREFERENCES.notifications.openWhatsNextPage);
			}

			if (workspace.getMCreatorVersion()
					< Launcher.version.versionlong) { // if this is the case, update the workspace files
				RegenerateCodeAction.regenerateCode(this, true, true);
				workspace.setMCreatorVersion(Launcher.version.versionlong);
				workspace.getFileManager().saveWorkspaceDirectlyAndWait();
			} else if (workspace.isRegenerateRequired()) {
				RegenerateCodeAction.regenerateCode(this, true, true);
			}

			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public final boolean closeThisMCreator(boolean returnToProjectSelector) {
		boolean safetoexit = gradleConsole.getStatus() != GradleConsole.RUNNING;
		if (!safetoexit) {
			if (gradleConsole.isGradleSetupTaskRunning()) {
				JOptionPane.showMessageDialog(this, L10N.t("action.gradle.close_mcreator_while_installation_message"),
						L10N.t("action.gradle.close_mcreator_while_installation_title"), JOptionPane.WARNING_MESSAGE);
				return false;
			}

			int reply = JOptionPane
					.showConfirmDialog(this, L10N.t("action.gradle.close_mcreator_while_running_message"),
							L10N.t("action.gradle.close_mcreator_while_running_title"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, null);
			if (reply == JOptionPane.YES_OPTION) {
				safetoexit = true;
				gradleConsole.cancelTask();
			}
		}

		if (safetoexit) {
			LOG.info("Closing MCreator window ...");
			PreferencesManager.PREFERENCES.hidden.fullScreen = getExtendedState() == MAXIMIZED_BOTH;
			if (splitPane != null)
				PreferencesManager.PREFERENCES.hidden.projectTreeSplitPos = splitPane
						.getDividerLocation(); // this one could be stored per workspace in the future

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

			application.getDiscordClient()
					.updatePresence("Working on " + workspace.getWorkspaceSettings().getModName() + tabAddition,
							Launcher.version.getMajorString() + " for " + workspace.getGenerator()
									.getGeneratorMinecraftVersion(),
							"type-" + workspace.getGeneratorConfiguration().getGeneratorFlavor().name()
									.toLowerCase(Locale.ENGLISH));
		}
	}

	@Override public boolean equals(Object mcreator) {
		if (mcreator instanceof MCreator) {
			MCreator theothermcreator = (MCreator) mcreator;
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

}
