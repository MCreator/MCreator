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

package net.mcreator.workspace.elements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.TagType;

public record TagElement(TagType type, String resourcePath) implements IElement {

	@Override public String getName() {
		if (resourcePath.contains(":")) {
			return resourcePath.split(":")[1];
		} else {
			return resourcePath;
		}
	}

	public String getNamespace() {
		if (resourcePath.contains(":")) {
			return resourcePath.split(":")[0];
		} else {
			return "minecraft";
		}
	}

	public int hashCode() {
		return (type + ":" + resourcePath).hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof TagElement other)
			return other.type.equals(type) && other.resourcePath.equals(resourcePath);
		return false;
	}

	// This method is used to serialize TagElement to JSON string for map keys
	@Override public String toString() {
		return type.name() + ":" + resourcePath;
	}

	// Helper functions for tag entries below

	public static String entryFromMappableElement(MappableElement element) {
		return (element.isManaged() ? "~" : "") + element.getUnmappedValue();
	}

	public static String getEntryName(String rawData) {
		return rawData.replace("~", "");
	}

	public static boolean isEntryManaged(String rawData) {
		return rawData.startsWith("~");
	}

	public static class TagElementDeserializer implements JsonDeserializer<TagElement> {

		@Override
		public TagElement deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			String raw = json.getAsJsonPrimitive().getAsString();
			String[] parts = raw.split(":", 2);
			if (parts.length == 2) {
				return new TagElement(TagType.valueOf(parts[0]), parts[1]);
			} else {
				throw new IllegalArgumentException("Invalid JSON format for TagElement: " + raw);
			}
		}

	}

}
