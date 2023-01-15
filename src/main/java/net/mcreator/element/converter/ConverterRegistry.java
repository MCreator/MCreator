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
import net.mcreator.element.converter.legacy.fv10.BiomeSpawnListConverter;
import net.mcreator.element.converter.legacy.fv11.GUICoordinateConverter;
import net.mcreator.element.converter.legacy.fv11.OverlayCoordinateConverter;
import net.mcreator.element.converter.v2020_5.fv12.BiomeDefaultFeaturesConverter;
import net.mcreator.element.converter.v2020_5.fv13.ProcedureSpawnGemPickupDelayFixer;
import net.mcreator.element.converter.v2020_6.fv14.BlockLuminanceFixer;
import net.mcreator.element.converter.v2020_6.fv14.DimensionLuminanceFixer;
import net.mcreator.element.converter.v2020_6.fv14.PlantLuminanceFixer;
import net.mcreator.element.converter.v2020_6.fv15.DimensionPortalSelectedFixer;
import net.mcreator.element.converter.v2021_1.fv16.BlockBoundingBoxFixer;
import net.mcreator.element.converter.v2021_1.fv17.GameruleDisplayNameFixer;
import net.mcreator.element.converter.v2021_1.fv18.BiomeFrozenTopLayerConverter;
import net.mcreator.element.converter.v2021_2.fv19.FluidBucketSelectedFixer;
import net.mcreator.element.converter.v2021_2.fv20.FluidNameFixer;
import net.mcreator.element.converter.v2021_2.fv21.BooleanGameRulesConverter;
import net.mcreator.element.converter.v2021_2.fv21.ProcedureVariablesConverter;
import net.mcreator.element.converter.v2021_2.fv22.BlockLightOpacityFixer;
import net.mcreator.element.converter.v2021_2.fv23.PotionToEffectConverter;
import net.mcreator.element.converter.v2021_2.fv24.ProcedureVariablesEntityFixer;
import net.mcreator.element.converter.v2021_3.fv25.LegacyProcedureBlockRemover;
import net.mcreator.element.converter.v2022_1.fv26.LegacyBlockPosProcedureRemover;
import net.mcreator.element.converter.v2022_1.fv27.ProcedureShootArrowFixer;
import net.mcreator.element.converter.v2022_1.fv28.FoodToItemConverter;
import net.mcreator.element.converter.v2022_2.fv29.CommandParameterBlockFixer;
import net.mcreator.element.converter.v2022_2.fv30.BlockRequiresCorrectToolConverter;
import net.mcreator.element.converter.fv31.*;
import net.mcreator.element.converter.v2022_2.fv31.*;
import net.mcreator.element.converter.v2022_2.fv32.FuelToItemExtensionConverter;
import net.mcreator.element.converter.v2022_2.fv32.ItemDispenseBehaviorToItemExtensionConverter;
import net.mcreator.element.converter.v2022_2.fv33.LegacyShootArrowProcedureRemover;
import net.mcreator.element.converter.v2022_3.fv34.BiomeDictionaryProcedureConverter;
import net.mcreator.element.converter.fv35.ToolToItemTypeProcedureConverter;
import net.mcreator.element.converter.fv36.GUIComponentNamer;
import net.mcreator.element.converter.fv37.SlotInteractionsConverter;
import net.mcreator.element.converter.fv38.BiomeGenParametersConverter;
import net.mcreator.element.converter.legacy.fv4.RecipeTypeConverter;
import net.mcreator.element.converter.legacy.fv5.AchievementFixer;
import net.mcreator.element.converter.legacy.fv6.GUIBindingInverter;
import net.mcreator.element.converter.legacy.fv7.ProcedureEntityDepFixer;
import net.mcreator.element.converter.legacy.fv8.OpenGUIProcedureDepFixer;
import net.mcreator.element.converter.legacy.fv9.ProcedureGlobalTriggerFixer;

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
