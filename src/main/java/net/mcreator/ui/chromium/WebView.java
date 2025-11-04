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

import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
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
		setLayout(new BorderLayout());
		setOpaque(false);

		this.client = CefUtils.createClient();
		this.router = CefMessageRouter.create();
		this.client.addMessageRouter(this.router);
		this.browser = this.client.createBrowser(url, CefUtils.useOSR(), false);
		this.browser.setCloseAllowed(); // workaround for https://github.com/chromiumembedded/java-cef/issues/364
		this.browser.createImmediately(); // needed so tests that don't render also work

		// Register persistent JS handler once
		// message router sends callback to all adapters
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

		this.client.addLoadHandler(new CefLoadHandlerAdapter() {
			@Override public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
				callbackExecutor.execute(() -> pageLoadListeners.forEach(PageLoadListener::pageLoaded));

				SwingUtilities.invokeLater(() -> add(cefComponent, BorderLayout.CENTER));
			}
		});

		this.cefComponent = browser.getUIComponent();
		if (cefComponent instanceof Container container) {
			for (Component child : container.getComponents()) {
				child.setBackground(Theme.current().getBackgroundColor());
			}
		}
		cefComponent.setBackground(Theme.current().getBackgroundColor());

		addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
				forceCefScaleDetectAndResize();
			}
		});

		addContainerListener(new ContainerAdapter() {
			@Override public void componentAdded(ContainerEvent e) {
				super.componentAdded(e);
				forceCefScaleDetectAndResize();
			}
		});
	}

	private void forceCefScaleDetectAndResize() {
		// This hack is only needed for WR rendering, not for OSR - workaround for https://github.com/chromiumembedded/java-cef/issues/438
		if (!CefUtils.useOSR()) {
			SwingUtilities.invokeLater(() -> {
				// First, call the paint method to update scaleFactor_ in CefBrowserWr
				cefComponent.paint(cefComponent.getGraphics());
				// After new scaleFactor_ is known, call setBounds to invoke wasResized of CefBrowser
				cefComponent.setBounds(cefComponent.getBounds());
			});
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
		new WebView("about:blank").close();
	}

}