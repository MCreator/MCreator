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

import com.jetbrains.cef.JCefAppConfig;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefRunContextMenuCallback;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefContextMenuHandler;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.network.CefRequest;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CefUtils {

	private static final Logger LOG = LogManager.getLogger("CEF");

	private static CefApp cefApp = null;

	public static boolean useOSR() {
		return OS.isLinux(); // TODO: OSR may also work on Linux, we just need to test
	}

	private static CefApp getCefApp() {
		if (cefApp == null) {
			JCefAppConfig config = JCefAppConfig.getInstance();
			List<String> appArgs = config.getAppArgsAsList();

			CefSettings settings = config.getCefSettings();
			settings.no_sandbox = true;
			settings.cache_path = UserFolderManager.getFileFromUserFolder("/cef/").toString();
			settings.background_color = settings.new ColorType(255, Theme.current().getBackgroundColor().getRed(),
					Theme.current().getBackgroundColor().getGreen(), Theme.current().getBackgroundColor().getBlue());
			settings.windowless_rendering_enabled = useOSR();
			settings.persist_session_cookies = false;
			settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;

			String[] args = appArgs.toArray(new String[0]);
			CefApp.addAppHandler(new CefAppHandlerAdapter(args) {
				@Override public void onContextInitialized() {
					cefApp.registerSchemeHandlerFactory("classloader", "", CefClassLoaderSchemeHandler::new);
				}
			});

			CefApp.startup(args);

			cefApp = CefApp.getInstance(settings);

			CountDownLatch latch = new CountDownLatch(1);
			cefApp.onInitialization(s -> latch.countDown());

			try {
				latch.await();
			} catch (InterruptedException ignored) {
			}
		}

		return cefApp;
	}

	public static void close() {
		if (cefApp != null) {
			cefApp.dispose();
			cefApp = null;
		}
	}

	public static CefClient createClient() {
		CefClient cefClient = getCefApp().createClient();

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
			public boolean runContextMenu(CefBrowser cefBrowser, CefFrame cefFrame,
					CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel,
					CefRunContextMenuCallback cefRunContextMenuCallback) {
				return false;
			}

			@Override
			public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params,
					int commandId, int eventFlags) {
				return false;
			}

			@Override public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
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

		return cefClient;
	}

}
