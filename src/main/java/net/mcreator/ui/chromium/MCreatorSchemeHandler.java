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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MCreatorSchemeHandler implements CefResourceHandler {

	private static final Logger LOG = LogManager.getLogger(MCreatorSchemeHandler.class);

	private static final List<RequestHandler> REQUEST_HANDLERS = new CopyOnWriteArrayList<>();

	private InputStream inputStream;
	private String contentType;

	private final CefBrowser browser;

	/**
	 * Registers an additional request handler that is queried before the default class-loader-based resource
	 * lookup. Handlers are queried in registration order, and the first one to return a non-null stream wins.
	 *
	 * @param handler The request handler to register.
	 */
	public static void registerRequestHandler(RequestHandler handler) {
		REQUEST_HANDLERS.add(handler);
	}

	@SuppressWarnings("unused")
	public MCreatorSchemeHandler(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
		this.browser = browser;
	}

	/**
	 * @return The MCreator instance the WebView holding the CefBrowser this handler serves is part of,
	 * or null if it can't be determined.
	 */
	@SuppressWarnings("resource") @Nullable public MCreator getMCreator() {
		WebView webView = WebView.fromBrowser(browser);
		return webView != null ? webView.getMCreator() : null;
	}

	@Override public boolean processRequest(CefRequest request, CefCallback callback) {
		String path = request.getURL().replaceFirst("^http://mcreator/", "/");

		MCreator mcreator = getMCreator();

		// Give registered request handlers a chance to rewrite the request path
		for (RequestHandler handler : REQUEST_HANDLERS) {
			path = handler.rewritePath(mcreator, path);
		}

		if (path.contains("favicon.ico")) {
			// return empty stream for favicon requests
			inputStream = InputStream.nullInputStream();
		} else {
			// First, give registered request handlers a chance to handle the request
			for (RequestHandler handler : REQUEST_HANDLERS) {
				try {
					InputStream handlerStream = handler.handleRequest(mcreator, path);
					if (handlerStream != null) {
						inputStream = handlerStream;
						break;
					}
				} catch (Exception e) {
					LOG.warn("Error handling request for: {}", path, e);
				}
			}

			if (inputStream == null) {
				inputStream = getClass().getResourceAsStream(path);
				if (inputStream == null) {
					// if resource not found, try to load it from the plugins
					inputStream = PluginLoader.INSTANCE.getResourceAsStream(path.substring(1));
					if (inputStream == null) {
						LOG.warn("Resource not found: {}", path);
						return false; // resource not found
					}
				}
			}
		}

		contentType = detectMimeType(path);
		callback.Continue();
		return true;
	}

	@Override public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
		response.setMimeType(contentType);
		response.setStatus(200);
		responseLength.set(-1);
	}

	@Override public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
		try {
			int n = inputStream.read(dataOut, 0, bytesToRead);
			if (n == -1) {
				closeStream();
				return false;
			}
			bytesRead.set(n);
			return true;
		} catch (IOException e) {
			LOG.warn("Error reading resource: {}", e.getMessage());
			closeStream();
			return false;
		}
	}

	@Override public void cancel() {
		closeStream();
	}

	private void closeStream() {
		try {
			if (inputStream != null)
				inputStream.close();
		} catch (IOException ignored) {
		}
	}

	private String detectMimeType(String path) {
		String extension = "";
		int dot = path.lastIndexOf('.');
		if (dot != -1 && dot < path.length() - 1) {
			extension = path.substring(dot + 1).toLowerCase();
		}

		return switch (extension) {
			case "ttf" -> "application/octet-stream";
			case "png" -> "image/png";
			case "jpeg" -> "image/jpeg";
			case "css" -> "text/css";
			case "js" -> "text/javascript";
			case "html" -> "text/html";
			case "cur" -> "image/x-icon";
			default -> "text/plain";
		};
	}

	public interface RequestHandler {

		/**
		 * Called for each request before resource resolution, allowing the handler to rewrite the request path.
		 * Rewrites of all registered handlers are chained in registration order.
		 *
		 * @param mcreator The MCreator instance the WebView making the request belongs to, or null if unknown.
		 * @param path     The requested resource path (e.g. /blockly/blockly.html), potentially already
		 *                 rewritten by previously registered handlers.
		 * @return The rewritten path, or the passed path unchanged if this handler does not rewrite it.
		 */
		default String rewritePath(@Nullable MCreator mcreator, String path) {
			return path;
		}

		/**
		 * Called for each request before the default class loader based resource lookup.
		 *
		 * @param mcreator The MCreator instance the WebView making the request belongs to, or null if unknown.
		 * @param path     The requested resource path (e.g. /blockly/blockly.html).
		 * @return Stream with the resource contents, or null to pass processing back to the default handler.
		 * @throws Exception If the request handling fails. The request is then passed to the remaining handlers.
		 */
		@Nullable InputStream handleRequest(@Nullable MCreator mcreator, String path) throws Exception;
	}

}
