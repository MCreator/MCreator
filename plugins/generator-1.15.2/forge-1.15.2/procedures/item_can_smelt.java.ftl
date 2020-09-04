<#include "mcitems.ftl">
(world.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(${mappedMCItemToItemStackCode(input$item, 1)}), world.getWorld()).isPresent())