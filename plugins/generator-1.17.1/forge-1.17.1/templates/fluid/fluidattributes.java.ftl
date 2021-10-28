<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 #
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 #
 # Additional permission for code generator templates (*.ftl files)
 #
 # As a special exception, you may create a larger work that contains part or
 # all of the MCreator code generator templates (*.ftl files) and distribute
 # that work under terms of your choice, so long as that work isn't itself a
 # template for code generation. Alternatively, if you modify or redistribute
 # the template itself, you may (at your option) remove this special exception,
 # which will cause the template and the resulting code generator output files
 # to be licensed under the GNU General Public License without this special
 # exception.
-->

<#-- @formatter:off -->

package ${package}.fluid.attributes;

public class ${name}FluidAttributes extends FluidAttributes {

	public static class CustomBuilder extends FluidAttributes.Builder {
		protected CustomBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture,
				BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
			super(stillTexture, flowingTexture, factory);
		}
	}

	public static CustomBuilder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
		return new CustomBuilder(stillTexture, flowingTexture, ${name}FluidAttributes::new);
	}

	protected ${name}FluidAttributes(Builder builder, Fluid fluid) {
		super(builder, fluid);
	}

	<#if data.isFluidTinted()>
	@Override
	public int getColor(BlockAndTintGetter world, BlockPos pos) {
		return
		<#if data.tintType == "Grass">
			BiomeColors.getAverageGrassColor(world, pos)
		<#elseif data.tintType == "Foliage">
			BiomeColors.getAverageFoliageColor(world, pos)
		<#elseif data.tintType == "Water">
			BiomeColors.getAverageWaterColor(world, pos)
		<#elseif data.tintType == "Sky">
			Minecraft.getInstance().level.getBiome(pos).getSkyColor()
		<#elseif data.tintType == "Fog">
			Minecraft.getInstance().level.getBiome(pos).getFogColor()
		<#else>
			Minecraft.getInstance().level.getBiome(pos).getWaterFogColor()
		</#if> | 0xFF000000;
	}
	</#if>

}