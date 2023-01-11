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

/**
 * Instances of this class store information about certain property (its name, type,
 * minimum and maximum values for number properties and list of allowed values for string properties).
 *
 * @implNote If type parameters {@link T} and {@link U} are not the same, methods {@link #toUIValue(Object)}
 * and {@link #fromUIValue(Object)} need to be overridden for proper values conversion between those two types.
 *
 * @param <T> Type of values this property can take.
 * @param <U> Type of this property for representation in UI (usually the same as {@link T}).
 */
public abstract class PropertyData<T, U> {
	private java.lang.String name;
	private final Class<U> uiType;

	/**
	 * @param name   Name of this property object.
	 * @param uiType Type of this property for representation in UI (usually the same as property type).
	 */
	private PropertyData(java.lang.String name, Class<U> uiType) {
		this.name = name;
		this.uiType = uiType;
	}

	/**
	 * @return The name of this property.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name of this property to the passed one (used within UI to rename properties).
	 *
	 * @param name The new name of this property.
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * @return Type of UI representations of possible values of this property.
	 */
	public Class<U> uiType() {
		return uiType;
	}

	/**
	 * @param <N> Type of number - integer or float.
	 * @return The minimum allowed value of this property or {@code null} if property is not of a number type.
	 */
	public <N extends Number> N min() {
		return null;
	}

	/**
	 * @param <N> Type of number - integer or float.
	 * @return The maximum allowed value of this property or {@code null} if property is not of a number type.
	 */
	public <N extends Number> N max() {
		return null;
	}

	/**
	 * @return List of allowed values of this property or an empty array if property is not of the string type.
	 */
	public java.lang.String[] arrayData() {
		return new java.lang.String[0];
	}

	/**
	 * Converts passed value of this property for representation in UI.
	 *
	 * @param value Value of this property's type.
	 * @return UI representation of the value.
	 */
	@SuppressWarnings("unchecked") public U toUIValue(Object value) {
		return (U) value;
	}

	/**
	 * Converts passed value extracted from UI component to a value this property can take.
	 *
	 * @param value UI representation of the value.
	 * @return Value of this property's type.
	 */
	@SuppressWarnings("unchecked") public T fromUIValue(Object value) {
		return (T) value;
	}

	/**
	 * Parses string representation of a passed value of this property.
	 *
	 * @param value Possible value of this property as a string.
	 * @return Value of this property's type.
	 */
	public abstract T parseObj(java.lang.String value);

	@Override public boolean equals(Object obj) {
		return super.equals(obj) || obj instanceof PropertyData<?, ?> that && this.name.equals(that.name);
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	@Override public java.lang.String toString() {
		return getName();
	}

	/**
	 * A subclass for boolean properties.
	 *
	 * @param <T> Type of this property for representation in UI (usually {@link java.lang.Boolean} as well).
	 */
	public static class Boolean<T> extends PropertyData<java.lang.Boolean, T> {
		public Boolean(java.lang.String name, Class<T> uiType) {
			super(name, uiType);
		}

		public static Boolean<java.lang.Boolean> create(java.lang.String name) {
			return new Boolean<>(name, java.lang.Boolean.class);
		}

		@Override public java.lang.Boolean parseObj(java.lang.String value) {
			return java.lang.Boolean.parseBoolean(value);
		}
	}

	/**
	 * A subclass for integer number properties.
	 *
	 * @param <T> Type of this property for representation in UI (usually {@link java.lang.Integer} as well).
	 */
	public static class Integer<T> extends PropertyData<java.lang.Integer, T> {
		private final int min, max;

		public Integer(java.lang.String name, Class<T> uiType, int min, int max) {
			super(name, uiType);
			this.min = min;
			this.max = max;
		}

		public static Integer<java.lang.Integer> create(java.lang.String name, int min, int max) {
			return new Integer<>(name, java.lang.Integer.class, min, max);
		}

		@Override @SuppressWarnings("unchecked") public java.lang.Integer min() {
			return min;
		}

		@Override @SuppressWarnings("unchecked") public java.lang.Integer max() {
			return max;
		}

		@Override public java.lang.Integer parseObj(java.lang.String value) {
			return java.lang.Integer.parseInt(value);
		}
	}

	/**
	 * A subclass for float number properties.
	 *
	 * @param <T> Type of this property for representation in UI (usually {@link java.lang.Float} as well).
	 */
	public static class Float<T> extends PropertyData<java.lang.Float, T> {
		private final float min, max;

		public Float(java.lang.String name, Class<T> uiType, float min, float max) {
			super(name, uiType);
			this.min = min;
			this.max = max;
		}

		public static Float<java.lang.Float> create(java.lang.String name, float min, float max) {
			return new Float<>(name, java.lang.Float.class, min, max);
		}

		@Override @SuppressWarnings("unchecked") public java.lang.Float min() {
			return min;
		}

		@Override @SuppressWarnings("unchecked") public java.lang.Float max() {
			return max;
		}

		@Override public java.lang.Float parseObj(java.lang.String value) {
			return java.lang.Float.parseFloat(value);
		}
	}

	/**
	 * A subclass for string properties.
	 *
	 * @param <T> Type of this property for representation in UI (usually {@link java.lang.String} as well).
	 */
	public static class String<T> extends PropertyData<java.lang.String, T> {
		private final java.lang.String[] arrayData;

		public String(java.lang.String name, Class<T> uiType, java.lang.String[] arrayData) {
			super(name, uiType);
			this.arrayData = arrayData;
		}

		public static String<java.lang.String> create(java.lang.String name, java.lang.String[] arrayData) {
			return new String<>(name, java.lang.String.class, arrayData);
		}

		@Override public java.lang.String[] arrayData() {
			return arrayData;
		}

		@Override public java.lang.String parseObj(java.lang.String value) {
			return value;
		}
	}
}
