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

package net.mcreator.element;

import net.mcreator.element.types.*;
import net.mcreator.ui.modgui.*;

import java.util.ArrayList;
import java.util.List;

public class ModElementTypeRegistry {

	public static List<ModElementType<?>> REGISTRY = new ArrayList<>();

	static {
		ModElementType.ADVANCEMENT = register(
				new ModElementType<>("achievement", 'h', BaseType.DATAPACK, RecipeType.NONE, AchievementGUI::new,
						Achievement.class));
		ModElementType.ARMOR = register(
				new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR, ArmorGUI::new, Armor.class));
		ModElementType.BIOME = register(
				new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE, BiomeGUI::new, Biome.class));
		ModElementType.BLOCK = register(
				new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK, BlockGUI::new, Block.class));
		ModElementType.COMMAND = register(
				new ModElementType<>("command", 'c', BaseType.COMMAND, RecipeType.NONE, CommandGUI::new,
						Command.class));
		ModElementType.DIMENSION = register(
				new ModElementType<>("dimension", 'd', BaseType.DATAPACK, RecipeType.ITEM, DimensionGUI::new,
						Dimension.class));
		ModElementType.CODE = register(
				new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE, CustomElementGUI::new,
						CustomElement.class));
		ModElementType.ENCHANTMENT = register(
				new ModElementType<>("enchantment", 'm', BaseType.ENCHANTMENT, RecipeType.NONE, EnchantmentGUI::new,
						Enchantment.class));
		ModElementType.FLUID = register(
				new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BLOCK, FluidGUI::new, Fluid.class));
		ModElementType.FOOD = register(
				new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM, FoodGUI::new, Food.class));
		ModElementType.FUEL = register(
				new ModElementType<>("fuel", '1', BaseType.FUEL, RecipeType.NONE, FuelGUI::new, Fuel.class));
		ModElementType.FUNCTION = register(
				new ModElementType<>("function", '2', BaseType.DATAPACK, RecipeType.NONE, FunctionGUI::new,
						Function.class));
		ModElementType.GAMERULE = register(
				new ModElementType<>("gamerule", '3', BaseType.OTHER, RecipeType.NONE, GameRuleGUI::new,
						GameRule.class));
		ModElementType.GUI = register(
				new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE, CustomGUIGUI::new, GUI.class));
		ModElementType.ITEM = register(
				new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM, ItemGUI::new, Item.class));
		ModElementType.KEYBIND = register(
				new ModElementType<>("keybind", 'k', BaseType.KEYBIND, RecipeType.NONE, KeyBindGUI::new,
						KeyBinding.class));
		ModElementType.LIVINGENTITY = register(
				new ModElementType<>("livingentity", 'e', BaseType.ENTITY, RecipeType.NONE, LivingEntityGUI::new,
						LivingEntity.class));
		ModElementType.LOOTTABLE = register(
				new ModElementType<>("loottable", 'l', BaseType.DATAPACK, RecipeType.NONE, LootTableGUI::new,
						LootTable.class));
		ModElementType.MUSICDISC = register(
				new ModElementType<>("musicdisc", 'x', BaseType.OTHER, RecipeType.ITEM, MusicDiscGUI::new,
						MusicDisc.class));
		ModElementType.OVERLAY = register(
				new ModElementType<>("overlay", 'v', BaseType.OVERLAY, RecipeType.NONE, OverlayGUI::new,
						Overlay.class));
		ModElementType.PAINTING = register(
				new ModElementType<>("painting", '4', BaseType.OTHER, RecipeType.NONE, PaintingGUI::new,
						Painting.class));
		ModElementType.PARTICLE = register(
				new ModElementType<>("particle", 'y', BaseType.PARTICLE, RecipeType.NONE, ParticleGUI::new,
						Particle.class));
		ModElementType.PLANT = register(
				new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK, PlantGUI::new, Plant.class));
		ModElementType.POTION = register(
				new ModElementType<>("potion", 'z', BaseType.POTION, RecipeType.NONE, PotionGUI::new, Potion.class));
		ModElementType.POTIONEFFECT = register(
				new ModElementType<>("potioneffect", null, BaseType.POTIONEFFECT, RecipeType.NONE, PotionEffectGUI::new, PotionEffect.class));
		ModElementType.PROCEDURE = register(
				new ModElementType<>("procedure", 'p', BaseType.PROCEDURE, RecipeType.NONE, ProcedureGUI::new,
						Procedure.class));
		ModElementType.RANGEDITEM = register(
				new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new,
						RangedItem.class));
		ModElementType.RECIPE = register(
				new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeType.NONE, RecipeGUI::new, Recipe.class));
		ModElementType.STRUCTURE = register(
				new ModElementType<>("structure", 's', BaseType.STRUCTURE, RecipeType.NONE, StructureGenGUI::new,
						Structure.class));
		ModElementType.TAB = register(
				new ModElementType<>("tab", 'w', BaseType.TAB, RecipeType.NONE, TabGUI::new, Tab.class));
		ModElementType.TAG = register(
				new ModElementType<>("tag", 'j', BaseType.DATAPACK, RecipeType.NONE, TagGUI::new, Tag.class));
		ModElementType.TOOL = register(
				new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM, ToolGUI::new, Tool.class));
	}

	private static ModElementType<?> register(ModElementType<?> elementType) {
		REGISTRY.add(elementType);
		return elementType;
	}

	public static ModElementType<?> getModElementType(String modElementName) {
		for (ModElementType<?> me : REGISTRY) {
			if (me.getRegistryName().equals(modElementName)) {
				return me;
			}
		}
		return null;
	}
}
