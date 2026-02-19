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
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.browser.WorkspaceFileBrowser;
import net.mcreator.ui.component.CollapsibleDockPanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.workspace.WorkspaceGeneratorSetupDialog;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.search.GlobalSearchListener;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.variants.resourcepackmaker.ResourcePackMaker;
import net.mcreator.ui.workspace.AbstractMainWorkspacePanel;
import net.mcreator.util.MCreatorVersionNumber;
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
import java.util.Date;
import java.util.Locale;

public abstract class MCreator extends MCreatorFrame {

	private static final Logger LOG = LogManager.getLogger("MCreator");

	public static final String DOCK_CONSOLE = "console";
	public static final String DOCK_PROJECT_BROWSER = "project_browser";

	private final GradleConsole gradleConsole;

	private final WorkspaceFileBrowser workspaceFileBrowser;

	private final ActionRegistry actionRegistry;

	private final MCreatorTabs mcreatorTabs;

	private final MainMenuBar menuBar;
	private final MainToolBar toolBar;

	public final MCreatorTabs.Tab workspaceTab;

	private final CollapsibleDockPanel leftDockRegion;
	private final CollapsibleDockPanel bottomDockRegion;

	public static MCreator create(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		if (workspace.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.RESOURCEPACK) {
			return new ResourcePackMaker(application, workspace);
		} else {
			return new ModMaker(application, workspace);
		}
	}

	protected MCreator(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		super(application, workspace);
		LOG.info("Opening MCreator workspace: {}", workspace.getWorkspaceSettings().getModID());

		this.gradleConsole = new GradleConsole(this);

		this.mcreatorTabs = new MCreatorTabs();

		this.actionRegistry = new ActionRegistry(this);

		this.workspaceFileBrowser = new WorkspaceFileBrowser(this);

		this.menuBar = createMenuBar();
		this.toolBar = createToolBar();

		setTitle(WindowTitleHelper.getWindowTitle(this));

		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent arg0) {
				closeThisMCreator(false);
			}
		});

		GlobalSearchListener.install(this, () -> mcreatorTabs.getCurrentTab().getContent());

		workspaceTab = new MCreatorTabs.Tab(L10N.t("tab.workspace").toUpperCase(), createWorkspaceTabContent(),
				"Workspace", false);
		mcreatorTabs.addTab(workspaceTab);

		mcreatorTabs.addTabShownListener(tab -> {
			reloadWorkspaceTabContents();
			menuBar.refreshMenuBar();
			setTitle(WindowTitleHelper.getWindowTitle(this));
		});

		mcreatorTabs.showTabNoNotify(workspaceTab);

		workspace.getFileManager().setDataSavedListener(() -> getStatusBar().setPersistentMessage(
				L10N.t("workspace.statusbar.autosave_message", new SimpleDateFormat("HH:mm").format(new Date()))));

		leftDockRegion = new CollapsibleDockPanel(CollapsibleDockPanel.DockPosition.LEFT, mcreatorTabs);
		bottomDockRegion = new CollapsibleDockPanel(CollapsibleDockPanel.DockPosition.DOWN, leftDockRegion);

		leftDockRegion.addDock(DOCK_PROJECT_BROWSER, 280, L10N.t("dock.project_browser"), UIRES.get("16px.dock_folder"),
				workspaceFileBrowser);

		bottomDockRegion.addDock(DOCK_CONSOLE, 300, createConsoleButton(), gradleConsole);

		JToolBar outerStrip = hasBackgroundImage() ? new TransparentToolBar() : new JToolBar();
		outerStrip.setOrientation(JToolBar.VERTICAL);
		outerStrip.setFloatable(false);
		outerStrip.setBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current().getSecondAltBackgroundColor()));
		outerStrip.add(leftDockRegion.getDockStrip());
		outerStrip.add(Box.createVerticalGlue());
		outerStrip.add(bottomDockRegion.getDockStrip());

		setMainContent(PanelUtils.westAndCenterElement(outerStrip, bottomDockRegion, 0, 0));

		add("North", toolBar);

		addWindowListener(new WindowAdapter() {
			@Override public void windowOpened(WindowEvent e) {
				super.windowOpened(e);

				// Apply dock state after the window is shown
				CollapsibleDockPanel.State.apply(workspace.getWorkspaceUserSettings().leftDockState, leftDockRegion);
				CollapsibleDockPanel.State.apply(workspace.getWorkspaceUserSettings().bottomDockState, bottomDockRegion);

				// Finalize MCreator initialization when the window is fully opened
				initializeMCreator();
			}
		});

		MCREvent.event(new MCreatorLoadedEvent(this));
	}

	@Nonnull private JToggleButton createConsoleButton() {
		JToggleButton consoleButton = new JToggleButton(UIRES.get("16px.dock_console")) {

			@Override protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Color dotColor = switch (gradleConsole.getStatus()) {
					case GradleConsole.RUNNING -> new Color(0x93c54b);
					case GradleConsole.ERROR -> new Color(0xe04442);
					default -> null;
				};
				if (gradleConsole.isGradleSetupTaskRunning())
					dotColor = new Color(0x739df0);

				if (dotColor != null) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g.setColor(dotColor);
					g.fillOval(getWidth() - 11, 5, 7, 7);
				}
			}
		};
		consoleButton.setToolTipText(L10N.t("dock.console"));
		consoleButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)
					actionRegistry.buildWorkspace.doAction();
			}
		});
		return consoleButton;
	}

	protected abstract MainMenuBar createMenuBar();

	protected abstract MainToolBar createToolBar();

	protected abstract JPanel createWorkspaceTabContent();

	public abstract AbstractMainWorkspacePanel getWorkspacePanel();

	public final void reloadWorkspaceTabContents() {
		if (mcreatorTabs.getCurrentTab().equals(workspaceTab)) {
			getWorkspacePanel().reloadWorkspaceTab();
		}
	}

	private void initializeMCreator() {
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
				workspaceFullyLoaded();
				workspaceGeneratorSwitched();
			});
		}, "ME preloader").start();
	}

	public void workspaceFullyLoaded() {
	}

	/**
	 * Called every time generator is switched. Also called when MCreator is loaded for the first time.
	 */
	public void workspaceGeneratorSwitched() {
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

			workspace.getWorkspaceUserSettings().bottomDockState = CollapsibleDockPanel.State.get(bottomDockRegion);
			workspace.getWorkspaceUserSettings().leftDockState = CollapsibleDockPanel.State.get(leftDockRegion);

			mcreatorTabs.getTabs().forEach(tab -> {
				if (tab.getTabClosedListener() != null)
					tab.getTabClosedListener().tabClosed(tab);
			});

			workspace.close();

			try { // in case the window was already disposed by some other source to prevent crashes here
				dispose(); // close the window
			} catch (Exception ignored) {
			}

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

	public void showConsole() {
		bottomDockRegion.setDockVisibility(DOCK_CONSOLE, true);
	}

	public GradleConsole getGradleConsole() {
		return gradleConsole;
	}

	public WorkspaceFileBrowser getProjectBrowser() {
		return workspaceFileBrowser;
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public MCreatorTabs getTabs() {
		return mcreatorTabs;
	}

	public MainMenuBar getMainMenuBar() {
		return menuBar;
	}

	public MainToolBar getToolBar() {
		return toolBar;
	}

	public CollapsibleDockPanel getLeftDockRegion() {
		return leftDockRegion;
	}

	public CollapsibleDockPanel getBottomDockRegion() {
		return bottomDockRegion;
	}

}
