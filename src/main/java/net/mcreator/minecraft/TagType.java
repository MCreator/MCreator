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

	ITEMS, BLOCKS, ENTITIES, FUNCTIONS, BIOMES, DAMAGE_TYPES;

	@Override public String toString() {
		return L10N.t("tag.type." + name().toLowerCase(Locale.ENGLISH));
	}

	public String getFolder() {
		return switch (this) {
			case ITEMS -> "items";
			case BLOCKS -> "blocks";
			case ENTITIES -> "entity_types";
			case FUNCTIONS -> "functions";
			case BIOMES -> "worldgen/biome";
			case DAMAGE_TYPES -> "damage_type";
		};
	}

	public Color getColor() {
		return switch (this) {
			case ITEMS -> Dependency.getColor("itemstack");
			case BLOCKS -> Dependency.getColor("blockstate");
			case ENTITIES -> Dependency.getColor("entity");
			case FUNCTIONS -> Dependency.getColor("string");
			case BIOMES -> Dependency.getColor("world");
			case DAMAGE_TYPES -> Dependency.getColor("damagesource");
		};
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
