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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.modgui.*;
import net.mcreator.workspace.elements.ModElement;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModElementTypeRegistry {

	public static final Map<ModElementType, ModTypeRegistration<?>> REGISTRY = new LinkedHashMap<ModElementType, ModTypeRegistration<?>>() {{
		put(ModElementType.BLOCK, new ModTypeRegistration<>('b', 16, BlockGUI::new, Block.class));
		put(ModElementType.ITEM, new ModTypeRegistration<>('i', 6, ItemGUI::new, Item.class));
		put(ModElementType.TOOL, new ModTypeRegistration<>('t', 15, ToolGUI::new, Tool.class));
		put(ModElementType.FOOD, new ModTypeRegistration<>('f', 3, FoodGUI::new, Food.class));
		put(ModElementType.FLUID, new ModTypeRegistration<>('l', 2, FluidGUI::new, Fluid.class));
		put(ModElementType.ARMOR, new ModTypeRegistration<>('a', 21, ArmorGUI::new, Armor.class));
		put(ModElementType.RANGEDITEM, new ModTypeRegistration<>('n', 5, RangedItemGUI::new, RangedItem.class));
		put(ModElementType.RECIPE, new ModTypeRegistration<>('r', 11, RecipeGUI::new, Recipe.class));
		put(ModElementType.FUEL, new ModTypeRegistration<>(null, 4, FuelGUI::new, Fuel.class));
		put(ModElementType.TAB, new ModTypeRegistration<>('w', 14, TabGUI::new, Tab.class));
		put(ModElementType.MOB, new ModTypeRegistration<>('e', 8, LivingEntityGUI::new, Mob.class));
		put(ModElementType.PLANT, new ModTypeRegistration<>('y', 10, PlantGUI::new, Plant.class));
		put(ModElementType.STRUCTURE, new ModTypeRegistration<>('s', 13, StructureGenGUI::new, Structure.class));
		put(ModElementType.BIOME, new ModTypeRegistration<>('o', 20, BiomeGUI::new, Biome.class));
		put(ModElementType.DIMENSION, new ModTypeRegistration<>('d', 12, DimensionGUI::new, Dimension.class));
		put(ModElementType.ACHIEVEMENT, new ModTypeRegistration<>('h', 18, AchievementGUI::new, Achievement.class));
		put(ModElementType.COMMAND, new ModTypeRegistration<>('c', 22, CommandGUI::new, Command.class));
		put(ModElementType.KEYBIND, new ModTypeRegistration<>('k', 7, KeyBindGUI::new, KeyBinding.class));
		put(ModElementType.GUI, new ModTypeRegistration<>('g', 19, CustomGUIGUI::new, GUI.class));
		put(ModElementType.OVERLAY, new ModTypeRegistration<>('v', 9, OverlayGUI::new, Overlay.class));
		put(ModElementType.PROCEDURE, new ModTypeRegistration<>('p', 17, ProcedureGUI::new, Procedure.class));
		put(ModElementType.POTION, new ModTypeRegistration<>('z', 23, PotionGUI::new, Potion.class));
		put(ModElementType.ENCHANTMENT, new ModTypeRegistration<>('m', 29, EnchantmentGUI::new, Enchantment.class));
		put(ModElementType.CODE, new ModTypeRegistration<>(null, 24, CustomElementGUI::new, CustomElement.class));
		put(ModElementType.TAG, new ModTypeRegistration<>('j', 1, TagGUI::new, Tag.class));
		put(ModElementType.LOOTTABLE, new ModTypeRegistration<>('q', 26, LootTableGUI::new, LootTable.class));
		put(ModElementType.FUNCTION, new ModTypeRegistration<>('u', 27, FunctionGUI::new, Function.class));
		put(ModElementType.MUSICDISC, new ModTypeRegistration<>(null, 28, MusicDiscGUI::new, MusicDisc.class));
		put(ModElementType.JSON, new ModTypeRegistration<>(null, 29, JsonGUI::new, Json.class));
	}};

	public static class ModTypeRegistration<GE extends GeneratableElement> {

		private final int icon_id;
		private final Character shortcut;

		private final ModElementGUIProvider<GE> modElementGUIProvider;
		private final Class<? extends GeneratableElement> modElementStorageClass;

		private boolean hasProcedureTriggers;

		ModTypeRegistration(Character shortcut, int icon_id, ModElementGUIProvider<GE> modElementGUIProvider,
				Class<? extends GE> modElementStorageClass) {
			this.icon_id = icon_id;
			this.modElementGUIProvider = modElementGUIProvider;
			this.shortcut = shortcut;
			this.modElementStorageClass = modElementStorageClass;

			for (Field field : modElementStorageClass.getFields())
				if (field.getType().isAssignableFrom(net.mcreator.element.parts.Procedure.class)) {
					hasProcedureTriggers = true;
					break;
				}
		}

		public Character getShortcut() {
			return shortcut;
		}

		public int getIconID() {
			return icon_id;
		}

		public ModElementGUI<GE> getModElement(MCreator mcreator, ModElement modElement, boolean editingMode) {
			return modElementGUIProvider.get(mcreator, modElement, editingMode);
		}

		public Class<? extends GeneratableElement> getModElementStorageClass() {
			return modElementStorageClass;
		}

		public boolean hasProcedureTriggers() {
			return hasProcedureTriggers;
		}
	}

	private interface ModElementGUIProvider<GE extends GeneratableElement> {
		ModElementGUI<GE> get(MCreator mcreator, ModElement modElement, boolean editingMode);
	}

}
