/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.element.parts;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(NumberProviderOrFixedValue.GSONAdapter.class) public record NumberProviderOrFixedValue(
		@Nullable NumberProviderEntry entry, double fixedValue) {

	public static class GSONAdapter
			implements JsonSerializer<NumberProviderOrFixedValue>, JsonDeserializer<NumberProviderOrFixedValue> {
		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
				.setStrictness(Strictness.LENIENT).create();

		@Override
		public NumberProviderOrFixedValue deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive()) {
				return new NumberProviderOrFixedValue(null, json.getAsDouble());
			} else if (json.isJsonObject()) {
				JsonObject jsonObj = json.getAsJsonObject();
				JsonObject entryObj = new JsonObject();
				entryObj.addProperty("value", jsonObj.get("entry").getAsString());
				return new NumberProviderOrFixedValue(gson.fromJson(entryObj, NumberProviderEntry.class),
						jsonObj.get("fixedValue").getAsDouble());
			}
			throw new JsonParseException("Invalid number provider with fixed value JSON format");
		}

		@Override
		public JsonElement serialize(NumberProviderOrFixedValue src, Type typeOfSrc,
				JsonSerializationContext context) {
			if (src.entry == null) { // Directly store the fixed value
				return new JsonPrimitive(src.fixedValue);
			} else {
				JsonObject obj = new JsonObject();
				obj.add("entry", gson.toJsonTree(src.entry).getAsJsonObject().get("value")); // Flatten to a string
				obj.add("fixedValue", new JsonPrimitive(src.fixedValue));
				return obj;
			}
		}
	}
}
