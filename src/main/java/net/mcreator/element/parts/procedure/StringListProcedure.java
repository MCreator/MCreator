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

package net.mcreator.element.parts.procedure;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class StringListProcedure extends RetvalProcedure<List<String>> {

	public StringListProcedure(String name, List<String> fixedValue) {
		super(name, fixedValue);
	}

	protected static class GSONAdapter implements JsonDeserializer<StringListProcedure> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@Override
		public StringListProcedure deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			try {
				return gson.fromJson(jsonElement, StringListProcedure.class);
			} catch (Exception e) {
				return new StringListProcedure(null,
						jsonElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList());
			}
		}

	}

}
