/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.element.registry;

import net.mcreator.element.types.GUI;
import net.mcreator.element.types.*;
import net.mcreator.ui.modgui.*;

import java.util.ArrayList;
import java.util.List;

import static net.mcreator.element.registry.ModElementRegistry.BuiltInTypes.*;

public class ModElementRegistry {

	public static final List<ModElementType<?>> ELEMENTS = new ArrayList<>() {{
		add(ADVANCEMENT = new ModElementType<>("achievement", 'h', BaseType.ACHIEVEMENT, RecipeType.NONE,
				AchievementGUI::new, Achievement.class));
		add(ARMOR = new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR, ArmorGUI::new, Armor.class));
		add(BIOME = new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE, BiomeGUI::new, Biome.class));
		add(BLOCK = new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK, BlockGUI::new, Block.class));
		add(COMMAND = new ModElementType<>("command", 'c', BaseType.COMMAND, RecipeType.NONE, CommandGUI::new,
				Command.class));
		add(DIMENSION = new ModElementType<>("dimension", 'd', BaseType.DIMENSION, RecipeType.ITEM, DimensionGUI::new,
				Dimension.class));
		add(CODE = new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE, CustomElementGUI::new,
				CustomElement.class));
		add(ENCHANTMENT = new ModElementType<>("enchantment", 'm', BaseType.ENCHANTMENT, RecipeType.NONE,
				EnchantmentGUI::new, Enchantment.class));
		add(FLUID = new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BLOCK, FluidGUI::new, Fluid.class));
		add(FOOD = new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM, FoodGUI::new, Food.class));
		add(FUEL = new ModElementType<>("fuel", '/', BaseType.FUEL, RecipeType.NONE, FuelGUI::new, Fuel.class));
		add(FUNCTION = new ModElementType<>("function", '\'', BaseType.DATAPACK, RecipeType.NONE, FunctionGUI::new,
				Function.class));
		add(GAMERULE = new ModElementType<>("gamerule", ';', BaseType.OTHER, RecipeType.NONE, GameRuleGUI::new,
				GameRule.class));
		add(GUI = new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE, CustomGUIGUI::new, GUI.class));
		add(ITEM = new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM, ItemGUI::new, Item.class));
		add(KEYBIND = new ModElementType<>("keybind", 'k', BaseType.KEYBIND, RecipeType.NONE, KeyBindGUI::new,
				KeyBinding.class));
		add(LIVINGENTITY = new ModElementType<>("livingentity", 'e', BaseType.ENTITY, RecipeType.NONE,
				LivingEntityGUI::new, LivingEntity.class));
		add(LOOTTABLE = new ModElementType<>("loottable", 'l', BaseType.DATAPACK, RecipeType.NONE, LootTableGUI::new,
				LootTable.class));
		add(MUSICDISC = new ModElementType<>("musicdisc", 'x', BaseType.OTHER, RecipeType.ITEM, MusicDiscGUI::new,
				MusicDisc.class));
		add(OVERLAY = new ModElementType<>("overlay", 'v', BaseType.OVERLAY, RecipeType.NONE, OverlayGUI::new,
				Overlay.class));
		add(PAINTING = new ModElementType<>("painting", '.', BaseType.OTHER, RecipeType.NONE, PaintingGUI::new,
				Painting.class));
		add(PARTICLE = new ModElementType<>("particle", 'y', BaseType.PARTICLE, RecipeType.NONE, ParticleGUI::new,
				Particle.class));
		add(PLANT = new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK, PlantGUI::new, Plant.class));
		add(POTION = new ModElementType<>("potion", 'z', BaseType.POTION, RecipeType.NONE, PotionGUI::new,
				Potion.class));
		add(PROCEDURE = new ModElementType<>("procedure", 'p', BaseType.PROCEDURE, RecipeType.NONE, ProcedureGUI::new,
				Procedure.class));
		add(RANGEDITEM = new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new,
				RangedItem.class));
		add(RECIPE = new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeType.NONE, RecipeGUI::new,
				Recipe.class));
		add(STRUCTURE = new ModElementType<>("structure", 's', BaseType.STRUCTURE, RecipeType.NONE,
				StructureGenGUI::new, Structure.class));
		add(TAB = new ModElementType<>("tab", 'w', BaseType.TAB, RecipeType.NONE, TabGUI::new, Tab.class));
		add(TAG = new ModElementType<>("tag", 'j', BaseType.OTHER, RecipeType.NONE, TagGUI::new, Tag.class));
		add(TOOL = new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM, ToolGUI::new, Tool.class));
	}};

	public static ModElementType<?> getModElementType(String modElementName) {
		for (ModElementType<?> me : ELEMENTS) {
			if (me.getRegistryName().equals(modElementName)) {
				return me;
			}
		}
		return null;
	}

	public static class BuiltInTypes {
		public static ModElementType<?> ADVANCEMENT;
		public static ModElementType<?> ARMOR;
		public static ModElementType<?> BIOME;
		public static ModElementType<?> BLOCK;
		public static ModElementType<?> COMMAND;
		public static ModElementType<?> DIMENSION;
		public static ModElementType<?> CODE;
		public static ModElementType<?> ENCHANTMENT;
		public static ModElementType<?> FLUID;
		public static ModElementType<?> FOOD;
		public static ModElementType<?> FUEL;
		public static ModElementType<?> FUNCTION;
		public static ModElementType<?> GAMERULE;
		public static ModElementType<?> GUI;
		public static ModElementType<?> ITEM;
		public static ModElementType<?> KEYBIND;
		public static ModElementType<?> LIVINGENTITY;
		public static ModElementType<?> LOOTTABLE;
		public static ModElementType<?> MUSICDISC;
		public static ModElementType<?> OVERLAY;
		public static ModElementType<?> PAINTING;
		public static ModElementType<?> PARTICLE;
		public static ModElementType<?> PLANT;
		public static ModElementType<?> POTION;
		public static ModElementType<?> PROCEDURE;
		public static ModElementType<?> RANGEDITEM;
		public static ModElementType<?> RECIPE;
		public static ModElementType<?> STRUCTURE;
		public static ModElementType<?> TAB;
		public static ModElementType<?> TAG;
		public static ModElementType<?> TOOL;
	}
}
