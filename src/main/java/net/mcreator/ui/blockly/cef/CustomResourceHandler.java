/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.blockly.cef;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.nio.ByteBuffer;

public class CustomResourceHandler extends CefResourceRequestHandlerAdapter {

	private int startPos = 0;
	private final byte[] data;
	private final String contentType;

	public CustomResourceHandler(byte[] data, String extension) {
		this.data = data;
		switch (extension) {
		case "ttf":
			this.contentType = "application/octet-stream";
			break;
		case "png":
			this.contentType = "image/png";
			break;
		case "jpeg":
			this.contentType = "image/jpeg";
			break;
		case "css":
			this.contentType = "text/css";
			break;
		case "js":
			this.contentType = "text/javascript";
			break;
		default:
			this.contentType = "text/plain";
		}
	}

	@Override
	public CefResourceHandler getResourceHandler(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest) {
		return new CefResourceHandlerAdapter() {
			@Override public boolean processRequest(CefRequest request, CefCallback callback) {
				startPos = 0;
				callback.Continue();
				return true;
			}

			@Override
			public void getResponseHeaders(CefResponse response, IntRef response_length, StringRef redirectUrl) {
				response.setMimeType(contentType);
				response.setStatus(200);
				response_length.set(data.length);
				response.setHeaderByName("Access-Control-Allow-Origin", "*", true);
			}

			@Override
			public boolean readResponse(byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
				if (startPos + bytes_to_read >= data.length)
					bytes_to_read = data.length - startPos;

				if (bytes_to_read <= 0)
					return false;

				byte[] buffer = new byte[bytes_to_read];
				System.arraycopy(data, startPos, buffer, 0, bytes_to_read);

				ByteBuffer result = ByteBuffer.wrap(data_out);
				result.put(buffer);

				bytes_read.set(bytes_to_read);

				startPos += bytes_to_read;
				return true;
			}

			@Override public void cancel() {
				startPos = 0;
			}
		};
	}

}