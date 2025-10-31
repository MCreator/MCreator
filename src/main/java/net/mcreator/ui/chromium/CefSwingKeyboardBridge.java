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

import net.mcreator.Launcher;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefKeyboardHandler;
import org.cef.misc.EventFlags;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public abstract class CefSwingKeyboardBridge implements CefKeyboardHandler {

	@Override public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
		// Only need to forward when no Swing component is in focus
		if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null) {
			return false;
		}

		// Only forward key presses
		if (event.type == CefKeyEvent.EventType.KEYEVENT_RAWKEYDOWN) {
			int keyCode = event.windows_key_code;
			int modifiers = 0;
			if ((event.modifiers & EventFlags.EVENTFLAG_CONTROL_DOWN) != 0) modifiers |= InputEvent.CTRL_DOWN_MASK;
			if ((event.modifiers & EventFlags.EVENTFLAG_SHIFT_DOWN) != 0) modifiers |= InputEvent.SHIFT_DOWN_MASK;
			if ((event.modifiers & EventFlags.EVENTFLAG_ALT_DOWN) != 0) modifiers |= InputEvent.ALT_DOWN_MASK;
			if ((event.modifiers & EventFlags.EVENTFLAG_COMMAND_DOWN) != 0) modifiers |= InputEvent.META_DOWN_MASK;

			// Create a synthetic AWT KeyEvent
			KeyEvent awtEvent = new KeyEvent(
					browser.getUIComponent(),
					KeyEvent.KEY_PRESSED,
					System.currentTimeMillis(),
					modifiers,
					keyCode,
					KeyEvent.CHAR_UNDEFINED
			);

			// Post it to Swing's event queue
			EventQueue.invokeLater(() -> Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(awtEvent));
		}

		return onAfterKeyEvent(browser, event);
	}

	public abstract boolean onAfterKeyEvent(CefBrowser browser, CefKeyEvent event);

}
