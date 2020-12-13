<#include "mcitems.ftl">
/*@ItemStack*/
(((World) world).getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), ((World) world).getWorld()).isPresent() ?
        ((World) world).getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), ((World) world).getWorld()).get().getRecipeOutput().copy()
        : ItemStack.EMPTY)