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

import com.google.gson.*;
import net.mcreator.ui.MCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Instances of this class store information about certain property (its name, type,
 * minimum and maximum values for number properties, list of allowed values for string properties and so on).
 *
 * @param <T> Type of values this property can take.
 */
public sealed interface IPropertyData<T> permits BuiltInPropertyData, PropertyData {

	Map<String, Class<? extends PropertyData<?>>> typeMappings = new HashMap<>() {{
		put("logic", PropertyData.LogicType.class);
		put("integer", PropertyData.IntegerType.class);
		put("number", PropertyData.NumberType.class);
		put("string", PropertyData.StringType.class);
	}};

	Map<Class<? extends PropertyData<?>>, String> typeMappingsReverse = typeMappings.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	/**
	 * @return The name of this property.
	 */
	String getName();

	/**
	 * @return The type of this property data object.
	 */
	default Class<?> getDataClass() {
		return getClass();
	}

	/**
	 * Provides the default value of type of this property. This is the "null" value of this type, which means
	 * it may be outside value limits defined for a particular property.
	 *
	 * @return A default value of this property's type.
	 */
	@Nonnull T getDefaultValue();

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
	 * @param value Possible value of this property as JsonElement.
	 * @return A value of this property's type.
	 */
	T parseObj(JsonElement value);

	/**
	 * Generates a UI component accepting values of type {@link T} and sets its value to the passed one.
	 *
	 * @param mcreator The future parent window of the component returned.
	 * @param value    Possible value of this property.
	 * @return A UI component that accepts values of type {@link T}.
	 * @throws ClassCastException if the type of passed value doesn't match the type of property or its subtype.
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
	 * We need a custom serializer/deserializer for this class because we need to store the type of property.
	 * Technically type could be determined from properties list, but we don't have a reference to it, and it also
	 * depends on the ME type, so this is second-best option. There is minimal overhead in storing the type.
	 */
	class GSONAdapter implements JsonSerializer<IPropertyData<?>>, JsonDeserializer<IPropertyData<?>> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override
		public IPropertyData<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			PropertyData<?> data = gson.fromJson(jsonObject, typeMappings.get(jsonObject.get("type").getAsString()));
			return !jsonObject.get("name").getAsString().startsWith("CUSTOM:") ? new BuiltInPropertyData<>(data) : data;
		}

		@Override
		public JsonElement serialize(IPropertyData<?> propertyData, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject retVal = new JsonObject();
			retVal.addProperty("name", propertyData.getName());
			retVal.addProperty("type", typeMappingsReverse.get(propertyData.getDataClass()));
			return retVal;
		}
	}

}
