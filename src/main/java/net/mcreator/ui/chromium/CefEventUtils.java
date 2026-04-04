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

import org.cef.handler.CefKeyboardHandler;
import org.cef.misc.EventFlags;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

final class CefEventUtils {

	private static final Map<Integer, Integer> CEF_2_JAVA_KEYCODES = new HashMap<>();
	private static final Map<Integer, Integer> CEF_2_JAVA_MODIFIERS = new HashMap<>();

	static {
		// CEF uses Windows VK: https://github.com/adobe/webkit/blob/master/Source/WebCore/platform/chromium/KeyboardCodes.h
		CEF_2_JAVA_KEYCODES.put(0x0d, KeyEvent.VK_ENTER);
		CEF_2_JAVA_KEYCODES.put(0x08, KeyEvent.VK_BACK_SPACE);
		CEF_2_JAVA_KEYCODES.put(0x09, KeyEvent.VK_TAB);

		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_CONTROL_DOWN, InputEvent.CTRL_DOWN_MASK);
		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_SHIFT_DOWN, InputEvent.SHIFT_DOWN_MASK);
		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_ALT_DOWN, InputEvent.ALT_DOWN_MASK);
		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_LEFT_MOUSE_BUTTON, InputEvent.BUTTON1_DOWN_MASK);
		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_MIDDLE_MOUSE_BUTTON, InputEvent.BUTTON2_DOWN_MASK);
		CEF_2_JAVA_MODIFIERS.put(EventFlags.EVENTFLAG_RIGHT_MOUSE_BUTTON, InputEvent.BUTTON3_DOWN_MASK);
	}

	public static KeyEvent convertCefKeyEvent(CefKeyboardHandler.CefKeyEvent cefKeyEvent, Component source) {
		int id = convertCefKeyEventType(cefKeyEvent);
		int keyCode = (id == KeyEvent.KEY_TYPED) ? KeyEvent.VK_UNDEFINED : convertCefKeyEventKeyCode(cefKeyEvent);

		//noinspection MagicConstant
		return new KeyEvent(source, id, System.currentTimeMillis(), convertCefKeyEventModifiers(cefKeyEvent), keyCode,
				cefKeyEvent.character, KeyEvent.KEY_LOCATION_UNKNOWN);
	}

	public static int convertCefKeyEventType(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
		return switch (cefKeyEvent.type) {
			case KEYEVENT_RAWKEYDOWN, KEYEVENT_KEYDOWN -> KeyEvent.KEY_PRESSED;
			case KEYEVENT_KEYUP -> KeyEvent.KEY_RELEASED;
			case KEYEVENT_CHAR -> KeyEvent.KEY_TYPED;
		};
	}

	public static int convertCefKeyEventKeyCode(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
		Integer value = CEF_2_JAVA_KEYCODES.get(cefKeyEvent.windows_key_code);
		if (value != null)
			return value;
		return cefKeyEvent.windows_key_code;
	}

	public static int convertCefKeyEventModifiers(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
		int javaModifiers = 0;

		for (Map.Entry<Integer, Integer> entry : CEF_2_JAVA_MODIFIERS.entrySet()) {
			if ((cefKeyEvent.modifiers & entry.getKey()) != 0) {
				javaModifiers |= entry.getValue();
			}
		}
		return javaModifiers;
	}

	public static boolean isUpDownKeyEvent(CefKeyboardHandler.CefKeyEvent cefKeyEvent) {
		return cefKeyEvent.windows_key_code == KeyEvent.VK_UP || cefKeyEvent.windows_key_code == KeyEvent.VK_DOWN;
	}

}
