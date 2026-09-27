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

<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@EventBusSubscriber public class ${JavaModName}Fuels {

	@SubscribeEvent public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
		<@javacompress>
		<#list itemextensions?filter(e -> e.enableFuel) as extension>
		event.modify(${mappedMCItemToItem(extension.item)}, (builder, _, item) -> {
			builder.set(DataComponents.COOKING_FUEL, new CookingFuel(
				new ResolvableInt.Constant(${extension.fuelPower.getFixedValue()}),
				new ResolvableFloat.Constant(1.0f)));
		});
		</#list>
		</@javacompress>
	}

	<#if itemextensions?filter(e -> e.hasFuelPowerProcedure())?size != 0>
	public static int getProcedureBurnTime(AbstractFurnaceBlockEntity furnace, ItemStack itemstack) {
		<#list itemextensions?filter(e -> e.hasFuelPowerProcedure()) as extension>
		<#if hasProcedure(extension.fuelPower) || hasProcedure(extension.fuelSuccessCondition)>
		if (itemstack.getItem() == ${mappedMCItemToItem(extension.item)}) {
			<#if hasProcedure(extension.fuelSuccessCondition)>
			if (<@procedureOBJToConditionCode extension.fuelSuccessCondition/>) {
			</#if>
			int burnTime = (int) <#if hasProcedure(extension.fuelPower)><@procedureOBJToNumberCode extension.fuelPower/><#else>${extension.fuelPower.getFixedValue()}</#if>;
			if (furnace instanceof BlastFurnaceBlockEntity || furnace instanceof SmokerBlockEntity)
				burnTime /= 2;
			return burnTime;
			<#if hasProcedure(extension.fuelSuccessCondition)>
			}
			return 0;
			</#if>
		}
		</#if>
		</#list>
		return -1;
	}
	</#if>
}
<#-- @formatter:on -->