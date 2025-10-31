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
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class WebView extends JPanel implements Closeable {

	private static final Logger LOG = LogManager.getLogger(WebView.class);

	private final CefBrowser browser;
	private final CefLoadHandlerAdapter cefLoadHandler;
	private final List<PageLoadListener> pageLoadListeners = new ArrayList<>();
	private final List<CefMessageRouterHandlerAdapter> associatedMessageHandlers = new ArrayList<>();

	private static final ExecutorService callbackExecutor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("WebView-Callback-Thread");
		thread.setUncaughtExceptionHandler((t, e) -> LOG.error(e));
		return thread;
	});

	public WebView(String url, boolean isTransparent) {
		this.browser = CefUtils.getCefClient().createBrowser(url, false, isTransparent);

		// Register persistent JS handler once
		CefUtils.getCefMessageRouter().addHandler(executeScriptHandler, false);

		this.cefLoadHandler = new CefLoadHandlerAdapter() {
			@Override public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
				if (browser != WebView.this.browser)
					return; // load handler is global
				runOnCallbackThread(() -> pageLoadListeners.forEach(PageLoadListener::pageLoaded));
			}
		};
		CefUtils.getMultiLoadHandler().addHandler(cefLoadHandler);

		Component cefComponent = browser.getUIComponent();
		cefComponent.setBackground(Theme.current().getBackgroundColor());
		setOpaque(false);
		setLayout(new BorderLayout());
		add(cefComponent, BorderLayout.CENTER);
	}

	public void addJavaScriptBridge(String name, Object bridge) {
		CefMessageRouterHandlerAdapter handler = new CefJavaBridgeHandler(this, bridge, name);
		associatedMessageHandlers.add(handler);
		CefUtils.getCefMessageRouter().addHandler(handler, false);
	}

	public synchronized void executeScript(String javaScript) {
		executeScript(javaScript, false);
	}

	// Helpers for executeScript
	private CountDownLatch executeScriptLatch;
	private final AtomicReference<String> executeScriptResult = new AtomicReference<>();
	private final CefMessageRouterHandlerAdapter executeScriptHandler = new CefMessageRouterHandlerAdapter() {
		@Override
		public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
				CefQueryCallback callback) {
			if (browser != WebView.this.browser)
				return false; // message router sends callback to all adapters

			if (request.startsWith("@jsResult:")) {
				executeScriptResult.set(request.substring("@jsResult:".length()));
				callback.success("ok");
				if (executeScriptLatch != null)
					executeScriptLatch.countDown();
				return true;
			}
			return false;
		}
	};

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

	public void addLoadListener(PageLoadListener listener) {
		pageLoadListeners.add(listener);
	}

	void runOnCallbackThread(Runnable runnable) {
		callbackExecutor.execute(runnable);
	}

	@Override public void close() {
		CefUtils.getMultiLoadHandler().removeHandler(cefLoadHandler);
		for (CefMessageRouterHandlerAdapter handler : associatedMessageHandlers) {
			CefUtils.getCefMessageRouter().removeHandler(handler);
		}
		CefUtils.getCefMessageRouter().removeHandler(executeScriptHandler);
		browser.close(true);
	}

	public interface PageLoadListener {
		void pageLoaded();
	}

}