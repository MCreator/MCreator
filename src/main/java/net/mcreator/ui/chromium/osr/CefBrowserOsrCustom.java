/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.chromium.osr;

import com.sun.jna.Native;
import org.cef.CefClient;
import org.cef.browser.CefBrowserOsrWithHandler;
import org.cef.browser.CefRendering;

import javax.swing.*;
import java.awt.*;

public class CefBrowserOsrCustom extends CefBrowserOsrWithHandler {

	public CefBrowserOsrCustom(CefClient client, String url, CefRendering.CefRenderingWithHandler renderingWithHandler) {
		super(client, url, null, renderingWithHandler.getRenderHandler(), renderingWithHandler.getComponent(), null);
	}

	// We need to override this one to provide a valid native window handle, or JS prompt in CefJavaBridgeHandler causes error logs
	@Override public void createImmediately() {
		long windowHandle = getNativeWindowHandle(this.getUIComponent());
		if (this.getParentBrowser() == null) {
			this.createBrowser(this.getClient(), windowHandle, this.getUrl(), true, false, null);
		} else {
			this.createDevTools(this.getParentBrowser(), this.getClient(), windowHandle, true, false, null,
					this.getInspectAt());
		}
	}

	public static long getNativeWindowHandle(Component component) {
		Window window = SwingUtilities.getWindowAncestor(component);
		if (window == null)
			return 0;
		return Native.getWindowID(window);
	}

}
