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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.*;
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

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class CefUtils {

	private static final Logger LOG = LogManager.getLogger("CEF");

	private static CefApp cefApp = null;

	private static Boolean useOSR = null;

	private static CefBrowserSettings settings = null;

	public static boolean useOSR() {
		if (useOSR == null) {
			useOSR = useOSRImpl();
		}
		return useOSR;
	}

	private static boolean useOSRImpl() {
		if (OS.isMacintosh()) {
			// Default OSR fails to load due to missing JOGL natives in the current JBR JCEF build
			// However, we use a custom OSR solution that works fine on macOS, but is slower than WR
			// WR is much more snappy, however, acts strangely during window resize, but has right size afterwards
			return false;
		}

		//noinspection IfStatementWithIdenticalBranches
		if (OS.isWindows()) {
			// On Windows, we can use WR or OS
			// WR has less latency between input events and rendering due to direct render pipeline
			// OSR starts up faster and has less glitching during visibility changes and supports cursor changes
			return true; // use OSR at this time due to more benefits
		}

		// On linux, we need to use OSR due to several focus and keyboard transfer issues with WR
		return true;
	}

	public static CefBrowserSettings getCefBrowserSettings() {
		if (settings == null) {
			settings = new CefBrowserSettings();

			int highestFPS = 30; // if we fail to detect anything, default to 30 FPS
			GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			for (GraphicsDevice device : devices) {
				DisplayMode mode = device.getDisplayMode();
				if (mode.getRefreshRate() > highestFPS)
					highestFPS = mode.getRefreshRate();
			}

			if (OS.isLinux() && "wayland".equals(System.getenv("XDG_SESSION_TYPE"))) {
				highestFPS = 60; // hardcode Wayland to 60 FPS as the method above will not work there
			}

			settings.windowless_frame_rate = highestFPS;
		}
		return settings;
	}

	private static CefApp getCefApp() {
		if (cefApp == null) {
			LOG.info("Initializing JCEF in {} mode",
					useOSR() ? "OSR (" + getCefBrowserSettings().windowless_frame_rate + " FPS)" : "WR");

			JCefAppConfig config = JCefAppConfig.getInstance();
			config.getAppArgsAsList().add("--disable-extensions");
			config.getAppArgsAsList().add("--disable-default-apps");
			config.getAppArgsAsList().add("--disable-sync");
			config.getAppArgsAsList().add("--disable-speech-api");
			config.getAppArgsAsList().add("--mute-audio");
			config.getAppArgsAsList().add("--disable-gaia-services");

			Set<String> disabledFeatures = new HashSet<>();
			// Get existing disabled features
			String toRemove = null;
			for (String entry : config.getAppArgsAsList()) {
				if (entry.startsWith("--disable-features=")) {
					disabledFeatures.addAll(List.of(entry.substring("--disable-features=".length()).split(",")));
					toRemove = entry;
					break;
				}
			}
			// Remove original flag
			if (toRemove != null)
				config.getAppArgsAsList().remove(toRemove);

			// Disable features we don't need
			disabledFeatures.add("WebUSB");
			disabledFeatures.add("WebBluetooth");
			disabledFeatures.add("WebHID");
			disabledFeatures.add("WebSerial");
			disabledFeatures.add("NewUsbBackend");
			disabledFeatures.add("TranslateUI");
			disabledFeatures.add("MediaRouter");

			if (TestUtil.isRunningInGitHubActions()) {
				// Flags for CI/CD as it is headless and without GPU
				config.getAppArgsAsList().add("--headless");
				config.getAppArgsAsList().add("--ignore-gpu-blocklist");
				config.getAppArgsAsList().add("--no-sandbox");
				config.getAppArgsAsList().add("--disable-setuid-sandbox");
				config.getAppArgsAsList().add("--disable-gpu");
				config.getAppArgsAsList().add("--disable-gpu-compositing");
				config.getAppArgsAsList().add("--disable-gpu-vsync");
				config.getAppArgsAsList().add("--disable-zygote");
				config.getAppArgsAsList().add("--disable-dev-shm-usage");
				config.getAppArgsAsList().add("--use-gl=swiftshader"); // CPU rendering

				// Reduce RAM usage as CI/CD has limited RAM
				config.getAppArgsAsList().add("--renderer-process-limit=1");
				config.getAppArgsAsList().add("--js-flags=--lite-mode");
				config.getAppArgsAsList().add("--disable-breakpad");

				// Disable certain browser features
				disabledFeatures.add("Vulkan");
				disabledFeatures.add("UseSkiaRenderer");
			} else if (!PreferencesManager.PREFERENCES.blockly.useGPUAcceleration.get()) {
				config.getAppArgsAsList().add("--disable-gpu");
				config.getAppArgsAsList().add("--disable-gpu-compositing");
				config.getAppArgsAsList().add("--use-gl=swiftshader");
			}

			// Disable GPU compositing for OSR mode. Workaround for https://github.com/chromiumembedded/cef/issues/3826
			if (useOSR()) {
				config.getAppArgsAsList().add("--disable-gpu-compositing");
			}

			config.getAppArgsAsList().add("--disable-features=" + String.join(",", disabledFeatures));

			LOG.debug("JCEF arguments: {}", config.getAppArgsAsList());

			List<String> appArgs = config.getAppArgsAsList();
			CefSettings settings = config.getCefSettings();
			settings.no_sandbox = true;
			settings.cache_path = UserFolderManager.getFileFromUserFolder("/cef/").toString();
			settings.background_color = settings.new ColorType(0, 0, 0, 0);
			settings.windowless_rendering_enabled = useOSR();
			settings.persist_session_cookies = false;
			settings.locale = L10N.getLocale().stripExtensions().toLanguageTag();
			settings.log_file = null;

			String[] args = appArgs.toArray(new String[0]);
			CefApp.addAppHandler(new CefAppHandlerAdapter(args) {
				@Override public void onContextInitialized() {
					cefApp.registerSchemeHandlerFactory("classloader", "", CefClassLoaderSchemeHandler::new);
				}

				@Override public boolean onBeforeTerminate() {
					return true; // Do not let JCEF terminate itself
				}
			});

			CefApp.startup(args);

			cefApp = CefApp.getInstance(settings);

			CountDownLatch latch = new CountDownLatch(1);
			cefApp.onInitialization(s -> {
				LOG.debug("CefApp initialized (JCEF: {}, CEF: {}, Chromium: {})", cefApp.getVersion().getJcefVersion(),
						cefApp.getVersion().getCefVersion(), cefApp.getVersion().getChromeVersion());
				latch.countDown();
			});

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

			@Override public boolean onCursorChange(CefBrowser browser, int cursorType) {
				if (!useOSR()) {
					//noinspection MagicConstant
					browser.getUIComponent().setCursor(Cursor.getPredefinedCursor(cursorType));
					return true;
				}
				return false;
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
