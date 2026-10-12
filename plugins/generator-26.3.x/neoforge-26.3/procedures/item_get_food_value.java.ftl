<#include "mcitems.ftl">
/*@int*/(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.FOOD) ? ${mappedMCItemToItemStackCode(input$item)}.get(DataComponents.FOOD).nutrition() : 0)