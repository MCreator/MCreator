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

package net.mcreator.ui.component.util;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventButtonGroup extends ButtonGroup {

	private final EventListenerList listenerList = new EventListenerList();

	@Override public void add(AbstractButton b) {
		b.addActionListener(this::fireActionListeners);
		super.add(b);
	}

	public void addActionListener(ActionListener listener) {
		listenerList.add(ActionListener.class, listener);
	}

	public void removeActionListener(ActionListener listener) {
		listenerList.remove(ActionListener.class, listener);
	}

	private void fireActionListeners(ActionEvent actionEvent) {
		Object[] listeners = listenerList.getListenerList();
		String actionCommand = "";
		ButtonModel model = getSelection();
		if (model != null) {
			actionCommand = model.getActionCommand();
		}
		ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(ae);
			}
		}
	}

}