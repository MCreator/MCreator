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
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.modgui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.mcreator.generator.GeneratorFlavor.BaseLanguage.JAVA;
import static net.mcreator.generator.GeneratorFlavor.DATAPACK;
import static net.mcreator.generator.GeneratorFlavor.GamePlatform.JAVAEDITION;

public class ModElementTypeLoader {

	private static final List<ModElementType<?>> REGISTRY = new ArrayList<>();

	public static void loadModElements() {
		//@formatter:off
		ModElementType.ADVANCEMENT = register(new ModElementType<>("achievement", 'h', AchievementGUI::new, Achievement.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.ARMOR = register(new ModElementType<>("armor", 'a', ArmorGUI::new, Armor.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.ATTRIBUTE = register(new ModElementType<>("attribute", null, AttributeGUI::new, Attribute.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.BIOME = register(new ModElementType<>("biome", 'o', BiomeGUI::new, Biome.class));
		ModElementType.BLOCK = register(new ModElementType<>("block", 'b', BlockGUI::new, Block.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.COMMAND = register(new ModElementType<>("command", 'c', CommandGUI::new, Command.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.DAMAGETYPE = register(new ModElementType<>("damagetype", null, DamageTypeGUI::new, DamageType.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.DIMENSION = register(new ModElementType<>("dimension", 'd', DimensionGUI::new, Dimension.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.CODE = register(new ModElementType<>("code", null, CustomElementGUI::new, CustomElement.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.ENCHANTMENT = register(new ModElementType<>("enchantment", 'm', EnchantmentGUI::new, Enchantment.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.FEATURE = register(new ModElementType<>("feature", 'f', FeatureGUI::new, Feature.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.FLUID = register(new ModElementType<>("fluid", null, FluidGUI::new, Fluid.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.FUNCTION = register(new ModElementType<>("function", 'u', FunctionGUI::new, Function.class));
		ModElementType.GAMERULE = register(new ModElementType<>("gamerule", null, GameRuleGUI::new, GameRule.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.GUI = register(new ModElementType<>("gui", 'g', CustomGUIGUI::new, GUI.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.ITEM = register(new ModElementType<>("item", 'i', ItemGUI::new, Item.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.ITEMEXTENSION = register(new ModElementType<>("itemextension", null, ItemExtensionGUI::new, ItemExtension.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.KEYBIND = register(new ModElementType<>("keybind", 'k', KeyBindGUI::new, KeyBinding.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.LIVINGENTITY = register(new ModElementType<>("livingentity", 'e', LivingEntityGUI::new, LivingEntity.class)).coveredOn(GeneratorFlavor.allBut(DATAPACK));
		ModElementType.LOOTTABLE = register(new ModElementType<>("loottable", 'l', LootTableGUI::new, LootTable.class));
		ModElementType.MUSICDISC = register(new ModElementType<>("musicdisc", 'x', MusicDiscGUI::new, MusicDisc.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.OVERLAY = register(new ModElementType<>("overlay", 'v', OverlayGUI::new, Overlay.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.PAINTING = register(new ModElementType<>("painting", null, PaintingGUI::new, Painting.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.PARTICLE = register(new ModElementType<>("particle", 'y', ParticleGUI::new, Particle.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.PLANT = register(new ModElementType<>("plant", 'n', PlantGUI::new, Plant.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.POTION = register(new ModElementType<>("potion", 'z', PotionGUI::new, Potion.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.POTIONEFFECT = register(new ModElementType<>("potioneffect", null, PotionEffectGUI::new, PotionEffect.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.PROCEDURE = register(new ModElementType<>("procedure", 'p', ProcedureGUI::new, Procedure.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.PROJECTILE = register(new ModElementType<>("projectile", 'q', ProjectileGUI::new, Projectile.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.RECIPE = register(new ModElementType<>("recipe", 'r', RecipeGUI::new, Recipe.class));
		ModElementType.STRUCTURE = register(new ModElementType<>("structure", 's', StructureGUI::new, Structure.class)).coveredOn(GeneratorFlavor.gamePlatform(JAVAEDITION));
		ModElementType.TAB = register(new ModElementType<>("tab", 'w', TabGUI::new, Tab.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.TOOL = register(new ModElementType<>("tool", 't', ToolGUI::new, Tool.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.VILLAGERPROFESSION = register(new ModElementType<>("villagerprofession", null, VillagerProfessionGUI::new, VillagerProfession.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));
		ModElementType.VILLAGERTRADE = register(new ModElementType<>("villagertrade", null, VillagerTradeGUI::new, VillagerTrade.class)).coveredOn(GeneratorFlavor.baseLanguage(JAVA));

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

	public static Collection<ModElementType<?>> getAllModElementTypes() {
		return Collections.unmodifiableList(REGISTRY);
	}

}
