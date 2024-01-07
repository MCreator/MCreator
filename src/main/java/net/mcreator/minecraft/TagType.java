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

package net.mcreator.minecraft;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.ui.init.L10N;

import java.awt.*;
import java.util.Locale;

public enum TagType {

	//@formatter:off
	ITEMS("items", Dependency.getColor("itemstack")),
	BLOCKS("blocks", Dependency.getColor("blockstate")),
	ENTITIES("entity_types", Dependency.getColor("entity")),
	FUNCTIONS("functions", Dependency.getColor("string")),
	BIOMES("worldgen/biome", Dependency.getColor("world")),
	DAMAGE_TYPES("damage_type", Dependency.getColor("damagesource"));
	//@formatter:on

	private final String folder;
	private final Color color;

	TagType(String folder, Color color) {
		this.folder = folder;
		this.color = color;
	}

	public String getFolder() {
		return folder;
	}

	public Color getColor() {
		return color;
	}

	@Override public String toString() {
		return L10N.t("tag.type." + name().toLowerCase(Locale.ENGLISH));
	}

	public static TagType fromLegacyName(String readableName) {
		return switch (readableName) {
			case "Items" -> ITEMS;
			case "Blocks" -> BLOCKS;
			case "Entities" -> ENTITIES;
			case "Functions" -> FUNCTIONS;
			case "Biomes" -> BIOMES;
			case "Damage types" -> DAMAGE_TYPES;
			default -> null;
		};
	}

}
