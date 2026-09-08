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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.callback.CefCallback;
import org.cef.callback.CefResourceReadCallback;
import org.cef.callback.CefResourceSkipCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.BoolRef;
import org.cef.misc.IntRef;
import org.cef.misc.LongRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class MCreatorSchemeHandler implements CefResourceHandler {

	private static final Logger LOG = LogManager.getLogger(MCreatorSchemeHandler.class);

	private final List<RequestHandler> requestHandlers;

	private InputStream inputStream;
	private String contentType;

	private volatile boolean cancelled = false;

	public MCreatorSchemeHandler(List<RequestHandler> customRequestHandlers) {
		requestHandlers = Objects.requireNonNullElseGet(customRequestHandlers, List::of);
	}

	@Override public boolean processRequest(CefRequest request, CefCallback callback) {
		return open(request, new BoolRef(), callback);
	}

	@Override public boolean open(CefRequest request, BoolRef handleRequest, CefCallback callback) {
		String path = request.getURL().replaceFirst("^http://mcreator/", "/");

		// Give registered request handlers a chance to rewrite the request path
		for (RequestHandler handler : requestHandlers) {
			path = handler.rewritePath(path);
		}

		if (path.contains("favicon.ico")) {
			// return empty stream for favicon requests
			inputStream = InputStream.nullInputStream();
		} else {
			// First, give registered request handlers a chance to handle the request
			for (RequestHandler handler : requestHandlers) {
				try {
					InputStream handlerStream = handler.handleRequest(path);
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
		return read(dataOut, bytesToRead, bytesRead, null);
	}

	@Override public boolean read(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefResourceReadCallback callback) {
		if (cancelled)
			return false;

		try {
			int n = inputStream.read(dataOut, 0, bytesToRead);
			if (n == -1) {
				closeStream();
				return false;
			}
			bytesRead.set(n);
			return true;
		} catch (IOException e) {
			if (!cancelled)
				LOG.warn("Error reading resource: {}", e.getMessage());
			closeStream();
			return false;
		}
	}

	@Override public boolean skip(long bytesToSkip, LongRef bytesSkipped, CefResourceSkipCallback callback) {
		if (cancelled) {
			bytesSkipped.set(-2);
			return false;
		}

		try {
			inputStream.skipNBytes(bytesToSkip);
			bytesSkipped.set(bytesToSkip);
			return true;
		} catch (IOException e) {
			if (!cancelled)
				LOG.warn("Error skipping resource: {}", e.getMessage());
			bytesSkipped.set(-2);
			return false;
		}
	}

	@Override public void cancel() {
		cancelled = true;
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
			case "svg" -> "image/svg+xml";
			case "css" -> "text/css";
			case "js" -> "text/javascript";
			case "html" -> "text/html";
			case "cur" -> "image/x-icon";
			default -> "text/plain";
		};
	}

}
