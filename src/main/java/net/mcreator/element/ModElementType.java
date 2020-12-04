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

package net.mcreator.element;

import com.google.gson.annotations.SerializedName;
import net.mcreator.ui.init.L10N;

import java.util.Locale;

public enum ModElementType {

	@SerializedName("block") BLOCK(BaseType.BLOCK, RecipeElementType.BLOCK),

	@SerializedName("item") ITEM(BaseType.ITEM, RecipeElementType.ITEM),

	@SerializedName("armor") ARMOR(BaseType.ARMOR, RecipeElementType.ARMOR),

	@SerializedName("biome") BIOME(BaseType.BIOME, RecipeElementType.NONE),

	@SerializedName("fluid") FLUID(BaseType.BLOCK, RecipeElementType.BLOCK),

	@SerializedName("command") COMMAND(BaseType.COMMAND, RecipeElementType.NONE),

	@SerializedName("fuel") FUEL(BaseType.FUEL, RecipeElementType.NONE),

	@SerializedName("mob") MOB(BaseType.ENTITY, RecipeElementType.NONE),

	@SerializedName("food") FOOD(BaseType.ITEM, RecipeElementType.ITEM),

	@SerializedName("tool") TOOL(BaseType.ITEM, RecipeElementType.ITEM),

	@SerializedName("achievement") ACHIEVEMENT(BaseType.DATAPACK, RecipeElementType.NONE),

	@SerializedName("tab") TAB(BaseType.TAB, RecipeElementType.NONE),

	@SerializedName("recipe") RECIPE(BaseType.DATAPACK, RecipeElementType.NONE),

	@SerializedName("plant") PLANT(BaseType.BLOCK, RecipeElementType.BLOCK),

	@SerializedName("dimension") DIMENSION(BaseType.DIMENSION, RecipeElementType.ITEM),

	@SerializedName("gun") RANGEDITEM(BaseType.ITEM, RecipeElementType.ITEM),

	@SerializedName("structure") STRUCTURE(BaseType.STRUCTURE, RecipeElementType.NONE),

	@SerializedName("gui") GUI(BaseType.GUI, RecipeElementType.NONE),

	@SerializedName("keybind") KEYBIND(BaseType.KEYBIND, RecipeElementType.NONE),

	@SerializedName("overlay") OVERLAY(BaseType.OVERLAY, RecipeElementType.NONE),

	@SerializedName("procedure") PROCEDURE(BaseType.PROCEDURE, RecipeElementType.NONE),

	@SerializedName("potioneffect") POTIONEFFECT(BaseType.POTIONEFFECT, RecipeElementType.NONE),

	@SerializedName("particle") PARTICLE(BaseType.PARTICLE, RecipeElementType.NONE),

	@SerializedName("enchantment") ENCHANTMENT(BaseType.ENCHANTMENT, RecipeElementType.NONE),

	@SerializedName("code") CODE(BaseType.OTHER, RecipeElementType.NONE),

	@SerializedName("tag") TAG(BaseType.OTHER, RecipeElementType.NONE),

	@SerializedName("musicdisc") MUSICDISC(BaseType.OTHER, RecipeElementType.ITEM),

	@SerializedName("loottable") LOOTTABLE(BaseType.DATAPACK, RecipeElementType.NONE),

	@SerializedName("function") FUNCTION(BaseType.DATAPACK, RecipeElementType.NONE),

	@SerializedName("painting") PAINTING(BaseType.OTHER, RecipeElementType.NONE),

	@SerializedName("potionitem") POTIONITEM(BaseType.POTIONITEM, RecipeElementType.NONE);

	private final BaseType baseType;
	private final String description;
	private final String readableName;
	private final RecipeElementType recipeElementType;

	ModElementType(BaseType baseType, RecipeElementType recipeElementType) {
		this.baseType = baseType;
		this.recipeElementType = recipeElementType;

		this.readableName = L10N.t("modelement." + name().toLowerCase(Locale.ENGLISH));
		this.description = L10N.t("modelement." + name().toLowerCase(Locale.ENGLISH) + ".description");
	}

	public String getReadableName() {
		return readableName;
	}

	public String getDescription() {
		return description;
	}

	public RecipeElementType getRecipeElementType() {
		return recipeElementType;
	}

	public BaseType getBaseType() {
		return baseType;
	}

	public enum RecipeElementType {
		BLOCK, ITEM, NONE, ARMOR
	}

	public enum BaseType {
		BLOCK, ITEM, ARMOR, BIOME, COMMAND, FUEL, ENTITY, TAB, DIMENSION, STRUCTURE, GUI, KEYBIND, PROCEDURE, OVERLAY, POTIONEFFECT, OTHER, DATAPACK, ENCHANTMENT, PARTICLE, POTIONITEM, /* legacy: */ ACHIEVEMENT
	}

}
