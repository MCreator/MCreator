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
import net.mcreator.ui.laf.themes.ThemeCSS;
import net.mcreator.util.TestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefRendering;
import org.cef.callback.CefJSDialogCallback;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.*;
import org.cef.misc.BoolRef;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WebView extends JPanel implements Closeable {

	private static final Logger LOG = LogManager.getLogger(WebView.class);

	private final CefClient client;
	private final CefMessageRouter router;
	private final CefBrowser browser;

	private final Component cefComponent;

	// Helper for page load listeners
	private final List<PageLoadListener> pageLoadListeners = new ArrayList<>();

	// Helper for JS dialog handlers
	private final List<JSDialogListener> jsDialogListeners = new ArrayList<>();

	// Helpers for executeScript
	private volatile CountDownLatch executeScriptLatch;
	private final AtomicReference<String> executeScriptResult = new AtomicReference<>();

	private volatile boolean isClosing = false;

	private final ExecutorService callbackExecutor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("WebView-Callback-Thread");
		thread.setUncaughtExceptionHandler((t, e) -> LOG.error("Failed to run WebView callback: {}", e, e));
		return thread;
	});

	private final ExecutorService edtJSWaitThread = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("EDT-JS-Wait-Thread");
		thread.setUncaughtExceptionHandler((t, e) -> LOG.error("Failed to wait on JS execution: {}", e, e));
		return thread;
	});

	public WebView(String url) {
		this(url, false);
	}

	private WebView(String url, boolean forcePreload) {
		setLayout(new BorderLayout());

		this.client = CefUtils.createClient();
		this.router = CefMessageRouter.create();
		this.client.addMessageRouter(this.router);
		this.browser = this.client.createBrowser(url, CefUtils.useOSR() ? CefRendering.OFFSCREEN : CefRendering.DEFAULT,
				false);

		/*
		 * Immediately create the browser if:
		 * - forcePreload set in preload() function so when preloading we don't infinitely wait for the browser to appear
		 * - on tests, the browser is never shown, so we need to preload it so it actually loads content
		 */
		if (forcePreload || TestUtil.isTestingEnvironment())
			this.browser.createImmediately(); // needed so tests that don't render also work

		this.router.addHandler(new CefMessageRouterHandlerAdapter() {
			@Override
			public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
					CefQueryCallback callback) {
				if (request.startsWith("@jsResult:")) {
					executeScriptResult.set(request.substring("@jsResult:".length()));
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
					try {
						Method method = browser.getClass().getMethod("openDevTools");
						method.setAccessible(true);
						method.invoke(browser);
					} catch (Exception ignored) {
					}
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
			@Override public boolean onSetFocus(CefBrowser browser, FocusSource source) {
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

		this.client.addJSDialogHandler(new CefJSDialogHandlerAdapter() {
			@Override
			public boolean onJSDialog(CefBrowser browser, String origin_url, JSDialogType dialog_type,
					String message_text, String default_prompt_text, CefJSDialogCallback callback,
					BoolRef suppress_message) {
				for (JSDialogListener listener : jsDialogListeners) {
					if (listener.onJSDialog(browser, origin_url, dialog_type, message_text, default_prompt_text,
							callback, suppress_message)) {
						return true;
					}
				}
				return false;
			}
		});

		this.cefComponent = browser.getUIComponent();
		cefComponent.setBackground(Theme.current().getBackgroundColor());

		if (!CefUtils.useOSR() && OS.isWindows()) {
			// Workaround for the non-OSR component to always gain focus in mouse click
			// Without this, focus is not correctly transferred in some cases
			cefComponent.addMouseListener(new MouseAdapter() {
				@Override public void mousePressed(MouseEvent e) {
					browser.setFocus(true);
					cefComponent.requestFocusInWindow();
				}
			});

			cefComponent.addHierarchyListener(e -> {
				if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
					browser.setFocus(true);
					cefComponent.requestFocusInWindow();
				}
			});
		}

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

		// In OSR mode, there is a black flash at initialization that can be partially prevented with this hack
		// below that prevents rendering of the Chromium component during initialization
		if (CefUtils.useOSR()) {
			CefOsrBlackFlashFix.apply(this, browser, cefComponent);
			this.addMouseWheelListener(new JcefOsrWheelFix(browser, this)::handle);
		}

		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		// Workaround for https://github.com/JetBrains/jcef/issues/15 - we force cursor to default + theme CSS
		addLoadListener(() -> addCSSToDOM("* { cursor: default !important; }" + ThemeCSS.generateCSS(Theme.current())));
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

	public void executeScript(String javaScript) {
		executeScript(javaScript, false);
	}

	@Nullable public synchronized String executeScript(String javaScript, boolean requestRetval) {
		if (isClosing)
			return null;

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

		if (SwingUtilities.isEventDispatchThread()) { // If called from EDT, create a secondary loop to wait on
			SecondaryLoop secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
			AtomicBoolean secondaryLoopExited = new AtomicBoolean(false);

			edtJSWaitThread.execute(() -> {
				try {
					executeScriptLatch.await(60, TimeUnit.SECONDS);
				} catch (InterruptedException ignored) { // called if thread we wait on exits, just ignore this
				} finally {
					secondaryLoopExited.set(true);
					SwingUtilities.invokeLater(secondaryLoop::exit);
				}
			});

			browser.executeJavaScript(script, "[WebView injected]", 0);

			if (!secondaryLoopExited.get() && !secondaryLoop.enter()) {
				throw new RuntimeException("Failed to enter secondary loop for executeScript");
			}
		} else { // Not EDT, wait directly in the calling thread
			browser.executeJavaScript(script, "[WebView injected]", 0);

			try {
				// Timeout at 60 seconds as JS is blocking and nothing should take that long
				executeScriptLatch.await(60, TimeUnit.SECONDS);
			} catch (InterruptedException ignored) { // called if thread we wait on exits, just ignore this
			}
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

	CefMessageRouter getRouter() {
		return router;
	}

	void addJSDialogListener(JSDialogListener listener) {
		jsDialogListeners.add(listener);
	}

	public void addLoadListener(PageLoadListener listener) {
		pageLoadListeners.add(listener);
	}

	@Override public void close() {
		if (isClosing)
			return;

		isClosing = true;

		remove(cefComponent);

		browser.stopLoad();
		browser.setCloseAllowed();
		browser.close(true);

		client.removeMessageRouter(router);
		router.dispose();

		client.dispose();

		callbackExecutor.shutdownNow();
		edtJSWaitThread.shutdownNow();

		if (executeScriptLatch != null) {
			executeScriptLatch.countDown();
		}
	}

	public interface PageLoadListener {
		void pageLoaded();
	}

	public interface JSDialogListener {
		boolean onJSDialog(CefBrowser browser, String origin_url, CefJSDialogHandler.JSDialogType dialog_type,
				String message_text, String default_prompt_text, CefJSDialogCallback callback,
				BoolRef suppress_message);
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