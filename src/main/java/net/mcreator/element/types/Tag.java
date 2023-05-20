/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.*;

@SuppressWarnings("unused") public class Tag extends NamespacedGeneratableElement
		implements IOtherModElementsDependent {

	public String type;

	public List<MItemBlock> items;
	public List<MItemBlock> blocks;
	public List<String> functions;
	public List<EntityEntry> entities;
	public List<BiomeEntry> biomes;

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

	@Override public Collection<String> getUsedElementNames() {
		return "Functions".equals(type) ? functions.stream().map(e -> "CUSTOM:" + e).toList() : Collections.emptyList();
	}

	@Override public Collection<? extends MappableElement> getUsedElementMappings() {
		return switch (type) {
			case "Items" -> new ArrayList<>(items);
			case "Blocks" -> new ArrayList<>(blocks);
			case "Entities" -> new ArrayList<>(entities);
			case "Biomes" -> new ArrayList<>(biomes);
			default -> Collections.emptyList();
		};
	}
}
