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

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public class CefJavaBridgeHandler {

	private static final Logger LOG = LogManager.getLogger(CefJavaBridgeHandler.class);

	private static final Gson gson = new GsonBuilder().create();

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
	CefJavaBridgeHandler(WebView webView, Object bridge, String name) {
		this.bridge = bridge;
		this.prefix = name + ":";

		// Install JavaScript bridge
		String wrapperJs = """
				(function() {
				    if (window.%s) return;
				    window.%s = new Proxy({}, {
				        get: function(_, methodName) {
				            return function(...args) {
				                let callback = null;
				                if (args.length && typeof args[args.length - 1] === 'object' && typeof args[args.length - 1].callback === 'function') {
				                    callback = args[args.length - 1].callback;
				                    args.pop();
				                }
				                if (callback) {
				                    window.cefQuery({
				                        request: '%s' + methodName + ':' + JSON.stringify(args),
										onSuccess: function(response) {
										    const data = JSON.parse(response);
										    callback.apply(null, Array.isArray(data) ? data : [data]);
										}
				                    });
				                    return;
				                } else {
									let result = prompt('%s' + methodName + ':' + JSON.stringify(args));
				                    try { return JSON.parse(result); } catch(e) { return result; }
								}
				            };
				        }
				    });
				})();
				""".formatted(name, name, prefix, prefix);

		webView.executeScript(wrapperJs, false);

		// Async calls via message router
		webView.getRouter().addHandler(new CefMessageRouterHandlerAdapter() {
			@Override
			public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
					CefQueryCallback callback) {
				if (!request.startsWith(prefix))
					return false;

				String result = invokeBridge(request, object -> callback.success(gson.toJson(object)));
				if (result != null)
					callback.success(result);

				return true;
			}
		}, false);

		// Blocking calls via JS dialogs (hacky, but the only way to suspend CEF IPC and wait for Java to provide value in a sync manner)
		webView.addJSDialogListener(
				(browser, origin_url, dialog_type, message_text, default_prompt_text, callback, suppress_message) -> {
					if (!message_text.startsWith(prefix))
						return false;

					String result = invokeBridge(message_text, null);
					callback.Continue(true, result != null ? result : "");
					return true;
				});
	}

	@Nullable private String invokeBridge(String request, @Nullable Consumer<Object> callback) {
		try {
			String payload = request.substring(prefix.length());
			int colonIndex = payload.indexOf(':');
			if (colonIndex < 0)
				return null;

			String methodName = payload.substring(0, colonIndex);
			String argsJson = payload.substring(colonIndex + 1);

			Object[] args;
			try {
				args = gson.fromJson(argsJson, Object[].class);
			} catch (JsonSyntaxException e) {
				LOG.error("Invalid JSON arguments: {} for method: {}", e.getMessage(), methodName, e);
				return null;
			}

			Method targetMethod = Arrays.stream(bridge.getClass().getMethods())
					.filter(m -> m.getName().equals(methodName)).filter(m -> {
						Class<?>[] params = m.getParameterTypes();
						return params.length == args.length || (params.length == args.length + 1
								&& params[params.length - 1] == Consumer.class);
					}).findFirst().orElse(null);

			if (targetMethod == null) {
				LOG.error("Method not found or wrong number of parameters: {}", methodName);
				return null;
			}

			Class<?>[] paramTypes = targetMethod.getParameterTypes();
			Object[] callArgs;
			if (paramTypes.length == args.length) {
				callArgs = args;
			} else {
				callArgs = Arrays.copyOf(args, args.length + 1);
				callArgs[args.length] = callback;
			}

			Object result = targetMethod.invoke(bridge, callArgs);
			if (result != null)
				return (result instanceof String) ? (String) result : gson.toJson(result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.error("Error invoking method: {}", e.getMessage(), e);
		}

		return null; // void methods return null
	}

}
