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

import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.WebConsoleListener;
import net.mcreator.util.DefaultExceptionHandler;
import net.mcreator.util.LoggingOutputStream;
import net.mcreator.util.MCreatorVersionNumber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Launcher {

	public static MCreatorVersionNumber version;

	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);

		System.setProperty("log_directory", UserFolderManager.getFileFromUserFolder("").getAbsolutePath());

		if (OS.getOS() == OS.WINDOWS && ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
				.noneMatch(arg -> arg.contains("idea_rt.jar"))) {
			System.setProperty("log_disable_ansi", "true");
		} else {
			System.setProperty("log_disable_ansi", "false");
		}

		final Logger LOG = LogManager.getLogger("Launcher"); // init logger after log directory is set

		System.setErr(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDERR"), Level.ERROR), true));
		System.setOut(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDOUT"), Level.INFO), true));
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

		openModuleExports();

		try {
			Properties conf = new Properties();
			conf.load(Launcher.class.getResourceAsStream("/mcreator.conf"));

			version = new MCreatorVersionNumber(conf);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("Starting MCreator " + version);

		// print version of Java
		LOG.info("Java version: " + System.getProperty("java.version") + ", specification: " + System
				.getProperty("java.specification.version") + ", VM name: " + System.getProperty("java.vm.name"));
		LOG.info("Current JAVA_HOME for running instance: " + System.getProperty("java.home"));

		// after we have libraries loaded, we load preferences
		PreferencesManager.loadPreferences();

		// set system properties from preferences
		System.setProperty("awt.useSystemAAFontSettings", PreferencesManager.PREFERENCES.ui.textAntialiasingType);
		System.setProperty("swing.aatext", Boolean.toString(PreferencesManager.PREFERENCES.ui.aatext));
		System.setProperty("sun.java2d.opengl", Boolean.toString(PreferencesManager.PREFERENCES.ui.use2DAcceleration));
		System.setProperty("sun.java2d.d3d", "false");
		System.setProperty("prism.lcdtext", "false");

		// if the OS is macOS, we enable javafx single thread mode to avoid some deadlocks with JFXPanel
		if (OS.getOS() == OS.MAC) {
			System.setProperty("javafx.embed.singleThread", "true");
		}

		if ("true".equals(System.getProperty("javafx.embed.singleThread"))) {
			LOG.warn("Running in javafx.embed.singleThread environment. "
					+ "This is just a note and should not cause any problems.");
		}

		WebConsoleListener.registerLogger(LOG);

		// check if proper version of MCreator per architecture is used
		if (OS.getBundledJVMBits() > OS.getSystemBits())
			JOptionPane.showMessageDialog(null,
					"<html><b>You are trying to run 64 bit MCreator on the 32 bit computer.<br>"
							+ "This will not work!</b><br>" + "<br>Use 32 bit edition of MCreator instead.",
					"MCreator error", JOptionPane.WARNING_MESSAGE);

		if (OS.getSystemBits() > OS.getBundledJVMBits())
			JOptionPane.showMessageDialog(null, "<html><b>You are using 32 bit MCreator on the 64 bit computer.</b>"
							+ "<br>This is not recommended and can cause problems!"
							+ "<br><br>If you get any errors and use this combination of versions, we can't offer you support."
							+ "</b><br><br>Use 64 bit edition of MCreator instead.", "MCreator error",
					JOptionPane.WARNING_MESSAGE);

		LOG.info("Installation path: " + System.getProperty("user.dir"));
		LOG.info("User home of MCreator: " + UserFolderManager.getFileFromUserFolder("/"));
		if (!UserFolderManager.createUserFolderIfNotExists()) {
			JOptionPane.showMessageDialog(null, "<html><b>MCreator failed to write to user directory!</b><br><br>"
							+ "Please make sure that the user running MCreator has permissions to read and write to the directory<br>"
							+ "in which MCreator tried to create user specific data storage. The path MCreator could not write to is:<br><br>"
							+ UserFolderManager.getFileFromUserFolder("/") + "<br>", "MCreator file system error",
					JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}

		MCreatorApplication.createApplication(arguments);
	}

	public static void openModuleExports() {
		// Foxtrot core
		ModuleLayer.boot().findModule("java.desktop")
				.ifPresent(module -> module.addOpens("java.awt", foxtrot.pumps.ConditionalEventPump.class.getModule()));

		// MCreator theme
		ModuleLayer.boot().findModule("java.desktop").ifPresent(
				module -> module.addOpens("sun.awt", net.mcreator.ui.laf.MCreatorLookAndFeel.class.getModule()));
		ModuleLayer.boot().findModule("java.desktop").ifPresent(module -> module
				.addOpens("javax.swing.text.html", net.mcreator.ui.laf.MCreatorLookAndFeel.class.getModule()));

		// Blockly panel transparency
		ModuleLayer.boot().findModule("javafx.web").ifPresent(module -> module
				.addOpens("com.sun.javafx.webkit", net.mcreator.ui.blockly.BlocklyPanel.class.getModule()));
		ModuleLayer.boot().findModule("javafx.web").ifPresent(
				module -> module.addOpens("com.sun.webkit", net.mcreator.ui.blockly.BlocklyPanel.class.getModule()));
	}

}
