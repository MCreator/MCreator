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

import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class CefClassLoaderSchemeHandler implements CefResourceHandler {

	private static final Logger LOG = LogManager.getLogger(CefClassLoaderSchemeHandler.class);

	private InputStream inputStream;
	private String contentType;

	@Override public boolean processRequest(CefRequest request, CefCallback callback) {
		try {
			String url = request.getURL(); // e.g. classloader://index.html
			String path = url.replaceFirst("^classloader://", "/").replace("[LANG]", L10N.getBlocklyLangName());
			URL resourceUrl = getClass().getResource(path);

			if (resourceUrl == null) {
				return false; // resource not found
			}

			inputStream = resourceUrl.openStream();
			contentType = detectMimeType(path);
			callback.Continue();
			return true;
		} catch (IOException e) {
			LOG.warn("Error opening resource: {}", e.getMessage());
			return false;
		}
	}

	@Override public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
		response.setMimeType(contentType);
		response.setStatus(200);
		responseLength.set(-1);
	}

	@Override public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
		try {
			int n = inputStream.read(dataOut, 0, bytesToRead);
			if (n == -1)
				return false;
			bytesRead.set(n);
			return true;
		} catch (IOException e) {
			LOG.warn("Error reading resource: {}", e.getMessage());
			return false;
		}
	}

	@Override public void cancel() {
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
			default -> "text/plain";
		};
	}

}
