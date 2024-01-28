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
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.DamageTypeEntry;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import java.awt.*;
import java.util.Locale;
import java.util.function.BiFunction;

public enum TagType {

	//@formatter:off
	ITEMS("items", Dependency.getColor("itemstack"), MItemBlock::new),
	BLOCKS("blocks", Dependency.getColor("blockstate"), MItemBlock::new),
	ENTITIES("entity_types", Dependency.getColor("entity"), EntityEntry::new),
	FUNCTIONS("functions", Dependency.getColor("string"), (w, e) -> new NonMappableElement(e)),
	BIOMES("worldgen/biome", Dependency.getColor("world"), BiomeEntry::new),
	DAMAGE_TYPES("damage_type", Dependency.getColor("damagesource"), DamageTypeEntry::new);
	//@formatter:on

	private final String folder;
	private final Color color;
	private final BiFunction<Workspace, String, MappableElement> mappableElementProvider;

	TagType(String folder, Color color, BiFunction<Workspace, String, MappableElement> mappableElementProvider) {
		this.folder = folder;
		this.color = color;
		this.mappableElementProvider = mappableElementProvider;
	}

	public BiFunction<Workspace, String, MappableElement> getMappableElementProvider() {
		return mappableElementProvider;
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

}
