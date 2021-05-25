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

	@SerializedName("block") BLOCK(BaseType.BLOCK, RecipeType.BLOCK),

	@SerializedName("item") ITEM(BaseType.ITEM, RecipeType.ITEM),

	@SerializedName("armor") ARMOR(BaseType.ARMOR, RecipeType.ARMOR),

	@SerializedName("biome") BIOME(BaseType.BIOME, RecipeType.NONE),

	@SerializedName("fluid") FLUID(BaseType.BLOCK, RecipeType.BUCKET),

	@SerializedName("command") COMMAND(BaseType.COMMAND, RecipeType.NONE),

	@SerializedName("fuel") FUEL(BaseType.FUEL, RecipeType.NONE),

	@SerializedName("mob") MOB(BaseType.ENTITY, RecipeType.NONE),

	@SerializedName("food") FOOD(BaseType.ITEM, RecipeType.ITEM),

	@SerializedName("tool") TOOL(BaseType.ITEM, RecipeType.ITEM),

	@SerializedName("achievement") ACHIEVEMENT(BaseType.DATAPACK, RecipeType.NONE),

	@SerializedName("tab") TAB(BaseType.TAB, RecipeType.NONE),

	@SerializedName("recipe") RECIPE(BaseType.DATAPACK, RecipeType.NONE),

	@SerializedName("plant") PLANT(BaseType.BLOCK, RecipeType.BLOCK),

	@SerializedName("dimension") DIMENSION(BaseType.DIMENSION, RecipeType.ITEM),

	@SerializedName("gun") RANGEDITEM(BaseType.ITEM, RecipeType.ITEM),

	@SerializedName("structure") STRUCTURE(BaseType.STRUCTURE, RecipeType.NONE),

	@SerializedName("gui") GUI(BaseType.GUI, RecipeType.NONE),

	@SerializedName("keybind") KEYBIND(BaseType.KEYBIND, RecipeType.NONE),

	@SerializedName("overlay") OVERLAY(BaseType.OVERLAY, RecipeType.NONE),

	@SerializedName("procedure") PROCEDURE(BaseType.PROCEDURE, RecipeType.NONE),

	@SerializedName("potion") POTIONEFFECT(BaseType.POTIONEFFECT, RecipeType.NONE),

	@SerializedName("particle") PARTICLE(BaseType.PARTICLE, RecipeType.NONE),

	@SerializedName("enchantment") ENCHANTMENT(BaseType.ENCHANTMENT, RecipeType.NONE),

	@SerializedName("code") CODE(BaseType.OTHER, RecipeType.NONE),

	@SerializedName("tag") TAG(BaseType.DATAPACK, RecipeType.NONE),

	@SerializedName("musicdisc") MUSICDISC(BaseType.OTHER, RecipeType.ITEM),

	@SerializedName("loottable") LOOTTABLE(BaseType.DATAPACK, RecipeType.NONE),

	@SerializedName("function") FUNCTION(BaseType.DATAPACK, RecipeType.NONE),

	@SerializedName("painting") PAINTING(BaseType.OTHER, RecipeType.NONE),

	@SerializedName("gamerule") GAMERULE(BaseType.OTHER, RecipeType.NONE),

	@SerializedName("potionitem") POTIONITEM(BaseType.POTIONITEM, RecipeType.NONE);

	private final BaseType baseType;
	private final String description;
	private final String readableName;
	private final RecipeType recipeElementType;

	ModElementType(BaseType baseType, RecipeType recipeElementType) {
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

	public RecipeType getRecipeType() {
		return recipeElementType;
	}

	public BaseType getBaseType() {
		return baseType;
	}

}
