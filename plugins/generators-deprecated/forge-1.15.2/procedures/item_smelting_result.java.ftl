<#include "mcitems.ftl">
/*@ItemStack*/
(world.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), world.getWorld()).isPresent() ?
        world.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), world.getWorld()).get().getRecipeOutput().copy()
        : ItemStack.EMPTY)