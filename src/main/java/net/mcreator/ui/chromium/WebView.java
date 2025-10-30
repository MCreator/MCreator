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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class WebView implements Closeable {

	private static final Logger LOG = LogManager.getLogger(WebView.class);

	private final CefBrowser browser;
	private final CefMessageRouter router;

	private final Component component;

	private final CefLoadHandler cefLoadHandler;

	private final List<PageLoadListener> pageLoadListeners = new ArrayList<>();

	public WebView(String url, @Nullable Object bridge, boolean isTransparent) {
		this.browser = CefUtils.getCEFClient().createBrowser(url, false, isTransparent);
		this.component = browser.getUIComponent();
		this.router = CefMessageRouter.create();
		this.browser.getClient().addMessageRouter(router);

		if (bridge != null) {
			router.addHandler(new CefJavaBridgeHandler(this.browser, bridge), true);
		}

		this.cefLoadHandler = new CefLoadHandlerAdapter() {
			@Override public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
				if (browser == WebView.this.browser) {
					for (PageLoadListener listener : pageLoadListeners) {
						listener.pageLoaded();
					}
				}
			}
		};
		CefUtils.getMultiLoadHandler().addHandler(cefLoadHandler);
	}

	public synchronized String executeJavaScript(String jsExpr) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<String> result = new AtomicReference<>();

		CefMessageRouterHandlerAdapter handler = new CefMessageRouterHandlerAdapter() {
			@Override
			public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
					CefQueryCallback callback) {
				if (request.startsWith("jsResult:")) {
					result.set(request.substring("jsResult:".length()));
					callback.success("ok");
					latch.countDown();
					return true;
				}
				return false;
			}
		};

		try {
			router.addHandler(handler, false);
			String script = """
					(function() {
					    try {
					        const res = %s;
					        window.cefQuery({ request: "jsResult:" + res });
					    } catch (e) {
					        window.cefQuery({ request: "jsResult:ERROR:" + e });
					    }
					})();
					""".formatted(jsExpr);

			browser.executeJavaScript(script, "", 0);

			latch.await(); // block until callback
		} catch (InterruptedException e) {
			LOG.warn("Interrupted while waiting for JavaScript evaluation", e);
		} finally {
			router.removeHandler(handler);
		}

		return result.get();
	}

	public void addCSSToPage(String css) {
		browser.executeJavaScript("""
				(function() {
				    var style = document.createElement('style');
				    style.type = 'text/css';
				    style.innerHTML = '%s';
				    document.head.appendChild(style);
				})();
				""".formatted(css), "", 0);
	}

	public CefBrowser getBrowser() {
		return browser;
	}

	public Component getComponent() {
		return component;
	}

	public void addLoadListener(PageLoadListener listener) {
		pageLoadListeners.add(listener);
	}

	@Override public void close() {
		if (browser != null) {
			CefUtils.getMultiLoadHandler().removeHandler(cefLoadHandler);
			router.dispose();
			browser.getClient().doClose(browser);
			browser.close(true);
		}
	}

	public interface PageLoadListener {
		void pageLoaded();
	}

}
