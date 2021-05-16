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
import net.mcreator.element.converter.fv10.BiomeSpawnListConverter;
import net.mcreator.element.converter.fv11.GUICoordinateConverter;
import net.mcreator.element.converter.fv11.OverlayCoordinateConverter;
import net.mcreator.element.converter.fv12.BiomeDefaultFeaturesConverter;
import net.mcreator.element.converter.fv13.ProcedureSpawnGemPickupDelayFixer;
import net.mcreator.element.converter.fv14.BlockLuminanceFixer;
import net.mcreator.element.converter.fv14.DimensionLuminanceFixer;
import net.mcreator.element.converter.fv14.PlantLuminanceFixer;
import net.mcreator.element.converter.fv15.DimensionPortalSelectedFixer;
import net.mcreator.element.converter.fv16.BlockBoundingBoxFixer;
import net.mcreator.element.converter.fv17.GameruleDisplayNameFixer;
import net.mcreator.element.converter.fv18.BiomeFrozenTopLayerConverter;
import net.mcreator.element.converter.fv19.FluidBucketSelectedFixer;
import net.mcreator.element.converter.fv19.ProcedureVariablesConverter;
import net.mcreator.element.converter.fv4.RecipeTypeConverter;
import net.mcreator.element.converter.fv5.AchievementFixer;
import net.mcreator.element.converter.fv6.GUIBindingInverter;
import net.mcreator.element.converter.fv7.ProcedureEntityDepFixer;
import net.mcreator.element.converter.fv8.OpenGUIProcedureDepFixer;
import net.mcreator.element.converter.fv9.ProcedureGlobalTriggerFixer;

import java.util.*;

public class ConverterRegistry {

	private static final Map<ModElementType, List<IConverter>> converters = new HashMap<ModElementType, List<IConverter>>() {{
		put(ModElementType.RECIPE, Collections.singletonList(new RecipeTypeConverter()));
		put(ModElementType.ACHIEVEMENT, Collections.singletonList(new AchievementFixer()));
		put(ModElementType.GUI, Arrays.asList(new GUIBindingInverter(), new GUICoordinateConverter()));
		put(ModElementType.PROCEDURE, Arrays.asList(new ProcedureEntityDepFixer(), new OpenGUIProcedureDepFixer(),
				new ProcedureGlobalTriggerFixer(), new ProcedureSpawnGemPickupDelayFixer(), new ProcedureVariablesConverter()));
		put(ModElementType.BIOME, Arrays.asList(new BiomeSpawnListConverter(), new BiomeDefaultFeaturesConverter(),
				new BiomeFrozenTopLayerConverter()));
		put(ModElementType.OVERLAY, Collections.singletonList(new OverlayCoordinateConverter()));
		put(ModElementType.BLOCK, Arrays.asList(new BlockLuminanceFixer(), new BlockBoundingBoxFixer()));
		put(ModElementType.PLANT, Collections.singletonList(new PlantLuminanceFixer()));
		put(ModElementType.GAMERULE, Collections.singletonList(new GameruleDisplayNameFixer()));
		put(ModElementType.DIMENSION, Arrays.asList(new DimensionLuminanceFixer(), new DimensionPortalSelectedFixer()));
		put(ModElementType.FLUID, Collections.singletonList(new FluidBucketSelectedFixer()));
	}};

	public static List<IConverter> getConvertersForModElementType(ModElementType modElementType) {
		return converters.get(modElementType);
	}

}
