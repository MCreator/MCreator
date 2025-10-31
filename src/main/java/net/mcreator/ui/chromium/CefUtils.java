/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.chromium;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import net.mcreator.Launcher;
import net.mcreator.io.UserFolderManager;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandler;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CefUtils {

	private static final Logger LOG = LogManager.getLogger("CEF");

	private static CefApp cefApp = null;
	private static CefClient cefClient = null;
	private static CefMessageRouter cefMessageRouter = null;

	private static final CefMultiLoadHandler cefMultiLoadHandler = new CefMultiLoadHandler();

	private static CefApp getCefApp() {
		if (cefApp == null) {
			cefApp = createApp();
		}

		return cefApp;
	}

	public static void close() {
		if (cefMessageRouter != null) {
			cefMessageRouter.dispose();
			cefMessageRouter = null;
		}
		if (cefClient != null) {
			cefClient.dispose();
			cefClient = null;
		}
		if (cefApp != null) {
			cefApp.dispose();
			cefApp = null;
		}
	}

	public static CefClient getCefClient() {
		if (cefClient == null) {
			cefClient = createClient();
			getCefMessageRouter(); // also preload CEF message router
			cefClient.addLoadHandler(cefMultiLoadHandler);
		}

		return cefClient;
	}

	public static CefMessageRouter getCefMessageRouter() {
		if (cefMessageRouter == null) {
			cefMessageRouter = CefMessageRouter.create();
			getCefClient().addMessageRouter(cefMessageRouter);
		}

		return cefMessageRouter;
	}

	static CefMultiLoadHandler getMultiLoadHandler() {
		return cefMultiLoadHandler;
	}

	private static CefApp createApp() {
		CefAppBuilder builder = new CefAppBuilder();

		builder.setInstallDir(UserFolderManager.getFileFromUserFolder("/cef/"));
		builder.setProgressHandler((enumProgress, v) -> LOG.info("Loading CEF: {} ({})", enumProgress, v));
		builder.getCefSettings().background_color = builder.getCefSettings().new ColorType(0, 0, 0, 0);
		builder.getCefSettings().windowless_rendering_enabled = false;
		builder.getCefSettings().persist_session_cookies = false;
		builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
		builder.getCefSettings().root_cache_path = UserFolderManager.getFileFromUserFolder("/cef/cache/").toString();
		builder.getCefSettings().cache_path = UserFolderManager.getFileFromUserFolder("/cef/cache/").toString();

		builder.setAppHandler(new MavenCefAppHandlerAdapter() {
			@Override public void stateHasChanged(CefApp.CefAppState state) {
				if (state == CefApp.CefAppState.TERMINATED) {
					LOG.error("CEF app terminated");
				}
			}

			@Override public void onContextInitialized() {
				cefApp.registerSchemeHandlerFactory("classloader", "",
						(browser, frame, schemeName, request) -> new CefClassLoaderSchemeHandler());
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
		CefApp cefApp = getCefApp();

		CefClient cefClient = cefApp.createClient();

		// Logging handling
		cefClient.addDisplayHandler(new CefDisplayHandlerAdapter() {
			@Override
			public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String message,
					String sourceUrl, int line) {
				String logMsg = String.format("%s (source: %s, line: %d)", message, sourceUrl, line);

				switch (logSeverity) {
				case LOGSEVERITY_VERBOSE -> LOG.trace(logMsg);
				case LOGSEVERITY_WARNING -> LOG.warn(logMsg);
				case LOGSEVERITY_ERROR -> LOG.error(logMsg);
				case LOGSEVERITY_FATAL -> LOG.fatal(logMsg);
				default -> LOG.info(logMsg);
				}

				if (message.contains("Error")) {
					TestUtil.failIfTestingEnvironment();
				}

				return true;
			}
		});

		// Disable context menu
		cefClient.addContextMenuHandler(new CefContextMenuHandler() {
			@Override
			public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params,
					CefMenuModel model) {
				model.clear();
			}

			@Override
			public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params,
					int commandId, int eventFlags) {
				return false;
			}

			@Override public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
			}
		});

		// Pass keyboard events from a native CEF component to Swing components using CefSwingKeyboardBridge + handle dev tools
		cefClient.addKeyboardHandler(new CefSwingKeyboardBridge() {
			@Override
			public boolean onPreKeyEvent(CefBrowser browser, CefKeyEvent event, BoolRef is_keyboard_shortcut) {
				return false;
			}

			@Override public boolean onAfterKeyEvent(CefBrowser browser, CefKeyEvent event) {
				if (event.windows_key_code == 123 /*F12*/ && Launcher.version.isDevelopment()
						&& event.type == CefKeyEvent.EventType.KEYEVENT_KEYUP) {
					browser.openDevTools();
					return true;
				}

				return false;
			}
		});

		// Disable access to the internet
		cefClient.addRequestHandler(new CefRequestHandlerAdapter() {
			@Override
			public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean userGesture,
					boolean isRedirect) {
				String url = request.getURL();
				return url.startsWith("http://") || url.startsWith("https://"); // return true to block the request
			}
		});

		// Prevent client from owning the focus so other text fields around the CEF component work
		cefClient.addFocusHandler(new CefFocusHandlerAdapter() {
			@Override public void onGotFocus(CefBrowser browser) {
				SwingUtilities.invokeLater(() -> {
					browser.setFocus(false);
					KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
					browser.setFocus(false);
				});
			}
		});

		return cefClient;
	}

}
