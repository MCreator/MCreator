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

public class ModElementTypeLoader {

	public static List<ModElementType<?>> REGISTRY = new ArrayList<>();

	public static void loadModElements() {
		//@formatter:off
		ModElementType.ADVANCEMENT = register(new ModElementType<>("achievement", 'h', BaseType.DATAPACK, AchievementGUI::new, Achievement.class));
		ModElementType.ARMOR = register(new ModElementType<>("armor", 'a', BaseType.ARMOR, ArmorGUI::new, Armor.class));
		ModElementType.BIOME = register(new ModElementType<>("biome", 'o', BaseType.BIOME, BiomeGUI::new, Biome.class));
		ModElementType.BLOCK = register(new ModElementType<>("block", 'b', BaseType.BLOCK, BlockGUI::new, Block.class));
		ModElementType.COMMAND = register(new ModElementType<>("command", 'c', BaseType.OTHER, CommandGUI::new, Command.class));
		ModElementType.DIMENSION = register(new ModElementType<>("dimension", 'd', BaseType.OTHER, DimensionGUI::new, Dimension.class));
		ModElementType.CODE = register(new ModElementType<>("code", null, BaseType.OTHER, CustomElementGUI::new, CustomElement.class));
		ModElementType.ENCHANTMENT = register(new ModElementType<>("enchantment", 'm', BaseType.OTHER, EnchantmentGUI::new, Enchantment.class));
		ModElementType.FEATURE = register(new ModElementType<>("feature", 'f', BaseType.FEATURE, FeatureGUI::new, Feature.class));
		ModElementType.FLUID = register(new ModElementType<>("fluid", null, BaseType.BLOCK, FluidGUI::new, Fluid.class));
		ModElementType.FUNCTION = register(new ModElementType<>("function", 'u', BaseType.DATAPACK, FunctionGUI::new, Function.class));
		ModElementType.GAMERULE = register(new ModElementType<>("gamerule", null, BaseType.OTHER, GameRuleGUI::new, GameRule.class));
		ModElementType.GUI = register(new ModElementType<>("gui", 'g', BaseType.GUI, CustomGUIGUI::new, GUI.class));
		ModElementType.ITEM = register(new ModElementType<>("item", 'i', BaseType.ITEM, ItemGUI::new, Item.class));
		ModElementType.ITEMEXTENSION = register(new ModElementType<>("itemextension", null, BaseType.OTHER, ItemExtensionGUI::new, ItemExtension.class));
		ModElementType.KEYBIND = register(new ModElementType<>("keybind", 'k', BaseType.OTHER, KeyBindGUI::new, KeyBinding.class));
		ModElementType.LIVINGENTITY = register(new ModElementType<>("livingentity", 'e', BaseType.ENTITY, LivingEntityGUI::new, LivingEntity.class));
		ModElementType.LOOTTABLE = register(new ModElementType<>("loottable", 'l', BaseType.DATAPACK, LootTableGUI::new, LootTable.class));
		ModElementType.MUSICDISC = register(new ModElementType<>("musicdisc", 'x', BaseType.OTHER, MusicDiscGUI::new, MusicDisc.class));
		ModElementType.OVERLAY = register(new ModElementType<>("overlay", 'v', BaseType.OTHER, OverlayGUI::new, Overlay.class));
		ModElementType.PAINTING = register(new ModElementType<>("painting", null, BaseType.OTHER, PaintingGUI::new, Painting.class));
		ModElementType.PARTICLE = register(new ModElementType<>("particle", 'y', BaseType.OTHER, ParticleGUI::new, Particle.class));
		ModElementType.PLANT = register(new ModElementType<>("plant", 'n', BaseType.BLOCK, PlantGUI::new, Plant.class));
		ModElementType.POTION = register(new ModElementType<>("potion", 'z', BaseType.OTHER, PotionGUI::new, Potion.class));
		ModElementType.POTIONEFFECT = register(new ModElementType<>("potioneffect", null, BaseType.OTHER, PotionEffectGUI::new, PotionEffect.class));
		ModElementType.PROCEDURE = register(new ModElementType<>("procedure", 'p', BaseType.OTHER, ProcedureGUI::new, Procedure.class));
		ModElementType.RANGEDITEM = register(new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RangedItemGUI::new, RangedItem.class));
		ModElementType.RECIPE = register(new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeGUI::new, Recipe.class));
		ModElementType.STRUCTURE = register(new ModElementType<>("structure", 's', BaseType.FEATURE, StructureGenGUI::new, Structure.class));
		ModElementType.TAB = register(new ModElementType<>("tab", 'w', BaseType.OTHER, TabGUI::new, Tab.class));
		ModElementType.TAG = register(new ModElementType<>("tag", 'j', BaseType.DATAPACK, TagGUI::new, Tag.class));
		ModElementType.TOOL = register(new ModElementType<>("tool", 't', BaseType.ITEM, ToolGUI::new, Tool.class));
		ModElementType.VILLAGERTRADE = register(new ModElementType<>("villagertrade", null, BaseType.OTHER, VillagerTradeGUI::new, VillagerTrade.class));

		// Unregistered type used to mask legacy removed mod element types
		ModElementType.UNKNOWN = new ModElementType<>("unknown", null, BaseType.OTHER, (mc, me, e) -> null, GeneratableElement.Unknown.class);

		//@formatter:on
	}

	public static ModElementType<?> register(ModElementType<?> elementType) {
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
}
