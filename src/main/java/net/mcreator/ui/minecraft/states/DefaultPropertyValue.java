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

@JsonAdapter(DefaultPropertyValue.GSONAdapter.class) public record DefaultPropertyValue<T>(PropertyData<T> property,
																						   @Nonnull T defaultValue) {

	@SuppressWarnings("unchecked")
	private static <T> DefaultPropertyValue<T> create(PropertyData<T> property, Object defaultValue) {
		return new DefaultPropertyValue<>(property, (T) defaultValue);
	}

	static class GSONAdapter
			implements JsonSerializer<DefaultPropertyValue<?>>, JsonDeserializer<DefaultPropertyValue<?>> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override
		public DefaultPropertyValue<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			PropertyData<?> data = gson.fromJson(jsonObject,
					PropertyData.typeMappings.get(jsonObject.get("type").getAsString()));
			return create(data, gson.fromJson(jsonObject.get("defaultValue"), data.getDefaultValue().getClass()));
		}

		@Override
		public JsonElement serialize(DefaultPropertyValue<?> value, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject retVal = gson.toJsonTree(value.property).getAsJsonObject();
			retVal.addProperty("defaultValue", gson.toJson(value.defaultValue));
			return retVal;
		}

	}

}
