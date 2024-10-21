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
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;

import java.util.ArrayList;
import java.util.List;

public class CefMultiLoadHandler extends CefLoadHandlerAdapter {

	private final List<CefLoadHandler> handlers = new ArrayList<>();

	public void addHandler(CefLoadHandler handler) {
		handlers.add(handler);
	}

	public void removeHandler(CefLoadHandler handler) {
		handlers.remove(handler);
	}

	@Override
	public void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType) {
		super.onLoadStart(cefBrowser, cefFrame, transitionType);
		handlers.forEach(handler -> handler.onLoadStart(cefBrowser, cefFrame, transitionType));
	}

	@Override public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i) {
		super.onLoadEnd(cefBrowser, cefFrame, i);
		handlers.forEach(handler -> handler.onLoadEnd(cefBrowser, cefFrame, i));
	}

}

