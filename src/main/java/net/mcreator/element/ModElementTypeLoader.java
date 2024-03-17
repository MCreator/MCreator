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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModElementTypeLoader {

	public static List<ModElementType<?>> REGISTRY = new ArrayList<>();

	public static void loadModElements() {
		//@formatter:off
		ModElementType.ADVANCEMENT = register(new ModElementType<>("achievement", 'h', AchievementGUI::new, Achievement.class));
		ModElementType.ARMOR = register(new ModElementType<>("armor", 'a', ArmorGUI::new, Armor.class));
		ModElementType.BIOME = register(new ModElementType<>("biome", 'o', BiomeGUI::new, Biome.class));
		ModElementType.BLOCK = register(new ModElementType<>("block", 'b', BlockGUI::new, Block.class));
		ModElementType.COMMAND = register(new ModElementType<>("command", 'c', CommandGUI::new, Command.class));
		ModElementType.DAMAGETYPE = register(new ModElementType<>("damagetype", null, DamageTypeGUI::new, DamageType.class));
		ModElementType.DIMENSION = register(new ModElementType<>("dimension", 'd', DimensionGUI::new, Dimension.class));
		ModElementType.CODE = register(new ModElementType<>("code", null, CustomElementGUI::new, CustomElement.class));
		ModElementType.ENCHANTMENT = register(new ModElementType<>("enchantment", 'm', EnchantmentGUI::new, Enchantment.class));
		ModElementType.FEATURE = register(new ModElementType<>("feature", 'f', FeatureGUI::new, Feature.class));
		ModElementType.FLUID = register(new ModElementType<>("fluid", null, FluidGUI::new, Fluid.class));
		ModElementType.FUNCTION = register(new ModElementType<>("function", 'u', FunctionGUI::new, Function.class));
		ModElementType.GAMERULE = register(new ModElementType<>("gamerule", null, GameRuleGUI::new, GameRule.class));
		ModElementType.GUI = register(new ModElementType<>("gui", 'g', CustomGUIGUI::new, GUI.class));
		ModElementType.ITEM = register(new ModElementType<>("item", 'i', ItemGUI::new, Item.class));
		ModElementType.ITEMEXTENSION = register(new ModElementType<>("itemextension", null, ItemExtensionGUI::new, ItemExtension.class));
		ModElementType.KEYBIND = register(new ModElementType<>("keybind", 'k', KeyBindGUI::new, KeyBinding.class));
		ModElementType.LIVINGENTITY = register(new ModElementType<>("livingentity", 'e', LivingEntityGUI::new, LivingEntity.class));
		ModElementType.LOOTTABLE = register(new ModElementType<>("loottable", 'l', LootTableGUI::new, LootTable.class));
		ModElementType.MUSICDISC = register(new ModElementType<>("musicdisc", 'x', MusicDiscGUI::new, MusicDisc.class));
		ModElementType.OVERLAY = register(new ModElementType<>("overlay", 'v', OverlayGUI::new, Overlay.class));
		ModElementType.PAINTING = register(new ModElementType<>("painting", null, PaintingGUI::new, Painting.class));
		ModElementType.PARTICLE = register(new ModElementType<>("particle", 'y', ParticleGUI::new, Particle.class));
		ModElementType.PLANT = register(new ModElementType<>("plant", 'n', PlantGUI::new, Plant.class));
		ModElementType.POTION = register(new ModElementType<>("potion", 'z', PotionGUI::new, Potion.class));
		ModElementType.POTIONEFFECT = register(new ModElementType<>("potioneffect", null, PotionEffectGUI::new, PotionEffect.class));
		ModElementType.PROCEDURE = register(new ModElementType<>("procedure", 'p', ProcedureGUI::new, Procedure.class));
		ModElementType.PROJECTILE = register(new ModElementType<>("projectile", 'q', ProjectileGUI::new, Projectile.class));
		ModElementType.RECIPE = register(new ModElementType<>("recipe", 'r', RecipeGUI::new, Recipe.class));
		ModElementType.STRUCTURE = register(new ModElementType<>("structure", 's', StructureGUI::new, Structure.class));
		ModElementType.TAB = register(new ModElementType<>("tab", 'w', TabGUI::new, Tab.class));
		ModElementType.TOOL = register(new ModElementType<>("tool", 't', ToolGUI::new, Tool.class));
		ModElementType.VILLAGERPROFESSION = register(new ModElementType<>("villagerprofession", null, VillagerProfessionGUI::new, VillagerProfession.class));
		ModElementType.VILLAGERTRADE = register(new ModElementType<>("villagertrade", null, VillagerTradeGUI::new, VillagerTrade.class));

		// Unregistered type used to mask legacy removed mod element types
		ModElementType.UNKNOWN = new ModElementType<>("unknown", null, (mc, me, e) -> null, GeneratableElement.Unknown.class);

		//@formatter:on
	}

	public static ModElementType<?> register(ModElementType<?> elementType) {
		REGISTRY.add(elementType);
		return elementType;
	}

	public static ModElementType<?> getModElementType(String typeName) throws IllegalArgumentException {
		// legacy support in case name was not converted up to this point
		if (typeName.equals("mob")) {
			typeName = "livingentity";
		}

		for (ModElementType<?> me : REGISTRY) {
			if (me.getRegistryName().equals(typeName)) {
				return me;
			}
		}

		throw new IllegalArgumentException("Mod element type " + typeName + " is not a registered type");
	}

	public static Set<ModElementType<?>> getModElementTypes() {
		return new HashSet<>(REGISTRY);
	}

}
