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

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.mcreator.ui.MCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Instances of this class store information about certain property (its name, type,
 * minimum and maximum values for number properties, list of allowed values for string properties and so on).
 *
 * @param <T> Type of values this property can take.
 */
@JsonAdapter(PropertyData.GSONAdapter.class) public abstract class PropertyData<T> {

	private static final Map<String, Class<? extends PropertyData<?>>> typeMappings = new HashMap<>() {{
		put("logic", PropertyData.LogicType.class);
		put("integer", PropertyData.IntegerType.class);
		put("number", PropertyData.NumberType.class);
		put("string", PropertyData.StringType.class);
	}};

	private static final Map<Class<? extends PropertyData<?>>, String> typeMappingsReverse = typeMappings.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	private final String name;

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
	 * Adds the prefix to the name of this property if it is not a built-in property and returns the result.
	 * Can be used for cases where property names need to be unique across multiple mod elements that could define property with the same name.
	 *
	 * @param prefix Prefix to add to the name
	 * @return The name of this property with the prefix
	 */
	@SuppressWarnings("unused") public final String getPrefixedName(String prefix) {
		return !name.startsWith("CUSTOM:") ? name : "CUSTOM:" + prefix + name.substring(7);
	}

	/**
	 * Provides the default value of type of this property. This is the "null" value of this type, which means
	 * it may be outside value limits defined for a particular property.
	 *
	 * @return A default value of this property's type.
	 */
	@Nonnull public abstract T getDefaultValue();

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
	 * @param value Possible value of this property as JsonElement.
	 * @return A value of this property's type.
	 */
	public abstract T parseObj(JsonElement value);

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

		public int getMin() {
			return min;
		}

		public int getMax() {
			return max;
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
			this(name, -Double.MAX_VALUE, Double.MAX_VALUE);
		}

		public NumberType(String name, double min, double max) {
			super(name);
			this.min = min;
			this.max = max;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
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

		public StringType(String name) {
			this(name, null);
		}

		public StringType(String name, String[] arrayData) {
			super(name);
			this.arrayData = arrayData;
		}

		public String[] getArrayData() {
			return arrayData;
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
			if (arrayData != null) {
				JComboBox<String> box = new JComboBox<>(arrayData);
				box.setEditable(false);
				box.setSelectedItem(Objects.requireNonNullElse((String) value, getDefaultValue()));
				return box;
			} else {
				JTextField box = new JTextField(10);
				box.setText(Objects.requireNonNullElse((String) value, getDefaultValue()));
				return box;
			}
		}

		@Override public String getValue(JComponent component) {
			return (String) (component instanceof JComboBox<?> ?
					((JComboBox<?>) component).getSelectedItem() :
					((JTextField) component).getText());
		}
	}

	/**
	 * We need a custom serializer/deserializer for this class because we need to store the type of property.
	 * Technically type could be determined from properties list, but we don't have a reference to it, and it also
	 * depends on the ME type, so this is second-best option. There is minimal overhead in storing the type.
	 */
	static class GSONAdapter implements JsonSerializer<PropertyData<?>>, JsonDeserializer<PropertyData<?>> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override public PropertyData<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			return gson.fromJson(jsonObject, typeMappings.get(jsonObject.get("type").getAsString()));
		}

		@Override
		public JsonElement serialize(PropertyData<?> propertyData, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject retVal = gson.toJsonTree(propertyData).getAsJsonObject();
			retVal.addProperty("type", typeMappingsReverse.get(propertyData.getClass()));
			return retVal;
		}
	}

}
