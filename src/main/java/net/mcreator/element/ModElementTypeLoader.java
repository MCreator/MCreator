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
import java.util.stream.Collectors;

public class ModElementTypeLoader {

	public static List<ModElementType<?>> REGISTRY = new ArrayList<>();

	public static void loadModElements() {
		ModElementType.ADVANCEMENT = register(
				new ModElementType<>("achievement", 'h', BaseType.DATAPACK, RecipeType.NONE, AchievementGUI::new,
						Achievement.class, true));
		ModElementType.ARMOR = register(
				new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR, ArmorGUI::new, Armor.class,
						true));
		ModElementType.BIOME = register(
				new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE, BiomeGUI::new, Biome.class,
						true));
		ModElementType.BLOCK = register(
				new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK, BlockGUI::new, Block.class,
						true));
		ModElementType.COMMAND = register(
				new ModElementType<>("command", 'c', BaseType.OTHER, RecipeType.NONE, CommandGUI::new, Command.class,
						true));
		ModElementType.DIMENSION = register(
				new ModElementType<>("dimension", 'd', BaseType.OTHER, RecipeType.ITEM, DimensionGUI::new,
						Dimension.class, true));
		ModElementType.CODE = register(
				new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE, CustomElementGUI::new,
						CustomElement.class, true));
		ModElementType.ENCHANTMENT = register(
				new ModElementType<>("enchantment", 'm', BaseType.OTHER, RecipeType.NONE, EnchantmentGUI::new,
						Enchantment.class, true));
		ModElementType.FLUID = register(
				new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BUCKET, FluidGUI::new, Fluid.class,
						true));
		ModElementType.FOOD = register(
				new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM, FoodGUI::new, Food.class,
						true));
		ModElementType.FUEL = register(
				new ModElementType<>("fuel", null, BaseType.OTHER, RecipeType.NONE, FuelGUI::new, Fuel.class,
						true));
		ModElementType.FUNCTION = register(
				new ModElementType<>("function", null, BaseType.DATAPACK, RecipeType.NONE, FunctionGUI::new,
						Function.class, true));
		ModElementType.GAMERULE = register(
				new ModElementType<>("gamerule", null, BaseType.OTHER, RecipeType.NONE, GameRuleGUI::new,
						GameRule.class, true));
		ModElementType.GUI = register(
				new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE, CustomGUIGUI::new, GUI.class,
						true));
		ModElementType.ITEM = register(
				new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM, ItemGUI::new, Item.class,
						true));
		ModElementType.KEYBIND = register(
				new ModElementType<>("keybind", 'k', BaseType.OTHER, RecipeType.NONE, KeyBindGUI::new,
						KeyBinding.class, true));
		ModElementType.LIVINGENTITY = register(
				new ModElementType<>("livingentity", 'e', BaseType.ENTITY, RecipeType.NONE, LivingEntityGUI::new,
						LivingEntity.class, true));
		ModElementType.LOOTTABLE = register(
				new ModElementType<>("loottable", 'l', BaseType.DATAPACK, RecipeType.NONE, LootTableGUI::new,
						LootTable.class, true));
		ModElementType.MUSICDISC = register(
				new ModElementType<>("musicdisc", 'x', BaseType.OTHER, RecipeType.ITEM, MusicDiscGUI::new,
						MusicDisc.class, true));
		ModElementType.OVERLAY = register(
				new ModElementType<>("overlay", 'v', BaseType.OTHER, RecipeType.NONE, OverlayGUI::new, Overlay.class,
						true));
		ModElementType.PAINTING = register(
				new ModElementType<>("painting", null, BaseType.OTHER, RecipeType.NONE, PaintingGUI::new,
						Painting.class, true));
		ModElementType.PARTICLE = register(
				new ModElementType<>("particle", 'y', BaseType.OTHER, RecipeType.NONE, ParticleGUI::new,
						Particle.class, true));
		ModElementType.PLANT = register(
				new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK, PlantGUI::new, Plant.class,
						true));
		ModElementType.POTION = register(
				new ModElementType<>("potion", 'z', BaseType.OTHER, RecipeType.NONE, PotionGUI::new, Potion.class,
						true));
		ModElementType.POTIONEFFECT = register(
				new ModElementType<>("potioneffect", null, BaseType.OTHER, RecipeType.NONE, PotionEffectGUI::new,
						PotionEffect.class, true));
		ModElementType.PROCEDURE = register(
				new ModElementType<>("procedure", 'p', BaseType.OTHER, RecipeType.NONE, ProcedureGUI::new,
						Procedure.class, true));
		ModElementType.RANGEDITEM = register(
				new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new,
						RangedItem.class, true));
		ModElementType.RECIPE = register(
				new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeType.NONE, RecipeGUI::new, Recipe.class,
						true));
		ModElementType.STRUCTURE = register(
				new ModElementType<>("structure", 's', BaseType.FEATURE, RecipeType.NONE, StructureGenGUI::new,
						Structure.class, true));
		ModElementType.TAB = register(
				new ModElementType<>("tab", 'w', BaseType.OTHER, RecipeType.NONE, TabGUI::new, Tab.class,
						true));
		ModElementType.TAG = register(
				new ModElementType<>("tag", 'j', BaseType.DATAPACK, RecipeType.NONE, TagGUI::new, Tag.class,
						true));
		ModElementType.TOOL = register(
				new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM, ToolGUI::new, Tool.class,
						true));

		// Unregistered type used to mask legacy removed mod element types
		ModElementType.UNKNOWN = new ModElementType<>("unknown", null, BaseType.OTHER, RecipeType.NONE,
				(mc, me, e) -> null, GeneratableElement.Unknown.class, false);
	}

	private static ModElementType<?> register(ModElementType<?> elementType) {
		REGISTRY.add(elementType);
		return elementType;
	}

	public static ModElementType<?> getModElementType(String typeName) throws IllegalArgumentException {
		// legacy support in case name was not converted up to this point
		if (typeName.equals("gun")) {
			typeName = "rangeditem";
		} else if (typeName.equals("mob")) {
			typeName = "livingentity";
		}

		for (ModElementType<?> me : REGISTRY) {
			if (me.getRegistryName().equals(typeName)) {
				return me;
			}
		}

		throw new IllegalArgumentException("Mod element type " + typeName + " is not a registered type");
	}

	public static List<String> getModElementsInModWT() {
		return REGISTRY.stream().filter(ModElementType::addToModWorkspaceType)
				.map(ModElementType::getRegistryName).collect(Collectors.toList());
	}
}
