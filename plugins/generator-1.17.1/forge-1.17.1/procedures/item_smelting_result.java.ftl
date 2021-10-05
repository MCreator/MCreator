<#include "mcitems.ftl">
/*@ItemStack*/
((world instanceof Level _lvlSmeltResult && _lvlSmeltResult.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(${mappedMCItemToItemStackCode(input$item, 1)}), _lvlSmeltResult).isPresent()) ?
        _lvlSmeltResult.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(${mappedMCItemToItemStackCode(input$item, 1)}), _lvlSmeltResult).get().getResultItem().copy()
        : ItemStack.EMPTY)