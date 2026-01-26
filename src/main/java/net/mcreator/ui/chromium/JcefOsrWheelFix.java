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

package net.mcreator.ui.chromium;

import net.mcreator.io.OS;
import org.cef.browser.CefBrowser;

import java.awt.*;
import java.awt.event.MouseWheelEvent;

final class JcefOsrWheelFix {

	private final CefBrowser browser;
	private final Component source;

	public JcefOsrWheelFix(CefBrowser browser, Component source) {
		this.browser = browser;
		this.source = source;
	}

	public void handle(MouseWheelEvent e) {
		double rotation = e.getPreciseWheelRotation();

		// Chromium has inverted mouse wheel logic on Linux
		if (OS.getOS() == OS.LINUX || OS.getOS() == OS.MAC)
			rotation *= -1;

		// One wheel tick is 120 units in Chromium
		int delta = (int) (rotation * 120);
		MouseWheelEvent synthetic = new MouseWheelEvent(source, MouseWheelEvent.MOUSE_WHEEL, e.getWhen(),
				e.getModifiersEx(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(),
				e.isPopupTrigger(), MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, delta);
		browser.sendMouseWheelEvent(synthetic);
	}

}