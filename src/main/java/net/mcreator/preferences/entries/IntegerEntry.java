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

public class IntegerEntry extends PreferencesEntry<Integer> {

	protected transient int min;
	protected transient int max;

	public IntegerEntry(String id, int value) {
		this(id, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public IntegerEntry(String id, int value, int min, int max) {
		super(id, value);
		this.min = min;
		this.max = max;
	}

	@Override public JSpinner getComponent(Window parent, Consumer<EventObject> fct) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(get().intValue(), min, max, 1));
		spinner.addChangeListener(fct::accept);
		return spinner;
	}
}
