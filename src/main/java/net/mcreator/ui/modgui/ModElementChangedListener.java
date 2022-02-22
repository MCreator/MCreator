/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import javafx.embed.swing.JFXPanel;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.modgui.codeviewer.ModElementCodeViewer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

/**
 * <p>Gets triggered whenever a change to a mod element is detected inside {@link ModElementGUI}</p>
 *
 * @see ModElementGUI#elementUpdateListener
 * @see ModElementCodeViewer#codeChangedListener
 */
public interface ModElementChangedListener
		extends MouseListener, KeyListener, ActionListener, ChangeListener, DocumentListener {

	/**
	 * <p>The main listener method, triggered when an event occurs on a registered container</p>
	 */
	void modElementChanged();

	/**
	 * <p>Registers the given UI component to trigger this listener when a change is detected on it</p>
	 *
	 * @param container The UI element to register
	 */
	default void registerUI(JComponent container) {
		for (Component component : container.getComponents()) {
			if (component instanceof MCItemHolder itemHolder) {
				itemHolder.addBlockSelectedListener(this);
			} else if (component instanceof JItemListField<?> listField) {
				listField.addChangeListener(this);
			} else if (component instanceof JEntriesList entriesList) {
				registerUI(entriesList);
				entriesList.addEntryRegisterListener(c -> {
					registerUI(c);
					modElementChanged();
				});
				component.addMouseListener(this);
			} else if (component instanceof AbstractButton button) {
				button.addActionListener(this);
			} else if (component instanceof JSpinner spinner) {
				spinner.addChangeListener(this);
			} else if (component instanceof JComboBox<?> comboBox) {
				comboBox.addActionListener(this);
			} else if (component instanceof JTextComponent textComponent) {
				textComponent.getDocument().addDocumentListener(this);
			} else if (component instanceof JFXPanel) {
				component.addMouseListener(this);
				component.addKeyListener(this);
			} else if (component instanceof JComponent jcomponent) {
				registerUI(jcomponent);

				if (!(component instanceof JLabel) && !(component instanceof JPanel)) {
					component.addMouseListener(this);
					component.addKeyListener(this);
				}
			}
		}
	}

	// Listener methods

	@Override default void mouseReleased(MouseEvent e) {
		modElementChanged();
	}

	@Override default void keyReleased(KeyEvent e) {
		modElementChanged();
	}

	@Override default void actionPerformed(ActionEvent e) {
		modElementChanged();
	}

	@Override default void stateChanged(ChangeEvent e) {
		modElementChanged();
	}

	@Override default void changedUpdate(DocumentEvent e) {
		modElementChanged();
	}

	@Override default void insertUpdate(DocumentEvent e) {
		modElementChanged();
	}

	@Override default void removeUpdate(DocumentEvent e) {
		modElementChanged();
	}

	@Override default void keyTyped(KeyEvent e) {
	}

	@Override default void keyPressed(KeyEvent e) {
	}

	@Override default void mouseClicked(MouseEvent e) {
	}

	@Override default void mousePressed(MouseEvent e) {
	}

	@Override default void mouseEntered(MouseEvent e) {
	}

	@Override default void mouseExited(MouseEvent e) {
	}
}
