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
import com.sun.jna.Pointer;
import net.mcreator.ui.chromium.CefUtils;
import net.mcreator.util.TestUtil;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsrWithHandler;
import org.cef.browser.CefRendering;
import org.cef.browser.CefRequestContext;
import org.cef.handler.CefRenderHandler;

import javax.swing.*;
import java.awt.*;

public class CefBrowserOsrCustom extends CefBrowserOsrWithHandler {

	private final boolean isTransparent;

	public CefBrowserOsrCustom(CefClient client, String url, CefRendering.CefRenderingWithHandler renderingWithHandler,
			boolean isTransparent) {
		super(client, url, null, renderingWithHandler.getRenderHandler(), renderingWithHandler.getComponent(),
				CefUtils.getCefBrowserSettings());
		this.isTransparent = isTransparent;
	}

	private CefBrowserOsrCustom(CefClient client, String url, CefRequestContext context, CefRenderHandler renderHandler,
			Component component, CefBrowser parent, Point inspectAt) {
		super(client, url, context, renderHandler, component, parent, inspectAt, null);
		this.isTransparent = false;
	}

	// We need to override this one to provide a valid native window handle, or JS prompt in CefJavaBridgeHandler causes error logs
	@Override public void createImmediately() {
		long windowHandle = getNativeWindowHandle(this.getUIComponent());
		if (this.getParentBrowser() == null) {
			this.createBrowser(this.getClient(), windowHandle, this.getUrl(), true, isTransparent, null);
		} else {
			this.createDevTools(this.getParentBrowser(), this.getClient(), windowHandle, true, false, null,
					this.getInspectAt());
		}
	}

	@Override
	protected CefBrowser createDevToolsBrowser(CefClient client, String url, CefRequestContext context,
			CefBrowser parent, Point inspectAt) {
		return new CefBrowserOsrCustom(client, url, context, this.getRenderHandler(), this.getUIComponent(), parent,
				inspectAt);
	}

	public static long getNativeWindowHandle(Component component) {
		if (TestUtil.isRunningInGitHubActions()) {
			// A hack to silence "Default dialog implementation requires a parent window handle; canceling the JS dialog" errors
			// logged in GitHub Actions due to lack of a display
			return 1;
		}

		Window window = SwingUtilities.getWindowAncestor(component);

		if (window == null)
			return 0;

		// On Wayland, the window handle is not available and crashes the software, so return 0 and don't call getWindowID
		if (OS.isLinux() && "wayland".equals(System.getenv("XDG_SESSION_TYPE"))) {
			return 0;
		}

		if (OS.isWindows()) {
			Pointer ptr = Native.getWindowPointer(window);
			long nativeWindowHandle = Pointer.nativeValue(ptr);
			if (nativeWindowHandle != 0)
				return Pointer.nativeValue(ptr);
		}

		return Native.getWindowID(window);
	}

}
