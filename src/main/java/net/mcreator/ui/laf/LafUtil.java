/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.laf;

import net.mcreator.io.OS;
import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class LafUtil {

	private static final Logger LOG = LogManager.getLogger(LafUtil.class);

	public static void applyDefaultHTMLStyles() {
		try {
			final Field keyField = HTMLEditorKit.class.getDeclaredField("DEFAULT_STYLES_KEY");
			keyField.setAccessible(true);
			final Object key = keyField.get(null);

			Object appContext = Class.forName("sun.awt.AppContext").getMethod("getAppContext").invoke(null);

			StyleSheet defaultStyles = (StyleSheet) appContext.getClass().getMethod("get", Object.class)
					.invoke(appContext, key);

			if (defaultStyles != null) {
				defaultStyles.addRule(
						"a {color: #" + Integer.toHexString(Theme.current().getInterfaceAccentColor().getRGB())
								.substring(2) + ";}");

				appContext.getClass().getMethod("put", Object.class, Object.class)
						.invoke(appContext, key, defaultStyles);
			}
		} catch (Throwable throwable) {
			LOG.warn("Failed to apply custom CSS style sheets", throwable);
		}
	}

	public static void fixMacOSActions() {
		if (OS.getOS() == OS.MAC) {
			applyMacOSActionsFor("TextField.focusInputMap");
			applyMacOSActionsFor("TextArea.focusInputMap");
			applyMacOSActionsFor("TextPane.focusInputMap");
		}
	}

	private static void applyMacOSActionsFor(String key) {
		InputMap im = (InputMap) UIManager.get(key);

		// Copy/paste/select actions
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);

		// Navigation actions
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.META_DOWN_MASK), DefaultEditorKit.beginLineAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.META_DOWN_MASK), DefaultEditorKit.endLineAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), DefaultEditorKit.previousWordAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), DefaultEditorKit.nextWordAction);

		// Navigation selection actions
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				DefaultEditorKit.selectionBeginLineAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				DefaultEditorKit.selectionEndLineAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				DefaultEditorKit.selectionPreviousWordAction);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				DefaultEditorKit.selectionNextWordAction);
	}

}
