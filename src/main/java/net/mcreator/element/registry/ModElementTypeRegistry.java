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

public class ModElementTypeRegistry {

	public static final List<ModElementType<?>> ELEMENTS = new ArrayList<>() {{
		add(ModElementType.ADVANCEMENT = new ModElementType<>("achievement", 'h', BaseType.DATAPACK, RecipeType.NONE,
				AchievementGUI::new, Achievement.class));
		add(ModElementType.ARMOR = new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR, ArmorGUI::new, Armor.class));
		add(ModElementType.BIOME = new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE, BiomeGUI::new, Biome.class));
		add(ModElementType.BLOCK = new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK, BlockGUI::new, Block.class));
		add(ModElementType.COMMAND = new ModElementType<>("command", 'c', BaseType.COMMAND, RecipeType.NONE, CommandGUI::new,
				Command.class));
		add(ModElementType.DIMENSION = new ModElementType<>("dimension", 'd', BaseType.DATAPACK, RecipeType.ITEM, DimensionGUI::new,
				Dimension.class));
		add(ModElementType.CODE = new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE, CustomElementGUI::new,
				CustomElement.class));
		add(ModElementType.ENCHANTMENT = new ModElementType<>("enchantment", 'm', BaseType.ENCHANTMENT, RecipeType.NONE,
				EnchantmentGUI::new, Enchantment.class));
		add(ModElementType.FLUID = new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BLOCK, FluidGUI::new, Fluid.class));
		add(ModElementType.FOOD = new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM, FoodGUI::new, Food.class));
		add(ModElementType.FUEL = new ModElementType<>("fuel", '/', BaseType.FUEL, RecipeType.NONE, FuelGUI::new, Fuel.class));
		add(ModElementType.FUNCTION = new ModElementType<>("function", '\'', BaseType.DATAPACK, RecipeType.NONE, FunctionGUI::new,
				Function.class));
		add(ModElementType.GAMERULE = new ModElementType<>("gamerule", ';', BaseType.OTHER, RecipeType.NONE, GameRuleGUI::new,
				GameRule.class));
		add(ModElementType.GUI = new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE, CustomGUIGUI::new, GUI.class));
		add(ModElementType.ITEM = new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM, ItemGUI::new, Item.class));
		add(ModElementType.KEYBIND = new ModElementType<>("keybind", 'k', BaseType.KEYBIND, RecipeType.NONE, KeyBindGUI::new,
				KeyBinding.class));
		add(ModElementType.LIVINGENTITY = new ModElementType<>("livingentity", 'e', BaseType.ENTITY, RecipeType.NONE,
				LivingEntityGUI::new, LivingEntity.class));
		add(ModElementType.LOOTTABLE = new ModElementType<>("loottable", 'l', BaseType.DATAPACK, RecipeType.NONE, LootTableGUI::new,
				LootTable.class));
		add(ModElementType.MUSICDISC = new ModElementType<>("musicdisc", 'x', BaseType.OTHER, RecipeType.ITEM, MusicDiscGUI::new,
				MusicDisc.class));
		add(ModElementType.OVERLAY = new ModElementType<>("overlay", 'v', BaseType.OVERLAY, RecipeType.NONE, OverlayGUI::new,
				Overlay.class));
		add(ModElementType.PAINTING = new ModElementType<>("painting", '.', BaseType.OTHER, RecipeType.NONE, PaintingGUI::new,
				Painting.class));
		add(ModElementType.PARTICLE = new ModElementType<>("particle", 'y', BaseType.PARTICLE, RecipeType.NONE, ParticleGUI::new,
				Particle.class));
		add(ModElementType.PLANT = new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK, PlantGUI::new, Plant.class));
		add(ModElementType.POTION = new ModElementType<>("potion", 'z', BaseType.POTION, RecipeType.NONE, PotionGUI::new,
				Potion.class));
		add(ModElementType.PROCEDURE = new ModElementType<>("procedure", 'p', BaseType.PROCEDURE, RecipeType.NONE, ProcedureGUI::new,
				Procedure.class));
		add(ModElementType.RANGEDITEM = new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new,
				RangedItem.class));
		add(ModElementType.RECIPE = new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeType.NONE, RecipeGUI::new,
				Recipe.class));
		add(ModElementType.STRUCTURE = new ModElementType<>("structure", 's', BaseType.STRUCTURE, RecipeType.NONE,
				StructureGenGUI::new, Structure.class));
		add(ModElementType.TAB = new ModElementType<>("tab", 'w', BaseType.TAB, RecipeType.NONE, TabGUI::new, Tab.class));
		add(ModElementType.TAG = new ModElementType<>("tag", 'j', BaseType.DATAPACK, RecipeType.NONE, TagGUI::new, Tag.class));
		add(ModElementType.TOOL = new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM, ToolGUI::new, Tool.class));
	}};

	public static ModElementType<?> getModElementType(String modElementName) {
		for (ModElementType<?> me : ELEMENTS) {
			if (me.getRegistryName().equals(modElementName)) {
				return me;
			}
		}
		return null;
	}

	public static class BuiltInTypes {}
}
