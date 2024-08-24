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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This object holds the state map. It is a map of property (property name and its description) to value of the state.
 */
@JsonAdapter(StateMap.GSONAdapter.class) public class StateMap extends LinkedHashMap<PropertyData<?>, Object> {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
			.setStrictness(Strictness.LENIENT)
			.registerTypeHierarchyAdapter(PropertyData.class, new PropertyData.GSONAdapter()).create();

	public static class GSONAdapter implements JsonSerializer<StateMap>, JsonDeserializer<StateMap> {

		@Override public StateMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			StateMap stateMap = new StateMap();

			JsonArray jsonArray = json.getAsJsonArray();
			jsonArray.forEach(jsonElement -> {
				JsonObject entryObject = jsonElement.getAsJsonObject();

				PropertyData<?> propertyData = gson.fromJson(entryObject.get("property"), PropertyData.class);
				stateMap.put(propertyData, propertyData.parseObj(entryObject.get("value")));
			});

			return stateMap;
		}

		@Override public JsonElement serialize(StateMap stateMap, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray retval = new JsonArray();

			// iterate key/value pairs of stateMap
			for (Map.Entry<PropertyData<?>, Object> entry : stateMap.entrySet()) {
				JsonObject propertyObject = new JsonObject();
				propertyObject.add("property", gson.toJsonTree(entry.getKey()));
				propertyObject.add("value", gson.toJsonTree(entry.getValue()));

				retval.add(propertyObject);
			}

			return retval;
		}
	}

}
