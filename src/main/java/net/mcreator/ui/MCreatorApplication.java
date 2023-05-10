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

import javafx.application.Platform;
import net.mcreator.Launcher;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.io.net.analytics.DeviceInfo;
import net.mcreator.io.net.analytics.GoogleAnalytics;
import net.mcreator.io.net.api.D8WebAPI;
import net.mcreator.io.net.api.IWebAPI;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.api.ModAPIManager;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.plugin.events.ApplicationLoadedEvent;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.component.util.DiscordClient;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.help.HelpLoader;
import net.mcreator.ui.init.*;
import net.mcreator.ui.laf.MCreatorLookAndFeel;
import net.mcreator.ui.workspace.selector.RecentWorkspaceEntry;
import net.mcreator.ui.workspace.selector.WorkspaceSelector;
import net.mcreator.util.MCreatorVersionNumber;
import net.mcreator.util.SoundUtils;
import net.mcreator.workspace.CorruptedWorkspaceFileException;
import net.mcreator.workspace.UnsupportedGeneratorException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public final class MCreatorApplication {

	private static final Logger LOG = LogManager.getLogger("Application");

	public static IWebAPI WEB_API = new D8WebAPI();
	public static final String SERVER_DOMAIN = "https://mcreator.net";
	public static boolean isInternet = true;

	private static boolean applicationStarted = false;

	private final List<MCreator> openMCreators = new ArrayList<>();

	private WorkspaceSelector workspaceSelector;
	private DeviceInfo deviceInfo;
	private GoogleAnalytics analytics;
	private DiscordClient discordClient;
	private TaskbarIntegration taskbarIntegration;

	private MCreatorApplication(List<String> launchArguments) {
		final SplashScreen splashScreen = new SplashScreen();

		new Thread(() -> {
			splashScreen.setProgress(5, "Loading plugins");

			PluginLoader.initInstance();

			MCREvent.event(new ApplicationLoadedEvent(this));

			// As plugins are now loaded, preferences from Java plugins can now be loaded
			PreferencesManager.initNonCore();

			splashScreen.setProgress(10, "Loading UI Themes");

			// We load UI themes now as theme plugins are loaded at this point
			ThemeLoader.initUIThemes();

			splashScreen.setProgress(15, "Loading UI core");

			UIRES.preloadImages();

			try {
				UIManager.setLookAndFeel(new MCreatorLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				LOG.error("Failed to set look and feel: " + e.getMessage());
			}

			SoundUtils.initSoundSystem();

			taskbarIntegration = new TaskbarIntegration();

			splashScreen.setProgress(25, "Loading interface components");

			// preload help entries cache
			HelpLoader.preloadCache();

			// load translations after plugins are loaded
			L10N.initTranslations();

			splashScreen.setProgress(35, "Loading plugin data");

			// load datalists and icons for them after plugins are loaded
			BlockItemIcons.init();
			DataListLoader.preloadCache();

			splashScreen.setProgress(45, "Building plugin cache");

			// load templates for image makers
			ImageMakerTexturesCache.init();
			ArmorMakerTexturesCache.init();

			splashScreen.setProgress(55, "Loading plugin templates");

			// load apis defined by plugins after plugins are loaded
			ModAPIManager.initAPIs();

			// load variable elements
			VariableTypeLoader.loadVariableTypes();

			// load special files for Blockly
			BlocklyJavaScriptsLoader.init();
			BlocklyToolboxesLoader.init();

			// load blockly blocks after plugins are loaded
			BlocklyLoader.init();

			// load entity animations for the Java Model animation editor
			EntityAnimationsLoader.init();

			// register mod element types
			ModElementTypeLoader.loadModElements();

			splashScreen.setProgress(60, "Preloading resources");
			TiledImageCache.loadAndTileImages();

			splashScreen.setProgress(70, "Loading generators");

			MCREvent.event(new PreGeneratorsLoadingEvent(this));

			Set<String> fileNamesUnordered = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));
			List<String> fileNames = new ArrayList<>(fileNamesUnordered);
			Collections.sort(fileNames);
			int i = 0;
			for (String generator : fileNames) {
				splashScreen.setProgress(70 + i * ((90 - 70) / fileNames.size()),
						"Loading generators: " + generator.split("/")[0]);
				LOG.info("Loading generator: " + generator);
				generator = generator.replace("/generator.yaml", "");
				try {
					Generator.GENERATOR_CACHE.put(generator, new GeneratorConfiguration(generator));
				} catch (Exception e) {
					LOG.error("Failed to load generator: " + generator, e);
				}
				i++;
			}

			splashScreen.setProgress(93, "Initiating user session");

			deviceInfo = new DeviceInfo();
			analytics = new GoogleAnalytics(deviceInfo);
			analytics.trackPage(AnalyticsConstants.PAGE_LAUNCH);

			isInternet = MCreatorApplication.WEB_API.initAPI();

			discordClient = new DiscordClient();

			// Do not externalize this text
			discordClient.updatePresence("Just opened", "Version " + Launcher.version.getMajorString());

			splashScreen.setProgress(100, "Loading MCreator windows");

			try {
				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT))
					Desktop.getDesktop().setAboutHandler(aboutEvent -> AboutAction.showDialog(null));

				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_PREFERENCES))
					Desktop.getDesktop().setPreferencesHandler(preferencesEvent -> new PreferencesDialog(null, null));

				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER))
					Desktop.getDesktop().setQuitHandler((e, response) -> MCreatorApplication.this.closeApplication());
			} catch (Exception e) {
				LOG.warn("Failed to register desktop handlers", e);
			}

			SwingUtilities.invokeLater(() -> {
				workspaceSelector = new WorkspaceSelector(this, this::openWorkspaceInMCreator);

				boolean directLaunch = false;
				if (launchArguments.size() > 0) {
					String lastArg = launchArguments.get(launchArguments.size() - 1);
					if (lastArg.length() >= 2 && lastArg.charAt(0) == '"'
							&& lastArg.charAt(lastArg.length() - 1) == '"')
						lastArg = lastArg.substring(1, lastArg.length() - 1);
					File passedFile = new File(lastArg);
					if (passedFile.isFile() && passedFile.getName().endsWith(".mcreator")) {
						splashScreen.setVisible(false);
						MCreator mcreator = openWorkspaceInMCreator(passedFile);
						StartupNotifications.handleStartupNotifications(mcreator);
						directLaunch = true;
					}
				}

				if (!directLaunch)
					showWorkspaceSelector();

				splashScreen.setVisible(false);
			});

			LOG.debug("Application loader finished");
		}, "Application-Loader").start();
	}

	public GoogleAnalytics getAnalytics() {
		return analytics;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public static void createApplication(List<String> arguments) {
		if (!applicationStarted) {
			SwingUtilities.invokeLater(() -> new MCreatorApplication(arguments));
			applicationStarted = true;
		}
	}

	public WorkspaceSelector getWorkspaceSelector() {
		return workspaceSelector;
	}

	/**
	 * @param workspaceFile File of the .mcreator workspace definition
	 * @return MCreator if new instance, null if existing is open or open failed
	 */
	public MCreator openWorkspaceInMCreator(File workspaceFile) {
		this.workspaceSelector.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Workspace workspace = null;
		try {
			workspace = Workspace.readFromFS(workspaceFile, this.workspaceSelector);
			if (workspace.getMCreatorVersion() > Launcher.version.versionlong
					&& !MCreatorVersionNumber.isBuildNumberDevelopment(workspace.getMCreatorVersion())) {
				ThreadUtil.runOnSwingThreadAndWait(() -> JOptionPane.showMessageDialog(workspaceSelector,
						L10N.t("dialog.workspace.open_failed_message"), L10N.t("dialog.workspace.open_failed_title"),
						JOptionPane.ERROR_MESSAGE));
			} else {
				AtomicReference<MCreator> openResult = new AtomicReference<>(null);

				Workspace finalWorkspace = workspace;
				ThreadUtil.runOnSwingThreadAndWait(() -> {
					MCreator mcreator = new MCreator(this, finalWorkspace);
					if (!this.openMCreators.contains(mcreator)) {
						this.workspaceSelector.setVisible(false);
						this.openMCreators.add(mcreator);
						mcreator.setVisible(true);
						mcreator.requestFocusInWindow();
						mcreator.toFront();
						analytics.trackPage(AnalyticsConstants.PAGE_WORKSPACE_OPEN);
						openResult.set(mcreator);
					} else { // already open, just focus it
						LOG.warn("Trying to open already open workspace, bringing it to the front.");
						for (MCreator openmcreator : openMCreators) {
							if (openmcreator.equals(mcreator)) {
								openmcreator.requestFocusInWindow();
								openmcreator.toFront();
							}
						}
					}
				});

				return openResult.get();
			}
		} catch (CorruptedWorkspaceFileException corruptedWorkspaceFile) {
			LOG.fatal("Failed to open workspace!", corruptedWorkspaceFile);

			File backupsDir = new File(workspaceFile.getParentFile(), ".mcreator/workspaceBackups");
			if (backupsDir.isDirectory()) {
				String[] files = backupsDir.list();
				if (files != null) {
					ThreadUtil.runOnSwingThreadAndWait(() -> {
						String[] backups = Arrays.stream(files).filter(e -> e.contains(".mcreator-backup"))
								.sorted(Collections.reverseOrder()).toArray(String[]::new);
						String selected = (String) JOptionPane.showInputDialog(this.workspaceSelector,
								L10N.t("dialog.workspace.got_corrupted_message"),
								L10N.t("dialog.workspace.got_corrupted_title"), JOptionPane.QUESTION_MESSAGE, null,
								backups, "");
						if (selected != null) {
							File backup = new File(backupsDir, selected);
							FileIO.copyFile(backup, workspaceFile);
							openWorkspaceInMCreator(workspaceFile);
						} else {
							reportFailedWorkspaceOpen(new IOException("User canceled workspace backup restoration",
									corruptedWorkspaceFile));
						}
					});
				}
			} else {
				reportFailedWorkspaceOpen(
						new IOException("Corrupted workspace file and no backups found", corruptedWorkspaceFile));
			}
		} catch (IOException | UnsupportedGeneratorException e) {
			reportFailedWorkspaceOpen(e);
		} finally {
			if (workspace != null) {
				this.workspaceSelector.addOrUpdateRecentWorkspace(
						new RecentWorkspaceEntry(workspace, workspaceFile, Launcher.version.getFullString()));
			}
			this.workspaceSelector.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		return null;
	}

	private void reportFailedWorkspaceOpen(Exception e) {
		ThreadUtil.runOnSwingThreadAndWait(() -> JOptionPane.showMessageDialog(this.workspaceSelector,
				L10N.t("dialog.workspace.is_not_valid_message") + e.getMessage(),
				L10N.t("dialog.workspace.is_not_valid_title"), JOptionPane.ERROR_MESSAGE));
	}

	public void closeApplication() {
		LOG.debug("Closing any potentially open MCreator windows");

		ThreadUtil.runOnSwingThreadAndWait(() -> {
			// create list copy, so we don't modify the list we iterate
			List<MCreator> mcreatorsTmp = new ArrayList<>(openMCreators);
			for (MCreator mcreator : mcreatorsTmp) {
				LOG.info("Attempting to close MCreator window with workspace: " + mcreator.getWorkspace());
				if (!mcreator.closeThisMCreator(false))
					return; // if we fail to close all windows, we cancel the application close
			}
		});

		LOG.debug("Performing exit tasks");
		PreferencesManager.savePreferences(); // store any potential preferences changes
		analytics.trackPageSync(AnalyticsConstants.PAGE_CLOSE); // track app close in sync mode

		discordClient.close(); // close discord client

		SoundUtils.close();

		// we close all windows and exit fx platform
		try {
			LOG.debug("Stopping AWT and FX threads");
			Arrays.stream(Window.getWindows()).forEach(Window::dispose);
			Platform.exit();
		} catch (Exception ignored) {
		}

		try {
			PluginLoader.INSTANCE.close();
		} catch (IOException e) {
			LOG.warn("Failed to close plugin loader", e);
		}

		try {
			Thread.sleep(1000); // additional sleep for more robustness
		} catch (Exception ignored) {
		}

		LOG.debug("Exiting MCreator");
		System.exit(0); // actually exit MCreator
	}

	void showWorkspaceSelector() {
		workspaceSelector.setVisible(true);

		StartupNotifications.handleStartupNotifications(workspaceSelector);
	}

	List<RecentWorkspaceEntry> getRecentWorkspaces() {
		return workspaceSelector.getRecentWorkspaces().getList();
	}

	public List<MCreator> getOpenMCreators() {
		return openMCreators;
	}

	public DiscordClient getDiscordClient() {
		return discordClient;
	}

	public TaskbarIntegration getTaskbarIntegration() {
		return taskbarIntegration;
	}

}
