/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.blockly.cef;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import net.mcreator.io.UserFolderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;

public class CEFUtils {

	private static final Logger LOG = LogManager.getLogger("CEF");

	private static CefApp cefApp = null;
	private static CefClient cefClient = null;

	private static final CefMultiLoadHandler cefMultiLoadHandler = new CefMultiLoadHandler();

	private static CefApp getCEFApp() {
		if (cefApp == null) {
			cefApp = createApp();
		}

		return cefApp;
	}

	public static void close() {
		if (cefClient != null) {
			cefClient.dispose();
			cefClient = null;
		}
		if (cefApp != null) {
			cefApp.dispose();
			cefApp = null;
		}
	}

	public static CefClient getCEFClient() {
		if (cefClient == null) {
			cefClient = createClient();
			cefClient.addLoadHandler(cefMultiLoadHandler);
		}

		return cefClient;
	}

	public static CefMultiLoadHandler getMultiLoadHandler() {
		return cefMultiLoadHandler;
	}

	private static CefApp createApp() {
		CefAppBuilder builder = new CefAppBuilder();

		builder.setInstallDir(UserFolderManager.getFileFromUserFolder("/cef/"));
		builder.setProgressHandler((enumProgress, v) -> LOG.info("Loading CEF: {} ({})", enumProgress, v));
		builder.getCefSettings().persist_session_cookies = false;
		builder.getCefSettings().root_cache_path = UserFolderManager.getFileFromUserFolder("/cef/cache/").toString();
		builder.getCefSettings().cache_path = UserFolderManager.getFileFromUserFolder("/cef/cache/").toString();

		builder.setAppHandler(new MavenCefAppHandlerAdapter() {
			@Override public void stateHasChanged(CefApp.CefAppState state) {
				if (state == CefApp.CefAppState.TERMINATED) {
					LOG.error("CEF app terminated");
				}
			}
		});

		try {
			cefApp = builder.build();
		} catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
			throw new RuntimeException(e);
		}
		return cefApp;
	}

	private static CefClient createClient() {
		CefApp cefApp = getCEFApp();

		CefClient cefClient = cefApp.createClient();

		cefClient.addDisplayHandler(new CefDisplayHandlerAdapter() {
			@Override
			public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String s,
					String s1, int i) {
				switch (logSeverity) {
				case LOGSEVERITY_VERBOSE:
					LOG.trace("{} ({})", s, i);
					break;
				case LOGSEVERITY_WARNING:
					LOG.warn("{} ({})", s, i);
					break;
				case LOGSEVERITY_ERROR:
					LOG.error("{} ({})", s, i);
					break;
				case LOGSEVERITY_FATAL:
					LOG.fatal("{} ({})", s, i);
					break;
				default:
					LOG.info("{} ({})", s, i);
					break;
				}
				return true;
			}
		});

		return cefClient;
	}

}
