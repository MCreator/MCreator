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

package net.mcreator.preferences.entry;

import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.EventObject;
import java.util.function.Consumer;

public class NumberEntry extends PreferenceEntry<Double> {

	public static final int MAX_RAM = (int) (((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize()
					/ 1048576) - 1024;
	protected transient double min, max;
	public NumberEntry(String id, int value, String section) {
		this(id, value, section, Double.NaN, Double.NaN);
	}

	public NumberEntry(String id, double value, String section, double min, double max) {
		super(id, value, section);
		this.min = min;
		this.max = max;
	}

	@Override public JSpinner getComponent(Window parent, Consumer<EventObject> fct) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(Math.round(value), min, max, 1));
		spinner.addChangeListener(fct::accept);
		return spinner;
	}
}
