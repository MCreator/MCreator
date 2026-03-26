<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
<#include "../procedures.java.ftl">

package ${package}.fluid.types;

<@javacompress>
public class ${name}FluidType extends FluidType {

	public ${name}FluidType() {
		super(FluidType.Properties.create()
			<#if data.type == "WATER">
			.fallDistanceModifier(0F)
			.canExtinguish(true)
			.supportsBoating(true)
			.canHydrate(true)
			<#else>
			.canSwim(false)
			.canDrown(false)
			.pathType(PathType.LAVA)
			.adjacentPathType(null)
			</#if>
			.motionScale(${0.007 * data.flowStrength}D)
			<#if data.luminosity != 0>.lightLevel(${(data.luminosity lt 15)?then(data.luminosity, 15)})</#if>
			<#if data.density != 1000>.density(${data.density})</#if>
			<#if data.viscosity != 1000>.viscosity(${data.viscosity})</#if>
			<#if data.temperature != 300>.temperature(${data.temperature})</#if>
			<#if data.canMultiply>.canConvertToSource(true)</#if>
			<#if data.rarity != "COMMON">.rarity(Rarity.${data.rarity})</#if>
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
			<#if data.emptySound?has_content && data.emptySound.getMappedValue()?has_content>
			.sound(SoundActions.BUCKET_EMPTY, BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("${data.emptySound}")))
			<#else>
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
			</#if>
			.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		);
	}

}</@javacompress>