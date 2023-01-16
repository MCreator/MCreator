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

package net.mcreator.element.converter;

import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.legacy.*;
import net.mcreator.element.converter.v2020_5.BiomeDefaultFeaturesConverter;
import net.mcreator.element.converter.v2020_5.ProcedureSpawnGemPickupDelayFixer;
import net.mcreator.element.converter.v2020_6.BlockLuminanceFixer;
import net.mcreator.element.converter.v2020_6.DimensionLuminanceFixer;
import net.mcreator.element.converter.v2020_6.DimensionPortalSelectedFixer;
import net.mcreator.element.converter.v2020_6.PlantLuminanceFixer;
import net.mcreator.element.converter.v2021_1.BiomeFrozenTopLayerConverter;
import net.mcreator.element.converter.v2021_1.BlockBoundingBoxFixer;
import net.mcreator.element.converter.v2021_1.GameruleDisplayNameFixer;
import net.mcreator.element.converter.v2021_2.*;
import net.mcreator.element.converter.v2021_3.LegacyProcedureBlockRemover;
import net.mcreator.element.converter.v2022_1.FoodToItemConverter;
import net.mcreator.element.converter.v2022_1.LegacyBlockPosProcedureRemover;
import net.mcreator.element.converter.v2022_1.ProcedureShootArrowFixer;
import net.mcreator.element.converter.v2022_2.*;
import net.mcreator.element.converter.v2022_3.BiomeDictionaryProcedureConverter;
import net.mcreator.element.converter.v2023_1.BiomeGenParametersConverter;
import net.mcreator.element.converter.v2023_1.GUIComponentNamer;
import net.mcreator.element.converter.v2023_1.SlotInteractionsConverter;
import net.mcreator.element.converter.v2023_1.ToolToItemTypeProcedureConverter;

import java.util.*;

public class ConverterRegistry {

	private static final Map<ModElementType<?>, List<IConverter>> converters = new HashMap<>() {{
		put(ModElementType.ADVANCEMENT, Arrays.asList(new AchievementFixer(), new AdvancementTextureConverter()));
		put(ModElementType.ARMOR, Collections.singletonList(new ArmorTexturesConverter()));
		put(ModElementType.BIOME, Arrays.asList(new BiomeSpawnListConverter(), new BiomeDefaultFeaturesConverter(),
				new BiomeFrozenTopLayerConverter(), new BiomeGenParametersConverter()));
		put(ModElementType.BLOCK,
				Arrays.asList(new BlockLuminanceFixer(), new BlockBoundingBoxFixer(), new BlockLightOpacityFixer(),
						new BlockRequiresCorrectToolConverter()));
		put(ModElementType.PLANT, Collections.singletonList(new PlantLuminanceFixer()));
		put(ModElementType.GAMERULE, Arrays.asList(new GameruleDisplayNameFixer(), new BooleanGameRulesConverter()));
		put(ModElementType.DIMENSION, Arrays.asList(new DimensionLuminanceFixer(), new DimensionPortalSelectedFixer()));
		put(ModElementType.FLUID, Arrays.asList(new FluidBucketSelectedFixer(), new FluidNameFixer()));
		put(ModElementType.COMMAND, Collections.singletonList(new CommandParameterBlockFixer()));
		put(ModElementType.GAMERULE, Arrays.asList(new GameruleDisplayNameFixer(), new BooleanGameRulesConverter()));
		put(ModElementType.GUI,
				Arrays.asList(new GUIBindingInverter(), new GUICoordinateConverter(), new GUITexturesConverter(),
						new GUIComponentNamer(), new SlotInteractionsConverter()));
		put(ModElementType.LIVINGENTITY, Collections.singletonList(new EntityTexturesConverter()));
		put(ModElementType.OVERLAY, Arrays.asList(new OverlayCoordinateConverter(), new OverlayTexturesConverter(),
				new GUIComponentNamer()));
		put(ModElementType.PARTICLE, Collections.singletonList(new ParticleTextureConverter()));
		put(ModElementType.PLANT, Collections.singletonList(new PlantLuminanceFixer()));
		put(ModElementType.POTION, Collections.singletonList(new PotionToEffectConverter()));
		put(ModElementType.POTIONEFFECT, Collections.singletonList(new EffectTextureConverter()));
		put(ModElementType.PROCEDURE, Arrays.asList(new ProcedureEntityDepFixer(), new OpenGUIProcedureDepFixer(),
				new ProcedureGlobalTriggerFixer(), new ProcedureSpawnGemPickupDelayFixer(),
				new ProcedureVariablesConverter(), new ProcedureVariablesEntityFixer(),
				new LegacyProcedureBlockRemover(), new LegacyBlockPosProcedureRemover(), new ProcedureShootArrowFixer(),
				new LegacyShootArrowProcedureRemover(), new BiomeDictionaryProcedureConverter(),
				new ToolToItemTypeProcedureConverter()));
		put(ModElementType.RANGEDITEM, Collections.singletonList(new RangedItemTextureConverter()));
		put(ModElementType.RECIPE, Collections.singletonList(new RecipeTypeConverter()));
		put(ModElementType.ITEM, Collections.singletonList(new ItemDispenseBehaviorToItemExtensionConverter()));
	}};

	// Converters that convert older mod element type to a newer one
	private static final Map<String, IConverter> converters_legacy = new HashMap<>() {{
		put("food", new FoodToItemConverter());
		put("fuel", new FuelToItemExtensionConverter());
	}};

	public static List<IConverter> getConvertersForModElementType(ModElementType<?> modElementType) {
		return converters.get(modElementType);
	}

	public static IConverter getConverterForModElementType(String modElementType) {
		return converters_legacy.get(modElementType);
	}

}
