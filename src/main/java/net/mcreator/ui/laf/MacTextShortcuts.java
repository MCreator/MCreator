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

package net.mcreator.ui.laf;

import net.mcreator.io.OS;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MacTextShortcuts {

	public static void installIfMac() {
		if (OS.getOS() != OS.MAC)
			return;

		Keymap keymap = JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
		if (keymap == null)
			return;

		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.META_DOWN_MASK),
				new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e) {
						if (!(e.getSource() instanceof JTextComponent textComponent))
							return;

						if (textComponent.getSelectionStart() != textComponent.getSelectionEnd()) {
							textComponent.replaceSelection("");
							return;
						}

						try {
							int caretPosition = textComponent.getCaretPosition();
							int lineStart = Utilities.getRowStart(textComponent, caretPosition);
							if (lineStart >= 0 && lineStart < caretPosition)
								textComponent.getDocument().remove(lineStart, caretPosition - lineStart);
						} catch (BadLocationException _) {
						}
					}
				});
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.META_DOWN_MASK),
				new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e) {
						if (!(e.getSource() instanceof JTextComponent textComponent))
							return;

						if (textComponent.getSelectionStart() != textComponent.getSelectionEnd()) {
							textComponent.replaceSelection("");
							return;
						}

						try {
							int caretPosition = textComponent.getCaretPosition();
							int lineEnd = Utilities.getRowEnd(textComponent, caretPosition);
							if (lineEnd >= 0 && lineEnd > caretPosition)
								textComponent.getDocument().remove(caretPosition, lineEnd - caretPosition);
						} catch (BadLocationException _) {
						}
					}
				});
	}
}
