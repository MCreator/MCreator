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
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CefJavaCallHandler extends CefMessageRouterHandlerAdapter {

	private static final String PREFIX = "javaCall:";

	private static final Logger LOG = LogManager.getLogger(CefJavaCallHandler.class);

	private final Object bridge;

	public CefJavaCallHandler(CefBrowser browser, Object bridge) {
		this.bridge = bridge;

		// Install javabridge
		String wrapperJs = """
				(function() {
				    if (window.javabridge) return;
				    window.javabridge = new Proxy({}, {
				        get: function(_, methodName) {
				            return function(...args) {
				                let callback = null;
				                if (args.length && typeof args[args.length - 1] === 'function') {
				                    callback = args.pop();
				                }
				                const request = '%s' + methodName + ':' + args.map(a => a.toString()).join(':');
				                window.cefQuery({
				                    request: request,
				                    onSuccess: function(response) { if (callback) callback(response); },
				                    onFailure: function(code, msg) { console.error('Java call failed:', code, msg); if (callback) callback(null); }
				                });
				            };
				        }
				    });
				})();
				""".formatted(PREFIX);
		browser.executeJavaScript(wrapperJs, browser.getURL(), 0);
	}

	@Override
	public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
			CefQueryCallback callback) {
		if (!request.startsWith(PREFIX)) {
			return false;
		}

		String payload = request.substring(PREFIX.length());
		String[] parts = payload.split(":", -1);
		if (parts.length < 1) {
			callback.failure(400, "No method specified");
			return true;
		}

		String methodName = parts[0];
		String[] args = Arrays.copyOfRange(parts, 1, parts.length);

		// Find a matching method
		Method targetMethod = Arrays.stream(bridge.getClass().getMethods()).filter(m -> m.getName().equals(methodName))
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
				callback.success(result.toString());
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.error("Error invoking method: {}", e.getMessage());
			callback.failure(500, "Error invoking method: " + e.getMessage());
		}

		return true;
	}

}
