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

import com.google.gson.JsonElement;
import net.mcreator.ui.MCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public abstract non-sealed class PropertyData<T> implements IPropertyData<T> {
	private String name;
	public transient UUID uuid;

	private PropertyData() {
		this.uuid = UUID.randomUUID();
	}

	/**
	 * The sole constructor.
	 *
	 * @param name Name of the future property object.
	 */
	protected PropertyData(String name) {
		this();
		this.name = name;
	}

	@Override public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this property to the passed one (used within UI to rename custom properties).
	 *
	 * @param name The new name of this property.
	 */
	public final void setName(String name) {
		this.name = name;
	}

	@Override public final boolean equals(Object obj) {
		return super.equals(obj) || obj instanceof PropertyData<?> that && this.uuid.equals(that.uuid);
	}

	@Override public final int hashCode() {
		return uuid.hashCode();
	}

	@Override public final String toString() {
		return getName();
	}

	/**
	 * A subclass for boolean type properties.
	 */
	public static class LogicType extends PropertyData<Boolean> {

		public LogicType(String name) {
			super(name);
		}

		@Override @Nonnull public Boolean getDefaultValue() {
			return false;
		}

		@Override public final String toString(Object value) {
			return Boolean.toString((Boolean) value);
		}

		@Override public final Boolean parseObj(JsonElement value) {
			return value.getAsBoolean();
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			JCheckBox box = new JCheckBox() {
				@Override public String getText() {
					return isSelected() ? "true" : "false";
				}
			};
			box.setSelected(Objects.requireNonNullElse((Boolean) value, getDefaultValue()));
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
	public static class IntegerType extends PropertyData<Integer> {
		private final int min, max;

		public IntegerType(String name) {
			this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}

		public IntegerType(String name, int min, int max) {
			super(name);
			this.min = min;
			this.max = max;
		}

		@Override @Nonnull public Integer getDefaultValue() {
			return 0;
		}

		@Override public final String toString(Object value) {
			return Integer.toString((Integer) value);
		}

		@Override public final Integer parseObj(JsonElement value) {
			return value.getAsInt();
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Math.max(min, Math.min(max, Objects.requireNonNullElse((Integer) value, getDefaultValue())));
			JSpinner box = new JSpinner(new SpinnerNumberModel((int) value, min, max, 1));
			box.setPreferredSize(new Dimension(105, 22));
			return box;
		}

		@Override public Integer getValue(JComponent component) {
			return (Integer) ((JSpinner) component).getValue();
		}
	}

	/**
	 * A subclass for fractional number type properties.
	 */
	public static class NumberType extends PropertyData<Double> {
		private static final DecimalFormat df = new DecimalFormat("#.#########",
				DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		private final double min, max;

		public NumberType(String name) {
			this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}

		public NumberType(String name, double min, double max) {
			super(name);
			this.min = min;
			this.max = max;
		}

		@Override @Nonnull public Double getDefaultValue() {
			return 0d;
		}

		@Override public final String toString(Object value) {
			return df.format((double) (Double) value);
		}

		@Override public final Double parseObj(JsonElement value) {
			return value.getAsDouble();
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			value = Math.max(min, Math.min(max, Objects.requireNonNullElse((Double) value, getDefaultValue())));
			JSpinner box = new JSpinner(new SpinnerNumberModel((double) value, min, max, 0.000000001));
			box.setEditor(new JSpinner.NumberEditor(box, "#.#########"));
			((JSpinner.NumberEditor) box.getEditor()).getFormat().setMaximumFractionDigits(9);
			box.setPreferredSize(new Dimension(130, 22));
			return box;
		}

		@Override public Double getValue(JComponent component) {
			return (Double) ((JSpinner) component).getValue();
		}
	}

	/**
	 * A subclass for string type properties.
	 */
	public static class StringType extends PropertyData<String> {
		private final String[] arrayData;

		public StringType(String name, String[] arrayData) {
			super(name);
			this.arrayData = arrayData;
		}

		@Override @Nonnull public String getDefaultValue() {
			return "";
		}

		@Override public final String toString(Object value) {
			return (String) value;
		}

		@Override public final String parseObj(JsonElement value) {
			return value.getAsString();
		}

		@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
			JComboBox<String> box = new JComboBox<>(arrayData);
			box.setEditable(false);
			box.setSelectedItem(Objects.requireNonNullElse((String) value, getDefaultValue()));
			return box;
		}

		@Override public String getValue(JComponent component) {
			return (String) ((JComboBox<?>) component).getSelectedItem();
		}
	}
}
