<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

package ${package}.recipes.anvil;

@Mod.EventBusSubscriber public class ${name}AnvilRecipe {

	@SubscribeEvent public static void onAnvilUpdate(AnvilUpdateEvent event) {
		boolean leftMatches = Ingredient.of(
			<#if data.anvilInputStack.getUnmappedValue().startsWith("TAG:")>
				ItemTags.create(new ResourceLocation("${data.anvilInputStack?replace("TAG:","")}"))
			<#elseif generator.map(data.anvilInputStack.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
				ItemTags.create(new ResourceLocation("${generator.map(data.anvilInputStack.getUnmappedValue(), "blocksitems", 1).replace("#", "")}"))
			<#else>
				${mappedMCItemToItem(data.anvilInputStack)}
			</#if>
		).test(event.getLeft());

		boolean rightMatches = Ingredient.of(
			<#if data.anvilInputAdditionStack.getUnmappedValue().startsWith("TAG:")>
				ItemTags.create(new ResourceLocation("${data.anvilInputAdditionStack?replace("TAG:","")}"))
			<#elseif generator.map(data.anvilInputAdditionStack.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
				ItemTags.create(new ResourceLocation("${generator.map(data.anvilInputAdditionStack.getUnmappedValue(), "blocksitems", 1).replace("#", "")}"))
			<#else>
				${mappedMCItemToItem(data.anvilInputAdditionStack)}
			</#if>
		).test(event.getRight());

		if (leftMatches && rightMatches) {
			event.setCost(${data.xpCost});
			event.setOutput(${mappedMCItemToItemStackCode(data.anvilReturnStack)});
		}
	}

}
<#-- @formatter:on -->