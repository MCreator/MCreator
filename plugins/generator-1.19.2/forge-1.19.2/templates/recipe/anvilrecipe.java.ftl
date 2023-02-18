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
<#assign leftHasTag = false>
<#assign rightHasTag = false>

package ${package}.recipes.anvil;

@Mod.EventBusSubscriber
public class ${name}AnvilRecipe {
	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		<#if data.anvilInputStack?starts_with("TAG:")>
		boolean left = Ingredient.of(ItemTags.create(new ResourceLocation("${data.anvilInputStack?replace("TAG:","")}"))).test(event.getLeft());
		<#assign leftHasTag = true>
		<#else>
		Item left = ${mappedMCItemToItem(data.anvilInputStack)};
		</#if>
		<#if data.anvilInputAdditionStack?starts_with("TAG:")>
		boolean right = Ingredient.of(ItemTags.create(new ResourceLocation("${data.anvilInputAdditionStack?replace("TAG:","")}"))).test(event.getRight());
		<#assign rightHasTag = true>
		<#else>
		Item right = ${mappedMCItemToItem(data.anvilInputAdditionStack)};
		</#if>
		ItemStack result = ${mappedMCItemToItemStackCode(data.anvilReturnStack)};
		<#if leftHasTag || rightHasTag>
		boolean showResult =
			<#if leftHasTag> left && <#else> left == event.getLeft().getItem() && </#if>
			<#if rightHasTag> right; <#else> right == event.getRight().getItem(); </#if>
		<#else>
		boolean showResult = event.getLeft().getItem() == left && event.getRight().getItem() == right;
		</#if>
		if (showResult) {
			event.setCost(${data.xpCost});
			event.setOutput(result);
		}
	}

}
<#-- @formatter:on -->