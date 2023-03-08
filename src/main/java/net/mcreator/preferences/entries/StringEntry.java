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

package net.mcreator.preferences.entries;

import net.mcreator.preferences.PreferencesEntry;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

public class StringEntry extends PreferencesEntry<String> {

	private transient final String[] choices;
	private transient final boolean editable;

	public StringEntry(String id, String value, String... choices) {
		this(id, value, false);
	}

	public StringEntry(String id, String value, boolean editable, String... choices) {
		super(id, value);
		this.choices = choices;
		this.editable = editable;
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		JComboBox<String> box = new JComboBox<>(choices);
		box.setEditable(editable);
		box.setSelectedItem(value);
		box.addActionListener(fct::accept);
		return box;
	}
}
