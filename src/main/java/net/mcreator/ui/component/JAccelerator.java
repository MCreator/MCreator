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

package net.mcreator.ui.component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;

public class JAccelerator extends JButton {
	private KeyStroke keyStroke;
	public JAccelerator(KeyStroke defaultKey, ActionListener al) {
		keyStroke = defaultKey;

		Border defaultBorder = this.getBorder();

		setText(getKeyText());

		addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {
				setText("Waiting...");
			}

			@Override public void focusLost(FocusEvent e) {
				setText(getKeyText());
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent newKey) {
				if (isFocusOwner()) {
					keyStroke = KeyStroke.getKeyStrokeForEvent(newKey);
					if (al != null)
						al.actionPerformed(new ActionEvent("", 0, ""));
					processFocusEvent(new FocusEvent(JAccelerator.this, FocusEvent.FOCUS_LOST));
				}
			}
		});
	}

	private String getKeyText() {
		String acceleratorText = "";
		if (keyStroke != null) {
			int modifiers = keyStroke.getModifiers();
			if (modifiers > 0) {
				acceleratorText = InputEvent.getModifiersExText(modifiers);
				acceleratorText += " + ";
			}
			acceleratorText += KeyEvent.getKeyText(keyStroke.getKeyCode());
		}
		return acceleratorText;
	}

	public KeyStroke getKey() {
		return keyStroke;
	}
}
