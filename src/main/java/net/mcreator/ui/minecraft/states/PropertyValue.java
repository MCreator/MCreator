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

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

@JsonAdapter(PropertyValue.GSONAdapter.class)
public record PropertyValue<T>(PropertyData<T> property, @Nonnull T value) {

	@SuppressWarnings("unchecked")
	private static <T> PropertyValue<T> create(PropertyData<T> property, Object value) {
		return new PropertyValue<>(property, (T) value);
	}

	static class GSONAdapter
			implements JsonSerializer<PropertyValue<?>>, JsonDeserializer<PropertyValue<?>> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.registerTypeHierarchyAdapter(PropertyData.class, new PropertyData.GSONAdapter()).create();

		@Override
		public PropertyValue<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			PropertyData<?> data = gson.fromJson(jsonObject, PropertyData.class);
			return create(data, data.parseObj(jsonObject.get("value")));
		}

		@Override
		public JsonElement serialize(PropertyValue<?> value, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject retVal = gson.toJsonTree(value.property).getAsJsonObject();
			retVal.add("value", gson.toJsonTree(value.value));
			return retVal;
		}

	}

}
