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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
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

import java.io.IOException;
import java.io.InputStream;

class CefClassLoaderSchemeHandler implements CefResourceHandler {

	private static final Logger LOG = LogManager.getLogger(CefClassLoaderSchemeHandler.class);

	private String contentType;

	private int offset = 0;
	private byte[] resourceData;

	private static final String blocklyThemeID;

	static {
		String _blocklyThemeID = Theme.current().getID();
		if (PluginLoader.INSTANCE.getResourceAsStream(
				String.format("themes/%s/styles/blockly.css", Theme.current().getID())) == null) {
			_blocklyThemeID = "default_dark"; // fallback to the default dark theme
		}
		blocklyThemeID = _blocklyThemeID;
	}

	@SuppressWarnings("unused")
	public CefClassLoaderSchemeHandler(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
	}

	@Override public boolean processRequest(CefRequest request, CefCallback callback) {
		String path = request.getURL().replaceFirst("^classloader://", "/")
				//@formatter:off
				.replace("__LANG__", L10N.getBlocklyLangName())
				.replace("__BLOCKLY_THEME_ID__", blocklyThemeID)
				//@formatter:on
				;

		InputStream inputStream = getClass().getResourceAsStream(path);
		if (inputStream == null) {
			// if resource not found, try to load it from the plugins
			inputStream = PluginLoader.INSTANCE.getResourceAsStream(path.substring(1));
			if (inputStream == null) {
				LOG.warn("Resource not found: {}", path);
				return false; // resource not found
			}
		}

		try {
			resourceData = inputStream.readAllBytes();
			inputStream.close();
		} catch (IOException e) {
			LOG.warn("Error reading resource: {}", path, e);
			return false;
		}

		contentType = detectMimeType(path);
		callback.Continue();
		return true;
	}

	@Override public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
		response.setStatus(200);
		response.setStatusText("OK");
		response.setMimeType(contentType);
		responseLength.set(resourceData != null ? resourceData.length : 0);
	}

	@Override
	public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
		if (resourceData == null || offset >= resourceData.length) {
			bytesRead.set(0);
			return false; // EOF
		}

		// Calculate how many bytes CEF wants vs. how many we have left
		int bytesToCopy = Math.min(bytesToRead, resourceData.length - offset);

		// Copy our cached byte array into the CEF JNI buffer
		System.arraycopy(resourceData, offset, dataOut, 0, bytesToCopy);

		offset += bytesToCopy;
		bytesRead.set(bytesToCopy);
		return true;
	}

	@Override public void cancel() {
		resourceData = null;
		offset = 0;
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

}
