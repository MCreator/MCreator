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

import net.mcreator.element.types.*;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.*;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModElementType<GE extends GeneratableElement> implements Comparable<ModElementType<?>> {
	public static List<ModElementType<?>> ELEMENTS = new ArrayList<>();

	public static ModElementType<?> ADVANCEMENT = register(
			new ModElementType<>("achievement", 'h', BaseType.DATAPACK, RecipeType.NONE, AchievementGUI::new,
					Achievement.class));
	public static ModElementType<?> ARMOR = register(
			new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR, ArmorGUI::new, Armor.class));
	public static ModElementType<?> BIOME = register(
			new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE, BiomeGUI::new, Biome.class));
	public static ModElementType<?> BLOCK = register(
			new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK, BlockGUI::new, Block.class));
	public static ModElementType<?> COMMAND = register(
			new ModElementType<>("command", 'c', BaseType.COMMAND, RecipeType.NONE, CommandGUI::new, Command.class));
	public static ModElementType<?> DIMENSION = register(
			new ModElementType<>("dimension", 'd', BaseType.DATAPACK, RecipeType.ITEM, DimensionGUI::new,
					Dimension.class));
	public static ModElementType<?> CODE = register(
			new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE, CustomElementGUI::new,
					CustomElement.class));
	public static ModElementType<?> ENCHANTMENT = register(
			new ModElementType<>("enchantment", 'm', BaseType.ENCHANTMENT, RecipeType.NONE, EnchantmentGUI::new,
					Enchantment.class));
	public static ModElementType<?> FLUID = register(
			new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BLOCK, FluidGUI::new, Fluid.class));
	public static ModElementType<?> FOOD = register(
			new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM, FoodGUI::new, Food.class));
	public static ModElementType<?> FUEL = register(
			new ModElementType<>("fuel", '/', BaseType.FUEL, RecipeType.NONE, FuelGUI::new, Fuel.class));
	public static ModElementType<?> FUNCTION = register(
			new ModElementType<>("function", '\'', BaseType.DATAPACK, RecipeType.NONE, FunctionGUI::new,
					Function.class));
	public static ModElementType<?> GAMERULE = register(
			new ModElementType<>("gamerule", ';', BaseType.OTHER, RecipeType.NONE, GameRuleGUI::new, GameRule.class));
	public static ModElementType<?> GUI = register(
			new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE, CustomGUIGUI::new, GUI.class));
	public static ModElementType<?> ITEM = register(
			new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM, ItemGUI::new, Item.class));
	public static ModElementType<?> KEYBIND = register(
			new ModElementType<>("keybind", 'k', BaseType.KEYBIND, RecipeType.NONE, KeyBindGUI::new, KeyBinding.class));
	public static ModElementType<?> LIVINGENTITY = register(
			new ModElementType<>("livingentity", 'e', BaseType.ENTITY, RecipeType.NONE, LivingEntityGUI::new,
					LivingEntity.class));
	public static ModElementType<?> LOOTTABLE = register(
			new ModElementType<>("loottable", 'l', BaseType.DATAPACK, RecipeType.NONE, LootTableGUI::new,
					LootTable.class));
	public static ModElementType<?> MUSICDISC = register(
			new ModElementType<>("musicdisc", 'x', BaseType.OTHER, RecipeType.ITEM, MusicDiscGUI::new,
					MusicDisc.class));
	public static ModElementType<?> OVERLAY = register(
			new ModElementType<>("overlay", 'v', BaseType.OVERLAY, RecipeType.NONE, OverlayGUI::new, Overlay.class));
	public static ModElementType<?> PAINTING = register(
			new ModElementType<>("painting", '.', BaseType.OTHER, RecipeType.NONE, PaintingGUI::new, Painting.class));
	public static ModElementType<?> PARTICLE = register(
			new ModElementType<>("particle", 'y', BaseType.PARTICLE, RecipeType.NONE, ParticleGUI::new,
					Particle.class));
	public static ModElementType<?> PLANT = register(
			new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK, PlantGUI::new, Plant.class));
	public static ModElementType<?> POTION = register(
			new ModElementType<>("potion", 'z', BaseType.POTION, RecipeType.NONE, PotionGUI::new, Potion.class));
	public static ModElementType<?> PROCEDURE = register(
			new ModElementType<>("procedure", 'p', BaseType.PROCEDURE, RecipeType.NONE, ProcedureGUI::new,
					Procedure.class));
	public static ModElementType<?> RANGEDITEM = register(
			new ModElementType<>("rangeditem", 'q', BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new,
					RangedItem.class));
	public static ModElementType<?> RECIPE = register(
			new ModElementType<>("recipe", 'r', BaseType.DATAPACK, RecipeType.NONE, RecipeGUI::new, Recipe.class));
	public static ModElementType<?> STRUCTURE = register(
			new ModElementType<>("structure", 's', BaseType.STRUCTURE, RecipeType.NONE, StructureGenGUI::new,
					Structure.class));
	public static ModElementType<?> TAB = register(
			new ModElementType<>("tab", 'w', BaseType.TAB, RecipeType.NONE, TabGUI::new, Tab.class));
	public static ModElementType<?> TAG = register(
			new ModElementType<>("tag", 'j', BaseType.DATAPACK, RecipeType.NONE, TagGUI::new, Tag.class));
	public static ModElementType<?> TOOL = register(
			new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM, ToolGUI::new, Tool.class));

	//Variables used for each mod element
	private final BaseType baseType;
	private final String registryName;
	private final String readableName;
	private final String description;
	private final Character shortcut;
	private final RecipeType recipeType;
	private final ModElementGUIProvider<GE> modElementGUIProvider;
	private final Class<? extends GE> modElementStorageClass;
	private GeneratorStats.CoverageStatus status = GeneratorStats.CoverageStatus.FULL;
	private boolean hasProcedureTriggers;

	public ModElementType(String registryName, Character shortcut, BaseType baseType, RecipeType recipeType,
			ModElementGUIProvider<GE> modElementGUIProvider, Class<? extends GE> modElementStorageClass) {
		this.baseType = baseType;
		this.recipeType = recipeType;
		this.registryName = registryName;
		this.shortcut = shortcut;

		this.modElementGUIProvider = modElementGUIProvider;
		this.modElementStorageClass = modElementStorageClass;

		this.readableName = L10N.t("modelement." + registryName.toLowerCase(Locale.ENGLISH));
		this.description = L10N.t("modelement." + registryName.toLowerCase(Locale.ENGLISH) + ".description");

		for (Field field : modElementStorageClass.getFields())
			if (field.getType().isAssignableFrom(Procedure.class)) {
				hasProcedureTriggers = true;
				break;
			}
	}

	private static ModElementType<?> register(ModElementType<?> elementType) {
		ELEMENTS.add(elementType);
		return elementType;
	}

	public static ModElementType<?> getModElementType(String modElementName) {
		for (ModElementType<?> me : ELEMENTS) {
			if (me.getRegistryName().equals(modElementName)) {
				return me;
			}
		}
		return null;
	}

	public String getRegistryName() {
		return registryName;
	}

	public Character getShortcut() {
		return shortcut;
	}

	public RecipeType getRecipeType() {
		return recipeType;
	}

	public BaseType getBaseType() {
		return baseType;
	}

	public String getReadableName() {
		return readableName;
	}

	public String getDescription() {
		return description;
	}

	public ImageIcon getIcon() {
		return UIRES.get("mod_types." + registryName);
	}

	public ModElementGUI<GE> getModElementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		return modElementGUIProvider.get(mcreator, modElement, editingMode);
	}

	public Class<? extends GeneratableElement> getModElementStorageClass() {
		return modElementStorageClass;
	}

	public GeneratorStats.CoverageStatus getStatus() {
		return status;
	}

	public void setStatus(GeneratorStats.CoverageStatus status) {
		this.status = status;
	}

	public boolean hasProcedureTriggers() {
		return hasProcedureTriggers;
	}

	@Override public int compareTo(ModElementType<?> o) {
		return o.getStatus().ordinal() - status.ordinal();
	}

	@Override public String toString() {
		return this.getReadableName() + ": " + this.registryName;
	}

	public interface ModElementGUIProvider<GE extends GeneratableElement> {
		ModElementGUI<GE> get(MCreator mcreator, ModElement modElement, boolean editingMode);
	}

}
