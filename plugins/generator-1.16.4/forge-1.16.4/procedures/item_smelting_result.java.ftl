<#include "mcitems.ftl">
/*@ItemStack*/
(((World) world).getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), ((World) world).getWorldInfo()).isPresent() ?
        ((World) world).getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), ((World) world).getWorldInfo()).get().getRecipeOutput().copy()
        : ItemStack.EMPTY)