<#include "mcitems.ftl">
/*@ItemStack*/(FurnaceRecipes.instance().getSmeltingResult(${mappedMCItemToItemStackCode(input$item, 1)}).copy())