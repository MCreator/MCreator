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

	public static final ModElementType<?> ARMOR = new ModElementType<>("armor", 'a', BaseType.ARMOR, RecipeType.ARMOR,
			ArmorGUI::new, Armor.class);
	public static final ModElementType<?> BIOME = new ModElementType<>("biome", 'o', BaseType.BIOME, RecipeType.NONE,
			BiomeGUI::new, Biome.class);
	public static final ModElementType<?> BLOCK = new ModElementType<>("block", 'b', BaseType.BLOCK, RecipeType.BLOCK,
			BlockGUI::new, Block.class);
	public static final ModElementType<?> COMMAND = new ModElementType<>("command", 'c', BaseType.COMMAND,
			RecipeType.NONE, CommandGUI::new, Command.class);
	public static final ModElementType<?> DIMENSION = new ModElementType<>("dimension", 'd', BaseType.DIMENSION,
			RecipeType.ITEM, DimensionGUI::new, Dimension.class);
	public static final ModElementType<?> CODE = new ModElementType<>("code", null, BaseType.OTHER, RecipeType.NONE,
			CustomElementGUI::new, CustomElement.class);
	public static final ModElementType<?> ENCHANTMENT = new ModElementType<>("enchantment", 'm', BaseType.ENCHANTMENT,
			RecipeType.NONE, EnchantmentGUI::new, Enchantment.class);
	public static final ModElementType<?> FLUID = new ModElementType<>("fluid", 'u', BaseType.BLOCK, RecipeType.BLOCK,
			FluidGUI::new, Fluid.class);
	public static final ModElementType<?> FOOD = new ModElementType<>("food", 'f', BaseType.ITEM, RecipeType.ITEM,
			FoodGUI::new, Food.class);
	public static final ModElementType<?> FUEL = new ModElementType<>("fuel", '/', BaseType.FUEL, RecipeType.NONE,
			FuelGUI::new, Fuel.class);
	public static final ModElementType<?> FUNCTION = new ModElementType<>("function", '\'', BaseType.DATAPACK,
			RecipeType.NONE, FunctionGUI::new, Function.class);
	public static final ModElementType<?> GAMERULE = new ModElementType<>("gamerule", ';', BaseType.OTHER,
			RecipeType.NONE, GameRuleGUI::new, GameRule.class);
	public static final ModElementType<?> GUI = new ModElementType<>("gui", 'g', BaseType.GUI, RecipeType.NONE,
			CustomGUIGUI::new, net.mcreator.element.types.GUI.class);
	public static final ModElementType<?> ITEM = new ModElementType<>("item", 'i', BaseType.ITEM, RecipeType.ITEM,
			ItemGUI::new, Item.class);
	public static final ModElementType<?> KEYBIND = new ModElementType<>("keybind", 'k', BaseType.KEYBIND,
			RecipeType.NONE, KeyBindGUI::new, KeyBinding.class);
	public static final ModElementType<?> LOOTTABLE = new ModElementType<>("loottable", 'l', BaseType.DATAPACK,
			RecipeType.NONE, LootTableGUI::new, LootTable.class);
	public static final ModElementType<?> MOB = new ModElementType<>("mob", "living_entity", 'e', BaseType.ENTITY,
			RecipeType.NONE, LivingEntityGUI::new, Mob.class);
	public static final ModElementType<?> MUSICDISC = new ModElementType<>("musicdisc", 'x', BaseType.OTHER,
			RecipeType.ITEM, MusicDiscGUI::new, MusicDisc.class);
	public static final ModElementType<?> OVERLAY = new ModElementType<>("overlay", 'v', BaseType.OVERLAY,
			RecipeType.NONE, OverlayGUI::new, Overlay.class);
	public static final ModElementType<?> PAINTING = new ModElementType<>("painting", '.', BaseType.OTHER,
			RecipeType.NONE, PaintingGUI::new, Painting.class);
	public static final ModElementType<?> PARTICLE = new ModElementType<>("particle", 'y', BaseType.PARTICLE,
			RecipeType.NONE, ParticleGUI::new, Particle.class);
	public static final ModElementType<?> PLANT = new ModElementType<>("plant", 'n', BaseType.BLOCK, RecipeType.BLOCK,
			PlantGUI::new, Plant.class);
	public static final ModElementType<?> POTION = new ModElementType<>("potion", 'z',
			BaseType.POTION, RecipeType.NONE, PotionGUI::new, Potion.class);
	public static final ModElementType<?> PROCEDURE = new ModElementType<>("procedure", 'p', BaseType.PROCEDURE,
			RecipeType.NONE, ProcedureGUI::new, Procedure.class);
	public static final ModElementType<?> RANGEDITEM = new ModElementType<>("gun", "rangeditem", "ranged_item", 'q',
			BaseType.ITEM, RecipeType.ITEM, RangedItemGUI::new, RangedItem.class);
	public static final ModElementType<?> RECIPE = new ModElementType<>("recipe", 'r', BaseType.DATAPACK,
			RecipeType.NONE, RecipeGUI::new, Recipe.class);
	public static final ModElementType<?> ADVANCEMENT = new ModElementType<>("achievement", "advancement", 'h',
			BaseType.ACHIEVEMENT, RecipeType.NONE, AchievementGUI::new, Achievement.class);
	public static final ModElementType<?> STRUCTURE = new ModElementType<>("structure", 's', BaseType.STRUCTURE,
			RecipeType.NONE, StructureGenGUI::new, Structure.class);
	public static final ModElementType<?> TAB = new ModElementType<>("tab", "item_group", 'w', BaseType.TAB, RecipeType.NONE,
			TabGUI::new, Tab.class);
	public static final ModElementType<?> TAG = new ModElementType<>("tag", 'j', BaseType.OTHER, RecipeType.NONE,
			TagGUI::new, Tag.class);
	public static final ModElementType<?> TOOL = new ModElementType<>("tool", 't', BaseType.ITEM, RecipeType.ITEM,
			ToolGUI::new, Tool.class);
	public static List<ModElementType<?>> elements = new ArrayList<>();
	//Variables used for each mod element
	private final BaseType baseType;
	private final String registryName;
	private final String description;
	private final String readableName;
	private final String name;
	private final ImageIcon icon;
	private final Character shortcut;
	private final RecipeType recipeType;
	private final ModElementGUIProvider<GE> modElementGUIProvider;
	private final Class<? extends GE> modElementStorageClass;
	private GeneratorStats.CoverageStatus status = GeneratorStats.CoverageStatus.FULL;
	private boolean hasProcedureTriggers;

	//Constructor used for the same registry name, localization ID and icon ID
	private ModElementType(String registryName, Character shortcut, BaseType baseType, RecipeType RecipeType,
			ModElementGUIProvider<GE> modElementGUIProvider, Class<? extends GE> modElementStorageClass) {
		this(registryName, registryName, registryName, shortcut, baseType, RecipeType, modElementGUIProvider,
				modElementStorageClass);
	}

	//Constructor used for the same registry name and localization ID.
	private ModElementType(String registryName, String iconID, Character shortcut, BaseType baseType,
			RecipeType RecipeType, ModElementGUIProvider<GE> modElementGUIProvider,
			Class<? extends GE> modElementStorageClass) {
		this(registryName, registryName, iconID, shortcut, baseType, RecipeType, modElementGUIProvider,
				modElementStorageClass);
	}

	//Used to have a different name and registry name
	private ModElementType(String registryName, String name, String iconID, Character shortcut, BaseType baseType,
			RecipeType recipeType, ModElementGUIProvider<GE> modElementGUIProvider,
			Class<? extends GE> modElementStorageClass) {
		this.baseType = baseType;
		this.recipeType = recipeType;
		this.registryName = registryName;
		this.name = name;
		this.icon = UIRES.get("mod_types." + iconID);
		this.shortcut = shortcut;

		this.readableName = L10N.t("modelement." + name.toLowerCase(Locale.ENGLISH));
		this.description = L10N.t("modelement." + name.toLowerCase(Locale.ENGLISH) + ".description");

		this.modElementGUIProvider = modElementGUIProvider;
		this.modElementStorageClass = modElementStorageClass;

		for (Field field : modElementStorageClass.getFields())
			if (field.getType().isAssignableFrom(net.mcreator.element.parts.Procedure.class)) {
				hasProcedureTriggers = true;
				break;
			}

		elements.add(this);
	}

	public static ModElementType<?> getModElementType(String modElementName) {
		for (ModElementType<?> me : elements) {
			if (me.registryName.equals(modElementName)) {
				return me;
			}
		}
		return null;
	}

	public String getRegistryName() {
		return registryName;
	}

	public String name() {
		return name;
	}

	public String getReadableName() {
		return readableName;
	}

	public String getDescription() {
		return description;
	}

	public ImageIcon getIcon() {
		return icon;
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

	public ModElementGUI<GE> getModElement(MCreator mcreator, ModElement modElement, boolean editingMode) {
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

	private interface ModElementGUIProvider<GE extends GeneratableElement> {
		ModElementGUI<GE> get(MCreator mcreator, ModElement modElement, boolean editingMode);
	}

}
