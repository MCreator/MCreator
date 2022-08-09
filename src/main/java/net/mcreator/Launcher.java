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

package net.mcreator;

import javafx.embed.swing.JFXPanel;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.WebConsoleListener;
import net.mcreator.ui.workspace.selector.WorkspaceSelector;
import net.mcreator.util.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Launcher {

	public static MCreatorVersionNumber version;

	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);

		System.setProperty("jna.nosys", "true");
		System.setProperty("log_directory",System.getProperty("user.dir"));

		if (OS.getOS() == OS.WINDOWS && ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
				.noneMatch(arg -> arg.contains("idea_rt.jar"))) {
			System.setProperty("log_disable_ansi", "true");
		} else {
			System.setProperty("log_disable_ansi", "false");
		}

		final Logger LOG = LogManager.getLogger("Launcher"); // init logger after log directory is set
		LOG.info("温馨提醒一下日志文件是否过多");

		File logs = new File(System.getProperty("user.dir"),"logs");
		File[] logsList = logs .listFiles(a -> !"mcreator.log".equals(a.getName()));
		assert logsList != null;
		if (logsList.length >= 10){
			int stat = JOptionPane.showConfirmDialog(null,"检测到您得日志文件超过10个,是否清空?","日志清理提醒",JOptionPane.YES_NO_OPTION);
			if (stat == JOptionPane.YES_OPTION){
				Arrays.stream(logsList).forEach(File::delete);
			}
		}

		System.setErr(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDERR"), Level.ERROR), true));
		System.setOut(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDOUT"), Level.INFO), true));
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

		TerribleModuleHacks.openAllUnnamed();
		TerribleModuleHacks.openMCreatorRequirements();

		UTF8Forcer.forceGlobalUTF8();

		try {
			//载入mcrc的版本文件
			Properties conf = new Properties();
			conf.load(Launcher.class.getResourceAsStream("/mcrc.conf"));
			version = new MCreatorVersionNumber(conf);

			if (Boolean.parseBoolean(conf.getProperty("debug"))){
				LOG.info("debug模式为开启状态,等待30秒钟来让外部debug工具接入");
				Thread.sleep(30000L);
			}
		} catch (IOException e) {
			LOG.error("无法载入MCreator-Chinese的内部配置文件", e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		LOG.info("Starting MCreator " + version);

		// print version of Java
		LOG.info("Java版本: " + System.getProperty("java.version") + ", VM: " + System.getProperty("java.vm.name")
				+ ", 供应商: " + System.getProperty("java.vendor"));
		LOG.info("JAVA_HOME: " + System.getProperty("java.home"));

		// after we have libraries loaded, we load preferences
		PreferencesManager.loadPreferences();

		// set system properties from preferences
		System.setProperty("apple.laf.useScreenMenuBar",
				Boolean.toString(PreferencesManager.PREFERENCES.ui.usemacOSMenuBar));
		System.setProperty("awt.useSystemAAFontSettings", PreferencesManager.PREFERENCES.ui.textAntialiasingType);
		System.setProperty("swing.aatext", Boolean.toString(PreferencesManager.PREFERENCES.ui.aatext));
		System.setProperty("sun.java2d.opengl", Boolean.toString(PreferencesManager.PREFERENCES.ui.use2DAcceleration));
		System.setProperty("sun.java2d.d3d", "false");
		System.setProperty("prism.lcdtext", "false");

		// if the OS is macOS, we enable javafx single thread mode to avoid some deadlocks(死锁) with JFXPanel
		if (OS.getOS() == OS.MAC) {
			System.setProperty("javafx.embed.singleThread", "true");
		}

		if ("true".equals(System.getProperty("javafx.embed.singleThread"))) {
			LOG.warn("Running in javafx.embed.singleThread environment. "
					+ "This is just a note and should not cause any problems.");
		}

		// Init JFX Toolkit
		try {
			SwingUtilities.invokeAndWait(JFXPanel::new);
		} catch (InterruptedException | InvocationTargetException e) {
			LOG.error("无法启动JFX toolkit", e);
		}

		WebConsoleListener.registerLogger(LOG);

		// check if proper version of MCreator per architecture is used
		if (OS.getSystemBits() == OS.BIT32) {
			JOptionPane.showMessageDialog(null, "<html>我们很抱歉的通知您MCreator不支持在32位的电脑上运行.<br>"
							+ "我们也将永远不会对32位做出兼容  cdc:我不知道为什么,总之就是不行(?????)"
					, "MCreator错误", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}

		LOG.info("安装目录: " + System.getProperty("user.dir"));
		LOG.info("MCreator的用户目录: " + UserFolderManager.getFileFromUserFolder("/"));

		if (!UserFolderManager.createUserFolderIfNotExists()) {
			JOptionPane.showMessageDialog(null, "<html><b>MCreator无法写入用户目录！</b><br><br>"
							+ "请确保运行MCreator的用户具有读取和写入目录的权限<br>"
							+ "McCreator尝试创建用户特定的数据存储但是失败了。MCreator无法写入的路径为：<br><br>"
							+ UserFolderManager.getFileFromUserFolder("/") + "<br>", "MCreator文件系统错误",
					JOptionPane.WARNING_MESSAGE);
			System.exit(-2);
		}

		MCreatorApplication.createApplication(arguments);
	}

}
