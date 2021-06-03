/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MCreatorDialog extends JDialog {

	private static final KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	private static final String actionMapKey = "net.mcreator.ui.dialogs:CLOSE_DIALOG";

	private boolean closable = true;

	public MCreatorDialog(Window w) {
		this(w, "", false);
	}

	public MCreatorDialog(Window w, String title) {
		this(w, title, false);
	}

	public MCreatorDialog(Window w, String title, boolean modal) {
		super(w);
		setModal(modal);
		setTitle(title);
		setIconImage(UIRES.getBuiltIn("icon").getImage());

		Action dispatchClosing = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent event) {
				if (closable) {
					dispatchEvent(new WindowEvent(MCreatorDialog.this, WindowEvent.WINDOW_CLOSING));
					setVisible(false);
					dispatchEvent(new WindowEvent(MCreatorDialog.this, WindowEvent.WINDOW_CLOSED));
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		JRootPane root = getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, actionMapKey);
		root.getActionMap().put(actionMapKey, dispatchClosing);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent we) {
				if (closable)
					dispose();
				else
					Toolkit.getDefaultToolkit().beep();
			}
		});

		super.getContentPane().setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
	}

	@Override public void setSize(int i, int i1) {
		super.setSize(i - 2, i1 + 24);
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

}
