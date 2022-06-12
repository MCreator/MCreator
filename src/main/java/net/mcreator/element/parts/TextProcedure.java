/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import java.lang.reflect.Type;

public class TextProcedure extends Procedure {

	private final String fixedText;

	public TextProcedure(String name, String fixedText) {
		super(name);
		this.fixedText = fixedText;
	}

	public String getFixedText() {
		return fixedText;
	}

	@Override public String toString() {
		return fixedText;
	}

	public static class GSONAdapter implements JsonDeserializer<TextProcedure> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override
		public TextProcedure deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			try {
				return gson.fromJson(jsonElement, TextProcedure.class);
			} catch (Exception e) {
				return new TextProcedure(null, jsonElement.getAsString());
			}
		}
	}
}
