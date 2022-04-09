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

	private final String actionID;
	private KeyStroke keyStroke;

	public JAcceleratorButton(String text, BasicAction action) {
		super(text);

		actionID = action.getAccelerator().getID();
		keyStroke = action.getAccelerator().getKeyStroke();

		// We change the color, so the user has a better understanding of the current action
		addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {
				setForeground(PreferencesManager.PREFERENCES.ui.interfaceAccentColor);
			}

			@Override public void focusLost(FocusEvent e) {
				setForeground(ThemeLoader.CURRENT_THEME.getColorScheme().getForegroundColor());
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (!(e.getModifiers() == 0 && Character.isLetterOrDigit(e.getKeyChar())) && !(
						e.isShiftDown() && Character.isLetterOrDigit(e.getKeyChar()))) {
					keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());

					AcceleratorsManager.INSTANCE.setInCache(action.getAccelerator().getID(),
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
					AcceleratorsManager.INSTANCE.setInCache(action.getAccelerator().reset());
					setText(AcceleratorDialog.setButtonText(action.getAccelerator().getKeyStroke()));
				} else if (e.isControlDown() && e.getClickCount() == 2) { // reset to previous saved value
					AcceleratorsManager.INSTANCE.setInCache(action.getAccelerator());
					setText(AcceleratorDialog.setButtonText(action.getAccelerator().getKeyStroke()));
				}
			}
		});
	}

	public String getAcceleratorID() {
		return actionID;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}
}
