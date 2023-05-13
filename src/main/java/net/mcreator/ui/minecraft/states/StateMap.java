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
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This object holds the state map. It is a map of property (property name and its description) to value of the state.
 */
@JsonAdapter(StateMap.GSONAdapter.class) public class StateMap extends LinkedHashMap<IPropertyData<?>, Object> {

	private static final Map<String, Class<? extends IPropertyData<?>>> typeMappings = new HashMap<>() {{
		put("logic", PropertyData.LogicType.class);
		put("integer", PropertyData.IntegerType.class);
		put("number", PropertyData.NumberType.class);
		put("string", PropertyData.StringType.class);
	}};

	private static final Map<Class<? extends IPropertyData<?>>, String> typeMappingsReverse = typeMappings.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	/**
	 * We need a custom serializer/deserializer for this class because we need to store the type of property.
	 * Technically type could be determined from properties list, but we don't have a reference to it, and it also
	 * depends on the ME type, so this is second-best option. There is minimal overhead in storing the type.
	 */
	public static class GSONAdapter implements JsonSerializer<StateMap>, JsonDeserializer<StateMap> {

		@Override public StateMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			StateMap stateMap = new StateMap();

			JsonObject jsonObject = json.getAsJsonObject();
			jsonObject.keySet().forEach(propertyName -> {
				JsonObject propertyObject = jsonObject.getAsJsonObject(propertyName);

				String propertyTypeName = propertyObject.get("type").getAsString();
				JsonElement propertyValue = propertyObject.get("value");

				IPropertyData<?> propertyData = context.deserialize(propertyObject, typeMappings.get(propertyTypeName));
				stateMap.put(propertyData, propertyData.parseObj(propertyValue));
			});

			return stateMap;
		}

		@Override public JsonElement serialize(StateMap stateMap, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject retval = new JsonObject();

			// iterate key/value pairs of stateMap
			for (Map.Entry<IPropertyData<?>, Object> entry : stateMap.entrySet()) {
				String propertyName = entry.getKey().getName();
				Object propertyValue = entry.getValue();

				JsonObject propertyObject = new JsonObject();
				propertyObject.addProperty("type", typeMappingsReverse.get(entry.getKey().getClass()));
				propertyObject.add("value", context.serialize(propertyValue));

				retval.add(propertyName, propertyObject);
			}

			return retval;
		}
	}

}
