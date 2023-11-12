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

import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.component.ITechnicalComponent;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;

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
 * <p>One case of this listener usage is warning the user about some unsaved changes made to a mod element.
 * Another case is the {@link net.mcreator.ui.modgui.codeviewer.ModElementCodeViewer ModElementCodeViewer} which
 * regenerates the code preview upon UI changes.</p>
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
	 * @param component The UI element to register
	 */
	default void registerUI(JComponent component) {
		if (component instanceof ITechnicalComponent)
			return; // don't add listeners if component triggers actions not affecting mod element directly

		if (component instanceof MCItemHolder itemHolder) {
			itemHolder.addBlockSelectedListener(this);
		} else if (component instanceof JColor jcolor) {
			jcolor.addColorSelectedListener(this);
		} else if (component instanceof SoundSelector soundSelector) {
			soundSelector.addSoundSelectedListener(this);
		} else if (component instanceof JItemListField<?> listField) {
			listField.addChangeListener(this);
		} else if (component instanceof JStringListField stringList) {
			stringList.addChangeListener(this);
		} else if (component instanceof JEntriesList entriesList) {
			entriesList.addEntryRegisterListener(c -> {
				registerUI(c);
				modElementChanged();
			});
		} else if (component instanceof AbstractButton button) {
			button.addActionListener(this);
		} else if (component instanceof JSpinner spinner) {
			spinner.addChangeListener(this);
		} else if (component instanceof JComboBox<?> comboBox) {
			comboBox.addActionListener(this);
		} else if (component instanceof JTextComponent textComponent) {
			textComponent.getDocument().addDocumentListener(this);
		} else if (component instanceof BlocklyPanel blocklyPanel) {
			blocklyPanel.addChangeListener(this);
		} else {
			if (!isGenericComponent(component)) {
				component.addMouseListener(this);
				component.addKeyListener(this);
			}

			for (Component subComponent : component.getComponents()) {
				if (subComponent instanceof JComponent jcomponent)
					registerUI(jcomponent);
			}
		}
	}

	private boolean isGenericComponent(Component component) {
		return component instanceof JLabel || component instanceof JPanel || component instanceof JScrollBar
				|| component instanceof JScrollPane || component instanceof JViewport;
	}

	// Trigger methods

	@Override default void mouseReleased(MouseEvent e) {
		modElementChanged();
	}

	@Override default void keyReleased(KeyEvent e) {
		modElementChanged();
	}

	@Override default void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			if (e.getModifiers() != 0)
				modElementChanged();
		} else {
			modElementChanged();
		}
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
