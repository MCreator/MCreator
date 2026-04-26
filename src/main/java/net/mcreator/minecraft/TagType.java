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
import net.mcreator.element.parts.*;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;

public enum TagType {

	//@formatter:off
	ITEMS("item", Dependency.getColor("itemstack"), MItemBlock::new, true),
	BLOCKS("block", Dependency.getColor("blockstate"), MItemBlock::new, true),
	ENTITIES("entity_type", Dependency.getColor("entity"), EntityEntry::new, true),
	FUNCTIONS("function", Dependency.getColor("string"), (w, e) -> new NonMappableElement(e), true),
	BIOMES("worldgen/biome", Dependency.getColor("world"), BiomeEntry::new, true),
	STRUCTURES("worldgen/structure", new Color(0xA16350), StructureEntry::new, true),
	DAMAGE_TYPES("damage_type", Dependency.getColor("damagesource"), DamageTypeEntry::new, true),
	ENCHANTMENTS("enchantment", Dependency.getColor("enchantment"), Enchantment::new, true),
	GAME_EVENTS("game_event", new Color(0x5057A1), GameEventEntry::new, true),
	PAINTING_VARIANTS("painting_variant", new Color(0x59806C), (w, e) -> new NonMappableElement(e), false),
	BANNER_PATTERNS("banner_pattern", new Color(0x805959), (w, e) -> new NonMappableElement(e), false),
	POINTS_OF_INTEREST("point_of_interest_type", new Color(0x807659), (w, e) -> new NonMappableElement(e), false);
	//@formatter:on

	private final String folder;
	private final Color color;
	private final BiFunction<Workspace, String, MappableElement> mappableElementProvider;
	private final boolean visible;

	TagType(String folder, Color color, BiFunction<Workspace, String, MappableElement> mappableElementProvider, boolean visible) {
		this.folder = folder;
		this.color = color;
		this.mappableElementProvider = mappableElementProvider;
		this.visible = visible;
	}

	public BiFunction<Workspace, String, MappableElement> getMappableElementProvider() {
		return mappableElementProvider;
	}

	public String getFolder() {
		return folder;
	}

	public String getPre21Folder() {
		return switch (this) {
			case ITEMS, FUNCTIONS, ENTITIES, BLOCKS -> folder + "s";
			default -> folder;
		};
	}

	public Color getColor() {
		return color;
	}

	public static TagType[] visibleValues() {
		return Arrays.stream(TagType.values()).filter(e -> e.visible).toArray(TagType[]::new);
	}

	@Override public String toString() {
		return L10N.t("tag.type." + name().toLowerCase(Locale.ENGLISH));
	}

}
