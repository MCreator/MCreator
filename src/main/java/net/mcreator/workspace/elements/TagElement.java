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
import net.mcreator.workspace.Workspace;

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
		if (obj instanceof TagElement other)
			return other.type.equals(type) && other.resourcePath.equals(resourcePath);
		return false;
	}

	// This method is used to serialize TagElement to JSON string for map keys
	@Override public String toString() {
		return type.name() + ":" + resourcePath;
	}

	// Helper functions for tag entries below

	private static final String MANAGED_PREFIX = "~";

	public static String entryFromMappableElement(MappableElement element) {
		return (element.isManaged() ? MANAGED_PREFIX : "") + element.getUnmappedValue();
	}

	public static MappableElement entryToMappableElement(Workspace workspace, TagType type, String entry) {
		MappableElement retval = type.getMappableElementProvider().apply(workspace, getEntryName(entry));
		retval.setManaged(TagElement.isEntryManaged(entry));
		return retval;
	}

	public static boolean isEntryManaged(String rawData) {
		return rawData.startsWith(MANAGED_PREFIX);
	}

	public static String makeEntryManaged(String rawData) {
		return MANAGED_PREFIX + rawData;
	}

	public static String getEntryName(String rawData) {
		return rawData.replace(MANAGED_PREFIX, "");
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

}
