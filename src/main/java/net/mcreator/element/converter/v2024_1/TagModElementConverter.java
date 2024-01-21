/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2024_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.minecraft.TagType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.TagElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagModElementConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(TagModElementConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			TagType type = fromLegacyName(definition.get("type").getAsString());

			if (type != null) {
				String name = definition.get("name").getAsString();
				String namespace = definition.get("namespace").getAsString();

				TagElement tagElement = new TagElement(type, namespace + ":" + name);
				if (!workspace.getTagElements().containsKey(tagElement)) {
					workspace.addTagElement(tagElement);
				}

				for (JsonElement value : switch (type) {
					case ITEMS -> definition.getAsJsonArray("items");
					case BLOCKS -> definition.getAsJsonArray("blocks");
					case ENTITIES -> definition.getAsJsonArray("entities");
					case FUNCTIONS -> definition.getAsJsonArray("functions");
					case BIOMES -> definition.getAsJsonArray("biomes");
					case DAMAGE_TYPES -> definition.getAsJsonArray("damageTypes");
				}) {
					workspace.getTagElements().get(tagElement).add(type == TagType.FUNCTIONS ?
							value.getAsString() :
							value.getAsJsonObject().get("value").getAsString());
				}
			} else {
				throw new NullPointerException("Tag type is null / unknown tag type");
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert tag mod element to tag element", e);
		}

		return null; // Mod element was converted to non-mod element form
	}

	@Override public int getVersionConvertingTo() {
		return 60;
	}

	private TagType fromLegacyName(String readableName) {
		return switch (readableName) {
			case "Items" -> TagType.ITEMS;
			case "Blocks" -> TagType.BLOCKS;
			case "Entities" -> TagType.ENTITIES;
			case "Functions" -> TagType.FUNCTIONS;
			case "Biomes" -> TagType.BIOMES;
			case "Damage types" -> TagType.DAMAGE_TYPES;
			default -> null;
		};
	}

}
