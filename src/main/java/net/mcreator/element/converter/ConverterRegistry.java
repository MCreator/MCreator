/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.converter.v2019_5.RecipeTypeConverter;
import net.mcreator.element.converter.v2020_1.AchievementFixer;
import net.mcreator.element.converter.v2020_2.GUIBindingInverter;
import net.mcreator.element.converter.v2020_3.OpenGUIProcedureDepFixer;
import net.mcreator.element.converter.v2020_3.ProcedureEntityDepFixer;
import net.mcreator.element.converter.v2020_4.BiomeSpawnListConverter;
import net.mcreator.element.converter.v2020_4.ProcedureGlobalTriggerFixer;
import net.mcreator.element.converter.v2020_5.BiomeDefaultFeaturesConverter;
import net.mcreator.element.converter.v2020_5.GUICoordinateConverter;
import net.mcreator.element.converter.v2020_5.OverlayCoordinateConverter;
import net.mcreator.element.converter.v2020_5.ProcedureSpawnGemPickupDelayFixer;
import net.mcreator.element.converter.v2021_1.*;
import net.mcreator.element.converter.v2021_2.*;
import net.mcreator.element.converter.v2021_3.LegacyProcedureBlockRemover;
import net.mcreator.element.converter.v2022_1.FoodToItemConverter;
import net.mcreator.element.converter.v2022_1.LegacyBlockPosProcedureRemover;
import net.mcreator.element.converter.v2022_1.ProcedureShootArrowFixer;
import net.mcreator.element.converter.v2022_2.*;
import net.mcreator.element.converter.v2022_3.BiomeDictionaryProcedureConverter;
import net.mcreator.element.converter.v2023_1.*;
import net.mcreator.element.converter.v2023_2.BiomeCustomFeaturesConverter;
import net.mcreator.element.converter.v2023_2.BlockOreReplacementBlocksFixer;
import net.mcreator.element.converter.v2023_2.ExplodeProcedureConverter;
import net.mcreator.element.converter.v2023_2.PaintingFieldsFixer;
import net.mcreator.element.converter.v2023_3.HugeFungusFeatureConverter;
import net.mcreator.element.converter.v2023_3.MaterialProcedureConverter;
import net.mcreator.element.converter.v2023_3.ProcedureDamageSourceFixer;
import net.mcreator.element.converter.v2023_3.PlantGenerationTypeConverter;
import net.mcreator.element.converter.v2023_4.BlockGenerationConditionRemover;
import net.mcreator.element.converter.v2023_4.PlantGenerationConditionRemover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterRegistry {

	private static final Map<ModElementType<?>, List<IConverter>> converters = new HashMap<>() {{
		put(ModElementType.ADVANCEMENT, List.of(new AchievementFixer(), new AdvancementTextureConverter()));
		put(ModElementType.ARMOR, List.of(new ArmorTexturesConverter()));
		put(ModElementType.BIOME, List.of(new BiomeSpawnListConverter(), new BiomeDefaultFeaturesConverter(),
				new BiomeFrozenTopLayerConverter(), new BiomeGenParametersConverter(),
				new BiomeCustomFeaturesConverter()));
		put(ModElementType.BLOCK,
				List.of(new BlockLuminanceFixer(), new BlockBoundingBoxFixer(), new BlockLightOpacityFixer(),
						new BlockRequiresCorrectToolConverter(), new BlockOreReplacementBlocksFixer(),
						new BlockGenerationConditionRemover()));
		put(ModElementType.DIMENSION, List.of(new DimensionLuminanceFixer()));
		put(ModElementType.FLUID, List.of(new FluidNameFixer(), new FluidGenToFeatureConverter()));
		put(ModElementType.COMMAND, List.of(new CommandParameterBlockFixer()));
		put(ModElementType.GAMERULE, List.of(new GameruleDisplayNameFixer(), new BooleanGameRulesConverter()));
		put(ModElementType.GUI,
				List.of(new GUIBindingInverter(), new GUICoordinateConverter(), new GUITexturesConverter(),
						new GUIComponentNamer(), new SlotInteractionsConverter()));
		put(ModElementType.LIVINGENTITY, List.of(new EntityTexturesConverter()));
		put(ModElementType.OVERLAY,
				List.of(new OverlayCoordinateConverter(), new OverlayTexturesConverter(), new GUIComponentNamer()));
		put(ModElementType.PAINTING, List.of(new PaintingFieldsFixer()));
		put(ModElementType.PARTICLE, List.of(new ParticleTextureConverter()));
		put(ModElementType.PLANT, List.of(new PlantLuminanceFixer(), new PlantGenerationTypeConverter(),
				new PlantGenerationConditionRemover()));
		put(ModElementType.POTION, List.of(new PotionToEffectConverter()));
		put(ModElementType.POTIONEFFECT, List.of(new EffectTextureConverter()));
		put(ModElementType.PROCEDURE, List.of(new ProcedureEntityDepFixer(), new OpenGUIProcedureDepFixer(),
				new ProcedureGlobalTriggerFixer(), new ProcedureSpawnGemPickupDelayFixer(),
				new ProcedureVariablesConverter(), new ProcedureVariablesEntityFixer(),
				new LegacyProcedureBlockRemover(), new LegacyBlockPosProcedureRemover(), new ProcedureShootArrowFixer(),
				new LegacyShootArrowProcedureRemover(), new BiomeDictionaryProcedureConverter(),
				new ToolToItemTypeProcedureConverter(), new ExplodeProcedureConverter(),
				new MaterialProcedureConverter(), new ProcedureDamageSourceFixer()));
		put(ModElementType.RANGEDITEM, List.of(new RangedItemTextureConverter()));
		put(ModElementType.RECIPE, List.of(new RecipeTypeConverter()));
		put(ModElementType.ITEM, List.of(new ItemDispenseBehaviorToItemExtensionConverter()));
		put(ModElementType.FEATURE, List.of(new HugeFungusFeatureConverter()));
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
