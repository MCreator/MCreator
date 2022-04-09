/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.action.accelerators;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.AcceleratorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <p>This component is a special {@link JButton} used to listen the keyboard and get the new modifiers and the new key code for an {@link Accelerator}.
 * Some visual aspects are added to help the user.</p>
 */
public class JAcceleratorButton extends JButton {

	private final String acceleratorID;
	private KeyStroke keyStroke;

	public JAcceleratorButton(String text, Accelerator accelerator) {
		super(text);

		acceleratorID = accelerator.getID();
		keyStroke = accelerator.getKeyStroke();

		// We treat ESCAPE differently as we need to disable its behaviour, so we can use it to not set a keystroke
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
			if (keyStroke.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UNDEFINED, 0);

				AcceleratorsManager.INSTANCE.setInCache(accelerator.getID(),
						keyStroke); // We use the cache, so the user can cancel changes.
				setText(AcceleratorDialog.setButtonText(keyStroke));
				return JAcceleratorButton.this.isFocusOwner();
			}

			return false;
		});

		// We change the color, so the user has a better understanding of the current accelerator
		addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {
				setForeground(PreferencesManager.PREFERENCES.ui.interfaceAccentColor);
			}

			@Override public void focusLost(FocusEvent e) {
				setForeground(ThemeLoader.CURRENT_THEME.getColorScheme().getForegroundColor());
				resetKeyboardActions();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (!(e.getModifiers() == 0 && Character.isLetterOrDigit(e.getKeyChar())) && !(e.isShiftDown()
						&& Character.isLetterOrDigit(e.getKeyChar()))) {
					keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());

					AcceleratorsManager.INSTANCE.setInCache(accelerator.getID(),
							keyStroke); // We use the cache, so the user can cancel changes.
					setText(AcceleratorDialog.setButtonText(keyStroke));
					setForeground(ThemeLoader.CURRENT_THEME.getColorScheme().getForegroundColor());
				} else {
					setText(AcceleratorDialog.setButtonText(KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers())));
					setForeground(Color.RED);
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown() && e.getClickCount() == 2) { // reset to default value
					AcceleratorsManager.INSTANCE.setInCache(accelerator.reset());
					setText(AcceleratorDialog.setButtonText(accelerator.getKeyStroke()));
				} else if (e.isControlDown() && e.getClickCount() == 2) { // reset to previous saved value
					AcceleratorsManager.INSTANCE.setInCache(accelerator);
					setText(AcceleratorDialog.setButtonText(accelerator.getKeyStroke()));
				}
			}
		});
	}

	public String getAcceleratorID() {
		return acceleratorID;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}
}
