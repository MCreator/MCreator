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

import java.lang.management.ManagementFactory;

public class NumberEntry extends PreferenceEntry<Integer> {

	public static int MAX_RAM = (int) (((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize()
					/ 1048576) - 1024;
	private final double min, max;

	public NumberEntry(String id, int value, PreferenceSection section, double min, double max) {
		super(id, value, section);
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
