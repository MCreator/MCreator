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

package net.mcreator.ui.component.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MacOSUIUtil {

	private static final Logger LOG = LogManager.getLogger("macOS Util");

	public static void enableTrueFullscreen(JFrame frame) {
		try {
			Class.forName("com.apple.eawt.FullScreenUtilities")
					.getMethod("setWindowCanFullScreen", Window.class, boolean.class).invoke(null, frame, true);
		} catch (Throwable e) {
			LOG.error("Full screen mode is not supported");
		}
	}

	public static void registerAboutHandler(MRJAboutHandler aboutHandler) {
		try {
			Class<?> someInterface = Class.forName("com.apple.mrj.MRJAboutHandler");
			Object instance = Proxy.newProxyInstance(someInterface.getClassLoader(), new Class<?>[] { someInterface },
					(proxy, method, args) -> {
						if (method.getName().equals("handleAbout"))
							aboutHandler.handleAbout();
						return null;
					});
			Method[] methods = Class.forName("com.apple.mrj.MRJApplicationUtils").getMethods();
			for (Method method : methods) {
				if (method.getName().equals("registerAboutHandler"))
					method.invoke(null, instance);
			}
		} catch (Throwable e) {
			LOG.error("Failed to register about handler");
		}
	}

	public static void registerQuitHandler(MRJQuitHandler quitHandler) {
		try {
			Class<?> someInterface = Class.forName("com.apple.mrj.MRJQuitHandler");
			Object instance = Proxy.newProxyInstance(someInterface.getClassLoader(), new Class<?>[] { someInterface },
					(proxy, method, args) -> {
						if (method.getName().equals("handleQuit"))
							quitHandler.handleQuit();
						return null;
					});
			Method[] methods = Class.forName("com.apple.mrj.MRJApplicationUtils").getMethods();
			for (Method method : methods) {
				if (method.getName().equals("registerQuitHandler"))
					method.invoke(null, instance);
			}
		} catch (Throwable e) {
			LOG.error("Failed to register quit handler");
		}
	}

	public static void registerPreferencesHandler(MRJPrefsHandler prefsHandler) {
		try {
			Class<?> someInterface = Class.forName("com.apple.mrj.MRJPrefsHandler");
			Object instance = Proxy.newProxyInstance(someInterface.getClassLoader(), new Class<?>[] { someInterface },
					(proxy, method, args) -> {
						if (method.getName().equals("handlePrefs"))
							prefsHandler.handlePrefs();
						return null;
					});
			Method[] methods = Class.forName("com.apple.mrj.MRJApplicationUtils").getMethods();
			for (Method method : methods) {
				if (method.getName().equals("registerPrefsHandler"))
					method.invoke(null, instance);
			}
		} catch (Throwable e) {
			LOG.error("Failed to register prefs handler");
		}
	}

	public interface MRJAboutHandler {
		void handleAbout();
	}

	public interface MRJPrefsHandler {
		void handlePrefs() throws IllegalStateException;
	}

	public interface MRJQuitHandler {
		void handleQuit() throws IllegalStateException;
	}

}
