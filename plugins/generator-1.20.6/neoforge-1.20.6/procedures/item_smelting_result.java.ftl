<#include "mcitems.ftl">
/*@ItemStack*/
(world instanceof Level _lvlSmeltResult ?
	_lvlSmeltResult.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(${mappedMCItemToItemStackCode(input$item, 1)}), _lvlSmeltResult)
			.map(recipe -> recipe.value().getResultItem(_lvlSmeltResult.registryAccess()).copy()).orElse(ItemStack.EMPTY)
		: ItemStack.EMPTY)