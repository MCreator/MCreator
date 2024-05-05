<#include "mcitems.ftl">
/*@int*/(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.FOOD) ? ${mappedMCItemToItemStackCode(input$item)}.getFoodProperties(null).nutrition() : 0)