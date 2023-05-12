/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Instances of this class store information about certain property (its name, type,
 * minimum and maximum values for number properties, list of allowed values for string properties and so on).
 *
 * @param <T> Type of values this property can take.
 */
public interface IPropertyData<T> {

	/**
	 * @return The name of this property.
	 */
	String getName();

	/**
	 * Provides the default value of type of this property. This is the "null" value of this type, which means
	 * it may be outside value limits defined for a particular property.
	 *
	 * @return A default value of this property's type.
	 */
	@Nonnull T getDefaultValue();

	/**
	 * Checks if the provided value is different from {@code null} and its type matches type of this property
	 * or its subtype. Falls back to the default property value if this condition is not met.
	 *
	 * @param value A value of this property's type.
	 * @return Passed value, or the default one if {@code null}.
	 */
	@SuppressWarnings("unchecked") default T checkValue(Object value) {
		try {
			if (value != null)
				return (T) value;
		} catch (ClassCastException ignored) {
		}
		return getDefaultValue();
	}

	/**
	 * Converts passed value of this property to its string representation.
	 *
	 * @param value A value of this property's type.
	 * @return Possible value of this property as a string.
	 * @throws ClassCastException if the type of passed value doesn't match the type of property or its subtype.
	 */
	String toString(Object value);

	/**
	 * Parses string representation of passed value of this property.
	 *
	 * @param value Possible value of this property as a string.
	 * @return A value of this property's type.
	 */
	T parseObj(String value);

	/**
	 * Generates a UI component accepting values of type {@link T} and sets its value to the passed one.
	 *
	 * @param mcreator The future parent window of the component returned.
	 * @param value    Possible value of this property.
	 * @return A UI component that accepts values of type {@link T}.
	 * @implNote It is recommended to use {@link #checkValue(Object)} to ensure the value is of specified type.
	 */
	JComponent getComponent(MCreator mcreator, @Nullable Object value);

	/**
	 * Extracts possible value of this property from the provided UI component.
	 *
	 * @param component A UI component that accepts values of type {@link T}.
	 * @return A value of this property's type.
	 */
	T getValue(JComponent component);

	/**
	 * Converts passed state string to a property-to-value map basing on the provided list of available properties.
	 *
	 * @param state      String representation of a state.
	 * @param properties List of properties that can be used.
	 * @return Map containing values of properties for the given state.
	 */
	static LinkedHashMap<IPropertyData<?>, Object> passStateToMap(String state, List<IPropertyData<?>> properties) {
		LinkedHashMap<IPropertyData<?>, Object> stateMap = new LinkedHashMap<>();
		Map<String, String> values = Arrays.stream(state.split(","))
				.collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1]));
		for (IPropertyData<?> property : properties) {
			if (values.containsKey(property.getName()))
				stateMap.put(property, property.parseObj(values.get(property.getName())));
		}
		return stateMap;
	}
}
