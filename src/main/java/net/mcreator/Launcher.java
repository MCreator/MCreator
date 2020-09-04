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
import net.mcreator.util.DefaultExceptionHandler;
import net.mcreator.util.LoggingOutputStream;
import net.mcreator.util.MCreatorVersionNumber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Launcher {

	public static MCreatorVersionNumber version;

	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);

		System.setProperty("log_directory", UserFolderManager.getFileFromUserFolder("").getAbsolutePath());
		if (OS.getOS() == OS.WINDOWS && !System.getProperty("java.class.path").contains("idea_rt.jar")) {
			System.setProperty("log_disable_ansi", "true");
		} else {
			System.setProperty("log_disable_ansi", "false");
		}

		final Logger LOG = LogManager.getLogger("Launcher"); // init logger after log directory is set

		System.setErr(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDERR"), Level.ERROR), true));
		System.setOut(new PrintStream(new LoggingOutputStream(LogManager.getLogger("STDOUT"), Level.INFO), true));
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

		try {
			Properties conf = new Properties();
			conf.load(Launcher.class.getResourceAsStream("/mcreator.conf"));

			version = new MCreatorVersionNumber(conf);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("Starting MCreator " + version);

		// print version of Java
		String java_spec_version = System.getProperty("java.specification.version");
		LOG.info("Java version: " + System.getProperty("java.version") + ", specification: " + java_spec_version
				+ ", VM name: " + System.getProperty("java.vm.name"));
		LOG.info("Current JAVA_HOME for running instance: " + System.getProperty("java.home"));

		// check if the user is using proper version of Java
		if (!java_spec_version.equals("1.8")) {
			LOG.info("Invalid java specification version!");
			String[] options = new String[] { "Close MCreator", "Proceed anyway" };
			int option = JOptionPane.showOptionDialog(null,
					"<html>You are using Java version that is not supported!<br>"
							+ "Only Java 1.8 (Java 8) is supported by MCreator, but detected version of Java is: <i>"
							+ java_spec_version + "</i><br>" + "<br>MCreator won't work!"
							+ "<br>You should set Java 8 as default Java version on your computer (including JAVA_HOME).",
					"Invalid java specification version", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
					options, options[0]);
			if (option == 0)
				System.exit(-1);
		}

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
			LOG.warn(
					"Running in javafx.embed.singleThread environment. This is just a note and should not cause any problems.");
		}

		// check if the user has JavaFX
		try {
			Launcher.class.getClassLoader().loadClass("javafx.embed.swing.JFXPanel");
			// if we manage to load JavaFX, we set the listener to print to the sout js messages
			com.sun.javafx.webkit.WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
				String[] sidparsed = sourceId.split("/");
				LOG.info("[JFX JS bridge] [" + sidparsed[sidparsed.length - 1] + ": " + lineNumber + "] " + message);
			});
		} catch (ClassNotFoundException e) {
			LOG.info("MCreator was not able to load JavaFX");
			String[] options = new String[] { "Close MCreator", "Proceed anyway" };
			int option = JOptionPane.showOptionDialog(null, "<html><b>MCreator was not able to load JavaFX!</b><br><br>"
							+ "MCreator can't work without JavaFX framework installed."
							+ "<br>Please check how to install JavaFX for your platform and install it before using MCreator.<br>"
							+ "<br>If you are using apt for package manager, you can install JavaFX by running:<br>"
							+ "<pre>sudo apt-get install openjfx</pre><br>"
							+ "Another option is to use Oracle JDK 8 which comes with JavaFX bundled.", "Failed to load JavaFX",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			if (option == 0)
				System.exit(-2);
		}

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
			System.exit(-3);
		}

		MCreatorApplication.createApplication(arguments);
	}

}
