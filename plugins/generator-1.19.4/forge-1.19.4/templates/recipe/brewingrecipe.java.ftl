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

package ${package}.recipes.brewing;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}BrewingRecipe implements IBrewingRecipe {

	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new ${name}BrewingRecipe()));
	}

	@Override public boolean isInput(ItemStack input) {
		<#if data.brewingInputStack?starts_with("POTION:")>
		Item inputItem = input.getItem();
		return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION)
			&& PotionUtils.getPotion(input) == ${generator.map(data.brewingInputStack?replace("POTION:",""), "potions")};
		<#else>
		return ${mappedMCItemToIngredient(data.brewingInputStack)}.test(input);
		</#if>
	}

	@Override public boolean isIngredient(ItemStack ingredient) {
		return ${mappedMCItemToIngredient(data.brewingIngredientStack)}.test(ingredient);
	}

	@Override public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (isInput(input) && isIngredient(ingredient)) {
			<#if data.brewingReturnStack?starts_with("POTION:")>
			return PotionUtils.setPotion(
				<#if data.brewingInputStack?starts_with("POTION:")>
				new ItemStack(input.getItem())
				<#else>
				new ItemStack(Items.POTION)
				</#if>, ${generator.map(data.brewingReturnStack?replace("POTION:",""), "potions")});
			<#else>
			return ${mappedMCItemToItemStackCode(data.brewingReturnStack, 1)};
			</#if>
		}
		return ItemStack.EMPTY;
	}

}