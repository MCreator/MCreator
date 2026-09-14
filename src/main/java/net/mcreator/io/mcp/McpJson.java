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

package net.mcreator.io.mcp;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public final class McpJson {

	private static final Gson LENIENT_GSON = createLenientGson();

	private McpJson() {}

	public static Gson lenientGson() {
		return LENIENT_GSON;
	}

	public static <T> T fromJson(JsonElement json, Class<T> type) {
		return LENIENT_GSON.fromJson(json, type);
	}

	private static Gson createLenientGson() {
		LenientJsonTypeAdapterFactory factory = new LenientJsonTypeAdapterFactory();
		return new GsonBuilder().registerTypeAdapterFactory(factory).create();
	}

	private static final class LenientJsonTypeAdapterFactory implements TypeAdapterFactory {

		@Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			Class<?> rawType = type.getRawType();
			if (!Map.class.isAssignableFrom(rawType) && !Collection.class.isAssignableFrom(rawType)
					&& !JsonElement.class.isAssignableFrom(rawType)) {
				return null;
			}

			TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
			return new TypeAdapter<>() {
				@Override public void write(JsonWriter out, T value) throws IOException {
					delegate.write(out, value);
				}

				@Override public T read(JsonReader in) throws IOException {
					if (in.peek() == JsonToken.STRING) {
						String json = in.nextString();
						if (json.isBlank()) {
							return null;
						}
						if (JsonElement.class.isAssignableFrom(rawType)) {
							@SuppressWarnings("unchecked") T parsed = (T) JsonParser.parseString(json);
							return parsed;
						}
						return gson.fromJson(json, type.getType());
					}
					return delegate.read(in);
				}
			};
		}
	}

}
