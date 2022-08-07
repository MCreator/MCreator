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
import net.mcreator.io.net.analytics.Analytics;
import net.mcreator.io.net.analytics.DeviceInfo;
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
import net.mcreator.ui.dialogs.UpdateNotifyDialog;
import net.mcreator.ui.dialogs.UpdatePluginDialog;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public final class MCreatorApplication {

	private static MCreatorApplication application;

	public static void exit(boolean restart){
		application.closeApplication(restart);
	}

	private static final Logger LOG = LogManager.getLogger("Application");

	public static IWebAPI WEB_API = new D8WebAPI();
	public static final String SERVER_DOMAIN = "https://mcreator.net";
	public static boolean isInternet = true;

	private final Analytics analytics;
	private final DeviceInfo deviceInfo;
	private static boolean applicationStarted = false;
	private WorkspaceSelector workspaceSelector;

	private final List<MCreator> openMCreators = new ArrayList<>();

	private final DiscordClient discordClient;

	private final TaskbarIntegration taskbarIntegration;

	private MCreatorApplication(List<String> launchArguments) {

		final SplashScreen splashScreen = new SplashScreen();
		splashScreen.setVisible(true);

		splashScreen.setProgress(5, "正在载入插件");

		// Plugins are loaded before the Splash screen is visible, so every image can be changed
		PluginLoader.initInstance();

		MCREvent.event(new ApplicationLoadedEvent(this));

		splashScreen.setProgress(10, "正在载入界面主题");

		// We load UI themes now as theme plugins are loaded at this point
		ThemeLoader.initUIThemes();

		splashScreen.setProgress(15, "正在载入UI核心");

		UIRES.preloadImages();

		try {
			UIManager.setLookAndFeel(new MCreatorLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			LOG.error("无法设置观感: " + e.getMessage());
		}

		SoundUtils.initSoundSystem();

		taskbarIntegration = new TaskbarIntegration();

		splashScreen.setProgress(25, "加载接口组件");

		// 预加载帮助数据
		HelpLoader.preloadCache();

		// load translations after plugins are loaded
		L10N.initTranslations();

		splashScreen.setProgress(35, "加载插件数据");

		// load datalists and icons for them after plugins are loaded
		BlockItemIcons.init();
		DataListLoader.preloadCache();

		splashScreen.setProgress(45, "构建插件缓存");

		// load templates for image makers
		ImageMakerTexturesCache.init();
		ArmorMakerTexturesCache.init();

		splashScreen.setProgress(55, "加载插件模板");

		// load apis defined by plugins after plugins are loaded
		ModAPIManager.initAPIs();

		// load variable elements
		VariableTypeLoader.loadVariableTypes();

		// load JS files for Blockly
		BlocklyJavaScriptsLoader.init();

		// load blockly blocks after plugins are loaded
		BlocklyLoader.init();

		// load entity animations for the Java Model animation editor
		EntityAnimationsLoader.init();

		// register mod element types
		ModElementTypeLoader.loadModElements();

		splashScreen.setProgress(60, "预加载资源");
		TiledImageCache.loadAndTileImages();

		splashScreen.setProgress(70, "加载生成器中");

		MCREvent.event(new PreGeneratorsLoadingEvent(this));

		Set<String> fileNamesUnordered = PluginLoader.INSTANCE.getResources(Pattern.compile("generator\\.yaml"));
		List<String> fileNames = new ArrayList<>(fileNamesUnordered);
		Collections.sort(fileNames);
		int i = 0;
		for (String generator : fileNames) {
			splashScreen.setProgress(70 + i * ((90 - 70) / fileNames.size()),
					"正在加载生成器: " + generator.split("/")[0]);
			LOG.info("加载生成器中: " + generator);
			generator = generator.replace("/generator.yaml", "");
			try {
				Generator.GENERATOR_CACHE.put(generator, new GeneratorConfiguration(generator));
			} catch (Exception e) {
				LOG.error("无法载入生成器: " + generator, e);
			}
			i++;
		}

		splashScreen.setProgress(93, "正在启动用户会话");

		deviceInfo = new DeviceInfo();
		analytics = new Analytics(deviceInfo);

		LOG.info("正在于官网联系");
		isInternet = MCreatorApplication.WEB_API.initAPI();

		// we do async login attempt
		LOG.info("正在检查是否要更新");
		UpdateNotifyDialog.showUpdateDialogIfUpdateExists(splashScreen, false);
		LOG.info("正在检查是否有插件更新");
		UpdatePluginDialog.showPluginUpdateDialogIfUpdatesExist(splashScreen);

		splashScreen.setProgress(100, "正在加载MCreator窗口");

		try {
			if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT))
				Desktop.getDesktop().setAboutHandler(aboutEvent -> AboutAction.showDialog(null));

			if (Desktop.getDesktop().isSupported(Desktop.Action.APP_PREFERENCES))
				Desktop.getDesktop().setPreferencesHandler(preferencesEvent -> new PreferencesDialog(null, null));

			if (Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER))
				Desktop.getDesktop().setQuitHandler((e, response) -> MCreatorApplication.this.closeApplication(false));
		} catch (Exception e) {
			LOG.warn("无法注册desktop handlers", e);
		}

		discordClient = new DiscordClient();

		if (Launcher.version.isSnapshot() && PreferencesManager.PREFERENCES.notifications.snapshotMessage) {
			JOptionPane.showMessageDialog(splashScreen, L10N.t("action.eap_loading.text"),
					L10N.t("action.eap_loading.title"), JOptionPane.WARNING_MESSAGE);
		}

		discordClient.updatePresence(L10N.t("dialog.discord_rpc.just_opened"),
				L10N.t("dialog.discord_rpc.version") + Launcher.version.getMajorString());

/*		//工程选择
		workspaceSelector = new WorkspaceSelector(this, this::openWorkspaceInMCreator);*/

		boolean directLaunch = false;
		if (launchArguments.size() > 0) {
			String lastArg = launchArguments.get(launchArguments.size() - 1);
			if (lastArg.length() >= 2 && lastArg.charAt(0) == '"' && lastArg.charAt(lastArg.length() - 1) == '"')
				lastArg = lastArg.substring(1, lastArg.length() - 1);
			File passedFile = new File(lastArg);
			if (passedFile.isFile() && passedFile.getName().endsWith(".mcreator")) {
				splashScreen.setVisible(false);
				openWorkspaceInMCreator(passedFile);
				directLaunch = true;
			}
		}

		if (!directLaunch) {
			showWorkspaceSelector();
		}

		splashScreen.setVisible(false);

		//track after the setup is done
		analytics.async(analytics::trackMCreatorLaunch);


	}

	public Analytics getAnalytics() {
		return analytics;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public static void createApplication(List<String> arguments) {
		if (!applicationStarted) {
			SwingUtilities.invokeLater(() ->
					application = new MCreatorApplication(arguments)
			);
			applicationStarted = true;
		}
	}

	public WorkspaceSelector getWorkspaceSelector() {
		return workspaceSelector;
	}

	public void openWorkspaceInMCreator(File workspaceFile) {
		this.workspaceSelector.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			Workspace workspace = Workspace.readFromFS(workspaceFile, this.workspaceSelector);
			if (workspace.getMCreatorVersion() > Launcher.version.versionlong
					&& !MCreatorVersionNumber.isBuildNumberDevelopment(workspace.getMCreatorVersion())) {
				JOptionPane.showMessageDialog(workspaceSelector, L10N.t("dialog.workspace.open_failed_message"),
						L10N.t("dialog.workspace.open_failed_title"), JOptionPane.ERROR_MESSAGE);
			} else {
				MCreator mcreator = new MCreator(this, workspace);
				this.workspaceSelector.setVisible(false);
				if (!this.openMCreators.contains(mcreator)) {
					this.openMCreators.add(mcreator);
					mcreator.setVisible(true);
					mcreator.requestFocusInWindow();
					mcreator.toFront();
				} else { // already open, just focus it
					LOG.warn("检测到你尝试打开已打开的工作区，我们将会把它置于最前面.");
					for (MCreator openmcreator : openMCreators) {
						if (openmcreator.equals(mcreator)) {
							openmcreator.requestFocusInWindow();
							openmcreator.toFront();
						}
					}
				}
				this.workspaceSelector.addOrUpdateRecentWorkspace(
						new RecentWorkspaceEntry(mcreator.getWorkspace(), workspaceFile));
			}
		} catch (CorruptedWorkspaceFileException corruptedWorkspaceFile) {
			LOG.fatal("无法打开工作目录!", corruptedWorkspaceFile);

			File backupsDir = new File(workspaceFile.getParentFile(), ".mcreator/workspaceBackups");
			if (backupsDir.isDirectory()) {
				String[] files = backupsDir.list();
				if (files != null) {
					String[] backups = Arrays.stream(files).filter(e -> e.contains(".mcreator-backup"))
							.sorted(Collections.reverseOrder()).toArray(String[]::new);
					String selected = (String) JOptionPane.showInputDialog(this.workspaceSelector,
							L10N.t("dialog.workspace.got_corrupted_message"),
							L10N.t("dialog.workspace.got_corrupted_title"), JOptionPane.QUESTION_MESSAGE, null, backups,
							"");
					if (selected != null) {
						File backup = new File(backupsDir, selected);
						FileIO.copyFile(backup, workspaceFile);
						openWorkspaceInMCreator(workspaceFile);
					} else {
						reportFailedWorkspaceOpen(
								new IOException("User canceled workspace backup restoration", corruptedWorkspaceFile));
					}
				}
			} else {
				reportFailedWorkspaceOpen(
						new IOException("Corrupted workspace file and no backups found", corruptedWorkspaceFile));
			}
		} catch (IOException | UnsupportedGeneratorException e) {
			reportFailedWorkspaceOpen(e);
		}
		this.workspaceSelector.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	private void reportFailedWorkspaceOpen(Exception e) {
		JOptionPane.showMessageDialog(this.workspaceSelector,
				L10N.t("dialog.workspace.is_not_valid_message") + e.getMessage(),
				L10N.t("dialog.workspace.is_not_valid_title"), JOptionPane.ERROR_MESSAGE);
	}

	public void closeApplication(boolean restart) {
		LOG.debug("关闭任何打开的MCreator窗口");
		List<MCreator> mcreatorsTmp = new ArrayList<>(
				openMCreators); // create list copy so we don't modify the list we iterate
		for (MCreator mcreator : mcreatorsTmp) {
			LOG.info("试图关闭工作区类型的MCreator窗口: " + mcreator.getWorkspace());
			if (!mcreator.closeThisMCreator(false)) {
				int stat = JOptionPane.showConfirmDialog(null, "是否强行退出MCreator?", "强制退出提醒", JOptionPane.YES_NO_OPTION);
				if (stat == JOptionPane.NO_OPTION) {
					return; // if we fail to close all windows, we cancel the application close
				}
			}
		}

		LOG.debug("执行退出任务");
		PreferencesManager.storePreferences(PreferencesManager.PREFERENCES); // store any potential preferences changes
		analytics.trackMCreatorClose(); // track app close in sync mode

		discordClient.close(); // close discord client

		SoundUtils.close();

		// we close all windows and exit fx platform
		try {
			LOG.debug("正在停止AWT和FX的线程");
			Arrays.stream(Window.getWindows()).forEach(Window::dispose);
			Platform.exit();
		} catch (Exception ignored) {
		}

		try {
			PluginLoader.INSTANCE.close();
			LOG.info("插件载入器关闭成功");
		} catch (IOException e) {
			LOG.warn("无法关闭插件载入器", e);
		}

		if (restart){
			LOG.info("执行重启");
			String restartCommand = "mcreator.exe";
			if (Files.exists(Paths.get(restartCommand))) {
				try {
					Runtime.getRuntime().exec(restartCommand);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LOG.info("正在退出MCreator");
		System.exit(-1);
	}

	void showWorkspaceSelector() {
		workspaceSelector = Optional.ofNullable(workspaceSelector).orElse(new WorkspaceSelector(this::openWorkspaceInMCreator));
		workspaceSelector.setVisible(true);
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
