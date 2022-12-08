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

package net.mcreator.ui.minecraft.states;

import javax.swing.*;

public class PropertyData {
	private String name;
	private final Class<?> type;

	private final Number min;
	private final Number max;
	private final String[] arrayData;

	public PropertyData(String name, Class<?> type, Number min, Number max, String[] arrayData) {
		this.name = name;
		this.type = type;
		this.min = min;
		this.max = max;
		this.arrayData = arrayData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> type() {
		return type;
	}

	public Number min() {
		return min;
	}

	public Number max() {
		return max;
	}

	public String[] arrayData() {
		return arrayData;
	}

	public Object parseValue(String value) {
		if (type.equals(Boolean.class))
			return Boolean.parseBoolean(value);
		else if (type.equals(Integer.class))
			return Integer.parseInt(value);
		else if (type.equals(Float.class))
			return Float.parseFloat(value);
		else if (type.equals(String.class))
			return value;
		return null;
	}

	public Object getValueFromComponent(JComponent component) {
		return null;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean setValueOfComponent(JComponent component, Object value) {
		return false;
	}

	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof PropertyData that)
			return this.name.equals(that.name) && this.type == that.type;
		return false;
	}

	@Override public int hashCode() {
		return name.hashCode() * 31 + type.hashCode();
	}
}
