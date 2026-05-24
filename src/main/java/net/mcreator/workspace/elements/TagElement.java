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

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.TagType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public record TagElement(TagType type, String resourcePath) implements IElement {

	@Override public String getName() {
		if (resourcePath.contains(":")) {
			return resourcePath.split(":")[1];
		} else {
			return resourcePath;
		}
	}

	public String getMCreatorNamespace() {
		if (resourcePath.contains(":")) {
			return resourcePath.split(":")[0];
		} else {
			return "minecraft";
		}
	}

	public String getMinecraftNamespace(Workspace workspace) {
		String mcreatorNamespace = getMCreatorNamespace();
		if (mcreatorNamespace.equals("mod"))
			return workspace.getWorkspaceSettings().getModID();
		return mcreatorNamespace;
	}

	public int hashCode() {
		return (type + ":" + resourcePath).hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof TagElement(TagType type1, String path))
			return type1.equals(type) && path.equals(resourcePath);
		return false;
	}

	// This method is used to serialize TagElement to JSON string for map keys
	@Nonnull @Override public String toString() {
		return type.name() + ":" + resourcePath;
	}

	// Helper functions for tag entries below

	public static TagElement.Entry entryFromMappableElement(MappableElement element) {
		return element.getAssociatedTagEntry() != null ?
				element.getAssociatedTagEntry() :
				new TagElement.Entry(element.getUnmappedValue(), element.isManaged());
	}

	public static MappableElement entryToMappableElement(Workspace workspace, TagType type, TagElement.Entry entry) {
		MappableElement retval = type.getMappableElementProvider().apply(workspace, entry.name);
		retval.setAssociatedTagEntry(entry);
		return retval;
	}

	public static TagElement fromString(String raw) {
		String[] parts = raw.split(":", 2);
		if (parts.length == 2) {
			return new TagElement(TagType.valueOf(parts[0]), parts[1]);
		} else {
			throw new IllegalArgumentException("Invalid JSON format for TagElement: " + raw);
		}
	}

	public static class TagElementDeserializer implements JsonDeserializer<TagElement> {

		@Override
		public TagElement deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			return fromString(json.getAsJsonPrimitive().getAsString());
		}

	}

	/**
	 * Utility method for normalizing tag name. If input does not start with # or TAG: prefix,
	 * normalization is not performed as it is assumed input is not a tag.
	 * <p>
	 * When input with TAG: (legacy use) prefix is provided, prefix is changed to #.
	 * <p>
	 * In all cases, if no namespace is provided in the input, minecraft: namespace is added.
	 *
	 * @param input Tag name to normalize
	 * @return Normalized tag name
	 */
	public static String normalizeTag(String input) {
		if (input.startsWith("#") || input.startsWith("TAG:")) {
			input = input.replaceFirst("#", "").replaceFirst("TAG:", "");
			if (input.contains(":")) {
				return "#" + input;
			} else {
				return "#minecraft:" + input;
			}
		}
		return input;
	}

	@JsonAdapter(TagElement.Entry.GSONAdapter.class) public record Entry(String name, boolean isManaged) {

		public static Entry unmanaged(String name) {
			return new Entry(name, false);
		}

		public static Entry managed(String name) {
			return new Entry(name, true);
		}

		@Override public boolean equals(Object o) {
			if (!(o instanceof Entry entry))
				return false;

			return name.equals(entry.name);
		}

		@Override public int hashCode() {
			return name.hashCode();
		}

		private static class GSONAdapter implements JsonSerializer<Entry>, JsonDeserializer<Entry> {

			private static final String MANAGED_PREFIX = "~";

			@Override public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				String value = json.getAsString();
				if (value.startsWith(MANAGED_PREFIX)) {
					return managed(value.substring(MANAGED_PREFIX.length()));
				} else {
					return unmanaged(value);
				}
			}

			@Override public JsonElement serialize(Entry entry, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive((entry.isManaged ? MANAGED_PREFIX : "") + entry.name);
			}

		}

	}

}
