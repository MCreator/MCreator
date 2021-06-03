/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.blockly;

import javafx.scene.web.WebView;
import net.mcreator.Launcher;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public abstract class WebConsoleListener {

	public abstract void messageAdded(WebView webView, String message, int lineNumber, String sourceId);

	public Object instance() throws ClassNotFoundException {
		return Proxy.newProxyInstance(Launcher.class.getClassLoader(),
				new Class[] { Class.forName("com.sun.javafx.webkit.WebConsoleListener") }, (proxy, method, args) -> {
					switch (method.getName()) {
					case "messageAdded":
						messageAdded((WebView) args[0], (String) args[1], (int) args[2], (String) args[3]);
						break;
					case "equals":
						return false;
					case "hashCode":
						return 0;
					case "toString":
						return "";
					}
					return null;
				});
	}

	public static void registerLogger(Logger logger) {
		try {
			WebConsoleListener listener = new WebConsoleListener() {
				@Override public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
					String[] sidparsed = sourceId.split("/");
					logger.info(
							"[JFX JS bridge] [" + sidparsed[sidparsed.length - 1] + ": " + lineNumber + "] " + message);
				}
			};

			//noinspection JavaReflectionInvocation
			Class.forName("com.sun.javafx.webkit.WebConsoleListener")
					.getMethod("setDefaultListener", Class.forName("com.sun.javafx.webkit.WebConsoleListener"))
					.invoke(null, listener.instance());
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			logger.warn("Failed to register JavaScript console listener", e);
		}
	}

}
