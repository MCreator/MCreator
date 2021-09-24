<#include "mcitems.ftl">
/*@ItemStack*/
((world instanceof Level _lvl_sr && _lvl_sr.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(${mappedMCItemToItemStackCode(input$item, 1)}), _lvl_sr).isPresent()) ?
        _lvl_sr.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(${mappedMCItemToItemStackCode(input$item, 1)}), _lvl_sr).get().getResultItem().copy()
        : ItemStack.EMPTY)