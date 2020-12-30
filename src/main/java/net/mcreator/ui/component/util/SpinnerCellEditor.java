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
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

public class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {

	private final JSpinner spinner;
	private boolean initialValSet;

	public SpinnerCellEditor(JSpinner spinner) {
		this.spinner = spinner;
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent fe) {
				SwingUtilities.invokeLater(() -> {
					if (initialValSet) {
						((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setCaretPosition(1);
					}
				});
			}
		});
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addActionListener(ae -> stopCellEditing());
	}

	@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (!initialValSet) {
			try {
				spinner.setValue(Integer.valueOf((String) value));
			} catch (NumberFormatException nfe) {
				spinner.setValue(0);
			}
		}
		SwingUtilities.invokeLater(() -> ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().requestFocus());
		return spinner;
	}

	@Override public boolean isCellEditable(EventObject eo) {
		if (eo instanceof KeyEvent) {
			KeyEvent ke = (KeyEvent) eo;
			((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setText(String.valueOf(ke.getKeyChar()));
			initialValSet = true;
		} else {
			initialValSet = false;
		}
		return true;
	}

	@Override public Object getCellEditorValue() {
		return spinner.getValue().toString();
	}

	@Override public boolean stopCellEditing() {
		try {
			spinner.commitEdit();
		} catch (java.text.ParseException ignored) {
		}
		return super.stopCellEditing();
	}

}
