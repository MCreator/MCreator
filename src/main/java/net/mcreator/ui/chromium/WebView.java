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

import net.mcreator.Launcher;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefRendering;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefKeyboardHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class WebView extends JPanel implements Closeable {

	private static final Logger LOG = LogManager.getLogger(WebView.class);

	private final CefClient client;
	private final CefMessageRouter router;
	private final CefBrowser browser;

	private final Component cefComponent;

	// Helper for page load listeners
	private final List<PageLoadListener> pageLoadListeners = new ArrayList<>();

	// Helpers for executeScript
	private CountDownLatch executeScriptLatch;
	private final AtomicReference<String> executeScriptResult = new AtomicReference<>();

	private final ExecutorService callbackExecutor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("WebView-Callback-Thread");
		thread.setUncaughtExceptionHandler((t, e) -> LOG.error("Failed to run WebView callback: {}", e, e));
		return thread;
	});

	public WebView(String url) {
		this(url, false);
	}

	private WebView(String url, boolean forcePreload) {
		setLayout(new BorderLayout());
		setOpaque(false);
		setLayout(new BorderLayout());
		setOpaque(false);

		this.client = CefUtils.createClient();
		this.router = CefMessageRouter.create();
		this.client.addMessageRouter(this.router);
		this.browser = this.client.createBrowser(url, CefUtils.useOSR() ? CefRendering.OFFSCREEN : CefRendering.DEFAULT,
				false);
		/*
		 * Immediately create the browser if:
		 * - forcePreload set in preload() function so when preloading we don't infinitely wait for the browser to appear
		 * - on Windows, it reduces loading flickering
		 * - on tests, brwoser is never shown so we need to preload it so it actually loads content
		 */
		if (forcePreload || OS.isWindows() || TestUtil.isTestingEnvironment())
			this.browser.createImmediately(); // needed so tests that don't render also work

		this.router.addHandler(new CefMessageRouterHandlerAdapter() {
			@Override
			public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
					CefQueryCallback callback) {
				if (request.startsWith("@jsResult:")) {
					executeScriptResult.set(request.substring("@jsResult:".length()));
					callback.success("ok");
					if (executeScriptLatch != null)
						executeScriptLatch.countDown();
					return true;
				}
				return false;
			}
		}, true);

		// Pass key events to swing when appropriate
		this.client.addKeyboardHandler(new CefKeyboardHandlerAdapter() {
			@Override public boolean onKeyEvent(CefBrowser browser, CefKeyEvent cefKeyEvent) {
				// Activate dev tools on F12 press if dev version of MCreator
				if (cefKeyEvent.windows_key_code == 123 && Launcher.version.isDevelopment()
						&& cefKeyEvent.type == CefKeyEvent.EventType.KEYEVENT_KEYUP) {
					browser.openDevTools();
					return true;
				}

				if (CefUtils.useOSR())
					return false;

				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				boolean consume = focusOwner != browser.getUIComponent();
				if (consume && OS.isMacintosh() && CefEventUtils.isUpDownKeyEvent(cefKeyEvent))
					return true; // consume

				if (cefKeyEvent.type == CefKeyEvent.EventType.KEYEVENT_RAWKEYDOWN && cefKeyEvent.is_system_key) {
					// CMD+[key] is not working on a Mac.
					// This switch statement delegates the common keyboard shortcuts to the browser
					switch (cefKeyEvent.unmodified_character) {
					case 'a':
						browser.getFocusedFrame().selectAll();
						break;
					case 'c':
						browser.getFocusedFrame().copy();
						break;
					case 'v':
						browser.getFocusedFrame().paste();
						break;
					case 'x':
						browser.getFocusedFrame().cut();
						break;
					case 'z':
						browser.getFocusedFrame().undo();
						break;
					case 'Z':
						browser.getFocusedFrame().redo();
						break;
					default:
						return false;
					}
					return true;
				}

				Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
				if (focusedWindow == null) {
					return true; // consume
				}
				try {
					KeyEvent javaKeyEvent = CefEventUtils.convertCefKeyEvent(cefKeyEvent, focusedWindow);
					Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(javaKeyEvent);
				} catch (IllegalArgumentException e) {
					LOG.error("Failed to convert CEF key event: {} due to: {}", cefKeyEvent, e);
				}

				return consume;
			}
		});

		// Focus fixes
		this.client.addFocusHandler(new CefFocusHandlerAdapter() {
			boolean firstShow = true;

			@Override public boolean onSetFocus(CefBrowser browser, FocusSource source) {
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				boolean componentFocused = focusOwner == WebView.this || focusOwner == WebView.this.cefComponent;
				boolean focusOnNavigation = firstShow || componentFocused;
				firstShow = false;

				if (source == FocusSource.FOCUS_SOURCE_NAVIGATION && !focusOnNavigation) {
					if (OS.isWindows()) {
						browser.setFocus(false);
					}
					return true; // suppress focusing the browser on navigation events
				}

				if (CefUtils.useOSR()) {
					return false;
				}

				if (!browser.getUIComponent().hasFocus()) {
					if (OS.isLinux()) {
						browser.getUIComponent().requestFocus();
					} else {
						browser.getUIComponent().requestFocusInWindow();
					}
				}
				return false;
			}
		});

		this.client.addLoadHandler(new CefLoadHandlerAdapter() {
			@Override public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
				callbackExecutor.execute(() -> pageLoadListeners.forEach(PageLoadListener::pageLoaded));
			}
		});

		this.cefComponent = browser.getUIComponent();
		cefComponent.setBackground(Theme.current().getBackgroundColor());

		setFocusCycleRoot(true);
		setFocusTraversalPolicyProvider(true);
		setFocusTraversalPolicy(new FocusTraversalPolicy() {
			@Override public Component getComponentAfter(Container container, Component component) {
				return cefComponent;
			}

			@Override public Component getComponentBefore(Container container, Component component) {
				return cefComponent;
			}

			@Override public Component getFirstComponent(Container container) {
				return cefComponent;
			}

			@Override public Component getLastComponent(Container container) {
				return cefComponent;
			}

			@Override public Component getDefaultComponent(Container container) {
				return cefComponent;
			}
		});

		if (!OS.isMacintosh()) {
			// On non MacOS systems, we can directly add the component
			add(cefComponent, BorderLayout.CENTER);
		} else {
			// On MacOS systems, we need to add the component only when the webview is shown
			// so when other webview can take over focus when they are showing instead of this one
			addHierarchyListener(e -> {
				if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
					if (isShowing()) {
						add(cefComponent, BorderLayout.CENTER);
					} else { // editor hidden
						removeAll();
					}
				}
			});
		}

		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	@Override public void removeNotify() {
		if (OS.isWindows()) {
			if (browser.getUIComponent().hasFocus()) {
				browser.setFocus(false);
			}
		}
		super.removeNotify();
	}

	@Override protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		if (e.getID() == FocusEvent.FOCUS_GAINED) {
			cefComponent.requestFocusInWindow();
		}
	}

	public void addJavaScriptBridge(String name, Object bridge) {
		new CefJavaBridgeHandler(this, bridge, name);
	}

	public synchronized void executeScript(String javaScript) {
		executeScript(javaScript, false);
	}

	public synchronized String executeScript(String javaScript, boolean requestRetval) {
		executeScriptLatch = new CountDownLatch(1);
		executeScriptResult.set(null);

		String script;
		if (requestRetval) {
			script = """
					(function() {
					    const res = %s;
					    window.cefQuery({ request: "@jsResult:" + res });
					})();
					""".formatted(javaScript);
		} else {
			script = """
					%s
					window.cefQuery({ request: "@jsResult:" });
					""".formatted(javaScript);
		}

		browser.executeJavaScript(script, "[WebView injected]", 0);

		try {
			executeScriptLatch.await();
		} catch (InterruptedException e) {
			LOG.warn("Interrupted while waiting for evaluation of JS: {}", javaScript, e);
		}

		return executeScriptResult.get();
	}

	public void addCSSToDOM(String css) {
		// Escape backslashes, single quotes, and newlines for JS string
		String jsSafeCss = css.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "");

		// can be safely run async
		executeScript("""
				(function() {
				    var style = document.createElement('style');
				    style.type = 'text/css';
				    style.innerHTML = '%s';
				    document.head.appendChild(style);
				})();
				""".formatted(jsSafeCss), false);
	}

	public void addStringConstantToDOM(String name, String value) {
		executeScript("window['%s'] = '%s';".formatted(name, value), false);
	}

	CefBrowser getBrowser() {
		return browser;
	}

	CefClient getClient() {
		return client;
	}

	CefMessageRouter getRouter() {
		return router;
	}

	public void addLoadListener(PageLoadListener listener) {
		pageLoadListeners.add(listener);
	}

	@Override public void close() {
		browser.close(true);
		router.dispose();
		client.dispose();
	}

	public interface PageLoadListener {
		void pageLoaded();
	}

	public static void preload() {
		LOG.debug("Preloading CEF WebView");
		CountDownLatch latch = new CountDownLatch(1);
		WebView preloader = new WebView("about:blank", true);
		try (preloader) {
			preloader.addLoadListener(latch::countDown);
			latch.await();
		} catch (InterruptedException ignored) {
		}
	}

}