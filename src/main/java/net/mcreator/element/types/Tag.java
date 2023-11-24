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

package net.mcreator.element.types;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Tag extends NamespacedGeneratableElement {

	@Nonnull public String type;

	@ModElementReference public List<MItemBlock> items;
	@ModElementReference public List<MItemBlock> blocks;
	@ModElementReference public List<String> functions;
	@ModElementReference public List<EntityEntry> entities;
	@ModElementReference public List<BiomeEntry> biomes;

	public Tag(ModElement element) {
		super(element);
	}

	public String tagType() {
		if (type.equals("Entities"))
			return "entity_types";

		if (type.equals("Biomes"))
			return "worldgen/biome";

		return type.toLowerCase(Locale.ENGLISH);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateTagPreviewPicture(type);
	}

	public static Color getColor(String type) {
		return switch (type) {
			case "Items" -> Dependency.getColor("itemstack");
			case "Blocks" -> Dependency.getColor("blockstate");
			case "Entities" -> Dependency.getColor("entity");
			case "Functions" -> Dependency.getColor("string");
			case "Biomes" -> Dependency.getColor("world");
			default -> Color.WHITE;
		};
	}

}
