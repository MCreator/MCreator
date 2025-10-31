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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CefJavaBridgeHandler extends CefMessageRouterHandlerAdapter {

	private static final Logger LOG = LogManager.getLogger(CefJavaBridgeHandler.class);

	private static final Gson gson = new GsonBuilder().create();

	private final WebView webView;

	private final Object bridge;
	private final String prefix;

	/**
	 * Installs a JavaScript bridge into the provided CefBrowser instance. This enables communication
	 * between the JavaScript code running in the browser and the provided Java object.
	 *
	 * @param webView The {@link WebView} instance where the JavaScript bridge will be injected.
	 * @param bridge  The Java object that serves as the backend for the JavaScript bridge, allowing
	 *                methods to be proxied and called from JavaScript.
	 * @param name    The name of the bridge, which will be used as the global object in JavaScript.
	 *                Use only alphanumeric characters and underscores.
	 */
	public CefJavaBridgeHandler(WebView webView, Object bridge, String name) {
		this.webView = webView;
		this.bridge = bridge;
		this.prefix = name + ":";

		// TODO: sync methods with retun value String or String[] are not supported yet

		// Install JavaScript bridge
		String wrapperJs = """
                (function() {
                    if (window.%s) return;
                    window.%s = new Proxy({}, {
                        get: function(_, methodName) {
                            return function(...args) {
                                let callback = null;
                                if (args.length && typeof args[args.length - 1] === 'function') {
                                    callback = args.pop();
                                }
                                const request = '%s' + methodName + ':' + JSON.stringify(args);
                                window.cefQuery({
                                    request: request,
                                    onSuccess: function(response) { if (callback) callback(response); },
                                    onFailure: function(code, msg) { console.error('Java call to ' + methodName + ' failed:', code, msg); if (callback) callback(null); }
                                });
                            };
                        }
                    });
                })();
                """.formatted(name, name, prefix);
		webView.executeScript(wrapperJs, false);
	}

	@Override
	public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
			CefQueryCallback callback) {
		if (browser != webView.getBrowser())
			return false; // message router sends callback to all adapters

		if (!request.startsWith(prefix)) {
			return false;
		}

		String payload = request.substring(prefix.length());
		int colonIndex = payload.indexOf(':');
		if (colonIndex < 0) {
			callback.failure(400, "No method or arguments specified");
			return true;
		}

		String methodName = payload.substring(0, colonIndex);
		String argsJson = payload.substring(colonIndex + 1);

		String[] args;
		try {
			args = gson.fromJson(argsJson, String[].class);
		} catch (JsonSyntaxException e) {
			LOG.error("Invalid JSON arguments: {}", e.getMessage(), e);
			callback.failure(400, "Invalid JSON arguments: " + e.getMessage());
			return true;
		}

		// Find a matching method
		Method targetMethod = Arrays.stream(bridge.getClass().getMethods())
				.filter(m -> m.getName().equals(methodName))
				.filter(m -> {
					Class<?>[] params = m.getParameterTypes();
					return params.length == args.length || (params.length == args.length + 1
							&& params[params.length - 1] == CefQueryCallback.class);
				}).findFirst().orElse(null);

		if (targetMethod == null) {
			callback.failure(404, "Method not found or wrong number of parameters: " + methodName);
			return true;
		}

		try {
			Class<?>[] paramTypes = targetMethod.getParameterTypes();
			Object[] callArgs;

			if (paramTypes.length == args.length) {
				callArgs = args;
			} else {
				callArgs = Arrays.copyOf(args, args.length + 1);
				callArgs[args.length] = callback;
			}

			Object result = targetMethod.invoke(bridge, callArgs);

			// Only call callback if the method didn't handle it itself
			if (result != null && !(paramTypes.length > 0
					&& paramTypes[paramTypes.length - 1] == CefQueryCallback.class)) {
				webView.runOnCallbackThread(() -> callback.success(result.toString()));
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.error("Error invoking method: {}", e.getMessage(), e);
			callback.failure(500, "Error invoking method: " + e.getMessage());
		}

		return true;
	}

}
