<#include "mcitems.ftl">
/*@ItemStack*/
(((World) world).getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), ((World) world)).isPresent() ?
        ((World) world).getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), (World) world).get().getRecipeOutput().copy()
        : ItemStack.EMPTY)