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

import net.mcreator.ui.MCreator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Instances of this class store information about certain property (its name, type,
 * minimum and maximum values for number properties, list of allowed values for string properties and so on).
 *
 * @param <T> Type of values this property can take.
 */
public abstract class PropertyData<T> {

	private String name;

	/**
	 * The sole constructor.
	 *
	 * @param name Name of the future property object.
	 */
	protected PropertyData(String name) {
		this.name = name;
	}

	/**
	 * @return The name of this property.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this property to the passed one (used within UI to rename properties).
	 *
	 * @param name The new name of this property.
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Converts passed value of this property to its string representation.
	 *
	 * @param value A value of this property's type.
	 * @return Possible value of this property as a string.
	 * @throws ClassCastException if the type of passed value doesn't match the type of property or its subtype.
	 */
	public abstract String toString(Object value);

	/**
	 * Parses string representation of passed value of this property.
	 *
	 * @param value Possible value of this property as a string.
	 * @return A value of this property's type.
	 */
	public abstract T parseObj(String value);

	/**
	 * Generates a UI component accepting values of type {@link T} and sets its value to the passed one.
	 *
	 * @param mcreator The future parent window of the component returned.
	 * @param value    Possible value of this property.
	 * @return A UI component that accepts values of type {@link T}.
	 * @throws ClassCastException if the type of passed value doesn't match the type of property or its subtype.
	 */
	public abstract JComponent getComponent(MCreator mcreator, @Nullable Object value);

	/**
	 * Extracts possible value of this property from the provided UI component.
	 *
	 * @param component A UI component that accepts values of type {@link T}.
	 * @return A value of this property's type.
	 */
	public abstract T getValue(JComponent component);

	@Override public final boolean equals(Object obj) {
		return super.equals(obj) || obj instanceof PropertyData<?> that && this.name.equals(that.name);
	}

	@Override public final int hashCode() {
		return name.hashCode();
	}

	@Override public final String toString() {
		return getName();
	}

	/**
	 * A subclass for boolean type properties.
	 */
	public static class Logic extends PropertyData<Boolean> {

		public Logic(String name) {
			super(name);
		}

		@Override public final String toString(Object value) {
			return Boolean.toString((Boolean) value);
		}

		@Override public final Boolean parseObj(String value) {
			return Boolean.parseBoolean(value);
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Objects.requireNonNullElse(value, false);
			JCheckBox box = new JCheckBox() {
				@Override public String getText() {
					return isSelected() ? "True" : "False";
				}
			};
			box.setSelected((boolean) value);
			box.setPreferredSize(new Dimension(54, 25));
			return box;
		}

		@Override public Boolean getValue(JComponent component) {
			return ((JCheckBox) component).isSelected();
		}
	}

	/**
	 * A subclass for integer number type properties.
	 */
	public static class IntNumber extends PropertyData<Integer> {
		private final int min;
		private final int max;

		public IntNumber(String name, int min, int max) {
			super(name);
			this.min = min;
			this.max = max;
		}

		@Override public final String toString(Object value) {
			return Integer.toString((Integer) value);
		}

		@Override public final Integer parseObj(String value) {
			return Integer.parseInt(value);
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Math.max(min, Math.min(max, (int) Objects.requireNonNullElse(value, 0)));
			JSpinner box = new JSpinner(new SpinnerNumberModel((int) value, min, max, 1));
			box.setPreferredSize(new Dimension(105, 22));
			return box;
		}

		@Override public Integer getValue(JComponent component) {
			return (Integer) ((JSpinner) component).getValue();
		}
	}

	/**
	 * A subclass for float number type properties.
	 */
	public static class FloatNumber extends PropertyData<Float> {
		private final float min;
		private final float max;

		public FloatNumber(String name, float min, float max) {
			super(name);
			this.min = min;
			this.max = max;
		}

		@Override public final String toString(Object value) {
			return Float.toString((Float) value);
		}

		@Override public final Float parseObj(String value) {
			return Float.parseFloat(value);
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Math.max(min, Math.min(max, (float) Objects.requireNonNullElse(value, 0F)));
			JSpinner box = new JSpinner(new SpinnerNumberModel((float) value, min, max, 0.001));
			box.setPreferredSize(new Dimension(130, 22));
			return box;
		}

		@Override public Float getValue(JComponent component) {
			Number num = (Number) ((JSpinner) component).getValue();
			return Math.round(num.floatValue() * 1000) / 1000F;
		}
	}

	/**
	 * A subclass for string type properties.
	 */
	public static class Text extends PropertyData<String> {
		private final String[] arrayData;

		public Text(String name, String[] arrayData) {
			super(name);
			this.arrayData = arrayData;
		}

		@Override public final String toString(Object value) {
			return (String) value;
		}

		@Override public final String parseObj(String value) {
			return value;
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Objects.requireNonNullElse(value, "");
			JComboBox<String> box = new JComboBox<>(arrayData);
			box.setEditable(false);
			box.setSelectedIndex(Math.max(0, Arrays.asList(arrayData).indexOf((String) value)));
			return box;
		}

		@Override public String getValue(JComponent component) {
			return (String) ((JComboBox<?>) component).getSelectedItem();
		}
	}
}
